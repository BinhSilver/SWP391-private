/**
 * Quiz Protection System
 * Bảo vệ trang quiz khỏi các hành vi gian lận
 * 
 * Chức năng chính:
 * - Cấm chuột phải (contextmenu)
 * - Cấm F12 và các phím tắt developer tools
 * - Cấm chuyển tab quá 3 lần (tự động nộp bài)
 * - Cấm copy/paste/cut
 * - Cấm select text và drag
 * - Cấm console access
 * - Cấm print screen
 */

// ===== QUIZ PROTECTION CLASS =====
/**
 * QuizProtection - Class chính để bảo vệ quiz
 * Quản lý tất cả các biện pháp bảo vệ chống gian lận
 */
class QuizProtection {
    
    // ===== CONSTRUCTOR =====
    /**
     * Khởi tạo Quiz Protection System
     * @param {Object} options - Các tùy chọn cấu hình
     * @param {number} options.maxViolations - Số lần vi phạm tối đa (mặc định: 3)
     * @param {string} options.autoSubmitUrl - URL để auto submit (mặc định: doQuiz)
     * @param {boolean} options.enabled - Bật/tắt protection (mặc định: true)
     * @param {boolean} options.debugMode - Bật debug mode (mặc định: false)
     */
    constructor(options = {}) {
        console.log('=== [QuizProtection] Khởi tạo Quiz Protection System ===');
        console.log(`[QuizProtection] Options:`, options);
        
        // ===== INSTANCE VARIABLES =====
        this.violationCount = 0;                    // Số lần vi phạm hiện tại
        this.maxViolations = options.maxViolations || 3;  // Số lần vi phạm tối đa
        this.isSubmitted = false;                   // Flag kiểm tra đã nộp bài chưa
        this.autoSubmitUrl = options.autoSubmitUrl || 'http://localhost:8080/Wasabii/doQuiz?lessonId=1';  // URL auto submit
        this.enabled = options.enabled !== false;   // Mặc định bật protection
        this.debugMode = options.debugMode || false; // Debug mode
        this.isInitializing = true;                 // Flag để tránh warning khi khởi tạo
        
        // ===== LOG CONFIGURATION =====
        console.log(`[QuizProtection] Max violations: ${this.maxViolations}`);
        console.log(`[QuizProtection] Auto submit URL: ${this.autoSubmitUrl}`);
        console.log(`[QuizProtection] Enabled: ${this.enabled}`);
        console.log(`[QuizProtection] Debug mode: ${this.debugMode}`);
        
        // ===== RESET ON START =====
        // Reset hoàn toàn khi bắt đầu quiz
        this.resetAllOnStart();
        
        // ===== DEBUG MODE HANDLING =====
        // Bật debug mode nếu cần
        if (this.debugMode) {
            sessionStorage.setItem('quizDebugMode', 'true');
            console.log('[QuizProtection] Debug mode enabled - F12 will be allowed');
        }
        
        // ===== INITIALIZE PROTECTION =====
        if (this.enabled) {
            console.log(`[QuizProtection] Initializing protection...`);
            this.initProtection();
            console.log(`[QuizProtection] Protection initialized successfully!`);
        } else {
            console.log(`[QuizProtection] Protection disabled by configuration`);
        }
        
        // ===== INITIALIZATION PERIOD =====
        // Tắt flag khởi tạo sau 5 giây
        setTimeout(() => {
            this.isInitializing = false;
            console.log(`[QuizProtection] Initialization period ended - warnings will now be shown`);
        }, 5000);
        
        console.log('=== [QuizProtection] Khởi tạo hoàn tất ===');
    }

    // ===== INITIALIZE PROTECTION METHODS =====
    /**
     * Khởi tạo tất cả các biện pháp bảo vệ
     */
    initProtection() {
        console.log('=== [QuizProtection] Khởi tạo các biện pháp bảo vệ ===');
        
        // ===== RIGHT CLICK PREVENTION =====
        // Cấm chuột phải
        console.log(`[QuizProtection] Setting up right-click prevention...`);
        this.preventRightClick();
        
        // ===== KEYBOARD SHORTCUTS PREVENTION =====
        // Cấm F12 và các phím tắt
        console.log(`[QuizProtection] Setting up keyboard shortcuts prevention...`);
        this.preventKeyboardShortcuts();
        
        // ===== TAB SWITCH PREVENTION =====
        // Cấm thoát tab
        console.log(`[QuizProtection] Setting up tab switch prevention...`);
        this.preventTabSwitch();
        
        // ===== COPY/PASTE PREVENTION =====
        // Cấm copy/paste/cut
        console.log(`[QuizProtection] Setting up copy/paste prevention...`);
        this.preventCopyPaste();
        
        // ===== TEXT SELECTION PREVENTION =====
        // Cấm select text và drag
        console.log(`[QuizProtection] Setting up text selection prevention...`);
        this.preventTextSelection();
        
        // ===== CONSOLE ACCESS PREVENTION =====
        // Cấm console access
        console.log(`[QuizProtection] Setting up console access prevention...`);
        this.preventConsoleAccess();
        
        // ===== PRINT SCREEN PREVENTION =====
        // Cấm print screen
        console.log(`[QuizProtection] Setting up print screen prevention...`);
        this.preventPrintScreen();
        
        console.log('=== [QuizProtection] Quiz Protection System đã được kích hoạt thành công ===');
    }

