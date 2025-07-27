// Edit Flashcard JavaScript
class EditFlashcardManager {
    constructor() {
        this.initializeEventListeners();
        this.setupFormValidation();
    }

    initializeEventListeners() {
        // Form validation
        const flashcardForm = document.getElementById('flashcardForm');
        if (flashcardForm) {
            flashcardForm.addEventListener('submit', this.handleFlashcardFormSubmit.bind(this));
        }

        // Auto-save functionality
        this.setupAutoSave();
    }

    setupFormValidation() {
        // Validate required fields
        const requiredFields = document.querySelectorAll('[required]');
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

    handleFlashcardFormSubmit(event) {
        const title = document.getElementById('title');
        if (!this.validateField({ target: title })) {
            event.preventDefault();
            return false;
        }
        return true;
    }

    setupAutoSave() {
        // Auto-save flashcard info every 30 seconds
        let autoSaveTimer;
        const flashcardForm = document.getElementById('flashcardForm');
        
        if (flashcardForm) {
            const inputs = flashcardForm.querySelectorAll('input, textarea');
            inputs.forEach(input => {
                input.addEventListener('input', () => {
                    clearTimeout(autoSaveTimer);
                    autoSaveTimer = setTimeout(() => {
                        this.autoSaveFlashcard();
                    }, 30000); // 30 seconds
                });
            });
        }
    }

    async autoSaveFlashcard() {
        const form = document.getElementById('flashcardForm');
        if (!form) return;

        const formData = new FormData(form);
        
        try {
            const response = await fetch('edit-flashcard', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                this.showToast('success', 'Đã tự động lưu thay đổi');
            }
        } catch (error) {
            console.error('Auto-save failed:', error);
        }
    }
}

// Global functions for item editing
function toggleEdit(itemId) {
    const viewDiv = document.getElementById(`view-${itemId}`);
    const editDiv = document.getElementById(`edit-${itemId}`);
    const itemContainer = document.getElementById(`item-${itemId}`);
    
    if (viewDiv.style.display === 'none') {
        // Switch to view mode
        viewDiv.style.display = 'flex';
        editDiv.style.display = 'none';
        itemContainer.classList.remove('editing');
    } else {
        // Switch to edit mode
        viewDiv.style.display = 'none';
        editDiv.style.display = 'block';
        itemContainer.classList.add('editing');
        
        // Focus on first input
        const firstInput = editDiv.querySelector('input, textarea');
        if (firstInput) {
            firstInput.focus();
        }
    }
    
    // Check if any items are in edit mode and show/hide save all button
    checkEditModeAndShowSaveAll();
}

function cancelEdit(itemId) {
    toggleEdit(itemId);
    checkEditModeAndShowSaveAll();
}

async function saveItem(itemId) {
    const form = document.querySelector(`form[data-item-id="${itemId}"]`);
    if (!form) {
        showAlert('danger', 'Không tìm thấy form cho item này');
        return;
    }
    
    const formData = new FormData(form);
    const itemContainer = document.getElementById(`item-${itemId}`);
    
    // Add loading state
    itemContainer.classList.add('loading');
    
    try {
        const response = await fetch('edit-flashcard-item', {
            method: 'POST',
            body: formData
        });

        const data = await response.json();
        
        if (data.success) {
            showAlert('success', data.message);
            toggleEdit(itemId);
            
            // Clear unsaved changes flag for this item
            itemContainer.classList.remove('has-changes');
            
            // Check if all items are saved
            const editingItems = document.querySelectorAll('.flashcard-item.editing');
            if (editingItems.length === 0) {
                window.hasUnsavedChanges = false;
            }
            
            // Reload page after a short delay to show updated data
            setTimeout(() => {
                location.reload();
            }, 1000);
        } else {
            showAlert('danger', data.message);
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('danger', 'Có lỗi xảy ra khi lưu thay đổi');
    } finally {
        itemContainer.classList.remove('loading');
    }
}

function showAlert(type, message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-triangle'}"></i> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    const container = document.querySelector('.container');
    container.insertBefore(alertDiv, container.firstChild);
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

function showToast(type, message) {
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
    let toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
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

// Keyboard shortcuts
document.addEventListener('keydown', function(event) {
    // Ctrl/Cmd + S to save flashcard
    if ((event.ctrlKey || event.metaKey) && event.key === 's') {
        event.preventDefault();
        const flashcardForm = document.getElementById('flashcardForm');
        if (flashcardForm) {
            flashcardForm.submit();
        }
    }
    
    // Escape to cancel editing
    if (event.key === 'Escape') {
        const editingItems = document.querySelectorAll('.flashcard-item.editing');
        editingItems.forEach(item => {
            const itemId = item.id.replace('item-', '');
            cancelEdit(itemId);
        });
    }
});

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    new EditFlashcardManager();
    
    // Add smooth scrolling
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // Initialize image previews for existing images
    initializeImagePreviews();
    
    // Fix file input click issue
    fixFileInputClick();
    
    // Add form change listeners to show save all button
    addFormChangeListeners();
    
    // Add beforeunload event listener for unsaved changes warning
    addUnsavedChangesWarning();
});

// Fix file input click issue
function fixFileInputClick() {
    console.log('Fixing file input click...');
    
    // Remove any existing event listeners
    const fileInputs = document.querySelectorAll('input[type="file"]');
    console.log('Found', fileInputs.length, 'file inputs');
    
    fileInputs.forEach((input, index) => {
        console.log('Processing input', index, input.name, input.id);
        
        // Clone the input to remove all event listeners
        const newInput = input.cloneNode(true);
        input.parentNode.replaceChild(newInput, input);
        
        // Add new event listener
        newInput.addEventListener('change', function(e) {
            console.log('File input changed:', this.name, this.id);
            const previewId = this.getAttribute('data-preview') || 
                             (this.id === 'coverImage' ? 'coverPreview' : 
                              this.name === 'frontImage' ? 'frontPreview' + this.closest('.flashcard-item').id.replace('item-', '') :
                              this.name === 'backImage' ? 'backPreview' + this.closest('.flashcard-item').id.replace('item-', '') : '');
            console.log('Preview ID:', previewId);
            if (previewId) {
                previewImage(this, previewId);
            }
        });
    });
    
    // Add click event to labels
    const fileLabels = document.querySelectorAll('.file-input-label');
    console.log('Found', fileLabels.length, 'file labels');
    
    fileLabels.forEach((label, index) => {
        console.log('Processing label', index);
        
        // Remove existing event listeners
        const newLabel = label.cloneNode(true);
        label.parentNode.replaceChild(newLabel, label);
        
        newLabel.addEventListener('click', function(e) {
            console.log('Label clicked!');
            e.preventDefault();
            e.stopPropagation();
            const input = this.parentNode.querySelector('input[type="file"]');
            console.log('Found input:', input);
            if (input) {
                input.click();
            }
        });
    });
}

// Add form change listeners to show save all button
function addFormChangeListeners() {
    const forms = document.querySelectorAll('.item-edit-form');
    forms.forEach(form => {
        const inputs = form.querySelectorAll('input, textarea');
        inputs.forEach(input => {
            input.addEventListener('input', function() {
                // Mark the parent item as having changes
                const itemContainer = form.closest('.flashcard-item');
                if (itemContainer) {
                    itemContainer.classList.add('has-changes');
                }
                // Mark that there are unsaved changes
                window.hasUnsavedChanges = true;
            });
        });
    });
    
    // Also listen to flashcard form changes
    const flashcardForm = document.getElementById('flashcardForm');
    if (flashcardForm) {
        const inputs = flashcardForm.querySelectorAll('input, textarea');
        inputs.forEach(input => {
            input.addEventListener('input', function() {
                window.hasUnsavedChanges = true;
            });
        });
    }
}

// Add unsaved changes warning
function addUnsavedChangesWarning() {
    // Warning when leaving page
    window.addEventListener('beforeunload', function(e) {
        if (window.hasUnsavedChanges) {
            e.preventDefault();
            e.returnValue = 'Bạn có thay đổi chưa lưu. Bạn có chắc muốn rời khỏi trang?';
            return 'Bạn có thay đổi chưa lưu. Bạn có chắc muốn rời khỏi trang?';
        }
    });
    
    // Warning when clicking links
    document.addEventListener('click', function(e) {
        if (window.hasUnsavedChanges && e.target.tagName === 'A' && e.target.href) {
            const href = e.target.href;
            // Don't warn for same page links or anchors
            if (href.includes(window.location.href) || href.startsWith('#')) {
                return;
            }
            
            e.preventDefault();
            showUnsavedChangesDialog(href);
        }
    });
    
    // Warning when clicking back button
    window.addEventListener('popstate', function(e) {
        if (window.hasUnsavedChanges) {
            e.preventDefault();
            showUnsavedChangesDialog('back');
        }
    });
}

// Initialize image previews for existing images
function initializeImagePreviews() {
    const imagePreviews = document.querySelectorAll('.image-preview');
    imagePreviews.forEach(preview => {
        if (preview.src && !preview.src.endsWith('null') && preview.src !== '' && preview.src !== window.location.href) {
            preview.style.display = 'block';
        } else {
            preview.style.display = 'none';
        }
    });
    
    // Ensure file input labels are clickable
    const fileInputLabels = document.querySelectorAll('.file-input-label');
    fileInputLabels.forEach(label => {
        label.addEventListener('click', function(e) {
            e.preventDefault();
            const input = this.previousElementSibling;
            if (input && input.type === 'file') {
                input.click();
            }
        });
    });
}

// Image preview function
function previewImage(input, previewId) {
    const preview = document.getElementById(previewId);
    const file = input.files[0];
    
    if (file) {
        // Validate file type
        if (!file.type.startsWith('image/')) {
            showAlert('danger', 'Vui lòng chọn file ảnh hợp lệ');
            input.value = '';
            return;
        }
        
        // Validate file size (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
            showAlert('danger', 'File ảnh không được lớn hơn 5MB');
            input.value = '';
            return;
        }
        
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
            
            // Add loading indicator
            preview.style.opacity = '0.7';
            preview.onload = function() {
                preview.style.opacity = '1';
            };
        };
        reader.readAsDataURL(file);
    } else {
        // If no file selected, show existing image if available
        if (preview.src && !preview.src.endsWith('null') && preview.src !== '') {
            preview.style.display = 'block';
        } else {
            preview.style.display = 'none';
        }
    }
}

