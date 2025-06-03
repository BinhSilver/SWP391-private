<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!--begin of menu-->
<nav class="navbar navbar-expand-md navbar-dark bg-dark" style="position: fixed; top: 0; width:100%;  z-index: 100000;">
    <div class="container">
        <a class="navbar-brand" href="home">Wasabii</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExampleDefault" aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse justify-content-end" id="navbarsExampleDefault">
            <ul class="navbar-nav m-auto">
                <li class="nav-item">
                    <a class="nav-link" href="home">Home</a>
                </li> 
                <li class="nav-item">
                    <a class="nav-link" href="shop">Shop</a>
                </li> 

                <%--  <c:if test="${sessionScope.acc.isSell == 1}">
                     <li class="nav-item">
                         <a class="nav-link" href="manager">Manager Product</a>
                     </li>
                 </c:if> --%>
                <c:if test="${sessionScope.acc != null}">
                    <li class="nav-item">
                        <a class="nav-link" href="#">Hello ${sessionScope.acc.user}</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="logout">Logout</a>
                    </li> 
                </c:if>
                <c:if test="${sessionScope.acc == null}">
                    <li class="nav-item">
                        <a class="nav-link" href="LoginJSP/LoginIndex.jsp">Login</a>
                    </li>
                </c:if>
               
                <c:if test="${sessionScope.acc != null}">
                    <li class="nav-item">
                        <a class="nav-link" href="Profile/EditProfile.jsp">Edit Profile</a>
                    </li>
                </c:if>
                <c:if test="${sessionScope.acc == null}">
                    <li class="nav-item">
                        <a class="nav-link" href="Profile/profile-view.jsp">My Profile</a>
                    </li>
                </c:if>
                     <c:if test="${sessionScope.acc == null}">
                    <li class="nav-item">
                        <a class="nav-link" href="VideoCall/call.jsp">call video</a>
                    </li>
                </c:if>
                <%--  <c:if test="${sessionScope.acc.isAdmin == 1}">
                     <li class="nav-item">
                         <a class="nav-link" href="statistic">Statistic</a>
                     </li>
                 </c:if> --%>
            </ul>
        </div>
    </div>
</nav>
<!--end of menu-->
