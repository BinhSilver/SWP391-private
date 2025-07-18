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
        let timeInput = newQuestionBlock.querySelector('.quiz-time-limit');
        if (!timeInput) {
            timeInput = document.createElement('input');
            timeInput.type = 'hidden';
            timeInput.className = 'quiz-time-limit';
            timeInput.name = `lessons[${currentLessonIndex}][questions][${newIndex}][timeLimit]`;
            timeInput.value = document.getElementById('quizTimeLimitInput')?.value || 60;
            newQuestionBlock.appendChild(timeInput);
        }
        quizQuestionsContainer.appendChild(newQuestionBlock);
        updateQuestionIndices();
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

    lessonTabContent.addEventListener('click', function (event) {
        if (event.target.classList.contains('btn-delete-lesson') || event.target.closest('.btn-delete-lesson')) {
            const deleteButton = event.target.closest('.btn-delete-lesson');
            const lessonBlock = deleteButton.closest('.lesson-block');
            const lessonIndexToDelete = parseInt(lessonBlock.getAttribute('data-lesson-index'));

            if (confirm('Bạn có chắc muốn xoá bài học này không?')) {
                document.getElementById(`tab-${lessonIndexToDelete}`).parentElement.remove();
                document.getElementById(`lesson-${lessonIndexToDelete}`).remove();

                const hiddenInput = document.createElement('input');
                hiddenInput.type = 'hidden';
                hiddenInput.name = `lessonsToDelete`;
                hiddenInput.value = lessonBlock.querySelector('input[name*="[id]"]').value;
                document.getElementById('wizardForm').appendChild(hiddenInput);

                currentLessonQuizIndexInput.value = "";
                quizQuestionsContainer.innerHTML = "";

                // Xóa lesson khỏi allCourseData
                if (typeof allCourseData !== 'undefined' && allCourseData.lessons && allCourseData.lessons[lessonIndexToDelete]) {
                    allCourseData.lessons.splice(lessonIndexToDelete, 1);
                }

                const remainingTabs = lessonTabList.querySelectorAll('.nav-link');
                if (remainingTabs.length > 0) {
                    new bootstrap.Tab(remainingTabs[0]).show();
                } else {
                    addLesson();
                }

                updateLessonIndices();
            }
        }
    });

    lessonTabContent.addEventListener('click', function (event) {
        if (event.target.classList.contains('btn-toggle-quiz') || event.target.closest('.btn-toggle-quiz')) {
            const quizButton = event.target.closest('.btn-toggle-quiz');
            const lessonIndex = parseInt(quizButton.getAttribute('data-lesson-index'));
            // Đảm bảo luôn set lại currentLessonQuizIndexInput.value đúng lesson
            currentLessonQuizIndexInput.value = lessonIndex;
            quizQuestionsContainer.innerHTML = '';
            // Lấy dữ liệu quiz cũ nếu có
            const lessonData = typeof allCourseData !== 'undefined' && allCourseData && allCourseData.lessons ? allCourseData.lessons[lessonIndex] : null;
            let quizCount = 0;
            if (lessonData && lessonData.quizzes && Array.isArray(lessonData.quizzes) && lessonData.quizzes.length > 0) {
                quizCount = lessonData.quizzes.length;
                quizQuestionsContainer.innerHTML = '';
                lessonData.quizzes.forEach((question, qIndex) => {
                    let templateHtml = document.getElementById('quizQuestionTemplate').innerHTML;
                    templateHtml = templateHtml
                            .replace(/{{lessonIndex}}/g, lessonIndex)
                            .replace(/{{questionIndex}}/g, qIndex)
                            .replace(/{{questionLabel}}/g, qIndex + 1)
                            .replace(/{{index}}/g, qIndex)
                            + `<input type="hidden" class="quiz-time-limit" name="lessons[${lessonIndex}][questions][${qIndex}][timeLimit]" value="${question.timeLimit || 60}" />`;
                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = templateHtml;
                    const newQuestionBlock = tempDiv.firstElementChild;
                    newQuestionBlock.setAttribute('data-question-index', qIndex);
                    quizQuestionsContainer.appendChild(newQuestionBlock);
                });
                quizQuestionIndexCounter = quizCount;
            } else {
                addQuestion();
                quizQuestionIndexCounter = 1;
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
            const questionId = questionBlock.querySelector('input[name*="[id]"]').value;
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

    quizForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const currentLessonIndex = currentLessonQuizIndexInput.value;
        const quizQuestions = [];
        const totalBlocks = quizQuestionsContainer.querySelectorAll('.quiz-question-block').length;
        console.log('[DEBUG] Processing', totalBlocks, 'question blocks in container');
        quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach((block, index) => {
            const questionId = block.querySelector('input[name*="[id]"]')?.value || "";
            const questionText = block.querySelector('textarea[name*="[question]"]')?.value || "";
            const optionA = block.querySelector('input[name*="[optionA]"]')?.value || "";
            const optionB = block.querySelector('input[name*="[optionB]"]')?.value || "";
            const optionC = block.querySelector('input[name*="[optionC]"]')?.value || "";
            const optionD = block.querySelector('input[name*="[optionD]"]')?.value || "";
            const answer = block.querySelector('select[name*="[answer]"]')?.value || "";
            
            console.log(`[DEBUG] Processing question ${index + 1}:`, {questionText, optionA, optionB, optionC, optionD, answer});
            
                    // Only add questions that have all required fields filled
        if (questionText.trim() && optionA.trim() && optionB.trim() && optionC.trim() && optionD.trim() && answer) {
            quizQuestions.push({
                id: questionId,
                question: questionText,
                optionA: optionA,
                optionB: optionB,
                optionC: optionC,
                optionD: optionD,
                answer: answer
            });
        } else {
            console.log('[DEBUG] Skipping empty question:', {questionText, optionA, optionB, optionC, optionD, answer});
        }
        });

        // Lưu quiz data vào allCourseData
        if (typeof allCourseData === 'undefined') {
            allCourseData = {lessons: []};
        }
        if (!allCourseData.lessons[currentLessonIndex]) {
            allCourseData.lessons[currentLessonIndex] = {
                id: 0,
                name: "",
                description: "",
                quizzes: []
            };
        }
        allCourseData.lessons[currentLessonIndex].quizzes = quizQuestions;

        console.log('Quiz saved to allCourseData for lesson', currentLessonIndex, ':', quizQuestions);
        alert('Quiz đã được lưu tạm thời. Hãy click "Cập Nhật Khóa Học" để lưu toàn bộ.');
        const modalInstance = bootstrap.Modal.getInstance(quizModal);
        if (modalInstance) {
            modalInstance.hide();
        }
    });

    if (lessonTabList.querySelectorAll('.nav-link.active').length === 0) {
        const firstTab = lessonTabList.querySelector('.nav-link');
        if (firstTab) {
            new bootstrap.Tab(firstTab).show();
        }
    }
});
