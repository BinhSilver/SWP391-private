package controller.flashcard;

import Dao.FlashcardDAO;
import Dao.FlashcardItemDAO;
import config.S3Util;
import model.Flashcard;
import model.FlashcardItem;
import model.User;
import service.PremiumService;

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

@WebServlet(name = "CreateFlashcardServlet", urlPatterns = {"/create-flashcard"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10,  // 10 MB
    maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
public class CreateFlashcardServlet extends HttpServlet {
    private FlashcardDAO flashcardDAO;
    private FlashcardItemDAO flashcardItemDAO;
    private PremiumService premiumService;

    @Override
    public void init() throws ServletException {
        flashcardDAO = new FlashcardDAO();
        flashcardItemDAO = new FlashcardItemDAO();
        premiumService = new PremiumService();
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
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        System.out.println("[CreateFlashcardServlet] authUser: " + (authUser != null ? authUser.getUserID() : "null"));

        if (authUser == null) {
            System.out.println("[CreateFlashcardServlet] Chưa đăng nhập, chuyển hướng login");
            response.sendRedirect("login");
            return;
        }

        try {
            // Kiểm tra giới hạn tạo flashcard
            if (!premiumService.canCreateFlashcard(authUser.getUserID())) {
                String limitInfo = premiumService.getLimitInfo(authUser.getUserID());
                request.setAttribute("error", "Bạn đã đạt giới hạn tạo flashcard trong tuần này. " + limitInfo);
                request.getRequestDispatcher("/create-flashcard.jsp").forward(request, response);
                return;
            }
            
            // Lấy thông tin flashcard từ form
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            boolean isPublic = true; // Mặc định là công khai
            
            System.out.println("[CreateFlashcardServlet] Thông tin flashcard: title=" + title + 
                            ", description=" + description + ", isPublic=" + isPublic);

            // Tạo flashcard mới
            Flashcard flashcard = new Flashcard();
            flashcard.setUserID(authUser.getUserID());
            flashcard.setTitle(title);
            flashcard.setDescription(description);
            flashcard.setPublicFlag(isPublic);

            // Xử lý ảnh bìa nếu có
            Part coverImagePart = request.getPart("coverImage");
            String coverImageUrl = null;
            if (coverImagePart != null && coverImagePart.getSize() > 0) {
                coverImageUrl = uploadFlashcardCoverImage(coverImagePart);
                System.out.println("[CreateFlashcardServlet] Cover image URL: " + coverImageUrl);
            }
            flashcard.setCoverImage(coverImageUrl);

            // Lưu flashcard và lấy ID
            int flashcardId = flashcardDAO.createFlashcard(flashcard);
            System.out.println("[CreateFlashcardServlet] Đã tạo flashcard mới với ID: " + flashcardId);

            // Xử lý các flashcard item
            String itemCountStr = request.getParameter("itemCount");
            int itemCount = 1;
            try {
                itemCount = Integer.parseInt(itemCountStr);
            } catch (Exception ignore) {}
            System.out.println("[CreateFlashcardServlet] Số lượng item: " + itemCount);

            // Debug: In ra tất cả parameters
            System.out.println("[CreateFlashcardServlet] All parameters:");
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String paramValue = request.getParameter(paramName);
                System.out.println("  " + paramName + " = " + paramValue);
            }

            for (int i = 1; i <= itemCount; i++) {
                String frontContent = request.getParameter("frontContent" + (i > 1 ? "-" + i : ""));
                String backContent = request.getParameter("backContent" + (i > 1 ? "-" + i : ""));
                String note = request.getParameter("note" + (i > 1 ? "-" + i : ""));
                
                // Debug: In ra tên parameter để kiểm tra
                System.out.println("[CreateFlashcardServlet] Looking for parameters:");
                System.out.println("  frontContent param: frontContent" + (i > 1 ? "-" + i : ""));
                System.out.println("  backContent param: backContent" + (i > 1 ? "-" + i : ""));
                System.out.println("  note param: note" + (i > 1 ? "-" + i : ""));
                
                System.out.println("[CreateFlashcardServlet] Item " + i + ": frontContent=" + frontContent + 
                                ", backContent=" + backContent + ", note=" + note);

                // Xử lý ảnh mặt trước và mặt sau - Sửa logic tên file
                Part frontImagePart = null;
                Part backImagePart = null;
                
                try {
                    frontImagePart = request.getPart("frontImage" + (i > 1 ? "-" + i : ""));
                } catch (Exception e) {
                    System.out.println("[CreateFlashcardServlet] Không tìm thấy frontImage cho item " + i);
                }
                
                try {
                    backImagePart = request.getPart("backImage" + (i > 1 ? "-" + i : ""));
                } catch (Exception e) {
                    System.out.println("[CreateFlashcardServlet] Không tìm thấy backImage cho item " + i);
                }
                
                String frontImageUrl = null;
                String backImageUrl = null;
                
                if (frontImagePart != null && frontImagePart.getSize() > 0) {
                    frontImageUrl = uploadFlashcardItemImage(frontImagePart);
                    System.out.println("[CreateFlashcardServlet] Front image URL for item " + i + ": " + frontImageUrl);
                }
                
                if (backImagePart != null && backImagePart.getSize() > 0) {
                    backImageUrl = uploadFlashcardItemImage(backImagePart);
                    System.out.println("[CreateFlashcardServlet] Back image URL for item " + i + ": " + backImageUrl);
                }

                // Tạo flashcard item
                FlashcardItem item = new FlashcardItem();
                item.setFlashcardID(flashcardId);
                item.setFrontContent(frontContent);
                item.setBackContent(backContent);
                item.setNote(note);
                item.setFrontImage(frontImageUrl);
                item.setBackImage(backImageUrl);
                item.setOrderIndex(i);
                
                // Debug: In ra giá trị trước khi lưu vào database
                System.out.println("[CreateFlashcardServlet] Lưu item: " +
                                "frontContent=" + (frontContent != null ? frontContent : "null") + 
                                ", backContent=" + (backContent != null ? backContent : "null") + 
                                ", frontImage=" + (frontImageUrl != null ? frontImageUrl : "null") + 
                                ", backImage=" + (backImageUrl != null ? backImageUrl : "null") + 
                                ", note=" + (note != null ? note : "null") + 
                                ", orderIndex=" + i);

                // Lưu flashcard item
                flashcardItemDAO.createFlashcardItem(item);
            }

            // Chuyển hướng đến trang xem flashcard
            response.sendRedirect("view-flashcard?id=" + flashcardId);
            
        } catch (Exception e) {
            System.out.println("[CreateFlashcardServlet] Error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tạo flashcard: " + e.getMessage());
            request.getRequestDispatcher("/create-flashcard.jsp").forward(request, response);
        }
    }

    private String uploadFlashcardCoverImage(Part coverImagePart) {
        try {
            java.io.InputStream is = coverImagePart.getInputStream();
            long size = coverImagePart.getSize();
            String originalFileName = coverImagePart.getSubmittedFileName();
            String key = "flashcards/flashcard_cover_" + java.util.UUID.randomUUID();
            if (originalFileName != null && originalFileName.toLowerCase().endsWith(".jpg")) {
                key += ".jpg";
            } else if (originalFileName != null && originalFileName.toLowerCase().endsWith(".png")) {
                key += ".png";
            }
            String contentType = coverImagePart.getContentType();
            String s3Url = config.S3Util.uploadFile(is, size, key, contentType);
            return s3Url;
        } catch (Exception e) {
            System.err.println("[CreateFlashcardServlet] Lỗi upload cover S3: " + e.getMessage());
            return null;
        }
    }

    private String uploadFlashcardItemImage(Part imagePart) {
        try {
            java.io.InputStream is = imagePart.getInputStream();
            long size = imagePart.getSize();
            String originalFileName = imagePart.getSubmittedFileName();
            String key = "flashcards/flashcard_" + imagePart.getSubmittedFileName() + "_" + java.util.UUID.randomUUID();
            if (originalFileName != null && originalFileName.toLowerCase().endsWith(".jpg")) {
                key += ".jpg";
            } else if (originalFileName != null && originalFileName.toLowerCase().endsWith(".png")) {
                key += ".png";
            }
            String contentType = imagePart.getContentType();
            String s3Url = config.S3Util.uploadFile(is, size, key, contentType);
            return s3Url;
        } catch (Exception e) {
            System.err.println("[CreateFlashcardServlet] Lỗi upload image S3: " + e.getMessage());
            return null;
        }
    }

    private int getItemCount(HttpServletRequest request) {
        // Kiểm tra các tham số frontContent, frontContent-2, frontContent-3, ...
        int count = 0;
        
        // Kiểm tra tham số frontContent (không có số) - item đầu tiên
        if (request.getParameter("frontContent") != null) {
            count = 1;
        }
        
        // Kiểm tra các tham số frontContent-2, frontContent-3, ...
        int i = 2;
        while (request.getParameter("frontContent-" + i) != null) {
            count++;
            i++;
        }
        
        // Debug: In ra tất cả các tham số để kiểm tra
        System.out.println("[CreateFlashcardServlet] Debug - Tất cả parameters:");
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            System.out.println("  " + paramName + " = " + request.getParameter(paramName));
        }
        
        System.out.println("[CreateFlashcardServlet] Đếm được " + count + " flashcard items");
        return count;
    }
} 