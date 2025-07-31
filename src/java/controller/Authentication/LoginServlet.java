package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import controller.Email.EmailUtil;           // Utility cho email
import Dao.CoursesDAO;                      // Data Access Object cho Courses
import Dao.UserDAO;                         // Data Access Object cho Users
import jakarta.mail.MessagingException;     // Exception cho email
import java.io.IOException;                 // IO Exception
import java.sql.SQLException;               // SQL Exception
import java.util.List;                      // List collection
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.MultipartConfig;  // Annotation cho file upload
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.Cookie;                // Cookie handling
import jakarta.servlet.http.HttpServlet;           // Base HTTP Servlet
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpServletResponse;   // HTTP Response
import jakarta.servlet.http.HttpSession;           // Session handling
import model.Course;                        // Course model
import model.User;                          // User model
import service.PasswordService;             // Password hashing service
import jakarta.servlet.http.Part;          // File upload part
import config.S3Util;                      // AWS S3 utility

// ===== SERVLET CONFIGURATION =====
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})  // Map đến URL /login
@MultipartConfig  // Cho phép file upload
public class LoginServlet extends HttpServlet {

    // ===== GET METHOD - DISPLAY LOGIN PAGE =====
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== COOKIE HANDLING - REMEMBER ME =====
        // Đọc cookie để tự động điền email nếu user đã chọn "Remember Me"
        String rememberedEmail = getRememberedEmail(request);
        if (rememberedEmail != null && !rememberedEmail.trim().isEmpty()) {
            request.setAttribute("rememberedEmail", rememberedEmail);
            request.setAttribute("rememberMe", "on"); // Tự động check checkbox
            System.out.println("🍪 [Cookie] Đã đọc email từ cookie: " + rememberedEmail);
        }
        
        // ===== LANGUAGE PREFERENCE COOKIE =====
        // Đọc cookie cho language preference
        String language = getCookieValue(request, "language");
        if (language != null) {
            request.setAttribute("userLanguage", language);
            System.out.println("🌐 [Cookie] Language preference: " + language);
        }
        
        // ===== THEME PREFERENCE COOKIE =====
        // Đọc cookie cho theme preference
        String theme = getCookieValue(request, "theme");
        if (theme != null) {
            request.setAttribute("userTheme", theme);
            System.out.println("🎨 [Cookie] Theme preference: " + theme);
        }
        
        // ===== GOOGLE OAUTH ERROR HANDLING =====
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
        
