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
            List<Flashcard> userFlashcards = flashcardDAO.getFlashcardsByUserID(authUser.getUserID());
            System.out.println("[FlashcardServlet] userFlashcards count: " + userFlashcards.size());
            List<Flashcard> courseFlashcards = flashcardDAO.getFlashcardsFromEnrolledCourses(authUser.getUserID());
            System.out.println("[FlashcardServlet] courseFlashcards count: " + courseFlashcards.size());

            request.setAttribute("userFlashcards", userFlashcards);
            request.setAttribute("courseFlashcards", courseFlashcards);
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