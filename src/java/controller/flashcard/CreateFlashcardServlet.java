package controller.flashcard;

import Dao.FlashcardDAO;
import Dao.FlashcardItemDAO;
import config.CloudinaryUtil;
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
                    coverImageUrl = CloudinaryUtil.uploadImage(coverPart.getInputStream());
                } catch (Exception e) {
                    System.err.println("Error uploading cover image: " + e.getMessage());
                    // Không dừng quá trình nếu upload ảnh cover thất bại
                }
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
                        Part frontImagePart = request.getPart("frontImage" + i);
                        if (frontImagePart != null && frontImagePart.getSize() > 0) {
                            try {
                                String frontImageUrl = CloudinaryUtil.uploadImage(frontImagePart.getInputStream());
                                item.setFrontImage(frontImageUrl);
                            } catch (Exception e) {
                                System.err.println("Error uploading front image for card " + (i + 1) + ": " + e.getMessage());
                                // Không dừng quá trình nếu upload ảnh thất bại
                            }
                        }

                        // Upload ảnh back nếu có
                        Part backImagePart = request.getPart("backImage" + i);
                        if (backImagePart != null && backImagePart.getSize() > 0) {
                            try {
                                String backImageUrl = CloudinaryUtil.uploadImage(backImagePart.getInputStream());
                                item.setBackImage(backImageUrl);
                            } catch (Exception e) {
                                System.err.println("Error uploading back image for card " + (i + 1) + ": " + e.getMessage());
                                // Không dừng quá trình nếu upload ảnh thất bại
                            }
                        }

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