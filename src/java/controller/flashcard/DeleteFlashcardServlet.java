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

@WebServlet(name = "DeleteFlashcardServlet", urlPatterns = {"/delete-flashcard"})
public class DeleteFlashcardServlet extends HttpServlet {
    private FlashcardDAO flashcardDAO;

    @Override
    public void init() throws ServletException {
        flashcardDAO = new FlashcardDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");

        if (authUser == null) {
            response.sendRedirect("login");
            return;
        }

        String flashcardIDParam = request.getParameter("flashcardID");
        if (flashcardIDParam == null || flashcardIDParam.trim().isEmpty()) {
            response.sendRedirect("flashcard?error=invalid");
            return;
        }

        try {
            int flashcardID = Integer.parseInt(flashcardIDParam);
            
            // Kiểm tra quyền sở hữu
            Flashcard flashcard = flashcardDAO.getFlashcardByID(flashcardID);
            if (flashcard == null) {
                response.sendRedirect("flashcard?error=notfound");
                return;
            }

            if (flashcard.getUserID() != authUser.getUserID()) {
                response.sendRedirect("flashcard?error=unauthorized");
                return;
            }

            // Xóa flashcard
            boolean success = flashcardDAO.deleteFlashcard(flashcardID);
            if (success) {
                response.sendRedirect("flashcard?success=deleted");
            } else {
                response.sendRedirect("flashcard?error=deletefailed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("flashcard?error=invalid");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("flashcard?error=database");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("flashcard");
    }
} 