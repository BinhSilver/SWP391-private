<%@ page import="java.util.*, model.User" %>
<%
    int roomId = Integer.parseInt(request.getParameter("roomId"));
    User user = (User) session.getAttribute("authUser");
%>
<html>
<head>
    <title>RoomCall - WebRTC</title>
    <script src="https://webrtc.github.io/adapter/adapter-latest.js"></script>
    <script src="Webrtc/webrtc.js"></script>
</head>
<body>
    <h2>?ang ? trong phòng: <%= roomId %></h2>
    <video id="localVideo" autoplay muted></video>
    <video id="remoteVideo" autoplay></video>

    <script>
        const roomId = "<%= roomId %>";
        const userId = "<%= user.getUserID() %>";
        startWebRTC(roomId, userId);
    </script>
</body>
</html>
