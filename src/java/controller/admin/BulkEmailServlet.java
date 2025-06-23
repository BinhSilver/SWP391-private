package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;
import Dao.UserDAO;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

@WebServlet("/send-bulk-email-admin")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class BulkEmailServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String subject = request.getParameter("subject");
        String content = request.getParameter("content");
        String[] roles = request.getParameterValues("roles");
        String sendToAll = request.getParameter("sendToAll");
        String sendToFree = request.getParameter("sendToFree");

        // Validate input
        if (subject == null || subject.isEmpty() || content == null || content.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập tiêu đề và nội dung email.");
            request.getRequestDispatcher("/BulkEmailAdmin.jsp").forward(request, response);
            return;
        }

        // Determine recipient roles
        List<String> selectedRoles = new ArrayList<>();
        if ("on".equals(sendToAll)) {
            selectedRoles.addAll(Arrays.asList("Admin", "Teacher", "Student", "Free"));
        } else if ("on".equals(sendToFree)) {
            selectedRoles.add("Free");
        } else if (roles != null && roles.length > 0) {
            selectedRoles.addAll(Arrays.asList(roles));
        }

        if (selectedRoles.isEmpty()) {
            request.setAttribute("error", "Vui lòng chọn ít nhất một vai trò người nhận.");
            request.getRequestDispatcher("/BulkEmailAdmin.jsp").forward(request, response);
            return;
        }

        // Handle file upload
        String attachmentPath = null;
        String attachmentName = null;
        Part filePart = request.getPart("attachment");
        if (filePart != null && filePart.getSize() > 0) {
            attachmentName = filePart.getSubmittedFileName();
            Path tempFile = Files.createTempFile("email_attachment_", attachmentName);
            try (var inputStream = filePart.getInputStream()) {
                Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            attachmentPath = tempFile.toString();
        }

        // Fetch users by role
        UserDAO userDAO = new UserDAO();
        List<User> users;
        try {
            users = userDAO.getUsersByRoles(selectedRoles);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi lấy danh sách người dùng: " + e.getMessage());
            request.getRequestDispatcher("/BulkEmailAdmin.jsp").forward(request, response);
            return;
        }

        if (users.isEmpty()) {
            request.setAttribute("error", "Không tìm thấy người dùng nào với vai trò đã chọn.");
            request.getRequestDispatcher("/BulkEmailAdmin.jsp").forward(request, response);
            return;
        }

        // Send emails
        int successCount = 0;
        for (User user : users) {
            try {
                BulkEmailUtil.sendEmailWithAttachment(user.getEmail(), subject, content, attachmentPath, attachmentName);
                successCount++;
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        // Clean up temporary file
        if (attachmentPath != null) {
            Files.deleteIfExists(Path.of(attachmentPath));
        }

        // Set success or partial success message
        String message = successCount == users.size()
                ? "Đã gửi email thành công đến " + successCount + " người dùng."
                : "Đã gửi email đến " + successCount + "/" + users.size() + " người dùng. Vui lòng kiểm tra log để biết chi tiết.";
        request.setAttribute("success", message);
        request.getRequestDispatcher("/BulkEmailAdmin.jsp").forward(request, response);
    }
}