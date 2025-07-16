<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${flashcard.title} - Wasabii</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="<c:url value='/css/flashcard.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/nav.css'/>" rel="stylesheet">
    <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>" rel="stylesheet">
    <style>
        .flashcard-viewer {
            background: white;
            border-radius: 20px;
            box-shadow: 0 4px 20px rgba(233, 79, 100, 0.15);
            overflow: hidden;
            margin-bottom: 2rem;
        }
        
        .flashcard-header {
            background: linear-gradient(135deg, #e94f64, #ff6b9d);
            color: white;
            padding: 2rem;
            text-align: center;
        }
        
        .flashcard-header h1 {
            margin-bottom: 0.5rem;
            font-weight: 700;
        }
        
        .flashcard-header p {
            margin-bottom: 0;
            opacity: 0.9;
        }
        
        .flashcard-controls {
            background: #f8f9fa;
            padding: 1.5rem;
            border-bottom: 1px solid #dee2e6;
        }
        
        .progress-container {
            background: white;
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 1rem;
        }
        
        .progress-bar {
            background: #e94f64;
            border-radius: 10px;
            height: 8px;
        }
        
        .card-display {
            padding: 3rem;
            text-align: center;
            min-height: 400px;
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
        }
        
        .flashcard {
            width: 100%;
            max-width: 500px;
            height: 300px;
            perspective: 1000px;
            cursor: pointer;
        }
        
        .flashcard-inner {
            position: relative;
            width: 100%;
            height: 100%;
            text-align: center;
            transition: transform 0.8s;
            transform-style: preserve-3d;
            border-radius: 15px;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }
        
        .flashcard.flipped .flashcard-inner {
            transform: rotateY(180deg);
        }
        
        .flashcard-front, .flashcard-back {
            position: absolute;
            width: 100%;
            height: 100%;
            backface-visibility: hidden;
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
            font-size: 1.5rem;
            font-weight: 600;
            color: white;
        }
        
        .flashcard-front {
            background: linear-gradient(135deg, #e94f64, #ff6b9d);
        }
        
        .flashcard-back {
            background: linear-gradient(135deg, #17a2b8, #20c997);
            transform: rotateY(180deg);
        }
        
        .flashcard-image {
            max-width: 100%;
            max-height: 100%;
            border-radius: 10px;
            object-fit: cover;
        }
        
        .navigation-buttons {
            display: flex;
            justify-content: center;
            gap: 1rem;
            margin-top: 2rem;
        }
        
        .nav-btn {
            background: #e94f64;
            color: white;
            border: none;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.2rem;
            transition: all 0.3s ease;
        }
        
        .nav-btn:hover {
            background: #d43e55;
            transform: scale(1.1);
        }
        
        .nav-btn:disabled {
            background: #6c757d;
            cursor: not-allowed;
            transform: none;
        }
        
        .study-mode-toggle {
            background: #28a745;
            color: white;
            border: none;
            border-radius: 25px;
            padding: 0.75rem 1.5rem;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .study-mode-toggle:hover {
            background: #218838;
            transform: translateY(-2px);
        }
        
        .study-mode-toggle.active {
            background: #dc3545;
        }
        
        .study-mode-toggle.active:hover {
            background: #c82333;
        }
        
        .btn-outline-primary {
            border-color: #e94f64;
            color: #e94f64;
            background: transparent;
            border-radius: 25px;
            padding: 0.75rem 1.5rem;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .btn-outline-primary:hover {
            background: #e94f64;
            border-color: #e94f64;
            color: white;
            transform: translateY(-2px);
        }
        
        .card-info {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 15px;
            margin-bottom: 2rem;
        }
        
        .card-info h3 {
            color: #e94f64;
            margin-bottom: 1rem;
        }
        
        .info-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 0.5rem;
            padding: 0.5rem 0;
            border-bottom: 1px solid #dee2e6;
        }
        
        .info-item:last-child {
            border-bottom: none;
        }
        
        .info-label {
            font-weight: 600;
            color: #333;
        }
        
        .info-value {
            color: #6c757d;
        }
        
        .note-section {
            background: white;
            border-radius: 15px;
            padding: 1.5rem;
            margin-top: 2rem;
            border-left: 4px solid #e94f64;
        }
        
        .note-section h4 {
            color: #e94f64;
            margin-bottom: 1rem;
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 4rem;
            color: #e94f64;
            margin-bottom: 1rem;
        }
        .navigation-buttons {
            display: flex;
            gap: 1.5rem;
        }
        .nav-btn {
            background: #e94f64;
            color: white;
            border: none;
            border-radius: 50%;
            width: 56px;
            height: 56px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
            transition: all 0.2s;
            box-shadow: 0 2px 8px rgba(233,79,100,0.08);
        }
        .nav-btn:disabled {
            background: #bdbdbd;
            color: #fff;
            cursor: not-allowed;
        }
        .nav-btn:hover:not(:disabled) {
            background: #ff6b9d;
            transform: scale(1.12);
            box-shadow: 0 4px 16px rgba(233,79,100,0.18);
        }
        .btn-back-flashcard {
            background: linear-gradient(135deg, #e94f64, #ff6b9d) !important;
            color: #fff !important;
            border: none;
            border-radius: 12px;
            font-weight: 600;
            font-size: 1.15rem;
            padding: 0.75rem 2rem;
            box-shadow: 0 2px 8px rgba(233,79,100,0.08);
            transition: background 0.2s, box-shadow 0.2s;
        }
        .btn-back-flashcard:hover, .btn-back-flashcard:focus {
            background: linear-gradient(135deg, #ff6b9d, #e94f64) !important;
            color: #fff !important;
            box-shadow: 0 4px 16px rgba(233,79,100,0.18);
        }
        .back-link-flashcard {
            background: none !important;
            color: #fff !important;
            border: none;
            border-radius: 0;
            font-weight: 600;
            font-size: 1.15rem;
            padding: 0.75rem 2rem 0.75rem 0.5rem;
            box-shadow: none;
            transition: color 0.2s;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }
        .back-link-flashcard:hover, .back-link-flashcard:focus {
            color: #fff !important;
            text-decoration: underline;
        }
        .back-link-icon {
            padding-right: 0.5rem;
            font-size: 1.2em;
            display: inline-block;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <%@ include file="Home/nav.jsp" %>

    <!-- Main Content -->
    <div class="container mt-4">
        <!-- Error Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle"></i>
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Flashcard Viewer -->
        <div class="flashcard-viewer position-relative">
            <!-- Nút back ở góc trái -->
            <a href="<c:url value='/flashcard'/>" class="back-link-flashcard position-absolute" style="top: 24px; left: 24px; z-index: 10;">
                <i class="fas fa-arrow-left back-link-icon"></i>
                Quay lại danh sách
            </a>
            <!-- Header -->
            <div class="flashcard-header text-center">
                <h1>${flashcard.title}</h1>
                <c:if test="${not empty flashcard.description}">
                    <p>${flashcard.description}</p>
                </c:if>
            </div>

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
                        <div class="flashcard" id="flashcard" onclick="flipCard()">
                            <div class="flashcard-inner">
                                <div class="flashcard-front" id="cardFront">
                                    <div id="frontContent"></div>
                                </div>
                                <div class="flashcard-back" id="cardBack">
                                    <div id="backContent"></div>
                                </div>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <!-- Đưa navigation-buttons xuống dưới, căn giữa, thêm margin -->
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
                        <!-- Debug info -->
                        <small class="text-muted d-block">
                            Debug: publicFlag = ${flashcard.publicFlag}
                        </small>
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
        const flashcards = [
            <c:forEach var="item" items="${items}" varStatus="status">
                {
                    id: ${item.flashcardItemID},
                    front: '${item.frontContent != null ? item.frontContent.replace("'", "\\'") : ""}',
                    back: '${item.backContent != null ? item.backContent.replace("'", "\\'") : ""}',
                    frontImage: ${item.frontImage != null ? "'" + item.frontImage + "'" : "null"},
                    backImage: ${item.backImage != null ? "'" + item.backImage + "'" : "null"},
                    note: '${item.note != null ? item.note.replace("'", "\\'") : ""}',
                    orderIndex: ${item.orderIndex}
                }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];

        let currentIndex = 0;
        let isStudyMode = false;
        let studyOrder = [];
        let originalOrder = [...Array(flashcards.length).keys()]; // Lưu thứ tự gốc

        // Initialize
        document.addEventListener('DOMContentLoaded', function() {
            if (flashcards.length > 0) {
                console.log('[FlashcardViewer] Flashcards loaded:', flashcards.length, 'items');
                console.log('[FlashcardViewer] Original order:', originalOrder);
                console.log('[FlashcardViewer] Flashcards array:', flashcards);
                updateCard();
                updateProgress();
            }
        });

        function getRealIndex() {
            // Nếu đang ở study mode (random), lấy index thực tế từ studyOrder
            return isStudyMode ? studyOrder[currentIndex] : currentIndex;
        }

        function updateCard() {
            const realIndex = getRealIndex();
            const card = flashcards[realIndex];
            console.log('[FlashcardViewer] updateCard - currentIndex:', currentIndex, 'realIndex:', realIndex, 'card:', card);
            console.log('[FlashcardViewer] FrontImage value:', card.frontImage, 'type:', typeof card.frontImage);
            console.log('[FlashcardViewer] BackImage value:', card.backImage, 'type:', typeof card.backImage);
            
            const frontContent = document.getElementById('frontContent');
            const backContent = document.getElementById('backContent');
            const noteContent = document.getElementById('noteContent');
            const noteSection = document.getElementById('noteSection');

            // Update front content - ưu tiên ảnh nếu có
            if (card.frontImage && card.frontImage !== null && card.frontImage.toString().trim() !== '') {
                frontContent.innerHTML = `<img src="${card.frontImage}" alt="Front" class="flashcard-image">`;
                console.log('[FlashcardViewer] Front image displayed:', card.frontImage);
            } else {
                frontContent.textContent = card.front || 'Không có nội dung';
                console.log('[FlashcardViewer] Front text displayed:', card.front);
            }

            // Update back content - ưu tiên ảnh nếu có
            if (card.backImage && card.backImage !== null && card.backImage.toString().trim() !== '') {
                backContent.innerHTML = `<img src="${card.backImage}" alt="Back" class="flashcard-image">`;
                console.log('[FlashcardViewer] Back image displayed:', card.backImage);
            } else {
                backContent.textContent = card.back || 'Không có nội dung';
                console.log('[FlashcardViewer] Back text displayed:', card.back);
            }

            // Update note
            if (card.note && card.note.trim() !== '') {
                noteContent.textContent = card.note;
                noteSection.style.display = 'block';
                console.log('[FlashcardViewer] Note displayed:', card.note);
            } else {
                noteSection.style.display = 'none';
            }

            // Update card info với OrderIndex
            const currentCardInfo = document.getElementById('currentCardInfo');
            if (currentCardInfo) {
                currentCardInfo.textContent = `${currentIndex + 1} / ${flashcards.length} (OrderIndex: ${card.orderIndex})`;
                console.log('[FlashcardViewer] currentCardInfo updated:', `${currentIndex + 1} / ${flashcards.length} (OrderIndex: ${card.orderIndex})`);
            }

            // Update navigation buttons
            document.getElementById('prevBtn').disabled = currentIndex === 0;
            document.getElementById('nextBtn').disabled = currentIndex === flashcards.length - 1;

            // Reset card to front
            document.getElementById('flashcard').classList.remove('flipped');
        }

        function flipCard() {
            document.getElementById('flashcard').classList.toggle('flipped');
        }

        function nextCard() {
            if (currentIndex < flashcards.length - 1) {
                currentIndex++;
                updateCard();
                updateProgress();
            }
        }

        function previousCard() {
            if (currentIndex > 0) {
                currentIndex--;
                updateCard();
                updateProgress();
            }
        }

        function updateProgress() {
            const progress = ((currentIndex + 1) / flashcards.length) * 100;
            document.getElementById('progressBar').style.width = progress + '%';
            document.getElementById('progressText').textContent = `${currentIndex + 1} / ${flashcards.length}`;
        }

        // Study mode toggle
        document.getElementById('studyModeToggle').addEventListener('click', function() {
            isStudyMode = !isStudyMode;
            const button = this;
            
            if (isStudyMode) {
                // Generate random order
                studyOrder = [...Array(flashcards.length).keys()];
                for (let i = studyOrder.length - 1; i > 0; i--) {
                    const j = Math.floor(Math.random() * (i + 1));
                    [studyOrder[i], studyOrder[j]] = [studyOrder[j], studyOrder[i]];
                }
                
                button.innerHTML = '<i class="fas fa-list"></i> Chế độ tuần tự';
                button.classList.add('active');
                
                // Set current index to first in study order
                currentIndex = 0;
                console.log('[FlashcardViewer] Study mode activated, random order:', studyOrder);
            } else {
                button.innerHTML = '<i class="fas fa-random"></i> Chế độ học tập';
                button.classList.remove('active');
                
                // Reset to original order
                currentIndex = 0;
                console.log('[FlashcardViewer] Study mode deactivated, original order restored');
            }
            
            updateCard();
            updateProgress();
        });

        // Reset to original order
        function resetToOriginalOrder() {
            isStudyMode = false;
            studyOrder = [...originalOrder];
            currentIndex = 0;
            
            const button = document.getElementById('studyModeToggle');
            button.innerHTML = '<i class="fas fa-random"></i> Chế độ học tập';
            button.classList.remove('active');
            
            console.log('[FlashcardViewer] Reset to original order:', originalOrder);
            updateCard();
            updateProgress();
        }

        // Keyboard navigation
        document.addEventListener('keydown', function(e) {
            switch(e.key) {
                case 'ArrowLeft':
                    if (currentIndex > 0) {
                        previousCard();
                    }
                    break;
                case 'ArrowRight':
                    if (currentIndex < flashcards.length - 1) {
                        nextCard();
                    }
                    break;
                case ' ':
                    e.preventDefault();
                    flipCard();
                    break;
            }
        });
    </script>
</body>
</html> 