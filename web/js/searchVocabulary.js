document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const searchDropdown = document.getElementById('searchDropdown');
    
    // Sử dụng context path từ config
    const contextPath = window.contextPath || '';

    console.log('DOM ready');

    searchInput.addEventListener('input', function() {
        const keyword = this.value.trim();
        console.log('Input changed, keyword:', keyword);
        if (keyword.length > 0) {
            searchVocabulary(keyword);
        } else {
            searchDropdown.style.display = 'none';
            searchDropdown.innerHTML = '';
        }
    });

    function searchVocabulary(keyword) {
        const url = getApiUrl(API_CONFIG.SEARCH_VOCABULARY) + '?query=' + encodeURIComponent(keyword);
        console.log('Sending request to:', url);

        const xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    try {
                        const data = JSON.parse(xhr.responseText);
                        console.log('Data received:', data);
                        displayResults(data);
                    } catch (e) {
                        console.error('Error parsing JSON:', e);
                        searchDropdown.innerHTML = '<div class="search-result">Lỗi khi xử lý dữ liệu</div>';
                    }
                } else {
                    console.error('AJAX Error:', xhr.status, xhr.statusText);
                    searchDropdown.innerHTML = '<div class="search-result">Lỗi khi tìm kiếm</div>';
                }
            }
        };
        xhr.send();
    }

    function displayResults(data) {
        searchDropdown.innerHTML = '';
        if (data && data.length > 0) {
            const resultsToShow = Math.min(5, data.length); // Giới hạn 5 kết quả
            for (let i = 0; i < resultsToShow; i++) {
                const vocab = data[i];
                const resultHtml = `
                    <div class="search-result">
                        ${vocab.imagePath ? `<img src="${vocab.imagePath}" alt="${vocab.word}">` : ''}
                        <div class="vocab-details">
                            <div><span class="label">Tiếng Nhật:</span> <span class="value">${vocab.word || 'Không có'}</span></div>
                            <div><span class="label">Tiếng Việt:</span> <span class="value">${vocab.meaning || 'Không có'}</span></div>
                            <div><span class="label">Đọc:</span> <span class="value">${vocab.reading || 'Không có'}</span></div>
                            <div><span class="label">Ví dụ:</span> <span class="value">${vocab.example || 'Không có'}</span></div>
                            <button class="play-btn" data-word="${vocab.word || ''}"><i class="fa-solid fa-volume-up"></i> Phát âm</button>
                        </div>
                    </div>
                `;
                searchDropdown.innerHTML += resultHtml;
            }
            searchDropdown.style.display = 'block';
            addPlayButtonListeners();
        } else {
            searchDropdown.innerHTML = '<div id="search-resultt">Không tìm thấy kết quả</div>';
            searchDropdown.style.display = 'block';
        }
    }

    function addPlayButtonListeners() {
        const playButtons = searchDropdown.querySelectorAll('.play-btn');
        playButtons.forEach(button => {
            button.addEventListener('click', function() {
                const word = this.getAttribute('data-word');
                if (word && responsiveVoice) {
                    responsiveVoice.speak(word, "Japanese Female", { rate: 0.9 });
                }
            });
        });
    }

});