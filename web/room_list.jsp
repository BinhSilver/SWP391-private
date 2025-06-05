<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="model.Room"%>
<%@page import="model.User"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Danh sách phòng</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                padding: 20px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 20px;
            }
            th, td {
                padding: 10px;
                border: 1px solid #ddd;
                text-align: left;
            }
            th {
                background-color: #f5f5f5;
            }
            a, button {
                color: #0077cc;
                text-decoration: none;
                cursor: pointer;
            }
            a:hover, button:hover {
                text-decoration: underline;
            }
            .error, .success {
                color: red;
            }
            .success {
                color: green;
            }
            .action-buttons {
                display: flex;
                gap: 10px;
            }
            button:disabled {
                color: #999;
                cursor: not-allowed;
                background-color: #f0f0f0;
            }
            button:disabled:hover {
                text-decoration: none;
            }
            a.disabled {
                color: #999;
                cursor: not-allowed;
                text-decoration: none;
            }
        </style>
        <script src="<%= request.getContextPath()%>/js/room_list.js"></script>
    </head>
    <body>
        <%
            String contextPath = request.getContextPath();
            User authUser = (User) request.getAttribute("authUser");
            List<Room> rooms = (List<Room>) request.getAttribute("rooms");
            String error = (String) request.getAttribute("error");
            String message = (String) request.getAttribute("message");
            Map<Integer, Boolean> suitabilityMap = (Map<Integer, Boolean>) request.getAttribute("suitabilityMap");
        %>
        <h2>Danh sách phòng đang hoạt động</h2>
        <% if (error != null) {%>
        <p class="error">Lỗi: <%= error.equals("request_failed") ? "Gửi yêu cầu tham gia thất bại."
                : error.equals("already_requested") ? "Bạn đã gửi yêu cầu cho phòng này."
                : error.equals("invalid_room") ? "Phòng không tồn tại hoặc không hoạt động."
                : error.equals("invalid_room_id") ? "Mã phòng không hợp lệ."
                : error.equals("not_approved") ? "Bạn cần được chủ phòng chấp nhận trước khi tham gia."
                : error.equals("Không tìm thấy phòng phù hợp.") ? "Không tìm thấy phòng phù hợp."
            : "Lỗi không xác định."%></p>
            <% } %>
            <% if (message != null) {%>
        <p class="success"><%= message%></p>
        <% } %>
        <table>
            <tr>
                <th>Mã phòng</th>
                <th>Chủ phòng</th>
                <th>Trình độ</th>
                <th>Giới tính</th>
                <th>Độ tuổi</th>
                <th>Xét duyệt</th>
                <th>Tham gia</th>
            </tr>
            <%
                if (rooms != null && !rooms.isEmpty()) {
                    for (Room room : rooms) {
                        boolean isSuitable = suitabilityMap != null && suitabilityMap.getOrDefault(room.getRoomID(), false);
            %>
            <tr>
                <td><%= room.getRoomID()%></td>
                <td><%= room.getHostUserID()%></td>
                <td><%= room.getLanguageLevel() != null ? room.getLanguageLevel() : "Không xác định"%></td>
                <td><%= room.getGenderPreference() != null ? room.getGenderPreference() : "Không xác định"%></td>
                <td><%= room.getMinAge()%> - <%= room.getMaxAge()%></td>
                <td><%= room.isAllowApproval() ? "Có" : "Không"%></td>
                <td>
                    <% if (room.isAllowApproval() && room.getHostUserID() != authUser.getUserID()) {%>
                    <form action="<%= contextPath%>/joinRoom" method="post">
                        <input type="hidden" name="roomId" value="<%= room.getRoomID()%>">
                        <button type="submit" <%= isSuitable ? "" : "disabled onclick='showNotSuitableMessage(); return false;'"%>>Yêu cầu tham gia</button>
                    </form>
                    <% } else {%>
                    <a href="<%= contextPath%>/video.jsp?roomId=<%= room.getRoomID()%>" 
                       class="<%= isSuitable ? "" : "disabled"%>"
                       <%= isSuitable ? "" : "onclick='showNotSuitableMessage(); return false;'"%>>Tham gia ngay</a>
                    <% } %>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="7">Không có phòng nào đang hoạt động.</td>
            </tr>
            <% }%>
        </table>
        <div class="action-buttons">
            <a href="<%= contextPath%>/video.jsp">Quay lại</a>
            <a href="<%= contextPath%>/create.jsp">Tạo phòng mới</a>
            <a href="<%= contextPath%>/join_requests.jsp">Xem yêu cầu tham gia</a>
        </div>
    </body>
</html>