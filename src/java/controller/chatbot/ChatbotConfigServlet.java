package controller.chatbot;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ChatbotConfig;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ChatbotConfigServlet", urlPatterns = {"/chatbot-config"})
public class ChatbotConfigServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        // Tạo JSON response với cấu hình chatbot
        String jsonConfig = String.format(
            "{\n" +
            "  \"apiUrl\": \"%s\",\n" +
            "  \"apiKey\": \"%s\",\n" +
            "  \"botId\": \"%s\",\n" +
            "  \"authToken\": \"%s\",\n" +
            "  \"modelName\": \"%s\"\n" +
            "}",
            ChatbotConfig.getApiUrl(),
            ChatbotConfig.getApiKey(),
            ChatbotConfig.getBotId(),
            ChatbotConfig.getAuthToken(),
            ChatbotConfig.getModelName()
        );
        
        out.print(jsonConfig);
        out.flush();
    }
}
