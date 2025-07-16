package controller.courses;

import Dao.ProgressDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Progress;
import model.User;

@WebServlet(name = "GetProgressServlet", urlPatterns = {"/GetProgressServlet"})
public class GetProgressServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(GetProgressServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder jsonBuilder = new StringBuilder();
        
        // Get parameters
        String lessonIdParam = request.getParameter("lessonId");
        String courseIdParam = request.getParameter("courseId");
        
        // Validate parameters
        if (lessonIdParam == null || courseIdParam == null) {
            LOGGER.log(Level.WARNING, "Missing required parameters: lessonId or courseId");
            jsonBuilder.append("{\"success\": false, \"error\": \"Missing required parameters\"}");
            response.getWriter().write(jsonBuilder.toString());
            return;
        }
        
        // Get user from session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");
        
        if (user == null) {
            LOGGER.log(Level.WARNING, "User not authenticated");
            jsonBuilder.append("{\"success\": false, \"error\": \"User not authenticated\"}");
            response.getWriter().write(jsonBuilder.toString());
            return;
        }
        
        try {
            int lessonId = Integer.parseInt(lessonIdParam);
            int courseId = Integer.parseInt(courseIdParam);
            
            // Get current progress from database
            ProgressDAO progressDAO = new ProgressDAO();
            Progress progress = progressDAO.getUserLessonProgress(user.getUserID(), lessonId);
            
            if (progress != null) {
                jsonBuilder.append("{\"success\": true, \"progress\": ").append(progress.getCompletionPercent()).append("}");
                LOGGER.log(Level.INFO, "Retrieved progress for user {0}, lesson {1}: {2}%", 
                        new Object[]{user.getUserID(), lessonId, progress.getCompletionPercent()});
            } else {
                jsonBuilder.append("{\"success\": true, \"progress\": 0}");
                LOGGER.log(Level.INFO, "No progress record found for user {0}, lesson {1}", 
                        new Object[]{user.getUserID(), lessonId});
            }
            
            response.getWriter().write(jsonBuilder.toString());
            
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid parameter format", e);
            jsonBuilder.append("{\"success\": false, \"error\": \"Invalid parameter format\"}");
            response.getWriter().write(jsonBuilder.toString());
        }
    }
} 