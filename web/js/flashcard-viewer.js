// Flashcard Viewer JavaScript
class FlashcardViewer {
    constructor() {
        this.flashcards = [];
        this.currentIndex = 0;
        this.isStudyMode = false;
        this.studyOrder = [];
        this.originalOrder = [];
        
        // Không gọi init() ngay trong constructor
        // Sẽ gọi sau khi DOM đã load
    }
    
    init() {
        // Tạo flashcards array từ dữ liệu JSP
        this.createFlashcardsArray();
        
        // Debug: In ra thông tin chi tiết về dữ liệu flashcard
        console.log('DEBUG FLASHCARDS DATA:', JSON.stringify(this.flashcards, null, 2));
        console.log('DEBUG FLASHCARDS LENGTH:', this.flashcards.length);
        
        if (this.flashcards.length > 0) {
            console.log('DEBUG FIRST CARD:', this.flashcards[0]);
            console.log('DEBUG FIRST CARD FRONT:', this.flashcards[0].front);
            console.log('DEBUG FIRST CARD BACK:', this.flashcards[0].back);
        }
        
        this.originalOrder = [...Array(this.flashcards.length).keys()];
        
        if (this.flashcards.length > 0) {
            console.log('[FlashcardViewer] Flashcards loaded:', this.flashcards.length, 'items');
            console.log('[FlashcardViewer] Original order:', this.originalOrder);
            console.log('[FlashcardViewer] Flashcards array:', this.flashcards);
            
            // Đảm bảo card bắt đầu ở mặt trước
            const flashcard = document.getElementById('flashcard');
            if (flashcard) {
                flashcard.classList.remove('flipped');
                console.log('[FlashcardViewer] Initial card state: front (not flipped)');
            }
            
            this.updateCard();
            this.updateProgress();
            this.setupEventListeners();
        }
    }
    
    createFlashcardsArray() {
        // Đọc dữ liệu từ data attributes
        const dataContainer = document.getElementById('flashcardData');
        if (dataContainer) {
            const items = dataContainer.querySelectorAll('.flashcard-item');
            console.log('[FlashcardViewer] Found', items.length, 'flashcard items in DOM');
            
            items.forEach((item, index) => {
                const flashcardData = {
                    id: parseInt(item.dataset.id),
                    front: item.dataset.front || '',
                    back: item.dataset.back || '',
                    frontImage: item.dataset.frontImage || '',
                    backImage: item.dataset.backImage || '',
                    note: item.dataset.note || '',
                    orderIndex: parseInt(item.dataset.order)
                };
                
                console.log('[FlashcardViewer] Item', index + 1, 'data:', flashcardData);
                this.flashcards.push(flashcardData);
            });
        } else {
            console.error('[FlashcardViewer] flashcardData container not found!');
        }
    }
    
    setupEventListeners() {
        // Study mode toggle
        const studyModeToggle = document.getElementById('studyModeToggle');
        if (studyModeToggle) {
            studyModeToggle.addEventListener('click', () => this.toggleStudyMode());
        }
        
        // Keyboard navigation
        document.addEventListener('keydown', (e) => this.handleKeyboard(e));
    }
    
    getRealIndex() {
        // Nếu đang ở study mode (random), lấy index thực tế từ studyOrder
        return this.isStudyMode ? this.studyOrder[this.currentIndex] : this.currentIndex;
    }
    
    updateCard() {
        const realIndex = this.getRealIndex();
        const card = this.flashcards[realIndex];
        
        console.log('[FlashcardViewer] updateCard - currentIndex:', this.currentIndex, 'realIndex:', realIndex, 'card:', card);
        console.log('[FlashcardViewer] Front content:', card.front);
        console.log('[FlashcardViewer] Back content:', card.back);
        console.log('[FlashcardViewer] FrontImage value:', card.frontImage, 'type:', typeof card.frontImage);
        console.log('[FlashcardViewer] BackImage value:', card.backImage, 'type:', typeof card.backImage);
        console.log('[FlashcardViewer] Note content:', card.note);
        
        const frontContent = document.getElementById('frontContent');
        const backContent = document.getElementById('backContent');
        const noteContent = document.getElementById('noteContent');
        const noteSection = document.getElementById('noteSection');

        if (frontContent && backContent) {
            // Update front content - Mặt trước hiển thị từ tiếng Nhật
            if (card.frontImage && card.frontImage !== 'null' && card.frontImage.trim() !== '') {
                frontContent.innerHTML = `
                    <div style="display: flex; align-items: center; justify-content: center; gap: 2rem;">
                        <div class="text-content" style="flex:1;">${card.front || 'Không có nội dung'}</div>
                        <img src="${card.frontImage}" alt="Front" class="flashcard-image" style="max-width:180px;max-height:180px;flex-shrink:0;">
                    </div>
                `;
                console.log('[FlashcardViewer] Front image displayed:', card.frontImage);
            } else {
                // Hiển thị từ tiếng Nhật ở mặt trước
                frontContent.textContent = card.front || 'Không có nội dung';
                console.log('[FlashcardViewer] Front text displayed:', card.front);
            }
            
            // Update back content - Mặt sau hiển thị nghĩa tiếng Việt và hình nếu có
            if (card.backImage && card.backImage !== 'null' && card.backImage.trim() !== '') {
                backContent.innerHTML = `
                    <div style="display: flex; align-items: center; justify-content: center; gap: 2rem;">
                        <div class="text-content" style="flex:1;">${card.back || 'Không có nội dung'}</div>
                        <img src="${card.backImage}" alt="Back" class="flashcard-image" style="max-width:180px;max-height:180px;flex-shrink:0;">
                    </div>
                `;
                console.log('[FlashcardViewer] Back image displayed:', card.backImage);
            } else {
                // Hiển thị nghĩa tiếng Việt ở mặt sau
                backContent.textContent = card.back || 'Không có nội dung';
                console.log('[FlashcardViewer] Back text displayed:', card.back);
            }
        } else {
            console.error('[FlashcardViewer] Front or back content elements not found!');
        }

        // Update note - hiển thị ghi chú
        if (noteContent && noteSection) {
            if (card.note && card.note !== 'null' && card.note.trim() !== '') {
                noteContent.textContent = card.note;
                noteSection.style.display = 'block';
                console.log('[FlashcardViewer] Note displayed:', card.note);
            } else {
                noteSection.style.display = 'none';
            }
        }

        // Update card info
        const currentCardInfo = document.getElementById('currentCardInfo');
        if (currentCardInfo) {
            currentCardInfo.textContent = (this.currentIndex + 1) + ' / ' + this.flashcards.length;
            console.log('[FlashcardViewer] currentCardInfo updated:', (this.currentIndex + 1) + ' / ' + this.flashcards.length);
        }

        // Update navigation buttons
        const prevBtn = document.getElementById('prevBtn');
        const nextBtn = document.getElementById('nextBtn');
        
        if (prevBtn) prevBtn.disabled = this.currentIndex === 0;
        if (nextBtn) nextBtn.disabled = this.currentIndex === this.flashcards.length - 1;

        // Reset card to front - Đảm bảo card luôn hiển thị mặt trước khi load
        const flashcard = document.getElementById('flashcard');
        if (flashcard) {
            flashcard.classList.remove('flipped');
            console.log('[FlashcardViewer] Card reset to front, flipped class removed');
            console.log('[FlashcardViewer] Card classes after reset:', flashcard.className);
        }
    }
    
