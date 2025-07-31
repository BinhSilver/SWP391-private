package controller.RealTime;

import Dao.ConversationDAO;
import Dao.MessageDAO;
import Dao.BlockDAO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import model.Conversation;
import model.User;

@WebServlet(name = "SendMessageServlet", urlPatterns = {"/sendMessage"})
public class SendMessageServlet extends HttpServlet {
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            // Check if user is authenticated
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("authUser") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Unauthorized\"}");
                return;
            }
            
            User currentUser = (User) session.getAttribute("authUser");
            
            // Read JSON from request body
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            
            Map<String, Object> messageData = gson.fromJson(sb.toString(), Map.class);
            
            // Extract message data
            int fromUserId = currentUser.getUserID();
            int toUserId = ((Double) messageData.get("toUserId")).intValue();
            String content = (String) messageData.get("content");
            
            // Check if user is blocked
            if (new BlockDAO().isBlocked(fromUserId, toUserId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\": \"User is blocked\"}");
                return;
            }
            
            // Save message to database
            ConversationDAO convDAO = new ConversationDAO();
            Conversation conv = convDAO.getConversation(fromUserId, toUserId);
            if (conv == null) {
                conv = new Conversation();
                conv.setConversationId(convDAO.createConversation(fromUserId, toUserId));
            }
            
            int messageId = new MessageDAO().saveMessage(conv.getConversationId(), fromUserId, content, "text");
            
            // Return success response
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("messageId", messageId);
            result.put("conversationId", conv.getConversationId());
            result.put("sentAt", LocalDateTime.now().toString());
            
            response.getWriter().write(gson.toJson(result));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Internal server error\"}");
        }
    }
} 