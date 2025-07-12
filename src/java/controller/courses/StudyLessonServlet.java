package controller.courses;

import Dao.LessonAccessDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import Dao.VocabularyDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Lesson;
import model.LessonMaterial;
import model.QuizQuestion;
import model.User;
import model.Vocabulary;

import java.io.IOException;
import java.util.ArrayList;
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

        int lessonId = Integer.parseInt(lessonIdParam);
        int courseId = Integer.parseInt(courseIdParam);

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        LessonAccessDAO accessDAO = new LessonAccessDAO();
        if (user != null) {
            accessDAO.recordAccess(user.getUserID(), lessonId);
            session.setAttribute("accessedLessons", accessDAO.getAccessedLessons(user.getUserID()));
        }

        Lesson lesson = LessonsDAO.getLessonById(lessonId);
        List<LessonMaterial> materials = LessonMaterialsDAO.getByLessonId(lessonId);
        List<QuizQuestion> quiz = QuizDAO.getQuestionsWithAnswersByLessonId(lessonId);

        // Lấy từ vựng dựa trên LessonID
        List<Vocabulary> vocabulary = new ArrayList<>();
        try {
            vocabulary = VocabularyDAO.getVocabularyByLessonId(lessonId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LessonsDAO lessonsDAO = new LessonsDAO();
        List<Lesson> lessons = lessonsDAO.getLessonsByCourseID(courseId);

        request.setAttribute("lesson", lesson);
        request.setAttribute("materials", materials);
        request.setAttribute("quiz", quiz);
        request.setAttribute("vocabulary", vocabulary);
        request.setAttribute("lessons", lessons);

        request.getRequestDispatcher("study.jsp").forward(request, response);
    }
}
