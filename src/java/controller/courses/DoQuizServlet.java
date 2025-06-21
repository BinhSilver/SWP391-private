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

        int lessonId = Integer.parseInt(lessonIdRaw);

        List<QuizQuestion> questions = QuizService.loadQuizWithAnswers(lessonId);
        request.setAttribute("questions", questions);
        request.setAttribute("lessonId", lessonId);
        request.getRequestDispatcher("do-quiz.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int lessonId = Integer.parseInt(request.getParameter("lessonId"));
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

        request.setAttribute("total", questions.size());
        request.setAttribute("score", score);
        request.setAttribute("lessonId", lessonId);
        request.getRequestDispatcher("quiz-result.jsp").forward(request, response);
    }
}
