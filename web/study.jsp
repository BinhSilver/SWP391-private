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

            <!-- Advertisement Banner -->
            <%@ include file="ads.jsp"%>

            <div class="custom-layout">
                <!-- MAIN CONTENT -->
                <main class="main-study-area" id="mainContent">
                    <div class="lesson-header">
                        <h2><i class="fas fa-graduation-cap me-3"></i>${lesson.title}</h2>
                        <div class="d-flex justify-content-between align-items-center mt-2">
                            <p class="lead mb-0"><i class="fas fa-info-circle me-2"></i>${lesson.description}</p>
                            <!-- Add navigation buttons at the top -->
                            <div class="lesson-nav-buttons">
                                <c:choose>
                                    <c:when test="${prevLessonId > 0}">
                                        <a href="StudyLessonServlet?courseId=${lesson.courseID}&lessonId=${prevLessonId}" 
                                           class="btn btn-outline-primary btn-sm me-2">
                                            <i class="fas fa-chevron-left nav-icon"></i>
                                            Bài trước
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-outline-secondary btn-sm me-2" disabled>
                                            <i class="fas fa-chevron-left nav-icon"></i>
                                            Bài trước
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                                
                                <c:choose>
                                    <c:when test="${nextLessonId > 0 && (currentProgress == 100 || isNextLessonUnlocked)}">
                                        <a href="StudyLessonServlet?courseId=${lesson.courseID}&lessonId=${nextLessonId}" 
                                           class="btn btn-outline-primary btn-sm">
                                            Bài tiếp
                                            <i class="fas fa-chevron-right nav-icon"></i>
                                        </a>
                                    </c:when>
                                    <c:when test="${nextLessonId > 0}">
                                        <button class="btn btn-outline-secondary btn-sm" disabled title="Hoàn thành bài học này để mở khóa">
                                            Bài tiếp
                                            <i class="fas fa-lock nav-icon"></i>
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn btn-outline-secondary btn-sm" disabled>
                                            Bài tiếp
                                            <i class="fas fa-chevron-right nav-icon"></i>
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <!-- Lesson progress tracking -->
                    <div class="lesson-progress">
                        <div class="progress-title">
                            <i class="fas fa-chart-line progress-icon"></i>
                            <h5 class="mb-0">Tiến độ học tập</h5>
                        </div>
                        <div class="progress" style="height: 20px;">
                            <div class="progress-bar bg-success" role="progressbar" 
                                 style="width: ${currentProgress}%;" 
                                 aria-valuenow="${currentProgress}" 
                                 aria-valuemin="0" 
                                 aria-valuemax="100">${currentProgress}%</div>
                        </div>
                    </div>

                    <!-- VIDEO -->
                    <c:forEach var="m" items="${materials}">
                        <c:if test="${m.materialType eq 'Ngữ pháp' && m.fileType eq 'Video'}">
                            <div class="material-item">
                                <h5><i class="fas fa-play-circle me-2"></i>Video bài học</h5>
                                <strong><i class="fas fa-film me-2"></i>${m.title}</strong>
                                <div class="file-viewer position-relative">
                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                    <video controls preload="metadata">
                                        <source src="${m.filePath}" type="video/mp4">
                                        Trình duyệt của bạn không hỗ trợ video.
                                    </video>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>

                    <!-- Tabs -->
                    <div class="tabs-container">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                        <ul class="nav lesson-tabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <a class="nav-link active" data-bs-toggle="tab" href="#grammar" data-section="grammar">
                                    <i class="fas fa-book me-1"></i> Ngữ pháp
                                </a>
                            </li>
                            <li class="nav-item" role="presentation">
                                <a class="nav-link" data-bs-toggle="tab" href="#vocab" data-section="vocab">
                                    <i class="fas fa-language me-1"></i> Từ vựng
                                </a>
                            </li>
                            <li class="nav-item" role="presentation">
                                <a class="nav-link" data-bs-toggle="tab" href="#kanji" data-section="kanji">
                                    <i class="fas fa-pencil-alt me-1"></i> Kanji
                                </a>
                            </li>
                            <li class="nav-item" role="presentation">
                                <a class="nav-link" data-bs-toggle="tab" href="#quiz" data-section="quiz">
                                    <i class="fas fa-question-circle me-1"></i> Bài tập
                                </a>
                            </li>
                        </ul>
                            <!-- Add completion button in the tab area -->
                            <div class="section-complete-btn">
                                <c:choose>
                                    <c:when test="${currentProgress == 100}">
                                        <span class="lesson-completed-badge">
                                            <i class="fas fa-check-circle me-1"></i> Đã hoàn thành
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <button id="completeSectionBtn" class="btn btn-success btn-sm">
                                            <i class="fas fa-check-circle me-1"></i> Đánh dấu hoàn thành
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

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
                                                <iframe src="${m.filePath}" title="Tài liệu ngữ pháp"></iframe>
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
                                                        <button class="play-btn" data-word="${vocab.word}" style="color: white;"><i class="fa-solid fa-volume-up"></i> Phát phát âm</button>
                                                    </div>
                                                    <c:if test="${not empty vocab.imagePath}">
                                                        <img src="${vocab.imagePath}" alt="${vocab.word}" class="vocab-image">
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
                                        <c:if test="${m.materialType eq 'Từ vựng' && m.fileType eq 'Video'}">
                                            <div class="material-item">
                                                <h5><i class="fas fa-play-circle me-2"></i>Video Từ vựng</h5>
                                                <strong><i class="fas fa-film me-2"></i>${m.title}</strong>
                                                <div class="file-viewer position-relative">
                                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                    <video controls preload="metadata">
                                                        <source src="${m.filePath}" type="video/mp4">
                                                        Trình duyệt của bạn không hỗ trợ video.
                                                    </video>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                    <c:forEach var="m" items="${materials}">
                                        <c:if test="${m.materialType eq 'Từ vựng' && m.fileType eq 'PDF'}">
                                            <div class="material-item">
                                                <h5><i class="fas fa-file-pdf me-2"></i>Tài liệu từ vựng</h5>
                                                <strong><i class="fas fa-document me-2"></i>${m.title}</strong>
                                                <div class="file-viewer position-relative">
                                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                    <iframe src="${m.filePath}" title="Tài liệu từ vựng"></iframe>
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
                                        <c:if test="${m.materialType eq 'Kanji' && m.fileType eq 'Video'}">
                                            <div class="material-item">
                                                <h5><i class="fas fa-play-circle me-2"></i>Video Kanji</h5>
                                                <strong><i class="fas fa-film me-2"></i>${m.title}</strong>
                                                <div class="file-viewer position-relative">
                                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                    <video controls preload="metadata">
                                                        <source src="${m.filePath}" type="video/mp4">
                                                        Trình duyệt của bạn không hỗ trợ video.
                                                    </video>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                    <c:forEach var="m" items="${materials}">
                                        <c:if test="${m.materialType eq 'Kanji' && m.fileType eq 'PDF'}">
                                            <div class="material-item">
                                                <h5><i class="fas fa-file-pdf me-2"></i>Tài liệu Kanji</h5>
                                                <strong><i class="fas fa-document me-2"></i>${m.title}</strong>
                                                <div class="file-viewer position-relative">
                                                    <i class="fa-solid fa-expand fullscreen-toggle" title="Xem toàn màn hình"></i>
                                                    <iframe src="${m.filePath}" title="Tài liệu Kanji"></iframe>
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



                                                <div class="quiz-actions">
                                                    <a href="doQuiz?lessonId=${lesson.lessonID}" class="btn btn-primary" style="color: white;">
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

                        <!-- Lesson completion button -->
                        <div class="text-center my-4">
                            <c:choose>
                                <c:when test="${currentProgress == 100}">
                                    <button class="complete-button completed" disabled>
                                        <i class="fas fa-check-circle complete-icon"></i>
                                        Đã hoàn thành bài học
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button class="complete-button" id="completeLesson">
                                        <i class="fas fa-check-circle complete-icon"></i>
                                        Đánh dấu hoàn thành bài học
                                    </button>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- Lesson navigation buttons -->
                        <div class="navigation-buttons">
                            <c:choose>
                                <c:when test="${prevLessonId > 0}">
                                    <a href="StudyLessonServlet?courseId=${lesson.courseID}&lessonId=${prevLessonId}" 
                                       class="nav-button prev-lesson">
                                        <i class="fas fa-chevron-left nav-icon"></i>
                                        Bài học trước
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <span class="nav-button disabled">
                                        <i class="fas fa-chevron-left nav-icon"></i>
                                        Bài học trước
                                    </span>
                                </c:otherwise>
                            </c:choose>

                            <a href="CourseDetailServlet?id=${lesson.courseID}" class="nav-button">
                                <i class="fas fa-th-list nav-icon"></i>
                                Về trang khóa học
                            </a>

                            <c:choose>
                                <c:when test="${nextLessonId > 0 && (currentProgress == 100 || isNextLessonUnlocked)}">
                                    <a href="StudyLessonServlet?courseId=${lesson.courseID}&lessonId=${nextLessonId}" 
                                       class="nav-button next-lesson">
                                        Bài học tiếp theo
                                        <i class="fas fa-chevron-right nav-icon"></i>
                                    </a>
                                </c:when>
                                <c:when test="${nextLessonId > 0}">
                                    <span class="nav-button disabled" title="Hoàn thành bài học này để mở khóa">
                                        Bài học tiếp theo
                                        <i class="fas fa-lock nav-icon"></i>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="nav-button disabled">
                                        Bài học tiếp theo
                                        <i class="fas fa-chevron-right nav-icon"></i>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                </main>

                    <!-- Sidebar -->
                <aside class="study-sidebar" id="lessonSidebar">
                    <button id="hideSidebarBtn" class="sidebar-toggle-btn" type="button" title="Ẩn sidebar">
                        <i class="fa-solid fa-chevron-right"></i>
                    </button>
                    <div class="sidebar-inner">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5><i class="fas fa-list me-2"></i>Danh sách bài học</h5>
                                <c:choose>
                                    <c:when test="${currentProgress == 100}">
                                        <span class="lesson-completed-badge">
                                            <i class="fas fa-check-circle"></i> Đã hoàn thành
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <button id="sidebarCompleteBtn" class="btn btn-sm btn-success">
                                            <i class="fas fa-check-circle"></i> Hoàn thành
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
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

                <!-- Completion Modal -->
                <div class="modal fade" id="completionModal" tabindex="-1" aria-labelledby="completionModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="completionModalLabel">
                                    <i class="fas fa-check-circle me-2"></i>
                                    Chúc mừng!
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body text-center">
                                <div class="celebration-icon-container mb-3">
                                    <i class="fas fa-medal celebration-icon"></i>
                                </div>
                                <h4 class="mb-3">Bạn đã hoàn thành bài học!</h4>
                                <p class="text-muted">Tiếp tục học các bài học tiếp theo để hoàn thành khóa học.</p>
                            </div>
                            <div class="modal-footer justify-content-between">
                                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Đóng</button>
                                <c:if test="${nextLessonId > 0}">
                                    <a href="StudyLessonServlet?courseId=${lesson.courseID}&lessonId=${nextLessonId}" 
                                       class="btn btn-success">
                                        <i class="fas fa-arrow-right me-2"></i>
                                        Bài học tiếp theo
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
                <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script>
                    $(document).ready(function() {
                        // Mark current lesson as active in the sidebar
                        $(".list-group-item a").each(function() {
                            const href = $(this).attr('href');
                            if (href && href.includes("lessonId=${lesson.lessonID}")) {
                                $(this).closest('.list-group-item').addClass('active-lesson');
                                $(this).css('color', 'white');
                            }
                        });
                        
                        // Track visited sections to calculate progress
                        const sections = ['grammar', 'vocab', 'kanji', 'quiz'];
                        let visitedSections = {};
                        
                        // Initialize with stored progress if available
                        try {
                            const storedProgress = localStorage.getItem('lesson_progress_${lesson.lessonID}');
                            if (storedProgress) {
                                visitedSections = JSON.parse(storedProgress);
                            }
                        } catch (e) {
                            console.error("Error loading stored progress:", e);
                        }
                        
                        // Update UI based on visited sections
                        function updateSectionProgress() {
                            const visitedCount = Object.values(visitedSections).filter(Boolean).length;
                            const totalSections = sections.length;
                            const percentComplete = Math.floor((visitedCount / totalSections) * 100);
                            
                            // Update the section completion button text to show progress
                            $("#completeSectionBtn").html(
                                '<i class="fas fa-check-circle me-1"></i> Hoàn thành (' + visitedCount + '/' + totalSections + ')'
                            );
                            
                            // Store progress in local storage
                            localStorage.setItem('lesson_progress_${lesson.lessonID}', JSON.stringify(visitedSections));
                            
                            // Update server-side progress if it's different from current progress
                            var currentProgress = ${currentProgress};
                            if (currentProgress < percentComplete && percentComplete < 100) {
                                updateProgress(percentComplete);
                            }
                            
                            return percentComplete;
                        }
                        
                        // Mark the current tab as visited when switched
                        $('.nav-link[data-bs-toggle="tab"]').on('shown.bs.tab', function (e) {
                            const section = $(e.target).data('section');
                            visitedSections[section] = true;
                            updateSectionProgress();
                        });
                        
                        // Mark initial tab as visited
                        const initialTab = $('.nav-link.active').data('section');
                        if (initialTab) {
                            visitedSections[initialTab] = true;
                            updateSectionProgress();
                        }
                        
                        // Complete section button functionality
                        $("#completeSectionBtn").click(function() {
                            // Mark all sections as visited
                            sections.forEach(function(section) {
                                visitedSections[section] = true;
                            });
                            
                            // Update UI
                            const percentComplete = updateSectionProgress();
                            
                            // Disable the button to prevent multiple clicks
                            $(this).attr('disabled', true).html('<i class="fas fa-spinner fa-spin me-1"></i> Đang xử lý...');
                            
                            // Call the server to mark lesson as complete
                            $.ajax({
                                url: 'CompleteLessonServlet',
                                type: 'POST',
                                data: {
                                    lessonId: ${lesson.lessonID},
                                    courseId: ${lesson.courseID},
                                    completionPercent: 100
                                },
                                success: function(response) {
                                    // Show completion modal
                                    $("#completionModal").modal('show');
                                    
                                    // Update progress bar
                                    $(".progress-bar").css('width', '100%').attr('aria-valuenow', 100).text('100%');
                                    
                                    // Update button
                                    $("#completeSectionBtn")
                                        .removeClass('btn-success')
                                        .addClass('btn-secondary')
                                        .html('<i class="fas fa-check-circle me-1"></i> Đã hoàn thành')
                                        .attr('disabled', true);
                                    
                                    // Update the original complete button too
                                    $("#completeLesson")
                                        .removeClass('complete-button')
                                        .addClass('complete-button completed')
                                        .html('<i class="fas fa-check-circle complete-icon"></i> Đã hoàn thành bài học')
                                        .attr('disabled', true);
                                    
                                    // Update the sidebar button
                                    $("#sidebarCompleteBtn")
                                        .removeClass('btn-success')
                                        .addClass('btn-secondary')
                                        .html('<i class="fas fa-check-circle"></i> Đã hoàn thành')
                                        .attr('disabled', true);
                                    
                                    // Log completion
                                    console.log('Lesson marked as completed');
                                },
                                error: function(error) {
                                    // Re-enable the button if there's an error
                                    $("#completeSectionBtn")
                                        .attr('disabled', false)
                                        .html('<i class="fas fa-check-circle me-1"></i> Đánh dấu hoàn thành');
                                    
                                    // Show error alert
                                    alert('Có lỗi xảy ra. Vui lòng thử lại sau!');
                                    console.error('Error:', error);
                        }
                    });
                });

                        // Link the original complete button to this new functionality
                        $("#completeLesson").click(function() {
                            $("#completeSectionBtn").click();
                        });
                        
                        // Link the sidebar complete button to this new functionality
                        $("#sidebarCompleteBtn").click(function() {
                            $("#completeSectionBtn").click();
                        });
                        
                        // Video playback tracking for partial progress
                        $("video").on('timeupdate', function() {
                            const video = this;
                            const duration = video.duration;
                            const currentTime = video.currentTime;
                            
                            // Only update progress if duration is available and video has been played for at least 10%
                            if (duration && currentTime > (duration * 0.1)) {
                                const percentWatched = Math.floor((currentTime / duration) * 100);
                                
                                // Calculate overall progress based on sections
                                const visitedCount = Object.values(visitedSections).filter(Boolean).length;
                                const totalSections = sections.length;
                                let lessonProgress = Math.floor((visitedCount / totalSections) * 100);
                                
                                // Include video watch progress in the calculation
                                if (lessonProgress < 100 && percentWatched >= 90) {
                                    visitedSections['video'] = true;
                                    lessonProgress = Math.min(75, lessonProgress + 25); // Video counts for up to 25% of total progress
                                }
                                
                                // If the current progress is less than the calculated progress
                                var currentProgress = ${currentProgress};
                                if (currentProgress < lessonProgress && lessonProgress < 100) {
                                    // Update progress every 25% watched (to reduce server calls)
                                    if (lessonProgress % 25 === 0) {
                                        updateProgress(lessonProgress);
                                    }
                                }
                            }
                        });
                        
                        function updateProgress(percentComplete) {
                            $.ajax({
                                url: 'CompleteLessonServlet',
                                type: 'POST',
                                data: {
                                    lessonId: ${lesson.lessonID},
                                    courseId: ${lesson.courseID},
                                    completionPercent: percentComplete
                                },
                                success: function() {
                                    // Update the progress bar
                                    $(".progress-bar").css('width', percentComplete + '%')
                                        .attr('aria-valuenow', percentComplete)
                                        .text(percentComplete + '%');
                                        
                                    console.log('Progress updated to ' + percentComplete + '%');
                                },
                                error: function(error) {
                                    console.error('Error updating progress:', error);
                                }
                            });
                        }
                        
                        // Fullscreen toggle functionality
                        $('.fullscreen-toggle').click(function() {
                            const viewer = $(this).closest('.file-viewer');
                            viewer.toggleClass('fullscreen');
                            $(this).toggleClass('fa-expand fa-compress');
                            $(this).attr('title', viewer.hasClass('fullscreen') ? 'Thu nhỏ' : 'Xem toàn màn hình');
                            
                            // Add escape key listener for fullscreen
                            if (viewer.hasClass('fullscreen')) {
                                $(document).on('keydown.fullscreen', function(e) {
                                    if (e.key === 'Escape') {
                                        viewer.removeClass('fullscreen');
                                        $('.fullscreen-toggle', viewer).removeClass('fa-compress').addClass('fa-expand');
                                        $('.fullscreen-toggle', viewer).attr('title', 'Xem toàn màn hình');
                                        $(document).off('keydown.fullscreen');
                                    }
                                });
                            }
                        });
                        
                        // Sidebar toggle functionality
                        const sidebar = $('#lessonSidebar');
                        const hideBtn = $('#hideSidebarBtn');
                        const showBtn = $('#showSidebarBtn');
                        const mainContent = $('#mainContent');
                        
                        hideBtn.click(function() {
                            sidebar.addClass('collapsed');
                            mainContent.addClass('expanded');
                            hideBtn.hide();
                            showBtn.css('display', 'flex');
                        });
                        
                        showBtn.click(function() {
                            sidebar.removeClass('collapsed');
                            mainContent.removeClass('expanded');
                            hideBtn.show();
                            showBtn.hide();
                        });
                        
                        // Smooth scroll for anchor links
                        $('a[href^="#"]').click(function(e) {
                            e.preventDefault();
                            const target = $(this.getAttribute('href'));
                            if (target.length) {
                                $('html, body').animate({
                                    scrollTop: target.offset().top
                                }, 300);
                            }
                });

                // Add loading animation for iframes
                        $('iframe').on('load', function() {
                            $(this).css('opacity', '1');
                        }).css({
                            'opacity': '0',
                            'transition': 'opacity 0.3s ease'
                });

                // Add hover effects for material items
                        $('.material-item').hover(
                            function() { $(this).css('transform', 'translateY(-4px)'); },
                            function() { $(this).css('transform', 'translateY(0)'); }
                        );
                        
            // Slideshow functionality
                        const slides = $('.vocab-item');
                        const prevBtn = $('.vocab-prev-btn');
                        const nextBtn = $('.vocab-next-btn');
let currentSlide = 0;

function showSlide(index) {
                            slides.removeClass('active prev next');
                            slides.each(function(i) {
        if (i === index) {
                                    $(this).addClass('active');
        } else if (i < index) {
                                    $(this).addClass('prev');
        } else {
                                    $(this).addClass('next');
        }
    });
}

                        prevBtn.click(function() {
    currentSlide = (currentSlide > 0) ? currentSlide - 1 : slides.length - 1;
    showSlide(currentSlide);
});

                        nextBtn.click(function() {
    currentSlide = (currentSlide < slides.length - 1) ? currentSlide + 1 : 0;
    showSlide(currentSlide);
});

                        // Pronunciation with ResponsiveVoice
                        $('.play-btn').click(function() {
                            const word = $(this).data('word');
                            if (word && typeof responsiveVoice !== 'undefined') {
            responsiveVoice.speak(word, "Japanese Female", {rate: 0.9});
        }
    });

                        // Periodically refresh progress from server
                        function refreshProgressFromServer() {
                            $.ajax({
                                url: 'GetProgressServlet',
                                type: 'GET',
                                data: {
                                    lessonId: ${lesson.lessonID},
                                    courseId: ${lesson.courseID}
                                },
                                dataType: 'json',
                                success: function(response) {
                                    // Store current progress for comparison
                                    var currentProgress = ${currentProgress};
                                    
                                    if (response.success && response.progress > currentProgress) {
                                        // Update the progress bar only if it's higher than current
                                        $(".progress-bar").css('width', response.progress + '%')
                                            .attr('aria-valuenow', response.progress)
                                            .text(response.progress + '%');
                                            
                                        console.log('Progress refreshed from server: ' + response.progress + '%');
                                        
                                        // If progress is 100%, update all buttons
                                        if (response.progress >= 100) {
                                            // Update button
                                            $("#completeSectionBtn")
                                                .removeClass('btn-success')
                                                .addClass('btn-secondary')
                                                .html('<i class="fas fa-check-circle me-1"></i> Đã hoàn thành')
                                                .attr('disabled', true);
                                            
                                            // Update the original complete button too
                                            $("#completeLesson")
                                                .removeClass('complete-button')
                                                .addClass('complete-button completed')
                                                .html('<i class="fas fa-check-circle complete-icon"></i> Đã hoàn thành bài học')
                                                .attr('disabled', true);
                                            
                                            // Update the sidebar button
                                            $("#sidebarCompleteBtn")
                                                .removeClass('btn-success')
                                                .addClass('btn-secondary')
                                                .html('<i class="fas fa-check-circle"></i> Đã hoàn thành')
                                                .attr('disabled', true);
                                        }
                                    }
                                },
                                error: function(error) {
                                    console.error('Error refreshing progress:', error);
                                }
                            });
                        }
                        
                        // Check progress from server every 30 seconds
                        setInterval(refreshProgressFromServer, 30000);
});
        </script>
    </body>
</html>