<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Làm Quiz</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/do-quiz.css'/>">
    </head>
    <body>

        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <div class="container py-5">
                <h3 class="mb-4 text-primary">📝 Làm bài Quiz</h3>

                <form method="post" action="doQuiz">
                    <input type="hidden" name="lessonId" value="${lessonId}"/>
                    <input type="hidden" name="courseId" value="${courseId}"/>

                    <c:forEach var="q" items="${questions}" varStatus="loop">
                        <div class="card shadow-sm mb-4">
                            <div class="card-body">
                                <p class="fw-bold mb-3">❓ Câu ${loop.index + 1}: ${q.question}</p>

                                <%
                                    String[] labels = {"A", "B", "C", "D"};
                                    pageContext.setAttribute("labels", labels);
                                %>

                                <c:forEach var="a" items="${q.answers}">
                                    <c:set var="label" value="${labels[a.answerNumber - 1]}" />
                                    <div class="form-check mb-2">
                                        <input class="form-check-input" type="radio"
                                               name="question_${q.id}"
                                               id="q${q.id}_a${a.answerNumber}"
                                               value="${a.answerNumber}" />
                                        <label class="form-check-label" for="q${q.id}_a${a.answerNumber}">
                                            <strong>${label}.</strong> ${a.answerText}
                                        </label>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>

                    <button type="submit" class="btn btn-success">✅ Nộp bài</button>
                    <a href="<c:url value='/CourseDetailServlet?id=${lesson.courseID}'/>" class="btn btn-secondary ms-2">← Quay lại khóa học</a>
                </form>
            </div>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

        <%@ include file="../Home/footer.jsp" %>

    </body>
</html>
