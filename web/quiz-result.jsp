<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Kết quả Quiz</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        .result-container {
            max-width: 600px;
            margin: 0 auto;
            padding: 2rem;
        }
        
        .result-card {
            background: #fff;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            padding: 2rem;
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .score-circle {
            width: 150px;
            height: 150px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 1.5rem;
            font-size: 2.5rem;
            font-weight: bold;
            color: white;
            position: relative;
        }
        
        .score-excellent {
            background: linear-gradient(135deg, #28a745, #20c997);
        }
        
        .score-good {
            background: linear-gradient(135deg, #17a2b8, #6f42c1);
        }
        
        .score-average {
            background: linear-gradient(135deg, #ffc107, #fd7e14);
        }
        
        .score-poor {
            background: linear-gradient(135deg, #dc3545, #e83e8c);
        }
        
        .score-text {
            font-size: 1.2rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }
        
        .score-percentage {
            font-size: 3rem;
            font-weight: bold;
            margin-bottom: 0.5rem;
        }
        
        .score-details {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 1.5rem;
            margin: 1.5rem 0;
        }
        
        .detail-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.5rem 0;
            border-bottom: 1px solid #e9ecef;
        }
        
        .detail-item:last-child {
            border-bottom: none;
        }
        
        .feedback-section {
            background: linear-gradient(135deg, #f8f9fa, #e9ecef);
            border-radius: 12px;
            padding: 1.5rem;
            margin: 1.5rem 0;
        }
        
        .action-buttons {
            display: flex;
            gap: 1rem;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn-custom {
            padding: 12px 24px;
            border-radius: 12px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
            border: none;
        }
        
        .btn-primary-custom {
            background: linear-gradient(135deg, #e94f64, #d13d52);
            color: white;
        }
        
        .btn-primary-custom:hover {
            background: linear-gradient(135deg, #d13d52, #b83247);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(233, 79, 100, 0.3);
            color: white;
        }
        
        .btn-secondary-custom {
            background: #6c757d;
            color: white;
        }
        
        .btn-secondary-custom:hover {
            background: #5a6268;
            transform: translateY(-2px);
            color: white;
        }
        
        .celebration {
            font-size: 2rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <%@ include file="/Home/nav.jsp" %>

    <div class="page-wrapper">
        <div class="container py-5">
            <div class="result-container">
                <div class="result-card">
                    <c:set var="percentage" value="${(score * 100) / total}" />
                    <c:set var="scoreClass" value="" />
                    <c:set var="feedback" value="" />
                    <c:set var="celebration" value="" />
                    
                    <c:choose>
                        <c:when test="${percentage >= 90}">
                            <c:set var="scoreClass" value="score-excellent" />
                            <c:set var="feedback" value="Xuất sắc! Bạn đã nắm vững kiến thức bài học này." />
                            <c:set var="celebration" value="🎉" />
                        </c:when>
                        <c:when test="${percentage >= 75}">
                            <c:set var="scoreClass" value="score-good" />
                            <c:set var="feedback" value="Tốt! Bạn đã hiểu khá tốt nội dung bài học." />
                            <c:set var="celebration" value="👏" />
                        </c:when>
                        <c:when test="${percentage >= 60}">
                            <c:set var="scoreClass" value="score-average" />
                            <c:set var="feedback" value="Khá! Bạn cần ôn tập thêm một số phần." />
                            <c:set var="celebration" value="👍" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="scoreClass" value="score-poor" />
                            <c:set var="feedback" value="Cần cải thiện! Hãy xem lại bài học và thử lại." />
                            <c:set var="celebration" value="💪" />
                        </c:otherwise>
                    </c:choose>
                    
                    <div class="celebration">${celebration}</div>
                    <h2 class="mb-4">Kết quả Quiz</h2>
                    
                    <div class="score-circle ${scoreClass}">
                        <div>
                            <div class="score-percentage">${percentage}%</div>
                            <div class="score-text">${score}/${total}</div>
                        </div>
                    </div>
                    
                    <div class="score-details">
                        <div class="detail-item">
                            <span><i class="fas fa-check-circle text-success me-2"></i>Đúng:</span>
                            <span class="fw-bold">${score} câu</span>
                        </div>
                        <div class="detail-item">
                            <span><i class="fas fa-times-circle text-danger me-2"></i>Sai:</span>
                            <span class="fw-bold">${total - score} câu</span>
                        </div>
                        <div class="detail-item">
                            <span><i class="fas fa-clock text-info me-2"></i>Tổng câu hỏi:</span>
                            <span class="fw-bold">${total} câu</span>
                        </div>
                    </div>
                    
                    <div class="feedback-section">
                        <h5><i class="fas fa-comment me-2"></i>Nhận xét:</h5>
                        <p class="mb-0">${feedback}</p>
                    </div>
                    
                    <div class="action-buttons">
                        <a href="doQuiz?lessonId=${lessonId}" class="btn btn-custom btn-primary-custom">
                            <i class="fas fa-redo me-2"></i>Làm lại Quiz
                        </a>
                        <a href="StudyLessonServlet?lessonId=${lessonId}&courseId=${courseId}" class="btn btn-custom btn-secondary-custom">
                            <i class="fas fa-book me-2"></i>Xem lại bài học
                        </a>
                        <a href="CourseDetailServlet?id=${courseId}" class="btn btn-custom btn-secondary-custom">
                            <i class="fas fa-arrow-left me-2"></i>Về khóa học
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <%@ include file="../Home/footer.jsp" %>
</body>
</html> 