    // ===== RIGHT CLICK PREVENTION =====
    /**
     * Ngăn chặn chuột phải và inspect element
     */
    preventRightClick() {
        // ===== CONTEXT MENU PREVENTION =====
        // Cấm menu chuột phải
        document.addEventListener('contextmenu', (e) => {
            e.preventDefault();
            this.handleViolation('Chuột phải bị cấm!', false);
            return false;
        });

        // ===== INSPECT ELEMENT PREVENTION =====
        // Cấm inspect element bằng chuột phải
        document.addEventListener('mousedown', (e) => {
            if (e.button === 2) {  // Right mouse button
                e.preventDefault();
                this.handleViolation('Inspect bằng chuột phải bị cấm!', false);
                return false;
            }
        });
    }

    preventKeyboardShortcuts() {
        document.addEventListener('keydown', (e) => {
            // F12 - Cho phép trong debug mode
            if (e.keyCode === 123) {
                // Kiểm tra debug mode
                const isDebugMode = sessionStorage.getItem('quizDebugMode') === 'true';
                if (isDebugMode) {
                    console.log('[QuizProtection] F12 allowed in debug mode');
                    return; // Cho phép F12
                }
                
                console.log(`[QuizProtection] F12 detected and blocked`);
                e.preventDefault();
                this.handleViolation('F12 bị cấm!', false);
                return false;
            }
            
            // Ctrl+Shift+I (Developer Tools)
            if (e.ctrlKey && e.shiftKey && e.keyCode === 73) {
                console.log(`[QuizProtection] Ctrl+Shift+I detected and blocked`);
                e.preventDefault();
                this.handleViolation('Developer Tools bị cấm!', false);
                return false;
            }
            
            // Ctrl+U (View Source)
            if (e.ctrlKey && e.keyCode === 85) {
                console.log(`[QuizProtection] Ctrl+U detected and blocked`);
                e.preventDefault();
                this.handleViolation('View Source bị cấm!', false);
                return false;
            }
            
            // Ctrl+Shift+C (Inspect Element)
            if (e.ctrlKey && e.shiftKey && e.keyCode === 67) {
                console.log(`[QuizProtection] Ctrl+Shift+C detected and blocked`);
                e.preventDefault();
                this.handleViolation('Inspect Element bị cấm!', false);
                return false;
            }
            
            // Alt+F4
            if (e.altKey && e.keyCode === 115) {
                console.log(`[QuizProtection] Alt+F4 detected and blocked`);
                e.preventDefault();
                this.handleViolation('Alt+F4 bị cấm!', false);
                return false;
            }
            
            // Ctrl+W (Close tab)
            if (e.ctrlKey && e.keyCode === 87) {
                console.log(`[QuizProtection] Ctrl+W detected and blocked`);
                e.preventDefault();
                this.handleViolation('Đóng tab bị cấm!', false);
                return false;
            }
            
            // Ctrl+Shift+W (Close window)
            if (e.ctrlKey && e.shiftKey && e.keyCode === 87) {
                console.log(`[QuizProtection] Ctrl+Shift+W detected and blocked`);
                e.preventDefault();
                this.handleViolation('Đóng window bị cấm!', false);
                return false;
            }
            
            // Print Screen
            if (e.keyCode === 44) {
                console.log(`[QuizProtection] Print Screen detected and blocked`);
                e.preventDefault();
                this.handleViolation('Print Screen bị cấm!', false);
                return false;
            }
        });
    }

