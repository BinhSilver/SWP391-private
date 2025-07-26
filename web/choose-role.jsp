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
            background: linear-gradient(135deg, #bf7026 0%, #e94f64 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Poppins", sans-serif;
        }
        .role-card {
            background: white;
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            text-align: center;
            max-width: 500px;
            width: 100%;
        }
        .role-option {
            border: 2px solid #e9ecef;
            border-radius: 15px;
            padding: 30px;
            margin: 20px 0;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        .role-option:hover {
            border-color: #bf7026;
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(191, 112, 38, 0.2);
        }
        .role-option.selected {
            border-color: #bf7026;
            background: linear-gradient(135deg, #bf7026 0%, #e94f64 100%);
            color: white;
        }
        .role-option.selected .text-muted {
            color: rgba(255, 255, 255, 0.8) !important;
        }
        .role-icon {
            font-size: 3rem;
            margin-bottom: 15px;
        }
        .btn-continue {
            background: linear-gradient(135deg, #bf7026 0%, #e94f64 100%);
            border: none;
            border-radius: 25px;
            padding: 12px 40px;
            color: white;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-continue:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(191, 112, 38, 0.3);
            color: white;
        }
        .btn-continue:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }
        .text-primary {
            color: #bf7026 !important;
        }
        .text-warning {
            color: #e94f64 !important;
        }
        h2 {
            color: #bf7026;
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
            
            roleOptions.forEach(option => {
                option.addEventListener('click', function() {
                    // Remove selected class from all options
                    roleOptions.forEach(opt => opt.classList.remove('selected'));
                    
                    // Add selected class to clicked option
                    this.classList.add('selected');
                    
                    // Set the selected role
                    const role = this.getAttribute('data-role');
                    selectedRoleInput.value = role;
                    
                    // Enable continue button
                    continueBtn.disabled = false;
                });
            });
        });
    </script>
</body>
</html> 