// Script để tự động cập nhật tất cả file JSP với config mới
// Chạy script này để thêm config vào tất cả file JSP

const fs = require('fs');
const path = require('path');

// Danh sách các file JSP cần cập nhật
const jspFiles = [
    'web/index.jsp',
    'web/Course.jsp',
    'web/course-detail.jsp',
    'web/create_course.jsp',
    'web/update_course.jsp',
    'web/study.jsp',
    'web/do-quiz.jsp',
    'web/quiz-result.jsp',
    'web/flashcard.jsp',
    'web/create-flashcard.jsp',
    'web/view-flashcard.jsp',
    'web/PaymentJSP/Payment.jsp',
    'web/PaymentJSP/PaymentSuccess.jsp',
    'web/PaymentJSP/PaymentCancel.jsp',
    'web/PaymentJSP/PaymentQR.jsp',
    'web/Profile/profile-view.jsp',
    'web/Profile/profile-edit.jsp',
    'web/chatrealtime.jsp',
    'web/chatBoxjsp/chatBox.jsp',
    'web/videocall.jsp',
    'web/meeting.jsp',
    'web/admin/dashboard.jsp',
    'web/userManagement.jsp',
    'web/admin/teacher-approval.jsp',
    'web/admin/premium-plans.jsp',
    'web/admin/add-premium-plan.jsp',
    'web/admin/edit-premium-plan.jsp',
    'web/BulkEmailAdmin.jsp',
    'web/userstatistic.jsp',
    'web/coursestatistic.jsp',
    'web/revenue_stats.jsp',
    'web/teacher_dashboard.jsp',
    'web/introduce.jsp',
    'web/Menu.jsp',
    'web/calendar.jsp',
    'web/LoginJSP/LoginIndex.jsp',
    'web/LoginJSP/SignUp.jsp',
    'web/LoginJSP/ForgotPassword.jsp',
    'web/LoginJSP/ChangePassword.jsp',
    'web/LoginJSP/SignIn.jsp',
    'web/LoginJSP/bear.jsp'
];

// Template để thêm vào đầu file JSP
const configTemplate = `
<script>
    // Inject context path vào window object
    window.contextPath = '\${pageContext.request.contextPath}';
    console.log('Context path loaded:', window.contextPath);
</script>
<script src="\${pageContext.request.contextPath}/js/config.js"></script>
`;

function updateJspFile(filePath) {
    try {
        if (!fs.existsSync(filePath)) {
            console.log(`File không tồn tại: ${filePath}`);
            return;
        }

        const content = fs.readFileSync(filePath, 'utf8');
        
        // Kiểm tra xem file đã có config chưa
        if (content.includes('window.contextPath') || content.includes('config.js')) {
            console.log(`File đã có config: ${filePath}`);
            return;
        }

        // Tìm vị trí để thêm config (sau DOCTYPE hoặc sau thẻ html)
        let insertPosition = 0;
        
        if (content.includes('<!DOCTYPE html>')) {
            insertPosition = content.indexOf('<!DOCTYPE html>') + '<!DOCTYPE html>'.length;
        } else if (content.includes('<html>')) {
            insertPosition = content.indexOf('<html>') + '<html>'.length;
        } else if (content.includes('<head>')) {
            insertPosition = content.indexOf('<head>') + '<head>'.length;
        }

        // Thêm config vào vị trí thích hợp
        const newContent = content.slice(0, insertPosition) + configTemplate + content.slice(insertPosition);
        
        fs.writeFileSync(filePath, newContent, 'utf8');
        console.log(`Đã cập nhật: ${filePath}`);
        
    } catch (error) {
        console.error(`Lỗi khi cập nhật ${filePath}:`, error.message);
    }
}

// Cập nhật tất cả file JSP
console.log('Bắt đầu cập nhật các file JSP...');
jspFiles.forEach(updateJspFile);
console.log('Hoàn thành cập nhật!');

// Hướng dẫn sử dụng
console.log('\n=== HƯỚNG DẪN SỬ DỤNG ===');
console.log('1. Chạy script này để thêm config vào tất cả file JSP');
console.log('2. Cập nhật các file JavaScript để sử dụng config');
console.log('3. Test ứng dụng để đảm bảo tất cả đường dẫn hoạt động');
console.log('4. Xem file CONFIG_README.md để biết thêm chi tiết'); 