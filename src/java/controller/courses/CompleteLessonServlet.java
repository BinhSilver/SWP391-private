package controller.courses;

import Dao.ProgressDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import util.LogUtil;

@WebServlet(name = "CompleteLessonServlet", urlPatterns = {"/CompleteLessonServlet"})
public class CompleteLessonServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CompleteLessonServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get parameters
        String lessonIdParam = request.getParameter("lessonId");
        String courseIdParam = request.getParameter("courseId");
        String completionPercentParam = request.getParameter("completionPercent");
        
        // Validate parameters
        if (lessonIdParam == null || courseIdParam == null) {
            LOGGER.log(Level.WARNING, "Missing required parameters: lessonId or courseId");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"Missing required parameters\"}");
            return;
        }
        
        // Get user from session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");
        
        if (user == null) {
            LOGGER.log(Level.WARNING, "User not authenticated");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"error\": \"User not authenticated\"}");
            return;
        }
        
        try {
            int lessonId = Integer.parseInt(lessonIdParam);
            int courseId = Integer.parseInt(courseIdParam);
            int completionPercent = completionPercentParam != null ? 
                    Integer.parseInt(completionPercentParam) : 100;
            
            // Update progress in database
            ProgressDAO progressDAO = new ProgressDAO();
            boolean success = progressDAO.updateProgress(
                    user.getUserID(), courseId, lessonId, completionPercent);
            
            if (success) {
                // Log to standard logger
                LOGGER.log(Level.INFO, 
                        "User {0} updated lesson {1} in course {2} with {3}%", 
                        new Object[]{user.getUserID(), lessonId, courseId, completionPercent});
                
                // Log to file for easier tracking
                LogUtil.logProgress(user.getUserID(), lessonId, courseId, completionPercent);
                
                // If the lesson is completed (100%), log a special completion entry
                if (completionPercent == 100) {
                    LogUtil.logLessonCompletion(user.getUserID(), lessonId, courseId);
                }
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": true}");
            } else {
                LOGGER.log(Level.WARNING, 
                        "Failed to update progress for user {0}, lesson {1}", 
                        new Object[]{user.getUserID(), lessonId});
                
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"success\": false, \"error\": \"Failed to update progress\"}");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid parameter format", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"Invalid parameter format\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to POST
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method not supported, use POST instead");
    }
} 