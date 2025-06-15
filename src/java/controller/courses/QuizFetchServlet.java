package controller.courses;

import com.google.gson.Gson;
import service.QuizService;
import model.QuizQuestion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "QuizFetchServlet", urlPatterns = {"/getQuiz"})
public class QuizFetchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();

        try {
            int lessonId = Integer.parseInt(req.getParameter("lessonId"));

            // Gọi qua service đã có logic gắn thêm Answer vào QuizQuestion
            List<QuizQuestion> questions = QuizService.loadQuestions(lessonId);

            String json = gson.toJson(questions);
            resp.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
