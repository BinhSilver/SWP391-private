<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.Lesson, model.LessonMaterial, model.QuizQuestion, model.Vocabulary" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Bài học - ${lesson.title}</title>

        <link rel="stylesheet" href="<c:url value='/css/study.css'/>">

        <script src="https://code.responsivevoice.org/responsivevoice.js?key=YC77U5uD"></script>
        <!-- CSS & Fonts -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/stylechat.css'/>">
    </head>
    <body>
        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <div class="lesson-main">
                <!-- Sidebar -->
                <div class="lesson-sidebar" id="lessonSidebar">
                    <h5 class="text-dark">📚 Danh sách bài học</h5>
                    <ul class="list-group">
                        <c:forEach var="l" items="${lessons}">
                            <li class="list-group-item">
                                <a href="StudyLessonServlet?lessonId=${l.lessonID}&courseId=${lesson.courseID}"
                                   class="text-decoration-none ${l.lessonID == lesson.lessonID ? 'fw-bold' : ''}">
                                    ${l.title}
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
                <!-- Content -->
                <div class="lesson-content" id="lessonContent">
                    <button id="toggleSidebar" class="sidebar-toggle-btn">
                        <i class="fa-solid fa-caret-left"></i>
                    </button>
                    <h2>${lesson.title}</h2>
                    <p class="lead">${lesson.description}</p>

                    <!-- Tabs -->
                    <ul class="nav lesson-tabs" role="tablist">
                        <li class="nav-item"><a class="nav-link active" data-bs-toggle="tab" href="#grammar">Ngữ pháp</a></li>
                        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#vocab">Từ vựng</a></li>
                        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#kanji">Kanji</a></li>
                        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#quiz">Quiz</a></li>
                    </ul>

                    <div class="tab-content">
                        <!-- Ngữ pháp -->
                        <div class="tab-pane fade show active" id="grammar">
                            <h4 class="section-title">🧠 Ngữ pháp</h4>
                            <div class="row">
                                <div class="col-md-6 grammar-video-section">
                                    <h5>🎬 Video bài học</h5>
                                    <c:forEach var="m" items="${materials}">
                                        <c:if test="${m.materialType eq 'Ngữ pháp' && m.fileType eq 'Video'}">
                                            <div class="material-item mb-3">
                                                <strong>${m.title}</strong>
                                                <div class="file-viewer position-relative">
                                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                    <video controls>
                                                        <source src="${pageContext.request.contextPath}/${m.filePath}" type="video/mp4">
                                                    </video>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>
                                <div class="col-md-6 grammar-pdf-section">
                                    <h5>📄 Tài liệu PDF</h5>
                                    <c:forEach var="m" items="${materials}">
                                        <c:if test="${m.materialType eq 'Ngữ pháp' && m.fileType eq 'PDF'}">
                                            <div class="material-item mb-3">
                                                <strong>${m.title}</strong>
                                                <div class="file-viewer position-relative">
                                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                    <iframe src="${pageContext.request.contextPath}/${m.filePath}"></iframe>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>

                        <!-- Từ vựng -->
                        <div class="tab-pane fade" id="vocab">
                            <h4 class="section-title">📖 Từ vựng</h4>
                            <div class="vocab-container">
                                <div class="vocab-slideshow">
                                    <c:forEach var="vocab" items="${vocabulary}" varStatus="loop">
                                        <div class="vocab-item ${loop.index == 0 ? 'active' : ''}">
                                            <div class="vocab-header">こんにちは 今日は</div>
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
                                <c:forEach var="m" items="${materials}">
                                    <c:if test="${m.materialType eq 'Từ vựng' && m.fileType eq 'PDF'}">
                                        <div class="material-item mb-3">
                                            <p>Sau đây là tài liệu PDF tham khảo cho:</p>
                                            <strong>${m.title}</strong>
                                            <div class="file-viewer position-relative">
                                                <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                <iframe src="${pageContext.request.contextPath}/${m.filePath}"></iframe>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </div>

                        <!-- Kanji -->
                        <div class="tab-pane fade" id="kanji">
                            <h4 class="section-title">🈶 Kanji</h4>
                            <c:forEach var="m" items="${materials}">
                                <c:if test="${m.materialType eq 'Kanji'}">
                                    <div class="material-item mb-3">
                                        <strong>${m.title}</strong>
                                        <div class="file-viewer position-relative">
                                            <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                            <iframe src="${pageContext.request.contextPath}/${m.filePath}"></iframe>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>

                        <!-- Quiz -->
                        <div class="tab-pane fade" id="quiz">
                            <c:if test="${not empty quiz && fn:length(quiz) > 0}">
                                <div class="quiz-section">
                                    <h4>📝 Quiz</h4>
                                    <a href="doQuiz?lessonId=${lesson.lessonID}" class="btn">Làm Quiz</a>
                                </div>
                            </c:if>
                            <c:if test="${empty quiz}">
                                <p class="text-muted">Chưa có bài kiểm tra cho bài học này.</p>
                            </c:if>
                        </div>
                    </div>

                    <div class="mt-4">
                        <a href="CourseDetailServlet?id=${lesson.courseID}" class="btn btn-secondary">← Quay lại khóa học</a>
                    </div>
                </div>
            </div>

            <%@ include file="../Home/footer.jsp" %>
        </div>
        <script>
            document.addEventListener('DOMContentLoaded', () => {
                document.querySelectorAll('.fullscreen-toggle').forEach(btn => {
                    btn.addEventListener('click', () => {
                        const viewer = btn.closest('.file-viewer');
                        viewer.classList.toggle('fullscreen');
                        btn.classList.toggle('fa-expand');
                        btn.classList.toggle('fa-compress');
                        btn.title = viewer.classList.contains('fullscreen') ? 'Thu nhỏ' : 'Xem toàn màn hình';
                    });
                });

                const toggleBtn = document.getElementById('toggleSidebar');
                const sidebar = document.getElementById('lessonSidebar');
                const content = document.getElementById('lessonContent');
                const icon = toggleBtn.querySelector('i');

                toggleBtn.addEventListener('click', () => {
                    sidebar.classList.toggle('collapsed');
                    content.classList.toggle('expanded');
                    icon.classList.toggle('fa-caret-left');
                    icon.classList.toggle('fa-caret-right');
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
            });
        </script>
        <!-- Scripts -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
        <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
        <script src="<c:url value='/Script/cherry-blossom.js'/>"></script>
    </body>
</html>