package controller.courses;

import Dao.LessonAccessDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Lesson;
import model.LessonMaterial;
import model.QuizQuestion;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StudyLessonServlet", urlPatterns = {"/StudyLessonServlet"})
public class StudyLessonServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy tham số từ request
        String lessonIdParam = request.getParameter("lessonId");
        String courseIdParam = request.getParameter("courseId");

        // Kiểm tra đầu vào
        if (lessonIdParam == null || courseIdParam == null) {
            response.sendRedirect("HomeServlet");
            return;
        }

        int lessonId = Integer.parseInt(lessonIdParam);
        int courseId = Integer.parseInt(courseIdParam);

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        // Ghi nhận truy cập nếu người dùng đã đăng nhập
        if (user != null) {
            LessonAccessDAO dao = new LessonAccessDAO();
            dao.recordAccess(user.getUserID(), lessonId);
            session.setAttribute("accessedLessons", dao.getAccessedLessons(user.getUserID()));
        }

        // Lấy dữ liệu bài học, tài liệu và quiz
        Lesson lesson = LessonsDAO.getLessonById(lessonId);
        List<LessonMaterial> materials = LessonMaterialsDAO.getByLessonId(lessonId);
        List<QuizQuestion> quiz = QuizDAO.getQuestionsWithAnswersByLessonId(lessonId);

        // Gửi dữ liệu về study.jsp
        request.setAttribute("lesson", lesson);
        request.setAttribute("materials", materials);
        request.setAttribute("quiz", quiz);

        // Chuyển tiếp đến trang học
        request.getRequestDispatcher("study.jsp").forward(request, response);
    }
}
