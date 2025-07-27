package controller.courses;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Answer;
import model.QuizQuestion;
import service.QuizService;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "DoQuizServlet", urlPatterns = {"/doQuiz"})
public class DoQuizServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String lessonIdRaw = request.getParameter("lessonId");
        if (lessonIdRaw == null) {
            response.sendRedirect("HomeServlet");
            return;
        }

        int lessonId;
        try {
            lessonId = Integer.parseInt(lessonIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect("HomeServlet");
            return;
        }

        List<QuizQuestion> questions = QuizService.loadQuizWithAnswers(lessonId);

        if (questions == null || questions.isEmpty()) {
            // Có thể redirect hoặc báo lỗi hiển thị thông báo
            request.setAttribute("errorMsg", "Bài học này chưa có quiz.");
            request.getRequestDispatcher("do-quiz.jsp").forward(request, response);
            return;
        }

        int courseId = QuizService.getCourseIdByLessonId(lessonId);

        // Tính tổng thời gian quiz dựa trên tổng timeLimit của từng câu hỏi
        int totalTime = 0;
        for (QuizQuestion q : questions) {
            totalTime += q.getTimeLimit();
        }
        request.setAttribute("totalTime", totalTime);

        request.setAttribute("questions", questions);
        request.setAttribute("lessonId", lessonId);
        request.setAttribute("courseId", courseId);
        request.getRequestDispatcher("do-quiz.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String lessonIdRaw = request.getParameter("lessonId");
        if (lessonIdRaw == null) {
            response.sendRedirect("HomeServlet");
            return;
        }

        int lessonId;
        try {
            lessonId = Integer.parseInt(lessonIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect("HomeServlet");
            return;
        }

        List<QuizQuestion> questions = QuizService.loadQuizWithAnswers(lessonId);

        int score = 0;
        for (QuizQuestion q : questions) {
            String selected = request.getParameter("question_" + q.getId());
            if (selected != null && !selected.isEmpty()) {
                int chosen = Integer.parseInt(selected);
                if (chosen == q.getCorrectAnswer()) {
                    score++;
                }
            }
        }

        // Lấy courseId từ form hoặc từ service
        String courseIdParam = request.getParameter("courseId");
        int courseId = (courseIdParam != null && !courseIdParam.isEmpty())
                ? Integer.parseInt(courseIdParam)
                : QuizService.getCourseIdByLessonId(lessonId);

        request.setAttribute("total", questions.size());
        request.setAttribute("score", score);
        request.setAttribute("lessonId", lessonId);
        request.setAttribute("courseId", courseId);
        request.setAttribute("questions", questions); 

        request.getRequestDispatcher("quiz-result.jsp").forward(request, response);
    }
}
