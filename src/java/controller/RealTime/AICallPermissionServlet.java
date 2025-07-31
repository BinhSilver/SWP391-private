package controller.RealTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.PremiumService;

import java.io.IOException;

@WebServlet(name = "AICallPermissionServlet", urlPatterns = {"/check-ai-call-permission"})
public class AICallPermissionServlet extends HttpServlet {
    
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
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Vui lòng đăng nhập để sử dụng tính năng này\"}");
            return;
        }
        
        boolean canUseAICall = premiumService.canUseAICall(user.getUserID());
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (canUseAICall) {
            response.getWriter().write("{\"success\": true, \"canUse\": true, \"message\": \"Bạn có thể sử dụng AI call\"}");
        } else {
            response.getWriter().write("{\"success\": true, \"canUse\": false, \"message\": \"Tính năng AI call chỉ dành cho Premium User. Vui lòng nâng cấp tài khoản để sử dụng.\"}");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
} 