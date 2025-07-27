<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chọn vai trò - Wasabii</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #e94f64 0%, #d63384 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "JetBrains Mono", "Fira Code", monospace;
        }
        .role-card {
            background: white;
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 20px 40px rgba(233, 79, 100, 0.15);
            text-align: center;
            max-width: 500px;
            width: 100%;
            border: 1px solid rgba(233, 79, 100, 0.1);
        }
        .role-option {
            border: 2px solid #e9ecef;
            border-radius: 15px;
            padding: 30px;
            margin: 20px 0;
            cursor: pointer;
            transition: all 0.3s ease;
            background: #fafafa;
        }
        .role-option:hover {
            border-color: #e94f64;
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(233, 79, 100, 0.2);
            background: white;
        }
        .role-option.selected {
            border-color: #e94f64;
            background: linear-gradient(135deg, #e94f64 0%, #d63384 100%);
            color: white;
            box-shadow: 0 10px 25px rgba(233, 79, 100, 0.3);
        }
        .role-option.selected .text-muted {
            color: rgba(255, 255, 255, 0.8) !important;
        }
        .role-option.selected ul li {
            color: rgba(255, 255, 255, 0.8) !important;
        }
        .role-icon {
            font-size: 3rem;
            margin-bottom: 15px;
            transition: all 0.3s ease;
        }
        .role-option:hover .role-icon {
            transform: scale(1.1);
        }
        .role-option.selected .role-icon {
            transform: scale(1.1);
        }
        .btn-continue {
            background: linear-gradient(135deg, #e94f64 0%, #d63384 100%);
            border: none;
            border-radius: 25px;
            padding: 12px 40px;
            color: white;
            font-weight: 600;
            transition: all 0.3s ease;
            font-family: "JetBrains Mono", "Fira Code", monospace;
        }
        .btn-continue:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(233, 79, 100, 0.3);
            color: white;
            background: linear-gradient(135deg, #d63384 0%, #e94f64 100%);
        }
        .btn-continue:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
            background: #ccc;
        }
        .text-primary {
            color: #e94f64 !important;
        }
        .text-warning {
            color: #d63384 !important;
        }
        h2 {
            color: #e94f64;
            font-weight: 700;
        }
        .role-option h4 {
            font-weight: 600;
            margin-bottom: 10px;
        }
        .role-option ul {
            list-style: none;
            padding-left: 0;
        }
        .role-option ul li {
            padding: 5px 0;
            position: relative;
            padding-left: 20px;
        }
        .role-option ul li:before {
            content: "✓";
            position: absolute;
            left: 0;
            color: #e94f64;
            font-weight: bold;
        }
        .role-option.selected ul li:before {
            color: white;
        }
        .role-option small {
            display: block;
            margin-top: 15px;
            padding: 10px;
            background: rgba(214, 51, 132, 0.1);
            border-radius: 8px;
            border-left: 3px solid #d63384;
        }
        .role-option.selected small {
            background: rgba(255, 255, 255, 0.2);
            border-left-color: white;
        }
        @media (max-width: 768px) {
            .role-card {
                margin: 20px;
                padding: 30px 20px;
            }
            .role-option {
                padding: 20px;
            }
            .role-icon {
                font-size: 2.5rem;
            }
            h2 {
                font-size: 1.5rem;
            }
            .role-option h4 {
                font-size: 1.2rem;
            }
        }
        
        @media (max-width: 480px) {
            .role-card {
                margin: 10px;
                padding: 20px 15px;
            }
            .role-option {
                padding: 15px;
                margin: 15px 0;
            }
            .role-icon {
                font-size: 2rem;
            }
            .btn-continue {
                padding: 10px 30px;
                font-size: 0.9rem;
            }
        }
        
        /* Accessibility improvements */
        .role-option:focus {
            outline: 3px solid #e94f64;
            outline-offset: 2px;
        }
        
        .btn-continue:focus {
            outline: 3px solid #d63384;
            outline-offset: 2px;
        }
        
        /* Loading state */
        .loading {
            opacity: 0.7;
            pointer-events: none;
        }
        
        /* Success animation */
        .success-animation {
            animation: successPulse 0.6s ease-in-out;
        }
        
        @keyframes successPulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.05); }
            100% { transform: scale(1); }
        }
    </style>