// Check if any items are in edit mode and show/hide save all button
function checkEditModeAndShowSaveAll() {
    const editingItems = document.querySelectorAll('.flashcard-item.editing');
    const saveAllBtn = document.getElementById('saveAllBtn');
    
    if (editingItems.length > 0) {
        saveAllBtn.style.display = 'inline-block';
    } else {
        saveAllBtn.style.display = 'none';
    }
}

// Save all items that are in edit mode
async function saveAllItems() {
    const editingItems = document.querySelectorAll('.flashcard-item.editing');
    if (editingItems.length === 0) {
        showAlert('info', 'Không có thay đổi nào để lưu');
        return;
    }
    
    const saveAllBtn = document.getElementById('saveAllBtn');
    const originalText = saveAllBtn.innerHTML;
    
    // Disable button and show loading
    saveAllBtn.disabled = true;
    saveAllBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang lưu...';
    
    let successCount = 0;
    let errorCount = 0;
    
    try {
        // Save all items sequentially
        for (const item of editingItems) {
            const itemId = item.id.replace('item-', '');
            const form = item.querySelector('form[data-item-id]');
            
            if (form) {
                const formData = new FormData(form);
                
                try {
                    const response = await fetch('edit-flashcard-item', {
                        method: 'POST',
                        body: formData
                    });
                    
                    const data = await response.json();
                    
                    if (data.success) {
                        successCount++;
                    } else {
                        errorCount++;
                        console.error('Error saving item', itemId, data.message);
                    }
                } catch (error) {
                    errorCount++;
                    console.error('Error saving item', itemId, error);
                }
            }
        }
        
        // Show result
        if (errorCount === 0) {
            showAlert('success', `Đã lưu thành công ${successCount} thẻ!`);
            // Clear unsaved changes flag
            window.hasUnsavedChanges = false;
            // Reload page to show updated data
            setTimeout(() => {
                location.reload();
            }, 1500);
        } else {
            showAlert('warning', `Đã lưu ${successCount} thẻ, ${errorCount} thẻ lỗi. Vui lòng kiểm tra lại.`);
        }
        
    } catch (error) {
        console.error('Error in saveAllItems:', error);
        showAlert('danger', 'Có lỗi xảy ra khi lưu tất cả');
    } finally {
        // Re-enable button
        saveAllBtn.disabled = false;
        saveAllBtn.innerHTML = originalText;
    }
}

