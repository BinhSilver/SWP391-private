// flashcard-viewer.js - Wasabii

let flashcards = window.flashcardsData || [];
let currentIndex = 0;
let isStudyMode = false;
let studyOrder = [];
let originalOrder = [...Array(flashcards.length).keys()];

function getRealIndex() {
    return isStudyMode ? studyOrder[currentIndex] : currentIndex;
}

function updateCard() {
    const realIndex = getRealIndex();
    const card = flashcards[realIndex];
    const frontContent = document.getElementById('frontContent');
    const backContent = document.getElementById('backContent');
    const noteContent = document.getElementById('noteContent');
    const noteSection = document.getElementById('noteSection');
    // Front: ưu tiên ảnh
    if (card.frontImage && card.frontImage !== null && card.frontImage.toString().trim() !== '') {
        frontContent.innerHTML = `<img src="${card.frontImage}" alt="Front" class="flashcard-image">`;
    } else {
        frontContent.textContent = card.front || 'Không có nội dung';
    }
    // Back: ưu tiên ảnh
    if (card.backImage && card.backImage !== null && card.backImage.toString().trim() !== '') {
        backContent.innerHTML = `<img src="${card.backImage}" alt="Back" class="flashcard-image">`;
    } else {
        backContent.textContent = card.back || 'Không có nội dung';
    }
    // Note
    if (card.note && card.note.trim() !== '') {
        noteContent.textContent = card.note;
        noteSection.style.display = 'block';
    } else {
        noteSection.style.display = 'none';
    }
    // Card info
    const currentCardInfo = document.getElementById('currentCardInfo');
    if (currentCardInfo) {
        currentCardInfo.textContent = `${currentIndex + 1} / ${flashcards.length} (OrderIndex: ${card.orderIndex})`;
    }
    // Navigation
    document.getElementById('prevBtn').disabled = currentIndex === 0;
    document.getElementById('nextBtn').disabled = currentIndex === flashcards.length - 1;
    // Reset card to front
    document.getElementById('flashcard').classList.remove('flipped');
    updateProgress();
}

function flipCard() {
    document.getElementById('flashcard').classList.toggle('flipped');
}

function nextCard() {
    if (currentIndex < flashcards.length - 1) {
        currentIndex++;
        updateCard();
    }
}

function previousCard() {
    if (currentIndex > 0) {
        currentIndex--;
        updateCard();
    }
}

function updateProgress() {
    const progress = ((currentIndex + 1) / flashcards.length) * 100;
    document.getElementById('progressBar').style.width = progress + '%';
    document.getElementById('progressText').textContent = `${currentIndex + 1} / ${flashcards.length}`;
}

// Study mode toggle
function toggleStudyMode() {
    isStudyMode = !isStudyMode;
    const button = document.getElementById('studyModeToggle');
    if (isStudyMode) {
        // Random order
        studyOrder = [...Array(flashcards.length).keys()];
        for (let i = studyOrder.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [studyOrder[i], studyOrder[j]] = [studyOrder[j], studyOrder[i]];
        }
        button.innerHTML = '<i class="fas fa-list"></i> Chế độ tuần tự';
        button.classList.add('active');
        currentIndex = 0;
    } else {
        button.innerHTML = '<i class="fas fa-random"></i> Chế độ học tập';
        button.classList.remove('active');
        currentIndex = 0;
    }
    updateCard();
}

function resetToOriginalOrder() {
    isStudyMode = false;
    studyOrder = [...originalOrder];
    currentIndex = 0;
    const button = document.getElementById('studyModeToggle');
    button.innerHTML = '<i class="fas fa-random"></i> Chế độ học tập';
    button.classList.remove('active');
    updateCard();
}

// Keyboard navigation
function handleKeydown(e) {
    switch(e.key) {
        case 'ArrowLeft':
            if (currentIndex > 0) previousCard();
            break;
        case 'ArrowRight':
            if (currentIndex < flashcards.length - 1) nextCard();
            break;
        case ' ':
            e.preventDefault();
            flipCard();
            break;
    }
}

document.addEventListener('DOMContentLoaded', function() {
    if (typeof window.flashcardsData !== 'undefined') {
        flashcards = window.flashcardsData;
        originalOrder = [...Array(flashcards.length).keys()];
    }
    if (flashcards.length > 0) {
        updateCard();
    }
    document.getElementById('studyModeToggle').addEventListener('click', toggleStudyMode);
    document.getElementById('prevBtn').addEventListener('click', previousCard);
    document.getElementById('nextBtn').addEventListener('click', nextCard);
    document.addEventListener('keydown', handleKeydown);
    document.getElementById('flashcard').addEventListener('click', flipCard);
    document.getElementById('resetOrderBtn').addEventListener('click', resetToOriginalOrder);
}); 