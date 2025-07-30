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
        <link rel="stylesheet" href="<c:url value='/css/quiz-protection.css'/>">
    </head>
    <body>

        <div class="page-wrapper">
            <%@ include file="../Home/nav.jsp" %>

            <div class="container py-5">
                <h3 class="mb-4 text-primary">📝 Làm bài Quiz</h3>

                <!-- Đếm ngược thời gian -->
                <div class="quiz-timer-container text-center mb-4">
                    <span class="quiz-timer-label">⏰ Thời gian còn lại:</span>
                    <span id="quiz-timer" class="quiz-timer">05:00</span>
                </div>

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
                    <c:if test="${not empty courseId}">
                        <a href="CourseDetailServlet?id=${courseId}" class="btn btn-secondary ms-2">← Quay lại khóa học</a>
                    </c:if>
                    <c:if test="${empty courseId}">
                        <span class="text-danger">Thiếu CourseID, không thể quay lại khóa học!</span>
                    </c:if>
                </form>
            </div>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<c:url value='/js/quiz-protection.js'/>"></script>
        <script src="<c:url value='/js/quiz-protection-init.js'/>"></script>

        <%@ include file="../Home/footer.jsp" %>

        <script>
            // Debug: Kiểm tra xem các file JS có được load không
            console.log('=== [do-quiz.jsp] Page loaded ===');
            console.log('[do-quiz.jsp] QuizProtection class available:', typeof QuizProtection);
            console.log('[do-quiz.jsp] initQuizProtection function available:', typeof initQuizProtection);
            
            // Khởi tạo Quiz Protection System tự động
            let quizProtection = null;
            
            try {
                quizProtection = initQuizProtection({
                    maxViolations: 3,
                    enabled: true,
                    debugMode: true // Bật debug mode để có thể dùng F12
                });
                
                // Bật F12 ngay lập tức
                if (quizProtection) {
                    quizProtection.enableDebugMode();
                    console.log('[QuizProtection] F12 enabled for debugging');
                }
                
                console.log('[do-quiz.jsp] Quiz Protection initialized successfully');
                console.log('[do-quiz.jsp] Protection status:', quizProtection.getStatus());
                
                // Test chuyển tab
                console.log('[do-quiz.jsp] Testing tab switch detection...');
                
            } catch (error) {
                console.error('[do-quiz.jsp] ERROR initializing Quiz Protection:', error);
                
                // Fallback: tạo instance trực tiếp
                try {
                    quizProtection = new QuizProtection({
                        maxViolations: 3,
                        enabled: true
                    });
                    console.log('[do-quiz.jsp] Fallback initialization successful');
                } catch (fallbackError) {
                    console.error('[do-quiz.jsp] Fallback initialization failed:', fallbackError);
                }
            }
            
            // Test function để kiểm tra từ console
            window.testQuizProtectionFromPage = function() {
                console.log('=== [do-quiz.jsp] Manual test ===');
                if (quizProtection) {
                    console.log('Quiz Protection status:', quizProtection.getStatus());
                    quizProtection.showWarning('Manual test warning');
                } else {
                    console.error('Quiz Protection not initialized!');
                }
            };

            // ===== TIMER CODE =====
            // Tổng thời gian (giây) - lấy từ BE nếu có, mặc định 60s/câu
            var totalSeconds = <c:out value='${totalTime != null ? totalTime : (questions.size() * 60)}'/>;
            var timerDisplay = document.getElementById('quiz-timer');
            var quizForm = document.querySelector('form[action="doQuiz"]');
            var timerInterval;

            function updateTimer() {
                var min = Math.floor(totalSeconds / 60);
                var sec = totalSeconds % 60;
                timerDisplay.textContent = (min < 10 ? '0' : '') + min + ':' + (sec < 10 ? '0' : '') + sec;
                if (totalSeconds <= 0) {
                    clearInterval(timerInterval);
                    alert('Hết thời gian! Bài làm sẽ được nộp tự động.');
                    if (quizForm) quizForm.submit();
                }
                totalSeconds--;
            }

            if (timerDisplay) {
                updateTimer();
                timerInterval = setInterval(updateTimer, 1000);
            }
        </script>

    </body>
</html>
