package controller.flashcard;

import Dao.FlashcardDAO;
import model.Flashcard;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "FlashcardServlet", urlPatterns = {"/flashcard"})
public class FlashcardServlet extends HttpServlet {
    private FlashcardDAO flashcardDAO;

    @Override
    public void init() throws ServletException {
        flashcardDAO = new FlashcardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        System.out.println("[FlashcardServlet] authUser: " + (authUser != null ? authUser.getUserID() : "null"));

        if (authUser == null) {
            System.out.println("[FlashcardServlet] Chưa đăng nhập, chuyển hướng login");
            response.sendRedirect("login");
            return;
        }

        try {
            System.out.println("=== [FlashcardServlet] USER " + authUser.getUserID() + " TRUY CẬP TRANG FLASHCARD ===");
            List<Flashcard> allFlashcards = flashcardDAO.getAllAccessibleFlashcards(authUser.getUserID());
            System.out.println("[FlashcardServlet] allFlashcards count: " + allFlashcards.size());
            
            // Log chi tiết từng flashcard
            for (Flashcard f : allFlashcards) {
                System.out.println("  - Flashcard: ID=" + f.getFlashcardID() + 
                                 ", Title=" + f.getTitle() + 
                                 ", Owner=" + f.getUserID() + 
                                 ", IsPublic=" + f.isPublicFlag() + 
                                 ", CourseID=" + f.getCourseID());
            }
            
            request.setAttribute("allFlashcards", allFlashcards);
            System.out.println("[FlashcardServlet] Forwarding to flashcard.jsp");
            request.getRequestDispatcher("/flashcard.jsp").forward(request, response);
        } catch (SQLException e) {
            System.out.println("[FlashcardServlet] SQLException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải flashcard: " + e.getMessage());
            request.getRequestDispatcher("/flashcard.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("[FlashcardServlet] Exception: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi không xác định: " + e.getMessage());
            request.getRequestDispatcher("/flashcard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
} 