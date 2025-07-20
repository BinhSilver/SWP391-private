<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Tạo Khóa Học Mới - Wasabii</title>

        <!-- CSS & Font -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" />
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap" />
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>" />
        <link rel="stylesheet" href="<c:url value='/css/create_course.css'/>" />
    </head>

    <body>
        <%@ include file="/Home/nav.jsp" %>

        <section class="container py-5">
            <h2 class="text-danger fw-bold text-center mb-4">Tạo Khóa Học Mới</h2>
            <form id="wizardForm" method="post" action="CreateCourseServlet" enctype="multipart/form-data"
                  class="bg-white p-4 rounded-4 shadow-sm">

                <!-- THÔNG TIN CHUNG -->
                <div class="mb-4 border rounded p-3 bg-light">
                    <h5 class="fw-bold text-primary mb-3">Thông tin chung</h5>

                    <div class="mb-3">
                        <label for="courseTitle" class="form-label">Tên khóa học <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="courseTitle" name="courseTitle" required />
                    </div>

                    <div class="mb-3">
                        <label for="courseDescription" class="form-label">Mô tả khóa học</label>
                        <textarea class="form-control" id="courseDescription" name="courseDescription" rows="3"></textarea>
                    </div>

                    <div class="mb-3">
                        <label for="thumbnailFile" class="form-label">Thumbnail khóa học</label>
                        <input type="file" class="form-control" id="thumbnailFile" name="thumbnailFile" accept="image/*" />
                    </div>

                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="isHidden" name="isHidden" />
                        <label class="form-check-label" for="isHidden">Ẩn khóa học (chỉ admin thấy)</label>
                    </div>
                    <c:if test="${sessionScope.authUser != null && sessionScope.authUser.roleID == 4}">
                        <div class="form-check mt-2">
                            <input class="form-check-input" type="checkbox" id="isSuggested" name="isSuggested" />
                            <label class="form-check-label" for="isSuggested">Đánh dấu là khóa học nổi bật</label>
                        </div>
                    </c:if>
                </div>

                <!-- LESSONS -->
                <div class="row">
                    <div class="col-md-3">
                        <ul class="nav nav-tabs nav-tabs-vertical" id="lessonTabList" role="tablist">
                            <li class="nav-item">
                                <a class="nav-link active" id="tab-0" data-bs-toggle="tab" href="#lesson-0" role="tab">Lesson 1</a>
                            </li>
                        </ul>
                        <button type="button" class="btn btn-outline-primary mt-3 w-100" id="addLessonBtn">
                            <i class="fas fa-plus"></i> Thêm Lesson
                        </button>
                    </div>

                    <div class="col-md-9">
                        <div class="tab-content" id="lessonTabContent">
                            <div class="tab-pane fade show active" id="lesson-0" role="tabpanel" data-lesson-index="0">
                                <div class="lesson-block course-card border p-3 rounded mb-3" data-lesson-index="0">
                                    <h6 class="fw-semibold mb-3">Lesson 1</h6>
                                    <div class="mb-2">
                                        <label class="form-label">Tên Bài Học</label>
                                        <input type="text" class="form-control" name="lessons[0][name]" required />
                                    </div>
                                    <div class="mb-2">
                                        <label class="form-label">Mô tả bài học</label>
                                        <textarea class="form-control" name="lessons[0][description]" rows="2"></textarea>
                                    </div>
                                    <!-- Vocab -->
                                    <div class="mb-2 vocab-entry-container" data-lesson-index="0">
                                        <label class="form-label">Từ Vựng (Word:Meaning:Reading:Example)</label>
                                        <div class="input-group mb-2">
                                            <input type="text" class="form-control vocab-text" name="lessons[0][vocabText][0]" placeholder="Word:Meaning:Reading:Example" />
                                            <input type="file" class="form-control vocab-image" name="lessons[0][vocabImage][0]" accept="image/*" />
                                            <button type="button" class="btn btn-outline-success btn-add-vocab ms-2">+</button>
                                            <button type="button" class="btn btn-outline-danger btn-remove-vocab ms-1">-</button>
                                        </div>
                                    </div>
                                    <div class="mb-2">
                                        <label class="form-label">Tài Liệu Từ Vựng (PDF)</label>
                                        <input type="file" class="form-control" name="lessons[0][vocabDoc][]" accept="application/pdf" multiple />
                                    </div>
                                    <!-- Grammar -->
                                    <div class="mb-2">
                                        <label class="form-label">Video Ngữ Pháp</label>
                                        <input type="file" class="form-control" name="lessons[0][grammarVideo][]" accept="video/*" multiple />
                                    </div>
                                    <div class="mb-2">
                                        <label class="form-label">Tài Liệu Ngữ Pháp</label>
                                        <input type="file" class="form-control" name="lessons[0][grammarDoc][]" accept="application/pdf" multiple />
                                    </div>
                                    <div class="mb-2">
                                        <label class="form-label">Tài Liệu Kanji</label>
                                        <input type="file" class="form-control" name="lessons[0][kanjiDoc][]" accept="application/pdf" multiple />
                                    </div>
                                    <div class="d-flex gap-2 mt-2">
                                        <button type="button" class="btn btn-outline-success btn-save-lesson">Lưu Lesson</button>
                                        <button type="button" class="btn btn-outline-info btn-toggle-quiz" data-bs-toggle="modal" data-bs-target="#quizModal" data-lesson-index="0">Tạo Quiz</button>
                                        <button type="button" class="btn btn-outline-primary btn-generate-vocabulary" data-bs-toggle="modal" data-bs-target="#vocabularyModal" data-lesson-index="0">Tạo Vocabulary bằng AI</button>
                                        <button type="button" class="btn btn-outline-danger btn-delete-lesson">Xoá Lesson</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="text-end mt-4">
                    <button type="submit" class="btn btn-danger px-4 py-2">Hoàn tất & Tạo Khóa Học</button>
                </div>
            </form>
        </section>

        <!-- Vocabulary Modal -->
        <div class="modal fade" id="vocabularyModal" tabindex="-1" aria-labelledby="vocabularyModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <form id="vocabularyForm">
                        <div class="modal-header">
                            <h5 class="modal-title" id="vocabularyModalLabel">Tạo Vocabulary bằng AI</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                        </div>
                        <div class="modal-body">
                            <input type="hidden" id="vocabulary-lesson-id" name="lessonId">
                            <div class="mb-3">
                                <label for="vocabulary-input" class="form-label">Nhập văn bản để tạo vocabulary</label>
                                <textarea class="form-control" id="vocabulary-input" name="vocabularyInput" rows="5" placeholder="Nhập văn bản để AI tạo vocabulary" required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                            <button type="submit" class="btn btn-success">Tạo Vocabulary</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Quiz Template and Modal -->
        <template id="quizQuestionTemplate">
            <div class="quiz-question-block mb-4 p-3 border rounded">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h6 class="mb-0">Câu hỏi {{questionLabel}}</h6>
                    <button class="btn btn-link p-0 text-decoration-none quiz-collapse-toggle" type="button"
                            data-bs-toggle="collapse" data-bs-target="#questionCollapse-{{lessonIndex}}-{{questionIndex}}" aria-expanded="false"
                            aria-controls="questionCollapse-{{lessonIndex}}-{{questionIndex}}">
                        <i class="fas fa-chevron-down"></i>
                    </button>
                </div>
                <div class="collapse" id="questionCollapse-{{lessonIndex}}-{{questionIndex}}">
                    <div class="mb-3">
                        <label class="form-label">Nội dung câu hỏi</label>
                        <input type="text" class="form-control"
                               name="lessons[{{lessonIndex}}][questions][{{questionIndex}}][question]" required>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6 mb-2">
                            <label class="form-label">Lựa chọn A</label>
                            <input type="text" class="form-control"
                                   name="lessons[{{lessonIndex}}][questions][{{questionIndex}}][optionA]" required>
                        </div>
                        <div class="col-md-6 mb-2">
                            <label class="form-label">Lựa chọn B</label>
                            <input type="text" class="form-control"
                                   name="lessons[{{lessonIndex}}][questions][{{questionIndex}}][optionB]" required>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6 mb-2">
                            <label class="form-label">Lựa chọn C</label>
                            <input type="text" class="form-control"
                                   name="lessons[{{lessonIndex}}][questions][{{questionIndex}}][optionC]" required>
                        </div>
                        <div class="col-md-6 mb-2">
                            <label class="form-label">Lựa chọn D</label>
                            <input type="text" class="form-control"
                                   name="lessons[{{lessonIndex}}][questions][{{questionIndex}}][optionD]" required>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Đáp án đúng</label>
                        <select class="form-select"
                                name="lessons[{{lessonIndex}}][questions][{{questionIndex}}][answer]" required>
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

        <!-- MODAL QUIZ -->
        <div class="modal fade" id="quizModal" tabindex="-1" aria-labelledby="quizModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-scrollable">
                <div class="modal-content">
                    <form id="quizForm">
                        <div class="modal-header">
                            <h5 class="modal-title" id="quizModalLabel">Tạo Quiz</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                        </div>
                        <div class="modal-body">
                            <div class="quiz-time-setting border rounded-3 p-3 mb-3 bg-light d-flex align-items-center gap-3 flex-wrap">
                                <label for="quizTimeLimitInput" class="form-label fw-semibold mb-0 me-2" style="min-width: 160px;">Thời gian mỗi câu (giây):</label>
                                <input type="number" min="1" id="quizTimeLimitInput" class="form-control" style="width: 120px; max-width: 160px;" value="60" />
                                <button type="button" class="btn btn-outline-danger fw-semibold ms-2" id="setQuizTimeBtn">
                                    <i class="fas fa-clock me-1"></i> Áp dụng cho tất cả câu hỏi
                                </button>
                            </div>
                            <div id="quizQuestionsContainer"></div>
                            <button type="button" class="btn btn-outline-primary mt-3" id="addQuestionBtn">
                                <i class="fas fa-plus"></i> Thêm câu hỏi
                            </button>
                        </div>
                        <div class="modal-footer">
                            <button type="submit" class="btn btn-success">Lưu Quiz</button>
                        </div>
                        <div class="quiz-import-excel border rounded-3 p-3 mb-3 bg-light text-center d-flex flex-column align-items-center justify-content-center">
                            <label for="quizFile" class="form-label fw-semibold text-success mb-2" style="font-size:1.1rem;">
                                <i class="fas fa-file-excel me-2" style="color:#198754;font-size:1.5rem;"></i>Import Quiz từ Excel
                            </label>
                            <input type="file" class="form-control mb-2 w-auto" id="quizFile" accept=".xlsx, .xls" style="max-width:260px;">
                            <small class="form-text text-muted">Chỉ chấp nhận file Excel (.xlsx, .xls) theo định dạng mẫu.</small>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<c:url value='/js/create_course.js'/>"></script>

        <%@ include file="Home/footer.jsp" %>
    </body>
</html>

