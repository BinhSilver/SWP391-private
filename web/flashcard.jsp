<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Flashcard - Wasabii</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
    <link rel="stylesheet" href="<c:url value='/css/flashcard.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/stylechat.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/nav.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>" rel="stylesheet">
    
    <!-- Config injection -->
    <script>
        window.contextPath = '${pageContext.request.contextPath}';
        console.log('Context path loaded:', window.contextPath);
    </script>
    <script src="<c:url value='/js/config.js'/>"></script>
</head>
<body>
    <div class="page-wrapper">
        <!-- Navigation -->
        <%@ include file="Home/nav.jsp" %>

    <!-- Advertisement Banner -->
    <%@ include file="ads.jsp"%>

        <%@ include file="chatBoxjsp/chatBox.jsp" %>

        <!-- Main Content -->
        <div class="container mt-4">
        <!-- Header -->
        <div class="row mb-4">
            <div class="col-md-8">
                <h1 class="flashcard-title">
                    <i class="fas fa-layer-group text-primary"></i>
                    Flashcard của tôi
                </h1>
                <p class="text-muted">Quản lý và học tập với flashcard</p>
            </div>
            <div class="col-md-4 text-end">
                <a href="<c:url value='/create-flashcard'/>" class="btn btn-primary btn-lg">
                    <i class="fas fa-plus"></i>
                    Tạo Flashcard Mới
                </a>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <c:if test="${param.success == 'true'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert" id="createSuccessAlert">
                <i class="fas fa-check-circle"></i>
                Tạo flashcard thành công!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <c:if test="${param.success == 'deleted'}">
            <div class="alert alert-success alert-dismissible fade show" role="alert" id="deleteSuccessAlert">
                <i class="fas fa-check-circle"></i>
                Xóa flashcard thành công!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${param.error == 'notfound'}">
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle"></i>
                Không tìm thấy flashcard!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${param.error == 'unauthorized'}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-ban"></i>
                Bạn không có quyền truy cập flashcard này!
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- All Accessible Flashcards Section -->
        <div class="row mb-5">
            <div class="col-12">
                <h2 class="section-title">
                    <i class="fas fa-layer-group text-primary"></i>
                    Tất cả Flashcard bạn có thể truy cập
                </h2>
                <c:choose>
                    <c:when test="${empty allFlashcards}">
                        <div class="empty-state">
                            <div class="empty-icon">
                                <i class="fas fa-layer-group"></i>
                            </div>
                            <h3>Chưa có flashcard nào</h3>
                            <p>Bắt đầu tạo flashcard đầu tiên của bạn để học tập hiệu quả hơn!</p>
                            <a href="<c:url value='/create-flashcard'/>" class="btn btn-primary">
                                <i class="fas fa-plus"></i>
                                Tạo Flashcard
                            </a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            <c:forEach var="flashcard" items="${allFlashcards}">
                                <div class="col-md-4 col-lg-3 mb-4">
                                    <div class="flashcard-card">
                                        <div class="flashcard-image">
                                            <c:choose>
                                                <c:when test="${not empty flashcard.coverImage}">
                                                    <img src="${flashcard.coverImage}" alt="${flashcard.title}" class="img-fluid">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="default-image">
                                                        <i class="fas fa-layer-group"></i>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="flashcard-overlay">
                                                <div class="btn-group" role="group">
                                                    <a href="<c:url value='/view-flashcard?id=${flashcard.flashcardID}'/>" 
                                                       class="btn btn-sm btn-light" title="Xem">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <c:if test="${authUser.userID eq flashcard.userID}">
                                                        <button type="button" class="btn btn-sm btn-danger" 
                                                                onclick="deleteFlashcard(${flashcard.flashcardID})" title="Xóa">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="flashcard-content">
                                            <h5 class="flashcard-title">${flashcard.title}</h5>
                                            <c:if test="${not empty flashcard.description}">
                                                <p class="flashcard-description">${flashcard.description}</p>
                                            </c:if>
                                            <div class="flashcard-meta">
                                                <c:choose>
                                                    <c:when test="${flashcard.publicFlag}">
                                                        <span class="badge bg-success">
                                                            <i class="fas fa-globe"></i>
                                                            Công khai
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">
                                                            <i class="fas fa-lock"></i>
                                                            Riêng tư
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                                <small class="text-muted">
                                                    <fmt:formatDate value="${flashcard.createdAt}" pattern="dd/MM/yyyy"/>
                                                </small>
                                                <!-- Debug info -->
                                                <small class="text-muted d-block">
                                                    ID: ${flashcard.flashcardID}, Public: ${flashcard.publicFlag}
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <%@ include file="Home/footer.jsp" %>

    <!-- Delete Confirmation Modal -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Xác nhận xóa</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Bạn có chắc chắn muốn xóa flashcard này không? Hành động này không thể hoàn tác.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <form id="deleteForm" method="post" action="<c:url value='/delete-flashcard'/>">
                        <input type="hidden" id="flashcardID" name="flashcardID">
                        <button type="submit" class="btn btn-danger">Xóa</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <script src="<c:url value='/js/flashcard.js'/>"></script>
    </div>
</body>
</html> 