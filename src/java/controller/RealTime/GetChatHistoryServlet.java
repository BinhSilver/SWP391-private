/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.RealTime;


import Dao.ConversationDAO;
import Dao.MessageDAO;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Conversation;
import model.Message;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/getChatHistory")
public class GetChatHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        String user1Id = req.getParameter("user1");
        String user2Id = req.getParameter("user2");

        if (user1Id == null || user2Id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số");
            return;
        }

        try {
            Conversation conv = new ConversationDAO().getConversation(Integer.parseInt(user1Id), Integer.parseInt(user2Id));
            if (conv == null) {
                resp.getWriter().write("{\"messages\": []}");
                return;
            }

            List<Message> messages = new MessageDAO().getMessagesByConversationId(conv.getConversationId());
            Map<String, Object> payload = new HashMap<>();
            payload.put("messages", messages);

            try (PrintWriter out = resp.getWriter()) {
                out.print(new Gson().toJson(payload));
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi lấy lịch sử trò chuyện");
        }
    }
}