
package controller.Email;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.Random;
import jakarta.mail.MessagingException;

@WebServlet("/send-otp")
public class SendOtpServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        if(email == null || email.isEmpty()) {
            response.getWriter().write("error");
            return;
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        HttpSession session = request.getSession();
        session.setAttribute("otp_" + email, otp);
        session.setAttribute("otp_time_" + email, System.currentTimeMillis()); // Lưu thời gian gửi

        try {
            EmailUtil.sendOtpEmail(email, otp);
            response.getWriter().write("ok");
        } catch (MessagingException e) {
            e.printStackTrace();
            response.getWriter().write("error");
        }
    }
}
