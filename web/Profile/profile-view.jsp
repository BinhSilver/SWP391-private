<%@page import="model.User"%>
<%@page session="true" contentType="text/html" pageEncoding="UTF-8"%>
<%
    User user = (User) session.getAttribute("authUser");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <title>My Profile</title>
       <link rel="stylesheet" href="${pageContext.request.contextPath}/Profile/profile.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>
    <body>
        <div class="container">
            <div class="sidebar">
                <div class="profile-pic">
                    <img src="<%= user.getAvatar()%>" alt="avatar">
                </div>
                <h3><%= user.getFullName()%></h3>
                <p class="title">User ID: <%= user.getUserID()%></p>
                <ul class="menu">
                    <li><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/editprofile">Edit Profile</a></li>
                    <li><a href="LoginJSP/ChangePassword.jsp">Change Password</a></li>
                    <li><a href="logout">Logout</a></li>
                </ul>
            </div>
            <div class="main">
                <div class="header">
                    <h2>My Details</h2>
                   
                </div>
                <div class="info-grid">
                    <div><label><i class="fa-solid fa-envelope"></i> Email:</label> <span><%= user.getEmail()%></span></div>
                    <div><label><i class="fa-solid fa-phone"></i> Phone:</label> <span><%= user.getPhoneNumber()%></span></div>
                    <div><label><i class="fa-solid fa-cake-candles"></i> Birth Date:</label> <span><%= user.getBirthDate()%></span></div>
                    <div><label><i class="fa-solid fa-language"></i> Japanese Level:</label> <span><%= user.getJapaneseLevel()%></span></div>
                    <div><label><i class="fa-solid fa-flag"></i> Country:</label> <span><%= user.getCountry()%></span></div>
                    <div><label><i class="fa-solid fa-location-dot"></i> Address:</label> <span><%= user.getAddress()%></span></div>
                </div>

            </div>
        </div>
    </body>
</html>
