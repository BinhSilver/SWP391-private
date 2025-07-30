package controller.Authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.User;
import Dao.UserDAO;
import config.S3Util;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/upload-certificate")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 10MB max
public class UploadCertificateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        // Kiểm tra xem user đã đăng nhập chưa
        if (authUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        if (authUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try {
            Part certificatePart = request.getPart("certificate");
            
            // Kiểm tra file có được upload không
            if (certificatePart == null || certificatePart.getSize() == 0) {
                request.setAttribute("error", "❌ Vui lòng chọn file chứng chỉ để upload!");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra kích thước file (10MB = 10 * 1024 * 1024 bytes)
            long fileSize = certificatePart.getSize();
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (fileSize > maxSize) {
                request.setAttribute("error", "❌ File quá lớn! Kích thước tối đa là 10MB. File hiện tại: " + 
                    String.format("%.2f", fileSize / (1024.0 * 1024.0)) + "MB");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra tên file
            String fileName = certificatePart.getSubmittedFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                request.setAttribute("error", "❌ Tên file không hợp lệ!");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra định dạng file (chỉ chấp nhận PDF)
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                request.setAttribute("error", "❌ Chỉ chấp nhận file PDF! File hiện tại: " + fileName);
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra content type
            String contentType = certificatePart.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                request.setAttribute("error", "❌ File không phải định dạng PDF hợp lệ!");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // Upload file lên S3
            String certificatePath = null;
            try {
                java.io.InputStream is = certificatePart.getInputStream();
                long size = certificatePart.getSize();
                String key = "certificates/" + authUser.getUserID() + "_" + System.currentTimeMillis() + ".pdf";
                
                String s3Url = S3Util.uploadFile(is, size, key, contentType);
                certificatePath = key;
                System.out.println("✅ Upload chứng chỉ thành công: " + s3Url);
                System.out.println("📁 Certificate path saved: " + certificatePath);
                
            } catch (Exception e) {
                System.err.println("❌ Lỗi upload S3: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback: lưu local
                try {
                    String uploadPath = getServletContext().getRealPath("/certificates/");
                    java.io.File uploadDir = new java.io.File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    
                    String localFileName = authUser.getUserID() + "_" + System.currentTimeMillis() + ".pdf";
                    String filePath = uploadPath + java.io.File.separator + localFileName;
                    certificatePart.write(filePath);
                    certificatePath = "certificates/" + localFileName;
                    System.out.println("✅ Upload local thành công: " + filePath);
                } catch (Exception localError) {
                    System.err.println("❌ Lỗi upload local: " + localError.getMessage());
                    request.setAttribute("error", "❌ Không thể upload file! Vui lòng thử lại sau.");
                    request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                    return;
                }
            }
            
            // Cập nhật user thành teacher pending (vẫn là học sinh, chờ xác nhận)
            UserDAO userDAO = new UserDAO();
            authUser.setRoleID(1); // Vẫn là học sinh, chờ admin xác nhận
            authUser.setTeacherPending(true);
            authUser.setCertificatePath(certificatePath);
            
            try {
                userDAO.updateUser(authUser);
                System.out.println("✅ Cập nhật user thành công: UserID=" + authUser.getUserID() + ", TeacherPending=true");
                
                // Cập nhật session
                session.setAttribute("authUser", authUser);
                
                request.setAttribute("success", "✅ Đã gửi chứng chỉ thành công! Admin sẽ kiểm tra và phê duyệt trong thời gian sớm nhất.");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                
            } catch (SQLException e) {
                System.err.println("❌ Lỗi cập nhật database: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "❌ Có lỗi xảy ra khi lưu thông tin! Vui lòng thử lại sau.");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "❌ Có lỗi xảy ra khi upload chứng chỉ: " + e.getMessage());
            request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
        }
    }
} 