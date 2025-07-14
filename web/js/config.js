// File cấu hình chung cho ứng dụng
// Đường dẫn context sẽ được inject từ server-side

// Cấu hình API endpoints
const API_CONFIG = {
    // Search APIs
    SEARCH_VOCABULARY: '/SearchVocabulary',
    SEARCH_KANJI: '/SearchKanji', 
    SEARCH_COURSE: '/SearchCourse',
    
    // Authentication APIs
    LOGIN: '/Login',
    LOGOUT: '/Logout',
    REGISTER: '/Register',
    FORGOT_PASSWORD: '/ForgotPassword',
    RESET_PASSWORD: '/ResetPassword',
    VERIFY_OTP: '/VerifyOtp',
    SEND_OTP: '/SendOtp',
    
    // Course APIs
    COURSES: '/Courses',
    COURSE_DETAIL: '/CourseDetail',
    CREATE_COURSE: '/CreateCourse',
    EDIT_COURSE: '/EditCourse',
    DELETE_COURSE: '/DeleteCourse',
    UPLOAD_LESSON_MATERIAL: '/UploadLessonMaterial',
    STUDY_LESSON: '/StudyLesson',
    
    // Quiz APIs
    DO_QUIZ: '/DoQuiz',
    EDIT_QUIZ: '/EditQuiz',
    
    // Flashcard APIs
    FLASHCARD: '/Flashcard',
    CREATE_FLASHCARD: '/CreateFlashcard',
    DELETE_FLASHCARD: '/DeleteFlashcard',
    VIEW_FLASHCARD: '/ViewFlashcard',
    
    // Payment APIs
    CREATE_PAYMENT: '/CreatePayment',
    PAYMENT_PAGE: '/PaymentPage',
    CANCEL_PAYMENT: '/CancelPayment',
    RETURN_FROM_PAYOS: '/ReturnFromPayOS',
    
    // Profile APIs
    PROFILE: '/Profile',
    EDIT_PROFILE: '/EditProfile',
    CHANGE_AVATAR: '/ChangeAvatar',
    
    // Chat APIs
    CHAT_WEBSOCKET: '/chat',
    GET_CHAT_HISTORY: '/getChatHistory',
    SEARCH_USERS: '/searchUsers',
    CHAT_USERS: '/chatUsers',
    CHECK_BLOCK: '/checkBlock',
    BLOCK_USER: '/blockUser',
    CHATBOT_CONFIG: '/chatbot-config',
    
    // Video call APIs
    GET_DAILY_TOKEN: '/getDailyToken',
    
    // Admin APIs
    ADMIN_HOME: '/admin/AdminHome',
    USER_MANAGEMENT: '/admin/UserManagement',
    COURSE_MANAGEMENT: '/admin/CourseManagement',
    TEACHER_APPROVAL: '/admin/TeacherApproval',
    PREMIUM_PLAN_MANAGEMENT: '/admin/PremiumPlanManagement',
    BULK_EMAIL: '/admin/BulkEmail',
    USER_STATS: '/admin/UserStats',
    COURSE_STATS: '/admin/CourseStats',
    REVENUE_STATS: '/admin/RevenueStats',
    
    // Teacher APIs
    TEACHER_DASHBOARD: '/teacher/teacher_dashboard',
    
    // Chart APIs
    ENROLLMENT_STATS: '/chart/Enrollment',
    REGISTRATION_STATS: '/chart/Registration',
    PAYMENT_STATUS_STATS: '/chart/PaymentStatus',
    PAYMENT_TRENDS: '/chart/PaymentTrends',
    REVENUE_STATS_CHART: '/chart/RevenueStats',
    USER_ROLE_STATS: '/chart/UserRoleStats',
    USER_STATS_CHART: '/chart/UserStats'
};

