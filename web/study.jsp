<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.Lesson, model.LessonMaterial, model.QuizQuestion" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Bài học</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/study.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
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
                        <!-- Grammar -->
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

                        <!-- Vocab -->
                        <div class="tab-pane fade" id="vocab">
                            <h4 class="section-title">📄 Từ vựng</h4>
                            <c:forEach var="m" items="${materials}">
                                <c:if test="${m.materialType eq 'Từ vựng'}">
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
                        </div>
                    </div>

                    <div class="mt-4">
                        <a href="CourseDetailServlet?id=${lesson.courseID}" class="btn btn-secondary">← Quay lại khóa học</a>
                    </div>
                </div>
            </div>

            <%@ include file="../Home/footer.jsp" %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Toggle fullscreen
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

                // Toggle sidebar
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
            });
        </script>
    </body>
</html>
