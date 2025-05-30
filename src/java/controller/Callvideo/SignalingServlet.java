package controller.Callvideo;

import dao.RoomDAO;
import dao.RoomParticipantDAO;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import model.RoomParticipant;

@ServerEndpoint("/signaling/{roomId}/{userId}")
public class SignalingServlet {

    private static final Map<Integer, Set<Session>> roomSessions = new ConcurrentHashMap<>();
    private static final Map<Session, Integer> sessionToRoom = new ConcurrentHashMap<>();
    private static final Map<Session, Integer> sessionToUser = new ConcurrentHashMap<>();

    RoomParticipantDAO participantDAO = new RoomParticipantDAO();
    RoomDAO roomDAO = new RoomDAO();

    @OnOpen
    public void onOpen(Session session,
            @PathParam("roomId") int roomId,
            @PathParam("userId") int userId) {

        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionToRoom.put(session, roomId);
        sessionToUser.put(session, userId);

        RoomParticipant participant = new RoomParticipant(roomId, userId, new Date());
        participantDAO.addParticipant(participant);

        broadcast(roomId, userId + " đã tham gia phòng.");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        int roomId = sessionToRoom.get(session);

        // Broadcast cho các client còn lại trong phòng
        for (Session s : roomSessions.get(roomId)) {
            if (!s.equals(session)) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        int roomId = sessionToRoom.getOrDefault(session, -1);
        int userId = sessionToUser.getOrDefault(session, -1);

        if (roomId != -1) {
            roomSessions.getOrDefault(roomId, new HashSet<>()).remove(session);
            if (roomSessions.get(roomId).isEmpty()) {
                roomSessions.remove(roomId);
            }
        }

        sessionToRoom.remove(session);
        sessionToUser.remove(session);

        participantDAO.removeParticipant(roomId, userId);
        roomDAO.deleteRoomIfEmpty(roomId);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    private void broadcast(int roomId, String message) {
        Set<Session> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            for (Session s : sessions) {
                try {
                    s.getBasicRemote().sendText("{\"systemMessage\": \"" + message + "\"}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
