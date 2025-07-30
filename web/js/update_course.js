document.addEventListener('DOMContentLoaded', function () {
    let lessonIndexCounter = 0;
    let quizQuestionIndexCounter = 0;

    const lessonTabList = document.getElementById('lessonTabList');
    const lessonTabContent = document.getElementById('lessonTabContent');
    const addLessonBtn = document.getElementById('addLessonBtn');
    const quizModal = document.getElementById('quizModal');
    const quizQuestionsContainer = document.getElementById('quizQuestionsContainer');
    const addQuestionBtn = document.getElementById('addQuestionBtn');
    const quizForm = document.getElementById('quizForm');
    const currentLessonQuizIndexInput = document.getElementById('currentLessonQuizIndex');

    function updateLessonIndices() {
        const lessonBlocks = lessonTabContent.querySelectorAll('.lesson-block');
        const newLessonsArr = [];

        lessonBlocks.forEach((block, newIndex) => {
            const oldIndex = block.getAttribute('data-lesson-index');

            block.setAttribute('data-lesson-index', newIndex);

            block.querySelectorAll('[name^="lessons["]').forEach(input => {
                const oldName = input.getAttribute('name');
                const newName = oldName.replace(`lessons[${oldIndex}]`, `lessons[${newIndex}]`);
                input.setAttribute('name', newName);
            });

            const tabLink = document.getElementById(`tab-${oldIndex}`);
            if (tabLink) {
                tabLink.id = `tab-${newIndex}`;
                tabLink.setAttribute('href', `#lesson-${newIndex}`);
                tabLink.textContent = `Lesson ${newIndex + 1}`;
            }

            const tabPane = document.getElementById(`lesson-${oldIndex}`);
            if (tabPane) {
                tabPane.id = `lesson-${newIndex}`;
            }

            block.querySelector('h6').textContent = `Lesson ${newIndex + 1}`;

            const quizButton = block.querySelector('.btn-toggle-quiz');
            if (quizButton) {
                quizButton.setAttribute('data-lesson-index', newIndex);
            }

            // Map old data to new index
            if (typeof allCourseData !== 'undefined' && allCourseData.lessons && allCourseData.lessons[oldIndex]) {
                newLessonsArr[newIndex] = allCourseData.lessons[oldIndex];
            }
        });

        if (typeof allCourseData !== 'undefined') {
            allCourseData.lessons = newLessonsArr;
        }

        lessonIndexCounter = lessonBlocks.length;
    }

    function updateQuestionIndices() {
        const questionBlocks = quizQuestionsContainer.querySelectorAll('.quiz-question-block');
        questionBlocks.forEach((block, newIndex) => {
            const oldIndex = block.getAttribute('data-question-index') || newIndex;
            block.setAttribute('data-question-index', newIndex);

            block.querySelectorAll('[name^="lessons["]').forEach(input => {
                const oldName = input.getAttribute('name');
                const newName = oldName.replace(/questions\[\d+\]/, `questions[${newIndex}]`);
                input.setAttribute('name', newName);
            });

            const collapseToggle = block.querySelector('.quiz-collapse-toggle');
            if (collapseToggle) {
                collapseToggle.setAttribute('data-bs-target', `#questionCollapse-${newIndex}`);
                collapseToggle.setAttribute('aria-controls', `questionCollapse-${newIndex}`);
            }
            const collapseDiv = block.querySelector('.collapse');
            if (collapseDiv) {
                collapseDiv.id = `questionCollapse-${newIndex}`;
            }

            block.querySelector('h6').textContent = `Câu hỏi ${newIndex + 1}`;
        });

        quizQuestionIndexCounter = questionBlocks.length;
    }

    function addLesson() {
        const newIndex = lessonIndexCounter++;
        let templateHtml = document.getElementById('lessonTemplate').innerHTML;
        templateHtml = templateHtml
                .replace(/{{index}}/g, newIndex)
                .replace(/{{indexLabel}}/g, newIndex + 1)
                .replace(/{{lessonIndex}}/g, newIndex);

        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = templateHtml;
        const newLessonPane = tempDiv.firstElementChild;
        lessonTabContent.appendChild(newLessonPane);

        const newTabItem = document.createElement('li');
        newTabItem.classList.add('nav-item');
        newTabItem.innerHTML = `<a class="nav-link" id="tab-${newIndex}" data-bs-toggle="tab" href="#lesson-${newIndex}" role="tab">Lesson ${newIndex + 1}</a>`;
        lessonTabList.appendChild(newTabItem);

        const newTab = new bootstrap.Tab(newTabItem.querySelector('.nav-link'));
        newTab.show();

        // Thêm lesson mới vào allCourseData
        if (typeof allCourseData === 'undefined') {
            allCourseData = {lessons: []};
        }
        if (!allCourseData.lessons[newIndex]) {
            allCourseData.lessons[newIndex] = {
                id: 0,
                name: "",
                description: "",
                quizzes: []
            };
        }

        updateLessonIndices();
    }

    function addQuestion() {
        const currentLessonIndex = currentLessonQuizIndexInput.value;
        if (currentLessonIndex === '' || currentLessonIndex === undefined || isNaN(currentLessonIndex)) {
            alert("Không xác định bài học.");
            return;
        }
        const newIndex = quizQuestionIndexCounter++;
        let templateHtml = document.getElementById('quizQuestionTemplate').innerHTML;
        templateHtml = templateHtml
                .replace(/{{lessonIndex}}/g, currentLessonIndex)
                .replace(/{{questionIndex}}/g, newIndex)
                .replace(/{{questionLabel}}/g, newIndex + 1)
                .replace(/{{index}}/g, newIndex)
                + `<input type="hidden" class="quiz-time-limit" name="lessons[${currentLessonIndex}][questions][${newIndex}][timeLimit]" value="${document.getElementById('quizTimeLimitInput')?.value || 60}" />`;
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = templateHtml;
        const newQuestionBlock = tempDiv.firstElementChild;
        newQuestionBlock.setAttribute('data-question-index', newIndex);
        quizQuestionsContainer.appendChild(newQuestionBlock);
        updateQuestionIndices();
        // Focus input đầu tiên
        setTimeout(() => {
            const firstInput = newQuestionBlock.querySelector('input[name*="[question]"]');
            if (firstInput) firstInput.focus();
        }, 100);
        // Scroll đến câu hỏi mới
        newQuestionBlock.scrollIntoView({behavior: 'smooth', block: 'center'});
    }

    function addQuestionBlock(index, questionData = {}) {
        let html = document.getElementById('quizQuestionTemplate').innerHTML
            .replace(/{{lessonIndex}}/g, currentLessonQuizIndexInput.value)
            .replace(/{{questionIndex}}/g, index)
            .replace(/{{questionLabel}}/g, index + 1)
            .replace(/{{index}}/g, `${currentLessonQuizIndexInput.value}-${index}`);

        const wrapper = document.createElement("div");
        wrapper.innerHTML = html.trim();
        const block = wrapper.firstElementChild;

        // Đảm bảo id collapse và các thuộc tính toggle là duy nhất
        const collapse = block.querySelector('.collapse');
        const toggleBtn = block.querySelector('.quiz-collapse-toggle');
        const collapseId = `questionCollapse-${currentLessonQuizIndexInput.value}-${index}`;
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
        if (questionData) {
            block.querySelector(`[name="lessons[${currentLessonQuizIndexInput.value}][questions][${index}][question]"]`).value = questionData.question || '';
            block.querySelector(`[name="lessons[${currentLessonQuizIndexInput.value}][questions][${index}][optionA]"]`).value = questionData.optionA || '';
            block.querySelector(`[name="lessons[${currentLessonQuizIndexInput.value}][questions][${index}][optionB]"]`).value = questionData.optionB || '';
            block.querySelector(`[name="lessons[${currentLessonQuizIndexInput.value}][questions][${index}][optionC]"]`).value = questionData.optionC || '';
            block.querySelector(`[name="lessons[${currentLessonQuizIndexInput.value}][questions][${index}][optionD]"]`).value = questionData.optionD || '';
            block.querySelector(`[name="lessons[${currentLessonQuizIndexInput.value}][questions][${index}][answer]"]`).value = questionData.answer || '';
        }

        quizQuestionsContainer.appendChild(block);
    }

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

    // Khởi tạo lessonIndexCounter dựa trên số lesson hiện có
    const existingLessons = document.querySelectorAll('.lesson-block');
    if (existingLessons.length > 0) {
        lessonIndexCounter = existingLessons.length;
    } else if (typeof allCourseData !== 'undefined' && allCourseData && allCourseData.lessons && allCourseData.lessons.length > 0) {
        lessonIndexCounter = allCourseData.lessons.length;
    } else {
        lessonIndexCounter = 0;
    }

    addLessonBtn.addEventListener('click', addLesson);

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
                    document.getElementById('updateCourseForm').appendChild(lessonsToDeleteInput);
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

    lessonTabContent.addEventListener('click', function (event) {
        if (event.target.classList.contains('btn-toggle-quiz') || event.target.closest('.btn-toggle-quiz')) {
            const quizButton = event.target.closest('.btn-toggle-quiz');
            const lessonIndex = parseInt(quizButton.getAttribute('data-lesson-index'));
            currentLessonQuizIndexInput.value = lessonIndex;
            quizQuestionsContainer.innerHTML = '';
            // Lấy dữ liệu quiz cũ nếu có
            const lessonData = typeof allCourseData !== 'undefined' && allCourseData && allCourseData.lessons ? allCourseData.lessons[lessonIndex] : null;
            let quizArr = [];
            if (lessonData && lessonData.quizzes && Array.isArray(lessonData.quizzes) && lessonData.quizzes.length > 0) {
                quizArr = lessonData.quizzes;
            }
            // Nếu không có quiz thì tạo 1 block trống
            if (quizArr.length === 0) {
                addQuestion();
                quizQuestionIndexCounter = 1;
            } else {
                quizArr.forEach((q, qIndex) => {
                    let templateHtml = document.getElementById('quizQuestionTemplate').innerHTML;
                    templateHtml = templateHtml
                        .replace(/{{lessonIndex}}/g, lessonIndex)
                        .replace(/{{questionIndex}}/g, qIndex)
                        .replace(/{{questionLabel}}/g, qIndex + 1)
                        .replace(/{{index}}/g, qIndex)
                        + `<input type="hidden" class="quiz-time-limit" name="lessons[${lessonIndex}][questions][${qIndex}][timeLimit]" value="${q.timeLimit || 60}" />`;
                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = templateHtml;
                    const newQuestionBlock = tempDiv.firstElementChild;
                    newQuestionBlock.setAttribute('data-question-index', qIndex);
                    // Gán value cho input
                    const questionInput = newQuestionBlock.querySelector(`[name="lessons[${lessonIndex}][questions][${qIndex}][question]"]`);
                    if (questionInput) questionInput.value = q.question || '';
                    newQuestionBlock.querySelector(`[name="lessons[${lessonIndex}][questions][${qIndex}][optionA]"]`).value = q.optionA || '';
                    newQuestionBlock.querySelector(`[name="lessons[${lessonIndex}][questions][${qIndex}][optionB]"]`).value = q.optionB || '';
                    newQuestionBlock.querySelector(`[name="lessons[${lessonIndex}][questions][${qIndex}][optionC]"]`).value = q.optionC || '';
                    newQuestionBlock.querySelector(`[name="lessons[${lessonIndex}][questions][${qIndex}][optionD]"]`).value = q.optionD || '';
                    newQuestionBlock.querySelector(`[name="lessons[${lessonIndex}][questions][${qIndex}][answer]"]`).value = q.answer || '';
                    quizQuestionsContainer.appendChild(newQuestionBlock);
                });
                quizQuestionIndexCounter = quizArr.length;
            }
            updateQuestionIndices();
            document.getElementById('quizModalLabel').textContent = 'Sửa Quiz';
            const modalInstance = bootstrap.Modal.getOrCreateInstance(quizModal);
            modalInstance.show();
        }
    });

    addQuestionBtn.addEventListener('click', addQuestion);

    quizQuestionsContainer.addEventListener('click', function (event) {
        if (event.target.classList.contains('btn-delete-question') || event.target.closest('.btn-delete-question')) {
            const deleteButton = event.target.closest('.btn-delete-question');
            const questionBlock = deleteButton.closest('.quiz-question-block');
            const questionId = questionBlock.querySelector('input[name*="[id]"]')?.value;
            const currentLessonIndex = currentLessonQuizIndexInput.value;

            if (confirm('Bạn có chắc muốn xoá câu hỏi này không?')) {
                questionBlock.remove();

                if (questionId) {
                    const hiddenInput = document.createElement('input');
                    hiddenInput.type = 'hidden';
                    hiddenInput.name = `lessons[${currentLessonIndex}][quizQuestionsToDelete]`;
                    hiddenInput.value = questionId;
                    quizForm.appendChild(hiddenInput);
                }

                updateQuestionIndices();
                quizQuestionIndexCounter = quizQuestionsContainer.querySelectorAll('.quiz-question-block').length;
            }
        }
    });

    lessonTabContent.addEventListener('click', function (event) {
        if (event.target.classList.contains('btn-delete-file') || event.target.closest('.btn-delete-file')) {
            const deleteButton = event.target.closest('.btn-delete-file');
            const fileType = deleteButton.getAttribute('data-file-type');
            const lessonIndex = deleteButton.getAttribute('data-lesson-index');
            const fileId = deleteButton.getAttribute('data-file-id');
            const listItem = deleteButton.closest('li');

            if (confirm('Bạn có chắc muốn xoá tệp này không?')) {
                listItem.remove();

                const hiddenInput = document.createElement('input');
                hiddenInput.type = 'hidden';
                hiddenInput.name = `lessons[${lessonIndex}][filesToDelete][${fileType}][]`;
                hiddenInput.value = fileId;
                document.getElementById('wizardForm').appendChild(hiddenInput);
            }
        }
    });

    document.getElementById('wizardForm').addEventListener('submit', function (event) {
        const quizData = [];

        // Lấy quiz data từ allCourseData
        if (typeof allCourseData !== 'undefined' && allCourseData && allCourseData.lessons) {
            allCourseData.lessons.forEach((lesson, lessonIndex) => {
                if (lesson && lesson.quizzes && lesson.quizzes.length > 0) {
                    quizData.push({
                        lessonIndex: lessonIndex,
                        questions: lesson.quizzes
                    });
                }
            });
        }

        // Cũng lấy quiz data từ các quiz-question-block trong modal (nếu có)
        const quizQuestionsContainer = document.getElementById('quizQuestionsContainer');
        if (quizQuestionsContainer) {
            const currentLessonIndex = document.getElementById('currentLessonQuizIndex')?.value;
            if (currentLessonIndex !== null && currentLessonIndex !== '') {
                const lessonIndex = parseInt(currentLessonIndex);
                const questions = [];
                
                quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach(block => {
                    const questionId = block.querySelector('input[name*="[id]"]')?.value || "";
                    const questionText = block.querySelector('textarea[name*="[question]"]')?.value || "";
                    const optionA = block.querySelector('input[name*="[optionA]"]')?.value || "";
                    const optionB = block.querySelector('input[name*="[optionB]"]')?.value || "";
                    const optionC = block.querySelector('input[name*="[optionC]"]')?.value || "";
                    const optionD = block.querySelector('input[name*="[optionD]"]')?.value || "";
                    const answer = block.querySelector('select[name*="[answer]"]')?.value || "";
                    
                    // Only add questions that have all required fields filled (trimmed)
                    if (questionText.trim() && optionA.trim() && optionB.trim() && optionC.trim() && optionD.trim() && answer) {
                        questions.push({
                            id: questionId,
                            question: questionText,
                            optionA: optionA,
                            optionB: optionB,
                            optionC: optionC,
                            optionD: optionD,
                            answer: answer
                        });
                    }
                });
                
                if (questions.length > 0) {
                    // Cập nhật hoặc thêm vào quizData
                    const existingIndex = quizData.findIndex(item => item.lessonIndex === lessonIndex);
                    if (existingIndex >= 0) {
                        quizData[existingIndex].questions = questions;
                    } else {
                        quizData.push({
                            lessonIndex: lessonIndex,
                            questions: questions
                        });
                    }
                }
            }
        }

        const existingQuizInput = document.querySelector('input[name="quizJson"]');
        if (existingQuizInput) {
            existingQuizInput.remove();
        }

        const quizJsonInput = document.createElement('input');
        quizJsonInput.type = 'hidden';
        quizJsonInput.name = 'quizJson';
        quizJsonInput.value = JSON.stringify(quizData);
        document.getElementById('wizardForm').appendChild(quizJsonInput);

        console.log('Quiz data being submitted:', quizData);
    });

    // --- LƯU QUIZ AJAX ---
    quizForm.addEventListener('submit', async function (event) {
        event.preventDefault();
        const currentLessonIndex = currentLessonQuizIndexInput.value;
        const lessonIdInput = document.querySelector(`input[name="lessons[${currentLessonIndex}][id]"]`);
        const lessonId = lessonIdInput ? lessonIdInput.value : '';
        if (!lessonId || lessonId === '0') {
            alert('Hãy lưu bài học trước khi tạo quiz!');
            return;
        }
        const quizQuestions = [];
        quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach((block, index) => {
            const questionText = block.querySelector('input[name*="[question]"]')?.value || '';
            const optionA = block.querySelector('input[name*="[optionA]"]')?.value || '';
            const optionB = block.querySelector('input[name*="[optionB]"]')?.value || '';
            const optionC = block.querySelector('input[name*="[optionC]"]')?.value || '';
            const optionD = block.querySelector('input[name*="[optionD]"]')?.value || '';
            const answer = block.querySelector('select[name*="[answer]"]')?.value || '';
            if (questionText.trim() && optionA.trim() && optionB.trim() && optionC.trim() && optionD.trim() && answer) {
                quizQuestions.push({
                    question: questionText,
                    optionA,
                    optionB,
                    optionC,
                    optionD,
                    answer
                });
            }
        });
        if (quizQuestions.length === 0) {
            alert('Vui lòng nhập ít nhất 1 câu hỏi đầy đủ!');
            return;
        }
        // Loading hiệu ứng
        const saveBtn = quizForm.querySelector('button[type="submit"]');
        saveBtn.disabled = true;
        const oldText = saveBtn.textContent;
        saveBtn.textContent = 'Đang lưu...';
        try {
            const res = await fetch('EditQuiz', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ lessonId: lessonId, quizzes: quizQuestions })
            });
            const data = await res.json();
            if (data.status === 'success') {
                alert('Lưu quiz thành công!');
                if (typeof allCourseData !== 'undefined' && allCourseData.lessons && allCourseData.lessons[currentLessonIndex]) {
                    allCourseData.lessons[currentLessonIndex].quizzes = quizQuestions;
                }
                const modalInstance = bootstrap.Modal.getInstance(quizModal);
                if (modalInstance) modalInstance.hide();
            } else {
                alert('Lỗi lưu quiz: ' + (data.msg || 'Không rõ lỗi.'));
            }
        } catch (err) {
            alert('Lỗi khi lưu quiz: ' + err.message);
        } finally {
            saveBtn.disabled = false;
            saveBtn.textContent = oldText;
        }
    });

    // --- LƯU LESSON RIÊNG LẺ (AJAX) ---
    lessonTabContent.addEventListener('click', async function (event) {
        if (event.target.classList.contains('save-lesson') || event.target.closest('.save-lesson')) {
            const btn = event.target.closest('.save-lesson');
            const lessonBlock = btn.closest('.lesson-block');
            const lessonPane = lessonBlock.closest('.tab-pane');
            const lessonIndex = lessonBlock.getAttribute('data-lesson-index');
            const courseId = document.querySelector('input[name="courseId"]').value;
            // Lấy dữ liệu
            const idInput = lessonBlock.querySelector(`input[name="lessons[${lessonIndex}][id]"]`);
            const nameInput = lessonBlock.querySelector(`input[name="lessons[${lessonIndex}][name]"]`);
            const descInput = lessonBlock.querySelector(`textarea[name="lessons[${lessonIndex}][desc]"]`);
            const orderInput = lessonBlock.querySelector(`input[name="lessons[${lessonIndex}][orderIndex]"]`);
            const lessonId = idInput ? idInput.value : '';
            const name = nameInput ? nameInput.value : '';
            const desc = descInput ? descInput.value : '';
            const orderIndex = orderInput ? orderInput.value : lessonIndex;
            if (!name.trim()) {
                alert('Tên bài học không được để trống!');
                return;
            }
            btn.disabled = true;
            btn.textContent = 'Đang lưu...';
            try {
                const formData = new FormData();
                formData.append('action', 'saveLesson');
                formData.append('lessonId', lessonId);
                formData.append('name', name);
                formData.append('desc', desc);
                formData.append('orderIndex', orderIndex);
                formData.append('courseId', courseId);
                const response = await fetch('EditCourseServlet', {
                    method: 'POST',
                    body: formData
                });
                if (!response.ok) throw new Error('Lỗi mạng hoặc server!');
                const data = await response.json();
                if (data.success) {
                    if (idInput) idInput.value = data.lessonId;
                    alert('Lưu bài học thành công!');
                } else {
                    alert('Lưu bài học thất bại: ' + (data.message || 'Không rõ lỗi.'));
                }
            } catch (err) {
                alert('Lỗi khi lưu bài học: ' + err.message);
            } finally {
                btn.disabled = false;
                btn.textContent = 'Lưu Lesson';
            }
        }
    });

    if (lessonTabList.querySelectorAll('.nav-link.active').length === 0) {
        const firstTab = lessonTabList.querySelector('.nav-link');
        if (firstTab) {
            new bootstrap.Tab(firstTab).show();
        }
    }

    // Tooltip hướng dẫn
    document.querySelectorAll('.quiz-question-block input, .quiz-question-block select').forEach(el => {
        el.title = 'Nhập thông tin cho câu hỏi quiz. Đáp án đúng sẽ được chọn ở mục cuối.';
    });
});
