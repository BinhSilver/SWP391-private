package controller.courses;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import Dao.LessonAccessDAO;
import Dao.ProgressDAO;
import Dao.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.*;
import model.Feedback;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CourseDetailServlet", urlPatterns = {"/CourseDetailServlet"})
public class CourseDetailServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CourseDetailServlet.class.getName());

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
        ProgressDAO progressDAO = new ProgressDAO();

        // 3. Lấy thông tin khóa học
        LOGGER.log(Level.INFO, "Loading course details for ID: {0}", courseID);
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
        
        // 7.5. Xử lý parameter showAllLessons
        String showAllLessonsParam = request.getParameter("showAllLessons");
        boolean showAllLessons = "true".equals(showAllLessonsParam);
        request.setAttribute("showAllLessons", showAllLessons);

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

        // Nếu là vào trang làm quiz (doQuiz), lưu thời điểm bắt đầu vào session
        String quizParam = request.getParameter("doQuiz");
        if (quizParam != null && quizParam.equals("1")) {
            HttpSession quizSession = request.getSession();
            quizSession.setAttribute("quizStartTime", System.currentTimeMillis());
        }

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
            
            // 9. Get progress information
            Map<Integer, Integer> lessonCompletionMap = progressDAO.getLessonCompletionMap(currentUser.getUserID(), courseID);
            List<Integer> completedLessons = progressDAO.getCompletedLessons(currentUser.getUserID(), courseID);
            int overallProgress = progressDAO.getCourseCompletionPercentage(currentUser.getUserID(), courseID);
            
            // Create a map of lesson unlock status
            Map<Integer, Boolean> lessonUnlockStatus = new HashMap<>();
            for (Lesson lesson : lessons) {
                boolean isUnlocked = progressDAO.isLessonUnlocked(
                        currentUser.getUserID(), courseID, lesson.getLessonID(), lessons);
                lessonUnlockStatus.put(lesson.getLessonID(), isUnlocked);
            }
            
            request.setAttribute("lessonCompletionMap", lessonCompletionMap);
            request.setAttribute("completedLessons", completedLessons);
            request.setAttribute("overallProgress", overallProgress);
            request.setAttribute("completed", overallProgress == 100 ? 1 : 0); // Thêm dòng này để JSP nhận biết đã hoàn thành
            request.setAttribute("lessonUnlockStatus", lessonUnlockStatus);
            
            LOGGER.log(Level.INFO, "User {0} progress for course {1}: {2}%", 
                    new Object[]{currentUser.getUserID(), courseID, overallProgress});
        }
        request.setAttribute("accessedLessons", accessedLessons);
        request.setAttribute("hasAccessedCourse", hasAccessedCourse);

        // 11. Lấy danh sách feedback cho khóa học với phân trang
        try (java.sql.Connection conn = DB.JDBCConnection.getConnection()) {
            FeedbackDAO feedbackDAO = new FeedbackDAO(conn);
            
            // Lấy tham số phân trang
            String pageParam = request.getParameter("page");
            int currentPage = 1;
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) currentPage = 1;
                } catch (NumberFormatException e) {
                    currentPage = 1;
                }
            }
            
            int pageSize = 5; // Hiển thị 5 feedback mỗi trang
            int offset = (currentPage - 1) * pageSize;
            
            // Lấy tổng số feedback
            int totalFeedbacks = feedbackDAO.getFeedbackCountByCourseId(courseID);
            int totalPages = (int) Math.ceil((double) totalFeedbacks / pageSize);
            
            // Lấy feedback với phân trang
            List<Feedback> feedbacks = feedbackDAO.getFeedbacksByCourseIdWithPagination(courseID, offset, pageSize);
            
            // Kiểm tra xem user hiện tại đã viết feedback cho khóa học này chưa
            Feedback userFeedback = null;
            if (currentUser != null) {
                userFeedback = feedbackDAO.getFeedbackByUserAndCourse(currentUser.getUserID(), courseID);
            }
            
            request.setAttribute("feedbacks", feedbacks);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalFeedbacks", totalFeedbacks);
            request.setAttribute("userFeedback", userFeedback);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 10. Đẩy dữ liệu về trang JSP
        request.setAttribute("course", course);
        request.setAttribute("lessons", lessons);
        request.setAttribute("lessonMaterialsMap", lessonMaterialsMap);
        request.setAttribute("quizMap", quizMap);

        request.getRequestDispatcher("course-detail.jsp").forward(request, response);
    }
}
