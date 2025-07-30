<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${flashcard.title} - Wasabii</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="<c:url value='/css/nav.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/indexstyle.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/view-flashcard.css'/>" rel="stylesheet">
</head>
<body class="view-flashcard-page">
    <!-- Navigation -->
    <%@ include file="Home/nav.jsp" %>

    <!-- Main Content -->
    <div class="container mt-4 view-flashcard-page">
        <!-- Error Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle"></i>
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Header Section với 3 ô riêng biệt -->
        <div class="flashcard-header-section">
            <div class="row">
                <!-- Ô 1: Quay lại danh sách -->
                <div class="col-md-4">
                    <div class="header-box back-box">
                        <a href="<c:url value='/flashcard'/>" class="back-link-flashcard">
                            <i class="fas fa-arrow-left back-link-icon"></i>
                            Quay lại danh sách
                        </a>
                    </div>
                </div>
                
                <!-- Ô 2: Tên flashcard -->
                <div class="col-md-4">
                    <div class="header-box title-box">
                        <h1 class="flashcard-title">${flashcard.title}</h1>
                        <c:if test="${not empty flashcard.description}">
                            <p class="flashcard-description">${flashcard.description}</p>
                        </c:if>
                    </div>
                </div>
                
                <!-- Ô 3: Chỉnh sửa -->
                <div class="col-md-4">
                    <div class="header-box edit-box">
                        <c:if test="${authUser.userID eq flashcard.userID}">
                            <a href="edit-flashcard?id=${flashcard.flashcardID}" class="edit-button">
                                <i class="fas fa-edit"></i> Chỉnh sửa
                            </a>
                        </c:if>
                        
                        <!-- Debug info (remove in production) -->
                        <c:if test="${authUser.userID ne flashcard.userID}">
                            <div class="debug-info">
                                <small>
                                    User ID: ${authUser.userID} | Flashcard User ID: ${flashcard.userID}
                                </small>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>

        <!-- Flashcard Viewer -->
        <div class="flashcard-viewer position-relative">
            <!-- Controls -->
            <div class="flashcard-controls">
                <div class="row align-items-center">
                    <div class="col-md-4">
                        <div class="progress-container">
                            <div class="d-flex justify-content-between mb-2">
                                <span>Tiến độ học tập</span>
                                <span id="progressText">0 / ${items.size()}</span>
                            </div>
                            <div class="progress">
                                <div class="progress-bar" id="progressBar" style="width: 0%"></div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 text-center">
                        <button type="button" class="study-mode-toggle" id="studyModeToggle">
                            <i class="fas fa-random"></i>
                            Chế độ học tập
                        </button>
                    </div>
                    <div class="col-md-4 text-end">
                        <button type="button" class="btn btn-outline-primary" id="resetOrderBtn" onclick="resetToOriginalOrder()">
                            <i class="fas fa-sort-numeric-up"></i>
                            Sắp xếp theo thứ tự gốc
                        </button>
                    </div>
                </div>
            </div>

            <!-- Card Display -->
            <div class="card-display">
                <c:choose>
                    <c:when test="${empty items}">
                        <div class="empty-state">
                            <i class="fas fa-layer-group"></i>
                            <h3>Chưa có nội dung</h3>
                            <p>Flashcard này chưa có nội dung để học tập.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                                               
                        <!-- Hidden data container -->
                        <div id="flashcardData" style="display: none;">
                            <c:forEach var="item" items="${items}" varStatus="status">
                                <div class="flashcard-item" 
                                     data-id="${item.flashcardItemID}"
                                     data-front="${fn:escapeXml(item.frontContent)}"
                                     data-back="${fn:escapeXml(item.backContent)}"
                                     data-front-image="${fn:escapeXml(item.frontImage)}"
                                     data-back-image="${fn:escapeXml(item.backImage)}"
                                     data-note="${fn:escapeXml(item.note)}"
                                     data-order="${item.orderIndex}">
                                </div>
                            </c:forEach>
                        </div>
                        
                        <div class="flashcard" id="flashcard" onclick="flipCard()">
                            <div class="flashcard-inner">
                                <div class="flashcard-front" id="cardFront">
                                    <div id="frontContent">Loading...</div>
                                </div>
                                <div class="flashcard-back" id="cardBack">
                                    <div id="backContent">Loading...</div>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <!-- Navigation buttons -->
            <c:if test="${not empty items}">
                <div class="d-flex justify-content-center mt-4 mb-4">
                    <div class="navigation-buttons">
                        <button type="button" class="nav-btn" id="prevBtn" onclick="previousCard()">
                            <i class="fas fa-chevron-left"></i>
                        </button>
                        <button type="button" class="nav-btn" id="nextBtn" onclick="nextCard()">
                            <i class="fas fa-chevron-right"></i>
                        </button>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- Card Information -->
        <c:if test="${not empty items}">
            <div class="card-info">
                <h3>
                    <i class="fas fa-info-circle"></i>
                    Thông tin thẻ
                </h3>
                <div class="info-item">
                    <span class="info-label">Thẻ hiện tại:</span>
                    <span class="info-value" id="currentCardInfo"></span>
                </div>
                <div class="info-item">
                    <span class="info-label">Tổng số thẻ:</span>
                    <span class="info-value">${items.size()}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">Ngày tạo:</span>
                    <span class="info-value">
                        <fmt:formatDate value="${flashcard.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                    </span>
                </div>
                <div class="info-item">
                    <span class="info-label">Quyền riêng tư:</span>
                    <span class="info-value">
                        <span class="badge ${flashcard.publicFlag ? 'bg-success' : 'bg-secondary'}">
                            <i class="fas ${flashcard.publicFlag ? 'fa-globe' : 'fa-lock'}"></i>
                            ${flashcard.publicFlag ? 'Công khai' : 'Riêng tư'}
                        </span>
                    </span>
                </div>
            </div>

            <!-- Note Section -->
            <div class="note-section" id="noteSection" style="display: none;">
                <h4>
                    <i class="fas fa-sticky-note"></i>
                    Ghi chú
                </h4>
                <p id="noteContent"></p>
            </div>
        </c:if>
    </div>

    <%@ include file="Home/footer.jsp" %>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <script>
        // Flashcard data
        window.flashcardsData = [
            <c:forEach var="item" items="${items}" varStatus="status">
                {
                    id: ${item.flashcardItemID},
                    front: '${fn:escapeXml(item.frontContent)}',
                    back: '${fn:escapeXml(item.backContent)}',
                    frontImage: '${empty item.frontImage ? "null" : fn:escapeXml(item.frontImage)}',
                    backImage: '${empty item.backImage ? "null" : fn:escapeXml(item.backImage)}',
                    note: '${fn:escapeXml(item.note)}',
                    orderIndex: ${item.orderIndex}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
    </script>
    <script src="<c:url value='/js/flashcard-viewer.js'/>"></script>
</body>
</html> 