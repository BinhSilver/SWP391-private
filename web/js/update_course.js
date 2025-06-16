/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


document.addEventListener('DOMContentLoaded', function() {
    let lessonIndexCounter = 0; // Keep track of the next available lesson index
    let quizQuestionIndexCounter = 0; // Keep track of the next available quiz question index

    const lessonTabList = document.getElementById('lessonTabList');
    const lessonTabContent = document.getElementById('lessonTabContent');
    const addLessonBtn = document.getElementById('addLessonBtn');
    const quizModal = document.getElementById('quizModal');
    const quizQuestionsContainer = document.getElementById('quizQuestionsContainer');
    const addQuestionBtn = document.getElementById('addQuestionBtn');
    const quizForm = document.getElementById('quizForm');
    const currentLessonQuizIndexInput = document.getElementById('currentLessonQuizIndex');

    // Function to re-index all lesson inputs after add/delete
    function updateLessonIndices() {
        const lessonBlocks = lessonTabContent.querySelectorAll('.lesson-block');
        lessonBlocks.forEach((block, newIndex) => {
            const oldIndex = block.getAttribute('data-lesson-index');
            // Update data-lesson-index
            block.setAttribute('data-lesson-index', newIndex);

            // Update form input names
            block.querySelectorAll('[name^="lessons["]').forEach(input => {
                const oldName = input.getAttribute('name');
                const newName = oldName.replace(`lessons[${oldIndex}]`, `lessons[${newIndex}]`);
                input.setAttribute('name', newName);
            });

            // Update associated tab links
            const tabLink = document.getElementById(`tab-${oldIndex}`);
            if (tabLink) {
                tabLink.id = `tab-${newIndex}`;
                tabLink.setAttribute('href', `#lesson-${newIndex}`);
                tabLink.textContent = `Lesson ${newIndex + 1}`; // Update display label
            }
            const tabPane = document.getElementById(`lesson-${oldIndex}`);
            if (tabPane) {
                tabPane.id = `lesson-${newIndex}`;
            }

            // Update lesson block heading
            block.querySelector('h6').textContent = `Lesson ${newIndex + 1}`;

            // Update quiz button data-lesson-index
            const quizButton = block.querySelector('.btn-toggle-quiz');
            if (quizButton) {
                quizButton.setAttribute('data-lesson-index', newIndex);
            }
        });
        lessonIndexCounter = lessonBlocks.length; // Ensure counter is updated
    }

    // Function to re-index all quiz question inputs within the modal
    function updateQuestionIndices() {
        const questionBlocks = quizQuestionsContainer.querySelectorAll('.quiz-question-block');
        questionBlocks.forEach((block, newIndex) => {
            const oldIndex = block.getAttribute('data-question-index'); // You'll need to add this attribute in your template
            if (!oldIndex) { // Handle newly added questions without an old index
                 block.setAttribute('data-question-index', newIndex);
            }

            // Update form input names
            block.querySelectorAll('[name^="questions["]').forEach(input => {
                const oldName = input.getAttribute('name');
                const newName = oldName.replace(/questions\[\d+\]/, `questions[${newIndex}]`);
                input.setAttribute('name', newName);
            });

            // Update collapse toggle target/aria-controls
            const collapseToggle = block.querySelector('.quiz-collapse-toggle');
            if (collapseToggle) {
                collapseToggle.setAttribute('data-bs-target', `#questionCollapse-${newIndex}`);
                collapseToggle.setAttribute('aria-controls', `questionCollapse-${newIndex}`);
            }
            const collapseDiv = block.querySelector('.collapse');
            if (collapseDiv) {
                collapseDiv.id = `questionCollapse-${newIndex}`;
            }

            // Update question block heading
            block.querySelector('h6').textContent = `Câu hỏi ${newIndex + 1}`;
        });
        quizQuestionIndexCounter = questionBlocks.length; // Ensure counter is updated
    }

    // Function to add a new lesson (similar to your create_course.js but adapted for update)
    function addLesson() {
        const newIndex = lessonIndexCounter++;
        let templateHtml = document.getElementById('lessonTemplate').innerHTML;
        templateHtml = templateHtml.replace(/{{index}}/g, newIndex)
                                   .replace(/{{indexLabel}}/g, newIndex + 1);

        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = templateHtml;
        const newLessonPane = tempDiv.firstElementChild;
        lessonTabContent.appendChild(newLessonPane);

        const newTabItem = document.createElement('li');
        newTabItem.classList.add('nav-item');
        newTabItem.innerHTML = `<a class="nav-link" id="tab-${newIndex}" data-bs-toggle="tab" href="#lesson-${newIndex}" role="tab">Lesson ${newIndex + 1}</a>`;
        lessonTabList.appendChild(newTabItem);

        // Activate the new tab
        const newTab = new bootstrap.Tab(newTabItem.querySelector('.nav-link'));
        newTab.show();

        updateLessonIndices(); // Ensure all indices are correct after adding
    }

    // Function to add a new quiz question
    function addQuestion() {
        const newIndex = quizQuestionIndexCounter++;
        let templateHtml = document.getElementById('quizQuestionTemplate').innerHTML;
        templateHtml = templateHtml.replace(/{{index}}/g, newIndex)
                                   .replace(/{{questionLabel}}/g, newIndex + 1);

        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = templateHtml;
        const newQuestionBlock = tempDiv.firstElementChild;
        newQuestionBlock.setAttribute('data-question-index', newIndex); // Set data attribute

        quizQuestionsContainer.appendChild(newQuestionBlock);
        updateQuestionIndices(); // Re-index after adding
    }

    // Initial population of lessons when the page loads (using allCourseData)
    if (allCourseData && allCourseData.lessons.length > 0) {
        // Clear the default lesson 1 if it exists
        const defaultLessonTab = document.getElementById('tab-0');
        const defaultLessonPane = document.getElementById('lesson-0');
        if (defaultLessonTab && defaultLessonPane && allCourseData.lessons.length > 0) {
             defaultLessonTab.parentElement.remove(); // Remove li
             defaultLessonPane.remove(); // Remove div
        }

        // The JSP has already rendered the lessons, so just ensure counters are correct
        lessonIndexCounter = allCourseData.lessons.length;
    } else {
        // If no lessons, ensure there's at least one blank lesson initially
        lessonIndexCounter = 1; // Default Lesson 1 is already in the HTML
    }


    // Event listener for adding new lessons
    addLessonBtn.addEventListener('click', addLesson);

    // Event listener for deleting lessons
    lessonTabContent.addEventListener('click', function(event) {
        if (event.target.classList.contains('btn-delete-lesson') || event.target.closest('.btn-delete-lesson')) {
            const deleteButton = event.target.closest('.btn-delete-lesson');
            const lessonBlock = deleteButton.closest('.lesson-block');
            const lessonIndexToDelete = parseInt(lessonBlock.getAttribute('data-lesson-index'));

            if (confirm('Bạn có chắc muốn xoá bài học này không?')) {
                // Remove the tab and its content
                document.getElementById(`tab-${lessonIndexToDelete}`).parentElement.remove(); // li
                document.getElementById(`lesson-${lessonIndexToDelete}`).remove(); // div

                // You might need to send a hidden input to the server to mark this lesson for deletion
                const hiddenInput = document.createElement('input');
                hiddenInput.type = 'hidden';
                hiddenInput.name = `lessonsToDelete`; // Or lessons[${lessonIndexToDelete}][deleted]=true
                hiddenInput.value = lessonBlock.querySelector('input[name*="[id]"]').value; // Send the ID of the lesson to be deleted
                document.getElementById('wizardForm').appendChild(hiddenInput);

                // Reactivate a neighboring tab if the current one was deleted
                const remainingTabs = lessonTabList.querySelectorAll('.nav-link');
                if (remainingTabs.length > 0) {
                    new bootstrap.Tab(remainingTabs[0]).show();
                } else {
                    // If no lessons left, add a new one
                    addLesson();
                }

                updateLessonIndices(); // Re-index after deletion
            }
        }
    });

    // Event listener to open quiz modal and load quiz questions
    lessonTabContent.addEventListener('click', function(event) {
        if (event.target.classList.contains('btn-toggle-quiz') || event.target.closest('.btn-toggle-quiz')) {
            const quizButton = event.target.closest('.btn-toggle-quiz');
            const lessonIndex = parseInt(quizButton.getAttribute('data-lesson-index'));
            currentLessonQuizIndexInput.value = lessonIndex; // Store current lesson index

            quizQuestionsContainer.innerHTML = ''; // Clear previous quiz questions

            const lessonData = allCourseData.lessons[lessonIndex];
            if (lessonData && lessonData.quizzes && lessonData.quizzes.length > 0) {
                lessonData.quizzes.forEach((question, qIndex) => {
                    let templateHtml = document.getElementById('quizQuestionTemplate').innerHTML;
                    templateHtml = templateHtml.replace(/{{index}}/g, qIndex)
                                               .replace(/{{questionLabel}}/g, qIndex + 1);
                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = templateHtml;
                    const newQuestionBlock = tempDiv.firstElementChild;
                    newQuestionBlock.setAttribute('data-question-index', qIndex); // Set data attribute

                    // Pre-fill fields
                    newQuestionBlock.querySelector('input[name*="[id]"]').value = question.id; // Pre-fill question ID
                    newQuestionBlock.querySelector('input[name*="[question]"]').value = question.question;
                    newQuestionBlock.querySelector('input[name*="[optionA]"]').value = question.optionA;
                    newQuestionBlock.querySelector('input[name*="[optionB]"]').value = question.optionB;
                    newQuestionBlock.querySelector('input[name*="[optionC]"]').value = question.optionC;
                    newQuestionBlock.querySelector('input[name*="[optionD]"]').value = question.optionD;
                    newQuestionBlock.querySelector('select[name*="[answer]"]').value = question.answer;

                    quizQuestionsContainer.appendChild(newQuestionBlock);
                });
                quizQuestionIndexCounter = lessonData.quizzes.length;
            } else {
                // If no existing quizzes, add one blank question
                addQuestion();
            }
            updateQuestionIndices(); // Ensure indices are correct
        }
    });

    // Event listener for adding new quiz questions
    addQuestionBtn.addEventListener('click', addQuestion);

    // Event listener for deleting quiz questions
    quizQuestionsContainer.addEventListener('click', function(event) {
        if (event.target.classList.contains('btn-delete-question') || event.target.closest('.btn-delete-question')) {
            const deleteButton = event.target.closest('.btn-delete-question');
            const questionBlock = deleteButton.closest('.quiz-question-block');
            const questionId = questionBlock.querySelector('input[name*="[id]"]').value;
            const currentLessonIndex = currentLessonQuizIndexInput.value;

            if (confirm('Bạn có chắc muốn xoá câu hỏi này không?')) {
                questionBlock.remove();

                // If deleting an existing question, you'll need to inform the server
                if (questionId) {
                    const hiddenInput = document.createElement('input');
                    hiddenInput.type = 'hidden';
                    hiddenInput.name = `lessons[${currentLessonIndex}][quizQuestionsToDelete]`; // Array of IDs to delete
                    hiddenInput.value = questionId;
                    quizForm.appendChild(hiddenInput);
                }

                updateQuestionIndices(); // Re-index after deletion
            }
        }
    });

    // Event listener for file deletion (for existing files)
    lessonTabContent.addEventListener('click', function(event) {
        if (event.target.classList.contains('btn-delete-file') || event.target.closest('.btn-delete-file')) {
            const deleteButton = event.target.closest('.btn-delete-file');
            const fileType = deleteButton.getAttribute('data-file-type'); // e.g., "vocabVideo"
            const lessonIndex = deleteButton.getAttribute('data-lesson-index');
            const fileId = deleteButton.getAttribute('data-file-id');
            const listItem = deleteButton.closest('li');

            if (confirm('Bạn có chắc muốn xoá tệp này không?')) {
                listItem.remove();

                // Add a hidden input to mark this file for deletion on the server
                const hiddenInput = document.createElement('input');
                hiddenInput.type = 'hidden';
                // Example name: lessons[0][filesToDelete][vocabVideos][0]=id_of_file
                hiddenInput.name = `lessons[${lessonIndex}][filesToDelete][${fileType}][]`;
                hiddenInput.value = fileId;
                document.getElementById('wizardForm').appendChild(hiddenInput);
            }
        }
    });


    // Submit handler for the main form
    document.getElementById('wizardForm').addEventListener('submit', function(event) {
        // You might want to add client-side validation here
        // If using AJAX, prevent default submission and send data via fetch/XMLHttpRequest
        // For a full form submission, ensure all hidden inputs for deletions are in place.
    });

    // Submit handler for the quiz form (within the modal)
    quizForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Prevent default form submission

        const currentLessonIndex = currentLessonQuizIndexInput.value;
        const quizQuestions = [];
        quizQuestionsContainer.querySelectorAll('.quiz-question-block').forEach(block => {
            const questionId = block.querySelector('input[name*="[id]"]').value;
            quizQuestions.push({
                id: questionId, // Will be empty for new questions
                question: block.querySelector('input[name*="[question]"]').value,
                optionA: block.querySelector('input[name*="[optionA]"]').value,
                optionB: block.querySelector('input[name*="[optionB]"]').value,
                optionC: block.querySelector('input[name*="[optionC]"]').value,
                optionD: block.querySelector('input[name*="[optionD]"]').value,
                answer: block.querySelector('select[name*="[answer]"]').value
            });
        });

        // Store the quiz questions in your `allCourseData` structure
        // This is a client-side representation, needs to be sent to server on main form submit
        if (!allCourseData.lessons[currentLessonIndex]) {
            allCourseData.lessons[currentLessonIndex] = {}; // Initialize if not present
        }
        allCourseData.lessons[currentLessonIndex].quizzes = quizQuestions;

        alert('Quiz đã được lưu tạm thời. Hãy click "Cập Nhật Khóa Học" để lưu toàn bộ.');
        // Potentially close the modal here:
        const modalInstance = bootstrap.Modal.getInstance(quizModal);
        modalInstance.hide();
    });

    // Handle initial active tab (if no lessons, activate lesson-0)
    if (lessonTabList.querySelectorAll('.nav-link.active').length === 0) {
        const firstTab = lessonTabList.querySelector('.nav-link');
        if (firstTab) {
            new bootstrap.Tab(firstTab).show();
        }
    }
});