    preventTabSwitch() {
        let tabSwitchCount = 0;
        const maxTabSwitches = 3;
        let lastSwitchTime = 0;
        const switchCooldown = 1000; // 1 giây cooldown giữa các lần chuyển tab
        
        console.log('=== [QuizProtection] Khởi tạo Tab Switch Protection ===');
        console.log(`[QuizProtection] Max tab switches: ${maxTabSwitches}`);
        console.log(`[QuizProtection] Switch cooldown: ${switchCooldown}ms`);
        
        // Khôi phục trạng thái từ sessionStorage nếu có
        const savedTabSwitchCount = sessionStorage.getItem('quizTabSwitchCount');
        if (savedTabSwitchCount) {
            const oldCount = tabSwitchCount;
            tabSwitchCount = parseInt(savedTabSwitchCount);
            this.tabSwitchCount = tabSwitchCount;
            console.log(`[QuizProtection] Restored tab switch count from sessionStorage: ${oldCount} -> ${tabSwitchCount}`);
        } else {
            console.log(`[QuizProtection] No saved tab switch count found, starting fresh`);
        }
        
        // Lưu trạng thái tab switch vào sessionStorage
        this.tabSwitchCount = tabSwitchCount;
        this.maxTabSwitches = maxTabSwitches;
        
        // Đơn giản hóa logic chuyển tab
        let pageLoadTime = Date.now(); // Thời gian load trang
        let isWindowBlurred = false; // Flag để track window blur
        
        console.log(`[QuizProtection] Setting up visibilitychange listener...`);
        
        // Hàm xử lý chuyển tab
        const handleTabSwitch = (reason) => {
            const currentTime = Date.now();
            const timeSinceLastSwitch = currentTime - lastSwitchTime;
            const timeSincePageLoad = currentTime - pageLoadTime;
            
            console.log(`[QuizProtection] Tab switch detected - Reason: ${reason}`);
            console.log(`[QuizProtection] Time since last switch: ${timeSinceLastSwitch}ms`);
            console.log(`[QuizProtection] Time since page load: ${timeSincePageLoad}ms`);
            console.log(`[QuizProtection] Current tab switch count: ${tabSwitchCount}`);
            console.log(`[QuizProtection] Current violation count: ${this.violationCount}`);
            
            // Bỏ qua nếu vừa load trang (trong 2 giây đầu)
            if (timeSincePageLoad < 2000) {
                console.log(`[QuizProtection] Ignored - page just loaded (${timeSincePageLoad}ms < 2000ms)`);
                return;
            }
            
            // Kiểm tra cooldown để tránh spam (tăng lên 1000ms để tránh đếm trùng)
            if (timeSinceLastSwitch < 1000) {
                console.log(`[QuizProtection] Ignored due to cooldown (${timeSinceLastSwitch}ms < 1000ms)`);
                return;
            }
            
            // Kiểm tra xem có đang trong quá trình xử lý không
            if (this.isProcessingTabSwitch) {
                console.log(`[QuizProtection] Ignored - already processing tab switch`);
                return;
            }
            
            // Đánh dấu đang xử lý
            this.isProcessingTabSwitch = true;
            
            // Đếm chuyển tab
            tabSwitchCount++;
            lastSwitchTime = currentTime;
            
            console.log(`[QuizProtection] Tab switch count updated: ${tabSwitchCount}/${maxTabSwitches}`);
            console.log(`[QuizProtection] Last switch time: ${new Date(lastSwitchTime).toLocaleTimeString()}`);
            
            // Lưu trạng thái
            sessionStorage.setItem('quizTabSwitchCount', tabSwitchCount.toString());
            this.tabSwitchCount = tabSwitchCount;
            
            console.log(`[QuizProtection] Calling handleViolation with isTabSwitch=true`);
            // Gọi handleViolation để xử lý
            this.handleViolation(`Chuyển tab lần ${tabSwitchCount}`, true);
            
            // Reset flag sau 1 giây
            setTimeout(() => {
                this.isProcessingTabSwitch = false;
            }, 1000);
        };
        
        // Event listener cho visibilitychange (chuyển tab thông thường)
        document.addEventListener('visibilitychange', () => {
            console.log(`[QuizProtection] visibilitychange event triggered`);
            console.log(`[QuizProtection] document.hidden: ${document.hidden}`);
            console.log(`[QuizProtection] document.visibilityState: ${document.visibilityState}`);
            
            // Sử dụng visibilityState thay vì hidden để chính xác hơn
            if (document.visibilityState === 'hidden') {
                handleTabSwitch('visibilitychange - tab hidden');
            } else {
                console.log(`[QuizProtection] Tab visibility changed to VISIBLE`);
            }
        });
        
        // Event listener cho window blur/focus (phát hiện chia đôi màn hình)
        window.addEventListener('blur', () => {
            console.log(`[QuizProtection] Window blur detected`);
            console.log(`[QuizProtection] Document visibility state: ${document.visibilityState}`);
            
            // Chỉ đếm nếu không phải do click trong trang
            setTimeout(() => {
                if (document.visibilityState !== 'hidden') {
                    console.log(`[QuizProtection] Window blur confirmed - focus lost to another app`);
                    isWindowBlurred = true;
                    handleTabSwitch('window blur - focus lost');
                } else {
                    console.log(`[QuizProtection] Window blur ignored - tab is hidden`);
                }
            }, 100);
        });
        
        window.addEventListener('focus', () => {
            console.log(`[QuizProtection] Window focus detected`);
            if (isWindowBlurred) {
                console.log(`[QuizProtection] Window focus after blur - returning to quiz`);
                isWindowBlurred = false;
            }
        });
        
        // Thêm event listener cho mouse events để phát hiện click chuột chuyển tab
        let mouseClickTimeout = null;
        document.addEventListener('mousedown', (e) => {
            // Kiểm tra nếu click vào vùng tab (thường ở trên cùng)
            if (e.clientY < 50) {
                console.log(`[QuizProtection] Tab area click detected at Y: ${e.clientY}`);
                
                // Clear timeout cũ nếu có
                if (mouseClickTimeout) {
                    clearTimeout(mouseClickTimeout);
                }
                
                // Không preventDefault để cho phép chuyển tab, chỉ đếm
                mouseClickTimeout = setTimeout(() => {
                    if (document.visibilityState === 'hidden') {
                        console.log(`[QuizProtection] Tab click confirmed - tab switched`);
                        handleTabSwitch('mouse click - tab area');
                    }
                }, 300);
            }
        });
        
        console.log(`[QuizProtection] visibilitychange listener added successfully`);
        
        // Cấm các phím tắt chuyển tab
        document.addEventListener('keydown', (e) => {
            // Alt+Tab
            if (e.altKey && e.keyCode === 9) {
                console.log(`[QuizProtection] Alt+Tab detected and blocked`);
                e.preventDefault();
                tabSwitchCount++;
                this.handleViolation('Alt+Tab bị cấm!', true);
                return false;
            }
            
            // Ctrl+Tab
            if (e.ctrlKey && e.keyCode === 9) {
                console.log(`[QuizProtection] Ctrl+Tab detected and blocked`);
                e.preventDefault();
                tabSwitchCount++;
                this.handleViolation('Ctrl+Tab bị cấm!', true);
                return false;
            }
            
            // Windows+Tab
            if (e.metaKey && e.keyCode === 9) {
                console.log(`[QuizProtection] Windows+Tab detected and blocked`);
                e.preventDefault();
                tabSwitchCount++;
                this.handleViolation('Windows+Tab bị cấm!', true);
                return false;
            }
        });
        
        // Cấm mở tab mới
        document.addEventListener('keydown', (e) => {
            // Ctrl+T (New tab)
            if (e.ctrlKey && e.keyCode === 84) {
                console.log(`[QuizProtection] Ctrl+T (New tab) detected and blocked`);
                e.preventDefault();
                this.handleViolation('Mở tab mới bị cấm!', false);
                return false;
            }
            
            // Ctrl+N (New window)
            if (e.ctrlKey && e.keyCode === 78) {
                console.log(`[QuizProtection] Ctrl+N (New window) detected and blocked`);
                e.preventDefault();
                this.handleViolation('Mở window mới bị cấm!', false);
                return false;
            }
        });
        
        // Cấm click chuột phải vào tab
        document.addEventListener('mousedown', (e) => {
            // Kiểm tra nếu click vào vùng tab (thường ở trên cùng)
            if (e.clientY < 50) {
                console.log(`[QuizProtection] Tab area click detected and blocked (Y: ${e.clientY})`);
                e.preventDefault();
                this.handleViolation('Click vào tab bị cấm!', false);
                return false;
            }
        });
        
        console.log(`[QuizProtection] Tab Switch Protection initialized successfully`);
    }

