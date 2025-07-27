// Create Flashcard JavaScript - Namespace: CreateFlashcardManager
class CreateFlashcardManager {
    constructor() {
        this.cardCount = 0;
        this.initializeEventListeners();
        this.setupFormValidation();
    }

    initializeEventListeners() {
        // Add card button
        const addCardBtn = document.querySelector('.create-flashcard-page .add-card-btn');
        if (addCardBtn) {
            addCardBtn.addEventListener('click', this.addCardItem.bind(this));
        }

        // Form submission
        const flashcardForm = document.querySelector('.create-flashcard-page #flashcardForm');
        if (flashcardForm) {
            flashcardForm.addEventListener('submit', this.handleFormSubmit.bind(this));
        }

        // Auto-save functionality
        this.setupAutoSave();
    }

    setupFormValidation() {
        // Validate required fields
        const requiredFields = document.querySelectorAll('.create-flashcard-page [required]');
        requiredFields.forEach(field => {
            field.addEventListener('blur', this.validateField.bind(this));
            field.addEventListener('input', this.clearFieldError.bind(this));
        });
    }

    validateField(event) {
        const field = event.target;
        const value = field.value.trim();
        
        if (field.hasAttribute('required') && !value) {
            this.showFieldError(field, 'Trường này là bắt buộc');
            return false;
        }

        // URL validation for image fields
        if (field.type === 'url' && value) {
            if (!this.isValidUrl(value)) {
                this.showFieldError(field, 'URL không hợp lệ');
                return false;
            }
        }

        this.clearFieldError(field);
        return true;
    }

    showFieldError(field, message) {
        this.clearFieldError(field);
        
        field.classList.add('is-invalid');
        const errorDiv = document.createElement('div');
        errorDiv.className = 'invalid-feedback';
        errorDiv.textContent = message;
        field.parentNode.appendChild(errorDiv);
    }

    clearFieldError(field) {
        field.classList.remove('is-invalid');
        const errorDiv = field.parentNode.querySelector('.invalid-feedback');
        if (errorDiv) {
            errorDiv.remove();
        }
    }

    isValidUrl(string) {
        try {
            new URL(string);
            return true;
        } catch (_) {
            return false;
        }
    }

    addCardItem() {
        const cardContainer = document.querySelector('.create-flashcard-page #cardItems');
        if (!cardContainer) return;

        // Get current number of cards in DOM
        const currentCards = document.querySelectorAll('.create-flashcard-page .card-item');
        const newCardNumber = currentCards.length + 1;
        
        const cardItem = this.createCardItemHTML(newCardNumber);
        
        // Insert the new card item
        cardContainer.insertAdjacentHTML('beforeend', cardItem);
        
        // Add event listeners to the new card
        const newCard = cardContainer.lastElementChild;
        this.addCardEventListeners(newCard);
        
        // Update card numbers (this will also update this.cardCount)
        this.updateCardNumbers();
        
        // Scroll to the new card
        newCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
        
        // Focus on the first input of the new card
        const firstInput = newCard.querySelector('input[name^="frontContent"]');
        if (firstInput) {
            firstInput.focus();
        }
    }

    createCardItemHTML(cardNumber) {
        const frontName = cardNumber > 1 ? `frontContent-${cardNumber}` : 'frontContent';
        const backName = cardNumber > 1 ? `backContent-${cardNumber}` : 'backContent';
        const noteName = cardNumber > 1 ? `note-${cardNumber}` : 'note';
        const frontImageName = cardNumber > 1 ? `frontImage-${cardNumber}` : 'frontImage';
        const backImageName = cardNumber > 1 ? `backImage-${cardNumber}` : 'backImage';
        
        return `
            <div class="card-item" data-card-number="${cardNumber}">
                <div class="card-item-header">
                    <div class="card-number">${cardNumber}</div>
                    <button type="button" class="remove-card" onclick="createFlashcardManager.removeCardItem(this)">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                
                <div class="card-content-grid">
                    <div class="front-side">
                        <label class="form-label">Mặt trước *</label>
                        <input type="text" class="form-control" name="${frontName}" required>
                        
                        <div class="image-upload-section">
                            <label class="form-label">Ảnh mặt trước</label>
                            <div class="file-input-wrapper">
                                <input type="file" name="${frontImageName}" accept="image/*" data-preview="frontPreview${cardNumber}">
                                <button type="button" class="upload-btn">
                                    <i class="fas fa-upload"></i> Chọn ảnh
                                </button>
                            </div>
                            <div class="image-preview-container">
                                <img id="frontPreview${cardNumber}" class="image-preview" alt="Front preview">
                            </div>
                        </div>
                    </div>
                    
                    <div class="back-side">
                        <label class="form-label">Mặt sau *</label>
                        <input type="text" class="form-control" name="${backName}" required>
                        
                        <div class="image-upload-section">
                            <label class="form-label">Ảnh mặt sau</label>
                            <div class="file-input-wrapper">
                                <input type="file" name="${backImageName}" accept="image/*" data-preview="backPreview${cardNumber}">
                                <button type="button" class="upload-btn">
                                    <i class="fas fa-upload"></i> Chọn ảnh
                                </button>
                            </div>
                            <div class="image-preview-container">
                                <img id="backPreview${cardNumber}" class="image-preview" alt="Back preview">
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="mt-3">
                    <label class="form-label">Ghi chú</label>
                    <textarea class="form-control" name="${noteName}" rows="2" placeholder="Ghi chú bổ sung (tùy chọn)"></textarea>
                </div>
            </div>
        `;
    }

