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

@WebServlet(name = "VideoCallAccessServlet", urlPatterns = {"/videocall.jsp"})
public class VideoCallAccessServlet extends HttpServlet {
    
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
        
        // Kiểm tra quyền sử dụng video call
        if (!premiumService.canUseVideoCall(user.getUserID())) {
            // Chuyển hướng đến trang thông báo lỗi
            response.sendRedirect("limit-info?error=video-call-access-denied");
            return;
        }
        
        // Nếu có quyền, forward đến trang videocall.jsp
        request.getRequestDispatcher("/videocall.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
} 