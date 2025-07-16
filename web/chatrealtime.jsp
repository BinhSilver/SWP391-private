<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>SWP_HUY | Chat</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <meta name="current-user-id" content="${sessionScope.authUser.userID}">
        <meta name="current-username" content="${sessionScope.authUser.fullName}">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat.css">
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    </head>
    <body>
        <%@ include file="../Home/nav.jsp" %>
        <c:if test="${empty sessionScope.authUser}">
            <c:redirect url="/test/LoginJSP/LoginIndex.jsp"/>
        </c:if>

        <section class="chat-container">
            <div class="chat-sidebar">
                <input type="text" id="searchUser" placeholder="Tìm kiếm người dùng..." oninput="searchUsers(this.value)">
                <ul id="userList">
                    <c:forEach var="u" items="${userList}">
                        <c:if test="${u.userID != sessionScope.authUser.userID}">
                            <li data-user-id="${u.userID}" onclick="selectChatUser(${u.userID}, '${u.fullName}')">
                                <img src="${pageContext.request.contextPath}/avatar?userId=${u.userID}&v=${u.avatar != null ? u.avatar.hashCode() : System.currentTimeMillis()}" alt="Avatar" style="width: 48px; height: 48px; border-radius: 50%;">
                                <strong>${u.fullName}</strong>
                            </li>
                        </c:if>
                    </c:forEach>
                </ul>
            </div>
            <div class="chat-main" style="display: none;">
                <div class="chat-header">
                    <strong><span id="chatWith">[Chọn người dùng]</span></strong>
                    <div class="dropdown">
                        <ion-icon id="userDropDown" name="ellipsis-vertical-outline"></ion-icon>
                        <div id="userDropDownContent" class="dropdown-content" style="display: none;">
                            <button id="blockBtn" onclick="blockUser()">Chặn</button>
                            <button id="unblockBtn" onclick="unblockUser()" style="display: none;">Bỏ chặn</button>
                        </div>
                    </div>
                </div>
                <div class="chat-body" id="chatBox"></div>
                <div class="chat-input-wrap">
                    <input type="text" id="messageInput" placeholder="Nhập tin nhắn..." onkeydown="if (event.key === 'Enter')
                                sendMessage()">
                    <button onclick="sendMessage()">Gửi</button>
                    <p id="blockNotice" style="color: red; display: none;"></p>
                </div>
            </div>
        </section>
        <!-- Scripts -->
        <script>
            // Inject context path vào window object
            window.contextPath = '${pageContext.request.contextPath}';
            console.log('Context path loaded:', window.contextPath);
        </script>
        <script src="${pageContext.request.contextPath}/js/config.js"></script>
        <!-- Các import JS động -->
        <script src="${pageContext.request.contextPath}/chat/chat.js"></script>
        <!-- Nếu không có cherry-blossom.js thì bỏ dòng dưới, nếu có thì sửa lại đường dẫn động -->
        <!-- <script src="${pageContext.request.contextPath}/js/cherry-blossom.js"></script> -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    </body>
</html>