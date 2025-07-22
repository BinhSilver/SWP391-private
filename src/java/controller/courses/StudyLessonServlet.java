package controller.courses;

import Dao.LessonAccessDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.ProgressDAO;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "StudyLessonServlet", urlPatterns = {"/StudyLessonServlet"})
public class StudyLessonServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(StudyLessonServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String lessonIdParam = request.getParameter("lessonId");
        String courseIdParam = request.getParameter("courseId");

        if (lessonIdParam == null || courseIdParam == null) {
            LOGGER.log(Level.WARNING, "Missing required parameters");
            response.sendRedirect("HomeServlet");
            return;
        }

        int lessonId = Integer.parseInt(lessonIdParam);
        int courseId = Integer.parseInt(courseIdParam);

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        if (user == null) {
            LOGGER.log(Level.WARNING, "Unauthenticated user attempting to access lesson");
            response.sendRedirect("LoginServlet");
            return;
        }
        
        // Get lessons for the course to check progression
        LessonsDAO lessonsDAO = new LessonsDAO();
        List<Lesson> lessons = lessonsDAO.getLessonsByCourseID(courseId);
        
        // Check if the lesson is unlocked for this user
        ProgressDAO progressDAO = new ProgressDAO();
        boolean isLessonUnlocked = progressDAO.isLessonUnlocked(
                user.getUserID(), courseId, lessonId, lessons);
        
        // If the lesson is locked, redirect to course page with a message
        if (!isLessonUnlocked) {
            LOGGER.log(Level.INFO, 
                    "User {0} attempted to access locked lesson {1}", 
                    new Object[]{user.getUserID(), lessonId});
            
            request.setAttribute("errorMessage", 
                    "Bạn cần hoàn thành các bài học trước để mở khóa bài này.");
            
            response.sendRedirect("CourseDetailServlet?id=" + courseId);
            return;
        }

        // Record lesson access and start progress tracking
        LessonAccessDAO accessDAO = new LessonAccessDAO();
        accessDAO.recordAccess(user.getUserID(), lessonId);
        session.setAttribute("accessedLessons", accessDAO.getAccessedLessons(user.getUserID()));
        
        // Get or create progress record with minimum 0% completion
        if (progressDAO.getUserLessonProgress(user.getUserID(), lessonId) == null) {
            progressDAO.updateProgress(user.getUserID(), courseId, lessonId, 0);
            LOGGER.log(Level.INFO, 
                    "Created initial progress record for user {0} in lesson {1}", 
                    new Object[]{user.getUserID(), lessonId});
        }

        // Get lesson data
        Lesson lesson = LessonsDAO.getLessonById(lessonId);
        List<LessonMaterial> materials = LessonMaterialsDAO.getByLessonId(lessonId);
        
        LOGGER.log(Level.INFO, "User {0} accessing lesson {1}: {2}", 
                new Object[]{user.getUserID(), lessonId, lesson.getTitle()});
        
        // Get quiz questions
        List<QuizQuestion> quiz = QuizDAO.getQuestionsWithAnswersByLessonId(lessonId);

        // Get vocabulary for the lesson
        List<Vocabulary> vocabulary = new ArrayList<>();
        try {
            vocabulary = VocabularyDAO.getVocabularyByLessonId(lessonId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading vocabulary", e);
        }
        // Get current progress
        int currentProgress = 0;
        if (progressDAO.getUserLessonProgress(user.getUserID(), lessonId) != null) {
            currentProgress = progressDAO.getUserLessonProgress(user.getUserID(), lessonId).getCompletionPercent();
        }
        
        // Set request attributes
        request.setAttribute("lesson", lesson);
        request.setAttribute("materials", materials);
        request.setAttribute("quiz", quiz);
        request.setAttribute("vocabulary", vocabulary);
        request.setAttribute("lessons", lessons);
        request.setAttribute("currentProgress", currentProgress);
        request.setAttribute("isTeacher", user.getRoleID() == 3 || user.getRoleID() == 4);
        
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
                    
                    // Check if next lesson is unlocked
                    boolean isNextLessonUnlocked = progressDAO.isLessonUnlocked(
                            user.getUserID(), courseId, nextLessonId, lessons);
                    request.setAttribute("isNextLessonUnlocked", isNextLessonUnlocked);
                }
                break;
            }
        }
        
        request.setAttribute("prevLessonId", prevLessonId);
        request.setAttribute("nextLessonId", nextLessonId);

        request.getRequestDispatcher("study.jsp").forward(request, response);
    }
}
