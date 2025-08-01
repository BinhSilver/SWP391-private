package controller.courses;

// ===== IMPORT STATEMENTS =====
import Dao.LessonAccessDAO;                 // Data Access Object cho Lesson Access
import Dao.LessonsDAO;                      // Data Access Object cho Lessons
import Dao.LessonMaterialsDAO;              // Data Access Object cho Lesson Materials
import Dao.ProgressDAO;                     // Data Access Object cho Progress
import Dao.QuizDAO;                         // Data Access Object cho Quiz
import Dao.VocabularyDAO;                   // Data Access Object cho Vocabulary
import Dao.EnrollmentDAO;                   // Data Access Object cho Enrollment
import jakarta.servlet.ServletException;    // Servlet Exception
import jakarta.servlet.annotation.WebServlet;       // WebServlet annotation
import jakarta.servlet.http.*;              // HTTP Servlet classes
import model.Lesson;                        // Lesson model
import model.LessonMaterial;                // Lesson Material model
import model.QuizQuestion;                  // Quiz Question model
import model.User;                          // User model
import model.Vocabulary;                    // Vocabulary model
import model.Enrollment;                    // Enrollment model

import java.io.IOException;                 // IO Exception
import java.util.ArrayList;                 // ArrayList collection
import java.util.List;                      // List collection
import java.util.logging.Level;             // Logging Level
import java.util.logging.Logger;            // Logger

// ===== SERVLET CONFIGURATION =====
/**
 * StudyLessonServlet - Servlet xử lý việc học bài học
 * 
 * Chức năng chính:
 * - Kiểm tra quyền truy cập bài học
 * - Tự động enroll user vào khóa học nếu chưa join
 * - Kiểm tra bài học có được unlock không
 * - Ghi lại việc truy cập bài học
 * - Load nội dung bài học (vocabulary, quiz, materials)
 * 
 * URL mapping: /StudyLessonServlet
 */
@WebServlet(name = "StudyLessonServlet", urlPatterns = {"/StudyLessonServlet"})
public class StudyLessonServlet extends HttpServlet {
    
    // ===== LOGGER SETUP =====
    // Logger cho việc ghi log
    private static final Logger LOGGER = Logger.getLogger(StudyLessonServlet.class.getName());