    addCardEventListeners(cardElement) {
        // Add event listeners for file inputs
        const fileInputs = cardElement.querySelectorAll('input[type="file"]');
        fileInputs.forEach(input => {
            input.addEventListener('change', (e) => {
                const previewId = e.target.getAttribute('data-preview');
                if (previewId) {
                    this.previewImage(e.target, previewId);
                }
            });
        });

        // Add event listeners for text inputs
        const textInputs = cardElement.querySelectorAll('input[type="text"], textarea');
        textInputs.forEach(input => {
            input.addEventListener('blur', this.validateField.bind(this));
            input.addEventListener('input', this.clearFieldError.bind(this));
        });
    }

    removeCardItem(button) {
        const cardItem = button.closest('.card-item');
        if (!cardItem) return;

        // Add removing animation
        cardItem.classList.add('removing');
        
        // Remove after animation completes
        setTimeout(() => {
            cardItem.remove();
            this.updateCardNumbers();
        }, 300);
    }

    updateCardNumbers() {
        const cardItems = document.querySelectorAll('.create-flashcard-page .card-item');
        
        cardItems.forEach((item, index) => {
            const number = index + 1;
            const numberElement = item.querySelector('.card-number');
            if (numberElement) {
                numberElement.textContent = number;
            }
            
            // Update input names
            const frontInput = item.querySelector('input[name^="frontContent"]');
            const backInput = item.querySelector('input[name^="backContent"]');
            const frontImageInput = item.querySelector('input[name^="frontImage"]');
            const backImageInput = item.querySelector('input[name^="backImage"]');
            const noteInput = item.querySelector('textarea[name^="note"]');
            
            const newFrontName = `frontContent${number > 1 ? '-' + number : ''}`;
            const newBackName = `backContent${number > 1 ? '-' + number : ''}`;
            const newFrontImageName = `frontImage${number > 1 ? '-' + number : ''}`;
            const newBackImageName = `backImage${number > 1 ? '-' + number : ''}`;
            const newNoteName = `note${number > 1 ? '-' + number : ''}`;
            
            if (frontInput) {
                frontInput.name = newFrontName;
            }
            if (backInput) {
                backInput.name = newBackName;
            }
            if (frontImageInput) {
                frontImageInput.name = newFrontImageName;
            }
            if (backImageInput) {
                backImageInput.name = newBackImageName;
            }
            if (noteInput) {
                noteInput.name = newNoteName;
            }
            
            // Update preview IDs
            const frontPreview = item.querySelector('img[id^="frontPreview"]');
            const backPreview = item.querySelector('img[id^="backPreview"]');
            if (frontPreview) frontPreview.id = `frontPreview${number}`;
            if (backPreview) backPreview.id = `backPreview${number}`;
            
            // Update data-preview attributes
            if (frontImageInput) {
                frontImageInput.setAttribute('data-preview', `frontPreview${number}`);
            }
            if (backImageInput) {
                backImageInput.setAttribute('data-preview', `backPreview${number}`);
            }
        });
        
        this.cardCount = cardItems.length;
        
        // Update hidden input for item count
        const itemCountInput = document.getElementById('itemCount');
        if (itemCountInput) {
            itemCountInput.value = this.cardCount;
        }
    }

