package controller.flashcard;

import Dao.FlashcardDAO;
import Dao.FlashcardItemDAO;
import Dao.EnrollmentDAO;
import model.Flashcard;
import model.FlashcardItem;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ViewFlashcardServlet", urlPatterns = {"/view-flashcard"})
public class ViewFlashcardServlet extends HttpServlet {
    private FlashcardDAO flashcardDAO;
    private FlashcardItemDAO flashcardItemDAO;
    private EnrollmentDAO enrollmentDAO;

    @Override
    public void init() throws ServletException {
        flashcardDAO = new FlashcardDAO();
        flashcardItemDAO = new FlashcardItemDAO();
        enrollmentDAO = new EnrollmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        System.out.println("[ViewFlashcardServlet] authUser: " + (authUser != null ? authUser.getUserID() : "null"));

        if (authUser == null) {
            System.out.println("[ViewFlashcardServlet] Chưa đăng nhập, chuyển hướng login");
            response.sendRedirect("login");
            return;
        }

        String flashcardIDParam = request.getParameter("id");
        System.out.println("[ViewFlashcardServlet] flashcardIDParam: " + flashcardIDParam);
        if (flashcardIDParam == null || flashcardIDParam.trim().isEmpty()) {
            System.out.println("[ViewFlashcardServlet] Không có id, chuyển hướng flashcard");
            response.sendRedirect("flashcard");
            return;
        }

        try {
            int flashcardID = Integer.parseInt(flashcardIDParam);
            Flashcard flashcard = flashcardDAO.getFlashcardByID(flashcardID);
            System.out.println("[ViewFlashcardServlet] flashcard: " + (flashcard != null ? flashcard.getTitle() : "null"));
            if (flashcard == null) {
                System.out.println("[ViewFlashcardServlet] Không tìm thấy flashcard");
                response.sendRedirect("flashcard?error=notfound");
                return;
            }
            if (flashcard.getUserID() != authUser.getUserID() && !flashcard.isPublicFlag()) {
                System.out.println("[ViewFlashcardServlet] Không có quyền truy cập flashcard này");
                response.sendRedirect("flashcard?error=unauthorized");
                return;
            }
            // Nếu là flashcard public của khóa học, chỉ cho xem nếu user đã join khóa học đó
            if (flashcard.isPublicFlag() && flashcard.getCourseID() > 0 && flashcard.getUserID() != authUser.getUserID()) {
                boolean enrolled = enrollmentDAO.isUserEnrolled(authUser.getUserID(), flashcard.getCourseID());
                if (!enrolled) {
                    System.out.println("[ViewFlashcardServlet] User chưa join khóa học này, không cho xem flashcard public của khóa học");
                    response.sendRedirect("flashcard?error=unauthorized");
                    return;
                }
            }
            List<FlashcardItem> items = flashcardItemDAO.getFlashcardItemsByFlashcardID(flashcardID);
            System.out.println("[ViewFlashcardServlet] Số lượng items: " + (items != null ? items.size() : 0));
            
            // Log chi tiết thứ tự items để debug
            if (items != null && !items.isEmpty()) {
                System.out.println("[ViewFlashcardServlet] Thứ tự items theo OrderIndex:");
                for (int i = 0; i < items.size(); i++) {
                    FlashcardItem item = items.get(i);
                    System.out.println("  Item " + (i + 1) + ": ID=" + item.getFlashcardItemID() + 
                                     ", OrderIndex=" + item.getOrderIndex() + 
                                     ", Front='" + item.getFrontContent() + "'" +
                                     ", FrontImage=" + (item.getFrontImage() != null ? "có" : "không") +
                                     ", Back='" + item.getBackContent() + "'" +
                                     ", BackImage=" + (item.getBackImage() != null ? "có" : "không"));
                }
            }
            
            request.setAttribute("flashcard", flashcard);
            request.setAttribute("items", items);
            System.out.println("[ViewFlashcardServlet] Forwarding to view-flashcard.jsp");
            request.getRequestDispatcher("/view-flashcard.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            System.out.println("[ViewFlashcardServlet] NumberFormatException: " + e.getMessage());
            response.sendRedirect("flashcard?error=invalid");
        } catch (SQLException e) {
            System.out.println("[ViewFlashcardServlet] SQLException: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải flashcard: " + e.getMessage());
            request.getRequestDispatcher("/view-flashcard.jsp").forward(request, response);
        } catch (Exception e) {
            System.out.println("[ViewFlashcardServlet] Exception: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi không xác định: " + e.getMessage());
            request.getRequestDispatcher("/view-flashcard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
} 