    // ===== GET METHOD - STUDY LESSON =====
    /**
     * Xử lý GET request để học bài học
     * Quy trình:
     * 1. Validate parameters (lessonId, courseId)
     * 2. Kiểm tra user đã đăng nhập chưa
     * 3. Tự động enroll user vào khóa học
     * 4. Kiểm tra bài học có được unlock không
     * 5. Ghi lại việc truy cập bài học
     * 6. Load nội dung bài học
     * 7. Forward đến trang học bài
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== GET PARAMETERS =====
        // Lấy lessonId và courseId từ request
        String lessonIdParam = request.getParameter("lessonId");
        String courseIdParam = request.getParameter("courseId");

        // ===== PARAMETER VALIDATION =====
        // Kiểm tra parameters có đầy đủ không
        if (lessonIdParam == null || courseIdParam == null) {
            LOGGER.log(Level.WARNING, "Missing required parameters");
            response.sendRedirect("HomeServlet");
            return;
        }

        // ===== PARAMETER PARSING =====
        // Chuyển đổi parameters thành int
        int lessonId = Integer.parseInt(lessonIdParam);
        int courseId = Integer.parseInt(courseIdParam);

        // ===== SESSION VALIDATION =====
        // Lấy thông tin user từ session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        // ===== LOGIN CHECK =====
        // Kiểm tra user đã đăng nhập chưa
        if (user == null) {
            LOGGER.log(Level.WARNING, "Unauthenticated user attempting to access lesson");
            response.sendRedirect("LoginServlet");
            return;
        }
        
        // ===== AUTO ENROLLMENT =====
        // Tự động join user vào khóa học nếu chưa join
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
        try {
            boolean isEnrolled = enrollmentDAO.isUserEnrolled(user.getUserID(), courseId);
            if (!isEnrolled) {
                System.out.println("=== [StudyLessonServlet] TỰ ĐỘNG JOIN USER " + user.getUserID() + " VÀO KHÓA HỌC " + courseId + " ===");
                Enrollment enrollment = new Enrollment(0, user.getUserID(), courseId, null);
                enrollmentDAO.add(enrollment);
                System.out.println("[StudyLessonServlet] User " + user.getUserID() + " đã được tự động join vào khóa học " + courseId);
            } else {
                System.out.println("[StudyLessonServlet] User " + user.getUserID() + " đã join khóa học " + courseId + " trước đó");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking/creating enrollment for user " + user.getUserID() + " in course " + courseId, e);
        }
        
        // ===== LESSON UNLOCK CHECK =====
        // Get lessons for the course to check progression
        LessonsDAO lessonsDAO = new LessonsDAO();
        List<Lesson> lessons = lessonsDAO.getLessonsByCourseID(courseId);
        
        // Check if the lesson is unlocked for this user
        ProgressDAO progressDAO = new ProgressDAO();
        boolean isLessonUnlocked = progressDAO.isLessonUnlocked(
                user.getUserID(), courseId, lessonId, lessons);
        
        // ===== LESSON ACCESS CONTROL =====
        // If the lesson is locked, redirect to course page with a message
        if (!isLessonUnlocked) {
            LOGGER.log(Level.INFO, 
                    "User {0} attempted to access locked lesson {1}", 
                    new Object[]{user.getUserID(), lessonId});
            
            request.setAttribute("errorMessage", 
                    "Bạn cần hoàn thành các bài học trước để mở khóa bài này.");
            
            response.sendRedirect(request.getContextPath() + "/CourseDetailServlet?id=" + courseId);
            return;
        }

        // ===== RECORD LESSON ACCESS =====
        // Record lesson access and start progress tracking
        LessonAccessDAO accessDAO = new LessonAccessDAO();
        accessDAO.recordAccess(user.getUserID(), lessonId);
        session.setAttribute("accessedLessons", accessDAO.getAccessedLessons(user.getUserID()));
        
        // ===== INITIALIZE PROGRESS =====
        // Get or create progress record with minimum 0% completion
        if (progressDAO.getUserLessonProgress(user.getUserID(), lessonId) == null) {
            progressDAO.updateProgress(user.getUserID(), courseId, lessonId, 0);
        }

        // ===== GET LESSON DATA =====
        // Get lesson data
        Lesson lesson = LessonsDAO.getLessonById(lessonId);
        List<LessonMaterial> materials = LessonMaterialsDAO.getByLessonId(lessonId);
        
        LOGGER.log(Level.INFO, "User {0} accessing lesson {1}: {2}", 
                new Object[]{user.getUserID(), lessonId, lesson.getTitle()});
        
        // ===== GET QUIZ QUESTIONS =====
        // Get quiz questions
        List<QuizQuestion> quiz = QuizDAO.getQuestionsWithAnswersByLessonId(lessonId);

        // ===== GET VOCABULARY =====
        // Get vocabulary for the lesson
        List<Vocabulary> vocabulary = new ArrayList<>();
        try {
            vocabulary = VocabularyDAO.getVocabularyByLessonId(lessonId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading vocabulary", e);
        }
        
        // ===== GET CURRENT PROGRESS =====
        // Get current progress
        int currentProgress = 0;
        if (progressDAO.getUserLessonProgress(user.getUserID(), lessonId) != null) {
            currentProgress = progressDAO.getUserLessonProgress(user.getUserID(), lessonId).getCompletionPercent();
        }
        
        // ===== SET REQUEST ATTRIBUTES =====
        // Set request attributes
        request.setAttribute("lesson", lesson);
        request.setAttribute("materials", materials);
        request.setAttribute("quiz", quiz);
        request.setAttribute("vocabulary", vocabulary);
        request.setAttribute("lessons", lessons);
        request.setAttribute("currentProgress", currentProgress);
        request.setAttribute("isTeacher", user.getRoleID() == 3 || user.getRoleID() == 4);
        
        // ===== PREPARE ADJACENT LESSONS =====
        // Prepare adjacent lessons for navigation
        int prevLessonId = -1;
        int nextLessonId = -1;
        
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonID() == lessonId) {
                if (i > 0) {
                    prevLessonId = lessons.get(i - 1).getLessonID();
                }
                if (i < lessons.size() - 1) {
                    nextLessonId = lessons.get(i + 1).getLessonID();
                    
                    // ===== CHECK NEXT LESSON UNLOCK STATUS =====
                    // Check if next lesson is unlocked
                    boolean isNextLessonUnlocked = progressDAO.isLessonUnlocked(
                            user.getUserID(), courseId, nextLessonId, lessons);
                    request.setAttribute("isNextLessonUnlocked", isNextLessonUnlocked);
                }
                break;
            }
        }
        
        // ===== SET NAVIGATION ATTRIBUTES =====
        // Set navigation attributes for previous and next lessons
        request.setAttribute("prevLessonId", prevLessonId);
        request.setAttribute("nextLessonId", nextLessonId);

        // ===== FORWARD TO STUDY PAGE =====
        // Forward to study page with all lesson data
        request.getRequestDispatcher("study.jsp").forward(request, response);
    }
}
