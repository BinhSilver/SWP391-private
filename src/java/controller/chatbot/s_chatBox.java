package controller.chatbot;

import com.google.gson.*;
import Dao.CoursesDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Course;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Properties;

@WebServlet(name = "s_chatBox", urlPatterns = {"/aiGe"})
public class s_chatBox extends HttpServlet {
private static String API_URL;
    private static String API_KEY;
    private static final CoursesDAO coursesDAO = new CoursesDAO();

 @Override
    public void init() throws ServletException {
        // Đọc file config.properties từ thư mục controller/chatbot
        try (InputStream input = getClass().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new ServletException("Không tìm thấy file config.properties trong thư mục controller/chatbot");
            }
            prop.load(input);
            API_URL = prop.getProperty("openrouter.api.url");
            API_KEY = prop.getProperty("openrouter.api.key");
        } catch (IOException ex) {
            throw new ServletException("Lỗi khi đọc file config.properties", ex);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        BufferedReader reader = request.getReader();
        StringBuilder requestData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestData.append(line);
        }

        JsonObject requestJson = JsonParser.parseString(requestData.toString()).getAsJsonObject();
        String userMessage = requestJson.has("message") ? requestJson.get("message").getAsString() : "";

        if (userMessage.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Message cannot be empty\"}");
            return;
        }

        // Lấy danh sách khóa học từ DB
        List<Course> allCourses;
        try {
            allCourses = coursesDAO.getAllCourses();
        } catch (Exception e) {
            response.getWriter().write("{\"response\": \"Lỗi khi truy xuất dữ liệu khóa học.\"}");
            return;
        }

        // Tạo prompt
        StringBuilder prompt = new StringBuilder();
        prompt.append("Bạn là trợ lý hỗ trợ học tiếng Nhật và các khóa học hiện có. Chỉ trả lời nếu liên quan đến khóa học hoặc tiếng Nhật.\n\n");

        for (Course course : allCourses) {
            prompt.append("- ").append(course.getTitle()).append(": ").append(course.getDescription()).append("\n");
        }

        prompt.append("\nCâu hỏi của người dùng: ").append(userMessage);

        // Gửi đến OpenRouter
        JsonObject payload = new JsonObject();
        payload.addProperty("model", "openai/gpt-3.5-turbo"); // bạn có thể đổi sang model khác

        JsonArray messages = new JsonArray();
        JsonObject msgObj = new JsonObject();
        msgObj.addProperty("role", "user");
        msgObj.addProperty("content", prompt.toString());
        messages.add(msgObj);
        payload.add("messages", messages);

        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.toString().getBytes("utf-8"));
        }

        InputStream is = (conn.getResponseCode() < 400) ? conn.getInputStream() : conn.getErrorStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
        StringBuilder responseStr = new StringBuilder();
        while ((line = br.readLine()) != null) {
            responseStr.append(line);
        }

        String aiResponseText = "Vui lòng hỏi liên quan đến khóa học hoặc tiếng Nhật.";
        try {
            JsonObject json = JsonParser.parseString(responseStr.toString()).getAsJsonObject();
            JsonArray choices = json.getAsJsonArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JsonObject msg = choices.get(0).getAsJsonObject().getAsJsonObject("message");
                aiResponseText = msg.get("content").getAsString();
            }
        } catch (Exception e) {
            aiResponseText = "Lỗi xử lý phản hồi từ AI.";
        }

        JsonObject clientResponse = new JsonObject();
        clientResponse.addProperty("response", aiResponseText);
        response.setContentType("application/json");
        response.getWriter().write(clientResponse.toString());
    }
}
