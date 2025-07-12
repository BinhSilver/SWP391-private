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
            if (user != null && user.getRoleID() == 2) { // Nếu là user premium
                try {
                    UserPremiumDAO userPremiumDAO = new UserPremiumDAO();
                    // Kiểm tra xem premium đã hết hạn chưa
                    if (userPremiumDAO.checkPremiumExpired(user.getUserID())) {
                        // Nếu đã hết hạn, cập nhật role về free user (1)
                        user.setRoleID(1);
                        new UserService().updateUser(user);
                        session.setAttribute("authUser", user);
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