    preventCopyPaste() {
        // Cấm copy
        document.addEventListener('copy', (e) => {
            e.preventDefault();
            this.handleViolation('Copy bị cấm!', false);
            return false;
        });

        // Cấm paste
        document.addEventListener('paste', (e) => {
            e.preventDefault();
            this.handleViolation('Paste bị cấm!', false);
            return false;
        });

        // Cấm cut
        document.addEventListener('cut', (e) => {
            e.preventDefault();
            this.handleViolation('Cut bị cấm!', false);
            return false;
        });
    }

    preventTextSelection() {
        // Cấm select text
        document.addEventListener('selectstart', (e) => {
            e.preventDefault();
            this.handleViolation('Select text bị cấm!', false);
            return false;
        });

        // Cấm drag
        document.addEventListener('dragstart', (e) => {
            e.preventDefault();
            this.handleViolation('Drag bị cấm!', false);
            return false;
        });
    }

    preventConsoleAccess() {
        // Cấm console access nhưng vẫn cho phép log để debug
        const originalConsoleLog = console.log;
        const originalConsoleWarn = console.warn;
        const originalConsoleError = console.error;
        
        // Override console.log để vẫn log được nhưng cảnh báo
        console.log = (...args) => {
            // Vẫn cho phép log để debug
            originalConsoleLog.apply(console, args);
        };

        // Chỉ cấm một số console access cần thiết để tránh vòng lặp
        // Không cấm toàn bộ console để tránh lỗi
        console.log('[QuizProtection] Console access protection initialized (limited)');
    }

    preventPrintScreen() {
        // Cấm print screen bằng phím tắt
        document.addEventListener('keydown', (e) => {
            if (e.keyCode === 44) { // Print Screen
                e.preventDefault();
                this.handleViolation('Print Screen bị cấm!', false);
                return false;
            }
        });
    }

