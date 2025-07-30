<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload chứng chỉ - Wasabii</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="<c:url value='/css/upload-certificate.css'/>">
</head>
<body>
    <div class="upload-card">
        <h2 class="mb-4">
            <i class="fas fa-certificate text-primary"></i>
            Upload chứng chỉ giảng dạy
        </h2>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-triangle"></i>
                ${error}
            </div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                ${success}
            </div>
            
            <!-- Thông báo chờ xác nhận -->
            <div class="pending-notification">
                <i class="fas fa-clock"></i>
                <h4>Đang chờ xác nhận</h4>
                <p>Chứng chỉ của bạn đã được gửi thành công! Admin sẽ kiểm tra và phê duyệt trong 24-48 giờ.</p>
                <p><strong>Bạn sẽ được thông báo qua email khi có kết quả.</strong></p>
                <div class="mt-3">
                    <a href="${pageContext.request.contextPath}/HomeServlet" class="btn btn-light">
                        <i class="fas fa-home"></i>
                        Về trang chủ
                    </a>
                </div>
            </div>
        </c:if>
        
        <c:if test="${empty success}">
            <p class="text-muted mb-4">
                Xin chào <strong>${authUser.fullName}</strong>! 
                Để trở thành giáo viên, vui lòng upload chứng chỉ giảng dạy của bạn.
            </p>
            
            <div class="requirements mb-4">
                <h5><i class="fas fa-info-circle text-info"></i> Yêu cầu:</h5>
                <ul class="text-start text-muted">
                    <li>File phải có định dạng PDF</li>
                    <li>Kích thước tối đa: 10MB</li>
                    <li>Chứng chỉ phải còn hiệu lực</li>
                    <li>Admin sẽ kiểm tra và phê duyệt trong 24-48 giờ</li>
                </ul>
            </div>
            
            <form id="uploadForm" action="${pageContext.request.contextPath}/upload-certificate" method="post" enctype="multipart/form-data">
                <div class="upload-area" id="uploadArea">
                    <div class="upload-icon">
                        <i class="fas fa-cloud-upload-alt"></i>
                    </div>
                    <h5>Kéo thả file PDF vào đây</h5>
                    <p class="text-muted">hoặc click để chọn file</p>
                    <input type="file" name="certificate" id="certificateInput" accept=".pdf" style="display: none;">
                </div>
                
                <div class="file-info" id="fileInfo">
                    <div class="d-flex align-items-center">
                        <i class="fas fa-file-pdf text-danger me-3"></i>
                        <div class="text-start">
                            <strong id="fileName"></strong>
                            <br>
                            <small class="text-muted" id="fileSize"></small>
                        </div>
                        <button type="button" class="btn btn-sm btn-outline-danger ms-auto" id="removeFile">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </div>
                
                <div class="progress" id="progressBar">
                    <div class="progress-bar" role="progressbar" style="width: 0%"></div>
                </div>
                
                <button type="submit" class="btn btn-upload" id="submitBtn" disabled>
                    <i class="fas fa-upload"></i>
                    Upload chứng chỉ
                </button>
            </form>
            
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/HomeServlet" class="btn btn-outline-secondary">
                    <i class="fas fa-home"></i>
                    Về trang chủ
                </a>
            </div>
        </c:if>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="<c:url value='/js/upload-certificate.js'/>"></script>
</body>
</html> 