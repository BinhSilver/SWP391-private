package controller.chatbot;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Vocabulary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.InputStream;

@WebServlet(name = "GenerateVocabularyServlet", urlPatterns = {"/generate-vocabulary"})
@MultipartConfig
public class GenerateVocabularyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        System.out.println("[DEBUG] GenerateVocabularyServlet: Received POST request");

        try {
            String inputText = null;
            String lessonIdStr = null;
            Part filePart = null;

            // Kiểm tra loại content
            if (request.getContentType() != null && request.getContentType().toLowerCase().contains("multipart/form-data")) {
                System.out.println("[DEBUG] Processing multipart/form-data");
                inputText = request.getParameter("inputText");
                lessonIdStr = request.getParameter("lessonId");
                filePart = request.getPart("attachs");
                System.out.println("[DEBUG] FormData - inputText: " + inputText + ", lessonId: " + lessonIdStr + ", file: " + (filePart != null ? filePart.getSubmittedFileName() : "None"));
            } else {
                // Xử lý JSON
                BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder jsonBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBody.append(line);
                }
                System.out.println("[DEBUG] Raw JSON Request: " + jsonBody.toString());

                JsonObject jsonObject;
                try {
                    jsonObject = new Gson().fromJson(jsonBody.toString(), JsonObject.class);
                    if (jsonObject == null) {
                        throw new JsonSyntaxException("Empty JSON body");
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println("[ERROR] Bad request: Invalid JSON format - " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    JsonObject errorResponse = new JsonObject();
                    errorResponse.addProperty("error", "Dữ liệu JSON không hợp lệ.");
                    response.getWriter().write(new Gson().toJson(errorResponse));
                    return;
                }

                inputText = jsonObject.has("inputText") ? jsonObject.get("inputText").getAsString() : null;
                lessonIdStr = jsonObject.has("lessonId") ? jsonObject.get("lessonId").getAsString() : null;
                System.out.println("[DEBUG] JSON - inputText: " + inputText + ", lessonId: " + lessonIdStr);
            }

            // Kiểm tra đầu vào
            if ((inputText == null || inputText.trim().isEmpty()) && filePart == null) {
                System.out.println("[ERROR] Input text and file are missing or empty");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("error", "Vui lòng cung cấp văn bản hoặc file.");
                response.getWriter().write(new Gson().toJson(errorResponse));
                return;
            }

            int lessonId;
            try {
                lessonId = lessonIdStr != null && !lessonIdStr.isEmpty() ? Integer.parseInt(lessonIdStr) : 0;
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid lessonId: " + lessonIdStr);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonObject errorResponse = new JsonObject();
                errorResponse.addProperty("error", "Invalid lessonId: " + lessonIdStr);
                response.getWriter().write(new Gson().toJson(errorResponse));
                return;
            }
            System.out.println("[DEBUG] Parsed lessonId: " + lessonId);

            // Xử lý file nếu có
            if (filePart != null) {
                try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(filePart.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder fileText = new StringBuilder();
                    String fileLine;
                    while ((fileLine = fileReader.readLine()) != null) {
                        fileText.append(fileLine).append("\n");
                    }
                    String fileContent = fileText.toString();
                    inputText = (inputText != null && !inputText.trim().isEmpty() ? inputText + "\n" : "") + fileContent;
                }
            }

            // Gọi API AI để tạo vocabulary
            List<Vocabulary> vocabularyList = generateVocabularyFromAI(inputText, lessonId);

            // Trả về JSON
            Gson gson = new Gson();
            JsonObject jsonResponse = new JsonObject();
            JsonArray vocabArray = new JsonArray();
            for (Vocabulary vocab : vocabularyList) {
                JsonObject vocabJson = new JsonObject();
                vocabJson.addProperty("vocabID", vocab.getVocabID());
                vocabJson.addProperty("lessonID", vocab.getLessonID());
                vocabJson.addProperty("word", vocab.getWord());
                vocabJson.addProperty("meaning", vocab.getMeaning());
                vocabJson.addProperty("reading", vocab.getReading());
                vocabJson.addProperty("example", vocab.getExample());
                vocabJson.addProperty("imagePath", vocab.getImagePath());
                vocabArray.add(vocabJson);
            }
            jsonResponse.add("vocabulary", vocabArray);
            System.out.println("[DEBUG] Response JSON: " + gson.toJson(jsonResponse));
            response.getWriter().write(gson.toJson(jsonResponse));

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to process request: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("error", "Lỗi khi tạo vocabulary: " + e.getMessage());
            response.getWriter().write(new Gson().toJson(errorResponse));
        }
    }

    private List<Vocabulary> generateVocabularyFromAI(String query, int lessonId) throws Exception {
        // Cấu hình chatbot
        String apiUrl = "https://ai.ftes.vn/api/ai/rag_agent_template/stream";
        String apiKey = "AIzaSyAH5Su96L-fRZBAzWH46VD5ICXyf9Jpihs";
        String botId = "cc45aba522c01896c55753d3";
        String authToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4NjI5MDQwMjM5M2MzNTg5Y2QwMjEzMSJ9.8Ze_T_XpEWOI3Mi3pS5XgLHXw92YmqDZIsOJtRILvVw";
        String modelName = "gemini-2.5-flash-preview-05-20";
        System.out.println("[DEBUG] API Config - URL: " + apiUrl + ", Key: " + (apiKey != null ? "****" : null) +
                ", BotId: " + botId + ", AuthToken: " + (authToken != null ? "****" : null) + ", Model: " + modelName);

        if (apiUrl == null || apiKey == null || botId == null || authToken == null || modelName == null) {
            System.out.println("[ERROR] Invalid configuration: one or more configuration values are null");
            throw new IllegalArgumentException("Invalid configuration: one or more configuration values are null");
        }

        // Tạo FormData cho yêu cầu API
        String boundary = "----WebKitFormBoundary" + Long.toHexString(System.currentTimeMillis());
        StringBuilder formData = new StringBuilder();
        formData.append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"query\"\r\n\r\n")
                .append(query).append("\r\n")
                .append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"bot_id\"\r\n\r\n")
                .append(botId).append("\r\n")
                .append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"model_name\"\r\n\r\n")
                .append(modelName).append("\r\n")
                .append("--").append(boundary).append("\r\n")
                .append("Content-Disposition: form-data; name=\"api_key\"\r\n\r\n")
                .append(apiKey).append("\r\n")
                .append("--").append(boundary).append("--\r\n");

        // Tạo HttpClient
        HttpClient client = HttpClient.newHttpClient();

        // Tạo yêu cầu HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofString(formData.toString(), StandardCharsets.UTF_8))
                .build();

        // Gửi yêu cầu và xử lý phản hồi streaming
        System.out.println("[DEBUG] Sending request to API: " + apiUrl);
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        System.out.println("[DEBUG] API Response Status: " + response.statusCode());

        if (response.statusCode() != 200) {
            System.out.println("[ERROR] API request failed with status code: " + response.statusCode());
            throw new IOException("API request failed with status code: " + response.statusCode());
        }

        // Xử lý phản hồi streaming
        StringBuilder responseContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                System.out.println("[DEBUG] SSE Line: " + line);

                try {
                    JsonObject json = new Gson().fromJson(line, JsonObject.class);
                    if (json.has("type") && json.get("type").getAsString().equals("final")) {
                        JsonObject content = json.getAsJsonObject("content");
                        if (content.has("final_response")) {
                            responseContent.append(content.get("final_response").getAsString());
                        }
                        break; // Thoát ngay sau khi nhận được final_response
                    }
                } catch (JsonSyntaxException e) {
                    System.out.println("[ERROR] Failed to parse SSE line: " + line + ", Error: " + e.getMessage());
                    continue;
                }
            }
        }

        // Parse phản hồi từ AI
        String finalResponse = responseContent.toString();
        System.out.println("[DEBUG] Final AI Response: " + finalResponse);
        return parseVocabularyFromResponse(finalResponse, lessonId);
    }

    private List<Vocabulary> parseVocabularyFromResponse(String response, int lessonId) {
        List<Vocabulary> vocabularyList = new ArrayList<>();
        Set<String> processedLines = new HashSet<>(); // Để tránh trùng lặp

        // Loại bỏ các ký tự * và ** từ phản hồi
        String cleanedResponse = response.replaceAll("[*]+", "").trim();
        if (cleanedResponse.isEmpty()) {
            System.out.println("[ERROR] Response is empty after cleaning");
            return vocabularyList;
        }

        // Tách phản hồi thành các dòng
        String[] lines = cleanedResponse.split("\n");

        // Regex kiểm tra ký tự hợp lệ cho word và reading (chỉ chấp nhận hiragana, katakana, kanji)
        String japaneseTextRegex = "[\\p{InHiragana}\\p{InKatakana}\\p{InCJKUnifiedIdeographs}]+";
        // Regex kiểm tra meaning (chấp nhận chữ cái, số, khoảng trắng, và ký tự tiếng Việt)
        String meaningRegex = "[\\p{L}\\p{N}\\s]+";

        for (String line : lines) {
            // Làm sạch dòng: thay tab, full-width space thành space chuẩn
            line = line.replaceAll("[\\u3000\\t]+", " ").trim();
            // Bỏ qua dòng trống hoặc đã xử lý
            if (line.trim().isEmpty() || processedLines.contains(line)) {
                continue;
            }
            // Kiểm tra dòng có định dạng từ vựng: Word:Meaning:Reading:Example
            if (line.matches("^[^:]+:[^:]+:[^:]+:[^:]+.*$")) {
                String[] parts = line.split(":", 4);
                if (parts.length >= 4) {
                    // Làm sạch các phần tử
                    String word = parts[0].trim();
                    String meaning = parts[1].trim();
                    String reading = parts[2].trim();
                    String example = parts[3].trim();
                    // Chỉ kiểm tra word không rỗng (nới lỏng)
                    if (word.isEmpty()) {
                        System.out.println("[WARN] Empty word for line: " + line);
                        continue;
                    }
                    if (meaning.isEmpty()) {
                        System.out.println("[WARN] Empty meaning for line: " + line);
                        continue;
                    }
                    if (reading.isEmpty()) {
                        System.out.println("[WARN] Empty reading for line: " + line);
                        continue;
                    }
                    if (example.isEmpty()) {
                        System.out.println("[WARN] Empty example for line: " + line);
                        continue;
                    }
                    // Thêm dòng vào processedLines để tránh trùng lặp
                    processedLines.add(line);
                    Vocabulary vocab = new Vocabulary();
                    vocab.setVocabID(0); // ID sẽ được set sau khi lưu vào database
                    vocab.setLessonID(lessonId);
                    vocab.setWord(word);
                    vocab.setMeaning(meaning);
                    vocab.setReading(reading);
                    vocab.setExample(example);
                    vocab.setImagePath(null); // Không có image từ API
                    vocabularyList.add(vocab);
                    System.out.println("[DEBUG] Parsed Vocabulary: " + vocab.getWord());
                } else {
                    System.out.println("[WARN] Invalid line format (less than 4 parts): " + line);
                }
            } else {
                System.out.println("[WARN] Line does not match vocabulary format: " + line);
            }
        }

        if (vocabularyList.isEmpty()) {
            System.out.println("[ERROR] No valid vocabulary found in response");
        }

        return vocabularyList;
    }
}