    handleViolation(message, isTabSwitch = false) {
        console.log(`[QuizProtection] handleViolation called with message: "${message}", isTabSwitch: ${isTabSwitch}`);
        
        if (this.isSubmitted) {
            console.log(`[QuizProtection] Ignored violation "${message}" - quiz already submitted`);
            return;
        }
        
        console.log(`[QuizProtection] VIOLATION: ${message} (isTabSwitch: ${isTabSwitch})`);
        console.log(`[QuizProtection] Violation time: ${new Date().toLocaleTimeString()}`);
        console.log(`[QuizProtection] Current violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Max violations: ${this.maxViolations}`);
        
        // Chỉ đếm violation cho chuyển tab
        if (isTabSwitch) {
            this.violationCount++;
            console.log(`[QuizProtection] Tab switch violation counted: ${this.violationCount}/${this.maxViolations}`);
            console.log(`[QuizProtection] Tab switch count from session: ${this.tabSwitchCount || 0}`);
            
            if (this.violationCount >= this.maxViolations) {
                console.log(`[QuizProtection] MAX TAB SWITCH VIOLATIONS REACHED! Auto-submitting quiz...`);
                this.showWarning(`🚨 ĐÃ CHUYỂN TAB ${this.maxViolations} LẦN! BÀI LÀM SẼ ĐƯỢC NỘP TỰ ĐỘNG!`, 'error');
                setTimeout(() => {
                    console.log(`[QuizProtection] Executing auto-submit due to tab switch violations...`);
                    this.autoSubmitQuiz();
                }, 2000);
            } else {
                const remainingViolations = this.maxViolations - this.violationCount;
                console.log(`[QuizProtection] Tab switch warning shown. Remaining switches: ${remainingViolations}`);
                // Luôn hiển thị thông báo từ lần đầu tiên
                this.showWarning(`⚠️ CẢNH BÁO: Bạn đã chuyển tab ${this.violationCount} lần!<br>📊 Còn lại ${remainingViolations} lần chuyển tab.<br>🚨 Nếu chuyển tab thêm ${remainingViolations} lần nữa, bài làm sẽ được nộp tự động!`);
            }
        } else {
            // Các lỗi khác chỉ cảnh báo, không đếm vào violation count
            console.log(`[QuizProtection] Non-tab switch violation - only warning shown`);
            this.showWarning(`${message}<br>Vui lòng không thực hiện hành động này trong khi làm bài.`);
        }
        
        console.log(`[QuizProtection] handleViolation completed`);
    }

    autoSubmitQuiz() {
        if (this.isSubmitted) {
            console.log(`[QuizProtection] Auto-submit ignored - quiz already submitted`);
            return;
        }
        
        console.log(`[QuizProtection] Starting auto-submit process...`);
        this.isSubmitted = true;
        
        // Tạo form ẩn để submit
        const form = document.querySelector('form[action="doQuiz"]');
        if (form) {
            console.log(`[QuizProtection] Found quiz form, adding auto-submit flag`);
            // Thêm input ẩn để đánh dấu auto-submit
            const autoSubmitInput = document.createElement('input');
            autoSubmitInput.type = 'hidden';
            autoSubmitInput.name = 'autoSubmit';
            autoSubmitInput.value = 'true';
            form.appendChild(autoSubmitInput);
            
            console.log(`[QuizProtection] Submitting form with auto-submit flag`);
            // Submit form
            form.submit();
        } else {
            console.log(`[QuizProtection] No quiz form found, using fallback redirect`);
            // Fallback: redirect về trang quiz với lessonId hiện tại
            const currentUrl = window.location.href;
            const urlParams = new URLSearchParams(window.location.search);
            const lessonId = urlParams.get('lessonId') || '1';
            
            // Tạo URL redirect với lessonId hiện tại
            const redirectUrl = `${window.location.origin}${window.location.pathname}?lessonId=${lessonId}`;
            console.log(`[QuizProtection] Redirecting to: ${redirectUrl}`);
            window.location.href = redirectUrl;
        }
    }

    // Phương thức để bật/tắt debug mode
    enableDebugMode() {
        console.log(`[QuizProtection] Enabling debug mode...`);
        this.debugMode = true;
        sessionStorage.setItem('quizDebugMode', 'true');
        console.log(`[QuizProtection] Debug mode enabled - F12 will be allowed`);
    }

    disableDebugMode() {
        console.log(`[QuizProtection] Disabling debug mode...`);
        this.debugMode = false;
        sessionStorage.removeItem('quizDebugMode');
        console.log(`[QuizProtection] Debug mode disabled - F12 will be blocked`);
    }

