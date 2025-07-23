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
    <link href="<c:url value='/css/flashcard.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/nav.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>" rel="stylesheet">
    <style>
        .form-section {
            background: white;
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 2px 10px rgba(233, 79, 100, 0.1);
        }
        
        .form-label {
            font-weight: 600;
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .form-control, .form-select {
            border-radius: 10px;
            border: 2px solid #e9ecef;
            padding: 0.75rem 1rem;
            transition: all 0.3s ease;
        }
        
        .form-control:focus, .form-select:focus {
            border-color: #e94f64;
            box-shadow: 0 0 0 0.2rem rgba(233, 79, 100, 0.25);
        }
        
        .card-item {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 1.5rem;
            margin-bottom: 1rem;
            border: 2px dashed #dee2e6;
            transition: all 0.3s ease;
        }
        
        .card-item:hover {
            border-color: #e94f64;
            background: #fff5f6;
        }
        
        .card-item-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }
        
        .card-number {
            background: #e94f64;
            color: white;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
        }
        
        .remove-card {
            background: #dc3545;
            color: white;
            border: none;
            border-radius: 50%;
            width: 30px;
            height: 30px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .remove-card:hover {
            background: #c82333;
            transform: scale(1.1);
        }
        
        .image-preview {
            width: 100px;
            height: 100px;
            border-radius: 10px;
            object-fit: cover;
            border: 2px solid #dee2e6;
            margin-top: 0.5rem;
        }
        
        .file-input-wrapper {
            position: relative;
            display: inline-block;
            cursor: pointer;
        }
        
        .file-input-wrapper input[type=file] {
            position: absolute;
            opacity: 0;
            width: 100%;
            height: 100%;
            cursor: pointer;
        }
        
        .file-input-label {
            display: inline-block;
            padding: 0.5rem 1rem;
            background: #e94f64;
            color: white;
            border-radius: 20px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .file-input-label:hover {
            background: #d43e55;
            transform: translateY(-2px);
        }
        
        .btn-add-card {
            background: #28a745;
            color: white;
            border: none;
            border-radius: 25px;
            padding: 0.75rem 1.5rem;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .btn-add-card:hover {
            background: #218838;
            transform: translateY(-2px);
        }
        
        .btn-submit {
            background: #e94f64;
            color: white;
            border: none;
            border-radius: 25px;
            padding: 1rem 2rem;
            font-weight: 600;
            font-size: 1.1rem;
            transition: all 0.3s ease;
        }
        
        .btn-submit:hover {
            background: #d43e55;
            transform: translateY(-2px);
            box-shadow: 0 4px 20px rgba(233, 79, 100, 0.3);
        }
        
        .btn-cancel {
            background: #6c757d;
            color: white;
            border: none;
            border-radius: 25px;
            padding: 1rem 2rem;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .btn-cancel:hover {
            background: #5a6268;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <%@ include file="Home/nav.jsp" %>

    <!-- Main Content -->
    <div class="container mt-4">
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
                    <button type="button" class="btn-add-card" onclick="addCardItem()">
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
                <button type="submit" class="btn btn-submit">
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
    <script>
        let cardCount = 0;

        function addCardItem() {
            cardCount++;
            const cardItems = document.getElementById('cardItems');
            const uniqueId = Date.now() + '_' + cardCount; // Tạo ID duy nhất
            const suffix = cardCount > 1 ? '-' + cardCount : '';
            
            const cardItem = document.createElement('div');
            cardItem.className = 'card-item';
            cardItem.innerHTML = `
                <div class="card-item-header">
                    <div class="card-number">${cardCount}</div>
                    <button type="button" class="remove-card" onclick="removeCardItem(this)">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                
                <div class="row">
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label class="form-label">Mặt trước *</label>
                            <input type="text" class="form-control" name="frontContent${suffix}" required 
                                   placeholder="Nội dung mặt trước">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Ảnh mặt trước</label>
                            <div class="file-input-wrapper">
                                <input type="file" name="frontImage${suffix}" accept="image/*" 
                                       onchange="previewImage(this, 'frontPreview${uniqueId}')">
                                <label class="file-input-label">
                                    <i class="fas fa-upload"></i>
                                    Chọn ảnh
                                </label>
                            </div>
                            <img id="frontPreview${uniqueId}" class="image-preview" style="display: none;">
                        </div>
                    </div>
                    
                    <div class="col-md-6">
                        <div class="mb-3">
                            <label class="form-label">Mặt sau *</label>
                            <input type="text" class="form-control" name="backContent${suffix}" required 
                                   placeholder="Nội dung mặt sau">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Ảnh mặt sau</label>
                            <div class="file-input-wrapper">
                                <input type="file" name="backImage${suffix}" accept="image/*" 
                                       onchange="previewImage(this, 'backPreview${uniqueId}')">
                                <label class="file-input-label">
                                    <i class="fas fa-upload"></i>
                                    Chọn ảnh
                                </label>
                            </div>
                            <img id="backPreview${uniqueId}" class="image-preview" style="display: none;">
                        </div>
                    </div>
                </div>
                
                <div class="mb-3">
                    <label class="form-label">Ghi chú</label>
                    <textarea class="form-control" name="note${suffix}" rows="2" 
                              placeholder="Ghi chú bổ sung (tùy chọn)"></textarea>
                </div>
            `;
            
            cardItems.appendChild(cardItem);
            updateCardNumbers(); // Đảm bảo cập nhật lại name đúng thứ tự sau khi thêm
        }

        function removeCardItem(button) {
            const cardItem = button.closest('.card-item');
            cardItem.remove();
            updateCardNumbers();
        }

        function updateCardNumbers() {
            const cardItems = document.querySelectorAll('.card-item');
            cardItems.forEach((item, index) => {
                const number = index + 1;
                const numberElement = item.querySelector('.card-number');
                numberElement.textContent = number;
                // Cập nhật lại name cho input/textarea
                item.querySelector('input[name^="frontContent"]').name = 'frontContent' + (number > 1 ? '-' + number : '');
                item.querySelector('input[name^="backContent"]').name = 'backContent' + (number > 1 ? '-' + number : '');
                item.querySelector('input[name^="frontImage"]').name = 'frontImage' + (number > 1 ? '-' + number : '');
                item.querySelector('input[name^="backImage"]').name = 'backImage' + (number > 1 ? '-' + number : '');
                item.querySelector('textarea[name^="note"]').name = 'note' + (number > 1 ? '-' + number : '');
            });
            cardCount = cardItems.length;
        }

        function previewImage(input, previewId) {
            const preview = document.getElementById(previewId);
            if (input.files && input.files[0]) {
                const file = input.files[0];
                
                // Kiểm tra kích thước file (tối đa 10MB)
                if (file.size > 10 * 1024 * 1024) {
                    alert('File ảnh quá lớn. Vui lòng chọn file nhỏ hơn 10MB.');
                    input.value = '';
                    return;
                }
                
                // Kiểm tra loại file
                if (!file.type.startsWith('image/')) {
                    alert('Vui lòng chọn file ảnh hợp lệ.');
                    input.value = '';
                    return;
                }
                
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.src = e.target.result;
                    preview.style.display = 'block';
                };
                reader.readAsDataURL(file);
            }
        }

        // Add first card item when page loads
        document.addEventListener('DOMContentLoaded', function() {
            addCardItem();
        });

        // Thêm hàm getItemCount cho client-side để backend nhận đúng số lượng item
        function getItemCount() {
            return document.querySelectorAll('.card-item').length;
        }

        // Form validation
        document.getElementById('flashcardForm').addEventListener('submit', function(e) {
            const title = document.getElementById('title').value.trim();
            if (!title) {
                e.preventDefault();
                alert('Vui lòng nhập tiêu đề flashcard!');
                return;
            }
            const cardItems = document.querySelectorAll('.card-item');
            if (cardItems.length === 0) {
                e.preventDefault();
                alert('Vui lòng thêm ít nhất một thẻ flashcard!');
                return;
            }
            let hasValidContent = false;
            cardItems.forEach(item => {
                const frontContent = item.querySelector('input[name^="frontContent"]').value.trim();
                const backContent = item.querySelector('input[name^="backContent"]').value.trim();
                if (frontContent && backContent) {
                    hasValidContent = true;
                }
            });
            if (!hasValidContent) {
                e.preventDefault();
                alert('Vui lòng nhập nội dung cho ít nhất một thẻ flashcard!');
                return;
            }
            // Đảm bảo cập nhật lại name đúng thứ tự trước khi lấy itemCount
            updateCardNumbers();
            // Thêm input ẩn
            let itemCountInput = document.getElementById('itemCountInput');
            if (!itemCountInput) {
                itemCountInput = document.createElement('input');
                itemCountInput.type = 'hidden';
                itemCountInput.name = 'itemCount';
                itemCountInput.id = 'itemCountInput';
                this.appendChild(itemCountInput);
            }
            itemCountInput.value = getItemCount();
        });
    </script>
</body>
</html> 