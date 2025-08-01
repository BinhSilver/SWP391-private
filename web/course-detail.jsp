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
        <link rel="stylesheet" href="<c:url value='/css/course-card.css'/>" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    </head>
    <body>
        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <!-- Advertisement Banner -->
            <%@ include file="ads.jsp"%>

            <section class="container py-5">
                <c:choose>
                    <c:when test="${not empty course}">
                        <!-- Course Header -->
                        <div class="course-card-container mb-4">
                            <h2 class="course-title">${course.title}</h2>
                            <p class="course-description">${course.description}</p>
                            
                            <c:if test="${currentUser != null && not empty overallProgress}">
                                <div class="progress-section">
                                    <p class="progress-title">Tiến độ học tập: ${overallProgress}%</p>
                                    <div class="progress">
                                        <div class="progress-bar" role="progressbar" 
                                             style="width: ${overallProgress}%;" 
                                             aria-valuenow="${overallProgress}" 
                                             aria-valuemin="0" 
                                             aria-valuemax="100">${overallProgress}%</div>
                                    </div>
                                </div>
                            </c:if>
                            
                            <div class="course-status">
                                <p>Trạng thái:
                                    <c:choose>
                                        <c:when test="${course.hidden}">Ẩn</c:when>
                                        <c:otherwise>Hiển thị</c:otherwise>
                                    </c:choose>
                                </p>
                                <p class="course-status-item">
                                    <c:choose>
                                        <c:when test="${course.suggested}">
                                            <i class="fas fa-check-circle"></i>
                                            Gợi ý: Có đề xuất
                                        </c:when>
                                        <c:otherwise>
                                            <i class="fas fa-times-circle"></i>
                                            Gợi ý: Không
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>

                            <div class="course-actions">
                                <c:choose>
                                    <c:when test="${currentUser == null}">
                                        <a href="LoginServlet" class="btn-continue">
                                            <i class="fas fa-sign-in-alt"></i> Đăng nhập
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${hasAccessedCourse}">
                                            <form action="StudyLessonServlet" method="get" class="d-inline">
                                                <input type="hidden" name="courseId" value="${course.courseID}" />
                                                <input type="hidden" name="lessonId" value="${lessons[0].lessonID}" />
                                                <button type="submit" class="btn-continue">
                                                    <i class="fas fa-book-open"></i> Học tiếp
                                                </button>
                                            </form>
                                        </c:if>
                                        <c:if test="${!hasAccessedCourse}">
                                            <form action="StudyLessonServlet" method="get" class="d-inline">
                                                <input type="hidden" name="courseId" value="${lessons[0].courseID}" />
                                                <input type="hidden" name="lessonId" value="${lessons[0].lessonID}" />
                                                <button type="submit" class="btn-continue">
                                                    <i class="fas fa-book-open"></i> Vào học
                                                </button>
                                            </form>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                                <a href="CoursesServlet" class="btn-back">
                                    <i class="fas fa-arrow-left"></i> Quay lại
                                </a>
                            </div>
                        </div>

                        <!-- Two Column Layout -->
                        <div class="row">
                            <!-- Left Column: Lessons List -->
                            <div class="col-lg-8">
                                <h4 class="text-dark mb-3">📘 Danh sách bài học</h4>
                                <div class="lesson-list">
                                    <c:forEach var="lesson" items="${lessons}" varStatus="lessonStatus">
                                        <c:if test="${lessonStatus.index < 2 || showAllLessons}">
                                        <div class="lesson-item">
                                            <c:if test="${currentUser != null && not empty lessonUnlockStatus && !lessonUnlockStatus[lesson.lessonID]}">
                                                <div class="locked-overlay">
                                                    <div class="locked-message">
                                                        <i class="fas fa-lock locked-icon"></i>
                                                        <span>Hoàn thành bài học trước để mở khóa</span>
                                                    </div>
                                                </div>
                                            </c:if>
                                            
                                            <div class="lesson-header">
                                                <div class="lesson-icon">
                                                    <i class="fas fa-book"></i>
                                                </div>
                                                <h5 class="lesson-title">Bài ${lessonStatus.index + 1}: ${lesson.title}</h5>
                                                <div class="lesson-status">
                                                    <c:choose>
                                                        <c:when test="${not empty completedLessons && completedLessons.contains(lesson.lessonID)}">
                                                            <span class="status-badge status-completed">
                                                                <i class="fas fa-check-circle"></i> Hoàn thành
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${not empty lessonCompletionMap && lessonCompletionMap[lesson.lessonID] > 0}">
                                                            <span class="status-badge status-in-progress">
                                                                <i class="fas fa-clock"></i> Đang học (${lessonCompletionMap[lesson.lessonID]}%)
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${not empty lessonUnlockStatus && !lessonUnlockStatus[lesson.lessonID]}">
                                                            <span class="status-badge status-locked">
                                                                <i class="fas fa-lock"></i> Khóa
                                                            </span>
                                                        </c:when>
                                                    </c:choose>
                                                </div>
                                            </div>
                                            
                                            <div class="lesson-content">
                                                <c:if test="${not empty lesson.description}">
                                                    <p class="lesson-description">${lesson.description}</p>
                                                </c:if>
                                                
                                                <c:if test="${not empty lessonMaterialsMap[lesson.lessonID]}">
                                                    <ul class="list-group list-group-flush mb-2">
                                                        <c:forEach var="mat" items="${lessonMaterialsMap[lesson.lessonID]}">
                                                            <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                                <a href="${mat.filePath}" target="_blank">${mat.title}</a>
                                                            </li>
                                                        </c:forEach>
                                                    </ul>
                                                </c:if>
                                                
                                                <c:if test="${not empty quizMap[lesson.lessonID]}">
                                                    <p>
                                                        <i class="fas fa-question-circle"></i> 
                                                        ${fn:length(quizMap[lesson.lessonID])} câu hỏi quiz
                                                    </p>
                                                </c:if>
                                            </div>
                                            
                                            <div class="lesson-actions">
                                                <c:choose>
                                                    <c:when test="${currentUser != null && not empty lessonUnlockStatus && lessonUnlockStatus[lesson.lessonID]}">
                                                        <form action="StudyLessonServlet" method="get">
                                                            <input type="hidden" name="courseId" value="${lesson.courseID}" />
                                                            <input type="hidden" name="lessonId" value="${lesson.lessonID}" />
                                                            <button type="submit" class="btn-continue btn-sm">
                                                                <i class="fas fa-play-circle"></i> Học thử
                                                            </button>
                                                        </form>
                                                    </c:when>
                                                    <c:when test="${currentUser == null}">
                                                        <a href="LoginServlet" class="btn-continue btn-sm">
                                                            <i class="fas fa-lock"></i> Đăng nhập để học
                                                        </a>
                                                    </c:when>
                                                </c:choose>
                                            </div>
                                        </div>
                                        </c:if>
                                    </c:forEach>
                                    
                                    <!-- Show More Button -->
                                    <c:if test="${fn:length(lessons) > 2 && !showAllLessons}">
                                        <div class="show-more-container text-center mt-4">
                                            <button class="btn-show-more" onclick="showAllLessons()">
                                                <i class="fas fa-chevron-down"></i> Xem thêm bài học
                                            </button>
                                        </div>
                                    </c:if>
                                    
                                    <!-- Show Less Button -->
                                    <c:if test="${showAllLessons}">
                                        <div class="show-more-container text-center mt-4">
                                            <button class="btn-show-less" onclick="showLessLessons()">
                                                <i class="fas fa-chevron-up"></i> Thu gọn
                                            </button>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <!-- Right Column: Course Feedback -->
                            <div class="col-lg-4">
                                <div class="feedback-section">
                                    <h4 class="text-dark mb-3">⭐ Đánh giá & Feedback khóa học</h4>
                                    
                                    <!-- Add Feedback Form -->
                                    <c:if test="${currentUser != null}">
                                        <c:choose>
                                            <c:when test="${userFeedback == null}">
                                                <!-- User hasn't written feedback yet -->
                                                <div class="add-feedback-section mb-4">
                                                    <h5>Viết đánh giá của bạn</h5>
                                                    <form id="addFeedbackForm" onsubmit="addFeedback(event)">
                                                        <div class="form-group">
                                                            <label>Nội dung:</label>
                                                            <textarea name="content" rows="3" required placeholder="Chia sẻ trải nghiệm học tập của bạn..."></textarea>
                                                        </div>
                                                        <div class="form-group">
                                                            <label>Đánh giá:</label>
                                                            <div class="rating-input">
                                                                <span class="star empty" onclick="setRating(1)">★</span>
                                                                <span class="star empty" onclick="setRating(2)">★</span>
                                                                <span class="star empty" onclick="setRating(3)">★</span>
                                                                <span class="star empty" onclick="setRating(4)">★</span>
                                                                <span class="star empty" onclick="setRating(5)">★</span>
                                                                <input type="hidden" name="rating" value="0">
                                                            </div>
                                                        </div>
                                                        <button type="submit" class="btn-add-feedback">Gửi đánh giá</button>
                                                    </form>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <!-- User has already written feedback -->
                                                <div class="user-feedback-notice mb-3">
                                                    <div class="alert alert-info">
                                                        <i class="fas fa-info-circle"></i>
                                                        Bạn đã viết đánh giá cho khóa học này.
                                                    </div>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                    
                                    <!-- Feedback List -->
                                    <div class="feedback-list">
                                        <c:choose>
                                            <c:when test="${not empty feedbacks}">
                                                <c:forEach var="feedback" items="${feedbacks}" varStatus="status">
                                                    <div class="feedback-item" data-feedback-id="${feedback.feedbackID}">
                                                        <div class="feedback-header">
                                                            <div class="user-info">
                                                                <div class="user-avatar">
                                                                    <img src="${pageContext.request.contextPath}/avatar?userId=${feedback.userID}&v=${feedback.userAvatar != null ? feedback.userAvatar.hashCode() : System.currentTimeMillis()}" alt="Avatar" onerror="this.style.display='none'; this.nextElementSibling.style.display='block';" />
                                                                    <i class="fas fa-user" style="display: none;"></i>
                                                                </div>
                                                                <div class="user-details">
                                                                    <div class="user-name">${feedback.userName}</div>
                                                                    <div class="feedback-rating">
                                                                        <c:forEach begin="1" end="5" var="i">
                                                                            <span class="star ${i <= feedback.rating ? 'filled' : 'empty'}">★</span>
                                                                        </c:forEach>
                                                                        <span class="rating-text">${feedback.rating}/5</span>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="feedback-date">
                                                                ${fn:substring(feedback.createdAt, 0, 10)}
                                                            </div>
                                                        </div>
                                                        <div class="feedback-content">
                                                            <p>${feedback.content}</p>
                                                        </div>
                                                        <div class="feedback-actions">
                                                            <!-- Like/Dislike buttons for all users -->
                                                            <button class="btn-like" onclick="voteFeedback(${feedback.feedbackID}, 1)">
                                                                <i class="fas fa-thumbs-up"></i> <span class="like-count">${feedback.totalLikes}</span>
                                                            </button>
                                                            <button class="btn-dislike" onclick="voteFeedback(${feedback.feedbackID}, -1)">
                                                                <i class="fas fa-thumbs-down"></i> <span class="dislike-count">${feedback.totalDislikes}</span>
                                                            </button>
                                                            
                                                            <!-- Edit/Delete buttons for current user's feedback -->
                                                            <c:if test="${currentUser != null && currentUser.userID == feedback.userID}">
                                                                <div class="user-feedback-actions">
                                                                    <button class="btn-edit" onclick="editFeedback(${feedback.feedbackID})">
                                                                        <i class="fas fa-edit"></i> Sửa
                                                                    </button>
                                                                    <button class="btn-delete" onclick="deleteFeedback(${feedback.feedbackID})">
                                                                        <i class="fas fa-trash"></i> Xóa
                                                                    </button>
                                                                </div>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                                
                                                <!-- Pagination -->
                                                <c:if test="${totalPages > 1}">
                                                    <nav aria-label="Feedback pagination" class="mt-3">
                                                        <ul class="pagination pagination-sm justify-content-center">
                                                            <c:if test="${currentPage > 1}">
                                                                <li class="page-item">
                                                                    <a class="page-link" href="${pageContext.request.contextPath}/CourseDetailServlet?id=${course.courseID}&page=${currentPage - 1}${showAllLessons ? '&showAllLessons=true' : ''}">
                                                                        <i class="fas fa-chevron-left"></i>
                                                                    </a>
                                                                </li>
                                                            </c:if>
                                                            
                                                            <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                                                <li class="page-item ${pageNum == currentPage ? 'active' : ''}">
                                                                    <a class="page-link" href="${pageContext.request.contextPath}/CourseDetailServlet?id=${course.courseID}&page=${pageNum}${showAllLessons ? '&showAllLessons=true' : ''}">${pageNum}</a>
                                                                </li>
                                                            </c:forEach>
                                                            
                                                            <c:if test="${currentPage < totalPages}">
                                                                <li class="page-item">
                                                                    <a class="page-link" href="${pageContext.request.contextPath}/CourseDetailServlet?id=${course.courseID}&page=${currentPage + 1}${showAllLessons ? '&showAllLessons=true' : ''}">
                                                                        <i class="fas fa-chevron-right"></i>
                                                                    </a>
                                                                </li>
                                                            </c:if>
                                                        </ul>
                                                    </nav>
                                                </c:if>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="no-feedback">
                                                    <i class="fas fa-comment-slash"></i>
                                                    <p>Chưa có đánh giá nào cho khóa học này.</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning">
                            <h3>Không tìm thấy khóa học!</h3>
                            <p>Khóa học này không tồn tại hoặc đã bị xóa.</p>
                            <a href="${pageContext.request.contextPath}/CoursesServlet" class="btn-back mt-3">
                                <i class="fas fa-arrow-left"></i> Quay lại trang khóa học
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <%@ include file="../Home/footer.jsp" %>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<c:url value='/js/feedback.js'/>"></script>
        <script>
            // Set contextPath and currentUserId for feedback voting
            window.contextPath = '${pageContext.request.contextPath}';
            window.currentUserId = ${currentUser != null ? currentUser.userID : 'null'};
            window.courseId = ${course.courseID};
            console.log('[course-detail] contextPath:', window.contextPath);
            console.log('[course-detail] currentUserId:', window.currentUserId);
            console.log('[course-detail] courseId:', window.courseId);
        </script>
        <script>
            function showAllLessons() {
                // Thêm parameter showAllLessons=true vào URL và giữ nguyên các parameter khác
                const url = new URL(window.location);
                url.searchParams.set('showAllLessons', 'true');
                // Giữ nguyên page parameter nếu có
                const currentPage = url.searchParams.get('page');
                if (currentPage) {
                    url.searchParams.set('page', currentPage);
                }
                window.location.href = url.toString();
            }
            
            function showLessLessons() {
                // Xóa parameter showAllLessons khỏi URL và giữ nguyên các parameter khác
                const url = new URL(window.location);
                url.searchParams.delete('showAllLessons');
                // Giữ nguyên page parameter nếu có
                const currentPage = url.searchParams.get('page');
                if (currentPage) {
                    url.searchParams.set('page', currentPage);
                }
                window.location.href = url.toString();
            }
        </script>
    </body>
</html>