// Show unsaved changes dialog
function showUnsavedChangesDialog(targetUrl) {
    // Create modal dialog
    const modal = document.createElement('div');
    modal.className = 'unsaved-changes-modal';
    modal.innerHTML = `
        <div class="unsaved-changes-dialog">
            <div class="unsaved-changes-header">
                <h5><i class="fas fa-exclamation-triangle text-warning"></i> Thay đổi chưa lưu</h5>
            </div>
            <div class="unsaved-changes-body">
                <p>Bạn có thay đổi chưa lưu. Bạn có muốn:</p>
                <ul>
                    <li><strong>Lưu tất cả thay đổi</strong> trước khi rời khỏi trang?</li>
                    <li><strong>Rời khỏi trang</strong> mà không lưu thay đổi?</li>
                    <li><strong>Ở lại trang</strong> để tiếp tục chỉnh sửa?</li>
                </ul>
            </div>
            <div class="unsaved-changes-footer">
                <button type="button" class="btn btn-secondary" onclick="closeUnsavedChangesDialog()">
                    <i class="fas fa-times"></i> Hủy
                </button>
                <button type="button" class="btn btn-danger" onclick="leaveWithoutSaving('${targetUrl}')">
                    <i class="fas fa-sign-out-alt"></i> Rời khỏi trang
                </button>
                <button type="button" class="btn btn-success" onclick="saveAndLeave('${targetUrl}')">
                    <i class="fas fa-save"></i> Lưu tất cả
                </button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Add backdrop
    const backdrop = document.createElement('div');
    backdrop.className = 'unsaved-changes-backdrop';
    document.body.appendChild(backdrop);
}

// Close unsaved changes dialog
function closeUnsavedChangesDialog() {
    const modal = document.querySelector('.unsaved-changes-modal');
    const backdrop = document.querySelector('.unsaved-changes-backdrop');
    
    if (modal) modal.remove();
    if (backdrop) backdrop.remove();
}

// Leave without saving
function leaveWithoutSaving(targetUrl) {
    // Show confirmation dialog
    if (confirm('Bạn có chắc chắn muốn rời khỏi trang mà không lưu thay đổi?')) {
        window.hasUnsavedChanges = false;
        closeUnsavedChangesDialog();
        
        if (targetUrl === 'back') {
            window.history.back();
        } else {
            window.location.href = targetUrl;
        }
    }
}

// Save and leave
async function saveAndLeave(targetUrl) {
    closeUnsavedChangesDialog();
    
    // Save all items first
    await saveAllItems();
    
    // Then navigate
    if (targetUrl === 'back') {
        window.history.back();
    } else {
        window.location.href = targetUrl;
    }
}

// Export functions for global use
window.EditFlashcardManager = EditFlashcardManager;
window.toggleEdit = toggleEdit;
window.cancelEdit = cancelEdit;
window.saveItem = saveItem;
window.saveAllItems = saveAllItems;
window.showAlert = showAlert;
window.showToast = showToast;
window.previewImage = previewImage;
window.closeUnsavedChangesDialog = closeUnsavedChangesDialog;
window.leaveWithoutSaving = leaveWithoutSaving;
window.saveAndLeave = saveAndLeave; 
window.previewImage = previewImage; 