    // Phương thức để tắt/bật bảo vệ
    enable() {
        console.log(`[QuizProtection] Enabling protection...`);
        this.enabled = true;
        this.initProtection();
        console.log(`[QuizProtection] Protection enabled successfully`);
    }

    disable() {
        console.log(`[QuizProtection] Disabling protection...`);
        this.enabled = false;
        console.log(`[QuizProtection] Protection disabled (note: event listeners are still active)`);
        // Có thể thêm logic để remove event listeners nếu cần
    }

    // Phương thức để reset violation count
    resetViolations() {
        console.log(`[QuizProtection] Resetting violations...`);
        console.log(`[QuizProtection] Old violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Old tab switch count: ${this.tabSwitchCount || 0}`);
        
        this.violationCount = 0;
        this.isSubmitted = false;
        // Reset tab switch count
        sessionStorage.removeItem('quizTabSwitchCount');
        if (this.tabSwitchCount !== undefined) {
            this.tabSwitchCount = 0;
        }
        
        console.log(`[QuizProtection] Violations reset successfully`);
        console.log(`[QuizProtection] New violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] New tab switch count: ${this.tabSwitchCount || 0}`);
    }

    // Phương thức để reset hoàn toàn khi bắt đầu quiz
    resetAllOnStart() {
        console.log(`[QuizProtection] Resetting all protection states on quiz start...`);
        
        // Reset violations
        this.violationCount = 0;
        this.isSubmitted = false;
        
        // Reset tab switch count
        this.tabSwitchCount = 0;
        sessionStorage.removeItem('quizTabSwitchCount');
        
        // Reset các biến khác
        sessionStorage.removeItem('quizDebugMode');
        
        console.log(`[QuizProtection] All protection states reset successfully on quiz start.`);
        console.log(`[QuizProtection] Violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Tab switch count: ${this.tabSwitchCount}`);
        console.log(`[QuizProtection] Is submitted: ${this.isSubmitted}`);
    }

    // Phương thức để reset tab switch count
    resetTabSwitchCount() {
        console.log(`[QuizProtection] Resetting tab switch count...`);
        console.log(`[QuizProtection] Old tab switch count: ${this.tabSwitchCount || 0}`);
        
        // Reset local variables
        this.tabSwitchCount = 0;
        
        // Remove from sessionStorage
        sessionStorage.removeItem('quizTabSwitchCount');
        
        console.log(`[QuizProtection] Tab switch count reset successfully`);
        console.log(`[QuizProtection] New tab switch count: ${this.tabSwitchCount}`);
    }

    // Phương thức để debug tab switch
    debugTabSwitch() {
        console.log('=== [QuizProtection] Tab Switch Debug Info ===');
        console.log(`[QuizProtection] Current tab switch count: ${this.tabSwitchCount || 0}`);
        console.log(`[QuizProtection] Max tab switches: ${this.maxTabSwitches || 3}`);
        console.log(`[QuizProtection] Document hidden: ${document.hidden}`);
        console.log(`[QuizProtection] Document visibility state: ${document.visibilityState}`);
        console.log(`[QuizProtection] SessionStorage tab switch count: ${sessionStorage.getItem('quizTabSwitchCount')}`);
        console.log('=== [QuizProtection] Debug Info End ===');
    }

    // Phương thức để xóa tất cả thông báo cảnh báo
    removeAllWarnings() {
        console.log(`[QuizProtection] Removing all existing warnings...`);
        
        // Xóa tất cả thông báo có class quiz-protection-warning
        const existingWarnings = document.querySelectorAll('.quiz-protection-warning');
        existingWarnings.forEach(warning => {
            console.log(`[QuizProtection] Removing existing warning: ${warning.textContent}`);
            warning.remove();
        });
        
        console.log(`[QuizProtection] Removed ${existingWarnings.length} existing warnings`);
    }

