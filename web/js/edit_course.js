document.addEventListener("DOMContentLoaded", function () {
    // --- Quiz Modal setup ---
    const quizQuestionTemplate = document.getElementById("quizQuestionTemplate");
    const quizQuestionsContainer = document.getElementById("quizQuestionsContainer");
    const quizForm = document.getElementById("quizForm");
    const wizardForm = document.getElementById("wizardForm");
    let activeQuizLessonIndex = null;
    let quizModal = null;

    // Initialize modal once
    const quizModalEl = document.getElementById('quizModal');
    if (quizModalEl) {
        quizModal = new bootstrap.Modal(quizModalEl);
    }

    // Function to show modal for a specific lesson
    window.showQuizModalForLesson = function (lessonIndex) {
        activeQuizLessonIndex = lessonIndex;
        loadQuizForLesson(lessonIndex);
        if (quizModal) {
            quizModal.show();
        }
    };

    // Function to close modal
    function closeQuizModal() {
        if (quizModal) {
            quizModal.hide();
        }
    }

    // Load quiz data for a lesson
    function loadQuizForLesson(lessonIndex) {
        quizQuestionsContainer.innerHTML = '';
        let questions = [];

        // Get quiz from preloaded data
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

    // Add a new question block
    function addQuestionBlock(index, questionData = {}) {
        let html = quizQuestionTemplate.innerHTML
                .replace(/{{index}}/g, index)
                .replace(/{{questionLabel}}/g, index + 1);

        const wrapper = document.createElement("div");
        wrapper.innerHTML = html.trim();
        const block = wrapper.firstElementChild;

        if (questionData.question) {
            block.querySelector(`[name="questions[${index}][question]"]`).value = questionData.question || '';
            block.querySelector(`[name="questions[${index}][optionA]"]`).value = questionData.optionA || '';
            block.querySelector(`[name="questions[${index}][optionB]"]`).value = questionData.optionB || '';
            block.querySelector(`[name="questions[${index}][optionC]"]`).value = questionData.optionC || '';
            block.querySelector(`[name="questions[${index}][optionD]"]`).value = questionData.optionD || '';
            block.querySelector(`[name="questions[${index}][answer]"]`).value = questionData.answer || '';
        }
        quizQuestionsContainer.appendChild(block);
    }

    // Update question indices after adding/deleting
    function updateQuizQuestionIndices() {
        quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach((block, idx) => {
            let h6 = block.querySelector('h6');
            if (h6) {
                h6.textContent = `Câu hỏi ${idx + 1}`;
            }

            let toggleBtn = block.querySelector('.quiz-collapse-toggle');
            if (toggleBtn) {
                toggleBtn.dataset.bsTarget = `#questionCollapse-${idx}`;
                toggleBtn.setAttribute('aria-controls', `questionCollapse-${idx}`);
            }

            let collapse = block.querySelector('.collapse');
            if (collapse) {
                collapse.id = `questionCollapse-${idx}`;
            }

            block.querySelectorAll('[name^="questions["]').forEach(input => {
                input.name = input.name.replace(/questions\[\d+\]/g, `questions[${idx}]`);
            });
        });
    }

    // Add question button
    document.getElementById("addQuestionBtn").addEventListener("click", function () {
        const idx = quizQuestionsContainer.querySelectorAll('.quiz-question-block').length;
        addQuestionBlock(idx, {});
        updateQuizQuestionIndices();
    });

    // Delete question handler
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

    // Submit quiz form
    quizForm.addEventListener("submit", function (e) {
        e.preventDefault();
        if (activeQuizLessonIndex === null) {
            alert("Lỗi: Không xác định được lesson index!");
            return;
        }

        let questionsArr = [];
        let hasError = false;

        quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach((questionBlock, idx) => {
            const question = questionBlock.querySelector(`[name="questions[${idx}][question]"]`).value.trim();
            const optionA = questionBlock.querySelector(`[name="questions[${idx}][optionA]"]`).value.trim();
            const optionB = questionBlock.querySelector(`[name="questions[${idx}][optionB]"]`).value.trim();
            const optionC = questionBlock.querySelector(`[name="questions[${idx}][optionC]"]`).value.trim();
            const optionD = questionBlock.querySelector(`[name="questions[${idx}][optionD]"]`).value.trim();
            const answer = questionBlock.querySelector(`[name="questions[${idx}][answer]"]`).value;

            if (!question || !optionA || !optionB || !optionC || !optionD || !answer) {
                alert(`Câu hỏi ${idx + 1}: Vui lòng điền đầy đủ thông tin!`);
                hasError = true;
                return;
            }

            questionsArr.push({
                question: question,
                optionA: optionA,
                optionB: optionB,
                optionC: optionC,
                optionD: optionD,
                answer: answer
            });
        });

        if (hasError) {
            return;
        }

        const lessonId = window.lessonIndexToIdMap[activeQuizLessonIndex];
        if (!lessonId) {
            alert("Không tìm thấy lessonId cho bài học này!");
            return;
        }

        fetch(contextPath + "/EditQuiz", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                lessonId: lessonId,
                quizzes: questionsArr
            })
        })
                .then(res => res.json())
                .then(data => {
                    if (data.status === "success") {
                        alert("Quiz đã được lưu thành công!");
                        closeQuizModal();
                    } else {
                        alert("Lưu quiz thất bại! " + (data.msg || ''));
                    }
                })
                .catch(err => {
                    console.error(err);
                    alert("Lỗi khi lưu quiz!");
                });
    });

    // Attach quiz buttons to lessons
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

    // Initialize
    attachQuizButtons();
    window.reattachQuizButtons = attachQuizButtons;
});