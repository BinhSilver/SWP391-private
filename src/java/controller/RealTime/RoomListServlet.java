package controller.RealTime;

import Dao.RoomDAO;
import model.Room;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/roomList")
public class RoomListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authUser") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginJSP/LoginIndex.jsp");
            return;
        }

        User authUser = (User) session.getAttribute("authUser");
        try {
            RoomDAO dao = new RoomDAO();
            String action = request.getParameter("action");

            if ("findMatch".equals(action)) {
                int age = authUser.getAge();
                String gender = authUser.getGender() != null ? authUser.getGender() : "Không xác định";
                String japaneseLevel = authUser.getJapaneseLevel() != null ? authUser.getJapaneseLevel() : "";
                List<Room> matches = dao.findMatchingRooms(gender, age, japaneseLevel);
                if (!matches.isEmpty()) {
                    response.sendRedirect("video.jsp?roomId=" + matches.get(0).getRoomID());
                    return;
                } else {
                    request.setAttribute("error", "Không tìm thấy phòng phù hợp.");
                }
            }

            List<Room> rooms = dao.getAvailableRooms();
            // Kiểm tra sự phù hợp của người dùng với từng phòng
            Map<Integer, Boolean> suitabilityMap = new HashMap<>();
            if (rooms != null) {
                for (Room room : rooms) {
                    boolean isSuitable = isSuitable(authUser, room);
                    suitabilityMap.put(room.getRoomID(), isSuitable);
                }
            }

            // Đặt dữ liệu vào request
            request.setAttribute("rooms", rooms);
            request.setAttribute("error", request.getParameter("error"));
            request.setAttribute("message", request.getParameter("message"));
            request.setAttribute("suitabilityMap", suitabilityMap);
            request.setAttribute("authUser", authUser);

            // Forward tới JSP
            request.getRequestDispatcher("/VideoCall/room_list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp?message=Failed to load room list");
        }
    }

    // Hàm kiểm tra sự phù hợp
    private boolean isSuitable(User user, Room room) {
        // 1. Kiểm tra trình độ tiếng Nhật
        String userLevel = user.getJapaneseLevel();
        String roomLevel = room.getLanguageLevel();
        if (roomLevel == null || roomLevel.equals("Không xác định")) {
            // Phòng không xác định phù hợp với mọi trình độ
        } else if (userLevel == null || userLevel.equals("Không xác định")) {
            return false; // Người dùng không xác định không phù hợp với phòng có trình độ cụ thể
        } else {
            // Xác định thứ tự trình độ: N5 < N4 < N3 < N2 < N1
            int[] levels = {5, 4, 3, 2, 1}; // N5=5, N4=4, ..., N1=1
            int userRank = -1, roomRank = -1;
            for (int i = 0; i < levels.length; i++) {
                if (userLevel.equals("N" + levels[i])) userRank = i;
                if (roomLevel.equals("N" + levels[i])) roomRank = i;
            }
            if (userRank == -1 || roomRank == -1 || userRank > roomRank) {
                return false; // Người dùng không đủ trình độ
            }
        }

        // 2. Kiểm tra giới tính
        String userGender = user.getGender();
        String roomGenderPreference = room.getGenderPreference();
        if (roomGenderPreference != null && !roomGenderPreference.equals("Không xác định") &&
            (userGender == null || !userGender.equals(roomGenderPreference))) {
            return false; // Giới tính không khớp
        }

        // 3. Kiểm tra độ tuổi
        int userAge = user.getAge();
        if (userAge == -1) {
            return false; // Không có thông tin tuổi
        }
        if (userAge < room.getMinAge() || userAge > room.getMaxAge()) {
            return false; // Độ tuổi không nằm trong khoảng
        }

        return true; // Phù hợp với tất cả tiêu chí
    }
}