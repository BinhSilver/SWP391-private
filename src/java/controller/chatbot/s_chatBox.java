package controller.chatbot;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.*;
import jakarta.servlet.annotation.WebServlet;
@WebServlet("/aiGe")
public class s_chatBox extends HttpServlet {

    private static final String API_URL = "https://router.huggingface.co/nscale/v1/chat/completions";
    private static final String API_KEY = "Bearer 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"; // <-- Thay bằng API key của bạn

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   
        BufferedReader reader = request.getReader();
        StringBuilder requestData = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestData.append(line);
        }

        JsonParser parser = new JsonParser();
        JsonObject requestJson = parser.parse(requestData.toString()).getAsJsonObject();
        String userMessage = requestJson.has("message") ? requestJson.get("message").getAsString().trim() : "";

        if (userMessage.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Message cannot be empty\"}");
            return;
        }

        // ================= COMMENT: Phần lấy dữ liệu xe không dùng nữa ====================
        /*
        List<Car> allCars = CarRep.getall();
        StringBuilder carData = new StringBuilder();
        for (Car car : allCars) {
            carData.append("Car Name: ").append(car.getCarName())
                    .append(", Type: ").append(car.getType())
                    .append(", Brand: ").append(car.getBrand())
                    .append(", Price: ").append(car.getPrice())
                    .append(", Year: ").append(car.getYearOfManufacture())
                    .append(", Stock: ").append(car.getStockQuantity())
                    .append("\n");
        }

        if (carData.length() == 0) {
            JsonObject emptyResp = new JsonObject();
            emptyResp.addProperty("response", "Vui lòng hỏi câu liên quan đến danh sách xe.");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(emptyResp.toString());
            return;
        }

        String prompt = "give me the shortest answer. Database Car:\n" + carData.toString() +
                        "\nUser Question: " + userMessage + ". Answer in Vietnamese";
        */

        // ======================= Dùng trực tiếp message làm prompt ========================
        String prompt = userMessage;

        // Tạo JSON payload gửi đến AI
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "meta-llama/Llama-3.1-8B-Instruct");
        requestBody.addProperty("stream", false);

        JsonArray messages = new JsonArray();
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", prompt);
        messages.add(userMsg);
        requestBody.add("messages", messages);

        // Gửi HTTP POST đến Hugging Face
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        InputStream inputStream = (status < 400) ? conn.getInputStream() : conn.getErrorStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder responseStr = new StringBuilder();
        while ((line = br.readLine()) != null) {
            responseStr.append(line.trim());
        }
        br.close();

        System.out.println("Response from HF API: " + responseStr);

        String aiResponseText = "Không thể xử lý phản hồi từ AI.";
        try {
            JsonObject jsonResponse = parser.parse(responseStr.toString()).getAsJsonObject();
            if (jsonResponse.has("choices")) {
                JsonArray choices = jsonResponse.getAsJsonArray("choices");
                if (choices.size() > 0) {
                    JsonObject firstChoice = choices.get(0).getAsJsonObject();
                    JsonObject messageObj = firstChoice.getAsJsonObject("message");
                    if (messageObj != null && messageObj.has("content")) {
                        aiResponseText = messageObj.get("content").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            aiResponseText = "Lỗi xử lý phản hồi từ AI.";
            e.printStackTrace();
        }

        // Gửi phản hồi về client
        JsonObject clientResponse = new JsonObject();
        clientResponse.addProperty("response", aiResponseText.replaceAll("\\n", "<br>"));
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(clientResponse.toString());
    }
}
