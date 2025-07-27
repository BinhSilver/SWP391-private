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
            
            if (certificatePart == null || certificatePart.getSize() == 0) {
                request.setAttribute("error", "Vui lòng chọn file chứng chỉ");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            String fileName = certificatePart.getSubmittedFileName();
            if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
                request.setAttribute("error", "Chỉ chấp nhận file PDF");
                request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
                return;
            }
            
            // Upload file lên S3
            String certificatePath = null;
            try {
                java.io.InputStream is = certificatePart.getInputStream();
                long size = certificatePart.getSize();
                String key = "certificates/" + authUser.getUserID() + "_" + System.currentTimeMillis() + ".pdf";
                String contentType = certificatePart.getContentType();
                
                String s3Url = S3Util.uploadFile(is, size, key, contentType);
                certificatePath = key;
                System.out.println("Upload chứng chỉ thành công: " + s3Url);
                System.out.println("Certificate path saved: " + certificatePath);
                
            } catch (Exception e) {
                System.err.println("Lỗi upload S3: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback: lưu local
                String uploadPath = getServletContext().getRealPath("/certificates/");
                java.io.File uploadDir = new java.io.File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                
                String localFileName = authUser.getUserID() + "_" + System.currentTimeMillis() + ".pdf";
                String filePath = uploadPath + java.io.File.separator + localFileName;
                certificatePart.write(filePath);
                certificatePath = "certificates/" + localFileName;
            }
            
            // Cập nhật user thành teacher pending (vẫn là học sinh, chờ xác nhận)
            UserDAO userDAO = new UserDAO();
            authUser.setRoleID(1); // Vẫn là học sinh, chờ admin xác nhận
            authUser.setTeacherPending(true);
            authUser.setCertificatePath(certificatePath);
            
            userDAO.updateUser(authUser);
            
            // Cập nhật session
            session.setAttribute("authUser", authUser);
            
            request.setAttribute("success", "Đã gửi chứng chỉ thành công! Admin sẽ kiểm tra và phê duyệt trong thời gian sớm nhất.");
            request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi upload chứng chỉ: " + e.getMessage());
            request.getRequestDispatcher("/upload-certificate.jsp").forward(request, response);
        }
    }
} 