package payment;

// Import các thư viện cần thiết
import java.io.IOException;

import Dao.PaymentDAO; // DAO để lưu dữ liệu thanh toán vào CSDL
import jakarta.servlet.annotation.WebServlet; // Annotation để định nghĩa URL mapping cho servlet
import jakarta.servlet.http.HttpServlet; // Lớp cha của servlet
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Payment; // Model Payment của bạn
import model.User;    // Model User của bạn
import vn.payos.PayOS; // SDK của PayOS
import vn.payos.type.CheckoutResponseData; // Kết quả trả về khi tạo link thanh toán
import vn.payos.type.PaymentData; // Dữ liệu gửi lên PayOS

/**
 * Servlet xử lý khi người dùng gửi yêu cầu tạo thanh toán
 */
@WebServlet("/CreatePayment")
public class CreatePaymentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy thông tin user hiện tại từ session (phải đăng nhập)
        int userID = ((User) request.getSession().getAttribute("authUser")).getUserID();

        // Lấy loại gói mà người dùng chọn từ form (monthly hoặc yearly)
        String planType = request.getParameter("plan");

        // Gán planID và amount tương ứng với loại gói
        int planID = planType.equals("monthly") ? 1 : 2;
        int amount = planType.equals("monthly") ? 50000 : 500000;

        // Tạo mô tả cho giao dịch
        String description = "Thanh toán gói " + planType;

        // URL để PayOS redirect về sau khi thanh toán thành công/hủy
        String returnUrl = Config.returnUrl;

        // Tạo instance PayOS từ config
        PayOS payOS = Config.payOS();

        try {
            // Tạo dữ liệu thanh toán gửi lên PayOS
            PaymentData paymentData = PaymentData.builder()
                    .amount(amount)
                    .description(description)
                    .returnUrl(returnUrl)
                    .cancelUrl(returnUrl) // Có thể tách riêng cancelUrl nếu cần
                    .build();

            // Gọi API PayOS để tạo link thanh toán
            CheckoutResponseData order = payOS.createPaymentLink(paymentData);

            // Lấy orderCode trả về (dạng Long) -> ép về String để lưu
            String orderCode = String.valueOf(order.getOrderCode());

            // Lấy đường dẫn thanh toán (link người dùng sẽ được redirect đến)
            String checkoutUrl = order.getCheckoutUrl();

            // Tạo object Payment để lưu vào DB (trạng thái là PENDING)
            Payment payment = new Payment(
                0,          // ID (auto-increment nên để 0)
                userID,     // ID người dùng
                planID,     // Loại gói
                amount,     // Số tiền
                null,       // Thời gian thanh toán (chưa thanh toán nên null)
                orderCode,  // Mã đơn hàng từ PayOS
                checkoutUrl,// Link thanh toán
                "PENDING",  // Trạng thái mặc định
                null        // Trường thêm nếu có
            );

            // Lưu vào CSDL thông qua DAO
            PaymentDAO.save(payment);

            // Redirect người dùng tới trang thanh toán của PayOS
            response.sendRedirect(checkoutUrl);

        } catch (Exception e) {
            // Nếu có lỗi thì log ra console và chuyển hướng tới trang thất bại
            e.printStackTrace();
            response.sendRedirect("PaymentJSP/payment-cancel.jsp");
        }
    }
}
