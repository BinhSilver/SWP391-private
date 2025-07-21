package controller.courses;

import Dao.FeedbackVoteDAO;
import DB.JDBCConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/course/feedback/vote")
public class FeedbackVoteServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int feedbackId = Integer.parseInt(req.getParameter("feedbackId"));
        int userId = Integer.parseInt(req.getParameter("userId"));
        int voteType = Integer.parseInt(req.getParameter("voteType")); // 1: like, -1: dislike
        try (Connection conn = JDBCConnection.getConnection()) {
            FeedbackVoteDAO voteDAO = new FeedbackVoteDAO(conn);
            var existing = voteDAO.getVote(feedbackId, userId);
            if (existing == null) {
                voteDAO.addVote(feedbackId, userId, voteType);
            } else if (existing.getVoteType() == voteType) {
                voteDAO.deleteVote(feedbackId, userId); // toggle off
            } else {
                voteDAO.updateVote(feedbackId, userId, voteType); // switch
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
} 