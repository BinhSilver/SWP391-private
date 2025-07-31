package controller.Authentication;

// ===== IMPORT STATEMENTS =====
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.MultipartConfig;  // Annotation cho file upload
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.HttpServlet;           // Base HTTP Servlet
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpServletResponse;   // HTTP Response
import jakarta.servlet.http.HttpSession;           // Session handling
import jakarta.servlet.http.Part;          // File upload part
import model.User;                          // User model
import Dao.UserDAO;                         // Data Access Object cho Users
import config.S3Util;                      // AWS S3 utility
import java.io.IOException;                 // IO Exception
import java.sql.SQLException;               // SQL Exception

// ===== SERVLET CONFIGURATION =====
/**
 * UploadCertificateServlet - Servlet xử lý upload chứng chỉ cho giáo viên
 * 
 * Chức năng chính:
 * - Hiển thị trang upload chứng chỉ
 * - Xử lý upload file chứng chỉ (PDF)
 * - Validate file (kích thước, định dạng, content type)
 * - Upload lên S3 hoặc lưu local
 * - Cập nhật trạng thái user thành teacher pending
 * 
 * URL mapping: /upload-certificate
 * Max file size: 10MB
 */
@WebServlet("/upload-certificate")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 10MB max
public class UploadCertificateServlet extends HttpServlet {

