<%@page import="model.User"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page session="true" contentType="text/html" pageEncoding="UTF-8"%>
<%
    User user = (User) session.getAttribute("authUser");
    if (user == null) {
        response.sendRedirect("LoginJSP/LoginIndex.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Edit Profile</title>
        <link rel="stylesheet" href="Profile/profile.css">
    
   
    </head>
    <body>
        <div class="container">
            <form action="editprofile" method="post" class="edit-form" enctype="multipart/form-data">
                <h2>Edit Profile</h2>
                <% if (request.getAttribute("error") != null) {%>
                <p style="color: red;"><%= request.getAttribute("error")%></p>
                <% }%>
                <input type="hidden" name="userID" value="<%= user.getUserID()%>" />

                
                <div class="form-grid">
                    <div class="form-group">
                        <label><i class="fa-solid fa-envelope"></i> Email</label>
                        <input type="text" name="email" value="<%= user.getEmail() != null ? user.getEmail() : ""%>" />
                    </div>

                    <div class="form-group">
                        <label><i class="fa-solid fa-user"></i> Full Name</label>
                        <input type="text" name="fullName" value="<%= user.getFullName() != null ? user.getFullName() : ""%>" />
                    </div>

                    <div class="form-group">
                        <label><i class="fa-solid fa-phone"></i> Phone</label>
                        <input type="text" name="phoneNumber" value="<%= user.getPhoneNumber() != null ? user.getPhoneNumber() : ""%>" />
                    </div>

                    <div class="form-group">
                        <label><i class="fa-solid fa-cake-candles"></i> Birth Date</label>
                        <input type="date" name="birthDate" value="<%= user.getBirthDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(user.getBirthDate()) : ""%>" />
                    </div>

                    <div class="form-group">
                        <label><i class="fa-solid fa-language"></i> Japanese Level:</label>
                        <select name="japaneseLevel">
                            <option value="Chưa có bằng" <%= "Chưa có bằng".equals(user.getJapaneseLevel()) ? "selected" : ""%>>Chưa có bằng</option>
                            <option value="N5" <%= "N5".equals(user.getJapaneseLevel()) ? "selected" : ""%>>N5</option>
                            <option value="N4" <%= "N4".equals(user.getJapaneseLevel()) ? "selected" : ""%>>N4</option>
                            <option value="N3" <%= "N3".equals(user.getJapaneseLevel()) ? "selected" : ""%>>N3</option>
                            <option value="N2" <%= "N2".equals(user.getJapaneseLevel()) ? "selected" : ""%>>N2</option>
                            <option value="N1" <%= "N1".equals(user.getJapaneseLevel()) ? "selected" : ""%>>N1</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label><i class="fa-solid fa-flag"></i> Country</label>
                        <input type="text" name="country" value="<%= user.getCountry() != null ? user.getCountry() : ""%>" />
                    </div>

                    <div class="form-group full-width">
                        <label><i class="fa-solid fa-image"></i> Ảnh đại diện</label>
                        <input type="file" name="avatar" accept="image/*" />
                    </div>
                    
                    <div class="form-group full-width">
                        <label><i class="fa-solid fa-location-dot"></i> Address</label>
                        <input type="text" id="address" name="address" list="address-options"
                               value="<%= user.getAddress() != null ? user.getAddress() : ""%>" oninput="suggestAddress()" />
                        <datalist id="address-options"></datalist>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn primary">Save Changes</button>
                    <button type="button" class="btn secondary" onclick="window.location.href = 'profile'">Cancel</button>
                </div>
            </form>
        </div>
    </body>
</html>