package controller.Authentication;

import controller.Email.EmailUtil;
import Dao.CoursesDAO;
import Dao.UserDAO;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Course;
import model.User;
import service.PasswordService;
import jakarta.servlet.http.Part;
import config.S3Util;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
@MultipartConfig
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Đọc cookie để tự động điền email nếu user đã chọn "Remember Me"
        String rememberedEmail = getRememberedEmail(request);
        if (rememberedEmail != null && !rememberedEmail.trim().isEmpty()) {
            request.setAttribute("rememberedEmail", rememberedEmail);
            request.setAttribute("rememberMe", "on"); // Tự động check checkbox
            System.out.println("🍪 [Cookie] Đã đọc email từ cookie: " + rememberedEmail);
        }
        
        // Đọc cookie cho language preference
        String language = getCookieValue(request, "language");
        if (language != null) {
            request.setAttribute("userLanguage", language);
            System.out.println("🌐 [Cookie] Language preference: " + language);
        }
        
        // Đọc cookie cho theme preference
        String theme = getCookieValue(request, "theme");
        if (theme != null) {
            request.setAttribute("userTheme", theme);
            System.out.println("🎨 [Cookie] Theme preference: " + theme);
        }
        
        // Xử lý error từ Google OAuth
        String error = request.getParameter("error");
        if (error != null) {
            String errorMessage = "";
            switch (error) {
                case "google_oauth_error":
                    errorMessage = "Có lỗi xảy ra khi đăng nhập bằng Google. Vui lòng thử lại.";
                    break;
                case "no_code":
                    errorMessage = "Không nhận được mã xác thực từ Google. Vui lòng thử lại.";
                    break;
                case "create_user_failed":
                    errorMessage = "Không thể tạo tài khoản mới. Vui lòng thử lại.";
                    break;
                case "google_login_failed":
                    errorMessage = "Đăng nhập Google thất bại. Vui lòng thử lại.";
                    break;
                case "token_exchange_failed":
                    errorMessage = "Không thể xác thực với Google. Vui lòng thử lại.";
                    break;
                default:
                    errorMessage = "Có lỗi xảy ra. Vui lòng thử lại.";
                    break;
            }
            request.setAttribute("message", errorMessage);
        }
        
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("signin".equals(action)) {
            handleSignIn(request, response);
            return;
        }

        if ("signup".equals(action)) {
            handleSignUp(request, response);
            return;
        }

        if ("change_pass".equals(action)) {
            doPut(request, response);
            return;
        }

        if ("forgot_pass".equals(action)) {
            handleForgotPassword(request, response);
            return;
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String oldpass = request.getParameter("oldPassword");
        String newpass = request.getParameter("newPassword");

        boolean isPasswordChanged = new PasswordService().changePassword(email, oldpass, newpass);
        User checkEmailExist = new UserDAO().getUserByEmail(email);
        if (isPasswordChanged) {
            request.setAttribute("message", "Đã thay đổi mật khẩu thành công!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        } else {
            if (checkEmailExist == null) {
                request.setAttribute("message", "Email không tồn tại!");

                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            request.setAttribute("message", "Mật khẩu cũ không đúng!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }
//Dang nhap
    private void handleSignIn(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");

        UserDAO dao = new UserDAO();
        User user = dao.getUserByEmail(email);

        if (user != null) {
            // Kiểm tra xem user có phải là Google user không
            if (dao.isGoogleUser(email)) {
                System.out.println("🚫 [Login] User cố gắng đăng nhập bằng password cho Google account: " + email);
                request.setAttribute("message", "❌ Tài khoản này được tạo bằng Google. Vui lòng đăng nhập bằng Google.");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            if (checkPassword(password, user.getPasswordHash())) {  
                if (!user.isActive()) {  // Sử dụng phương thức getter isActive() nếu trường là boolean
                    request.setAttribute("message", "Tài khoản đã bị khóa, vui lòng liên hệ admin để mở khóa");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }         
                User fullUser = null;
                try {
                    fullUser = dao.getUserById(user.getUserID());
                } catch (SQLException e) {
                    e.printStackTrace();
                    request.setAttribute("message", "Lỗi hệ thống khi đăng nhập!");

                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }

                HttpSession session = request.getSession();
                session.setAttribute("authUser", fullUser);
                session.setAttribute("userID", fullUser.getUserID());
                session.setMaxInactiveInterval(60 * 60 * 24);

                // Load premium info for all users (Free, Premium, Teacher, Admin)
                try {
                    Dao.UserPremiumDAO userPremiumDAO = new Dao.UserPremiumDAO();
                    model.UserPremium premium = userPremiumDAO.getCurrentUserPremium(fullUser.getUserID());
                    if (premium != null) {
                        session.setAttribute("userPremium", premium);
                        System.out.println("Premium info loaded for user: " + fullUser.getUserID() + " (role: " + fullUser.getRoleID() + ")");
                    }
                } catch (SQLException e) {
                    System.out.println("Error loading premium info: " + e.getMessage());
                    e.printStackTrace();
                }

                if ("on".equals(rememberMe)) {
                    setRememberMeCookies(response, email);
                } else {
                    clearRememberMeCookies(response);
                }

                // ✅ Thêm danh sách khóa học đề xuất
                CoursesDAO coursesDAO = new CoursesDAO();
                List<Course> suggestedCourses = coursesDAO.getSuggestedCourses();
                request.setAttribute("suggestedCourses", suggestedCourses);

                // ✅ Forward về index.jsp để giữ lại dữ liệu
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            } else {
                request.setAttribute("message", "Sai email hoặc mật khẩu");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("message", "Sai email hoặc mật khẩu");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }

// Đăng ký
    private void handleSignUp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String password = request.getParameter("password");
        String repass = request.getParameter("repass");
        String gender = request.getParameter("gender");
        String role = request.getParameter("role");
        Part certificatePart = null;
        String certificatePath = null;
        boolean isTeacherPending = false;

        // Kiểm tra họ tên
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("message_signup", "❌ Vui lòng nhập họ và tên!");
            request.setAttribute("registerActive", "true");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra độ dài họ tên
        if (fullName.trim().length() < 2) {
            request.setAttribute("message_signup", "❌ Họ và tên phải có ít nhất 2 ký tự!");
            request.setAttribute("registerActive", "true");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!password.equals(repass)) {
            request.setAttribute("message_signup", "Mật khẩu không trùng khớp!");
            request.setAttribute("registerActive", "true");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        // Kiểm tra email đã tồn tại
        User existingUser = new UserDAO().getUserByEmail(email);
        if (existingUser != null) {
            // Kiểm tra xem user này có được tạo bằng Google không
            UserDAO userDAO = new UserDAO();
            if (userDAO.isGoogleUser(email)) {
                System.out.println("🚫 [Registration] Email đã tồn tại và được tạo bằng Google: " + email);
                request.setAttribute("message_signup", "❌ Tài khoản này được tạo bằng Google. Vui lòng đăng nhập bằng Google.");
                request.setAttribute("registerActive", "true");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            } else {
                System.out.println("⚠️ [Registration] Email đã tồn tại (không phải Google): " + email);
                request.setAttribute("message_signup", "Email đã tồn tại!");
                request.setAttribute("registerActive", "true");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
        }

        // Nếu là giáo viên, xử lý file chứng chỉ
        if ("teacher".equals(role)) {
            isTeacherPending = true;
            try {
                certificatePart = request.getPart("certificate");
                
                // Kiểm tra file có được upload không
                if (certificatePart == null || certificatePart.getSize() == 0) {
                    request.setAttribute("message_signup", "❌ Bạn phải upload chứng chỉ để đăng ký làm giáo viên!");
                    request.setAttribute("registerActive", "true");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
                
                // Kiểm tra kích thước file (10MB)
                long fileSize = certificatePart.getSize();
                long maxSize = 10 * 1024 * 1024; // 10MB
                if (fileSize > maxSize) {
                    request.setAttribute("message_signup", "❌ File quá lớn! Kích thước tối đa là 10MB. File hiện tại: " + 
                        String.format("%.2f", fileSize / (1024.0 * 1024.0)) + "MB");
                    request.setAttribute("registerActive", "true");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
                
                // Kiểm tra tên file
                String originalFileName = certificatePart.getSubmittedFileName();
                if (originalFileName == null || originalFileName.trim().isEmpty()) {
                    request.setAttribute("message_signup", "❌ Tên file không hợp lệ!");
                    request.setAttribute("registerActive", "true");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
                
                // Kiểm tra định dạng file (chỉ chấp nhận PDF)
                if (!originalFileName.toLowerCase().endsWith(".pdf")) {
                    request.setAttribute("message_signup", "❌ Chỉ chấp nhận file PDF! File hiện tại: " + originalFileName);
                    request.setAttribute("registerActive", "true");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
                
                // Kiểm tra content type
                String contentType = certificatePart.getContentType();
                if (contentType == null || !contentType.equals("application/pdf")) {
                    request.setAttribute("message_signup", "❌ File không phải định dạng PDF hợp lệ!");
                    request.setAttribute("registerActive", "true");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
                
                try {
                    // Đọc file từ Part
                    java.io.InputStream is = certificatePart.getInputStream();
                    long size = certificatePart.getSize();
                    String key = "certificates/certificate_" + System.currentTimeMillis() + ".pdf";
                    
                    // Upload lên S3
                    String s3Url = config.S3Util.uploadFile(is, size, key, contentType);
                    certificatePath = key; // Lưu key S3 vào DB
                    System.out.println("✅ [CertificateUpload] Upload thành công S3: " + s3Url);
                } catch (Exception e) {
                    System.err.println("❌ [CertificateUpload] Lỗi upload chứng chỉ S3: " + e.getMessage());
                    e.printStackTrace();
                    // Fallback: lưu local nếu upload S3 thất bại
                    try {
                        String fileName = System.currentTimeMillis() + "_" + certificatePart.getSubmittedFileName();
                        String uploadPath = getServletContext().getRealPath("/certificates/");
                        java.io.File uploadDir = new java.io.File(uploadPath);
                        if (!uploadDir.exists()) uploadDir.mkdirs();
                        String filePath = uploadPath + java.io.File.separator + fileName;
                        certificatePart.write(filePath);
                        certificatePath = "certificates/" + fileName;
                        System.out.println("✅ [CertificateUpload] Upload local thành công: " + filePath);
                    } catch (Exception localError) {
                        System.err.println("❌ [CertificateUpload] Lỗi upload local: " + localError.getMessage());
                        request.setAttribute("message_signup", "❌ Không thể upload file! Vui lòng thử lại sau.");
                        request.setAttribute("registerActive", "true");
                        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("message_signup", "❌ Có lỗi xảy ra khi xử lý file chứng chỉ!");
                request.setAttribute("registerActive", "true");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute("pending_email", email);
        session.setAttribute("pending_fullName", fullName);
        session.setAttribute("pending_password", password);
        session.setAttribute("pending_gender", gender);
        session.setAttribute("pending_role", role);
        session.setAttribute("pending_isTeacherPending", isTeacherPending);
        session.setAttribute("pending_certificatePath", certificatePath);

        request.setAttribute("email", email);
        request.setAttribute("fullName", fullName);
        request.setAttribute("password", password);
        request.setAttribute("gender", gender);
        request.setAttribute("role", role);
        request.setAttribute("registerActive", "true");
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
    }

// Quên mật khẩu
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        // Kích hoạt giao diện form quên mật khẩu (ẩn hiện bằng JSTL)
        request.setAttribute("showForgotForm", true);

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("message_forgot", "Vui lòng nhập email.");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        User user = new UserDAO().getUserByEmail(email);
        if (user == null) {
            request.setAttribute("message_forgot", "Email không tồn tại.");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }

        // ✅ Email hợp lệ → hiển thị form OTP giống như khi đăng ký
        request.getSession().setAttribute("resetEmail", email); // dùng cho gửi OTP và verify
        request.setAttribute("showOtpForm", true);              // JSP dùng để hiện form nhập mã OTP
        request.setAttribute("email", email);                   // binding lại để form hiển thị email

        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        // Nếu user đăng nhập bằng Google, không cho phép đăng nhập bằng password thông thường
        if (hashedPassword != null && hashedPassword.startsWith("GOOGLE_LOGIN_")) {
            return false; // User này phải đăng nhập bằng Google
        }
        return rawPassword.equals(hashedPassword);
    }

    private void setRememberMeCookies(HttpServletResponse response, String email) {
        Cookie emailCookie = new Cookie("email", email);
        emailCookie.setHttpOnly(true);
        emailCookie.setMaxAge(60 * 60 * 24 * 7); // 7 ngày
        emailCookie.setPath("/");
        response.addCookie(emailCookie);
        
        // Thêm cookie cho language preference (mặc định là Vietnamese)
        Cookie languageCookie = new Cookie("language", "vi");
        languageCookie.setHttpOnly(false); // Cho phép JavaScript đọc
        languageCookie.setMaxAge(60 * 60 * 24 * 30); // 30 ngày
        languageCookie.setPath("/");
        response.addCookie(languageCookie);
        
        // Thêm cookie cho theme preference (mặc định là light)
        Cookie themeCookie = new Cookie("theme", "light");
        themeCookie.setHttpOnly(false); // Cho phép JavaScript đọc
        themeCookie.setMaxAge(60 * 60 * 24 * 30); // 30 ngày
        themeCookie.setPath("/");
        response.addCookie(themeCookie);
        
        System.out.println("🍪 [Cookie] Đã set cookies cho user: " + email);
    }

    private void clearRememberMeCookies(HttpServletResponse response) {
        Cookie emailCookie = new Cookie("email", "");
        emailCookie.setMaxAge(0);
        emailCookie.setPath("/");
        response.addCookie(emailCookie);
        
        // Clear language và theme cookies nếu cần
        Cookie languageCookie = new Cookie("language", "");
        languageCookie.setMaxAge(0);
        languageCookie.setPath("/");
        response.addCookie(languageCookie);
        
        Cookie themeCookie = new Cookie("theme", "");
        themeCookie.setMaxAge(0);
        themeCookie.setPath("/");
        response.addCookie(themeCookie);
        
        System.out.println("🍪 [Cookie] Đã clear cookies");
    }
    
    private String getRememberedEmail(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("email".equals(cookie.getName())) {
                    String email = cookie.getValue();
                    if (email != null && !email.trim().isEmpty()) {
                        return email;
                    }
                }
            }
        }
        return null;
    }
    
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.trim().isEmpty()) {
                        return value;
                    }
                }
            }
        }
        return null;
    }
    
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        response.addCookie(cookie);
        System.out.println("🍪 [Cookie] Đã set cookie: " + name + " = " + value);
    }
}
