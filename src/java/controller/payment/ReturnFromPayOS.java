package payment;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData; // Dùng khi tạo link thanh toán, không cần dùng ở đây nhưng bạn đã import
import vn.payos.type.PaymentData;          // Dùng khi tạo đơn hàng thanh toán, không cần dùng ở đây nhưng bạn đã import
import payment.Config;

@WebServlet("/ReturnFromPayOS")
public class ReturnFromPayOS extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy orderCode từ URL sau khi người dùng được PayOS redirect về
        String orderCode = request.getParameter("orderCode");

        // Nếu không có orderCode thì redirect về trang thanh toán thất bại
        if (orderCode == null) {
            response.sendRedirect("PaymentJSP/payment-cancel.jsp");
            return;
        }

        try {
            // PayOS yêu cầu orderCode kiểu long
            long orderCodeLong = Long.parseLong(orderCode);

            // Tạo đối tượng PayOS từ cấu hình
            PayOS payOS = Config.payOS();

            // Gọi hàm lấy thông tin đơn hàng theo orderCode
            var paymentInfo = payOS.getPaymentLinkInformation(orderCodeLong);

            // Lấy trạng thái thanh toán từ thông tin đơn hàng
            String status = paymentInfo.getStatus();

            // Nếu trạng thái là PAID (đã thanh toán thành công) thì redirect đến trang thành công
            if ("PAID".equalsIgnoreCase(status)) {
                response.sendRedirect("PaymentJSP/payment-success.jsp");
            } else {
                // Ngược lại (chưa thanh toán hoặc bị hủy), chuyển đến trang thất bại
                response.sendRedirect("PaymentJSP/payment-cancel.jsp");
            }

        } catch (Exception e) {
            // Nếu xảy ra lỗi (lỗi SDK, orderCode không hợp lệ...), cũng chuyển đến trang thất bại
            e.printStackTrace();
            response.sendRedirect("PaymentJSP/payment-cancel.jsp");
        }
    }
}
