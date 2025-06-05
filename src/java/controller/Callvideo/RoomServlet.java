package controller.Callvideo;

import dao.RoomDAO;
import model.Room;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@WebServlet("/room")
public class RoomServlet extends HttpServlet {
    RoomDAO dao = new RoomDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        Room room = new Room();
        room.setHostUserID(user.getUserID());
        room.setLanguageLevel(request.getParameter("languageLevel"));
        room.setGenderPreference(request.getParameter("genderPreference"));
        room.setMinAge(Integer.parseInt(request.getParameter("minAge")));
        room.setMaxAge(Integer.parseInt(request.getParameter("maxAge")));
        room.setAllowApproval(Boolean.parseBoolean(request.getParameter("allowApproval")));

        int roomId = dao.createRoom(room);
        response.sendRedirect("roomcall.jsp?roomId=" + roomId);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        int age = calculateAge((Date) user.getBirthDate());
        List<Room> matches = dao.findMatchingRooms(user.getGender(), age, user.getJapaneseLevel());

        if (!matches.isEmpty()) {
            response.sendRedirect("roomcall.jsp?roomId=" + matches.get(0).getRoomID());
        } else {
            response.sendRedirect("no-room-found.jsp");
        }
    }

    private int calculateAge(Date birthDate) {
        return Period.between(birthDate.toLocalDate(), LocalDate.now()).getYears();
    }
}
