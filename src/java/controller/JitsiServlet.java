package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class JitsiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String room = request.getParameter("room");
        if (room == null || room.trim().isEmpty()) {
            room = "PhongMacDinh";
        }

        // Truyền roomName sang JSP
        request.setAttribute("roomName", room);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jitsi.jsp");
        dispatcher.forward(request, response);
    }
}
