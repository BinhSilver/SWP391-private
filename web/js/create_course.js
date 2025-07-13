// --- GLOBAL VARIABLES ---
let lessonCount = 1;
let questionCount = 0;
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

// --- UPDATE LESSON INDICES ---
function updateLessonIndices() {
    const allLessonTabs = tabList.querySelectorAll('.nav-item');
    const allLessonPanes = tabContent.querySelectorAll('.tab-pane');
    lessonCount = 0;

    allLessonTabs.forEach((tab, index) => {
        const navLink = tab.querySelector('.nav-link');
        const pane = allLessonPanes[index];

        navLink.id = `tab-${index}`;
        navLink.href = `#lesson-${index}`;
        navLink.textContent = `Lesson ${index + 1}`;

        pane.id = `lesson-${index}`;
        pane.dataset.lessonIndex = index;

        const lessonBlock = pane.querySelector('.lesson-block');
        if (lessonBlock) {
            lessonBlock.dataset.lessonIndex = index;
            const h6Title = lessonBlock.querySelector('h6');
            if (h6Title)
                h6Title.textContent = `Lesson ${index + 1}`;
        }

        pane.querySelectorAll('[name^="lessons["]').forEach(input => {
            input.name = input.name.replace(/lessons\[\d+\]/, `lessons[${index}]`);
        });
    });

    lessonCount = allLessonTabs.length;
}

// --- ADD NEW LESSON ---
addLessonBtn.addEventListener("click", () => {
    const index = lessonCount;
    const tabId = `tab-${index}`;
    const paneId = `lesson-${index}`;

    const tab = document.createElement("li");
    tab.className = "nav-item";
    tab.innerHTML = `<a class="nav-link" id="${tabId}" data-bs-toggle="tab" href="#${paneId}" role="tab">Lesson ${index + 1}</a>`;
    tabList.appendChild(tab);

    const templateHtml = lessonTemplate.innerHTML
            .replace(/{{index}}/g, index)
            .replace(/{{indexLabel}}/g, index + 1);

    const paneWrapper = document.createElement("div");
    paneWrapper.innerHTML = templateHtml.trim();
    const pane = paneWrapper.firstElementChild;
    tabContent.appendChild(pane);

    // Xoá lesson
    pane.querySelector(".btn-delete-lesson").addEventListener("click", function () {
        if (confirm("Bạn có chắc muốn xoá lesson này?")) {
            const lessonPane = this.closest(".tab-pane");
            const lessonIndex = lessonPane.dataset.lessonIndex;

            const tabToDelete = document.querySelector(`#tab-${lessonIndex}`);
            if (tabToDelete)
                tabToDelete.parentElement.remove();

            lessonPane.remove();
            updateLessonIndices();

            if (!tabList.querySelector(".nav-link.active") && tabList.querySelector(".nav-link")) {
                new bootstrap.Tab(tabList.querySelector(".nav-link")).show();
            }
        }
    });

    new bootstrap.Tab(document.getElementById(tabId)).show();
    updateLessonIndices();
});

// --- SET ACTIVE LESSON FOR QUIZ ---
document.addEventListener("click", function (e) {
    const quizBtn = e.target.closest(".btn-toggle-quiz");
    if (quizBtn) {
        const lessonBlock = quizBtn.closest(".lesson-block");
        if (lessonBlock) {
            activeQuizLessonIndex = parseInt(lessonBlock.dataset.lessonIndex);
            questionCount = 0;
            quizQuestionsContainer.innerHTML = "";
            console.log("Tạo quiz cho lesson:", activeQuizLessonIndex);
        }
    }
});

// --- SAVE LESSON ---
document.addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-save-lesson")) {
        const lessonBlock = e.target.closest(".lesson-block");
        if (lessonBlock) {
            const lessonIndex = lessonBlock.dataset.lessonIndex;
            alert(`Lesson ${parseInt(lessonIndex) + 1} đã được lưu tạm!`);
        }
    }
});

