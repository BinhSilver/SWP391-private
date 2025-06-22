<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="model.Course" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết khóa học</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/course-detail.css'/>" />
    </head>
    <body>
        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <section class="container py-5">
                <c:choose>
                    <c:when test="${not empty course}">
                        <div class="card shadow-sm p-4">
                            <h2 class="text-primary mb-3">${course.title}</h2>
                            <p class="lead">${course.description}</p>
                            <p><strong>Trạng thái:</strong>
                                <c:choose>
                                    <c:when test="${course.hidden}">Ẩn</c:when>
                                    <c:otherwise>Hiển thị</c:otherwise>
                                </c:choose>
                            </p>
                            <p><strong>Gợi ý:</strong>
                                <c:choose>
                                    <c:when test="${course.suggested}">✔ Có đề xuất</c:when>
                                    <c:otherwise>✖ Không</c:otherwise>
                                </c:choose>
                            </p>

                            <!-- Nút hành động -->
                            <div class="mt-3">
                                <c:choose>
                                    <c:when test="${currentUser == null}">
                                        <a href="LoginServlet" class="btn btn-primary">Vào học</a>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="StudyLessonServlet" method="get" class="d-inline">
                                            <input type="hidden" name="courseId" value="${course.courseID}" />
                                            <input type="hidden" name="lessonId" value="${lessons[0].lessonID}" />
                                            <button type="submit" class="btn btn-success">Vào học</button>
                                        </form>

                                    </c:otherwise>
                                </c:choose>
                                <a href="HomeServlet" class="btn btn-secondary ms-2">← Quay lại</a>
                            </div>
                        </div>

                        <hr class="my-4">
                        <h4 class="text-dark">📘 Danh sách bài học</h4>

                        <c:forEach var="lesson" items="${lessons}">
                            <div class="accordion my-3" id="accordion-${lesson.lessonID}">
                                <div class="accordion-item">
                                    <h2 class="accordion-header" id="heading-${lesson.lessonID}">
                                        <button class="accordion-button collapsed" type="button"
                                                data-bs-toggle="collapse"
                                                data-bs-target="#collapse-${lesson.lessonID}"
                                                aria-expanded="false" aria-controls="collapse-${lesson.lessonID}">
                                            📖 ${lesson.title}
                                        </button>
                                    </h2>
                                    <div id="collapse-${lesson.lessonID}" class="accordion-collapse collapse"
                                         aria-labelledby="heading-${lesson.lessonID}" data-bs-parent="#accordion-${lesson.lessonID}">
                                        <div class="accordion-body">
                                            <c:choose>
                                                <c:when test="${not empty accessedLessons && accessedLessons.contains(lesson.lessonID)}">
                                                    <!-- Nội dung bài học (nếu đã vào học) -->
                                                    <h6 class="mt-2">📖 Từ vựng:</h6>
                                                    <c:set var="hasVocab" value="false" />
                                                    <c:forEach var="material" items="${lessonMaterialsMap[lesson.lessonID]}">
                                                        <c:if test="${material.materialType eq 'Từ vựng'}">
                                                            <c:set var="hasVocab" value="true" />
                                                            <div class="ms-3 mb-2">
                                                                • 📄 <strong>${material.title}</strong>
                                                                <c:if test="${not empty material.filePath}">
                                                                    <a href="${material.filePath}" target="_blank" class="btn btn-sm btn-outline-success ms-2">Xem</a>
                                                                </c:if>
                                                            </div>
                                                        </c:if>
                                                    </c:forEach>
                                                    <c:if test="${not hasVocab}">
                                                        <p class="ms-3 text-muted">Chưa có tài liệu từ vựng.</p>
                                                    </c:if>

                                                    <h6 class="mt-3">🈶 Kanji:</h6>
                                                    <c:set var="hasKanji" value="false" />
                                                    <c:forEach var="material" items="${lessonMaterialsMap[lesson.lessonID]}">
                                                        <c:if test="${material.materialType eq 'Kanji'}">
                                                            <c:set var="hasKanji" value="true" />
                                                            <div class="ms-3 mb-2">
                                                                • 📄 <strong>${material.title}</strong>
                                                                <c:if test="${not empty material.filePath}">
                                                                    <a href="${material.filePath}" target="_blank" class="btn btn-sm btn-outline-success ms-2">Xem</a>
                                                                </c:if>
                                                            </div>
                                                        </c:if>
                                                    </c:forEach>
                                                    <c:if test="${not hasKanji}">
                                                        <p class="ms-3 text-muted">Chưa có tài liệu kanji.</p>
                                                    </c:if>

                                                    <h6 class="mt-3">🧠 Ngữ pháp:</h6>
                                                    <c:set var="hasGrammar" value="false" />
                                                    <c:forEach var="material" items="${lessonMaterialsMap[lesson.lessonID]}">
                                                        <c:if test="${material.materialType eq 'Ngữ pháp'}">
                                                            <c:set var="hasGrammar" value="true" />
                                                            <div class="ms-3 mb-2">
                                                                • <c:choose>
                                                                    <c:when test="${material.fileType eq 'PDF'}">📄</c:when>
                                                                    <c:when test="${material.fileType eq 'Video'}">🎬</c:when>
                                                                    <c:otherwise>📁</c:otherwise>
                                                                </c:choose>
                                                                <strong>${material.title}</strong>
                                                                <c:if test="${not empty material.filePath}">
                                                                    <a href="${material.filePath}" target="_blank" class="btn btn-sm btn-outline-success ms-2">Xem</a>
                                                                </c:if>
                                                            </div>
                                                        </c:if>
                                                    </c:forEach>
                                                    <c:if test="${not hasGrammar}">
                                                        <p class="ms-3 text-muted">Chưa có tài liệu ngữ pháp.</p>
                                                    </c:if>

                                                    <h6 class="mt-3">📝 Quiz:</h6>
                                                    <p class="text-muted ms-2">Số câu hỏi: ${fn:length(quizMap[lesson.lessonID])}</p>
                                                    <c:if test="${not empty quizMap[lesson.lessonID]}">
                                                        <a href="doQuiz?lessonId=${lesson.lessonID}" class="btn btn-sm btn-primary mb-3">Làm Quiz</a>
                                                    </c:if>

                                                    <c:forEach var="question" items="${quizMap[lesson.lessonID]}">
                                                        <div class="ms-3">
                                                            <p><strong>❓ ${question.question}</strong></p>
                                                            <% String[] labels = {"A", "B", "C", "D"};
                                                        pageContext.setAttribute("labels", labels);%>
                                                            <ul>
                                                                <c:forEach var="answer" items="${question.answers}">
                                                                    <c:set var="label" value="${labels[answer.answerNumber - 1]}" />
                                                                    <li>
                                                                        <c:choose>
                                                                            <c:when test="${answer.answerNumber == question.correctAnswer}">
                                                                                ✅ <strong>${label}. ${answer.answerText}</strong>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                ${label}. ${answer.answerText}
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </li>
                                                                </c:forEach>
                                                            </ul>
                                                        </div>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <p class="text-muted">⚠ Bạn cần bấm nút "Vào học" để xem nội dung bài học này.</p>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-danger">Không tìm thấy khóa học.</div>
                    </c:otherwise>
                </c:choose>
            </section>

            <%@ include file="../Home/footer.jsp" %>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
