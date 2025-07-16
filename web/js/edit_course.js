document.addEventListener('DOMContentLoaded', function () {
    // --- GLOBAL VARIABLES ---
    let lessonCount = 0;
    let questionCount = 0;
    let activeQuizLessonIndex = null;
    let quizModal = null;

    // --- DOM ELEMENT REFERENCES ---
    const addLessonBtn = document.getElementById("addLessonBtn");
    const tabList = document.getElementById("lessonTabList");
    const tabContent = document.getElementById("lessonTabContent");
    const lessonTemplate = document.getElementById("lessonTemplate");
    const quizQuestionsContainer = document.getElementById("quizQuestionsContainer");
    const quizQuestionTemplate = document.getElementById("quizQuestionTemplate");
    const quizForm = document.getElementById("quizForm");
    const wizardForm = document.getElementById("wizardForm");
    const quizModalEl = document.getElementById('quizModal');
    const courseImageInput = document.getElementById("thumbnailFile"); // Sử dụng id từ JSP
    const thumbnailPreview = document.getElementById("thumbnailPreview");

    // --- CONTEXT PATH ---
    const contextPath = window.contextPath || ''; // Lấy từ JSP: ${pageContext.request.contextPath}

    // --- INITIALIZE QUIZ MODAL ---
    if (quizModalEl) {
        quizModal = new bootstrap.Modal(quizModalEl);
    }

    // --- UPDATE LESSON INDICES ---
    function updateLessonIndices() {
        const allLessonTabs = tabList.querySelectorAll('.nav-item');
        const allLessonPanes = tabContent.querySelectorAll('.tab-pane');
        lessonCount = allLessonTabs.length;

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
                if (h6Title) {
                    h6Title.textContent = `Lesson ${index + 1}`;
                }
            }

            pane.querySelectorAll('[name^="lessons["]').forEach(input => {
                input.name = input.name.replace(/lessons\[\d+\]/, `lessons[${index}]`);
            });
        });
    }

    // --- ADD NEW LESSON ---
    if (addLessonBtn) {
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

            // Xóa lesson
            pane.querySelector(".btn-delete-lesson").addEventListener("click", function () {
                if (confirm("Bạn có chắc muốn xóa lesson này?")) {
                    const lessonPane = this.closest(".tab-pane");
                    const lessonIndex = lessonPane.dataset.lessonIndex;

                    const tabToDelete = document.querySelector(`#tab-${lessonIndex}`);
                    if (tabToDelete) {
                        tabToDelete.parentElement.remove();
                    }

                    lessonPane.remove();
                    updateLessonIndices();

                    if (!tabList.querySelector(".nav-link.active") && tabList.querySelector(".nav-link")) {
                        new bootstrap.Tab(tabList.querySelector(".nav-link")).show();
                    }
                }
            });

            new bootstrap.Tab(document.getElementById(tabId)).show();
            updateLessonIndices();
            attachQuizButtons();
        });
    }

    // --- SHOW QUIZ MODAL ---
    window.showQuizModalForLesson = function (lessonIndex) {
        activeQuizLessonIndex = lessonIndex;
        loadQuizForLesson(lessonIndex);
        if (quizModal) {
            quizModal.show();
        }
    };

    // --- CLOSE QUIZ MODAL ---
    function closeQuizModal() {
        if (quizModal) {
            quizModal.hide();
        }
    }

    // --- LOAD QUIZ FOR LESSON ---
    function loadQuizForLesson(lessonIndex) {
        quizQuestionsContainer.innerHTML = '';
        let questions = [];

        if (typeof allCourseData !== 'undefined' && allCourseData &&
            allCourseData.lessons && allCourseData.lessons[lessonIndex] &&
            allCourseData.lessons[lessonIndex].quizzes) {
            questions = allCourseData.lessons[lessonIndex].quizzes || [];
        }

        questions.forEach((q, idx) => addQuestionBlock(idx, q));
        if (questions.length === 0) {
            addQuestionBlock(0, {});
        }
        updateQuizQuestionIndices();
    }

    // --- ADD QUESTION BLOCK ---
    function addQuestionBlock(index, questionData = {}) {
        let html = quizQuestionTemplate.innerHTML
            .replace(/{{lessonIndex}}/g, activeQuizLessonIndex)
            .replace(/{{questionIndex}}/g, index)
            .replace(/{{questionLabel}}/g, index + 1)
            .replace(/{{index}}/g, `${activeQuizLessonIndex}-${index}`);

        const wrapper = document.createElement("div");
        wrapper.innerHTML = html.trim();
        const block = wrapper.firstElementChild;

        if (questionData.question) {
            block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][question]"]`).value = questionData.question || '';
            block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][optionA]"]`).value = questionData.optionA || '';
            block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][optionB]"]`).value = questionData.optionB || '';
            block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][optionC]"]`).value = questionData.optionC || '';
            block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][optionD]"]`).value = questionData.optionD || '';
            block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][answer]"]`).value = questionData.answer || '';
        }

        const collapse = block.querySelector('.collapse');
        if (collapse) {
            new bootstrap.Collapse(collapse, { toggle: false });
        }

        quizQuestionsContainer.appendChild(block);
    }

    // --- UPDATE QUIZ QUESTION INDICES ---
    function updateQuizQuestionIndices() {
        quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach((block, idx) => {
            let h6 = block.querySelector('h6');
            if (h6) {
                h6.textContent = `Câu hỏi ${idx + 1}`;
            }

            let toggleBtn = block.querySelector('.quiz-collapse-toggle');
            if (toggleBtn) {
                toggleBtn.dataset.bsTarget = `#questionCollapse-${activeQuizLessonIndex}-${idx}`;
                toggleBtn.setAttribute('aria-controls', `questionCollapse-${activeQuizLessonIndex}-${idx}`);
            }

            let collapse = block.querySelector('.collapse');
            if (collapse) {
                collapse.id = `questionCollapse-${activeQuizLessonIndex}-${idx}`;
            }

            block.querySelectorAll('[name^="lessons["]').forEach(input => {
                input.name = input.name.replace(/lessons\[\d+]\[questions]\[\d+]/, `lessons[${activeQuizLessonIndex}][questions][${idx}]`);
            });
        });
    }

    // --- ADD NEW QUIZ QUESTION ---
    if (quizQuestionsContainer && quizQuestionTemplate) {
        const addQuestionBtn = document.getElementById("addQuestionBtn");
        if (addQuestionBtn) {
            addQuestionBtn.addEventListener("click", function () {
                if (activeQuizLessonIndex === null) {
                    alert("Không xác định bài học.");
                    return;
                }
                const idx = quizQuestionsContainer.querySelectorAll('.quiz-question-block').length;
                addQuestionBlock(idx, {});
                updateQuizQuestionIndices();
            });
        }
    }

    // --- TOGGLE ICONS ---
    if (quizQuestionsContainer) {
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
        quizQuestionsContainer.addEventListener("click", function (e) {
            if (e.target.classList.contains("btn-delete-question") || e.target.closest(".btn-delete-question")) {
                const button = e.target.closest(".btn-delete-question");
                const questionBlock = button.closest(".quiz-question-block");

                if (quizQuestionsContainer.querySelectorAll('.quiz-question-block').length <= 1) {
                    alert("Phải có ít nhất 1 câu hỏi!");
                    return;
                }

                if (confirm("Bạn có chắc muốn xóa câu hỏi này?")) {
                    questionBlock.remove();
                    updateQuizQuestionIndices();
                }
            }
        });
    }

    // --- QUIZ FORM SUBMIT ---
    if (quizForm) {
        quizForm.addEventListener("submit", function (e) {
            e.preventDefault();
            if (activeQuizLessonIndex === null) {
                alert("Lỗi: Không xác định được lesson index!");
                return;
            }

            let questionsArr = [];
            let hasError = false;

            quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach((questionBlock, idx) => {
                const question = questionBlock.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${idx}][question]"]`).value.trim();
                const optionA = questionBlock.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${idx}][optionA]"]`).value.trim();
                const optionB = questionBlock.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${idx}][optionB]"]`).value.trim();
                const optionC = questionBlock.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${idx}][optionC]"]`).value.trim();
                const optionD = questionBlock.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${idx}][optionD]"]`).value.trim();
                const answer = questionBlock.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${idx}][answer]"]`).value;

                if (!question || !optionA || !optionB || !optionC || !optionD || !answer) {
                    alert(`Câu hỏi ${idx + 1}: Vui lòng điền đầy đủ thông tin!`);
                    hasError = true;
                    return;
                }

                questionsArr.push({
                    question,
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    answer
                });
            });

            if (hasError) {
                return;
            }

            // Lưu quiz data vào allCourseData
            if (typeof allCourseData === 'undefined') {
                allCourseData = { lessons: [] };
            }
            if (!allCourseData.lessons[activeQuizLessonIndex]) {
                allCourseData.lessons[activeQuizLessonIndex] = {};
            }
            allCourseData.lessons[activeQuizLessonIndex].quizzes = questionsArr;

            // Di chuyển các input quiz từ modal vào lesson-block
            const lessonPane = document.querySelector(`.tab-pane[data-lesson-index='${activeQuizLessonIndex}']`);
            if (lessonPane) {
                let lessonBlock = lessonPane.querySelector('.lesson-block');
                let quizHolder = lessonBlock.querySelector('.quiz-holder');
                if (!quizHolder) {
                    quizHolder = document.createElement('div');
                    quizHolder.className = 'quiz-holder';
                    quizHolder.style.opacity = '0';
                    quizHolder.style.height = '0px';
                    quizHolder.style.overflow = 'hidden';
                    lessonBlock.appendChild(quizHolder);
                }
                Array.from(quizQuestionsContainer.children).forEach(child => {
                    quizHolder.appendChild(child);
                });
            }

            alert('Quiz đã được lưu tạm thời. Hãy click "Cập Nhật Khóa Học" để lưu toàn bộ.');
            closeQuizModal();
        });
    }

    // --- ADD/REMOVE VOCAB ENTRY ---
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

        // Xóa từ vựng hiện có
        if (e.target.classList.contains('btn-delete-vocab')) {
            const lessonIndex = e.target.dataset.lessonIndex;
            const vocabId = e.target.dataset.vocabId;
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = `lessons[${lessonIndex}][deleteVocabulary][]`;
            input.value = vocabId;
            e.target.closest('.lesson-block').appendChild(input);
            e.target.closest('.vocab-entry').remove();
        }
    });

    // --- Xử lý sự kiện mở modal tạo vocabulary ---
    document.querySelectorAll('.btn-generate-vocabulary').forEach(btn => {
        btn.addEventListener('click', function () {
            const lessonIndex = this.dataset.lessonIndex;
            document.getElementById('vocabulary-lesson-id').value = lessonIndex;
        });
    });

    // --- Xử lý form tạo vocabulary ---
    const vocabularyForm = document.getElementById('vocabularyForm');
    if (vocabularyForm) {
        vocabularyForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            const lessonIndex = document.getElementById('vocabulary-lesson-id').value;
            const userInput = document.getElementById('vocabulary-input').value.trim();

            if (!userInput) {
                alert('Vui lòng nhập văn bản!');
                return;
            }

            try {
                // Gửi yêu cầu tới API để tạo vocabulary từ văn bản
                const response = await fetch(`${contextPath}/generate-vocabulary`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ inputText: userInput, lessonId: lessonIndex })
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                if (data.error) {
                    alert('Lỗi: ' + data.error);
                    return;
                }

                // Thêm vocabulary vào giao diện
                const lessonBlock = document.querySelector(`.lesson-block[data-lesson-index="${lessonIndex}"] .vocab-entry-container`);
                let vocabIndex = lessonBlock.querySelectorAll('.input-group').length;
                data.vocabulary.forEach(vocab => {
                    const inputGroup = document.createElement('div');
                    inputGroup.className = 'input-group mb-2';
                    inputGroup.innerHTML = `
                        <input type="text" class="form-control vocab-text" name="lessons[${lessonIndex}][vocabText][${vocabIndex}]" value="${vocab.word}:${vocab.meaning}:${vocab.reading}:${vocab.example}" readonly />
                        <input type="file" class="form-control vocab-image" name="lessons[${lessonIndex}][vocabImage][${vocabIndex}]" accept="image/*" />
                        <button type="button" class="btn btn-outline-success btn-add-vocab ms-2">+</button>
                        <button type="button" class="btn btn-outline-danger btn-remove-vocab ms-1">-</button>
                    `;
                    lessonBlock.appendChild(inputGroup);
                    vocabIndex++;
                });

                // Đóng modal
                const modal = bootstrap.Modal.getInstance(document.getElementById('vocabularyModal'));
                modal.hide();

                // Xóa nội dung input
                document.getElementById('vocabulary-input').value = '';
            } catch (error) {
                console.error('Lỗi khi tạo vocabulary:', error);
                alert('Lỗi: Không thể tạo vocabulary!');
            }
        });
    }

    // --- Kiểm tra định dạng vocabText client-side ---
    document.querySelectorAll('.vocab-text').forEach(input => {
        input.addEventListener('change', function () {
            const value = this.value;
            if (value && !value.match(/^[^:]+:[^:]+:[^:]+:[^:]+$/)) {
                alert('Từ vựng phải có định dạng: Word:Meaning:Reading:Example');
                this.value = '';
            }
        });
    });

    // --- FINAL SUBMIT ---
    if (wizardForm) {
        wizardForm.addEventListener("submit", function (e) {
            const courseTitle = document.getElementById("courseTitle")?.value?.trim();
            if (!courseTitle) {
                alert("Vui lòng nhập tên khóa học!");
                e.preventDefault();
            }
        });
    }

    // --- THUMBNAIL PREVIEW ---
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

    // --- ATTACH QUIZ BUTTONS ---
    function attachQuizButtons() {
        document.querySelectorAll('.btn-toggle-quiz').forEach((btn, idx) => {
            if (!window.lessonIndexToIdMap[idx]) {
                btn.classList.add("disabled");
                btn.title = "Hãy lưu bài học trước khi tạo quiz";
                btn.onclick = null;
            } else {
                btn.classList.remove("disabled");
                btn.title = "";
                btn.onclick = function () {
                    showQuizModalForLesson(idx);
                };
            }
        });
    }

    // --- INITIALIZE ---
    updateLessonIndices();
    attachQuizButtons();
    window.reattachQuizButtons = attachQuizButtons;

    // --- SAVE LESSON ---
    document.addEventListener("click", function (e) {
        if (e.target.classList.contains("btn-save-lesson")) {
            const lessonBlock = e.target.closest(".lesson-block");
            if (lessonBlock) {
                const lessonIndex = lessonBlock.dataset.lessonIndex;
                alert(`Lesson ${parseInt(lessonIndex) + 1} đã được lưu tạm!`);
            }
        }
    });
});