<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.Lesson, model.LessonMaterial, model.QuizQuestion" %>
<!DOCTYPE html>
<html lang="vi">
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

            <div class="custom-layout">
                <!-- MAIN CONTENT -->
                <main class="main-study-area" id="mainContent">
                    <h2>${lesson.title}</h2>
                    <p class="lead">${lesson.description}</p>

                    <!-- VIDEO -->
                    <c:set var="showedVideo" value="false" />
                    <c:forEach var="m" items="${materials}">
                        <c:if test="${m.fileType eq 'Video' && !showedVideo}">
                            <div class="material-item mb-3">
                                <h5>🎬 Video bài học</h5>
                                <strong>${m.title}</strong>
                                <div class="file-viewer position-relative">
                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                    <video controls>
                                        <source src="${pageContext.request.contextPath}/${m.filePath}" type="video/mp4">
                                    </video>
                                </div>
                            </div>
                            <c:set var="showedVideo" value="true" />
                        </c:if>
                    </c:forEach>

                    <!-- Tabs -->
                    <ul class="nav lesson-tabs" role="tablist">
                        <li class="nav-item"><a class="nav-link active" data-bs-toggle="tab" href="#grammar">Ngữ pháp</a></li>
                        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#vocab">Từ vựng</a></li>
                        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#kanji">Kanji</a></li>
                        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#quiz">Quiz</a></li>
                    </ul>

                    <div class="tab-content mt-3">
                        <!-- NGỮ PHÁP -->
                        <div class="tab-pane fade show active" id="grammar">
                            <h4>🧠 Ngữ pháp</h4>
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

                        <!-- TỪ VỰNG -->
                        <div class="tab-pane fade" id="vocab">
                            <h4>📄 Từ vựng</h4>
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

                        <!-- KANJI -->
                        <div class="tab-pane fade" id="kanji">
                            <h4>🈶 Kanji</h4>
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

                        <!-- QUIZ -->
                        <div class="tab-pane fade" id="quiz">
                            <h4>📝 Quiz</h4>
                            <c:choose>
                                <c:when test="${not empty quiz}">
                                    <div class="quiz-section">
                                        <p><strong>Số câu hỏi:</strong> ${fn:length(quiz)}</p>
                                        <p><strong>Thời gian TB:</strong> 
                                            <c:set var="totalTime" value="0" />
                                            <c:forEach var="q" items="${quiz}">
                                                <c:set var="totalTime" value="${totalTime + q.timeLimit}" />
                                            </c:forEach>
                                            ${totalTime / fn:length(quiz)}s/câu
                                        </p>
                                        <div class="quiz-preview mt-3">
                                            <h5>Xem trước câu hỏi:</h5>
                                            <c:forEach var="q" items="${quiz}" varStatus="loop">
                                                <div class="question-preview-item mb-2">
                                                    <span><strong>Câu ${loop.index + 1}:</strong> ${q.question}</span>
                                                </div>
                                            </c:forEach>
                                        </div>
                                        <a href="doQuiz?lessonId=${lesson.lessonID}" class="btn btn-primary mt-3">Làm Quiz</a>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-muted">⚠️ Chưa có quiz cho bài học này.</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div class="mt-4">
                        <a href="CourseDetailServlet?id=${lesson.courseID}" class="btn btn-secondary">← Quay lại khóa học</a>
                    </div>
                </main>

                <!-- SIDEBAR -->
                <aside class="study-sidebar" id="lessonSidebar">
                    <button id="hideSidebarBtn" class="sidebar-toggle-btn" type="button">
                        <i class="fa-solid fa-chevron-right"></i>
                    </button>
                    <div class="sidebar-inner">
                        <h5>📚 Bài học</h5>
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
                </aside>
                <button id="showSidebarBtn" class="sidebar-show-btn" type="button" style="display: none;">
                    <i class="fa-solid fa-chevron-left"></i>
                </button>
            </div>

            <%@ include file="../Home/footer.jsp" %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
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
            });
        </script>
    </body>
</html>