    // Phương thức để hiển thị thông báo cảnh báo
    showWarning(message, type = 'warning') {
        // Không hiển thị warning trong thời gian khởi tạo
        if (this.isInitializing) {
            console.log(`[QuizProtection] Warning suppressed during initialization: ${message}`);
            return;
        }
        
        console.log(`[QuizProtection] Showing warning: ${message} (type: ${type})`);
        
        // Xóa tất cả thông báo cũ trước khi hiển thị thông báo mới
        this.removeAllWarnings();
        
        // Tính toán vị trí để tránh chồng đè
        const existingWarnings = document.querySelectorAll('.quiz-protection-warning');
        const topOffset = 20 + (existingWarnings.length * 80); // Mỗi thông báo cách nhau 80px
        
        // Tạo thông báo đẹp hơn
        const warningDiv = document.createElement('div');
        warningDiv.className = 'quiz-protection-warning';
        warningDiv.style.cssText = `
            position: fixed;
            top: ${topOffset}px;
            right: 20px;
            background: ${type === 'error' ? '#ff4444' : '#ffaa00'};
            color: white;
            padding: 15px 20px;
            border-radius: 5px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.3);
            z-index: 10000;
            font-family: Arial, sans-serif;
            font-size: 14px;
            max-width: 300px;
            animation: slideIn 0.3s ease-out;
        `;
        
        warningDiv.innerHTML = `
            <div style="display: flex; align-items: center; justify-content: space-between;">
                <div>
                    <strong>⚠️ Cảnh báo</strong><br>
                    ${message}
                </div>
                <button onclick="this.parentElement.parentElement.remove()" 
                        style="background: none; border: none; color: white; font-size: 18px; cursor: pointer; margin-left: 10px;">
                    ×
                </button>
            </div>
        `;
        
        // Thêm CSS animation nếu chưa có
        if (!document.getElementById('quiz-protection-styles')) {
            const style = document.createElement('style');
            style.id = 'quiz-protection-styles';
            style.textContent = `
                @keyframes slideIn {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @keyframes slideOut {
                    from { transform: translateX(0); opacity: 1; }
                    to { transform: translateX(100%); opacity: 0; }
                }
            `;
            document.head.appendChild(style);
        }
        
        document.body.appendChild(warningDiv);
        console.log(`[QuizProtection] Warning displayed successfully at position top: ${topOffset}px`);
        
        // Tự động ẩn sau 5 giây
        setTimeout(() => {
            if (warningDiv.parentElement) {
                warningDiv.style.animation = 'slideOut 0.3s ease-out';
                setTimeout(() => {
                    if (warningDiv.parentElement) {
                        warningDiv.remove();
                        console.log(`[QuizProtection] Warning auto-hidden after 5 seconds`);
                    }
                }, 300);
            }
        }, 5000);
    }

    // Phương thức test để kiểm tra hệ thống
    testSystem() {
        console.log('=== [QuizProtection] Testing System ===');
        console.log(`[QuizProtection] Current status:`, this.getStatus());
        console.log(`[QuizProtection] Testing tab switch detection...`);
        
        // Test tab switch
        const testEvent = new Event('visibilitychange');
        document.hidden = true;
        document.dispatchEvent(testEvent);
        
        console.log(`[QuizProtection] Test completed`);
        return true;
    }

    // Phương thức test chuyển tab
    testTabSwitch() {
        console.log('=== [QuizProtection] Testing Tab Switch ===');
        console.log(`[QuizProtection] Current tab switch count: ${this.tabSwitchCount || 0}`);
        console.log(`[QuizProtection] Current violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Document hidden: ${document.hidden}`);
        console.log(`[QuizProtection] Document visibility state: ${document.visibilityState}`);
        
        // Simulate tab switch
        console.log(`[QuizProtection] Simulating tab switch...`);
        document.hidden = true;
        const event = new Event('visibilitychange');
        document.dispatchEvent(event);
        
        console.log(`[QuizProtection] Tab switch simulation completed`);
        return true;
    }

    // Phương thức test chính xác chuyển tab bằng chuột
    testAccurateMouseTabSwitch() {
        console.log('=== [QuizProtection] Testing Accurate Mouse Tab Switch ===');
        console.log(`[QuizProtection] Current tab switch count: ${this.tabSwitchCount || 0}`);
        console.log(`[QuizProtection] Current violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Is processing tab switch: ${this.isProcessingTabSwitch || false}`);
        console.log(`[QuizProtection] Session storage: ${sessionStorage.getItem('quizTabSwitchCount')}`);
        
        console.log(`[QuizProtection] Instructions for accurate testing:`);
        console.log(`[QuizProtection] 1. Click on another browser tab ONCE`);
        console.log(`[QuizProtection] 2. Wait for the warning to appear`);
        console.log(`[QuizProtection] 3. Click back to this tab`);
        console.log(`[QuizProtection] 4. Repeat 2 more times (total 3 clicks)`);
        console.log(`[QuizProtection] 5. Check if count is accurate`);
        
        // Reset counts for clean test
        this.resetTabSwitchCount();
        console.log(`[QuizProtection] Counts reset for clean test`);
        
        return true;
    }

    // Phương thức debug để kiểm tra lỗi đếm
    debugCounting() {
        console.log('=== [QuizProtection] Debug Counting ===');
        console.log(`[QuizProtection] Tab switch count: ${this.tabSwitchCount || 0}`);
        console.log(`[QuizProtection] Violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Max violations: ${this.maxViolations}`);
        console.log(`[QuizProtection] Session storage tab switch: ${sessionStorage.getItem('quizTabSwitchCount')}`);
        console.log(`[QuizProtection] Is submitted: ${this.isSubmitted}`);
        
        // Kiểm tra xem có bị lỗi đếm không
        const sessionCount = parseInt(sessionStorage.getItem('quizTabSwitchCount') || '0');
        if (this.violationCount !== sessionCount) {
            console.log(`[QuizProtection] WARNING: Count mismatch! violationCount=${this.violationCount}, sessionCount=${sessionCount}`);
            console.log(`[QuizProtection] Fixing count mismatch...`);
            this.violationCount = sessionCount;
        }
        
        console.log(`[QuizProtection] Debug completed`);
        return true;
    }

