
package controller.Email;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailUtil {
    public static void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        final String fromEmail = "vaductai2905@gmail.com"; // email gửi
        final String password = "wout bsms srjj nnkb"; // mật khẩu email hoặc App Password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.port", "587"); 
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable", "true"); 

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject("Mã xác thực đăng ký tài khoản");
        msg.setContent("Mã OTP của bạn là: <b>" + otp + "</b>", "text/html");
        msg.setSentDate(new java.util.Date());

        Transport.send(msg);
    }
    
    public static void sendOtpEmailForResetPassword(String toEmail, String otp) throws MessagingException {
        final String fromEmail = "vaductai2905@gmail.com"; // email gửi
        final String password = "wout bsms srjj nnkb"; // mật khẩu email hoặc App Password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.port", "587"); 
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable", "true"); 

        Session session = Session.getInstance(props, new Authenticator() {
           protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
           }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject("Mã OTP đặt lại mật khẩu");
        msg.setContent("Mã OTP để đặt lại mật khẩu của bạn là: <b>" + otp + "</b><br>"
                 + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.", "text/html");
        msg.setSentDate(new java.util.Date());

        Transport.send(msg);
}

    public static void sendTeacherApprovedMail(model.User user) throws MessagingException {
        final String fromEmail = "vaductai2905@gmail.com";
        final String password = "wout bsms srjj nnkb";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail, false));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
        msg.setSubject("Xác nhận tài khoản giáo viên thành công");
        String content = "<h2>Chúc mừng bạn đã được xác nhận là giáo viên trên Wasabii!</h2>"
                + "<p>Xin chào <b>" + (user.getFullName() != null ? user.getFullName() : user.getEmail()) + "</b>,</p>"
                + "<p>Tài khoản của bạn đã được admin xác nhận thành công với vai trò giáo viên.</p>"
                + "<ul>"
                + "<li>Bạn có thể đăng nhập và sử dụng các chức năng dành cho giáo viên.</li>"
                + "<li>Nếu có thắc mắc, vui lòng liên hệ admin.</li>"
                + "</ul>"
                + "<p>Chúc bạn có trải nghiệm tuyệt vời cùng Wasabii!</p>"
                + "<hr><small>Đây là email tự động, vui lòng không trả lời email này.</small>";
        msg.setContent(content, "text/html; charset=UTF-8");
        msg.setSentDate(new java.util.Date());
        Transport.send(msg);
    }

}
