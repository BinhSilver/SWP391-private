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
        System.out.println("=== WebSocket Connection Attempt ===");
        System.out.println("Session ID: " + session.getId());
        System.out.println("Remote Address: " + session.getRequestURI());
        System.out.println("User Properties: " + config.getUserProperties());
        
        try {
            HttpSession httpSession = (HttpSession) config.getUserProperties().get("httpSession");
            if (httpSession == null) {
                System.err.println("❌ WebSocket: No HTTP session found");
                System.err.println("Available properties: " + config.getUserProperties().keySet());
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "No HTTP session"));
                return;
            }
            
            System.out.println("✅ HTTP Session found: " + httpSession.getId());
            System.out.println("Session attributes: " + httpSession.getAttributeNames());
            
            User user = (User) httpSession.getAttribute("authUser");
            if (user != null) {
                sessions.put(user.getUserID(), session);
                System.out.println("✅ WebSocket: User connected successfully");
                System.out.println("   - User ID: " + user.getUserID());
                System.out.println("   - Username: " + user.getFullName());
                System.out.println("   - Active sessions: " + sessions.size());
                System.out.println("   - Connected users: " + sessions.keySet());

                try {
                    List<Integer> unreadSenders = new MessageDAO().getUsersWithUnreadMessages(user.getUserID());
                    Map<String, Object> unreadNotice = new HashMap<>();
                    unreadNotice.put("type", "unread_list");
                    unreadNotice.put("senders", unreadSenders);
                    session.getAsyncRemote().sendText(gson.toJson(unreadNotice));
                    System.out.println("✅ Sent unread messages list to user " + user.getUserID());
                } catch (Exception e) {
                    System.err.println("❌ WebSocket: Error loading unread messages for user " + user.getUserID() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("❌ WebSocket: Unauthorized connection attempt - no authUser in session");
                System.err.println("Available session attributes: " + httpSession.getAttributeNames());
                // Instead of closing immediately, allow connection but mark as unauthorized
                System.out.println("⚠️ WebSocket: Allowing connection but user is not authenticated");
                // Store session with null user for potential later authentication
                sessions.put(-1, session); // Use -1 as temporary user ID
            }
        } catch (Exception e) {
            System.err.println("❌ WebSocket: Error in onOpen: " + e.getMessage());
            e.printStackTrace();
            session.close(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, "Internal server error"));
        }
    }

    @OnMessage
    public void onMessage(String messageJson, Session session) {
        System.out.println("📨 WebSocket: Received message from session " + session.getId());
        System.out.println("Message: " + messageJson);
        
        try {
            Map<String, Object> json = gson.fromJson(messageJson, Map.class);
            String type = (String) json.getOrDefault("type", "message");

            Integer fromUserId = null;
            String fromUsername = null;
            for (Map.Entry<Integer, Session> entry : sessions.entrySet()) {
                if (entry.getValue().equals(session)) {
                    fromUserId = entry.getKey();
                    if (fromUserId != -1) { // Only get username for authenticated users
                        try {
                            User user = new UserDAO().getUserById(fromUserId);
                            fromUsername = user != null ? user.getFullName() : "Unknown";
                        } catch (Exception e) {
                            System.err.println("❌ Error getting user info for ID " + fromUserId + ": " + e.getMessage());
                            fromUsername = "Unknown";
                        }
                    }
                    break;
                }
            }
            
            if (fromUserId == null) {
                System.err.println("❌ WebSocket: Could not find user for session " + session.getId());
                System.out.println("Available sessions: " + sessions.keySet());
                return;
            }
            
            // Handle unauthenticated users
            if (fromUserId == -1) {
                System.out.println("⚠️ WebSocket: Message from unauthenticated session " + session.getId());
                if ("register".equals(type)) {
                    // Handle registration for unauthenticated users
                    try {
                        int registerUserId = ((Double) json.get("userId")).intValue();
                        System.out.println("✅ User registration attempt: " + registerUserId);
                        // You might want to validate the user ID here
                        // For now, just log it
                    } catch (Exception e) {
                        System.err.println("❌ Error processing registration: " + e.getMessage());
                    }
                } else {
                    System.out.println("⚠️ Ignoring message type '" + type + "' from unauthenticated user");
                }
                return;
            }
            
            System.out.println("✅ Processing message from user: " + fromUsername + " (ID: " + fromUserId + ")");

            switch (type) {
                case "register":
                    // Handle user registration
                    int registerUserId = ((Double) json.get("userId")).intValue();
                    System.out.println("✅ User registration: " + registerUserId);
                    System.out.println("Current connected users: " + sessions.keySet());
                    // User is already registered in onOpen, so just log it
                    break;
                    
                case "ping":
                    // Handle ping to keep connection alive
                    System.out.println("🏓 Ping received from session " + session.getId() + " (User: " + fromUserId + ")");
                    // Send pong response
                    Map<String, Object> pongResponse = new HashMap<>();
                    pongResponse.put("type", "pong");
                    pongResponse.put("timestamp", LocalDateTime.now().toString());
                    session.getAsyncRemote().sendText(gson.toJson(pongResponse));
                    break;

                case "message":
                    int toUserId = ((Double) json.get("toUserId")).intValue();
                    String content = (String) json.get("content");

                    System.out.println("📝 Processing message:");
                    System.out.println("   - From: " + fromUserId + " (" + fromUsername + ")");
                    System.out.println("   - To: " + toUserId);
                    System.out.println("   - Content: " + content);
                    System.out.println("   - Current connected users: " + sessions.keySet());

                    // Check if recipient is online
                    if (!sessions.containsKey(toUserId)) {
                        System.out.println("⚠️ Người nhận [" + toUserId + "] chưa kết nối WebSocket");
                        System.out.println("   - Available users: " + sessions.keySet());
                        // Still save message to database for when user comes online
                    } else {
                        Session recipientSession = sessions.get(toUserId);
                        if (recipientSession == null || !recipientSession.isOpen()) {
                            System.out.println("⚠️ Người nhận [" + toUserId + "] có session nhưng không mở");
                            sessions.remove(toUserId); // Clean up invalid session
                        } else {
                            System.out.println("✅ Người nhận [" + toUserId + "] đang online và sẵn sàng nhận tin nhắn");
                        }
                    }

                    if (new BlockDAO().isBlocked(fromUserId, toUserId)) {
                        System.out.println("🚫 User " + fromUserId + " is blocked by " + toUserId);
                        sendBlockNotice(session);
                        return;
                    }

                    ConversationDAO convDAO = new ConversationDAO();
                    Conversation conv = convDAO.getConversation(fromUserId, toUserId);
                    if (conv == null) {
                        conv = new Conversation();
                        conv.setConversationId(convDAO.createConversation(fromUserId, toUserId));
                        System.out.println("✅ Created new conversation: " + conv.getConversationId());
                    } else {
                        System.out.println("✅ Using existing conversation: " + conv.getConversationId());
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

                    boolean sentToBoth = sendToBoth(fromUserId, toUserId, sendMessage);
                    if (sentToBoth) {
                        System.out.println("✅ Message sent successfully to both users");
                    } else {
                        System.out.println("⚠️ Message saved to database but not all recipients received it");
                    }
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

                    boolean recallSent = sendToBoth(fromUserId, toUserIdRecall, recallNotify);
                    if (recallSent) {
                        System.out.println("✅ Message recalled successfully");
                    } else {
                        System.out.println("⚠️ Recall notification not sent to all recipients");
                    }
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
                    System.out.println("✅ User " + fromUserId + " blocked user " + blockedId);
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
                    System.out.println("✅ User " + fromUserId + " unblocked user " + unblockedId);
                    break;

                case "read":
                    int senderId = ((Double) json.get("fromUserId")).intValue();
                    new MessageDAO().markMessagesAsRead(senderId, fromUserId);
                    System.out.println("✅ Messages marked as read from " + senderId + " to " + fromUserId);
                    break;

                case "callInvite":
                    int toUserIdCall = ((Double) json.get("toUserId")).intValue();
                    String roomUrl = (String) json.get("roomUrl");
                    String token = (String) json.get("token");
                    Map<String, Object> inviteData = new HashMap<>();
                    inviteData.put("type", "callInvite");
                    inviteData.put("fromUserId", fromUserId);
                    inviteData.put("fromUsername", fromUsername);
                    inviteData.put("toUserId", toUserIdCall);
                    inviteData.put("roomUrl", roomUrl);
                    inviteData.put("token", token);
                    boolean inviteSent = sendTo(toUserIdCall, inviteData);
                    if (inviteSent) {
                        System.out.println("✅ Call invite sent from " + fromUserId + " to " + toUserIdCall);
                    } else {
                        System.out.println("⚠️ Call invite not sent - recipient " + toUserIdCall + " not online");
                    }
                    break;

                case "callRejected":
                    int toUserIdReject = ((Double) json.get("toUserId")).intValue();
                    Map<String, Object> rejectData = new HashMap<>();
                    rejectData.put("type", "callRejected");
                    rejectData.put("fromUserId", fromUserId);
                    boolean rejectSent = sendTo(toUserIdReject, rejectData);
                    if (rejectSent) {
                        System.out.println("✅ Call rejection sent from " + fromUserId + " to " + toUserIdReject);
                    } else {
                        System.out.println("⚠️ Call rejection not sent - recipient " + toUserIdReject + " not online");
                    }
                    break;
                    
                default:
                    System.out.println("⚠️ Unknown message type: " + type);
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ WebSocket: Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("🔌 WebSocket: Session closed - " + session.getId());
        
        // Find and remove user from sessions
        Integer userIdToRemove = null;
        for (Map.Entry<Integer, Session> entry : sessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                userIdToRemove = entry.getKey();
                break;
            }
        }
        
        if (userIdToRemove != null) {
            sessions.remove(userIdToRemove);
            if (userIdToRemove == -1) {
                System.out.println("✅ Removed unauthenticated session from active sessions");
            } else {
                System.out.println("✅ Removed user " + userIdToRemove + " from active sessions");
            }
            System.out.println("   - Remaining sessions: " + sessions.size());
            System.out.println("   - Remaining users: " + sessions.keySet());
        } else {
            System.out.println("⚠️ Could not find user for session " + session.getId());
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("❌ WebSocket: Error in session " + session.getId());
        System.err.println("Error: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    private void sendBlockNotice(Session session) {
        Map<String, Object> blockNotify = new HashMap<>();
        blockNotify.put("type", "block");
        blockNotify.put("message", "Không thể gửi tin nhắn do bị chặn.");
        session.getAsyncRemote().sendText(gson.toJson(blockNotify));
        System.out.println("🚫 Sent block notice to session " + session.getId());
    }

    private boolean sendToBoth(int userA, int userB, Map<String, Object> message) {
        boolean sentToA = sendTo(userA, message);
        boolean sentToB = sendTo(userB, message);
        return sentToA && sentToB;
    }

    private boolean sendTo(int userId, Map<String, Object> message) {
        Session toSession = sessions.get(userId);
        if (toSession != null && toSession.isOpen()) {
            try {
                toSession.getAsyncRemote().sendText(gson.toJson(message));
                System.out.println("✅ Message sent to user " + userId);
                return true;
            } catch (Exception e) {
                System.err.println("❌ Error sending message to user " + userId + ": " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("⚠️ User " + userId + " is not online or session is closed");
            System.out.println("   - Session exists: " + (toSession != null));
            System.out.println("   - Session open: " + (toSession != null && toSession.isOpen()));
            return false;
        }
    }
    
    // Method to get WebSocket statistics
    public static Map<String, Object> getWebSocketStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", sessions.size());
        stats.put("authenticatedUsers", sessions.keySet().stream().filter(id -> id != -1).count());
        stats.put("unauthenticatedSessions", sessions.keySet().stream().filter(id -> id == -1).count());
        stats.put("activeSessionIds", sessions.keySet());
        return stats;
    }
    
    // Method to check if a user is online
    public static boolean isUserOnline(int userId) {
        Session session = sessions.get(userId);
        return session != null && session.isOpen();
    }
}