    // Phương thức test chuyển tab bằng chuột
    testMouseTabSwitch() {
        console.log('=== [QuizProtection] Testing Mouse Tab Switch ===');
        console.log(`[QuizProtection] Current tab switch count: ${this.tabSwitchCount || 0}`);
        console.log(`[QuizProtection] Current violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Document hidden: ${document.hidden}`);
        console.log(`[QuizProtection] Document visibility state: ${document.visibilityState}`);
        
        console.log(`[QuizProtection] Please test the following scenarios:`);
        console.log(`[QuizProtection] 1. Click on another browser tab`);
        console.log(`[QuizProtection] 2. Use Alt+Tab to switch applications`);
        console.log(`[QuizProtection] 3. Check if warnings appear correctly`);
        console.log(`[QuizProtection] 4. Verify count is accurate (should be 3 times max)`);
        
        return true;
    }

    // Phương thức test chia đôi màn hình
    testSplitScreen() {
        console.log('=== [QuizProtection] Testing Split Screen Detection ===');
        console.log(`[QuizProtection] Current tab switch count: ${this.tabSwitchCount || 0}`);
        console.log(`[QuizProtection] Current violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Document hidden: ${document.hidden}`);
        console.log(`[QuizProtection] Document visibility state: ${document.visibilityState}`);
        
        console.log(`[QuizProtection] Please test the following scenarios:`);
        console.log(`[QuizProtection] 1. Alt+Tab to another application`);
        console.log(`[QuizProtection] 2. Click on another application window`);
        console.log(`[QuizProtection] 3. Use Windows+Tab to switch apps`);
        console.log(`[QuizProtection] 4. Check the logs below after each action`);
        
        return true;
    }

    // Phương thức test chuyển tab thực
    testRealTabSwitch() {
        console.log('=== [QuizProtection] Testing Real Tab Switch ===');
        console.log(`[QuizProtection] Current tab switch count: ${this.tabSwitchCount || 0}`);
        console.log(`[QuizProtection] Current violation count: ${this.violationCount}`);
        console.log(`[QuizProtection] Document hidden: ${document.hidden}`);
        console.log(`[QuizProtection] Document visibility state: ${document.visibilityState}`);
        
        console.log(`[QuizProtection] Please switch tabs manually and check the logs...`);
        console.log(`[QuizProtection] Instructions:`);
        console.log(`[QuizProtection] 1. Click on another tab or use Alt+Tab`);
        console.log(`[QuizProtection] 2. Come back to this tab`);
        console.log(`[QuizProtection] 3. Check the logs below`);
        
        return true;
    }

    // Phương thức để lấy thông tin trạng thái
    getStatus() {
        const status = {
            enabled: this.enabled,
            violationCount: this.violationCount,
            maxViolations: this.maxViolations,
            isSubmitted: this.isSubmitted,
            tabSwitchCount: this.tabSwitchCount || 0,
            maxTabSwitches: this.maxTabSwitches || 3,
            debugMode: this.debugMode,
            documentHidden: document.hidden,
            visibilityState: document.visibilityState,
            sessionStorageTabSwitch: sessionStorage.getItem('quizTabSwitchCount')
        };
        
        console.log(`[QuizProtection] Current status:`, status);
        return status;
    }
}

// Export cho sử dụng trong module
if (typeof module !== 'undefined' && module.exports) {
    module.exports = QuizProtection;
}

// Debug: Kiểm tra xem class có được định nghĩa không
console.log('=== [QuizProtection] File loaded successfully ===');
console.log('[QuizProtection] QuizProtection class defined:', typeof QuizProtection);

// Test function để kiểm tra hệ thống
window.testQuizProtection = function() {
    console.log('=== [QuizProtection] Testing system... ===');
    
    if (typeof QuizProtection === 'undefined') {
        console.error('[QuizProtection] ERROR: QuizProtection class not defined!');
        return false;
    }
    
    try {
        const testProtection = new QuizProtection({
            maxViolations: 3,
            enabled: true
        });
        
        console.log('[QuizProtection] Test instance created successfully');
        console.log('[QuizProtection] Test status:', testProtection.getStatus());
        
        // Test các phương thức
        testProtection.showWarning('Test warning message');
        console.log('[QuizProtection] Warning test completed');
        
        return true;
    } catch (error) {
        console.error('[QuizProtection] ERROR during testing:', error);
        return false;
    }
};

// Auto-test khi file load
setTimeout(() => {
    console.log('[QuizProtection] Auto-testing system...');
    window.testQuizProtection();
}, 1000); 