let visibleCountKanji = 2;
let allKanjis = [];

function searchKanji() {
    const query = document.getElementById("searchInputKanji").value.trim();
    const resultContainer = document.getElementById("kanjiResults");
    const loadMoreButton = document.getElementById("loadMoreKanji");

    if (query === "") {
        resultContainer.innerHTML = "";
        loadMoreButton.style.display = "none";
        return;
    }

    fetch(`SearchKanji?query=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(data => {
            allKanjis = data;
            visibleCountKanji = 2;
            resultContainer.innerHTML = "";

            if (allKanjis.length === 0) {
                resultContainer.innerHTML = "<p>Không tìm thấy Kanji.</p>";
                loadMoreButton.style.display = "none";
            } else {
                displayKanjis();
                loadMoreButton.style.display = allKanjis.length > visibleCountKanji ? "block" : "none";
            }
        });
}

function displayKanjis() {
    const container = document.getElementById("kanjiResults");
    container.innerHTML = "";

    allKanjis.slice(0, visibleCountKanji).forEach(kanji => {
        const div = document.createElement("div");
        div.classList.add("search-item");
        div.innerHTML = `
            <h4>${kanji.character} - ${kanji.meaning}</h4>
            <p><b>Onyomi:</b> ${kanji.onyomi}</p>
            <p><b>Kunyomi:</b> ${kanji.kunyomi}</p>
        `;
        container.appendChild(div);
    });
}

function loadMoreKanji() {
    visibleCountKanji += 2;
    displayKanjis();
    if (visibleCountKanji >= allKanjis.length) {
        document.getElementById("loadMoreKanji").style.display = "none";
    }
}