    flipCard() {
        const flashcard = document.getElementById('flashcard');
        if (flashcard) {
            flashcard.classList.toggle('flipped');
        }
    }
    
    nextCard() {
        if (this.currentIndex < this.flashcards.length - 1) {
            this.currentIndex++;
            this.updateCard();
            this.updateProgress();
        }
    }
    
    previousCard() {
        if (this.currentIndex > 0) {
            this.currentIndex--;
            this.updateCard();
            this.updateProgress();
        }
    }
    
    updateProgress() {
        const progressBar = document.getElementById('progressBar');
        const progressText = document.getElementById('progressText');
        
        if (progressBar && progressText) {
            const progress = ((this.currentIndex + 1) / this.flashcards.length) * 100;
            progressBar.style.width = progress + '%';
            progressText.textContent = (this.currentIndex + 1) + ' / ' + this.flashcards.length;
        }
    }
    
    toggleStudyMode() {
        this.isStudyMode = !this.isStudyMode;
        const button = document.getElementById('studyModeToggle');
        
        if (this.isStudyMode) {
            // Generate random order
            this.studyOrder = [...Array(this.flashcards.length).keys()];
            for (let i = this.studyOrder.length - 1; i > 0; i--) {
                const j = Math.floor(Math.random() * (i + 1));
                [this.studyOrder[i], this.studyOrder[j]] = [this.studyOrder[j], this.studyOrder[i]];
            }
            
            if (button) {
                button.innerHTML = '<i class="fas fa-list"></i> Chế độ tuần tự';
                button.classList.add('active');
            }
            
            // Set current index to first in study order
            this.currentIndex = 0;
            console.log('[FlashcardViewer] Study mode activated, random order:', this.studyOrder);
        } else {
            if (button) {
                button.innerHTML = '<i class="fas fa-random"></i> Chế độ học tập';
                button.classList.remove('active');
            }
            
            // Reset to original order
            this.currentIndex = 0;
            console.log('[FlashcardViewer] Study mode deactivated, original order restored');
        }
        
        this.updateCard();
        this.updateProgress();
    }
    
    resetToOriginalOrder() {
        this.isStudyMode = false;
        this.studyOrder = [...this.originalOrder];
        this.currentIndex = 0;
        
        const button = document.getElementById('studyModeToggle');
        if (button) {
            button.innerHTML = '<i class="fas fa-random"></i> Chế độ học tập';
            button.classList.remove('active');
        }
        
        console.log('[FlashcardViewer] Reset to original order:', this.originalOrder);
        this.updateCard();
        this.updateProgress();
    }
    
    handleKeyboard(e) {
        switch(e.key) {
            case 'ArrowLeft':
                if (this.currentIndex > 0) {
                    this.previousCard();
                }
                break;
            case 'ArrowRight':
                if (this.currentIndex < this.flashcards.length - 1) {
                    this.nextCard();
                }
                break;
            case ' ':
                e.preventDefault();
                this.flipCard();
                break;
        }
    }
}

// Global functions for onclick handlers
function flipCard() {
    if (window.flashcardViewer) {
        window.flashcardViewer.flipCard();
    }
}

function nextCard() {
    if (window.flashcardViewer) {
        window.flashcardViewer.nextCard();
    }
}

function previousCard() {
    if (window.flashcardViewer) {
        window.flashcardViewer.previousCard();
    }
}

function resetToOriginalOrder() {
    if (window.flashcardViewer) {
        window.flashcardViewer.resetToOriginalOrder();
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Create global instance và khởi tạo ngay
    window.flashcardViewer = new FlashcardViewer();
    window.flashcardViewer.init();
}); 