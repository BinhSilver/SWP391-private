<%@page import="model.User"%>
<%@page import="dao.RoomDAO"%>
<%@page import="model.Room"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String contextPath = request.getContextPath();
    User authUser = (User) session.getAttribute("authUser");
    if (authUser == null) {
        response.sendRedirect(contextPath + "/LoginJSP/LoginIndex.jsp");
        return;
    }

    String roomId = request.getParameter("roomId");
    Room room = null;
    if (roomId != null && !roomId.isEmpty()) {
        try {
            RoomDAO roomDAO = new RoomDAO();
            room = roomDAO.getRoomById(Integer.parseInt(roomId));
            if (room == null || !room.isActive()) {
                response.sendRedirect(contextPath + "/VideoCall/room_list.jsp?error=invalid_room");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(contextPath + "/VideoCall/room_list.jsp?error=invalid_room_id");
            return;
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Video Call</title>
    <script src='https://meet.jit.si/external_api.js'></script>
    <style>
        html, body {
            margin: 0;
            padding: 0;
            height: 100%;
            width: 100%;
            font-family: Arial, sans-serif;
        }
        #jitsi-container {
            height: 90vh;
            width: 100%;
            display: <%= room != null ? "block" : "none" %>;
        }
        #room-controls {
            padding: 10px;
            background-color: #f5f5f5;
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }
        input[type="text"] {
            padding: 6px;
            font-size: 16px;
        }
        button {
            padding: 8px 16px;
            font-size: 16px;
            cursor: pointer;
        }
        .user-info {
            margin-left: auto;
            font-size: 14px;
            color: #555;
        }
        .room-info {
            font-size: 14px;
            color: #0077cc;
            font-weight: bold;
            margin-left: auto;
        }
    </style>
</head>
<body>
<div id="room-controls">
    <form action="<%= contextPath %>/video.jsp" method="get" style="display: flex; gap: 10px;">
        <input type="text" name="roomId" placeholder="Nhập mã phòng" required />
        <button type="submit">Tham gia phòng</button>
    </form>

    <form action="<%= contextPath %>/create.jsp" method="get">
        <button type="submit">Tạo phòng mới</button>
    </form>

    <form action="<%= contextPath %>/room_list.jsp" method="get">
        <button type="submit">Xem danh sách phòng</button>
    </form>

    <div class="user-info">
        👤 <%= authUser.getFullName() != null ? authUser.getFullName() : "Không xác định" %> | 
        📧 <%= authUser.getEmail() != null ? authUser.getEmail() : "Không xác định" %>
    </div>

    <% if (room != null) { %>
        <div class="room-info">
            📌 Mã phòng: <%= room.getRoomID() %> | Trình độ: <%= room.getLanguageLevel() != null ? room.getLanguageLevel() : "Không xác định" %>
        </div>
    <% } %>
</div>

<% if (room != null) { %>
    <div id="jitsi-container"></div>
    <script>
        const domain = "meet.jit.si";
        const options = {
            roomName: "Room_<%= room.getRoomID() %>",
            width: "100%",
            height: "100%",
            parentNode: document.getElementById("jitsi-container"),
            userInfo: {
                email: "<%= authUser.getEmail() != null ? authUser.getEmail() : "" %>",
                displayName: "<%= authUser.getFullName() != null ? authUser.getFullName() : "Người dùng" %>"
            }
        };
        const api = new JitsiMeetExternalAPI(domain, options);
        api.addEventListener('readyToClose', () => {
            window.location.href = '<%= contextPath %>/room_list.jsp';
        });
    </script>
<% } %>
</body>
</html>