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

        String lessonIdParam = request.getParameter("lessonId");
        String courseIdParam = request.getParameter("courseId");

        if (lessonIdParam == null || courseIdParam == null) {
            response.sendRedirect("HomeServlet");
            return;
        }

        int lessonId;
        int courseId;
        try {
            lessonId = Integer.parseInt(lessonIdParam);
            courseId = Integer.parseInt(courseIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect("HomeServlet");
            return;
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        LessonAccessDAO accessDAO = new LessonAccessDAO();

        if (user != null) {
            accessDAO.recordAccess(user.getUserID(), lessonId);
            session.setAttribute("accessedLessons", accessDAO.getAccessedLessons(user.getUserID()));
        }

        // Dữ liệu chính
        Lesson lesson = LessonsDAO.getLessonById(lessonId);
        List<LessonMaterial> materials = LessonMaterialsDAO.getByLessonId(lessonId);
        List<QuizQuestion> quiz = QuizDAO.getQuestionsWithAnswersByLessonId(lessonId);
        List<Lesson> lessons = new LessonsDAO().getLessonsByCourseID(courseId);

        // Truyền dữ liệu
        request.setAttribute("lesson", lesson);
        request.setAttribute("materials", materials);
        request.setAttribute("quiz", quiz);
        request.setAttribute("lessons", lessons);

        request.getRequestDispatcher("study.jsp").forward(request, response);
    }
}