// Cấu hình đường dẫn tài nguyên
const RESOURCE_CONFIG = {
    // Images
    AVATAR_PATH: '/assets/avatar/',
    VOCAB_IMAGE_PATH: '/imgvocab/',
    COURSE_THUMBNAIL_PATH: '/image/',
    
    // CSS
    CSS_PATH: '/css/',
    
    // JS
    JS_PATH: '/js/',
    
    // Files
    FILES_PATH: '/files/'
};

// Cấu hình trang JSP
const PAGE_CONFIG = {
    // Main pages
    HOME: '/index.jsp',
    LOGIN: '/LoginJSP/LoginIndex.jsp',
    SIGNUP: '/LoginJSP/SignUp.jsp',
    FORGOT_PASSWORD_PAGE: '/LoginJSP/ForgotPassword.jsp',
    CHANGE_PASSWORD: '/LoginJSP/ChangePassword.jsp',
    
    // Course pages
    COURSES: '/Course.jsp',
    COURSE_DETAIL: '/course-detail.jsp',
    CREATE_COURSE: '/create_course.jsp',
    EDIT_COURSE: '/update_course.jsp',
    STUDY: '/study.jsp',
    
    // Search pages
    SEARCH: '/search.jsp',
    SEARCH_SIDEBAR: '/Search/search.jsp',
    
    // Quiz pages
    DO_QUIZ: '/do-quiz.jsp',
    QUIZ_RESULT: '/quiz-result.jsp',
    
    // Flashcard pages
    FLASHCARD: '/flashcard.jsp',
    CREATE_FLASHCARD: '/create-flashcard.jsp',
    VIEW_FLASHCARD: '/view-flashcard.jsp',
    
    // Payment pages
    PAYMENT: '/PaymentJSP/Payment.jsp',
    PAYMENT_SUCCESS: '/PaymentJSP/PaymentSuccess.jsp',
    PAYMENT_CANCEL: '/PaymentJSP/PaymentCancel.jsp',
    PAYMENT_QR: '/PaymentJSP/PaymentQR.jsp',
    
    // Profile pages
    PROFILE: '/Profile/profile-view.jsp',
    PROFILE_EDIT: '/Profile/profile-edit.jsp',
    
    // Chat pages
    CHAT: '/chatrealtime.jsp',
    CHAT_BOX: '/chatBoxjsp/chatBox.jsp',
    
    // Video call pages
    VIDEO_CALL: '/videocall.jsp',
    MEETING: '/meeting.jsp',
    
    // Admin pages
    ADMIN_DASHBOARD: '/admin/dashboard.jsp',
    USER_MANAGEMENT_PAGE: '/userManagement.jsp',
    TEACHER_APPROVAL_PAGE: '/admin/teacher-approval.jsp',
    PREMIUM_PLANS: '/admin/premium-plans.jsp',
    ADD_PREMIUM_PLAN: '/admin/add-premium-plan.jsp',
    EDIT_PREMIUM_PLAN: '/admin/edit-premium-plan.jsp',
    BULK_EMAIL_PAGE: '/BulkEmailAdmin.jsp',
    USER_STATS_PAGE: '/userstatistic.jsp',
    COURSE_STATS_PAGE: '/coursestatistic.jsp',
    REVENUE_STATS_PAGE: '/revenue_stats.jsp',
    
    // Teacher pages
    TEACHER_DASHBOARD_PAGE: '/teacher_dashboard.jsp',
    
    // Other pages
    INTRODUCE: '/introduce.jsp',
    MENU: '/Menu.jsp',
    CALENDAR: '/calendar.jsp'
};

// Hàm helper để tạo URL đầy đủ
function getApiUrl(endpoint) {
    return window.contextPath + endpoint;
}

function getResourceUrl(resourcePath) {
    return window.contextPath + resourcePath;
}

function getPageUrl(pagePath) {
    return window.contextPath + pagePath;
}

// Export config để sử dụng trong các file khác
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        API_CONFIG,
        RESOURCE_CONFIG,
        PAGE_CONFIG,
        getApiUrl,
        getResourceUrl,
        getPageUrl
    };
} 