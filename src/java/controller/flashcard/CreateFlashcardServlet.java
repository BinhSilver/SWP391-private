package controller.flashcard;

import Dao.FlashcardDAO;
import Dao.FlashcardItemDAO;
import config.S3Util;
import model.Flashcard;
import model.FlashcardItem;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import com.cloudinary.Cloudinary;

@WebServlet(name = "CreateFlashcardServlet", urlPatterns = {"/create-flashcard"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
public class CreateFlashcardServlet extends HttpServlet {
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
        User authUser = (User) session.getAttribute("authUser");

        if (authUser == null) {
            response.sendRedirect("login");
            return;
        }

        request.getRequestDispatcher("/create-flashcard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");

        if (authUser == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            // Lấy thông tin flashcard
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            boolean isPublic = "on".equals(request.getParameter("isPublic"));

            // Upload ảnh cover nếu có
            String coverImageUrl = null;
            Part coverPart = request.getPart("coverImage");
            if (coverPart != null && coverPart.getSize() > 0) {
                try {
                    java.io.InputStream is = coverPart.getInputStream();
                    long size = coverPart.getSize();
                    String originalFileName = coverPart.getSubmittedFileName();
                    String key = "flashcards/flashcard_cover_" + java.util.UUID.randomUUID();
                    if (originalFileName != null && originalFileName.toLowerCase().endsWith(".jpg")) {
                        key += ".jpg";
                    } else if (originalFileName != null && originalFileName.toLowerCase().endsWith(".png")) {
                        key += ".png";
                    }
                    String contentType = coverPart.getContentType();
                    String s3Url = config.S3Util.uploadFile(is, size, key, contentType);
                    coverImageUrl = s3Url;
                    System.out.println("[FlashcardUpload] Cover upload thành công S3: " + coverImageUrl);
                } catch (Exception e) {
                    System.err.println("[FlashcardUpload] Lỗi upload cover S3: " + e.getMessage());
                }
            }

            // Log toàn bộ các part nhận được từ request để debug
            System.out.println("[FlashcardUpload] Danh sách các part nhận được:");
            for (Part part : request.getParts()) {
                System.out.println("  Part name: " + part.getName() + ", filename: " + part.getSubmittedFileName() + ", size: " + part.getSize());
            }

            // Tạo flashcard mới
            Flashcard flashcard = new Flashcard();
            flashcard.setUserID(authUser.getUserID());
            flashcard.setTitle(title);
            flashcard.setDescription(description);
            flashcard.setPublicFlag(isPublic);
            flashcard.setCoverImage(coverImageUrl);

            int flashcardID = flashcardDAO.createFlashcard(flashcard);

            // Xử lý các flashcard items
            String[] frontContents = request.getParameterValues("frontContent");
            String[] backContents = request.getParameterValues("backContent");
            String[] notes = request.getParameterValues("note");

            if (frontContents != null && backContents != null) {
                for (int i = 0; i < frontContents.length; i++) {
                    if (frontContents[i] != null && !frontContents[i].trim().isEmpty() &&
                        backContents[i] != null && !backContents[i].trim().isEmpty()) {
                        
                        FlashcardItem item = new FlashcardItem();
                        item.setFlashcardID(flashcardID);
                        item.setFrontContent(frontContents[i].trim());
                        item.setBackContent(backContents[i].trim());
                        item.setNote(notes != null && i < notes.length ? notes[i] : "");
                        item.setOrderIndex(i + 1);

                        // Upload ảnh front nếu có
                        Part frontImagePart = request.getPart("frontImage-" + (i + 1));
                        if (frontImagePart != null && frontImagePart.getSize() > 0 && frontImagePart.getSubmittedFileName() != null && !frontImagePart.getSubmittedFileName().isEmpty()) {
                            try {
                                String fileName = frontImagePart.getSubmittedFileName();
                                System.out.println("[FlashcardUpload] Đang upload frontImage cho card " + (i + 1) + ", file: " + fileName + ", size: " + frontImagePart.getSize());
                                java.io.InputStream is = frontImagePart.getInputStream();
                                long size = frontImagePart.getSize();
                                String originalFileName = frontImagePart.getSubmittedFileName();
                                String key = "flashcards/flashcard_" + flashcardID + "_front_" + i + "_" + java.util.UUID.randomUUID();
                                if (originalFileName != null && originalFileName.toLowerCase().endsWith(".jpg")) {
                                    key += ".jpg";
                                } else if (originalFileName != null && originalFileName.toLowerCase().endsWith(".png")) {
                                    key += ".png";
                                }
                                String contentType = frontImagePart.getContentType();
                                String frontImageUrl = config.S3Util.uploadFile(is, size, key, contentType);
                                System.out.println("[FlashcardUpload] Front image upload thành công: " + frontImageUrl);
                                item.setFrontImage(frontImageUrl);
                            } catch (Exception e) {
                                System.err.println("[FlashcardUpload] Lỗi upload frontImage cho card " + (i + 1) + ": " + e.getMessage());
                            }
                        } else {
                            System.out.println("[FlashcardUpload] Không nhận được frontImage cho card " + (i + 1) + " (part null hoặc không có file)");
                        }
                        // Upload ảnh back nếu có
                        Part backImagePart = request.getPart("backImage-" + (i + 1));
                        if (backImagePart != null && backImagePart.getSize() > 0 && backImagePart.getSubmittedFileName() != null && !backImagePart.getSubmittedFileName().isEmpty()) {
                            try {
                                String fileName = backImagePart.getSubmittedFileName();
                                System.out.println("[FlashcardUpload] Đang upload backImage cho card " + (i + 1) + ", file: " + fileName + ", size: " + backImagePart.getSize());
                                java.io.InputStream is = backImagePart.getInputStream();
                                long size = backImagePart.getSize();
                                String originalFileName = backImagePart.getSubmittedFileName();
                                String key = "flashcards/flashcard_" + flashcardID + "_back_" + i + "_" + java.util.UUID.randomUUID();
                                if (originalFileName != null && originalFileName.toLowerCase().endsWith(".jpg")) {
                                    key += ".jpg";
                                } else if (originalFileName != null && originalFileName.toLowerCase().endsWith(".png")) {
                                    key += ".png";
                                }
                                String contentType = backImagePart.getContentType();
                                String backImageUrl = config.S3Util.uploadFile(is, size, key, contentType);
                                System.out.println("[FlashcardUpload] Back image upload thành công: " + backImageUrl);
                                item.setBackImage(backImageUrl);
                            } catch (Exception e) {
                                System.err.println("[FlashcardUpload] Lỗi upload backImage cho card " + (i + 1) + ": " + e.getMessage());
                            }
                        } else {
                            System.out.println("[FlashcardUpload] Không nhận được backImage cho card " + (i + 1) + " (part null hoặc không có file)");
                        }

                        // Log giá trị trước khi lưu vào DB
                        System.out.println("[FlashcardUpload] Sắp lưu item: frontImage=" + item.getFrontImage() + ", backImage=" + item.getBackImage() + ", frontContent=" + item.getFrontContent() + ", backContent=" + item.getBackContent());
                        flashcardItemDAO.createFlashcardItem(item);
                    }
                }
            }

            response.sendRedirect("flashcard?success=true");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tạo flashcard: " + e.getMessage());
            request.getRequestDispatcher("/create-flashcard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Có lỗi xảy ra: " + e.getMessage();
            if (e.getMessage().contains("Cloudinary")) {
                errorMessage = "Có lỗi xảy ra khi upload ảnh. Vui lòng thử lại với ảnh khác hoặc bỏ qua việc upload ảnh.";
            }
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/create-flashcard.jsp").forward(request, response);
        }
    }
} 