package filter;

import Dao.UserPremiumDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.UserService;

import java.io.IOException;
import java.sql.SQLException;

@WebFilter("/*")
public class PremiumCheckFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);
        
        if (session != null) {
            User user = (User) session.getAttribute("authUser");
            if (user != null) {
                try {
                    UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                    
                    // Kiểm tra premium cho tất cả user (Free, Premium, Teacher, Admin)
                    // nhưng chỉ cập nhật role về Free nếu user hiện tại là Premium (role 2)
                    if (userPremiumDAO.checkPremiumExpired(user.getUserID())) {
                        if (user.getRoleID() == 2) { // Chỉ Premium user mới bị downgrade về Free
                            // Nếu đã hết hạn, cập nhật role về free user (1)
                            user.setRoleID(1);
                            new UserService().updateUser(user);
                            session.setAttribute("authUser", user);
                            session.removeAttribute("userPremium"); // Remove premium info from session
                            System.out.println("Premium expired for user: " + user.getUserID() + ", role updated to Free");
                        } else {
                            // Teacher hoặc Admin có premium hết hạn nhưng vẫn giữ role
                            System.out.println("Premium expired for user: " + user.getUserID() + " (role " + user.getRoleID() + "), but role unchanged");
                        }
                    } else {
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
                    e.printStackTrace();
                }
            }
        }
        
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
} 