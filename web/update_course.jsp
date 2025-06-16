<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Chỉnh Sửa Khóa Học - Wasabii</title> <%-- Changed title --%>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" />
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap" />
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/update_course.css'/>">
    </head>

    <body>
        <%@ include file="/Home/nav.jsp" %>
        <section class="container py-5">
            <h2 class="text-danger fw-bold text-center mb-4">Chỉnh Sửa Khóa Học</h2> <%-- Changed heading --%>
            <%-- Assuming courseId is passed as a request parameter or hidden input --%>
            <form id="wizardForm" class="bg-white p-4 rounded-4 shadow-sm" enctype="multipart/form-data" action="<c:url value='/updateCourse'/>" method="post"> <%-- Added action and method --%>
                <%-- Hidden input for Course ID --%>
                <input type="hidden" name="courseId" value="${course.id}" />

                <%-- Add main course details here, pre-filled --%>
                <div class="mb-4">
                    <label for="courseName" class="form-label">Tên Khóa Học</label>
                    <input type="text" class="form-control" id="courseName" name="courseName" value="${course.name}" required />
                </div>
                <div class="mb-4">
                    <label for="courseDescription" class="form-label">Mô tả Khóa Học</label>
                    <textarea class="form-control" id="courseDescription" name="courseDescription" rows="3">${course.description}</textarea>
                </div>
                <%-- You might have other course fields like image, category, price, etc. --%>

                <div class="row">
                    <div class="col-md-3">
                        <ul class="nav nav-tabs nav-tabs-vertical" id="lessonTabList" role="tablist">
                            <c:choose>
                                <c:when test="${not empty lessons}">
                                    <c:forEach var="lesson" items="${lessons}" varStatus="loop">
                                        <li class="nav-item">
                                            <a class="nav-link ${loop.first ? 'active' : ''}" id="tab-${loop.index}" data-bs-toggle="tab" href="#lesson-${loop.index}"
                                               role="tab">Lesson ${loop.count}</a>
                                        </li>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <%-- If no lessons exist, provide a default Lesson 1 --%>
                                    <li class="nav-item">
                                        <a class="nav-link active" id="tab-0" data-bs-toggle="tab" href="#lesson-0"
                                           role="tab">Lesson 1</a>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                        <button type="button" class="btn btn-outline-primary mt-3 w-100" id="addLessonBtn"><i
                                class="fas fa-plus"></i> Thêm Lesson</button>
                    </div>

                    <div class="col-md-9">
                        <div class="tab-content" id="lessonTabContent">
                            <c:choose>
                                <c:when test="${not empty lessons}">
                                    <c:forEach var="lesson" items="${lessons}" varStatus="loop">
                                        <div class="tab-pane fade ${loop.first ? 'show active' : ''}" id="lesson-${loop.index}" role="tabpanel">
                                            <div class="lesson-block course-card border p-3 rounded mb-3" data-lesson-index="${loop.index}">
                                                <h6 class="fw-semibold mb-3">Lesson ${loop.count}</h6>
                                                <input type="hidden" name="lessons[${loop.index}][id]" value="${lesson.id}" /> <%-- Add lesson ID --%>
                                                <div class="mb-2">
                                                    <label class="form-label">Tên Bài Học</label>
                                                    <input type="text" class="form-control" name="lessons[${loop.index}][name]" value="${lesson.name}" required />
                                                </div>
                                                <div class="mb-2">
                                                    <label class="form-label">Mô tả bài học</label>
                                                    <textarea class="form-control" name="lessons[${loop.index}][desc]" rows="2">${lesson.description}</textarea>
                                                </div>

                                                <%-- Handling Existing Videos/Documents --%>
                                                <div class="mb-2">
                                                    <label class="form-label">Video Từ Vựng</label>
                                                    <c:if test="${not empty lesson.vocabVideos}">
                                                        <ul class="list-group list-group-flush mb-2">
                                                            <c:forEach var="video" items="${lesson.vocabVideos}" varStatus="vLoop">
                                                                <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                                    <a href="${video.path}" target="_blank">${video.name}</a>
                                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete-file" data-file-type="vocabVideo" data-lesson-index="${loop.index}" data-file-id="${video.id}">
                                                                        <i class="fas fa-times"></i>
                                                                    </button>
                                                                    <input type="hidden" name="lessons[${loop.index}][existingVocabVideos][${vLoop.index}][id]" value="${video.id}" />
                                                                    <input type="hidden" name="lessons[${loop.index}][existingVocabVideos][${vLoop.index}][path]" value="${video.path}" />
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:if>
                                                    <input type="file" class="form-control" name="lessons[${loop.index}][newVocabVideo][]" accept="video/*" multiple />
                                                </div>

                                                <div class="mb-2">
                                                    <label class="form-label">Tài Liệu Từ Vựng (PDF)</label>
                                                    <c:if test="${not empty lesson.vocabDocs}">
                                                        <ul class="list-group list-group-flush mb-2">
                                                            <c:forEach var="doc" items="${lesson.vocabDocs}" varStatus="dLoop">
                                                                <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                                    <a href="${doc.path}" target="_blank">${doc.name}</a>
                                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete-file" data-file-type="vocabDoc" data-lesson-index="${loop.index}" data-file-id="${doc.id}">
                                                                        <i class="fas fa-times"></i>
                                                                    </button>
                                                                    <input type="hidden" name="lessons[${loop.index}][existingVocabDocs][${dLoop.index}][id]" value="${doc.id}" />
                                                                    <input type="hidden" name="lessons[${loop.index}][existingVocabDocs][${dLoop.index}][path]" value="${doc.path}" />
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:if>
                                                    <input type="file" class="form-control" name="lessons[${loop.index}][newVocabDoc][]" accept="application/pdf" multiple />
                                                </div>
                                                <%-- Repeat for Grammar and Kanji videos/docs --%>
                                                <div class="mb-2">
                                                    <label class="form-label">Video Ngữ Pháp</label>
                                                    <c:if test="${not empty lesson.grammarVideos}">
                                                        <ul class="list-group list-group-flush mb-2">
                                                            <c:forEach var="video" items="${lesson.grammarVideos}" varStatus="vLoop">
                                                                <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                                    <a href="${video.path}" target="_blank">${video.name}</a>
                                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete-file" data-file-type="grammarVideo" data-lesson-index="${loop.index}" data-file-id="${video.id}">
                                                                        <i class="fas fa-times"></i>
                                                                    </button>
                                                                    <input type="hidden" name="lessons[${loop.index}][existingGrammarVideos][${vLoop.index}][id]" value="${video.id}" />
                                                                    <input type="hidden" name="lessons[${loop.index}][existingGrammarVideos][${vLoop.index}][path]" value="${video.path}" />
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:if>
                                                    <input type="file" class="form-control" name="lessons[${loop.index}][newGrammarVideo][]" accept="video/*" multiple />
                                                </div>
                                                <div class="mb-2">
                                                    <label class="form-label">Tài Liệu Ngữ Pháp (PDF)</label>
                                                    <c:if test="${not empty lesson.grammarDocs}">
                                                        <ul class="list-group list-group-flush mb-2">
                                                            <c:forEach var="doc" items="${lesson.grammarDocs}" varStatus="dLoop">
                                                                <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                                    <a href="${doc.path}" target="_blank">${doc.name}</a>
                                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete-file" data-file-type="grammarDoc" data-lesson-index="${loop.index}" data-file-id="${doc.id}">
                                                                        <i class="fas fa-times"></i>
                                                                    </button>
                                                                    <input type="hidden" name="lessons[${loop.index}][existingGrammarDocs][${dLoop.index}][id]" value="${doc.id}" />
                                                                    <input type="hidden" name="lessons[${loop.index}][existingGrammarDocs][${dLoop.index}][path]" value="${doc.path}" />
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:if>
                                                    <input type="file" class="form-control" name="lessons[${loop.index}][newGrammarDoc][]" accept="application/pdf" multiple />
                                                </div>
                                                <div class="mb-2">
                                                    <label class="form-label">Video Kanji</label>
                                                    <c:if test="${not empty lesson.kanjiVideos}">
                                                        <ul class="list-group list-group-flush mb-2">
                                                            <c:forEach var="video" items="${lesson.kanjiVideos}" varStatus="vLoop">
                                                                <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                                    <a href="${video.path}" target="_blank">${video.name}</a>
                                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete-file" data-file-type="kanjiVideo" data-lesson-index="${loop.index}" data-file-id="${video.id}">
                                                                        <i class="fas fa-times"></i>
                                                                    </button>
                                                                    <input type="hidden" name="lessons[${loop.index}][existingKanjiVideos][${vLoop.index}][id]" value="${video.id}" />
                                                                    <input type="hidden" name="lessons[${loop.index}][existingKanjiVideos][${vLoop.index}][path]" value="${video.path}" />
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:if>
                                                    <input type="file" class="form-control" name="lessons[${loop.index}][newKanjiVideo][]" accept="video/*" multiple />
                                                </div>
                                                <div class="mb-2">
                                                    <label class="form-label">Tài Liệu Kanji (PDF)</label>
                                                    <c:if test="${not empty lesson.kanjiDocs}">
                                                        <ul class="list-group list-group-flush mb-2">
                                                            <c:forEach var="doc" items="${lesson.kanjiDocs}" varStatus="dLoop">
                                                                <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                                    <a href="${doc.path}" target="_blank">${doc.name}</a>
                                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete-file" data-file-type="kanjiDoc" data-lesson-index="${loop.index}" data-file-id="${doc.id}">
                                                                        <i class="fas fa-times"></i>
                                                                    </button>
                                                                    <input type="hidden" name="lessons[${loop.index}][existingKanjiDocs][${dLoop.index}][id]" value="${doc.id}" />
                                                                    <input type="hidden" name="lessons[${loop.index}][existingKanjiDocs][${dLoop.index}][path]" value="${doc.path}" />
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:if>
                                                    <input type="file" class="form-control" name="lessons[${loop.index}][newKanjiDoc][]" accept="application/pdf" multiple />
                                                </div>

                                                <div class="d-flex gap-2 mt-2">
                                                    <button type="button" class="btn btn-outline-success btn-save-lesson">Lưu Lesson</button>
                                                    <button type="button" class="btn btn-outline-info btn-toggle-quiz"
                                                            data-bs-toggle="modal" data-bs-target="#quizModal" data-lesson-index="${loop.index}">Tạo Quiz</button>
                                                    <button type="button" class="btn btn-outline-danger btn-delete-lesson"><i
                                                            class="fas fa-trash"></i> Xoá Lesson</button>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <%-- Default Lesson 1 if no lessons exist --%>
                                    <div class="tab-pane fade show active" id="lesson-0" role="tabpanel">
                                        <div class="lesson-block course-card border p-3 rounded mb-3" data-lesson-index="0">
                                            <h6 class="fw-semibold mb-3">Lesson 1</h6>
                                            <div class="mb-2">
                                                <label class="form-label">Tên Bài Học</label>
                                                <input type="text" class="form-control" name="lessons[0][name]" required />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Mô tả bài học</label>
                                                <textarea class="form-control" name="lessons[0][desc]" rows="2"></textarea>
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Video Từ Vựng</label>
                                                <input type="file" class="form-control" name="lessons[0][newVocabVideo][]"
                                                       accept="video/*" multiple />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Tài Liệu Từ Vựng (PDF)</label>
                                                <input type="file" class="form-control" name="lessons[0][newVocabDoc][]"
                                                       accept="application/pdf" multiple />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Video Ngữ Pháp</label>
                                                <input type="file" class="form-control" name="lessons[0][newGrammarVideo][]"
                                                       accept="video/*" multiple />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Tài Liệu Ngữ Pháp (PDF)</label>
                                                <input type="file" class="form-control" name="lessons[0][newGrammarDoc][]"
                                                       accept="application/pdf" multiple />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Video Kanji</label>
                                                <input type="file" class="form-control" name="lessons[0][newKanjiVideo][]"
                                                       accept="video/*" multiple />
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Tài Liệu Kanji (PDF)</label>
                                                <input type="file" class="form-control" name="lessons[0][newKanjiDoc][]"
                                                       accept="application/pdf" multiple />
                                            </div>
                                            <div class="d-flex gap-2 mt-2">
                                                <button type="button" class="btn btn-outline-success btn-save-lesson">Lưu
                                                    Lesson</button>
                                                <button type="button" class="btn btn-outline-info btn-toggle-quiz"
                                                        data-bs-toggle="modal" data-bs-target="#quizModal" data-lesson-index="0">Tạo Quiz</button>
                                            </div>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
                <div class="d-flex justify-content-end mt-4">
                    <button type="submit" class="btn btn-danger btn-lg">Cập Nhật Khóa Học</button> <%-- Added main submit button --%>
                </div>
            </form>
        </section>

        <template id="lessonTemplate">
            <%-- This template remains largely the same for adding *new* lessons dynamically,
                 but ensure the file input names align (e.g., 'newVocabVideo[]') if you differentiated --%>
            <div class="tab-pane fade" id="lesson-{{index}}" role="tabpanel">
                <div class="lesson-block course-card border p-3 rounded mb-3" data-lesson-index="{{index}}">
                    <h6 class="fw-semibold mb-3">Lesson {{indexLabel}}</h6>
                    <input type="hidden" name="lessons[{{index}}][id]" value="" /> <%-- New lessons won't have an ID initially --%>
                    <div class="mb-2">
                        <label class="form-label">Tên Bài Học</label>
                        <input type="text" class="form-control" name="lessons[{{index}}][name]" required />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Mô tả bài học</label>
                        <textarea class="form-control" name="lessons[{{index}}][desc]" rows="2"></textarea>
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Video Từ Vựng</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][newVocabVideo][]" accept="video/*"
                               multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Tài Liệu Từ Vựng (PDF)</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][newVocabDoc][]"
                               accept="application/pdf" multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Video Ngữ Pháp</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][newGrammarVideo][]" accept="video/*"
                               multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Tài Liệu Ngữ Pháp (PDF)</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][newGrammarDoc][]"
                               accept="application/pdf" multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Video Kanji</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][newKanjiVideo][]" accept="video/*"
                               multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Tài Liệu Kanji (PDF)</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][newKanjiDoc][]"
                               accept="application/pdf" multiple />
                    </div>
                    <div class="d-flex gap-2 mt-2">
                        <button type="button" class="btn btn-outline-success btn-save-lesson">Lưu Lesson</button>
                        <button type="button" class="btn btn-outline-info btn-toggle-quiz" data-bs-toggle="modal"
                                data-bs-target="#quizModal">Tạo Quiz</button>
                        <button type="button" class="btn btn-outline-danger btn-delete-lesson"><i
                                class="fas fa-trash"></i> Xoá Lesson</button>
                    </div>
                </div>
            </div>
        </template>


        <div class="modal fade" id="quizModal" tabindex="-1" aria-labelledby="quizModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content rounded-4 shadow-sm">
                    <div class="modal-header bg-danger text-white rounded-top-4">
                        <h5 class="modal-title fw-bold" id="quizModalLabel">Tạo Quiz</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body p-4">
                        <form id="quizForm">
                            <input type="hidden" id="currentLessonQuizIndex" value="" /> <%-- To track which lesson's quiz is being edited --%>
                            <div id="quizQuestionsContainer">
                                <%-- Quiz questions will be dynamically loaded/added here by JavaScript --%>
                            </div>
                            <button type="button" class="btn btn-outline-primary mb-3" id="addQuestionBtn">
                                <i class="fas fa-plus"></i> Thêm Câu Hỏi
                            </button>
                            <br>
                            <button type="submit" class="btn btn-danger">Lưu Quiz</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <template id="quizQuestionTemplate">
            <div class="quiz-question-block mb-4 p-3 border rounded">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h6 class="mb-0">Câu hỏi {{questionLabel}}</h6>
                    <button class="btn btn-link p-0 text-decoration-none quiz-collapse-toggle" type="button"
                            data-bs-toggle="collapse" data-bs-target="#questionCollapse-{{index}}" aria-expanded="false"
                            aria-controls="questionCollapse-{{index}}">
                        <i class="fas fa-chevron-down"></i>
                    </button>
                </div>
                <div class="collapse" id="questionCollapse-{{index}}">
                    <input type="hidden" name="questions[{{index}}][id]" value="" /> <%-- Quiz question ID --%>
                    <div class="mb-3">
                        <label class="form-label">Nội dung câu hỏi</label>
                        <input type="text" class="form-control" name="questions[{{index}}][question]" required>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6 mb-2">
                            <label class="form-label">Lựa chọn A</label>
                            <input type="text" class="form-control" name="questions[{{index}}][optionA]" required>
                        </div>
                        <div class="col-md-6 mb-2">
                            <label class="form-label">Lựa chọn B</label>
                            <input type="text" class="form-control" name="questions[{{index}}][optionB]" required>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6 mb-2">
                            <label class="form-label">Lựa chọn C</label>
                            <input type="text" class="form-control" name="questions[{{index}}][optionC]" required>
                        </div>
                        <div class="col-md-6 mb-2">
                            <label class="form-label">Lựa chọn D</label>
                            <input type="text" class="form-control" name="questions[{{index}}][optionD]" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Đáp án đúng</label>
                        <select class="form-select" name="questions[{{index}}][answer]" required>
                            <option value="">-- Chọn đáp án --</option>
                            <option value="A">A</option>
                            <option value="B">B</option>
                            <option value="C">C</option>
                            <option value="D">D</option>
                        </select>
                    </div>
                    <button type="button" class="btn btn-outline-danger btn-sm btn-delete-question"><i
                            class="fas fa-trash"></i> Xoá Câu Hỏi</button>
                </div>
            </div>
        </template>

        <%@ include file="Home/footer.jsp" %>


        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <%-- Load existing course and lesson data into a JS object --%>
        <script>
// This JavaScript variable will hold all your lesson and quiz data passed from the server
// Ensure your Java servlet populates this `allCourseData` object
var allCourseData = {
lessons: [
                <c:forEach var="lesson" items="${lessons}" varStatus="loop">
{
id: '${lesson.id}',
        name: '<c:out value="${lesson.name}" />',
        description: '<c:out value="${lesson.description}" />',
        vocabVideos: [
                <c:forEach var="video" items="${lesson.vocabVideos}">
        { id: '${video.id}', name: '<c:out value="${video.name}" />', path: '<c:out value="${video.path}" />' },
                </c:forEach>
        ],
        vocabDocs: [
                <c:forEach var="doc" items="${lesson.vocabDocs}">
        { id: '${doc.id}', name: '<c:out value="${doc.name}" />', path: '<c:out value="${doc.path}" />' },
                </c:forEach>
        ],
        grammarVideos: [
                <c:forEach var="video" items="${lesson.grammarVideos}">
        { id: '${video.id}', name: '<c:out value="${video.name}" />', path: '<c:out value="${video.path}" />' },
                </c:forEach>
        ],
        grammarDocs: [
                <c:forEach var="doc" items="${lesson.grammarDocs}">
        { id: '${doc.id}', name: '<c:out value="${doc.name}" />', path: '<c:out value="${doc.path}" />' },
                </c:forEach>
        ],
        kanjiVideos: [
                <c:forEach var="video" items="${lesson.kanjiVideos}">
        { id: '${video.id}', name: '<c:out value="${video.name}" />', path: '<c:out value="${video.path}" />' },
                </c:forEach>
        ],
        kanjiDocs: [
                <c:forEach var="doc" items="${lesson.kanjiDocs}">
        { id: '${doc.id}', name: '<c:out value="${doc.name}" />', path: '<c:out value="${doc.path}" />' },
                </c:forEach>
        ],
        quizzes: [ <%-- Assuming quizzes are part of lesson object or fetched per lesson --%>
                <c:forEach var="quizQuestion" items="${lesson.quizQuestions}"> <%-- Assuming lesson.quizQuestions exists --%>
        {
        id: '${quizQuestion.id}',
                question: '<c:out value="${quizQuestion.question}" />',
                optionA: '<c:out value="${quizQuestion.optionA}" />',
                optionB: '<c:out value="${quizQuestion.optionB}" />',
                optionC: '<c:out value="${quizQuestion.optionC}" />',
                optionD: '<c:out value="${quizQuestion.optionD}" />',
                answer: '<c:out value="${quizQuestion.answer}" />'
        },
                </c:forEach>
        ]
},
            </c:forEach>
]
};
        </script>
        <script src="<c:url value='/js/update_course.js'/>"></script> <%-- Renamed JS for clarity --%>
    </body>
</html>