</head>
<body>
    <div class="role-card">
        <h2 class="mb-4">
            <i class="fas fa-user-plus text-primary"></i>
            Chọn vai trò của bạn
        </h2>
        <p class="text-muted mb-4">Xin chào <strong>${authUser.fullName}</strong>! Vui lòng chọn vai trò phù hợp với bạn.</p>
        
        <form id="roleForm" action="${pageContext.request.contextPath}/choose-role" method="post">
            <div class="role-option" data-role="student">
                <div class="role-icon">
                    <i class="fas fa-graduation-cap"></i>
                </div>
                <h4>Học sinh</h4>
                <p class="text-muted">Tôi muốn học tiếng Nhật</p>
                <ul class="text-start text-muted">
                    <li>Tham gia các khóa học</li>
                    <li>Làm bài tập và quiz</li>
                    <li>Sử dụng flashcard</li>
                    <li>Chat với AI</li>
                </ul>
            </div>
            
            <div class="role-option" data-role="teacher">
                <div class="role-icon">
                    <i class="fas fa-chalkboard-teacher"></i>
                </div>
                <h4>Giáo viên</h4>
                <p class="text-muted">Tôi muốn giảng dạy tiếng Nhật</p>
                <ul class="text-start text-muted">
                    <li>Tạo và quản lý khóa học</li>
                    <li>Upload tài liệu giảng dạy</li>
                    <li>Tương tác với học sinh</li>
                    <li>Kiếm thu nhập từ giảng dạy</li>
                </ul>
                <small class="text-warning">
                    <i class="fas fa-info-circle"></i>
                    Cần upload chứng chỉ giảng dạy để admin phê duyệt
                </small>
            </div>
            
            <input type="hidden" name="role" id="selectedRole">
            
            <button type="submit" class="btn btn-continue" id="continueBtn" disabled>
                <i class="fas fa-arrow-right"></i>
                Tiếp tục
            </button>
        </form>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const roleOptions = document.querySelectorAll('.role-option');
            const selectedRoleInput = document.getElementById('selectedRole');
            const continueBtn = document.getElementById('continueBtn');
            
            // Add entrance animation
            roleOptions.forEach((option, index) => {
                option.style.opacity = '0';
                option.style.transform = 'translateY(20px)';
                setTimeout(() => {
                    option.style.transition = 'all 0.5s ease';
                    option.style.opacity = '1';
                    option.style.transform = 'translateY(0)';
                }, index * 200);
            });
            
            roleOptions.forEach(option => {
                option.addEventListener('click', function() {
                    // Add click animation
                    this.style.transform = 'scale(0.95)';
                    setTimeout(() => {
                        this.style.transform = 'scale(1)';
                    }, 150);
                    
                    // Remove selected class from all options
                    roleOptions.forEach(opt => opt.classList.remove('selected'));
                    
                    // Add selected class to clicked option
                    this.classList.add('selected');
                    
                    // Set the selected role
                    const role = this.getAttribute('data-role');
                    selectedRoleInput.value = role;
                    
                    // Enable continue button with animation
                    continueBtn.disabled = false;
                    continueBtn.style.transform = 'scale(1.05)';
                    setTimeout(() => {
                        continueBtn.style.transform = 'scale(1)';
                    }, 200);
                });
            });
            
            // Add hover sound effect (optional)
            roleOptions.forEach(option => {
                option.addEventListener('mouseenter', function() {
                    this.style.transform = 'translateY(-5px) scale(1.02)';
                });
                
                option.addEventListener('mouseleave', function() {
                    if (!this.classList.contains('selected')) {
                        this.style.transform = 'translateY(0) scale(1)';
                    }
                });
            });
            
            // Form submission with loading state
            const form = document.getElementById('roleForm');
            form.addEventListener('submit', function(e) {
                if (!selectedRoleInput.value) {
                    e.preventDefault();
                    alert('Vui lòng chọn vai trò của bạn');
                    return;
                }
                
                // Add loading state
                continueBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
                continueBtn.classList.add('loading');
                continueBtn.disabled = true;
                
                // Add success animation
                setTimeout(() => {
                    continueBtn.classList.add('success-animation');
                }, 100);
            });
            
            // Keyboard navigation
            roleOptions.forEach((option, index) => {
                option.setAttribute('tabindex', '0');
                option.addEventListener('keydown', function(e) {
                    if (e.key === 'Enter' || e.key === ' ') {
                        e.preventDefault();
                        this.click();
                    }
                });
            });
        });
    </script>
</body>
</html> 