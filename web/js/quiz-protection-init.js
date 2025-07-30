/**
 * Quiz Protection Initializer
 * Khởi tạo hệ thống bảo vệ quiz cho mọi trang quiz
 */

// Hàm khởi tạo Quiz Protection
function initQuizProtection(options = {}) {
    // Lấy lessonId từ URL hoặc từ server
    const urlParams = new URLSearchParams(window.location.search);
    const lessonId = urlParams.get('lessonId') || options.lessonId || '1';
    
    // Tạo URL redirect với lessonId hiện tại
    const currentPath = window.location.pathname;
    const autoSubmitUrl = `${window.location.origin}${currentPath}?lessonId=${lessonId}`;
    
    // Cấu hình mặc định
    const config = {
        maxViolations: options.maxViolations || 3,
        autoSubmitUrl: autoSubmitUrl,
        enabled: options.enabled !== false,
        lessonId: lessonId
    };
    
    // Khởi tạo Quiz Protection System
    const quizProtection = new QuizProtection(config);
    
    // Log thông tin khởi tạo
    console.log(`Quiz Protection đã được khởi tạo cho lessonId: ${lessonId}`);
    console.log('Cấu hình:', config);
    
    // Trả về instance để có thể điều khiển từ bên ngoài
    return quizProtection;
}

// Hàm khởi tạo tự động khi trang load
function autoInitQuizProtection() {
    // Kiểm tra xem có phải trang quiz không
    const isQuizPage = window.location.pathname.includes('doQuiz') || 
                      window.location.pathname.includes('quiz') ||
                      document.querySelector('form[action="doQuiz"]');
    
    if (isQuizPage) {
        return initQuizProtection();
    }
    
    return null;
}

// Tự động khởi tạo khi DOM ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', autoInitQuizProtection);
} else {
    autoInitQuizProtection();
}

// Export cho sử dụng trong module
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        initQuizProtection,
        autoInitQuizProtection
    };
} 