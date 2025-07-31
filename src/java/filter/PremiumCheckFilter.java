package filter;

// ===== IMPORT STATEMENTS =====
import Dao.UserPremiumDAO;                  // Data Access Object cho UserPremium
import jakarta.servlet.*;                   // Servlet filter interfaces
import jakarta.servlet.annotation.WebFilter; // WebFilter annotation
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpSession;           // Session handling
import model.User;                          // User model
import service.UserService;                 // User service

import java.io.IOException;                 // IO Exception
import java.sql.SQLException;               // SQL Exception

// ===== PREMIUM CHECK FILTER =====
/**
 * PremiumCheckFilter - Servlet Filter để kiểm tra trạng thái Premium
 * 
 * Chức năng chính:
 * - Kiểm tra premium status của user trên mọi request
 * - Tự động downgrade Premium user về Free nếu hết hạn
 * - Load premium info vào session nếu cần
 * - Áp dụng cho tất cả requests ("/*")
 * 
 * Logic xử lý:
 * - Premium user (role 2): Có thể bị downgrade về Free nếu hết hạn
 * - Teacher/Admin: Không bị downgrade dù premium hết hạn
 * - Free user: Không bị ảnh hưởng
 */
@WebFilter("/*")  // Áp dụng cho tất cả URLs
public class PremiumCheckFilter implements Filter {
    
    // ===== INITIALIZATION =====
    /**
     * Khởi tạo filter
     * @param filterConfig Cấu hình filter
     * @throws ServletException nếu có lỗi khởi tạo
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Không cần khởi tạo gì đặc biệt
    }

    // ===== FILTER PROCESSING =====
    /**
     * Xử lý filter cho mỗi request
     * Kiểm tra và cập nhật trạng thái premium của user
     * 
     * @param request Servlet request
     * @param response Servlet response
     * @param chain Filter chain
     * @throws IOException nếu có lỗi IO
     * @throws ServletException nếu có lỗi servlet
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // ===== GET HTTP REQUEST AND SESSION =====
        // Chuyển đổi request thành HTTP request và lấy session
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);  // false = không tạo session mới
        
        // ===== CHECK SESSION AND USER =====
        // Kiểm tra session và user có tồn tại không
        if (session != null) {
            User user = (User) session.getAttribute("authUser");
            if (user != null) {
                try {
                    // ===== INITIALIZE PREMIUM DAO =====
                    // Khởi tạo DAO để kiểm tra premium status
                    UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                    
                    // ===== CHECK PREMIUM EXPIRATION =====
                    // Kiểm tra premium cho tất cả user (Free, Premium, Teacher, Admin)
                    // nhưng chỉ cập nhật role về Free nếu user hiện tại là Premium (role 2)
                    if (userPremiumDAO.checkPremiumExpired(user.getUserID())) {
                        
                        // ===== PREMIUM EXPIRED - HANDLE DOWNGRADE =====
                        if (user.getRoleID() == 2) { // Chỉ Premium user mới bị downgrade về Free
                            // Nếu đã hết hạn, cập nhật role về free user (1)
                            user.setRoleID(1);
                            new UserService().updateUser(user);
                            session.setAttribute("authUser", user);
                            session.removeAttribute("userPremium"); // Remove premium info from session
                            System.out.println("Premium expired for user: " + user.getUserID() + ", role updated to Free");
                            
                        } else {
                            // ===== TEACHER/ADMIN PREMIUM EXPIRED =====
                            // Teacher hoặc Admin có premium hết hạn nhưng vẫn giữ role
                            System.out.println("Premium expired for user: " + user.getUserID() + " (role " + user.getRoleID() + "), but role unchanged");
                        }
                        
                    } else {
                        // ===== PREMIUM STILL VALID =====
                        // Premium còn hạn, load premium info vào session nếu chưa có
                        if (session.getAttribute("userPremium") == null) {
                            model.UserPremium premium = userPremiumDAO.getCurrentUserPremium(user.getUserID());
                            if (premium != null) {
                                session.setAttribute("userPremium", premium);
                                System.out.println("Premium info loaded for user: " + user.getUserID());
                            }
                        }
                    }
                    
                } catch (SQLException e) {
                    // ===== ERROR HANDLING =====
                    // Xử lý lỗi database
                    e.printStackTrace();
                }
            }
        }
        
        // ===== CONTINUE FILTER CHAIN =====
        // Tiếp tục xử lý request qua các filter khác
        chain.doFilter(request, response);
    }

    // ===== DESTROY =====
    /**
     * Dọn dẹp filter khi destroy
     */
    @Override
    public void destroy() {
        // Không cần dọn dẹp gì đặc biệt
    }
} 