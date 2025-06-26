// --- GLOBAL VARIABLES ---
let lessonCount = 1;
let questionCount = 1;
let quizData = [];
let activeQuizLessonIndex = null;

// --- DOM ELEMENT REFERENCES ---
const addLessonBtn = document.getElementById("addLessonBtn");
const tabList = document.getElementById("lessonTabList");
const tabContent = document.getElementById("lessonTabContent");
const lessonTemplate = document.getElementById("lessonTemplate");
const quizQuestionsContainer = document.getElementById("quizQuestionsContainer");
const quizQuestionTemplate = document.getElementById("quizQuestionTemplate");
const quizForm = document.getElementById("quizForm");
const wizardForm = document.getElementById("wizardForm");

// --- FUNCTION TO UPDATE LESSON INDICES AFTER ADD/DELETE ---
function updateLessonIndices() {
    const allLessonTabs = tabList.querySelectorAll('.nav-item');
    const allLessonPanes = tabContent.querySelectorAll('.tab-pane');
    lessonCount = 0;

    allLessonTabs.forEach((tab, index) => {
        const navLink = tab.querySelector('.nav-link');
        const pane = allLessonPanes[index];
        const newIndex = index;

        navLink.id = `tab-${newIndex}`;
        navLink.href = `#lesson-${newIndex}`;
        navLink.textContent = `Lesson ${newIndex + 1}`;

        pane.id = `lesson-${newIndex}`;
        pane.dataset.lessonIndex = newIndex; // cập nhật lại index cho pane

        // Cập nhật cho lesson-block bên trong
        const lessonBlock = pane.querySelector('.lesson-block');
        if (lessonBlock) {
            lessonBlock.dataset.lessonIndex = newIndex;
            const h6Title = lessonBlock.querySelector('h6');
            if (h6Title) h6Title.textContent = `Lesson ${newIndex + 1}`;
        }

        // Update all fields name
        pane.querySelectorAll('[name^="lessons["]').forEach(input => {
            input.name = input.name.replace(/lessons\[\d+\]/, `lessons[${newIndex}]`);
        });
    });

    lessonCount = allLessonTabs.length;
}

// --- FUNCTION TO UPDATE QUIZ QUESTION INDICES AFTER ADD/DELETE ---
function updateQuizQuestionIndices() {
    const allQuestionBlocks = quizQuestionsContainer.querySelectorAll('.quiz-question-block');
    questionCount = 0;

    allQuestionBlocks.forEach((block, index) => {
        const newIndex = index;
        const h6Title = block.querySelector('h6');
        if (h6Title)
            h6Title.textContent = `Câu hỏi ${newIndex + 1}`;

        const toggleButton = block.querySelector('.quiz-collapse-toggle');
        if (toggleButton) {
            toggleButton.dataset.bsTarget = `#questionCollapse-${newIndex}`;
            toggleButton.setAttribute('aria-controls', `questionCollapse-${newIndex}`);
        }

        const collapseContent = block.querySelector('.collapse');
        if (collapseContent)
            collapseContent.id = `questionCollapse-${newIndex}`;

        block.querySelectorAll('[name^="questions["]').forEach(input => {
            input.name = input.name.replace(/questions\[\d+\]/, `questions[${newIndex}]`);
        });

        questionCount++;
    });

    questionCount = allQuestionBlocks.length;
}

// --- EVENT LISTENER: ADD NEW LESSON ---
addLessonBtn.addEventListener("click", () => {
    const index = lessonCount;
    const tabId = `tab-${index}`;
    const paneId = `lesson-${index}`;

    // Tạo tab
    const tab = document.createElement("li");
    tab.className = "nav-item";
    tab.innerHTML = `<a class="nav-link" id="${tabId}" data-bs-toggle="tab" href="#${paneId}" role="tab">Lesson ${index + 1}</a>`;
    tabList.appendChild(tab);

    // Tạo nội dung lesson từ template
    const templateHtml = lessonTemplate.innerHTML
            .trim()
            .replace(/{{index}}/g, index)
            .replace(/{{indexLabel}}/g, index + 1);

    const paneWrapper = document.createElement("div");
    paneWrapper.innerHTML = templateHtml;
    const pane = paneWrapper.querySelector(".tab-pane");
    tabContent.appendChild(pane);

    // Gắn sự kiện xoá lesson
    pane.querySelector(".btn-delete-lesson").addEventListener("click", function () {
        if (confirm("Bạn có chắc muốn xoá lesson này?")) {
            const lessonPane = this.closest(".tab-pane");
            const lessonIndex = lessonPane.dataset.lessonIndex;

            // Xóa tab
            const tabToDelete = document.querySelector(`#tab-${lessonIndex}`);
            if (tabToDelete)
                tabToDelete.parentElement.remove();

            // Xóa lesson content
            lessonPane.remove();

            // Cập nhật lại index
            updateLessonIndices();

            // Nếu không còn tab nào active, active tab đầu tiên nếu có
            const activeTab = tabList.querySelector(".nav-link.active");
            if (!activeTab && tabList.querySelector(".nav-link")) {
                new bootstrap.Tab(tabList.querySelector(".nav-link")).show();
            }
        }
    });

    // Hiển thị tab vừa thêm
    new bootstrap.Tab(document.getElementById(tabId)).show();

    // Cập nhật chỉ mục sau khi thêm
    updateLessonIndices();
});

