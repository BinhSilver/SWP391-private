// feedback.js
function updateFeedback() {
    const form = document.getElementById('editFeedbackForm');
    const data = new FormData(form);
    console.log('[updateFeedback] data:', Object.fromEntries(data.entries()));
    fetch(window.contextPath + '/course/feedback', {
        method: 'PUT',
        body: new URLSearchParams(data),
    }).then(res => {
        console.log('[updateFeedback] response status:', res.status);
        if (res.ok) location.reload();
        else alert('Cập nhật feedback thất bại!');
    }).catch(err => {
        console.error('[updateFeedback] error:', err);
    });
}

function deleteFeedback() {
    const form = document.getElementById('editFeedbackForm');
    const feedbackId = form.elements['feedbackId'].value;
    const userId = form.elements['userId'].value;
    console.log('[deleteFeedback] feedbackId:', feedbackId, 'userId:', userId);
    fetch(window.contextPath + `/course/feedback?feedbackId=${feedbackId}&userId=${userId}`, {
        method: 'DELETE',
    }).then(res => {
        console.log('[deleteFeedback] response status:', res.status);
        if (res.ok) location.reload();
        else alert('Xóa feedback thất bại!');
    }).catch(err => {
        console.error('[deleteFeedback] error:', err);
    });
}

function voteFeedback(feedbackId, voteType) {
    const userId = window.currentUserId;
    console.log('[voteFeedback] userId:', userId, 'feedbackId:', feedbackId, 'voteType:', voteType);
    if (!userId || userId === 'null') {
        alert('Bạn cần đăng nhập để vote!');
        return;
    }
    fetch(window.contextPath + '/course/feedback/vote', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `feedbackId=${feedbackId}&userId=${userId}&voteType=${voteType}`
    }).then(res => {
        console.log('[voteFeedback] response status:', res.status);
        if (res.ok) location.reload();
        else alert('Vote thất bại!');
    }).catch(err => {
        console.error('[voteFeedback] error:', err);
    });
} 