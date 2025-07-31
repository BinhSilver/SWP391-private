package controller.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.PremiumService;

import java.io.IOException;

@WebServlet(name = "LimitInfoServlet", urlPatterns = {"/limit-info"})
public class LimitInfoServlet extends HttpServlet {
    
    private PremiumService premiumService;
    
    @Override
    public void init() throws ServletException {
        premiumService = new PremiumService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");
        
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        
        // Lấy thông tin giới hạn
        String limitInfo = premiumService.getLimitInfo(user.getUserID());
        boolean isPremium = premiumService.isUserPremium(user.getUserID());
        boolean canUseVideoCall = premiumService.canUseVideoCall(user.getUserID());
        boolean canUseAICall = premiumService.canUseAICall(user.getUserID());
        
        // Xử lý thông báo lỗi từ URL parameter
        String error = request.getParameter("error");
        if (error != null) {
            switch (error) {
                case "video-call-access-denied":
                    request.setAttribute("errorMessage", "Bạn không có quyền truy cập tính năng Video Call. Vui lòng nâng cấp tài khoản.");
                    break;
                case "ai-call-access-denied":
                    request.setAttribute("errorMessage", "Bạn không có quyền truy cập tính năng AI Call. Vui lòng nâng cấp tài khoản.");
                    break;
            }
        }
        
        // Set attributes cho JSP
        request.setAttribute("limitInfo", limitInfo);
        request.setAttribute("isPremium", isPremium);
        request.setAttribute("canUseVideoCall", canUseVideoCall);
        request.setAttribute("canUseAICall", canUseAICall);
        request.setAttribute("user", user);
        
        // Forward đến trang hiển thị thông tin giới hạn
        request.getRequestDispatcher("/limit-info.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
} 