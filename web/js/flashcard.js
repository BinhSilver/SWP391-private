// flashcard.js - Wasabii

function deleteFlashcard(flashcardID) {
    document.getElementById('flashcardID').value = flashcardID;
    new bootstrap.Modal(document.getElementById('deleteModal')).show();
}

// Ẩn popup xóa flashcard sau 5s
setTimeout(function() {
    var alert = document.getElementById('deleteSuccessAlert');
    if(alert) {
        var bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
        bsAlert.close();
    }
}, 5000);

// Ẩn popup tạo flashcard sau 5s
setTimeout(function() {
    var alert = document.getElementById('createSuccessAlert');
    if(alert) {
        var bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
        bsAlert.close();
    }
}, 5000);

// Flip card hiệu ứng
function enableFlashcardFlip() {
    document.querySelectorAll('.flashcard-flip').forEach(function(card) {
        card.addEventListener('click', function() {
            card.classList.toggle('flipped');
        });
    });
}

document.addEventListener('DOMContentLoaded', function() {
    enableFlashcardFlip();
}); 