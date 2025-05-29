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
    </head><!-- comment -->
    <body>
        <div class="page-wrapper">
            <%@include file="search.jsp" %>

            <div class="main-content">
                <jsp:include page="Menu.jsp"></jsp:include>

                
               <%@include file="chatBox.jsp" %> 
                <%@include file="jitsi.jsp" %>  
               
            </div>
        </div>


    </body>
</html>
