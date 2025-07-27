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
    
    // Add entrance animation
    if (uploadArea) {
        uploadArea.style.opacity = '0';
        uploadArea.style.transform = 'translateY(20px)';
        setTimeout(() => {
            uploadArea.style.transition = 'all 0.6s ease';
            uploadArea.style.opacity = '1';
            uploadArea.style.transform = 'translateY(0)';
        }, 300);
    }
    
    // Click upload area to select file
    if (uploadArea) {
        uploadArea.addEventListener('click', function() {
            certificateInput.click();
        });
    }
    
    // Drag and drop functionality
    if (uploadArea) {
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
    }
    
    // File input change
    if (certificateInput) {
        certificateInput.addEventListener('change', function(e) {
            if (e.target.files.length > 0) {
                handleFileSelect(e.target.files[0]);
            }
        });
    }
    
    // Remove file with animation
    if (removeFile) {
        removeFile.addEventListener('click', function() {
            fileInfo.style.transform = 'scale(0.9)';
            fileInfo.style.opacity = '0';
            setTimeout(() => {
                certificateInput.value = '';
                fileInfo.style.display = 'none';
                fileInfo.style.transform = 'scale(1)';
                fileInfo.style.opacity = '1';
                submitBtn.disabled = true;
                progressBar.style.display = 'none';
            }, 200);
        });
    }
    
    function handleFileSelect(file) {
        // Check file type
        if (!file.type.includes('pdf')) {
            showNotification('Chỉ chấp nhận file PDF!', 'error');
            return;
        }
        
        // Check file size (10MB)
        if (file.size > 10 * 1024 * 1024) {
            showNotification('File quá lớn! Kích thước tối đa là 10MB.', 'error');
            return;
        }
        
        // Display file info with animation
        fileName.textContent = file.name;
        fileSize.textContent = formatFileSize(file.size);
        fileInfo.style.display = 'block';
        fileInfo.style.opacity = '0';
        fileInfo.style.transform = 'scale(0.9)';
        setTimeout(() => {
            fileInfo.style.transition = 'all 0.3s ease';
            fileInfo.style.opacity = '1';
            fileInfo.style.transform = 'scale(1)';
        }, 50);
        
        submitBtn.disabled = false;
        submitBtn.style.transform = 'scale(1.05)';
        setTimeout(() => {
            submitBtn.style.transform = 'scale(1)';
        }, 200);
    }
    
    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
    
    function showNotification(message, type) {
        const notification = document.createElement('div');
        notification.className = `alert alert-${type === 'error' ? 'danger' : 'success'} notification-toast`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            min-width: 300px;
            animation: slideInRight 0.3s ease;
        `;
        notification.innerHTML = `
            <i class="fas fa-${type === 'error' ? 'exclamation-triangle' : 'check-circle'}"></i>
            ${message}
        `;
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => {
                if (document.body.contains(notification)) {
                    document.body.removeChild(notification);
                }
            }, 300);
        }, 3000);
    }
    
    // Form submission with progress
    if (uploadForm) {
        uploadForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(uploadForm);
            const xhr = new XMLHttpRequest();
            
            // Show progress bar with animation
            progressBar.style.display = 'block';
            progressBar.style.opacity = '0';
            setTimeout(() => {
                progressBar.style.transition = 'opacity 0.3s ease';
                progressBar.style.opacity = '1';
            }, 50);
            
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang upload...';
            submitBtn.style.transform = 'scale(0.95)';
            
            xhr.upload.addEventListener('progress', function(e) {
                if (e.lengthComputable) {
                    const percentComplete = (e.loaded / e.total) * 100;
                    const progressBarElement = progressBar.querySelector('.progress-bar');
                    progressBarElement.style.width = percentComplete + '%';
                    
                    // Add color transition based on progress
                    if (percentComplete < 50) {
                        progressBarElement.style.background = 'linear-gradient(135deg, #e94f64 0%, #d63384 100%)';
                    } else if (percentComplete < 90) {
                        progressBarElement.style.background = 'linear-gradient(135deg, #ffc107 0%, #ff9800 100%)';
                    } else {
                        progressBarElement.style.background = 'linear-gradient(135deg, #28a745 0%, #20c997 100%)';
                    }
                }
            });
            
            xhr.addEventListener('load', function() {
                if (xhr.status === 200) {
                    showNotification('Upload thành công! Đang chuyển về trang chủ...', 'success');
                    setTimeout(() => {
                        window.location.href = window.location.origin + '/Wasabii/HomeServlet';
                    }, 1500);
                } else {
                    showNotification('Có lỗi xảy ra khi upload file. Vui lòng thử lại.', 'error');
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = '<i class="fas fa-upload"></i> Upload chứng chỉ';
                    submitBtn.style.transform = 'scale(1)';
                    progressBar.style.display = 'none';
                }
            });
            
            xhr.addEventListener('error', function() {
                showNotification('Có lỗi xảy ra khi upload file. Vui lòng thử lại.', 'error');
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-upload"></i> Upload chứng chỉ';
                submitBtn.style.transform = 'scale(1)';
                progressBar.style.display = 'none';
            });
            
            xhr.open('POST', uploadForm.action);
            xhr.send(formData);
        });
    }
    
    // Add keyboard navigation
    if (uploadArea) {
        uploadArea.setAttribute('tabindex', '0');
        uploadArea.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                this.click();
            }
        });
    }
}); 