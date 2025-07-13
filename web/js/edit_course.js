document.addEventListener("DOMContentLoaded", function () {
    const quizQuestionTemplate = document.getElementById("quizQuestionTemplate");
    const quizQuestionsContainer = document.getElementById("quizQuestionsContainer");
    const quizForm = document.getElementById("quizForm");
    const wizardForm = document.getElementById("wizardForm");
    let activeQuizLessonIndex = null;
    let quizModal = null;

    const contextPath = window.contextPath || '';

    const quizModalEl = document.getElementById('quizModal');
    if (quizModalEl) {
        quizModal = new bootstrap.Modal(quizModalEl);
    }

    window.showQuizModalForLesson = function (lessonIndex) {
        activeQuizLessonIndex = lessonIndex;
        loadQuizForLesson(lessonIndex);
        if (quizModal) {
            quizModal.show();
        }
    };

    function closeQuizModal() {
        if (quizModal) {
            quizModal.hide();
        }
    }

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

    function addQuestionBlock(index, questionData = {}) {
        let html = quizQuestionTemplate.innerHTML
                .replace(/{{lessonIndex}}/g, activeQuizLessonIndex)
                .replace(/{{questionIndex}}/g, index)
                .replace(/{{questionLabel}}/g, index + 1);

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
            new bootstrap.Collapse(collapse, {toggle: false});
        }

        quizQuestionsContainer.appendChild(block);
    }

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

    // Chỉ thêm event listener nếu element tồn tại
    const addQuestionBtn = document.getElementById("addQuestionBtn");
    if (addQuestionBtn) {
        addQuestionBtn.addEventListener("click", function () {
            const idx = quizQuestionsContainer.querySelectorAll('.quiz-question-block').length;
            addQuestionBlock(idx, {});
            updateQuizQuestionIndices();
        });
    }

    if (quizQuestionsContainer) {
        quizQuestionsContainer.addEventListener("click", function (e) {
            if (e.target.classList.contains("btn-delete-question") ||
                    e.target.closest(".btn-delete-question")) {
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

    // Sửa logic xử lý quiz form để lưu vào allCourseData thay vì gọi API riêng
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

            // Lưu quiz data vào allCourseData thay vì gọi API riêng
            if (typeof allCourseData === 'undefined') {
                allCourseData = {lessons: []};
            }
            if (!allCourseData.lessons[activeQuizLessonIndex]) {
                allCourseData.lessons[activeQuizLessonIndex] = {};
            }
            allCourseData.lessons[activeQuizLessonIndex].quizzes = questionsArr;

            alert('Quiz đã được lưu tạm thời. Hãy click "Cập Nhật Khóa Học" để lưu toàn bộ.');
            closeQuizModal();
        });
    }

    if (!window.lessonIndexToIdMap) {
        window.lessonIndexToIdMap = {};
    }

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

    attachQuizButtons();
    window.reattachQuizButtons = attachQuizButtons;
});
