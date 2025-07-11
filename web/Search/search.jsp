<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<link rel="stylesheet" href="css/searchstyle.css">
<script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
<div class="search-sidebar">
    <div class="search-section">
        <h3>Tìm kiếm khóa học</h3>
        <input type="text" class="search-input" id="searchInputCourse" placeholder="Nhập tên khóa học..." onkeyup="searchCourse()">
        <div class="result-box" id="courseResults"></div>
        <button class="btn-load-more" id="loadMoreCourse" onclick="loadMoreCourse()">Xem thêm</button>
    </div>

    <div class="search-section">
        <h3>Tìm kiếm Kanji</h3>
        <input type="text" class="search-input" id="searchInputKanji" placeholder="Nhập Kanji hoặc nghĩa..." onkeyup="searchKanji()">
        <div class="result-box" id="kanjiResults"></div>
        <button class="btn-load-more" id="loadMoreKanji" onclick="loadMoreKanji()">Xem thêm</button>
    </div>

    <div class="search-section">
        <h3>Tìm kiếm từ vựng</h3>
        <input type="text" class="search-input" id="searchInputVocabulary" placeholder="Nhập từ vựng..." onkeyup="searchVocabulary()">
        <div class="result-box" id="vocabularyResults"></div>
        <button class="btn-load-more" id="loadMoreVocabulary" onclick="loadMoreVocabulary()">Xem thêm</button>
    </div>
</div>


<!-- Include JS -->
<script src="js/searchCourse.js"></script>
<script src="js/searchKanji.js"></script>
<script src="js/searchVocabulary.js"></script>
