package controller.courses;

import Dao.FeedbackDAO;
import model.Feedback;
import DB.JDBCConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.net.URLDecoder;

@WebServlet("/course/feedback")
public class FeedbackServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int courseId = Integer.parseInt(req.getParameter("courseId"));
        try (Connection conn = JDBCConnection.getConnection()) {
            FeedbackDAO feedbackDAO = new FeedbackDAO(conn);
            List<Feedback> feedbacks = feedbackDAO.getFeedbacksByCourseId(courseId);
            req.setAttribute("feedbacks", feedbacks);
            req.getRequestDispatcher("/web/course-feedback-list.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdStr = req.getParameter("userId");
        String courseIdStr = req.getParameter("courseId");
        String content = req.getParameter("content");
        String ratingStr = req.getParameter("rating");
        String redirectUrl = req.getParameter("redirectUrl");
        System.out.println("[FeedbackServlet] POST params: userId=" + userIdStr + ", courseId=" + courseIdStr + ", rating=" + ratingStr + ", content=" + content + ", redirectUrl=" + redirectUrl);
        try {
            int userId = Integer.parseInt(userIdStr);
            int courseId = Integer.parseInt(courseIdStr);
            int rating = Integer.parseInt(ratingStr);
            try (Connection conn = JDBCConnection.getConnection()) {
                FeedbackDAO feedbackDAO = new FeedbackDAO(conn);
                Feedback feedback = new Feedback();
                feedback.setUserID(userId);
                feedback.setCourseID(courseId);
                feedback.setContent(content);
                feedback.setRating(rating);
                feedbackDAO.addFeedback(feedback);
                if (redirectUrl != null && !redirectUrl.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/" + redirectUrl);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/course/feedback?courseId=" + courseId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Đọc body thủ công cho PUT
        String body = new BufferedReader(new InputStreamReader(req.getInputStream()))
            .lines().collect(java.util.stream.Collectors.joining("&"));
        java.util.Map<String, String> params = new java.util.HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) params.put(java.net.URLDecoder.decode(kv[0], "UTF-8"), java.net.URLDecoder.decode(kv[1], "UTF-8"));
        }
        int feedbackId = Integer.parseInt(params.get("feedbackId"));
        int userId = Integer.parseInt(params.get("userId"));
        String content = params.get("content");
        int rating = Integer.parseInt(params.get("rating"));
        try (Connection conn = JDBCConnection.getConnection()) {
            FeedbackDAO feedbackDAO = new FeedbackDAO(conn);
            Feedback feedback = new Feedback();
            feedback.setFeedbackID(feedbackId);
            feedback.setUserID(userId);
            feedback.setContent(content);
            feedback.setRating(rating);
            feedbackDAO.updateFeedback(feedback);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int feedbackId = Integer.parseInt(req.getParameter("feedbackId"));
        int userId = Integer.parseInt(req.getParameter("userId"));
        try (Connection conn = JDBCConnection.getConnection()) {
            FeedbackDAO feedbackDAO = new FeedbackDAO(conn);
            feedbackDAO.deleteFeedback(feedbackId, userId);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
} 