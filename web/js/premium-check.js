/**
 * Premium Check Utility
 * Kiểm tra quyền sử dụng các tính năng premium
 */

class PremiumChecker {
    
    /**
     * Kiểm tra quyền sử dụng video call
     * @returns {Promise<Object>} Kết quả kiểm tra
     */
    static async checkVideoCallPermission() {
        try {
            const response = await fetch('/check-video-call-permission', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            const result = await response.json();
            return result;
        } catch (error) {
            console.error('Lỗi kiểm tra quyền video call:', error);
            return {
                success: false,
                canUse: false,
                message: 'Vui lòng nâng cấp lên premium'
            };
        }
    }
    
    /**
     * Kiểm tra quyền sử dụng AI call
     * @returns {Promise<Object>} Kết quả kiểm tra
     */
    static async checkAICallPermission() {
        try {
            const response = await fetch('/check-ai-call-permission', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            const result = await response.json();
            return result;
        } catch (error) {
            console.error('Lỗi kiểm tra quyền AI call:', error);
            return {
                success: false,
                canUse: false,
                message: 'Vui lòng nâng cấp lên premium'
            };
        }
    }
    
    /**
     * Hiển thị thông báo lỗi khi không có quyền
     * @param {string} message - Thông báo lỗi
     * @param {string} type - Loại thông báo (error, warning, info)
     */
    static showNotification(message, type = 'error') {
        // Tạo notification element
        const notification = document.createElement('div');
        notification.className = `premium-notification ${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas ${type === 'error' ? 'fa-exclamation-triangle' : 'fa-info-circle'}"></i>
                <span>${message}</span>
                <button class="notification-close" onclick="this.parentElement.parentElement.remove()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `;
        
        // Thêm CSS styles
        if (!document.getElementById('premium-notification-styles')) {
            const style = document.createElement('style');
            style.id = 'premium-notification-styles';
            style.textContent = `
                .premium-notification {
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    z-index: 9999;
                    max-width: 400px;
                    animation: slideIn 0.3s ease;
                }
                
                .premium-notification.error {
                    background: linear-gradient(135deg, #ff6b6b, #ee5a52);
                    color: white;
                    border-radius: 10px;
                    box-shadow: 0 5px 15px rgba(0,0,0,0.2);
                }
                
                .premium-notification.warning {
                    background: linear-gradient(135deg, #feca57, #ff9ff3);
                    color: #333;
                    border-radius: 10px;
                    box-shadow: 0 5px 15px rgba(0,0,0,0.2);
                }
                
                .premium-notification.info {
                    background: linear-gradient(135deg, #48dbfb, #0abde3);
                    color: white;
                    border-radius: 10px;
                    box-shadow: 0 5px 15px rgba(0,0,0,0.2);
                }
                
                .notification-content {
                    padding: 15px 20px;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }
                
                .notification-close {
                    background: none;
                    border: none;
                    color: inherit;
                    cursor: pointer;
                    margin-left: auto;
                    opacity: 0.7;
                    transition: opacity 0.3s;
                }
                
                .notification-close:hover {
                    opacity: 1;
                }
                
                @keyframes slideIn {
                    from {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
            `;
            document.head.appendChild(style);
        }
        
        // Thêm vào body
        document.body.appendChild(notification);
        
        // Tự động xóa sau 5 giây
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
    }
    
    /**
     * Kiểm tra và xử lý quyền video call
     * @param {Function} onSuccess - Callback khi có quyền
     * @param {Function} onError - Callback khi không có quyền
     */
    static async handleVideoCallPermission(onSuccess, onError = null) {
        const result = await this.checkVideoCallPermission();
        
        if (result.success && result.canUse) {
            if (onSuccess) onSuccess();
        } else {
            this.showNotification(result.message || 'Bạn không có quyền sử dụng tính năng này', 'error');
            if (onError) onError(result.message);
        }
    }
    
    /**
     * Kiểm tra và xử lý quyền AI call
     * @param {Function} onSuccess - Callback khi có quyền
     * @param {Function} onError - Callback khi không có quyền
     */
    static async handleAICallPermission(onSuccess, onError = null) {
        const result = await this.checkAICallPermission();
        
        if (result.success && result.canUse) {
            if (onSuccess) onSuccess();
        } else {
            this.showNotification(result.message || 'Bạn không có quyền sử dụng tính năng này', 'error');
            if (onError) onError(result.message);
        }
    }
    
    /**
     * Chuyển hướng đến trang nâng cấp premium
     */
    static redirectToUpgrade() {
        window.location.href = '/premium-plans';
    }
    
    /**
     * Hiển thị modal nâng cấp premium
     */
    static showUpgradeModal() {
        const modal = document.createElement('div');
        modal.className = 'upgrade-modal';
        modal.innerHTML = `
            <div class="upgrade-modal-content">
                <div class="upgrade-modal-header">
                    <h2><i class="fas fa-crown"></i> Nâng cấp Premium</h2>
                    <button class="modal-close" onclick="this.closest('.upgrade-modal').remove()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="upgrade-modal-body">
                    <p>Tính năng này chỉ dành cho Premium User. Nâng cấp tài khoản để:</p>
                    <ul>
                        <li><i class="fas fa-check"></i> Sử dụng Video Call không giới hạn</li>
                        <li><i class="fas fa-check"></i> Sử dụng AI Call để luyện tập</li>
                        <li><i class="fas fa-check"></i> Tạo flashcard không giới hạn</li>
                        <li><i class="fas fa-check"></i> Thêm item không giới hạn</li>
                    </ul>
                </div>
                <div class="upgrade-modal-footer">
                    <button class="btn-secondary" onclick="this.closest('.upgrade-modal').remove()">
                        Để sau
                    </button>
                    <button class="btn-primary" onclick="PremiumChecker.redirectToUpgrade()">
                        <i class="fas fa-arrow-up"></i> Nâng cấp ngay
                    </button>
                </div>
            </div>
        `;
        
        // Thêm CSS styles cho modal
        if (!document.getElementById('upgrade-modal-styles')) {
            const style = document.createElement('style');
            style.id = 'upgrade-modal-styles';
            style.textContent = `
                .upgrade-modal {
                    position: fixed;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background: rgba(0,0,0,0.5);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    z-index: 10000;
                    animation: fadeIn 0.3s ease;
                }
                
                .upgrade-modal-content {
                    background: white;
                    border-radius: 15px;
                    max-width: 500px;
                    width: 90%;
                    max-height: 80vh;
                    overflow-y: auto;
                    animation: slideUp 0.3s ease;
                }
                
                .upgrade-modal-header {
                    padding: 20px;
                    border-bottom: 1px solid #eee;
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                }
                
                .upgrade-modal-header h2 {
                    margin: 0;
                    color: #333;
                }
                
                .modal-close {
                    background: none;
                    border: none;
                    font-size: 1.2em;
                    cursor: pointer;
                    color: #666;
                }
                
                .upgrade-modal-body {
                    padding: 20px;
                }
                
                .upgrade-modal-body ul {
                    list-style: none;
                    padding: 0;
                }
                
                .upgrade-modal-body li {
                    padding: 10px 0;
                    display: flex;
                    align-items: center;
                    gap: 10px;
                }
                
                .upgrade-modal-body i {
                    color: #28a745;
                }
                
                .upgrade-modal-footer {
                    padding: 20px;
                    border-top: 1px solid #eee;
                    display: flex;
                    gap: 10px;
                    justify-content: flex-end;
                }
                
                .btn-primary {
                    background: linear-gradient(135deg, #667eea, #764ba2);
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 5px;
                    cursor: pointer;
                }
                
                .btn-secondary {
                    background: #6c757d;
                    color: white;
                    border: none;
                    padding: 10px 20px;
                    border-radius: 5px;
                    cursor: pointer;
                }
                
                @keyframes fadeIn {
                    from { opacity: 0; }
                    to { opacity: 1; }
                }
                
                @keyframes slideUp {
                    from {
                        transform: translateY(50px);
                        opacity: 0;
                    }
                    to {
                        transform: translateY(0);
                        opacity: 1;
                    }
                }
            `;
            document.head.appendChild(style);
        }
        
        document.body.appendChild(modal);
    }
}

// Export cho sử dụng global
window.PremiumChecker = PremiumChecker; 