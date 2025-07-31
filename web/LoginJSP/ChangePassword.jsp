    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
    <!DOCTYPE html>
    <html>
    <head>
        <title>Change Password</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/change.css" />
        <link href='https://unpkg.com/boxicons@2.1.4/css/boxicons.min.css' rel='stylesheet'>
    </head>
    <body>
    <div class="container active-change">
        <div class="form-box change-password">
            <!-- Nút đóng -->
            <button class="close-btn" onclick="hideChangePasswordForm()">×</button>

            <form id="changePasswordForm" action="${pageContext.request.contextPath}/login" method="post">
                <input type="hidden" name="action" value="change_pass"> 
                <h1>Change Password</h1>
                <div class="input-box">
                    <input type="email" id="email" name="email" placeholder="Enter your email" 
                           value="<c:out value='${userEmail}' default='' />" required />
                    <i class="bx bxs-envelope"></i>
                </div>
                <div class="input-box">
                    <input type="password" name="oldPassword" placeholder="Old Password" required />
                    <i class="bx bxs-lock-alt"></i>
                </div>
                <div class="input-box">
                    <input type="password" name="newPassword" placeholder="New Password" required minlength="8"/>
                    <i class="bx bxs-lock-alt"></i>
                </div>

                <div class="password-rules" id="passwordRules" style="display: none;">
                    <ul>
                        <li id="length">At least 8 characters long</li>
                        <li id="uppercase">At least 1 uppercase letter</li>
                        <li id="special">At least 1 special symbol</li>
                    </ul>
                </div>

                <button type="submit" class="btn">Change</button>
            </form>
        </div>
    </div>

    <script>
        function hideChangePasswordForm() {
            document.querySelector('.container').classList.remove('active-change');
        }
    </script>
    </body>
    </html>