package controller.courses;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import Dao.QuizDAO;
import model.QuizQuestion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import service.QuizService;

@WebServlet(name = "QuizSaveServlet", urlPatterns = {"/saveQuiz"})
public class QuizSaveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();

        try {
            int lessonId = Integer.parseInt(req.getParameter("lessonId"));

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String json = sb.toString();
            Type listType = new TypeToken<List<QuizQuestion>>() {
            }.getType();
            List<QuizQuestion> questions = gson.fromJson(json, listType);

            boolean success = QuizService.saveQuestions(lessonId, questions);
            resp.getWriter().write("{\"success\": " + success + "}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
