package controller.RealTime;
import Dao.UserDAO;
import Dao.ConversationDAO;
import Dao.MessageDAO;
import Dao.BlockDAO;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.Conversation;
import model.User;

@ServerEndpoint(value = "/chat", configurator = HttpSessionConfigurator.class)
public class ChatWebSocket {
    private static final Map<Integer, Session> sessions = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws Exception {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
        User user = (User) httpSession.getAttribute("authUser");
        if (user != null) {
            sessions.put(user.getUserID(), session);
            System.out.println("User connected: " + user.getFullName());

            try {
                List<Integer> unreadSenders = new MessageDAO().getUsersWithUnreadMessages(user.getUserID());
                Map<String, Object> unreadNotice = new HashMap<>();
                unreadNotice.put("type", "unread_list");
                unreadNotice.put("senders", unreadSenders);
                session.getAsyncRemote().sendText(gson.toJson(unreadNotice));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized"));
        }
    }

    @OnMessage
    public void onMessage(String messageJson, Session session) {
        try {
            Map<String, Object> json = gson.fromJson(messageJson, Map.class);
            String type = (String) json.getOrDefault("type", "message");

            Integer fromUserId = null;
            String fromUsername = null;
            for (Map.Entry<Integer, Session> entry : sessions.entrySet()) {
                if (entry.getValue().equals(session)) {
                    fromUserId = entry.getKey();
                    fromUsername = new UserDAO().getUserById(fromUserId).getFullName();
                    break;
                }
            }
            if (fromUserId == null) return;

            switch (type) {
                case "message":
                    int toUserId = ((Double) json.get("toUserId")).intValue(); // Chuyển từ Double sang int
                    String content = (String) json.get("content");

                    if (new BlockDAO().isBlocked(fromUserId, toUserId)) {
                        sendBlockNotice(session);
                        return;
                    }

                    ConversationDAO convDAO = new ConversationDAO();
                    Conversation conv = convDAO.getConversation(fromUserId, toUserId);
                    if (conv == null) {
                        conv = new Conversation();
                        conv.setConversationId(convDAO.createConversation(fromUserId, toUserId));
                    }
                    int messageId = new MessageDAO().saveMessage(conv.getConversationId(), fromUserId, content, "text");
                    String timestamp = LocalDateTime.now().toString();

                    Map<String, Object> sendMessage = new HashMap<>();
                    sendMessage.put("type", "message");
                    sendMessage.put("fromUserId", fromUserId);
                    sendMessage.put("fromUsername", fromUsername);
                    sendMessage.put("toUserId", toUserId);
                    sendMessage.put("content", content);
                    sendMessage.put("messageId", messageId);
                    sendMessage.put("conversationId", conv.getConversationId());
                    sendMessage.put("sentAt", timestamp);

                    sendToBoth(fromUserId, toUserId, sendMessage);
                    break;

                case "recall":
                    int messageIdRecall = ((Double) json.get("messageId")).intValue();
                    int toUserIdRecall = ((Double) json.get("toUserId")).intValue();
                    new MessageDAO().recallMessage(messageIdRecall, fromUserId);

                    Map<String, Object> recallNotify = new HashMap<>();
                    recallNotify.put("type", "recall");
                    recallNotify.put("messageId", messageIdRecall);
                    recallNotify.put("fromUserId", fromUserId);
                    recallNotify.put("toUserId", toUserIdRecall);
                    recallNotify.put("sentAt", LocalDateTime.now().toString());

                    sendToBoth(fromUserId, toUserIdRecall, recallNotify);
                    break;

                case "block":
                    int blockedId = ((Double) json.get("blockedId")).intValue();
                    new BlockDAO().blockUser(fromUserId, blockedId);

                    Map<String, Object> notifyBlocker = new HashMap<>();
                    notifyBlocker.put("type", "block_status");
                    notifyBlocker.put("blockedId", blockedId);
                    notifyBlocker.put("status", "blocked_by_me");

                    Map<String, Object> notifyBlocked = new HashMap<>();
                    notifyBlocked.put("type", "block_status");
                    notifyBlocked.put("blockerId", fromUserId);
                    notifyBlocked.put("status", "blocked_me");

                    session.getAsyncRemote().sendText(gson.toJson(notifyBlocker));
                    sendTo(blockedId, notifyBlocked);
                    break;

                case "unblock":
                    int unblockedId = ((Double) json.get("blockedId")).intValue();
                    new BlockDAO().unblockUser(fromUserId, unblockedId);

                    Map<String, Object> notifyUnblocker = new HashMap<>();
                    notifyUnblocker.put("type", "block_status");
                    notifyUnblocker.put("blockedId", unblockedId);
                    notifyUnblocker.put("status", "unblocked_by_me");

                    Map<String, Object> notifyUnblocked = new HashMap<>();
                    notifyUnblocked.put("type", "block_status");
                    notifyUnblocked.put("blockerId", fromUserId);
                    notifyUnblocked.put("status", "unblocked_me");

                    session.getAsyncRemote().sendText(gson.toJson(notifyUnblocker));
                    sendTo(unblockedId, notifyUnblocked);
                    break;

                case "read":
                    int senderId = ((Double) json.get("fromUserId")).intValue();
                    new MessageDAO().markMessagesAsRead(senderId, fromUserId);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.values().remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void sendBlockNotice(Session session) {
        Map<String, Object> blockNotify = new HashMap<>();
        blockNotify.put("type", "block");
        blockNotify.put("message", "Không thể gửi tin nhắn do bị chặn.");
        session.getAsyncRemote().sendText(gson.toJson(blockNotify));
    }

    private void sendToBoth(int userA, int userB, Map<String, Object> message) {
        sendTo(userA, message);
        sendTo(userB, message);
    }

    private void sendTo(int userId, Map<String, Object> message) {
        Session toSession = sessions.get(userId);
        if (toSession != null && toSession.isOpen()) {
            toSession.getAsyncRemote().sendText(gson.toJson(message));
        }
    }
}