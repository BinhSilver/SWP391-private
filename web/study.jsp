<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.Lesson, model.LessonMaterial, model.QuizQuestion" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Bài học - ${lesson.title}</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/study.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
             <script src="https://code.responsivevoice.org/responsivevoice.js?key=YC77U5uD"></script>
    </head>
    <body>
        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <div class="custom-layout">
                <!-- MAIN CONTENT -->
                <main class="main-study-area" id="mainContent">
                    <div class="lesson-header">
                        <h2><i class="fas fa-graduation-cap me-3"></i>${lesson.title}</h2>
                        <p class="lead"><i class="fas fa-info-circle me-2"></i>${lesson.description}</p>
                    </div>

                    <!-- VIDEO -->
                    <c:set var="showedVideo" value="false" />
                    <c:forEach var="m" items="${materials}">
                        <c:if test="${m.fileType eq 'Video' && !showedVideo}">
                            <div class="material-item">
                                <h5><i class="fas fa-play-circle me-2"></i>Video bài học</h5>
                                <strong><i class="fas fa-film me-2"></i>${m.title}</strong>
                                <div class="file-viewer position-relative">
                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                    <video controls preload="metadata">
                                        <source src="${pageContext.request.contextPath}/${m.filePath}" type="video/mp4">
                                        Trình duyệt của bạn không hỗ trợ video.
                                    </video>
                                </div>
                            </div>
                            <c:set var="showedVideo" value="true" />
                        </c:if>
                    </c:forEach>

                    <!-- Tabs -->
                    <div class="tabs-container">
                        <ul class="nav lesson-tabs" role="tablist">
                            <li class="nav-item">
                                <a class="nav-link active" data-bs-toggle="tab" href="#grammar">
                                    <i class="fas fa-brain me-2"></i>Ngữ pháp
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" data-bs-toggle="tab" href="#vocab">
                                    <i class="fas fa-book me-2"></i>Từ vựng
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" data-bs-toggle="tab" href="#kanji">
                                    <i class="fas fa-language me-2"></i>Kanji
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" data-bs-toggle="tab" href="#quiz">
                                    <i class="fas fa-question-circle me-2"></i>Quiz
                                </a>
                            </li>
                        </ul>

                        <div class="tab-content mt-4">
                            <!-- NGỮ PHÁP -->
                            <div class="tab-pane fade show active" id="grammar">
                                <div class="section-header">
                                    <h4><i class="fas fa-brain me-2"></i>Ngữ pháp</h4>
                                    <p class="section-description">Học các quy tắc ngữ pháp quan trọng trong bài học này</p>
                                </div>
                                <c:forEach var="m" items="${materials}">
                                    <c:if test="${m.materialType eq 'Ngữ pháp' && m.fileType eq 'PDF'}">
                                        <div class="material-item">
                                            <h5><i class="fas fa-file-pdf me-2"></i>Tài liệu ngữ pháp</h5>
                                            <strong><i class="fas fa-document me-2"></i>${m.title}</strong>
                                            <div class="file-viewer position-relative">
                                                <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                <iframe src="${pageContext.request.contextPath}/${m.filePath}" title="Tài liệu ngữ pháp"></iframe>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>

                            <!-- TỪ VỰNG -->
                            <div class="tab-pane fade" id="vocab">
                                <div class="section-header">
                                    <h4><i class="fas fa-book me-2"></i>Từ vựng</h4>
                                    <p class="section-description">Mở rộng vốn từ vựng với các từ mới trong bài học</p>
                                </div>
                                <div class="vocab-container">
                                    <div class="vocab-slideshow">
                                        <c:forEach var="vocab" items="${vocabulary}" varStatus="loop">
                                            <div class="vocab-item ${loop.index == 0 ? 'active' : ''}">
                                            
                                                <div class="vocab-body">
                                                    <div class="vocab-text">
                                                        <div class="vocab-languages">
                                                            <div>từ vựng</div>
                                                            <div>${vocab.word}</div>
                                                            <div>nghĩa</div>
                                                            <div>${vocab.meaning}</div>
                                                        </div>
                                                        <div class="vocab-examples">
                                                            <div>ví dụ</div>
                                                            <div>${vocab.reading != null ? vocab.reading : ''}</div>
                                                            <div>${vocab.example != null ? vocab.example : ''}</div>
                                                        </div>
                                                        <button class="play-btn" data-word="${vocab.word}"><i class="fa-solid fa-volume-up"></i> Phát phát âm</button>
                                                    </div>
                                                    <c:if test="${not empty vocab.imagePath}">
                                                        <img src="${pageContext.request.contextPath}/imgvocab/${vocab.imagePath}" alt="${vocab.word}" class="vocab-image">
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <div class="vocab-nav">
                                        <button class="vocab-prev-btn"><i class="fa-solid fa-chevron-left"></i></button>
                                        <button class="vocab-next-btn"><i class="fa-solid fa-chevron-right"></i></button>
                                    </div>
                                      </div>
                                    <c:forEach var="m" items="${materials}">
                                        <c:if test="${m.materialType eq 'Từ vựng'}">
                                            <div class="material-item">
                                                <h5><i class="fas fa-file-pdf me-2"></i>Tài liệu từ vựng</h5>
                                                <strong><i class="fas fa-document me-2"></i>${m.title}</strong>
                                                <div class="file-viewer position-relative">
                                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                    <iframe src="${pageContext.request.contextPath}/${m.filePath}" title="Tài liệu từ vựng"></iframe>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <!-- KANJI -->
                                <div class="tab-pane fade" id="kanji">
                                    <div class="section-header">
                                        <h4><i class="fas fa-language me-2"></i>Kanji</h4>
                                        <p class="section-description">Học cách viết và ý nghĩa của các ký tự Kanji</p>
                                    </div>
                                    <c:forEach var="m" items="${materials}">
                                        <c:if test="${m.materialType eq 'Kanji'}">
                                            <div class="material-item">
                                                <h5><i class="fas fa-file-pdf me-2"></i>Tài liệu Kanji</h5>
                                                <strong><i class="fas fa-document me-2"></i>${m.title}</strong>
                                                <div class="file-viewer position-relative">
                                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                    <iframe src="${pageContext.request.contextPath}/${m.filePath}" title="Tài liệu Kanji"></iframe>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <!-- QUIZ -->
                                <div class="tab-pane fade" id="quiz">
                                    <div class="section-header">
                                        <h4><i class="fas fa-question-circle me-2"></i>Quiz</h4>
                                        <p class="section-description">Kiểm tra kiến thức của bạn với các câu hỏi trắc nghiệm</p>
                                    </div>
                                    <c:choose>
                                        <c:when test="${not empty quiz}">
                                            <div class="quiz-section">
                                                <div class="quiz-info">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <div class="quiz-stat-card">
                                                                <div class="stat-icon">
                                                                    <i class="fas fa-list-ol"></i>
                                                                </div>
                                                                <div class="stat-content">
                                                                    <h5>Số câu hỏi</h5>
                                                                    <p class="stat-number">${fn:length(quiz)}</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <div class="col-md-6">
                                                            <div class="quiz-stat-card">
                                                                <div class="stat-icon">
                                                                    <i class="fas fa-clock"></i>
                                                                </div>
                                                                <div class="stat-content">
                                                                    <h5>Thời gian TB</h5>
                                                                    <c:set var="totalTime" value="0" />
                                                                    <c:forEach var="q" items="${quiz}">
                                                                        <c:set var="totalTime" value="${totalTime + q.timeLimit}" />
                                                                    </c:forEach>
                                                                    <p class="stat-number">${totalTime / fn:length(quiz)}s/câu</p>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="quiz-preview">
                                                    <h5><i class="fas fa-eye me-2"></i>Xem trước câu hỏi</h5>
                                                    <div class="quiz-questions-preview">
                                                        <c:forEach var="q" items="${quiz}" varStatus="loop">
                                                            <div class="question-preview-item">
                                                                <div class="question-number">${loop.index + 1}</div>
                                                                <div class="question-text">${q.question}</div>
                                                                <div class="question-time">${q.timeLimit}s</div>
                                                            </div>
                                                        </c:forEach>
                                                    </div>
                                                </div>

                                                <div class="quiz-actions">
                                                    <a href="doQuiz?lessonId=${lesson.lessonID}" class="btn btn-primary">
                                                        <i class="fas fa-play me-2"></i>Bắt đầu làm Quiz
                                                    </a>
                                                </div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="text-muted">
                                                <div class="no-quiz-content">
                                                    <i class="fas fa-exclamation-triangle no-quiz-icon"></i>
                                                    <h5>Chưa có quiz</h5>
                                                    <p>Quiz cho bài học này đang được chuẩn bị. Vui lòng quay lại sau!</p>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>

                        <div class="lesson-footer mt-5">
                            <a href="CourseDetailServlet?id=${lesson.courseID}" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-2"></i>Quay lại khóa học
                            </a>
                        </div>
                </main>

                <!-- SIDEBAR -->
                <aside class="study-sidebar" id="lessonSidebar">
                    <button id="hideSidebarBtn" class="sidebar-toggle-btn" type="button" title="Ẩn sidebar">
                        <i class="fa-solid fa-chevron-right"></i>
                    </button>
                    <div class="sidebar-inner">
                        <h5><i class="fas fa-list me-2"></i>Danh sách bài học</h5>
                        <ul class="list-group">
                            <c:forEach var="l" items="${lessons}">
                                <li class="list-group-item">
                                    <a href="StudyLessonServlet?lessonId=${l.lessonID}&courseId=${lesson.courseID}"
                                       class="text-decoration-none ${l.lessonID == lesson.lessonID ? 'fw-bold' : ''}">
                                        <i class="fas fa-play-circle me-2"></i>${l.title}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </aside>
                <button id="showSidebarBtn" class="sidebar-show-btn" type="button" style="display: none;" title="Hiện sidebar">
                    <i class="fa-solid fa-chevron-left"></i>
                </button>
            </div>

            <%@ include file="../Home/footer.jsp" %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            document.addEventListener('DOMContentLoaded', () => {
                // Fullscreen toggle functionality
                document.querySelectorAll('.fullscreen-toggle').forEach(btn => {
                    btn.addEventListener('click', () => {
                        const viewer = btn.closest('.file-viewer');
                        viewer.classList.toggle('fullscreen');
                        btn.classList.toggle('fa-expand');
                        btn.classList.toggle('fa-compress');
                        btn.title = viewer.classList.contains('fullscreen') ? 'Thu nhỏ' : 'Xem toàn màn hình';

                        // Add escape key listener for fullscreen
                        if (viewer.classList.contains('fullscreen')) {
                            const escapeHandler = (e) => {
                                if (e.key === 'Escape') {
                                    viewer.classList.remove('fullscreen');
                                    btn.classList.remove('fa-compress');
                                    btn.classList.add('fa-expand');
                                    btn.title = 'Xem toàn màn hình';
                                    document.removeEventListener('keydown', escapeHandler);
                                }
                            };
                            document.addEventListener('keydown', escapeHandler);
                        }
                    });
                });

                // Sidebar toggle functionality
                const sidebar = document.getElementById('lessonSidebar');
                const hideBtn = document.getElementById('hideSidebarBtn');
                const showBtn = document.getElementById('showSidebarBtn');
                const mainContent = document.getElementById('mainContent');

                hideBtn.addEventListener('click', () => {
                    sidebar.classList.add('collapsed');
                    mainContent.classList.add('expanded');
                    hideBtn.style.display = 'none';
                    showBtn.style.display = 'flex';
                });

                showBtn.addEventListener('click', () => {
                    sidebar.classList.remove('collapsed');
                    mainContent.classList.remove('expanded');
                    hideBtn.style.display = 'block';
                    showBtn.style.display = 'none';
                });

                // Smooth scroll for anchor links
                document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                    anchor.addEventListener('click', function (e) {
                        e.preventDefault();
                        const target = document.querySelector(this.getAttribute('href'));
                        if (target) {
                            target.scrollIntoView({
                                behavior: 'smooth',
                                block: 'start'
                            });
                        }
                    });
                });

                // Add loading animation for iframes
                document.querySelectorAll('iframe').forEach(iframe => {
                    iframe.addEventListener('load', function () {
                        this.style.opacity = '1';
                    });
                    iframe.style.opacity = '0';
                    iframe.style.transition = 'opacity 0.3s ease';
                });

                // Add hover effects for material items
                document.querySelectorAll('.material-item').forEach(item => {
                    item.addEventListener('mouseenter', function () {
                        this.style.transform = 'translateY(-4px)';
                    });

                    item.addEventListener('mouseleave', function () {
                        this.style.transform = 'translateY(0)';
                    });
                });
            });
            // Slideshow functionality
const slides = document.querySelectorAll('.vocab-item');
const prevBtn = document.querySelector('.vocab-prev-btn');
const nextBtn = document.querySelector('.vocab-next-btn');
let currentSlide = 0;

function showSlide(index) {
    slides.forEach((slide, i) => {
        slide.classList.remove('active', 'prev', 'next');
        if (i === index) {
            slide.classList.add('active');
        } else if (i < index) {
            slide.classList.add('prev');
        } else {
            slide.classList.add('next');
        }
    });
}

prevBtn.addEventListener('click', () => {
    currentSlide = (currentSlide > 0) ? currentSlide - 1 : slides.length - 1;
    showSlide(currentSlide);
});

nextBtn.addEventListener('click', () => {
    currentSlide = (currentSlide < slides.length - 1) ? currentSlide + 1 : 0;
    showSlide(currentSlide);
});

// Phát âm từ vựng với ResponsiveVoice
document.querySelectorAll('.play-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        const word = btn.getAttribute('data-word');
        if (word && responsiveVoice) {
            responsiveVoice.speak(word, "Japanese Female", {rate: 0.9});
        }
    });
});
        </script>
    </body>
</html>