<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/indexstyle.css">
        <link rel="stylesheet" href="css/searchstyle.css">
        <link rel="stylesheet" type="text/css" href="css/CSS_chatbox.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <title>Trang Chủ</title>
        <style>
            .alert {
                padding: 15px;
                margin-bottom: 20px;
                border: 1px solid transparent;
                border-radius: 4px;
            }
            .alert-success {
                color: #155724;
                background-color: #d4edda;
                border-color: #c3e6cb;
            }
            .alert-danger {
                color: #721c24;
                background-color: #f8d7da;
                border-color: #f5c6cb;
            }
        </style>
    </head><!-- comment -->
    <body>
        <%
            Boolean paymentSuccess = (Boolean) session.getAttribute("paymentSuccess");
            String paymentMessage = (String) session.getAttribute("paymentMessage");
            if (paymentMessage != null) {
        %>
            <div class="alert <%= paymentSuccess ? "alert-success" : "alert-danger" %>">
                <%= paymentMessage %>
            </div>
        <%
                // Clear the message after displaying
                session.removeAttribute("paymentSuccess");
                session.removeAttribute("paymentMessage");
            }
        %>
        <div class="page-wrapper">
            <%@include file="Search/search.jsp" %>

            <div class="main-content">
                <jsp:include page="Menu.jsp"></jsp:include>

                
                <%@include file="chatBoxjsp/chatBox.jsp" %>
                <%@include file="jitsi.jsp" %>
            
            </div>
        </div>


    </body>
</html>
