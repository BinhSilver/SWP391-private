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
    <style>
        body {
            background: linear-gradient(135deg, #bf7026 0%, #e94f64 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            font-family: "Poppins", sans-serif;
        }
        .upload-card {
            background: white;
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            text-align: center;
            max-width: 600px;
            width: 100%;
        }
        .upload-area {
            border: 3px dashed #bf7026;
            border-radius: 15px;
            padding: 40px;
            margin: 20px 0;
            cursor: pointer;
            transition: all 0.3s ease;
            background: #f8f9fa;
        }
        .upload-area:hover {
            border-color: #e94f64;
            background: #e9ecef;
        }
        .upload-area.dragover {
            border-color: #28a745;
            background: #d4edda;
        }
        .upload-icon {
            font-size: 4rem;
            color: #bf7026;
            margin-bottom: 20px;
        }
        .file-info {
            background: #e9ecef;
            border-radius: 10px;
            padding: 15px;
            margin: 15px 0;
            display: none;
        }
        .btn-upload {
            background: linear-gradient(135deg, #bf7026 0%, #e94f64 100%);
            border: none;
            border-radius: 25px;
            padding: 12px 40px;
            color: white;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-upload:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(191, 112, 38, 0.3);
            color: white;
        }
        .btn-upload:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }
        .alert {
            border-radius: 15px;
            border: none;
        }
        .progress {
            height: 10px;
            border-radius: 5px;
            margin: 15px 0;
            display: none;
        }
        .text-primary {
            color: #bf7026 !important;
        }
        .text-info {
            color: #bf7026 !important;
        }
        h2 {
            color: #bf7026;
        }
        .pending-notification {
            background: linear-gradient(135deg, #bf7026 0%, #e94f64 100%);
            color: white;
            border-radius: 15px;
            padding: 20px;
            margin: 20px 0;
            text-align: center;
        }
        .pending-notification i {
            font-size: 2rem;
            margin-bottom: 10px;
        }
    </style>
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
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const uploadArea = document.getElementById('uploadArea');
            const certificateInput = document.getElementById('certificateInput');
            const fileInfo = document.getElementById('fileInfo');
            const fileName = document.getElementById('fileName');
            const fileSize = document.getElementById('fileSize');
            const removeFile = document.getElementById('removeFile');
            const submitBtn = document.getElementById('submitBtn');
            const progressBar = document.getElementById('progressBar');
            const uploadForm = document.getElementById('uploadForm');
            
            // Click upload area to select file
            uploadArea.addEventListener('click', function() {
                certificateInput.click();
            });
            
            // Drag and drop functionality
            uploadArea.addEventListener('dragover', function(e) {
                e.preventDefault();
                uploadArea.classList.add('dragover');
            });
            
            uploadArea.addEventListener('dragleave', function(e) {
                e.preventDefault();
                uploadArea.classList.remove('dragover');
            });
            
            uploadArea.addEventListener('drop', function(e) {
                e.preventDefault();
                uploadArea.classList.remove('dragover');
                
                const files = e.dataTransfer.files;
                if (files.length > 0) {
                    certificateInput.files = files;
                    handleFileSelect(files[0]);
                }
            });
            
            // File input change
            certificateInput.addEventListener('change', function(e) {
                if (e.target.files.length > 0) {
                    handleFileSelect(e.target.files[0]);
                }
            });
            
            // Remove file
            removeFile.addEventListener('click', function() {
                certificateInput.value = '';
                fileInfo.style.display = 'none';
                submitBtn.disabled = true;
                progressBar.style.display = 'none';
            });
            
            function handleFileSelect(file) {
                // Check file type
                if (!file.type.includes('pdf')) {
                    alert('Chỉ chấp nhận file PDF!');
                    return;
                }
                
                // Check file size (10MB)
                if (file.size > 10 * 1024 * 1024) {
                    alert('File quá lớn! Kích thước tối đa là 10MB.');
                    return;
                }
                
                // Display file info
                fileName.textContent = file.name;
                fileSize.textContent = formatFileSize(file.size);
                fileInfo.style.display = 'block';
                submitBtn.disabled = false;
            }
            
            function formatFileSize(bytes) {
                if (bytes === 0) return '0 Bytes';
                const k = 1024;
                const sizes = ['Bytes', 'KB', 'MB', 'GB'];
                const i = Math.floor(Math.log(bytes) / Math.log(k));
                return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
            }
            
            // Form submission with progress
            uploadForm.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const formData = new FormData(uploadForm);
                const xhr = new XMLHttpRequest();
                
                // Show progress bar
                progressBar.style.display = 'block';
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang upload...';
                
                xhr.upload.addEventListener('progress', function(e) {
                    if (e.lengthComputable) {
                        const percentComplete = (e.loaded / e.total) * 100;
                        progressBar.querySelector('.progress-bar').style.width = percentComplete + '%';
                    }
                });
                
                xhr.addEventListener('load', function() {
                    if (xhr.status === 200) {
                        // Reload page to show success message
                        window.location.reload();
                    } else {
                        alert('Có lỗi xảy ra khi upload file. Vui lòng thử lại.');
                        submitBtn.disabled = false;
                        submitBtn.innerHTML = '<i class="fas fa-upload"></i> Upload chứng chỉ';
                        progressBar.style.display = 'none';
                    }
                });
                
                xhr.addEventListener('error', function() {
                    alert('Có lỗi xảy ra khi upload file. Vui lòng thử lại.');
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = '<i class="fas fa-upload"></i> Upload chứng chỉ';
                    progressBar.style.display = 'none';
                });
                
                xhr.open('POST', uploadForm.action);
                xhr.send(formData);
            });
        });
    </script>
</body>
</html> 