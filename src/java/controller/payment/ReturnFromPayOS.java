package controller.payment;

import Dao.UserPremiumDAO;
import DB.JDBCConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import model.UserPremium;

@WebServlet("/ReturnFromPayOS")
public class ReturnFromPayOS extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get payment status from PayOS callback
        String status = request.getParameter("status");
        HttpSession session = request.getSession(false);
        Connection conn = null;
        
        if ("PAID".equals(status) && session != null) {
            try {
                // Bắt đầu transaction
                conn = JDBCConnection.getConnection();
                conn.setAutoCommit(false);
                
                // Payment successful
                session.setAttribute("paymentSuccess", true);
                session.setAttribute("paymentMessage", "Thanh toán thành công!");

                // Lấy thông tin user và plan
                model.User user = (model.User) session.getAttribute("authUser");
                Integer planId = (Integer) session.getAttribute("selectedPlanId");
                
                if (user != null && planId != null) {
                    // Cập nhật role thành premium (2) trong database
                    user.setRoleID(2);
                    service.UserService userService = new service.UserService();
                    userService.updateUserWithConnection(user, conn); // Sửa để sử dụng connection từ transaction
                    session.setAttribute("authUser", user);

                    // Tính ngày bắt đầu và kết thúc premium
                    Calendar calendar = Calendar.getInstance();
                    Date startDate = calendar.getTime(); // Ngày hiện tại
                    
                    // Lấy số tháng từ plan
                    int durationInMonths = new Dao.PremiumPlanDAO().getPlanDuration(planId);
                    calendar.add(Calendar.MONTH, durationInMonths);
                    Date endDate = calendar.getTime();

                    // Tạo đối tượng UserPremium
                    UserPremium userPremium = new UserPremium();
                    userPremium.setUserID(user.getUserID());
                    userPremium.setPlanID(planId);
                    userPremium.setStartDate(startDate);
                    userPremium.setEndDate(endDate);

                    // Lưu thông tin vào database
                    UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                    userPremiumDAO.addWithConnection(userPremium, conn); // Sửa để sử dụng connection từ transaction
                    
                    // Commit transaction
                    conn.commit();
                    
                    // Redirect to success page
                    response.sendRedirect(request.getContextPath() + "/PaymentJSP/PaymentSuccess.jsp");
                    return;
                }
            } catch (SQLException e) {
                // Rollback nếu có lỗi
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                
                // Log lỗi và set thông báo lỗi
                e.printStackTrace();
                if (session != null) {
                    session.setAttribute("paymentSuccess", false);
                    session.setAttribute("paymentMessage", "Có lỗi xảy ra khi xử lý thanh toán!");
                }
            } finally {
                // Đóng connection
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Payment failed or cancelled
            if (session != null) {
                session.setAttribute("paymentSuccess", false);
                session.setAttribute("paymentMessage", "Thanh toán không thành công hoặc đã bị hủy!");
            }
        }
        
        // Redirect về trang chủ nếu có lỗi hoặc thiếu thông tin
        response.sendRedirect(request.getContextPath() + "/HomeServlet");
    }
} 