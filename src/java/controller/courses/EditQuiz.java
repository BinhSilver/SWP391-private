package controller.courses;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.QuizQuestion;
import model.Answer;
import service.QuizService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@WebServlet(name = "EditQuiz", urlPatterns = {"/EditQuiz"})
public class EditQuiz extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            // Đọc JSON từ body (AJAX gửi lên)
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String body = sb.toString();

            Gson gson = new Gson();
            // Map: {lessonId, quizzes:[{question, optionA,...,answer}]}
            Map<String, Object> input = gson.fromJson(body, Map.class);
            Object lessonIdObj = input.get("lessonId");
            int lessonId;
            if (lessonIdObj instanceof Double) {
                lessonId = ((Double) lessonIdObj).intValue();
            } else if (lessonIdObj instanceof String) {
                lessonId = Integer.parseInt((String) lessonIdObj);
            } else if (lessonIdObj instanceof Integer) {
                lessonId = (Integer) lessonIdObj;
            } else {
                throw new IllegalArgumentException("lessonId không hợp lệ!");
            }
            List<Map<String, Object>> quizzes = (List<Map<String, Object>>) input.get("quizzes");

            // Parse về QuizQuestion list
            List<QuizQuestion> quizQuestions = new ArrayList<>();
            for (Map<String, Object> q : quizzes) {
                String questionText = (String) q.get("question");
                String optionA = (String) q.get("optionA");
                String optionB = (String) q.get("optionB");
                String optionC = (String) q.get("optionC");
                String optionD = (String) q.get("optionD");
                String answer = (String) q.get("answer");
                int correctAnswer = "A".equals(answer) ? 1 : "B".equals(answer) ? 2 : "C".equals(answer) ? 3 : 4;
                List<Answer> answers = Arrays.asList(
                    new Answer(0, 0, optionA, 1, "A".equals(answer) ? 1 : 0),
                    new Answer(0, 0, optionB, 2, "B".equals(answer) ? 1 : 0),
                    new Answer(0, 0, optionC, 3, "C".equals(answer) ? 1 : 0),
                    new Answer(0, 0, optionD, 4, "D".equals(answer) ? 1 : 0)
                );
                QuizQuestion quizQuestion = new QuizQuestion();
                quizQuestion.setQuestion(questionText);
                quizQuestion.setCorrectAnswer(correctAnswer);
                quizQuestion.setAnswers(answers);
                quizQuestion.setTimeLimit(60); // set cứng hoặc nhận từ client
                quizQuestions.add(quizQuestion);
            }

            // Validate trước khi lưu
            if (!QuizService.validateQuizData(quizQuestions)) {
                response.setStatus(400);
                response.getWriter().write("{\"status\":\"fail\",\"msg\":\"Quiz data không hợp lệ\"}");
                return;
            }

            // Xóa quiz cũ, lưu quiz mới
            QuizService.deleteQuiz(lessonId);
            boolean ok = QuizService.saveQuestions(lessonId, quizQuestions);

            if (ok) {
                response.getWriter().write("{\"status\":\"success\"}");
            } else {
                response.getWriter().write("{\"status\":\"fail\",\"msg\":\"Lỗi lưu quiz\"}");
            }
        } catch (Exception ex) {
            response.setStatus(500);
            response.getWriter().write("{\"status\":\"fail\",\"msg\":\"" + ex.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
