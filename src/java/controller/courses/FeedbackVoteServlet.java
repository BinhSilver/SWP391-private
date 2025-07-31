package controller.courses;

import Dao.FeedbackVoteDAO;
import DB.JDBCConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/course/feedback/vote")
public class FeedbackVoteServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(FeedbackVoteServlet.class.getName());
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.log(Level.INFO, "[FeedbackVoteServlet] Received vote request");
        
        try {
            int feedbackId = Integer.parseInt(req.getParameter("feedbackId"));
            int userId = Integer.parseInt(req.getParameter("userId"));
            int voteType = Integer.parseInt(req.getParameter("voteType")); // 1: like, -1: dislike
            
            LOGGER.log(Level.INFO, "[FeedbackVoteServlet] feedbackId: {0}, userId: {1}, voteType: {2}", 
                    new Object[]{feedbackId, userId, voteType});
            
            try (Connection conn = JDBCConnection.getConnection()) {
                FeedbackVoteDAO voteDAO = new FeedbackVoteDAO(conn);
                var existing = voteDAO.getVote(feedbackId, userId);
                
                if (existing == null) {
                    LOGGER.log(Level.INFO, "[FeedbackVoteServlet] Adding new vote");
                    voteDAO.addVote(feedbackId, userId, voteType);
                } else if (existing.getVoteType() == voteType) {
                    LOGGER.log(Level.INFO, "[FeedbackVoteServlet] Removing existing vote");
                    voteDAO.deleteVote(feedbackId, userId); // toggle off
                } else {
                    LOGGER.log(Level.INFO, "[FeedbackVoteServlet] Updating vote");
                    voteDAO.updateVote(feedbackId, userId, voteType); // switch
                }
                
                LOGGER.log(Level.INFO, "[FeedbackVoteServlet] Vote processed successfully");
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "[FeedbackVoteServlet] Database error", e);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Database error: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "[FeedbackVoteServlet] Invalid parameters", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid parameters: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[FeedbackVoteServlet] Unexpected error", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Unexpected error: " + e.getMessage());
        }
    }
} 