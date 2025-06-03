let visibleCountVocabulary = 2;
let allVocabularies = [];

function searchVocabulary() {
    const query = document.getElementById("searchInputVocabulary").value.trim();
    const resultContainer = document.getElementById("vocabularyResults");
    const loadMoreButton = document.getElementById("loadMoreVocabulary");

    if (query === "") {
        resultContainer.innerHTML = "";
        loadMoreButton.style.display = "none";
        return;
    }

    fetch(`SearchVocabulary?query=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(data => {
            allVocabularies = data;
            visibleCountVocabulary = 2;
            resultContainer.innerHTML = "";

            if (allVocabularies.length === 0) {
                resultContainer.innerHTML = "<p>Không tìm thấy từ vựng.</p>";
                loadMoreButton.style.display = "none";
            } else {
                displayVocabularies();
                loadMoreButton.style.display = allVocabularies.length > visibleCountVocabulary ? "block" : "none";
            }
        });
}

function displayVocabularies() {
    const container = document.getElementById("vocabularyResults");
    container.innerHTML = "";

    allVocabularies.slice(0, visibleCountVocabulary).forEach(voca => {
        const div = document.createElement("div");
        div.classList.add("search-item");
        div.innerHTML = `
            <h4>${voca.word}</h4>
            <p><b>Meaning:</b> ${voca.meaning}</p>
            <p><b>Type:</b> ${voca.wordType}</p>
        `;
        container.appendChild(div);
    });
}

function loadMoreVocabulary() {
    visibleCountVocabulary += 2;
    displayVocabularies();
    if (visibleCountVocabulary >= allVocabularies.length) {
        document.getElementById("loadMoreVocabulary").style.display = "none";
    }
}
