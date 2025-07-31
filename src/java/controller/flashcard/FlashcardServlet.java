package controller.flashcard;

// ===== IMPORT STATEMENTS =====
import Dao.FlashcardDAO;                    // Data Access Object cho Flashcards
import model.Flashcard;                     // Flashcard model
import model.User;                          // User model

import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.HttpServlet;           // Base HTTP Servlet
import jakarta.servlet.http.HttpServletRequest;    // HTTP Request
import jakarta.servlet.http.HttpServletResponse;   // HTTP Response
import jakarta.servlet.http.HttpSession;           // Session handling
import java.io.IOException;                 // IO Exception
import java.sql.SQLException;               // SQL Exception
import java.util.ArrayList;                 // ArrayList collection
import java.util.List;                      // List collection

// ===== SERVLET CONFIGURATION =====
@WebServlet(name = "FlashcardServlet", urlPatterns = {"/flashcard"})  // Map đến URL /flashcard
public class FlashcardServlet extends HttpServlet {
    
    // ===== INSTANCE VARIABLES =====
    private FlashcardDAO flashcardDAO;      // DAO instance cho flashcard operations

    // ===== INITIALIZATION =====
    @Override
    public void init() throws ServletException {
        // Khởi tạo FlashcardDAO khi servlet được load
        flashcardDAO = new FlashcardDAO();
    }

    // ===== GET METHOD - DISPLAY FLASHCARD PAGE =====
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // ===== SESSION HANDLING =====
        // Lấy session và user đã đăng nhập
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        System.out.println("[FlashcardServlet] authUser: " + (authUser != null ? authUser.getUserID() : "null"));

        // ===== AUTHENTICATION CHECK =====
        // Kiểm tra xem user đã đăng nhập chưa
        if (authUser == null) {
            System.out.println("[FlashcardServlet] Chưa đăng nhập, chuyển hướng login");
            response.sendRedirect("login");
            return;
        }

        // ===== GET FLASHCARDS =====
        try {
            System.out.println("=== [FlashcardServlet] USER " + authUser.getUserID() + " TRUY CẬP TRANG FLASHCARD ===");
            
            // Lấy tất cả flashcards mà user có thể truy cập
            // Bao gồm: flashcards của user + public flashcards từ courses đã enroll
            List<Flashcard> allFlashcards = flashcardDAO.getAllAccessibleFlashcards(authUser.getUserID());
            System.out.println("[FlashcardServlet] allFlashcards count: " + allFlashcards.size());
            
            // ===== LOGGING FOR DEBUG =====
            // Log chi tiết từng flashcard để debug
            for (Flashcard f : allFlashcards) {
                System.out.println("  - Flashcard: ID=" + f.getFlashcardID() + 
                                 ", Title=" + f.getTitle() + 
                                 ", Owner=" + f.getUserID() + 
                                 ", IsPublic=" + f.isPublicFlag() + 
                                 ", CourseID=" + f.getCourseID());
            }
            
            // ===== SET ATTRIBUTE FOR JSP =====
            // Gửi danh sách flashcards đến JSP để hiển thị
            request.setAttribute("allFlashcards", allFlashcards);
            System.out.println("[FlashcardServlet] Forwarding to flashcard.jsp");
            
            // ===== FORWARD TO FLASHCARD PAGE =====
            // Chuyển hướng đến trang flashcard.jsp
            request.getRequestDispatcher("/flashcard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            // ===== SQL ERROR HANDLING =====
            // Xử lý lỗi SQL
            System.out.println("[FlashcardServlet] SQLException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải flashcard: " + e.getMessage());
            request.getRequestDispatcher("/flashcard.jsp").forward(request, response);
            
        } catch (Exception e) {
            // ===== GENERAL ERROR HANDLING =====
            // Xử lý các lỗi khác
            System.out.println("[FlashcardServlet] Exception: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi không xác định: " + e.getMessage());
            request.getRequestDispatcher("/flashcard.jsp").forward(request, response);
        }
    }

    // ===== POST METHOD - REDIRECT TO GET =====
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chuyển POST request thành GET request
        doGet(request, response);
    }
} 