// --- EVENT LISTENER: GÁN lessonIndex cho quiz ---
document.addEventListener("click", function (e) {
    const quizBtn = e.target.closest(".btn-toggle-quiz");
    if (quizBtn) {
        const lessonBlock = quizBtn.closest(".lesson-block");
        if (lessonBlock) {
            activeQuizLessonIndex = parseInt(lessonBlock.dataset.lessonIndex);
            console.log("Tạo quiz cho lesson:", activeQuizLessonIndex);
        }
    }
});

// --- EVENT LISTENER: SAVE LESSON ---
document.addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-save-lesson")) {
        const lessonBlock = e.target.closest(".lesson-block");
        if (lessonBlock) {
            const lessonIndex = lessonBlock.dataset.lessonIndex;
            alert(`Lesson ${parseInt(lessonIndex) + 1} đã được lưu tạm!`);
        }
    }
});

// --- EVENT LISTENER: ADD NEW QUIZ QUESTION ---
document.getElementById("addQuestionBtn").addEventListener("click", () => {
    document.querySelectorAll('.quiz-question-block .collapse.show').forEach(collapseElement => {
        const bsCollapse = bootstrap.Collapse.getInstance(collapseElement) || new bootstrap.Collapse(collapseElement, {toggle: false});
        bsCollapse.hide();
    });

    const index = questionCount;
    const templateHtml = quizQuestionTemplate.innerHTML
            .replace(/{{index}}/g, index)
            .replace(/{{questionLabel}}/g, index + 1);

    const questionWrapper = document.createElement("div");
    questionWrapper.innerHTML = templateHtml.trim();
    const newQuestionBlock = questionWrapper.firstElementChild;

    quizQuestionsContainer.appendChild(newQuestionBlock);

    const newCollapseElement = newQuestionBlock.querySelector('.collapse');
    new bootstrap.Collapse(newCollapseElement, {toggle: true});

    updateQuizQuestionIndices();
});

// --- EVENT LISTENER: TOGGLE ICON ---
quizQuestionsContainer.addEventListener('shown.bs.collapse', function (e) {
    const toggleButton = e.target.previousElementSibling.querySelector('.quiz-collapse-toggle i');
    if (toggleButton) {
        toggleButton.classList.remove('fa-chevron-down');
        toggleButton.classList.add('fa-chevron-up');
    }
});
quizQuestionsContainer.addEventListener('hidden.bs.collapse', function (e) {
    const toggleButton = e.target.previousElementSibling.querySelector('.quiz-collapse-toggle i');
    if (toggleButton) {
        toggleButton.classList.remove('fa-chevron-up');
        toggleButton.classList.add('fa-chevron-down');
    }
});

// --- DELETE QUIZ QUESTION ---
quizQuestionsContainer.addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-delete-question")) {
        if (confirm("Bạn có chắc muốn xoá câu hỏi này?")) {
            e.target.closest(".quiz-question-block").remove();
            updateQuizQuestionIndices();
        }
    }
});

// --- QUIZ FORM SUBMIT ---
quizForm.addEventListener('submit', function (e) {
    e.preventDefault();
    quizData = [];
    quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach((questionBlock, index) => {
        const question = questionBlock.querySelector(`[name="questions[${index}][question]"]`).value;
        const optionA = questionBlock.querySelector(`[name="questions[${index}][optionA]"]`).value;
        const optionB = questionBlock.querySelector(`[name="questions[${index}][optionB]"]`).value;
        const optionC = questionBlock.querySelector(`[name="questions[${index}][optionC]"]`).value;
        const optionD = questionBlock.querySelector(`[name="questions[${index}][optionD]"]`).value;
        const answer = questionBlock.querySelector(`[name="questions[${index}][answer]"]`).value;

        quizData.push({
            lessonIndex: activeQuizLessonIndex,
            question,
            optionA,
            optionB,
            optionC,
            optionD,
            answer
        });
    });

    console.log("Quiz Data Submitted:", quizData);
    alert("Quiz đã được lưu!");

    const quizModal = bootstrap.Modal.getInstance(document.getElementById('quizModal'));
    if (quizModal)
        quizModal.hide();
});

// --- MAIN FORM SUBMIT (THÔNG TIN CHUNG + QUIZ) ---
wizardForm.addEventListener("submit", function (e) {
    const courseTitle = document.getElementById("courseTitle")?.value?.trim();
    if (!courseTitle) {
        alert("Vui lòng nhập tên khóa học!");
        e.preventDefault();
        return;
    }

    const hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name = "quizJson";
    hiddenInput.value = JSON.stringify(quizData || []);
    wizardForm.appendChild(hiddenInput);
});

// --- INITIALIZE ---
updateLessonIndices();
updateQuizQuestionIndices();
