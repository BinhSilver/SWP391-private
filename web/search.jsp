<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Tìm kiếm Từ vựng</title>
 
        <!-- CSS & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/search.css'/>">
    

    </head>
    <body >
        <%@ include file="../Home/nav.jsp" %>
        <div class="search-page">
        <div class="search-container">
            <h2 class="section-title">🔍 Tìm kiếm Từ vựng</h2>
            <div class="search-wrapper">
                <input type="text" id="searchInput" class="form-control" placeholder="Nhập từ khóa..." autocomplete="off">
                <div id="searchDropdown" class="search-dropdown"></div>
            </div>
        </div>
</div>
        <!-- Scripts -->
        <script src="<c:url value='/js/searchVocabulary.js'/>"></script>
        <script src="https://code.responsivevoice.org/responsivevoice.js?key=YC77U5uD"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>