// --- ADD NEW QUIZ QUESTION ---
document.getElementById("addQuestionBtn").addEventListener("click", () => {
    if (activeQuizLessonIndex === null) {
        alert("Không xác định bài học.");
        return;
    }

    const lessonIndex = activeQuizLessonIndex;
    const questionIndex = questionCount;

    const templateHtml = quizQuestionTemplate.innerHTML
            .replace(/{{lessonIndex}}/g, lessonIndex)
            .replace(/{{questionIndex}}/g, questionIndex)
            .replace(/{{questionLabel}}/g, questionIndex + 1);

    const questionWrapper = document.createElement("div");
    questionWrapper.innerHTML = templateHtml.trim();
    const newQuestionBlock = questionWrapper.firstElementChild;

    quizQuestionsContainer.appendChild(newQuestionBlock);

    const collapseElement = newQuestionBlock.querySelector('.collapse');
    new bootstrap.Collapse(collapseElement, {toggle: true});

    questionCount++;
});

// --- TOGGLE ICONS ---
quizQuestionsContainer.addEventListener('shown.bs.collapse', function (e) {
    const icon = e.target.previousElementSibling.querySelector('.quiz-collapse-toggle i');
    if (icon) {
        icon.classList.remove('fa-chevron-down');
        icon.classList.add('fa-chevron-up');
    }
});
quizQuestionsContainer.addEventListener('hidden.bs.collapse', function (e) {
    const icon = e.target.previousElementSibling.querySelector('.quiz-collapse-toggle i');
    if (icon) {
        icon.classList.remove('fa-chevron-up');
        icon.classList.add('fa-chevron-down');
    }
});

// --- DELETE QUIZ QUESTION ---
quizQuestionsContainer.addEventListener("click", (e) => {
    if (e.target.classList.contains("btn-delete-question")) {
        if (confirm("Bạn có chắc muốn xoá câu hỏi này?")) {
            e.target.closest(".quiz-question-block").remove();
        }
    }
});

<<<<<<< HEAD
// --- EVENT LISTENER: ADD/REMOVE VOCAB ENTRY ---
document.addEventListener('click', function (e) {
    if (e.target.classList.contains('btn-add-vocab')) {
        const container = e.target.closest('.vocab-entry-container');
        const lessonIndex = container.getAttribute('data-lesson-index');
        const vocabEntries = container.querySelectorAll('.input-group');
        const vocabIndex = vocabEntries.length;
        const newInput = `
            <div class="input-group mb-2">
                <input type="text" class="form-control vocab-text" name="lessons[${lessonIndex}][vocabText][${vocabIndex}]" placeholder="Word:Meaning:Reading:Example" />
                <input type="file" class="form-control vocab-image" name="lessons[${lessonIndex}][vocabImage][${vocabIndex}]" accept="image/*" />
                <button type="button" class="btn btn-outline-success btn-add-vocab ms-2">+</button>
                <button type="button" class="btn btn-outline-danger btn-remove-vocab ms-1">-</button>
            </div>
        `;
        e.target.closest('.input-group').insertAdjacentHTML('afterend', newInput);
    }

    if (e.target.classList.contains('btn-remove-vocab')) {
        const group = e.target.closest('.input-group');
        if (group.parentElement.querySelectorAll('.input-group').length > 1) {
            group.remove();
        }
    }
});

// --- QUIZ FORM SUBMIT ---
=======
// --- SUBMIT QUIZ FORM ---
>>>>>>> origin/nguyn
quizForm.addEventListener('submit', function (e) {
    e.preventDefault();
    alert("Quiz đã được lưu!");
    const quizModal = bootstrap.Modal.getInstance(document.getElementById('quizModal'));
    if (quizModal)
        quizModal.hide();
});

// --- FINAL SUBMIT ---
wizardForm.addEventListener("submit", function (e) {
    const courseTitle = document.getElementById("courseTitle")?.value?.trim();
    if (!courseTitle) {
        alert("Vui lòng nhập tên khóa học!");
        e.preventDefault();
    }
});
// --- THUMBNAIL PREVIEW ---
const courseImageInput = document.getElementById("courseImage");
const thumbnailPreview = document.getElementById("thumbnailPreview");

if (courseImageInput && thumbnailPreview) {
    courseImageInput.addEventListener("change", function (e) {
        const file = e.target.files[0];
        if (file) {
            const url = URL.createObjectURL(file);
            thumbnailPreview.src = url;
            thumbnailPreview.style.display = "block";
        } else {
            thumbnailPreview.src = "#";
            thumbnailPreview.style.display = "none";
        }
    });
}

// --- INITIALIZE ---
updateLessonIndices();
<<<<<<< HEAD
updateQuizQuestionIndices();
=======
>>>>>>> origin/nguyn