    // ===== GET METHOD - DISPLAY UPLOAD PAGE =====
    /**
     * Xử lý GET request để hiển thị trang upload chứng chỉ
     * Quy trình:
     * 1. Kiểm tra user đã đăng nhập chưa
     * 2. Hiển thị trang upload chứng chỉ
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== SESSION VALIDATION =====
        // Lấy thông tin user từ session
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        // ===== LOGIN CHECK =====
        // Kiểm tra xem user đã đăng nhập chưa
        if (authUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // ===== FORWARD TO UPLOAD PAGE =====
        // Hiển thị trang upload chứng chỉ
        request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
    }

    // ===== POST METHOD - HANDLE CERTIFICATE UPLOAD =====
    /**
     * Xử lý POST request để upload chứng chỉ
     * Quy trình:
     * 1. Kiểm tra user đã đăng nhập chưa
     * 2. Validate file upload
     * 3. Upload file lên S3 hoặc lưu local
     * 4. Cập nhật trạng thái user
     * 5. Hiển thị kết quả
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ===== SESSION VALIDATION =====
        // Lấy thông tin user từ session
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        // ===== LOGIN CHECK =====
        // Kiểm tra xem user đã đăng nhập chưa
        if (authUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            // ===== GET UPLOADED FILE =====
            // Lấy file chứng chỉ từ request
            Part certificatePart = request.getPart("certificate");
            
            // ===== FILE EXISTENCE CHECK =====
            // Kiểm tra file có được upload không
            if (certificatePart == null || certificatePart.getSize() == 0) {
                request.setAttribute("error", "❌ Vui lòng chọn file chứng chỉ để upload!");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // ===== FILE SIZE VALIDATION =====
            // Kiểm tra kích thước file (10MB = 10 * 1024 * 1024 bytes)
            long fileSize = certificatePart.getSize();
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (fileSize > maxSize) {
                request.setAttribute("error", "❌ File quá lớn! Kích thước tối đa là 10MB. File hiện tại: " + 
                    String.format("%.2f", fileSize / (1024.0 * 1024.0)) + "MB");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // ===== FILE NAME VALIDATION =====
            // Kiểm tra tên file
            String fileName = certificatePart.getSubmittedFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                request.setAttribute("error", "❌ Tên file không hợp lệ!");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // ===== FILE TYPE VALIDATION =====
            // Kiểm tra định dạng file (chỉ chấp nhận PDF)
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                request.setAttribute("error", "❌ Chỉ chấp nhận file PDF! File hiện tại: " + fileName);
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // ===== CONTENT TYPE VALIDATION =====
            // Kiểm tra content type
            String contentType = certificatePart.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                request.setAttribute("error", "❌ File không phải định dạng PDF hợp lệ!");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // ===== S3 UPLOAD =====
            // Upload file lên S3
            String certificatePath = null;
            try {
                // ===== PREPARE S3 UPLOAD =====
                // Chuẩn bị thông tin cho upload S3
                java.io.InputStream is = certificatePart.getInputStream();
                long size = certificatePart.getSize();
                String key = "certificates/" + authUser.getUserID() + "_" + System.currentTimeMillis() + ".pdf";
                
                // ===== EXECUTE S3 UPLOAD =====
                // Thực hiện upload lên S3
                String s3Url = S3Util.uploadFile(is, size, key, contentType);
                certificatePath = key;
                System.out.println("✅ [UploadCertificate] Upload chứng chỉ thành công: " + s3Url);
                System.out.println("📁 [UploadCertificate] Certificate path saved: " + certificatePath);
                
            } catch (Exception e) {
                // ===== S3 UPLOAD FALLBACK =====
                // Nếu upload S3 thất bại, lưu local
                System.err.println("❌ [UploadCertificate] Lỗi upload S3: " + e.getMessage());
                e.printStackTrace();
                
                // ===== LOCAL UPLOAD =====
                // Fallback: lưu local
                try {
                    // ===== PREPARE LOCAL UPLOAD =====
                    // Chuẩn bị thư mục upload local
                    String uploadPath = getServletContext().getRealPath("/certificates/");
                    java.io.File uploadDir = new java.io.File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    
                    // ===== EXECUTE LOCAL UPLOAD =====
                    // Thực hiện upload local
                    String localFileName = authUser.getUserID() + "_" + System.currentTimeMillis() + ".pdf";
                    String filePath = uploadPath + java.io.File.separator + localFileName;
                    certificatePart.write(filePath);
                    certificatePath = "certificates/" + localFileName;
                    System.out.println("✅ [UploadCertificate] Upload local thành công: " + filePath);
                } catch (Exception localError) {
                    // ===== LOCAL UPLOAD ERROR =====
                    // Xử lý lỗi upload local
                    System.err.println("❌ [UploadCertificate] Lỗi upload local: " + localError.getMessage());
                    request.setAttribute("error", "❌ Không thể upload file! Vui lòng thử lại sau.");
                    request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                    return;
                }
            }
            
            // ===== UPDATE USER STATUS =====
            // Cập nhật user thành teacher pending (vẫn là học sinh, chờ xác nhận)
            UserDAO userDAO = new UserDAO();
            authUser.setRoleID(1); // Vẫn là học sinh, chờ admin xác nhận
            authUser.setTeacherPending(true);
            authUser.setCertificatePath(certificatePath);
            
            try {
                // ===== DATABASE UPDATE =====
                // Cập nhật thông tin user trong database
                userDAO.updateUser(authUser);
                System.out.println("✅ [UploadCertificate] Cập nhật user thành công: UserID=" + authUser.getUserID() + ", TeacherPending=true");
                
                // ===== SESSION UPDATE =====
                // Cập nhật session với thông tin mới
                session.setAttribute("authUser", authUser);
                
                // ===== SUCCESS RESPONSE =====
                // Hiển thị thông báo thành công
                request.setAttribute("success", "✅ Đã gửi chứng chỉ thành công! Admin sẽ kiểm tra và phê duyệt trong thời gian sớm nhất.");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                
            } catch (SQLException e) {
                // ===== DATABASE ERROR =====
                // Xử lý lỗi database
                System.err.println("❌ [UploadCertificate] Lỗi cập nhật database: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "❌ Có lỗi xảy ra khi lưu thông tin! Vui lòng thử lại sau.");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            // ===== GENERAL ERROR HANDLING =====
            // Xử lý lỗi chung
            e.printStackTrace();
            request.setAttribute("error", "❌ Có lỗi xảy ra khi upload chứng chỉ: " + e.getMessage());
            request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
        }
    }
} 