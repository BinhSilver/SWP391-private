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

        updateLessonIndices();
    }

    function addQuestion() {
        const currentLessonIndex = currentLessonQuizIndexInput.value;
        const newIndex = quizQuestionIndexCounter++;
        let templateHtml = document.getElementById('quizQuestionTemplate').innerHTML;
        templateHtml = templateHtml
                .replace(/{{lessonIndex}}/g, currentLessonIndex)
                .replace(/{{questionIndex}}/g, newIndex)
                .replace(/{{questionLabel}}/g, newIndex + 1);

        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = templateHtml;
        const newQuestionBlock = tempDiv.firstElementChild;
        newQuestionBlock.setAttribute('data-question-index', newIndex);

        quizQuestionsContainer.appendChild(newQuestionBlock);
        updateQuestionIndices();
    }

    if (typeof allCourseData !== 'undefined' && allCourseData && allCourseData.lessons && allCourseData.lessons.length > 0) {
        const defaultLessonTab = document.getElementById('tab-0');
        const defaultLessonPane = document.getElementById('lesson-0');
        if (defaultLessonTab && defaultLessonPane) {
            defaultLessonTab.parentElement.remove();
            defaultLessonPane.remove();
        }
        lessonIndexCounter = allCourseData.lessons.length;
    } else {
        lessonIndexCounter = 1;
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
            currentLessonQuizIndexInput.value = lessonIndex;

            quizQuestionsContainer.innerHTML = '';

            const lessonData = typeof allCourseData !== 'undefined' && allCourseData && allCourseData.lessons ? allCourseData.lessons[lessonIndex] : null;
            if (lessonData && lessonData.quizzes && lessonData.quizzes.length > 0) {
                lessonData.quizzes.forEach((question, qIndex) => {
                    let templateHtml = document.getElementById('quizQuestionTemplate').innerHTML;
                    templateHtml = templateHtml
                            .replace(/{{lessonIndex}}/g, lessonIndex)
                            .replace(/{{questionIndex}}/g, qIndex)
                            .replace(/{{questionLabel}}/g, qIndex + 1);

                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = templateHtml;
                    const newQuestionBlock = tempDiv.firstElementChild;
                    newQuestionBlock.setAttribute('data-question-index', qIndex);

                    newQuestionBlock.querySelector('input[name*="[id]"]').value = question.id || "";
                    newQuestionBlock.querySelector('textarea[name*="[question]"]').value = question.question || "";
                    newQuestionBlock.querySelector('input[name*="[optionA]"]').value = question.optionA || "";
                    newQuestionBlock.querySelector('input[name*="[optionB]"]').value = question.optionB || "";
                    newQuestionBlock.querySelector('input[name*="[optionC]"]').value = question.optionC || "";
                    newQuestionBlock.querySelector('input[name*="[optionD]"]').value = question.optionD || "";
                    newQuestionBlock.querySelector('select[name*="[answer]"]').value = question.answer || "";

                    quizQuestionsContainer.appendChild(newQuestionBlock);

                    // Open first question by default
                    if (qIndex === 0) {
                        const collapseDiv = newQuestionBlock.querySelector('.collapse');
                        if (collapseDiv) {
                            new bootstrap.Collapse(collapseDiv, {toggle: true});
                        }
                    }
                });
                quizQuestionIndexCounter = lessonData.quizzes.length;
            } else {
                addQuestion();
            }
            updateQuestionIndices();

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
        quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach(block => {
            const questionId = block.querySelector('input[name*="[id]"]').value;
            quizQuestions.push({
                id: questionId,
                question: block.querySelector('textarea[name*="[question]"]').value,
                optionA: block.querySelector('input[name*="[optionA]"]').value,
                optionB: block.querySelector('input[name*="[optionB]"]').value,
                optionC: block.querySelector('input[name*="[optionC]"]').value,
                optionD: block.querySelector('input[name*="[optionD]"]').value,
                answer: block.querySelector('select[name*="[answer]"]').value
            });
        });

        if (typeof allCourseData === 'undefined') {
            allCourseData = {lessons: []};
        }
        if (!allCourseData.lessons[currentLessonIndex]) {
            allCourseData.lessons[currentLessonIndex] = {};
        }
        allCourseData.lessons[currentLessonIndex].quizzes = quizQuestions;

        alert('Quiz đã được lưu tạm thời. Hãy click "Cập Nhật Khóa Học" để lưu toàn bộ.');
        const modalInstance = bootstrap.Modal.getInstance(quizModal);
        modalInstance.hide();
    });

    if (lessonTabList.querySelectorAll('.nav-link.active').length === 0) {
        const firstTab = lessonTabList.querySelector('.nav-link');
        if (firstTab) {
            new bootstrap.Tab(firstTab).show();
        }
    }
});
