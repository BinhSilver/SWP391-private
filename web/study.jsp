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
    </head>
    <body>
        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <div class="lesson-main">
                <!-- Sidebar bài học -->
                <div class="lesson-sidebar">
                    <h5 class="text-dark">📚 Danh sách bài học</h5>
                    <ul class="list-group">
                        <c:forEach var="l" items="${sessionScope.accessedLessons}">
                            <li class="list-group-item">
                                <a href="StudyLessonServlet?lessonId=${l.lessonID}&courseId=${lesson.courseID}"
                                   class="text-decoration-none ${l.lessonID == lesson.lessonID ? 'fw-bold text-primary' : ''}">
                                    ${l.title}
                                </a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>

                <!-- Nội dung bài học -->
                <div class="lesson-content">
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
                        <!-- 🧠 Ngữ pháp -->
                        <div class="tab-pane fade show active" id="grammar">
                            <h4 class="section-title">🧠 Ngữ pháp</h4>

                            <!-- VIDEO trước -->
                            <c:forEach var="m" items="${materials}">
                                <c:if test="${m.materialType eq 'Ngữ pháp' and m.fileType eq 'Video'}">
                                    <div class="material-item">
                                        <strong>${m.title}</strong>
                                        <div class="file-viewer">
                                            <video controls>
                                                <source src="${m.filePath}" type="video/mp4">
                                                Trình duyệt của bạn không hỗ trợ video.
                                            </video>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>

                            <!-- PDF sau -->
                            <c:forEach var="m" items="${materials}">
                                <c:if test="${m.materialType eq 'Ngữ pháp' and m.fileType eq 'PDF'}">
                                    <div class="material-item">
                                        <strong>${m.title}</strong>
                                        <div class="file-viewer">
                                            <iframe src="${m.filePath}"></iframe>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>

                        <!-- 📄 Từ vựng -->
                        <div class="tab-pane fade" id="vocab">
                            <h4 class="section-title">📄 Từ vựng</h4>
                            <c:forEach var="m" items="${materials}">
                                <c:if test="${m.materialType eq 'Từ vựng'}">
                                    <div class="material-item">
                                        <strong>${m.title}</strong>
                                        <c:if test="${m.fileType eq 'PDF'}">
                                            <div class="file-viewer">
                                                <iframe src="${m.filePath}"></iframe>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>

                        <!-- 🈶 Kanji -->
                        <div class="tab-pane fade" id="kanji">
                            <h4 class="section-title">🈶 Kanji</h4>
                            <c:forEach var="m" items="${materials}">
                                <c:if test="${m.materialType eq 'Kanji'}">
                                    <div class="material-item">
                                        <strong>${m.title}</strong>
                                        <c:if test="${m.fileType eq 'PDF'}">
                                            <div class="file-viewer">
                                                <iframe src="${m.filePath}"></iframe>
                                            </div>
                                        </c:if>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>

                        <!-- 📝 Quiz -->
                        <div class="tab-pane fade" id="quiz">
                            <c:if test="${not empty quiz and fn:length(quiz) > 0}">
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
    </body>
</html>
