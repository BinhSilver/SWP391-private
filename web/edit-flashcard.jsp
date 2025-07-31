<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa Flashcard - Wasabii</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="<c:url value='/css/nav.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/indexstyle.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/edit-flashcard.css'/>" rel="stylesheet">
</head>
<body class="edit-flashcard-page">
    <!-- Navigation -->
    <%@ include file="Home/nav.jsp" %>
    
    <!-- Advertisement Banner -->
    <%@ include file="ads.jsp"%>

    <!-- Main Content -->
    <div class="container mt-4 edit-flashcard-page">
        <!-- Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center">
                    <h2><i class="fas fa-edit text-primary"></i> Chỉnh sửa Flashcard</h2>
                    <a href="view-flashcard?id=${flashcard.flashcardID}" class="btn btn-outline-secondary">
                        <i class="fas fa-arrow-left"></i> Quay lại
                    </a>
                </div>
            </div>
        </div>

        <!-- Error/Success Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <!-- Limit Information -->
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="fas fa-info-circle"></i>
            <strong>Thông tin giới hạn item:</strong>
            <c:choose>
                <c:when test="${sessionScope.authUser != null}">
                    <c:set var="premiumService" value="<%= new service.PremiumService() %>" />
                    <c:set var="userID" value="${sessionScope.authUser.userID}" />
                    <c:set var="isPremium" value="${premiumService.isUserPremium(userID)}" />
                    <c:set var="itemLimitInfo" value="${premiumService.getItemLimitInfo(userID, flashcard.flashcardID)}" />
                    
                    <c:choose>
                        <c:when test="${sessionScope.authUser.roleID == 3}">
                            <span class="text-success">Teacher - Không giới hạn item</span>
                        </c:when>
                        <c:when test="${sessionScope.authUser.roleID == 4}">
                            <span class="text-success">Admin - Không giới hạn item</span>
                        </c:when>
                        <c:when test="${isPremium}">
                            <span class="text-success">Premium User - Không giới hạn item</span>
                        </c:when>
                        <c:otherwise>
                            <span class="text-warning">${itemLimitInfo}</span>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <span>Vui lòng đăng nhập để xem thông tin giới hạn</span>
                </c:otherwise>
            </c:choose>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>

        <!-- Flashcard Information Form -->
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-info-circle"></i> Thông tin Flashcard</h5>
            </div>
            <div class="card-body">
                <form action="edit-flashcard" method="post" enctype="multipart/form-data" id="flashcardForm">
                    <input type="hidden" name="flashcardId" value="${flashcard.flashcardID}">
                    
                    <div class="row">
                        <div class="col-md-8">
                            <div class="mb-3">
                                <label for="title" class="form-label">Tiêu đề <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="title" name="title" 
                                       value="${flashcard.title}" required>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="mb-3">
                                <label class="form-label">Trạng thái</label>
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" id="isPublic" name="isPublic" 
                                           ${flashcard.publicFlag ? 'checked' : ''}>
                                    <label class="form-check-label" for="isPublic">
                                        Công khai
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">Mô tả</label>
                        <textarea class="form-control" id="description" name="description" rows="3">${flashcard.description}</textarea>
                    </div>

                    <div class="mb-3">
                        <label for="coverImage" class="form-label">Ảnh bìa</label>
                        <input type="hidden" name="oldCoverImage" value="${flashcard.coverImage}">
                        <div class="file-input-wrapper">
                            <input type="file" id="coverImage" name="coverImage" accept="image/*" 
                                   data-preview="coverPreview">
                            <label for="coverImage" class="file-input-label">
                                <i class="fas fa-upload"></i>
                                Chọn ảnh bìa
                            </label>
                        </div>
                        <div class="image-preview-container">
                            <img id="coverPreview" class="image-preview" 
                                 src="${flashcard.coverImage}" alt="Cover preview">
                        </div>
                    </div>

                    <div class="text-end">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Lưu thông tin
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Flashcard Items -->
        <div class="card">
            <div class="card-header">
                <div class="d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="fas fa-list"></i> Danh sách thẻ (${items.size()} thẻ)</h5>
                    <button type="button" class="btn btn-success" onclick="saveAllItems()" id="saveAllBtn" style="display: none;">
                        <i class="fas fa-save"></i> Lưu tất cả thay đổi
                    </button>
                </div>
            </div>
            <div class="card-body">
                <c:if test="${empty items}">
                    <div class="text-center py-4">
                        <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                        <p class="text-muted">Chưa có thẻ nào trong flashcard này.</p>
                        <a href="create-flashcard?id=${flashcard.flashcardID}" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Thêm thẻ mới
                        </a>
                    </div>
                </c:if>

                <c:forEach var="item" items="${items}" varStatus="status">
                    <div class="flashcard-item" id="item-${item.flashcardItemID}">
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <h6 class="mb-0">Thẻ #${status.index + 1}</h6>
                            <button class="btn btn-sm btn-outline-primary" onclick="toggleEdit(${item.flashcardItemID})">
                                <i class="fas fa-edit"></i> Chỉnh sửa
                            </button>
                        </div>

                        <!-- View Mode -->
                        <div class="item-content" id="view-${item.flashcardItemID}">
                            <div class="front-side">
                                <strong>Mặt trước:</strong>
                                <p class="mb-2">${item.frontContent}</p>
                                <c:if test="${not empty item.frontImage}">
                                    <img src="${item.frontImage}" alt="Front Image" class="img-fluid rounded" style="max-height: 100px;">
                                </c:if>
                            </div>
                            <div class="back-side">
                                <strong>Mặt sau:</strong>
                                <p class="mb-2">${item.backContent}</p>
                                <c:if test="${not empty item.backImage}">
                                    <img src="${item.backImage}" alt="Back Image" class="img-fluid rounded" style="max-height: 100px;">
                                </c:if>
                            </div>
                        </div>

                        <!-- Edit Mode -->
                        <div class="item-content" id="edit-${item.flashcardItemID}" style="display: none;">
                            <form class="w-100 item-edit-form" enctype="multipart/form-data" data-item-id="${item.flashcardItemID}">
                                <input type="hidden" name="itemId" value="${item.flashcardItemID}">
                                <input type="hidden" name="flashcardId" value="${flashcard.flashcardID}">
                                <input type="hidden" name="orderIndex" value="${item.orderIndex}">
                                
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label class="form-label">Mặt trước <span class="text-danger">*</span></label>
                                            <textarea class="form-control" name="frontContent" rows="3" required>${item.frontContent}</textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">Ảnh mặt trước</label>
                                            <input type="hidden" name="oldFrontImage" value="${item.frontImage}">
                                            <div class="file-input-wrapper">
                                                <input type="file" name="frontImage" accept="image/*" 
                                                       data-preview="frontPreview${item.flashcardItemID}">
                                                <label class="file-input-label">
                                                    <i class="fas fa-upload"></i>
                                                    Chọn ảnh
                                                </label>
                                            </div>
                                            <div class="image-preview-container">
                                                <img id="frontPreview${item.flashcardItemID}" class="image-preview" 
                                                     src="${item.frontImage}" alt="Front preview">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label class="form-label">Mặt sau <span class="text-danger">*</span></label>
                                            <textarea class="form-control" name="backContent" rows="3" required>${item.backContent}</textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label class="form-label">Ảnh mặt sau</label>
                                            <input type="hidden" name="oldBackImage" value="${item.backImage}">
                                            <div class="file-input-wrapper">
                                                <input type="file" name="backImage" accept="image/*" 
                                                       data-preview="backPreview${item.flashcardItemID}">
                                                <label class="file-input-label">
                                                    <i class="fas fa-upload"></i>
                                                    Chọn ảnh
                                                </label>
                                            </div>
                                            <div class="image-preview-container">
                                                <img id="backPreview${item.flashcardItemID}" class="image-preview" 
                                                     src="${item.backImage}" alt="Back preview">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="mb-3">
                                    <label class="form-label">Ghi chú</label>
                                    <textarea class="form-control" name="note" rows="2">${item.note}</textarea>
                                </div>

                                <div class="text-end">
                                    <button type="button" class="btn btn-secondary me-2" onclick="cancelEdit(${item.flashcardItemID})">
                                        <i class="fas fa-times"></i> Hủy
                                    </button>
                                    <button type="button" class="btn btn-save-item" onclick="saveItem(${item.flashcardItemID})">
                                        <i class="fas fa-save"></i> Lưu thay đổi
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>

    <%@ include file="Home/footer.jsp" %>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/js/edit-flashcard.js'/>"></script>
</body>
</html> 