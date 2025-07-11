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
        <style>
            iframe.pdf-frame {
                width: 100%;
                height: 1900px;
                border: none;
                border-radius: 12px;
                display: block;
                background-color: #fff;
                box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
            }
        </style>
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

                            <div class="mt-3">
                                <c:choose>
                                    <c:when test="${currentUser == null}">
                                        <a href="LoginServlet" class="btn btn-primary">Vào học</a>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${hasAccessedCourse}">
                                            <form action="StudyLessonServlet" method="get" class="d-inline">
                                                <input type="hidden" name="courseId" value="${course.courseID}" />
                                                <input type="hidden" name="lessonId" value="${lessons[0].lessonID}" />
                                                <button type="submit" class="btn btn-success">Học tiếp</button>
                                            </form>
                                        </c:if>
                                        <c:if test="${!hasAccessedCourse}">
                                            <form action="StudyLessonServlet" method="get" class="d-inline">
                                                <input type="hidden" name="courseId" value="${lessons[0].courseID}" />
                                                <input type="hidden" name="lessonId" value="${lessons[0].lessonID}" />
                                                <button type="submit" class="btn btn-success">Vào học</button>
                                            </form>
                                        </c:if>
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
                                            <c:if test="${not empty lesson.description}">
                                                <div class="lesson-description-preview">
                                                    <span class="fw-bold">Mô tả bài học:</span>
                                                    <span>${lesson.description}</span>
                                                </div>
                                            </c:if>



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
