package controller.Course;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "CourseDetailServlet", urlPatterns = {"/CourseDetailServlet"})
public class CourseDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing course ID");
            return;
        }

        int courseID;
        try {
            courseID = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
            return;
        }

        CoursesDAO courseDAO = new CoursesDAO();
        LessonsDAO lessonDAO = new LessonsDAO();
        LessonMaterialsDAO materialDAO = new LessonMaterialsDAO();
        QuizDAO quizDAO = new QuizDAO();

        // 1. Lấy thông tin khóa học
        Course course = courseDAO.getCourseByID(courseID);
        if (course == null) {
            request.setAttribute("error", "Không tìm thấy khóa học.");
            request.getRequestDispatcher("course-detail.jsp").forward(request, response);
            return;
        }

        // 2. Lấy danh sách bài học
        List<Lesson> lessons = lessonDAO.getLessonsByCourseID(courseID);

        // 3. Lấy tài liệu & quiz từng bài học
        Map<Integer, List<LessonMaterial>> lessonMaterialsMap = new HashMap<>();
        Map<Integer, List<QuizQuestion>> quizMap = new HashMap<>();

        for (Lesson lesson : lessons) {
            int lessonId = lesson.getLessonID();
            lessonMaterialsMap.put(lessonId, materialDAO.getMaterialsByLessonID(lessonId));
            quizMap.put(lessonId, quizDAO.getQuestionsWithAnswersByLessonId(lessonId));
        }

        // 4. Truyền dữ liệu sang JSP
        request.setAttribute("course", course);
        request.setAttribute("lessons", lessons);
        request.setAttribute("lessonMaterialsMap", lessonMaterialsMap);
        request.setAttribute("quizMap", quizMap);

        request.getRequestDispatcher("course-detail.jsp").forward(request, response);
    }
}
