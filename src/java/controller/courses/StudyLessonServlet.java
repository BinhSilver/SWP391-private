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
        System.out.println(lessonIdParam);
        // Kiểm tra đầu vào
        if (lessonIdParam == null || courseIdParam == null) {
            response.sendRedirect("HomeServlet");
            return;
        }

        int lessonId = Integer.parseInt(lessonIdParam);
        int courseId = Integer.parseInt(courseIdParam);

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        LessonAccessDAO accessDAO = new LessonAccessDAO();

        // Ghi nhận truy cập nếu người dùng đã đăng nhập
        if (user != null) {
            accessDAO.recordAccess(user.getUserID(), lessonId);
            session.setAttribute("accessedLessons", accessDAO.getAccessedLessons(user.getUserID()));
        }

        // Lấy dữ liệu bài học hiện tại
        Lesson lesson = LessonsDAO.getLessonById(lessonId);

List<LessonMaterial> materials = LessonMaterialsDAO.getByLessonId(lessonId);
for (LessonMaterial m : materials) {
    System.out.println("Material filePath: " + m.getFilePath());
    System.out.println("Material: " + m.getFileType());
    System.out.println("Material: " + m.getMaterialType());
}
List<QuizQuestion> quiz = QuizDAO.getQuestionsWithAnswersByLessonId(lessonId);


        // Lấy danh sách tất cả bài học của khóa học để hiển thị ở sidebar
        LessonsDAO lessonsDAO = new LessonsDAO();
        List<Lesson> lessons = lessonsDAO.getLessonsByCourseID(courseId);

        // Gửi dữ liệu sang trang JSP
        request.setAttribute("lesson", lesson);
        request.setAttribute("materials", materials);
        request.setAttribute("quiz", quiz);
        request.setAttribute("lessons", lessons);

        request.getRequestDispatcher("study.jsp").forward(request, response);
    }
}
