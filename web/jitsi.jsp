<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    String roomName = (String) request.getAttribute("roomName");
    if (roomName == null || roomName.trim().isEmpty()) {
        roomName = "PhongMacDinh";
    }
%>

<!-- Có thể include phần này vào trang khác -->
<div id="jitsi-container" style="height: 700px; width: 100%; margin-top: 20px;"></div>

<!-- Nhúng Jitsi -->
<script src="https://meet.jit.si/external_api.js"></script>
<script>
    const domain = "meet.jit.si";
    const options = {
        roomName: "<%= roomName %>",
        width: "100%",
        height: 700,
        parentNode: document.querySelector('#jitsi-container'),
        interfaceConfigOverwrite: {
            SHOW_JITSI_WATERMARK: false,
            SHOW_BRAND_WATERMARK: false
        }
    };
    const api = new JitsiMeetExternalAPI(domain, options);

    // Lắng nghe sự kiện
    api.addListener('participantJoined', function (event) {
        console.log("Người mới vào phòng:", event.displayName);
    });

    api.addListener('participantLeft', function (event) {
        console.log("Người rời khỏi phòng:", event);
    });
</script>