    previewImage(input, previewId) {
        const preview = document.getElementById(previewId);
        if (!preview) return;

        if (input.files && input.files[0]) {
            const file = input.files[0];
            
            // Check file size (max 10MB)
            if (file.size > 10 * 1024 * 1024) {
                this.showAlert('danger', 'File ảnh quá lớn. Vui lòng chọn file nhỏ hơn 10MB.');
                input.value = '';
                return;
            }
            
            // Check file type
            if (!file.type.startsWith('image/')) {
                this.showAlert('danger', 'Vui lòng chọn file ảnh hợp lệ.');
                input.value = '';
                return;
            }
            
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.src = e.target.result;
                preview.style.display = 'block';
                preview.classList.add('show');
            };
            reader.readAsDataURL(file);
        }
    }

    setupAutoSave() {
        // Auto-save form data every 30 seconds
        let autoSaveTimer;
        const form = document.querySelector('.create-flashcard-page #flashcardForm');
        
        if (form) {
            const inputs = form.querySelectorAll('input, textarea');
            inputs.forEach(input => {
                input.addEventListener('input', () => {
                    clearTimeout(autoSaveTimer);
                    autoSaveTimer = setTimeout(() => {
                        this.autoSaveForm();
                    }, 30000); // 30 seconds
                });
            });
        }
    }

    autoSaveForm() {
        const form = document.querySelector('.create-flashcard-page #flashcardForm');
        if (!form) return;

        // Save form data to localStorage
        const formData = new FormData(form);
        const formObject = {};
        
        for (let [key, value] of formData.entries()) {
            formObject[key] = value;
        }
        
        localStorage.setItem('createFlashcardDraft', JSON.stringify(formObject));
        this.showToast('info', 'Đã tự động lưu bản nháp');
    }

    loadDraft() {
        const draft = localStorage.getItem('createFlashcardDraft');
        if (draft) {
            try {
                const formData = JSON.parse(draft);
                const form = document.querySelector('.create-flashcard-page #flashcardForm');
                
                if (form) {
                    Object.keys(formData).forEach(key => {
                        // Skip itemCount to avoid overwriting the correct value
                        if (key === 'itemCount') {
                            return;
                        }
                        
                        const field = form.querySelector(`[name="${key}"]`);
                        if (field) {
                            field.value = formData[key];
                        }
                    });
                }
                
                this.showToast('info', 'Đã khôi phục bản nháp');
            } catch (error) {
                console.error('Error loading draft:', error);
            }
        }
    }

    clearDraft() {
        localStorage.removeItem('createFlashcardDraft');
    }

    handleFormSubmit(event) {
        const title = document.querySelector('.create-flashcard-page #title');
        if (!this.validateField({ target: title })) {
            event.preventDefault();
            return false;
        }

        const cardItems = document.querySelectorAll('.create-flashcard-page .card-item');
        if (cardItems.length === 0) {
            event.preventDefault();
            this.showAlert('danger', 'Vui lòng thêm ít nhất một thẻ flashcard!');
            return false;
        }

        let hasValidContent = false;
        cardItems.forEach(item => {
            const frontContent = item.querySelector('input[name^="frontContent"]').value.trim();
            const backContent = item.querySelector('input[name^="backContent"]').value.trim();
            if (frontContent && backContent) {
                hasValidContent = true;
            }
        });

        if (!hasValidContent) {
            event.preventDefault();
            this.showAlert('danger', 'Vui lòng nhập nội dung cho ít nhất một thẻ flashcard!');
            return false;
        }

        // Update card numbers before submission
        this.updateCardNumbers();
        
        // Update hidden input for item count
        const itemCountInput = document.getElementById('itemCount');
        if (itemCountInput) {
            itemCountInput.value = this.cardCount;
        }

        // Clear draft after successful submission
        this.clearDraft();
        
        // Allow form to submit
        return true;
    }

    showAlert(type, message) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-triangle' : 'info-circle'}"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        const container = document.querySelector('.create-flashcard-page .container');
        if (container) {
            container.insertBefore(alertDiv, container.firstChild);
            
            // Auto-dismiss after 5 seconds
            setTimeout(() => {
                if (alertDiv.parentNode) {
                    alertDiv.remove();
                }
            }, 5000);
        }
    }

    showToast(type, message) {
        // Create toast notification
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-white bg-${type} border-0`;
        toast.setAttribute('role', 'alert');
        toast.setAttribute('aria-live', 'assertive');
        toast.setAttribute('aria-atomic', 'true');
        
        toast.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-${type === 'success' ? 'check-circle' : 'info-circle'}"></i> ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        `;
        
        // Add toast container if it doesn't exist
        let toastContainer = document.getElementById('create-flashcard-toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.id = 'create-flashcard-toast-container';
            toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
            toastContainer.style.zIndex = '1055';
            document.body.appendChild(toastContainer);
        }
        
        toastContainer.appendChild(toast);
        
        // Show toast
        const bsToast = new bootstrap.Toast(toast);
        bsToast.show();
        
        // Remove toast element after it's hidden
        toast.addEventListener('hidden.bs.toast', () => {
            toast.remove();
        });
    }

    getItemCount() {
        return this.cardCount;
    }
}

// Global instance
let createFlashcardManager;

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    createFlashcardManager = new CreateFlashcardManager();
    
    // Load draft if exists
    createFlashcardManager.loadDraft();
    
    // Add first card item when page loads
    createFlashcardManager.addCardItem();
});

// Keyboard shortcuts
document.addEventListener('keydown', function(event) {
    // Ctrl/Cmd + S to save draft
    if ((event.ctrlKey || event.metaKey) && event.key === 's') {
        event.preventDefault();
        createFlashcardManager.autoSaveForm();
    }
    
    // Ctrl/Cmd + Enter to submit form
    if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
        event.preventDefault();
        const form = document.querySelector('.create-flashcard-page #flashcardForm');
        if (form) {
            form.submit();
        }
    }
});

// Export for global use
window.CreateFlashcardManager = CreateFlashcardManager;
window.createFlashcardManager = createFlashcardManager; 