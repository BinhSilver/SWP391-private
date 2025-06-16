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
                    <a href="<c:url value='/index.jsp'/>" class="btn btn-secondary mt-3">← Quay lại</a>
                </div>

                <hr class="my-4">
                <h4 class="text-dark">📘 Danh sách bài học</h4>

                <c:forEach var="lesson" items="${lessons}">
                    <div class="accordion my-3" id="accordion-${lesson.lessonID}">
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="heading-${lesson.lessonID}">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse-${lesson.lessonID}" aria-expanded="false" aria-controls="collapse-${lesson.lessonID}">
                                    📖 ${lesson.title}
                                </button>
                            </h2>
                            <div id="collapse-${lesson.lessonID}" class="accordion-collapse collapse" aria-labelledby="heading-${lesson.lessonID}" data-bs-parent="#accordion-${lesson.lessonID}">
                                <div class="accordion-body">

                                    <!-- Tài liệu -->
                                    <h6 class="mt-2">📚 Tài liệu:</h6>
                                    <c:forEach var="material" items="${lessonMaterialsMap[lesson.lessonID]}">
                                        <div class="ms-3 mb-2">
                                            • 
                                            <c:choose>
                                                <c:when test="${material.materialType eq 'Từ vựng'}">📖</c:when>
                                                <c:when test="${material.materialType eq 'Ngữ pháp'}">🧠</c:when>
                                                <c:when test="${material.materialType eq 'Kanji'}">🈶</c:when>
                                                <c:otherwise>📄</c:otherwise>
                                            </c:choose>
                                            <strong>${material.materialType}</strong> - ${material.title}
                                            <c:if test="${not empty material.filePath}">
                                                <a href="${material.filePath}" target="_blank" class="ms-2 btn btn-sm btn-outline-success">Xem</a>
                                            </c:if>
                                        </div>
                                    </c:forEach>

                                    <!-- Quiz -->
                                    <h6 class="mt-3">📝 Quiz:</h6>
                                    <p class="text-muted ms-2">Số câu hỏi: ${fn:length(quizMap[lesson.lessonID])}</p>
                                    <c:if test="${not empty quizMap[lesson.lessonID]}">
                                        <a href="#" class="btn btn-sm btn-primary mb-3">Làm Quiz</a>
                                    </c:if>

                                    <c:forEach var="question" items="${quizMap[lesson.lessonID]}">
                                        <div class="ms-3">
                                            <p><strong>❓ ${question.question}</strong></p>
                                            <ul>
                                                <c:forEach var="answer" items="${question.answers}">
                                                    <li>
                                                        <c:choose>
                                                            <c:when test="${answer.answerNumber == question.correctAnswer}">
                                                                ✅ <strong>${answer.answerText}</strong>
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${answer.answerText}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </li>
                                                </c:forEach>
                                            </ul>
                                        </div>
                                    </c:forEach>

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
