package controller.flashcard;

import Dao.FlashcardDAO;
import Dao.FlashcardItemDAO;
import model.Flashcard;
import model.FlashcardItem;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.UUID;
import config.S3Util;

@WebServlet(name = "EditFlashcardItemServlet", urlPatterns = {"/edit-flashcard-item"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 5,   // 5 MB
    maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class EditFlashcardItemServlet extends HttpServlet {

    private FlashcardDAO flashcardDAO;
    private FlashcardItemDAO flashcardItemDAO;

    @Override
    public void init() throws ServletException {
        flashcardDAO = new FlashcardDAO();
        flashcardItemDAO = new FlashcardItemDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");
        
        if (user == null) {
            response.sendRedirect("LoginJSP/LoginIndex.jsp");
            return;
        }

        String itemIdParam = request.getParameter("itemId");
        String flashcardIdParam = request.getParameter("flashcardId");
        String frontContent = request.getParameter("frontContent");
        String backContent = request.getParameter("backContent");
        String note = request.getParameter("note");
        String orderIndexParam = request.getParameter("orderIndex");
        String oldFrontImage = request.getParameter("oldFrontImage");
        String oldBackImage = request.getParameter("oldBackImage");
        
        // Handle front image upload
        String frontImage = oldFrontImage != null ? oldFrontImage : "";
        Part frontImagePart = request.getPart("frontImage");
        if (frontImagePart != null && frontImagePart.getSize() > 0) {
            try {
                frontImage = uploadImage(frontImagePart, "flashcard-items");
                
                // Delete old front image from S3 if exists
                if (oldFrontImage != null && !oldFrontImage.trim().isEmpty()) {
                    deleteImageFromS3(oldFrontImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": false, \"message\": \"Lỗi upload ảnh mặt trước: " + e.getMessage() + "\"}");
                return;
            }
        }
        
        // Handle back image upload
        String backImage = oldBackImage != null ? oldBackImage : "";
        Part backImagePart = request.getPart("backImage");
        if (backImagePart != null && backImagePart.getSize() > 0) {
            try {
                backImage = uploadImage(backImagePart, "flashcard-items");
                
                // Delete old back image from S3 if exists
                if (oldBackImage != null && !oldBackImage.trim().isEmpty()) {
                    deleteImageFromS3(oldBackImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": false, \"message\": \"Lỗi upload ảnh mặt sau: " + e.getMessage() + "\"}");
                return;
            }
        }

        if (itemIdParam == null || flashcardIdParam == null || frontContent == null || backContent == null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Thông tin không hợp lệ\"}");
            return;
        }

        try {
            int itemId = Integer.parseInt(itemIdParam);
            int flashcardId = Integer.parseInt(flashcardIdParam);
            int orderIndex = orderIndexParam != null ? Integer.parseInt(orderIndexParam) : 0;

            // Kiểm tra quyền sở hữu flashcard
            Flashcard flashcard = flashcardDAO.getFlashcardByID(flashcardId);
            if (flashcard == null || flashcard.getUserID() != user.getUserID()) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": false, \"message\": \"Bạn không có quyền chỉnh sửa flashcard này\"}");
                return;
            }

            // Lấy item hiện tại
            FlashcardItem item = flashcardItemDAO.getFlashcardItemByID(itemId);
            if (item == null || item.getFlashcardID() != flashcardId) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": false, \"message\": \"Không tìm thấy item\"}");
                return;
            }

            // Cập nhật thông tin item
            item.setFrontContent(frontContent.trim());
            item.setBackContent(backContent.trim());
            item.setNote(note != null ? note.trim() : "");
            item.setFrontImage(frontImage != null ? frontImage.trim() : "");
            item.setBackImage(backImage != null ? backImage.trim() : "");
            item.setOrderIndex(orderIndex);

            boolean updateSuccess = flashcardItemDAO.updateFlashcardItem(item);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            if (updateSuccess) {
                response.getWriter().write("{\"success\": true, \"message\": \"Cập nhật thành công\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"Cập nhật thất bại\"}");
            }

        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"ID không hợp lệ\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\": false, \"message\": \"Lỗi database: " + e.getMessage() + "\"}");
        }
    }
    
    private String uploadImage(Part filePart, String folder) throws Exception {
        if (filePart == null || filePart.getSize() == 0) {
            return "";
        }
        
        // Generate unique filename
        String originalFileName = filePart.getSubmittedFileName();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;
        String s3Key = folder + "/" + fileName;
        
        // Upload to S3
        try (InputStream is = filePart.getInputStream()) {
            String s3Url = S3Util.uploadFile(is, filePart.getSize(), s3Key, filePart.getContentType());
            return s3Url;
        }
    }
    
    private void deleteImageFromS3(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                // Extract S3 key from URL
                String s3Key = extractS3KeyFromUrl(imageUrl);
                if (s3Key != null) {
                    S3Util.deleteFile(s3Key);
                    System.out.println("[EditFlashcardItemServlet] Đã xóa ảnh cũ: " + s3Key);
                }
            }
        } catch (Exception e) {
            System.err.println("[EditFlashcardItemServlet] Lỗi xóa ảnh cũ: " + e.getMessage());
        }
    }
    
    private String extractS3KeyFromUrl(String imageUrl) {
        try {
            // Extract key from S3 URL
            // Example: https://bucket.s3.region.amazonaws.com/folder/filename.jpg
            if (imageUrl.contains("amazonaws.com/")) {
                return imageUrl.substring(imageUrl.indexOf("amazonaws.com/") + 14);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
} 