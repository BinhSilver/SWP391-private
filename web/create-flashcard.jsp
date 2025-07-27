<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Flashcard - Wasabii</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="<c:url value='/css/nav.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/indexstyle.css'/>" rel="stylesheet">
    <link href="<c:url value='/css/create-flashcard.css'/>" rel="stylesheet">

</head>
<body class="create-flashcard-page">
    <!-- Navigation -->
    <%@ include file="Home/nav.jsp" %>

    <!-- Main Content -->
    <div class="container mt-4 create-flashcard-page">
        <!-- Header -->
        <div class="row mb-4">
            <div class="col-12">
                <h1 class="flashcard-title">
                    <i class="fas fa-plus-circle"></i>
                    Tạo Flashcard Mới
                </h1>
                <p class="text-muted">Tạo flashcard để học tập hiệu quả hơn</p>
            </div>
        </div>

        <!-- Error Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle"></i>
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <form action="<c:url value='/create-flashcard'/>" method="post" enctype="multipart/form-data" id="flashcardForm">
            <!-- Hidden input for item count -->
            <input type="hidden" name="itemCount" id="itemCount" value="1">
            <!-- Basic Information -->
            <div class="form-section">
                <h3 class="section-title">
                    <i class="fas fa-info-circle"></i>
                    Thông tin cơ bản
                </h3>
                
                <div class="row">
                    <div class="col-md-8">
                        <div class="mb-3">
                            <label for="title" class="form-label">Tiêu đề flashcard *</label>
                            <input type="text" class="form-control" id="title" name="title" required 
                                   placeholder="Nhập tiêu đề flashcard">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="mb-3">
                            <label for="isPublic" class="form-label">Quyền riêng tư</label>
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="isPublic" name="isPublic">
                                <label class="form-check-label" for="isPublic">
                                    Công khai
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="mb-3">
                    <label for="description" class="form-label">Mô tả</label>
                    <textarea class="form-control" id="description" name="description" rows="3" 
                              placeholder="Mô tả về flashcard này"></textarea>
                </div>
                
                <div class="mb-3">
                    <label for="coverImage" class="form-label">Ảnh bìa</label>
                    <div class="file-input-wrapper">
                        <input type="file" id="coverImage" name="coverImage" accept="image/*" 
                               onchange="previewImage(this, 'coverPreview')">
                        <label for="coverImage" class="file-input-label">
                            <i class="fas fa-upload"></i>
                            Chọn ảnh bìa
                        </label>
                    </div>
                    <img id="coverPreview" class="image-preview" style="display: none;">
                </div>
            </div>

            <!-- Flashcard Items -->
            <div class="form-section">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h3 class="section-title mb-0">
                        <i class="fas fa-layer-group"></i>
                        Nội dung flashcard
                    </h3>
                    <button type="button" class="add-card-btn">
                        <i class="fas fa-plus"></i>
                        Thêm thẻ
                    </button>
                </div>
                
                <div id="cardItems">
                    <!-- Card items will be added here -->
                </div>
            </div>



            <!-- Submit Buttons -->
            <div class="form-section text-center">
                <button type="button" class="btn btn-cancel me-3" onclick="history.back()">
                    <i class="fas fa-arrow-left"></i>
                    Quay lại
                </button>
                <button type="submit" class="submit-btn">
                    <i class="fas fa-save"></i>
                    Tạo Flashcard
                </button>
            </div>
        </form>
    </div>

    <%@ include file="Home/footer.jsp" %>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <script src="<c:url value='/js/create-flashcard.js'/>"></script>
    

</body>
</html> 