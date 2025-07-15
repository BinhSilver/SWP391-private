package controller.courses;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import Dao.LessonAccessDAO;
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

        // 1. Lấy CourseID từ request
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu course ID");
            return;
        }

        int courseID;
        try {
            courseID = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Course ID không hợp lệ");
            return;
        }

        // 2. Khởi tạo DAO
        CoursesDAO courseDAO = new CoursesDAO();
        LessonsDAO lessonDAO = new LessonsDAO();
        LessonMaterialsDAO materialDAO = new LessonMaterialsDAO();
        QuizDAO quizDAO = new QuizDAO();
        LessonAccessDAO accessDAO = new LessonAccessDAO();

        // 3. Lấy thông tin khóa học
        System.out.println("[Debug]"+courseID);
        Course course = courseDAO.getCourseByID(courseID);
        if (course == null) {
            request.setAttribute("error", "Không tìm thấy khóa học.");
            request.getRequestDispatcher("course-detail.jsp").forward(request, response);
            return;
        }

        // 7. Lấy user từ session
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("authUser");
        request.setAttribute("currentUser", currentUser);

        // Kiểm tra quyền truy cập khóa học
        if (currentUser != null && currentUser.getRoleID() == 3) {
            // Nếu là giáo viên, chỉ cho phép xem khóa học do mình tạo
            if (course.getCreatedBy() != currentUser.getUserID()) {
                request.setAttribute("error", "Bạn không có quyền truy cập khóa học này.");
                request.getRequestDispatcher("course-detail.jsp").forward(request, response);
                return;
            }
        }

        // 4. Lấy danh sách bài học
        List<Lesson> lessons = lessonDAO.getLessonsByCourseID(courseID);

        // 5. Lấy tài liệu của từng bài học
        Map<Integer, List<LessonMaterial>> lessonMaterialsMap = materialDAO.getAllMaterialsGroupedByLesson(courseID);

        // 6. Lấy danh sách câu hỏi quiz của từng bài học
        Map<Integer, List<QuizQuestion>> quizMap = new HashMap<>();
        for (Lesson lesson : lessons) {
            List<QuizQuestion> quizQuestions = quizDAO.getQuestionsWithAnswersByLessonId(lesson.getLessonID());
            quizMap.put(lesson.getLessonID(), quizQuestions);
        }

        // 8. Lấy danh sách bài học đã "vào học" từ DB (persistent)
        Set<Integer> accessedLessons = new HashSet<>();
        boolean hasAccessedCourse = false;
        if (currentUser != null) {
            accessedLessons = accessDAO.getAccessedLessons(currentUser.getUserID());
            hasAccessedCourse = accessDAO.hasUserAccessedCourse(currentUser.getUserID(), courseID);
        }
        request.setAttribute("accessedLessons", accessedLessons);
        request.setAttribute("hasAccessedCourse", hasAccessedCourse);

        // 9. Đẩy dữ liệu về trang JSP
        request.setAttribute("course", course);
        request.setAttribute("lessons", lessons);
        request.setAttribute("lessonMaterialsMap", lessonMaterialsMap);
        request.setAttribute("quizMap", quizMap);

        request.getRequestDispatcher("course-detail.jsp").forward(request, response);
    }
}