        // ===== FORWARD TO LOGIN PAGE =====
        // Chuyển hướng đến trang login
        request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
    }

    // ===== POST METHOD - HANDLE FORM SUBMISSIONS =====
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== GET ACTION PARAMETER =====
        // Lấy action từ form để xác định hành động cần thực hiện
        String action = request.getParameter("action");

        // ===== SIGN IN HANDLING =====
        // Xử lý đăng nhập
        if ("signin".equals(action)) {
            handleSignIn(request, response);
            return;
        }

        // ===== SIGN UP HANDLING =====
        // Xử lý đăng ký
        if ("signup".equals(action)) {
            handleSignUp(request, response);
            return;
        }

        // ===== CHANGE PASSWORD HANDLING =====
        // Xử lý đổi mật khẩu
        if ("change_pass".equals(action)) {
            doPut(request, response);
            return;
        }

        // ===== FORGOT PASSWORD HANDLING =====
        // Xử lý quên mật khẩu
        if ("forgot_pass".equals(action)) {
            handleForgotPassword(request, response);
            return;
        }
    }

    // ===== PUT METHOD - HANDLE AJAX REQUESTS =====
    /**
     * Xử lý PUT request cho AJAX calls
     * Chủ yếu dùng cho việc kiểm tra email tồn tại
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // ===== EMAIL VALIDATION =====
        // Kiểm tra email có tồn tại trong database không
        String email = request.getParameter("email");
        if (email != null && !email.trim().isEmpty()) {
            try {
                UserDAO userDAO = new UserDAO();
                User existingUser = userDAO.getUserByEmail(email);
                
                // Trả về kết quả dưới dạng JSON
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                
                if (existingUser != null) {
                    response.getWriter().write("{\"exists\": true}");
                } else {
                    response.getWriter().write("{\"exists\": false}");
                }
            } catch (Exception e) {
                response.getWriter().write("{\"error\": \"Database error\"}");
            }
        }
    }

    // ===== SIGN IN HANDLER =====
    /**
     * Xử lý đăng nhập user
     * Quy trình:
     * 1. Validate input
     * 2. Kiểm tra user trong database
     * 3. Verify password
     * 4. Set session và cookies
     * 5. Redirect
     */
    private void handleSignIn(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== GET FORM PARAMETERS =====
        // Lấy thông tin từ form đăng nhập
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        // ===== INPUT VALIDATION =====
        // Kiểm tra input có hợp lệ không
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("message", "❌ Vui lòng nhập đầy đủ email và mật khẩu!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }
        
        try {
            // ===== DATABASE OPERATIONS =====
            // Tìm user trong database
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);
            
            // ===== USER NOT FOUND =====
            // Kiểm tra user có tồn tại không
            if (user == null) {
                request.setAttribute("message", "❌ Email không tồn tại trong hệ thống!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            // ===== GOOGLE USER VALIDATION =====
            // Kiểm tra nếu user được tạo bằng Google
            if (userDAO.isGoogleUser(email)) {
                request.setAttribute("message", "❌ Tài khoản này được tạo bằng Google. Vui lòng đăng nhập bằng Google.");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            // ===== PASSWORD VERIFICATION =====
            // Kiểm tra password có đúng không
            if (!checkPassword(password, user.getPasswordHash())) {
                request.setAttribute("message", "❌ Mật khẩu không đúng!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            // ===== ACCOUNT STATUS CHECK =====
            // Kiểm tra tài khoản có bị khóa không
            if (user.isLocked()) {
                request.setAttribute("message", "❌ Tài khoản đã bị khóa. Vui lòng liên hệ admin!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            // ===== SESSION MANAGEMENT =====
            // Tạo session và lưu thông tin user
            HttpSession session = request.getSession();
            session.setAttribute("authUser", user);
            System.out.println("✅ [Login] User " + user.getEmail() + " đăng nhập thành công!");
            
            // ===== REMEMBER ME COOKIE =====
            // Xử lý "Remember Me" functionality
            if ("on".equals(rememberMe)) {
                setRememberMeCookies(response, email);
                System.out.println("🍪 [Cookie] Đã set Remember Me cho email: " + email);
            } else {
                clearRememberMeCookies(response);
                System.out.println("🍪 [Cookie] Đã clear Remember Me cookies");
            }
            
            // ===== REDIRECT AFTER LOGIN =====
            // Chuyển hướng về trang chủ sau khi đăng nhập thành công
            response.sendRedirect("HomeServlet");
            
        } catch (Exception e) {
            // ===== ERROR HANDLING =====
            // Xử lý lỗi database
            System.err.println("❌ [Login] Database error: " + e.getMessage());
            request.setAttribute("message", "❌ Lỗi hệ thống. Vui lòng thử lại sau!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }

    // ===== SIGN UP HANDLER =====
    /**
     * Xử lý đăng ký user mới
     * Quy trình:
     * 1. Validate input
     * 2. Kiểm tra email đã tồn tại chưa
     * 3. Validate teacher certificate (nếu cần)
     * 4. Gửi OTP
     * 5. Lưu thông tin vào session
     */
    private void handleSignUp(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== GET FORM PARAMETERS =====
        // Lấy thông tin từ form đăng ký
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String gender = request.getParameter("gender");
        String role = request.getParameter("role");
        
        // ===== INPUT VALIDATION =====
        // Kiểm tra input có hợp lệ không
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("message", "❌ Vui lòng nhập đầy đủ thông tin!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }
        
        // ===== PASSWORD VALIDATION =====
        // Kiểm tra password và confirm password có khớp không
        if (!password.equals(confirmPassword)) {
            request.setAttribute("message", "❌ Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }
        
        // ===== PASSWORD LENGTH VALIDATION =====
        // Kiểm tra độ dài password
        if (password.length() < 6) {
            request.setAttribute("message", "❌ Mật khẩu phải có ít nhất 6 ký tự!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }
        
        // ===== FULL NAME VALIDATION =====
        // Kiểm tra độ dài full name
        if (fullName.length() < 2) {
            request.setAttribute("message", "❌ Họ và tên phải có ít nhất 2 ký tự!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }
        
        try {
            // ===== DATABASE OPERATIONS =====
            // Kiểm tra email đã tồn tại chưa
            UserDAO userDAO = new UserDAO();
            User existingUser = userDAO.getUserByEmail(email);
            
            if (existingUser != null) {
                // ===== GOOGLE USER CHECK =====
                // Kiểm tra nếu email đã được tạo bằng Google
                if (userDAO.isGoogleUser(email)) {
                    request.setAttribute("message", "❌ Tài khoản này được tạo bằng Google. Vui lòng đăng nhập bằng Google.");
                } else {
                    request.setAttribute("message", "❌ Email đã tồn tại trong hệ thống!");
                }
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            // ===== TEACHER REGISTRATION VALIDATION =====
            // Xử lý đăng ký giáo viên
            boolean isTeacherPending = false;
            String certificatePath = null;
            
            if ("teacher".equals(role)) {
                // ===== CERTIFICATE FILE VALIDATION =====
                // Kiểm tra file chứng chỉ cho giáo viên
                Part certificatePart = request.getPart("certificate");
                
                if (certificatePart == null || certificatePart.getSize() == 0) {
                    request.setAttribute("message", "❌ Vui lòng upload file chứng chỉ khi đăng ký làm giáo viên!");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
                
                // ===== FILE TYPE VALIDATION =====
                // Kiểm tra loại file có phải PDF không
                String fileName = certificatePart.getSubmittedFileName();
                if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
                    request.setAttribute("message", "❌ File chứng chỉ phải là định dạng PDF!");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
                
                // ===== FILE SIZE VALIDATION =====
                // Kiểm tra kích thước file
                if (certificatePart.getSize() > 10 * 1024 * 1024) { // 10MB
                    request.setAttribute("message", "❌ File chứng chỉ không được lớn hơn 10MB!");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
                
                // ===== UPLOAD CERTIFICATE =====
                // Upload file chứng chỉ lên S3
                try {
                    java.io.InputStream is = certificatePart.getInputStream();
                    long size = certificatePart.getSize();
                    String key = "certificates/certificate_" + System.currentTimeMillis() + ".pdf";
                    String contentType = certificatePart.getContentType();
                    certificatePath = S3Util.uploadFile(is, size, key, contentType);
                    isTeacherPending = true;
                    System.out.println("📄 [Certificate] Đã upload certificate: " + certificatePath);
                } catch (Exception e) {
                    System.err.println("❌ [Certificate] Upload error: " + e.getMessage());
                    request.setAttribute("message", "❌ Lỗi upload file chứng chỉ. Vui lòng thử lại!");
                    request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                    return;
                }
            }
            
            // ===== SEND OTP =====
            // Gửi OTP qua email
            try {
                String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                EmailUtil.sendOtpEmail(email, otp);
                // Lưu OTP vào session để verify
                HttpSession session = request.getSession();
                session.setAttribute("otp_" + email, otp);
                System.out.println("📧 [OTP] Đã gửi OTP cho email: " + email);
            } catch (MessagingException e) {
                System.err.println("❌ [OTP] Send error: " + e.getMessage());
                request.setAttribute("message", "❌ Lỗi gửi OTP. Vui lòng thử lại!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            // ===== SESSION STORAGE =====
            // Lưu thông tin đăng ký vào session để sử dụng trong OTP verification
            HttpSession session = request.getSession();
            session.setAttribute("pending_email", email);
            session.setAttribute("pending_password", password);
            session.setAttribute("pending_fullName", fullName);
            session.setAttribute("pending_gender", gender);
            session.setAttribute("pending_role", role);
            session.setAttribute("pending_isTeacherPending", isTeacherPending);
            session.setAttribute("pending_certificatePath", certificatePath);
            
            // ===== REDIRECT TO OTP PAGE =====
            // Chuyển hướng đến trang nhập OTP
            response.sendRedirect("LoginJSP/VerifyOtp.jsp");
            
        } catch (Exception e) {
            // ===== ERROR HANDLING =====
            // Xử lý lỗi database
            System.err.println("❌ [SignUp] Database error: " + e.getMessage());
            request.setAttribute("message", "❌ Lỗi hệ thống. Vui lòng thử lại sau!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }

    // ===== FORGOT PASSWORD HANDLER =====
    /**
     * Xử lý quên mật khẩu
     * Quy trình:
     * 1. Validate email
     * 2. Kiểm tra user tồn tại
     * 3. Gửi OTP
     * 4. Lưu thông tin vào session
     */
    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== GET EMAIL PARAMETER =====
        // Lấy email từ form quên mật khẩu
        String email = request.getParameter("forgotEmail");
        
        // ===== INPUT VALIDATION =====
        // Kiểm tra email có hợp lệ không
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("message", "❌ Vui lòng nhập email!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
            return;
        }
        
        try {
            // ===== DATABASE OPERATIONS =====
            // Kiểm tra user có tồn tại không
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserByEmail(email);
            
            if (user == null) {
                request.setAttribute("message", "❌ Email không tồn tại trong hệ thống!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            // ===== SEND OTP =====
            // Gửi OTP qua email
            try {
                String otp = String.format("%06d", new java.util.Random().nextInt(999999));
                EmailUtil.sendOtpEmailForResetPassword(email, otp);
                // Lưu OTP vào session để verify
                HttpSession session = request.getSession();
                session.setAttribute("otp_" + email, otp);
                System.out.println("📧 [ForgotPassword] Đã gửi OTP cho email: " + email);
            } catch (MessagingException e) {
                System.err.println("❌ [ForgotPassword] Send error: " + e.getMessage());
                request.setAttribute("message", "❌ Lỗi gửi OTP. Vui lòng thử lại!");
                request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
                return;
            }
            
            // ===== SESSION STORAGE =====
            // Lưu email vào session để sử dụng trong OTP verification
            HttpSession session = request.getSession();
            session.setAttribute("forgot_email", email);
            
            // ===== REDIRECT TO OTP PAGE =====
            // Chuyển hướng đến trang nhập OTP
            response.sendRedirect("LoginJSP/VerifyForgotOtp.jsp");
            
        } catch (Exception e) {
            // ===== ERROR HANDLING =====
            // Xử lý lỗi database
            System.err.println("❌ [ForgotPassword] Database error: " + e.getMessage());
            request.setAttribute("message", "❌ Lỗi hệ thống. Vui lòng thử lại sau!");
            request.getRequestDispatcher("LoginJSP/LoginIndex.jsp").forward(request, response);
        }
    }

    // ===== PASSWORD CHECKING =====
    /**
     * Kiểm tra password có đúng không
     * @param rawPassword Password người dùng nhập
     * @param hashedPassword Password đã hash trong database
     * @return true nếu password đúng, false nếu sai
     */
    private boolean checkPassword(String rawPassword, String hashedPassword) {
        // ===== GOOGLE USER CHECK =====
        // Nếu password hash bắt đầu bằng "GOOGLE_LOGIN_", đây là user Google
        if (hashedPassword != null && hashedPassword.startsWith("GOOGLE_LOGIN_")) {
            return false; // Google user không thể đăng nhập bằng password
        }
        
        // ===== PASSWORD COMPARISON =====
        // So sánh password (hiện tại chưa hash, cần cải thiện)
        return rawPassword.equals(hashedPassword);
    }

    // ===== REMEMBER ME COOKIE SETTING =====
    /**
     * Set cookies cho "Remember Me" functionality
     * @param response HTTP response
     * @param email Email của user
     */
    private void setRememberMeCookies(HttpServletResponse response, String email) {
        // ===== EMAIL COOKIE =====
        // Set cookie cho email
        setCookie(response, "email", email, 30 * 24 * 60 * 60, false); // 30 days
        
        // ===== REMEMBER ME FLAG =====
        // Set cookie cho remember me flag
        setCookie(response, "rememberMe", "true", 30 * 24 * 60 * 60, false); // 30 days
    }

    // ===== REMEMBER ME COOKIE CLEARING =====
    /**
     * Clear cookies cho "Remember Me" functionality
     * @param response HTTP response
     */
    private void clearRememberMeCookies(HttpServletResponse response) {
        // ===== CLEAR EMAIL COOKIE =====
        // Clear cookie cho email
        setCookie(response, "email", "", 0, false);
        
        // ===== CLEAR REMEMBER ME FLAG =====
        // Clear cookie cho remember me flag
        setCookie(response, "rememberMe", "", 0, false);
    }

    // ===== REMEMBERED EMAIL GETTER =====
    /**
     * Lấy email từ cookie "Remember Me"
     * @param request HTTP request
     * @return Email đã lưu trong cookie hoặc null
     */
    private String getRememberedEmail(HttpServletRequest request) {
        return getCookieValue(request, "email");
    }

    // ===== COOKIE VALUE GETTER =====
    /**
     * Lấy giá trị của một cookie theo tên
     * @param request HTTP request
     * @param cookieName Tên cookie cần lấy
     * @return Giá trị cookie hoặc null nếu không tìm thấy
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        // ===== GET ALL COOKIES =====
        // Lấy tất cả cookies từ request
        Cookie[] cookies = request.getCookies();
        
        if (cookies != null) {
            // ===== SEARCH FOR SPECIFIC COOKIE =====
            // Tìm cookie theo tên
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // ===== COOKIE SETTER =====
    /**
     * Set một cookie với các tham số cụ thể
     * @param response HTTP response
     * @param name Tên cookie
     * @param value Giá trị cookie
     * @param maxAge Thời gian sống (giây)
     * @param httpOnly Có phải httpOnly cookie không
     */
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        // ===== CREATE COOKIE =====
        // Tạo cookie mới
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setPath("/"); // Cookie có hiệu lực cho toàn bộ website
        
        // ===== ADD COOKIE TO RESPONSE =====
        // Thêm cookie vào response
        response.addCookie(cookie);
    }
}
