package controller.courses;

import Dao.QuizDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;
import model.QuizQuestion;
import model.Answer;

@WebServlet("/api/import-quiz")
public class ImportQuizServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();

        try {
            // Đọc JSON từ body request
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();

            // Parse JSON thành Java object
            Gson gson = new Gson();
            ImportQuizRequest importReq = gson.fromJson(json, ImportQuizRequest.class);

            // Convert sang list<QuizQuestion>
            List<QuizQuestion> questions = new ArrayList<>();
            for (ImportQuizRequest.QuestionDTO q : importReq.questions) {
                List<Answer> answers = new ArrayList<>();
                for (ImportQuizRequest.OptionDTO o : q.options) {
                    Answer ans = new Answer();
                    ans.setAnswerText(o.optionText);
                    ans.setAnswerNumber(optionKeyToNumber(o.optionKey));
                    ans.setIsCorrect(o.optionKey.equals(q.correctOption) ? 1 : 0);
                    answers.add(ans);
                }

                QuizQuestion quizQuestion = new QuizQuestion(
                        0,
                        0,
                        q.questionText,
                        optionKeyToNumber(q.correctOption),
                        q.timeLimit,
                        answers
                );
                questions.add(quizQuestion);
            }

            // Gọi DAO lưu dữ liệu
            boolean success = QuizDAO.saveQuestions(importReq.lessonId, questions);

            Map<String, Object> res = new HashMap<>();
            res.put("success", success);
            res.put("message", success ? "Import thành công!" : "Import thất bại!");

            out.print(gson.toJson(res));
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> res = new HashMap<>();
            res.put("success", false);
            res.put("message", "Lỗi xử lý import: " + e.getMessage());
            out.print(new Gson().toJson(res));
            out.flush();
        }
    }

    private int optionKeyToNumber(String key) {
        return switch (key) {
            case "A" -> 1;
            case "B" -> 2;
            case "C" -> 3;
            case "D" -> 4;
            default -> 1;
        };
    }

    // DTO class để parse JSON từ front-end
    static class ImportQuizRequest {
        public int lessonId;
        public List<QuestionDTO> questions;

        static class QuestionDTO {
            public String questionText;
            public int timeLimit;
            public String correctOption;
            public List<OptionDTO> options;
        }

        static class OptionDTO {
            public String optionText;
            public String optionKey;
        }
    }
}
