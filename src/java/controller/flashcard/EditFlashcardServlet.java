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
import java.util.List;
import java.io.InputStream;
import java.util.UUID;
import config.S3Util;

@WebServlet(name = "EditFlashcardServlet", urlPatterns = {"/edit-flashcard"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 5,   // 5 MB
    maxRequestSize = 1024 * 1024 * 10 // 10 MB
)
public class EditFlashcardServlet extends HttpServlet {

    private FlashcardDAO flashcardDAO;
    private FlashcardItemDAO flashcardItemDAO;

    @Override
    public void init() throws ServletException {
        flashcardDAO = new FlashcardDAO();
        flashcardItemDAO = new FlashcardItemDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");
        
        if (user == null) {
            response.sendRedirect("LoginJSP/LoginIndex.jsp");
            return;
        }

        String flashcardIdParam = request.getParameter("id");
        if (flashcardIdParam == null || flashcardIdParam.trim().isEmpty()) {
            request.setAttribute("error", "Flashcard ID không hợp lệ");
            request.getRequestDispatcher("view-flashcard.jsp").forward(request, response);
            return;
        }

        try {
            int flashcardId = Integer.parseInt(flashcardIdParam);
            Flashcard flashcard = flashcardDAO.getFlashcardByID(flashcardId);
            
            if (flashcard == null) {
                request.setAttribute("error", "Không tìm thấy flashcard");
                request.getRequestDispatcher("view-flashcard.jsp").forward(request, response);
                return;
            }

            // Kiểm tra quyền sở hữu
            if (flashcard.getUserID() != user.getUserID()) {
                request.setAttribute("error", "Bạn không có quyền chỉnh sửa flashcard này");
                request.getRequestDispatcher("view-flashcard.jsp").forward(request, response);
                return;
            }

            // Lấy danh sách items của flashcard
            List<FlashcardItem> items = flashcardItemDAO.getFlashcardItemsByFlashcardID(flashcardId);
            
            request.setAttribute("flashcard", flashcard);
            request.setAttribute("items", items);
            request.getRequestDispatcher("edit-flashcard.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Flashcard ID không hợp lệ");
            request.getRequestDispatcher("view-flashcard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi database: " + e.getMessage());
            request.getRequestDispatcher("view-flashcard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");
        
        if (user == null) {
            response.sendRedirect("LoginJSP/LoginIndex.jsp");
            return;
        }

        String flashcardIdParam = request.getParameter("flashcardId");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String isPublic = request.getParameter("isPublic");
        String oldCoverImage = request.getParameter("oldCoverImage");
        
        // Handle cover image upload
        String coverImage = oldCoverImage != null ? oldCoverImage : "";
        Part coverImagePart = request.getPart("coverImage");
        if (coverImagePart != null && coverImagePart.getSize() > 0) {
            try {
                // Upload new image to S3
                coverImage = uploadImage(coverImagePart, "flashcard-covers");
                
                // Delete old image from S3 if exists
                if (oldCoverImage != null && !oldCoverImage.trim().isEmpty()) {
                    deleteImageFromS3(oldCoverImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Lỗi upload ảnh bìa: " + e.getMessage());
                doGet(request, response);
                return;
            }
        }

        if (flashcardIdParam == null || title == null || title.trim().isEmpty()) {
            request.setAttribute("error", "Thông tin không hợp lệ");
            doGet(request, response);
            return;
        }

        try {
            int flashcardId = Integer.parseInt(flashcardIdParam);
            Flashcard flashcard = flashcardDAO.getFlashcardByID(flashcardId);
            
            if (flashcard == null) {
                request.setAttribute("error", "Không tìm thấy flashcard");
                doGet(request, response);
                return;
            }

            // Kiểm tra quyền sở hữu
            if (flashcard.getUserID() != user.getUserID()) {
                request.setAttribute("error", "Bạn không có quyền chỉnh sửa flashcard này");
                doGet(request, response);
                return;
            }

            // Cập nhật thông tin flashcard
            flashcard.setTitle(title.trim());
            flashcard.setDescription(description != null ? description.trim() : "");
            flashcard.setPublicFlag("on".equals(isPublic));
            flashcard.setCoverImage(coverImage != null ? coverImage.trim() : "");

            boolean updateSuccess = flashcardDAO.updateFlashcard(flashcard);
            
            if (updateSuccess) {
                request.setAttribute("success", "Cập nhật flashcard thành công!");
                // Redirect về trang view flashcard
                response.sendRedirect("view-flashcard?id=" + flashcardId);
            } else {
                request.setAttribute("error", "Cập nhật flashcard thất bại");
                doGet(request, response);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Flashcard ID không hợp lệ");
            doGet(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi database: " + e.getMessage());
            doGet(request, response);
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
                    System.out.println("[EditFlashcardServlet] Đã xóa ảnh cũ: " + s3Key);
                }
            }
        } catch (Exception e) {
            System.err.println("[EditFlashcardServlet] Lỗi xóa ảnh cũ: " + e.getMessage());
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