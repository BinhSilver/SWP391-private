/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Callvideo;

import dao.RoomDAO;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.Date;
import java.time.LocalDate;

import java.time.Period;

import java.util.List;
import model.Room;
import model.User;
@WebServlet("/room")
public class RoomServlet extends HttpServlet {
    RoomDAO dao = new RoomDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Tạo phòng mới
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        Room room = new Room();
        room.setHostUserId(user.getUserID());
        room.setLanguageLevel(request.getParameter("languageLevel"));
        room.setGenderPreference(request.getParameter("genderPreference"));
        room.setMinAge(Integer.parseInt(request.getParameter("minAge")));
        room.setMaxAge(Integer.parseInt(request.getParameter("maxAge")));

        int roomId = dao.createRoom(room);
        response.sendRedirect("VideoCall/roomcall.jsp?roomId=" + roomId);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // Ghép nhanh
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        int age = calculateAge((Date) user.getBirthDate());
        List<Room> matches = dao.findMatchingRooms(user.getGender(), age, user.getJapaneseLevel());

        if (!matches.isEmpty()) {
            response.sendRedirect("VideoCall/roomcall.jsp?roomId=" + matches.get(0).getRoomId());
        } else {
            response.sendRedirect("no-room-found.jsp");
        }
    }

    private int calculateAge(Date birthDate) {
        return Period.between(birthDate.toLocalDate(), LocalDate.now()).getYears();
    }
}
