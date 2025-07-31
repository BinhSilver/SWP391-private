// feedback.js
function updateFeedback(feedbackId) {
    const form = document.getElementById('editFeedbackForm-' + feedbackId);
    if (!form) {
        console.error('[updateFeedback] Không tìm thấy form với id:', 'editFeedbackForm-' + feedbackId);
        return;
    }
    const data = new FormData(form);
    console.log('[updateFeedback] data:', Object.fromEntries(data.entries()));
    fetch(window.contextPath + '/course/feedback', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(data),
    }).then(res => {
        console.log('[updateFeedback] response status:', res.status);
        if (res.ok) location.reload();
        else alert('Cập nhật feedback thất bại!');
    }).catch(err => {
        console.error('[updateFeedback] error:', err);
    });
}

function editFeedback(feedbackId) {
    const userId = window.currentUserId;
    console.log('[editFeedback] feedbackId:', feedbackId, 'userId:', userId);
    
    if (!userId || userId === 'null') {
        alert('Bạn cần đăng nhập để sửa feedback!');
        return;
    }
    
    // Lấy thông tin feedback hiện tại từ DOM
    const feedbackItem = document.querySelector(`[data-feedback-id="${feedbackId}"]`);
    if (!feedbackItem) {
        console.error('[editFeedback] Không tìm thấy feedback item');
        return;
    }
    
    const currentContent = feedbackItem.querySelector('.feedback-content p').textContent;
    const currentRating = feedbackItem.querySelectorAll('.star.filled').length;
    
    // Tạo modal edit
    const modal = document.createElement('div');
    modal.className = 'feedback-edit-modal';
    modal.innerHTML = `
        <div class="modal-overlay">
            <div class="modal-content">
                <h4>Sửa đánh giá</h4>
                <form id="editFeedbackForm">
                    <div class="form-group">
                        <label>Nội dung:</label>
                        <textarea name="content" rows="4" required>${currentContent}</textarea>
                    </div>
                    <div class="form-group">
                        <label>Đánh giá:</label>
                        <div class="rating-input">
                            ${Array.from({length: 5}, (_, i) => 
                                `<span class="star ${i < currentRating ? 'filled' : 'empty'}" 
                                      onclick="setRating(${i + 1})">★</span>`
                            ).join('')}
                            <input type="hidden" name="rating" value="${currentRating}">
                        </div>
                    </div>
                    <div class="modal-actions">
                        <button type="button" onclick="closeEditModal()" class="btn-cancel">Hủy</button>
                        <button type="submit" class="btn-save">Lưu</button>
                    </div>
                </form>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // Xử lý submit form
    document.getElementById('editFeedbackForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const formData = new FormData(this);
        const content = formData.get('content');
        const rating = formData.get('rating');
        
        updateFeedback(feedbackId, content, rating);
    });
}

function setRating(rating) {
    const stars = document.querySelectorAll('.rating-input .star');
    const ratingInput = document.querySelector('input[name="rating"]');
    
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('filled');
            star.classList.remove('empty');
        } else {
            star.classList.add('empty');
            star.classList.remove('filled');
        }
    });
    
    ratingInput.value = rating;
}

function closeEditModal() {
    const modal = document.querySelector('.feedback-edit-modal');
    if (modal) {
        modal.remove();
    }
}

function addFeedback(event) {
    event.preventDefault();
    
    const userId = window.currentUserId;
    const contextPath = window.contextPath;
    
    if (!userId || userId === 'null') {
        alert('Bạn cần đăng nhập để viết feedback!');
        return;
    }
    
    const form = event.target;
    const formData = new FormData(form);
    const content = formData.get('content');
    const rating = formData.get('rating');
    
    if (rating == 0) {
        alert('Vui lòng chọn đánh giá sao!');
        return;
    }
    
    console.log('[addFeedback] content:', content, 'rating:', rating);
    
    if (!contextPath) {
        console.error('[addFeedback] contextPath is undefined!');
        alert('Lỗi: contextPath không được định nghĩa!');
        return;
    }
    
    const url = contextPath + '/course/feedback';
    const body = `userId=${userId}&courseId=${window.courseId}&content=${encodeURIComponent(content)}&rating=${rating}&redirectUrl=CourseDetailServlet?id=${window.courseId}`;
    
    console.log('[addFeedback] URL:', url);
    console.log('[addFeedback] Body:', body);
    
    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body
    }).then(res => {
        console.log('[addFeedback] response status:', res.status);
        if (res.ok) {
            console.log('[addFeedback] Add thành công!');
            location.reload();
        } else {
            console.error('[addFeedback] Add thất bại! Status:', res.status);
            alert('Thêm feedback thất bại!');
        }
    }).catch(err => {
        console.error('[addFeedback] error:', err);
        alert('Lỗi kết nối: ' + err.message);
    });
}

function updateFeedback(feedbackId, content, rating) {
    const userId = window.currentUserId;
    const contextPath = window.contextPath;
    
    console.log('[updateFeedback] feedbackId:', feedbackId, 'content:', content, 'rating:', rating);
    
    if (!contextPath) {
        console.error('[updateFeedback] contextPath is undefined!');
        alert('Lỗi: contextPath không được định nghĩa!');
        return;
    }
    
    const url = contextPath + '/course/feedback';
    const body = `feedbackId=${feedbackId}&userId=${userId}&content=${encodeURIComponent(content)}&rating=${rating}`;
    
    console.log('[updateFeedback] URL:', url);
    console.log('[updateFeedback] Body:', body);
    
    fetch(url, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body
    }).then(res => {
        console.log('[updateFeedback] response status:', res.status);
        if (res.ok) {
            console.log('[updateFeedback] Update thành công!');
            closeEditModal();
            location.reload();
        } else {
            console.error('[updateFeedback] Update thất bại! Status:', res.status);
            alert('Cập nhật feedback thất bại!');
        }
    }).catch(err => {
        console.error('[updateFeedback] error:', err);
        alert('Lỗi kết nối: ' + err.message);
    });
}

function deleteFeedback(feedbackId) {
    const userId = window.currentUserId;
    console.log('[deleteFeedback] feedbackId:', feedbackId, 'userId:', userId);
    
    if (!userId || userId === 'null') {
        alert('Bạn cần đăng nhập để xóa feedback!');
        return;
    }
    
    if (!confirm('Bạn có chắc chắn muốn xóa feedback này?')) {
        return;
    }
    
    const contextPath = window.contextPath;
    if (!contextPath) {
        console.error('[deleteFeedback] contextPath is undefined!');
        alert('Lỗi: contextPath không được định nghĩa!');
        return;
    }
    
    const url = contextPath + `/course/feedback?feedbackId=${feedbackId}&userId=${userId}`;
    console.log('[deleteFeedback] URL:', url);
    
    fetch(url, {
        method: 'DELETE',
    }).then(res => {
        console.log('[deleteFeedback] response status:', res.status);
        if (res.ok) {
            console.log('[deleteFeedback] Delete thành công!');
            location.reload();
        } else {
            console.error('[deleteFeedback] Delete thất bại! Status:', res.status);
            alert('Xóa feedback thất bại!');
        }
    }).catch(err => {
        console.error('[deleteFeedback] error:', err);
        alert('Lỗi kết nối: ' + err.message);
    });
}

function voteFeedback(feedbackId, voteType) {
    const userId = window.currentUserId;
    const contextPath = window.contextPath;
    console.log('[voteFeedback] userId:', userId, 'feedbackId:', feedbackId, 'voteType:', voteType);
    console.log('[voteFeedback] contextPath:', contextPath);
    
    if (!userId || userId === 'null') {
        alert('Bạn cần đăng nhập để vote!');
        return;
    }
    
    if (!contextPath) {
        console.error('[voteFeedback] contextPath is undefined!');
        alert('Lỗi: contextPath không được định nghĩa!');
        return;
    }
    
    const url = contextPath + '/course/feedback/vote';
    const body = `feedbackId=${feedbackId}&userId=${userId}&voteType=${voteType}`;
    
    console.log('[voteFeedback] URL:', url);
    console.log('[voteFeedback] Body:', body);
    
    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body
    }).then(res => {
        console.log('[voteFeedback] response status:', res.status);
        if (res.ok) {
            console.log('[voteFeedback] Vote thành công!');
            location.reload();
        } else {
            console.error('[voteFeedback] Vote thất bại! Status:', res.status);
            alert('Vote thất bại!');
        }
    }).catch(err => {
        console.error('[voteFeedback] error:', err);
        alert('Lỗi kết nối: ' + err.message);
    });
} 