
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SWP_HUY | Cuộc họp</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://unpkg.com/@daily-co/daily-js"></script>
    <style>
        html, body {
            margin: 0;
            padding: 0;
            height: 100vh; /* Full viewport height */
             /* Prevent scrollbars */
            font-family: 'JetBrains Mono', monospace;
        }
     
        /* General iframe styles for other iframes (less specific) */
        iframe {
            width:80%;
            height: 60%;
            border: none;
            display: block;
            position: relative; /* Less aggressive than absolute for other iframes */
            margin: 0;
            padding: 0;
        }
        .error-message {
            color: red;
            text-align: center;
            padding: 20px;
        }
    </style>
</head>
<body>
            <%@ include file="Home/nav.jsp" %>
        
        <!-- Advertisement Banner -->
        <%@ include file="ads.jsp"%>

    <c:if test="${empty param.roomUrl or empty param.token}">
        <c:redirect url="/test/videocall.jsp"/>
    </c:if>

    <c:set var="roomUrl" value="${param.roomUrl}"/>
    <c:choose>
        <c:when test="${not fn:contains(roomUrl, 'wasabii.daily.co') or fn:contains(roomUrl, 'dashboard')}">
            <div class="error-message">
                <p>URL phòng họp không hợp lệ. Vui lòng đảm bảo mã phòng sử dụng domain 'wasabii.daily.co'!</p>
                <a href="/test/videocall.jsp" class="btn btn-primary">Quay lại</a>
            </div>
        </c:when>
        <c:otherwise>
            <div id="meetingContainer"></div>
        </c:otherwise>
    </c:choose>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const container = document.getElementById('meetingContainer');
            if (container) {
                const callObject = window.DailyIframe.createFrame({
                    iframeStyle: {
                        width: '100%',
                        height: '100%',
                        border: 'none'
                    },
                    showLeaveButton: true,
                    showFullscreenButton: true
                });

                callObject.join({ url: "${param.roomUrl}?t=${param.token}" })
                    .then(() => {
                        console.log("Joined meeting successfully");
                        callObject.setLocalVideo(false);
                        callObject.setLocalAudio(false);
                    })
                    .catch((error) => {
                        console.error("Failed to join meeting: ", error);
                        alert("Không thể tham gia cuộc họp. Vui lòng kiểm tra kết nối hoặc liên hệ hỗ trợ!");
                    });

                navigator.mediaDevices.getUserMedia({ video: true, audio: true })
                    .then(stream => {
                        stream.getTracks().forEach(track => track.stop());
                        console.log("Camera and mic access confirmed");
                    })
                    .catch(err => {
                        console.warn("Media access error (optional): ", err);
                    });
            }
        });

        window.addEventListener('message', function(event) {
            if (event.data === 'left-meeting') {
                window.location.href = "/test/videocall.jsp";
            }
        });
    </script>
</body>
</html>
