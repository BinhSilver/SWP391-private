document.addEventListener('DOMContentLoaded', function () {
    // --- GLOBAL VARIABLES ---
    let lessonCount = 1; // Bắt đầu với 1 lesson mặc định
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
    const courseImageInput = document.getElementById("thumbnailFile");
    const thumbnailPreview = document.getElementById("thumbnailPreview");

    // --- CONTEXT PATH ---
    const contextPath = window.contextPath || '/SWP_HUY'; // Lấy từ JSP: ${pageContext.request.contextPath}

    // --- INITIALIZE QUIZ MODAL ---
    if (quizModalEl) {
        quizModal = new bootstrap.Modal(quizModalEl);
    }

    // --- INITIALIZE allCourseData ---
    if (typeof allCourseData === 'undefined') {
        allCourseData = { lessons: [] };
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

    // --- Gán lại sự kiện xóa cho tất cả lesson (kể cả lesson gốc) ---
    function attachDeleteLessonEvents() {
        document.querySelectorAll('.btn-delete-lesson').forEach(btn => {
            btn.onclick = function () {
                if (tabList.querySelectorAll('.nav-item').length <= 1) {
                    alert("Phải có ít nhất 1 lesson!");
                    return;
                }
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
            }
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

            updateLessonIndices();
            attachDeleteLessonEvents();
            new bootstrap.Tab(document.getElementById(tabId)).show();
        });
    }

    // --- SET ACTIVE LESSON FOR QUIZ ---
    document.addEventListener("click", function (e) {
        const quizBtn = e.target.closest(".btn-toggle-quiz");
        if (quizBtn) {
            const lessonBlock = quizBtn.closest(".lesson-block");
            if (lessonBlock) {
                activeQuizLessonIndex = parseInt(lessonBlock.dataset.lessonIndex);
                questionCount = 0;
                quizQuestionsContainer.innerHTML = "";
                // Nếu lesson đã có quiz-holder, chuyển các input quiz vào modal để chỉnh sửa
                let quizHolder = lessonBlock.querySelector('.quiz-holder');
                if (quizHolder && quizHolder.children.length > 0) {
                    // Di chuyển từng quiz-question-block vào modal
                    Array.from(quizHolder.children).forEach(child => {
                        quizQuestionsContainer.appendChild(child);
                    });
                    questionCount = quizQuestionsContainer.querySelectorAll('.quiz-question-block').length;
                }
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

        // Lấy giá trị thời gian mặc định từ input
        const defaultTimeLimit = parseInt(document.getElementById('quizTimeLimitInput')?.value || '60', 10);

        const templateHtml = quizQuestionTemplate.innerHTML
                .replace(/{{lessonIndex}}/g, lessonIndex)
                .replace(/{{questionIndex}}/g, questionIndex)
                .replace(/{{questionLabel}}/g, questionIndex + 1)
                + `<input type="hidden" class="quiz-time-limit" name="lessons[${lessonIndex}][questions][${questionIndex}][timeLimit]" value="${defaultTimeLimit}" />`;

        const questionWrapper = document.createElement("div");
        questionWrapper.innerHTML = templateHtml.trim();
        const newQuestionBlock = questionWrapper.firstElementChild;

        // Đảm bảo có input timeLimit ẩn
        let timeInput = newQuestionBlock.querySelector('.quiz-time-limit');
        if (!timeInput) {
            timeInput = document.createElement('input');
            timeInput.type = 'hidden';
            timeInput.className = 'quiz-time-limit';
            timeInput.name = `lessons[${lessonIndex}][questions][${questionIndex}][timeLimit]`;
            timeInput.value = defaultTimeLimit;
            newQuestionBlock.appendChild(timeInput);
        }

        quizQuestionsContainer.appendChild(newQuestionBlock);

        const collapseElement = newQuestionBlock.querySelector('.collapse');
        new bootstrap.Collapse(collapseElement, {toggle: true});

        questionCount++;
    });

    // --- SET QUIZ TIME FOR ALL QUESTIONS ---
    document.getElementById('setQuizTimeBtn').addEventListener('click', function() {
        const time = parseInt(document.getElementById('quizTimeLimitInput').value, 10);
        if (isNaN(time) || time < 1) {
            alert('Vui lòng nhập thời gian hợp lệ!');
            return;
        }
        // Set cho tất cả input timeLimit của các câu hỏi hiện tại
        document.querySelectorAll('#quizQuestionsContainer .quiz-time-limit').forEach(input => {
            input.value = time;
        });
        alert('Đã áp dụng thời gian cho tất cả câu hỏi!');
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

    // --- Xử lý sự kiện mở modal tạo vocabulary ---
    document.querySelectorAll('.btn-generate-vocabulary').forEach(btn => {
        btn.addEventListener('click', function () {
            const lessonIndex = this.dataset.lessonIndex;
            document.getElementById('vocabulary-lesson-id').value = lessonIndex;
        });
    });

    // --- Xử lý form tạo vocabulary (phiên bản cải tiến) ---
    const vocabularyForm = document.getElementById('vocabularyForm');
    if (vocabularyForm) {
        vocabularyForm.addEventListener('submit', async function (e) {
            e.preventDefault();
            const lessonIndex = document.getElementById('vocabulary-lesson-id')?.value;
            const userInput = document.getElementById('vocabulary-input')?.value.trim();

            // Kiểm tra các phần tử DOM cần thiết
            if (!lessonIndex || !userInput) {
                console.warn('Thiếu lessonIndex hoặc userInput:', { lessonIndex, userInput });
                alert('Vui lòng nhập đầy đủ văn bản và chọn bài học!');
                return;
            }

            // Kiểm tra contextPath
            console.log('Gửi yêu cầu tới:', `${contextPath}/generate-vocabulary`);

            try {
                const response = await fetch(`${contextPath}/generate-vocabulary`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ inputText: userInput, lessonId: lessonIndex })
                });

                console.log('Phản hồi từ server:', {
                    status: response.status,
                    ok: response.ok,
                    redirected: response.redirected,
                    url: response.url
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
                }

                const data = await response.json();
                if (data.error) {
                    console.error('Lỗi từ server:', data.error);
                    alert('Lỗi: ' + data.error);
                    return;
                }

                // Thêm vocabulary vào giao diện
                const lessonBlock = document.querySelector(`.lesson-block[data-lesson-index="${lessonIndex}"] .vocab-entry-container`);
                if (!lessonBlock) {
                    console.error('Không tìm thấy lessonBlock cho lessonIndex:', lessonIndex);
                    alert('Lỗi: Không tìm thấy container từ vựng!');
                    return;
                }

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
                if (!modal) {
                    console.error('Không tìm thấy modal vocabularyModal');
                    alert('Lỗi: Không thể đóng modal từ vựng!');
                    return;
                }
                modal.hide();

                // Xóa nội dung input
                document.getElementById('vocabulary-input').value = '';
            } catch (error) {
                console.error('Lỗi khi tạo vocabulary:', error, {
                    lessonIndex,
                    userInput,
                    contextPath
                });
                alert('Lỗi: Không thể tạo vocabulary! Chi tiết lỗi: ' + error.message);
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

    // --- QUIZ FORM SUBMIT ---
    quizForm.addEventListener('submit', function (e) {
        // Mở tất cả các collapse để trình duyệt validate
        document.querySelectorAll('#quizQuestionsContainer .collapse').forEach(collapse => {
            collapse.classList.add('show');
        });
        e.preventDefault();
        if (activeQuizLessonIndex === null) {
            alert("Không xác định bài học để lưu quiz!");
            return;
        }
        // Di chuyển các input quiz từ modal vào lesson-block tương ứng (và nằm trong form chính)
        const lessonPane = document.querySelector(`.tab-pane[data-lesson-index='${activeQuizLessonIndex}']`);
        if (lessonPane) {
            let lessonBlock = lessonPane.querySelector('.lesson-block');
            let quizHolder = lessonBlock.querySelector('.quiz-holder');
            if (!quizHolder) {
                quizHolder = document.createElement('div');
                quizHolder.className = 'quiz-holder';
                // Không dùng display: none để tránh lỗi focus, chỉ ẩn bằng opacity và height
                quizHolder.style.opacity = '0';
                quizHolder.style.height = '0px';
                quizHolder.style.overflow = 'hidden';
                lessonBlock.appendChild(quizHolder);
            }
            // Di chuyển từng quiz-question-block vào quizHolder
            Array.from(quizQuestionsContainer.children).forEach(child => {
                quizHolder.appendChild(child);
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

            if (allCourseData.lessons && allCourseData.lessons[lessonIndex] && allCourseData.lessons[lessonIndex].quizzes) {
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

            // Đảm bảo id collapse và các thuộc tính toggle là duy nhất
            const collapse = block.querySelector('.collapse');
            const toggleBtn = block.querySelector('.quiz-collapse-toggle');
            const collapseId = `questionCollapse-${activeQuizLessonIndex}-${index}`;
            if (collapse) {
                collapse.id = collapseId;
            }
            if (toggleBtn) {
                toggleBtn.setAttribute('data-bs-target', `#${collapseId}`);
                toggleBtn.setAttribute('aria-controls', collapseId);
            }
            // Khởi tạo lại Bootstrap Collapse cho phần tử mới
            if (collapse) {
                new bootstrap.Collapse(collapse, { toggle: false });
            }

            // Populate data if available
            if (questionData.question) {
                block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][question]"]`).value = questionData.question || '';
                block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][optionA]"]`).value = questionData.optionA || '';
                block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][optionB]"]`).value = questionData.optionB || '';
                block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][optionC]"]`).value = questionData.optionC || '';
                block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][optionD]"]`).value = questionData.optionD || '';
                block.querySelector(`[name="lessons[${activeQuizLessonIndex}][questions][${index}][answer]"]`).value = questionData.answer || '';
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
                let collapse = block.querySelector('.collapse');
                const collapseId = `questionCollapse-${activeQuizLessonIndex}-${idx}`;
                if (toggleBtn) {
                    toggleBtn.setAttribute('data-bs-target', `#${collapseId}`);
                    toggleBtn.setAttribute('aria-controls', collapseId);
                }
                if (collapse) {
                    collapse.id = collapseId;
                }
                block.querySelectorAll('[name^="lessons["]').forEach(input => {
                    input.name = input.name.replace(/lessons\[\d+\]\[questions\]\[\d+\]/, `lessons[${activeQuizLessonIndex}][questions][${idx}]`);
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

                alert('Quiz đã được lưu tạm thời. Hãy click "Hoàn tất & Tạo Khóa Học" để lưu toàn bộ.');
                closeQuizModal();
            });
        }

        // --- FINAL SUBMIT ---
        if (wizardForm) {
            wizardForm.addEventListener("submit", function (e) {
                // Serialize toàn bộ quiz của các lesson vào input ẩn quizJson
                if (typeof allCourseData !== 'undefined' && allCourseData.lessons) {
                    const quizJsonInput = document.createElement('input');
                    quizJsonInput.type = 'hidden';
                    quizJsonInput.name = 'quizJson';
                    quizJsonInput.value = JSON.stringify(allCourseData.lessons.map(l => l?.quizzes || []));
                    wizardForm.appendChild(quizJsonInput);
                }
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
                btn.classList.remove("disabled");
                btn.title = "";
                btn.onclick = function () {
                    showQuizModalForLesson(idx);
                };
            });
        }

        // --- XÓA LESSON ---
        document.addEventListener('click', function (e) {
            if (e.target.classList.contains('btn-delete-lesson')) {
                const lessonBlock = e.target.closest('.lesson-block');
                const lessonPane = lessonBlock.closest('.tab-pane');
                const lessonIndex = lessonBlock.getAttribute('data-lesson-index');
                // Nếu lesson đã có id (lesson cũ), thêm vào lessonsToDelete
                const lessonIdInput = lessonBlock.querySelector('input[name^="lessons[' + lessonIndex + '][id]"]');
                if (lessonIdInput && lessonIdInput.value) {
                    let lessonsToDeleteInput = document.querySelector('input[name="lessonsToDelete"]');
                    if (!lessonsToDeleteInput) {
                        lessonsToDeleteInput = document.createElement('input');
                        lessonsToDeleteInput.type = 'hidden';
                        lessonsToDeleteInput.name = 'lessonsToDelete';
                        lessonsToDeleteInput.value = '';
                        document.getElementById('wizardForm').appendChild(lessonsToDeleteInput);
                    }
                    lessonsToDeleteInput.value += (lessonsToDeleteInput.value ? ',' : '') + lessonIdInput.value;
                }
                // Xóa tab và pane
                const tabId = 'tab-' + lessonIndex;
                const tab = document.getElementById(tabId);
                if (tab) tab.parentElement.remove();
                lessonPane.remove();
                updateLessonIndices();
            }
        });

        // --- INITIALIZE ---
        updateLessonIndices();
        attachQuizButtons();
        window.reattachQuizButtons = attachQuizButtons;
        attachDeleteLessonEvents(); // Gán lại sự kiện xóa cho lesson gốc khi load trang
    });
});

