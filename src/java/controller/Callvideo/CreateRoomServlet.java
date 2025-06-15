package controller.Callvideo;

import Dao.RoomDAO; 
import model.Room;
import model.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/createRoom")
public class CreateRoomServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");

        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            String genderPreference = request.getParameter("genderPreference");
            String minAgeStr = request.getParameter("minAge");
            String maxAgeStr = request.getParameter("maxAge");
            String languageLevel = request.getParameter("languageLevel");
            String approval = request.getParameter("allowApproval");

            // Kiểm tra đầu vào
            if (genderPreference == null || minAgeStr == null || maxAgeStr == null || languageLevel == null) {
                response.sendRedirect("create.jsp?error=missing_parameters");
                return;
            }

            int minAge = Integer.parseInt(minAgeStr);
            int maxAge = Integer.parseInt(maxAgeStr);
            boolean allowApproval = "true".equalsIgnoreCase(approval);

            if (minAge < 0 || maxAge < 0 || minAge > maxAge) {
                response.sendRedirect("create.jsp?error=invalid_age_range");
                return;
            }

            if (languageLevel.trim().isEmpty()) {
                response.sendRedirect("create.jsp?error=invalid_language_level");
                return;
            }

            // Tạo Room object
            Room room = new Room();
            room.setHostUserID(user.getUserID());
            room.setLanguageLevel(languageLevel);
            room.setGenderPreference(genderPreference);
            room.setMinAge(minAge);
            room.setMaxAge(maxAge);
            room.setAllowApproval(allowApproval);
            room.setIsActive(true);

            // Tạo phòng và nhận roomId
            RoomDAO dao = new RoomDAO();
            int roomId = dao.createRoom(room);

            if (roomId == -1) {
                response.sendRedirect("create.jsp?error=create_failed");
                return;
            }

            // Chuyển hướng sang trang video call
            response.sendRedirect("video.jsp?roomId=" + roomId);
        } catch (NumberFormatException e) {
            response.sendRedirect("create.jsp?error=invalid_age_format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("create.jsp?error=server_error");
        }
    }
}