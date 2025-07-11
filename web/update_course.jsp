<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Chỉnh Sửa Khóa Học - Wasabii</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"/>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;700&display=swap"/>
        <link rel="stylesheet" href="<c:url value='/css/indexstyle.css'/>"/>
        <link rel="stylesheet" href="<c:url value='/css/update_course.css'/>"/>
        <style>
            body {
                font-family: "JetBrains Mono", monospace;
            }
            .nav-tabs-vertical .nav-link {
                font-size: 1.15rem;
                font-weight: bold;
            }
            .nav-tabs-vertical .nav-link.active {
                background: #e94f64;
                color: #fff;
            }
            .lesson-block input, .lesson-block textarea, .lesson-block select {
                font-size: 1.07rem;
            }
            .btn-outline-primary, .btn-outline-success, .btn-outline-danger, .btn-outline-info {
                font-weight: 600;
            }
            .modal-header {
                background: #e94f64;
                color: #fff;
            }
            .modal-title {
                font-weight: bold;
                font-size: 1.4rem;
            }
            .quiz-question-block {
                background: #fff;
                border: 1.5px solid #e94f64;
                margin-bottom: 18px;
                border-radius: 13px;
            }
            .quiz-question-block h6 {
                color: #e94f64;
                font-weight: 700;
                font-size: 1.15rem;
            }
            .btn-delete-question, .btn-delete-lesson {
                font-weight: 600;
            }
            .btn-add-question {
                font-weight: 600;
            }
            .quiz-question-list {
                max-height: 55vh;         /* Chiều cao tối đa của vùng câu hỏi, có thể chỉnh 60vh */
                overflow-y: auto;
                padding-right: 10px;
            }
            .quiz-question-list::-webkit-scrollbar {
                width: 8px;
                background: #f3f3f3;
                border-radius: 6px;
            }
            .quiz-question-list::-webkit-scrollbar-thumb {
                background: #e94f64;
                border-radius: 6px;
            }
            /* Đẹp hơn cho từng block */
            .quiz-question-block {
                background: #fff;
                box-shadow: 0 4px 10px rgba(0,0,0,0.06);
            }
        </style>
    </head>
    <body>
        <%@ include file="/Home/nav.jsp" %>
        <div class="update-course-page">
            <section class="container py-5">
                <h2 class="text-danger fw-bold text-center mb-4">Chỉnh Sửa Khóa Học</h2>
                <form id="wizardForm" method="post" action="EditCourseServlet" enctype="multipart/form-data" class="bg-white p-4 rounded-4 shadow-sm">
                    <input type="hidden" name="courseId" value="${course.courseID}" />
                    <!-- THÔNG TIN CHUNG -->
                    <div class="mb-4 border rounded p-3 bg-light">
                        <h5 class="fw-bold text-primary mb-3">Thông tin chung</h5>
                        <div class="mb-3">
                            <label class="form-label">Tên khóa học <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="courseTitle" value="${course.title}" required/>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mô tả khóa học</label>
                            <textarea class="form-control" name="courseDescription" rows="3">${course.description}</textarea>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="isHidden" name="isHidden" <c:if test="${course.hidden}">checked</c:if>/>
                                <label class="form-check-label" for="isHidden">Ẩn khóa học (chỉ admin thấy)</label>
                            </div>
                            <div class="form-check mt-2">
                                <input class="form-check-input" type="checkbox" id="isSuggested" name="isSuggested" <c:if test="${course.suggested}">checked</c:if>/>
                                <label class="form-check-label" for="isSuggested">Đánh dấu là khóa học nổi bật</label>
                            </div>
                        </div>
                        <!-- LESSON TABS -->
                        <div class="row">
                            <div class="col-md-3">
                                <ul class="nav nav-tabs nav-tabs-vertical" id="lessonTabList" role="tablist">
                                <c:forEach var="lesson" items="${lessons}" varStatus="loop">
                                    <li class="nav-item">
                                        <a class="nav-link <c:if test='${loop.first}'>active</c:if>'" id="tab-${loop.index}" data-bs-toggle="tab" href="#lesson-${loop.index}" role="tab">
                                            Lesson ${loop.index + 1}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                            <button type="button" class="btn btn-outline-primary mt-3 w-100" id="addLessonBtn">
                                <i class="fa-solid fa-plus"></i> Thêm Lesson
                            </button>
                        </div>
                        <div class="col-md-9">
                            <div class="tab-content" id="lessonTabContent">
                                <c:forEach var="lesson" items="${lessons}" varStatus="loop">
                                    <div class="tab-pane fade <c:if test='${loop.first}'>show active</c:if>'" id="lesson-${loop.index}" role="tabpanel" data-lesson-index="${loop.index}">
                                        <div class="lesson-block border p-3 rounded mb-3" data-lesson-index="${loop.index}">
                                            <input type="hidden" name="lessons[${loop.index}][id]" value="${lesson.lessonID}" />
                                            <h6 class="fw-semibold mb-3">Lesson ${loop.index + 1}</h6>
                                            <div class="mb-2">
                                                <label class="form-label">Tên Bài Học</label>
                                                <input type="text" class="form-control" name="lessons[${loop.index}][name]" value="${lesson.title}" required/>
                                            </div>
                                            <div class="mb-2">
                                                <label class="form-label">Mô tả bài học</label>
                                                <textarea class="form-control" name="lessons[${loop.index}][desc]" rows="2">${lesson.description}</textarea>
                                            </div>
                                            <!-- FILE -->
                                            <c:set var="materials" value="${materialsMap[lesson.lessonID]}" />
                                            <c:forEach var="type" items="${['vocabVideo','vocabDoc','grammarVideo','grammarDoc','kanjiVideo','kanjiDoc']}">
                                                <div class="mb-2">
                                                    <label class="form-label">
                                                        <c:choose>
                                                            <c:when test="${type == 'vocabDoc'}">Tài liệu Từ Vựng (PDF)</c:when>
                                                            <c:when test="${type == 'vocabVideo'}">Video Từ Vựng</c:when>
                                                            <c:when test="${type == 'grammarDoc'}">Tài liệu Ngữ Pháp (PDF)</c:when>
                                                            <c:when test="${type == 'grammarVideo'}">Video Ngữ Pháp</c:when>
                                                            <c:when test="${type == 'kanjiDoc'}">Tài liệu Kanji (PDF)</c:when>
                                                            <c:otherwise>Video Kanji</c:otherwise>
                                                        </c:choose>
                                                    </label>
                                                    <ul class="list-group list-group-flush mb-2">
                                                        <c:forEach var="mat" items="${materials}">
                                                            <c:if test="${mat.materialType == type}">
                                                                <li class="list-group-item d-flex justify-content-between align-items-center py-1">
                                                                    <a href="${mat.filePath}" target="_blank">${mat.title}</a>
                                                                    <button type="button" class="btn btn-sm btn-outline-danger btn-delete-file"
                                                                            data-material-id="${mat.materialID}" data-lesson-index="${loop.index}">
                                                                        <i class="fa-solid fa-times"></i>
                                                                    </button>
                                                                    <input type="hidden" name="lessons[${loop.index}][existingMaterials][${mat.materialID}][id]" value="${mat.materialID}" />
                                                                </li>
                                                            </c:if>
                                                        </c:forEach>
                                                    </ul>
                                                    <input type="file" class="form-control" name="lessons[${loop.index}][${type}][]"
                                                           <c:if test="${type.endsWith('Video')}">accept="video/*"</c:if>
                                                           <c:if test="${type.endsWith('Doc')}">accept="application/pdf"</c:if>
                                                               multiple/>
                                                    </div>
                                            </c:forEach>
                                            <!-- QUIZ -->
                                            <div class="d-flex gap-2 mt-2">
                                                <button type="button" class="btn btn-outline-success btn-save-lesson">Lưu Lesson</button>
                                                <button type="button" class="btn btn-outline-info btn-toggle-quiz" data-bs-toggle="modal" data-bs-target="#quizModal" data-lesson-index="${loop.index}">
                                                    Tạo Quiz
                                                </button>
                                                <button type="button" class="btn btn-outline-danger btn-delete-lesson">
                                                    Xoá Lesson
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                    <div class="d-flex justify-content-end mt-4">
                        <button type="submit" class="btn btn-danger btn-lg">Cập Nhật Khóa Học</button>
                    </div>
                </form>
            </section>
        </div>

        <!-- LESSON TEMPLATE -->
        <template id="lessonTemplate">
            <div class="tab-pane fade" id="lesson-{{index}}" role="tabpanel" data-lesson-index="{{index}}">
                <div class="lesson-block border p-3 rounded mb-3" data-lesson-index="{{index}}">
                    <input type="hidden" name="lessons[{{index}}][id]" value="" />
                    <h6 class="fw-semibold mb-3">Lesson {{indexLabel}}</h6>
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
                        <input type="file" class="form-control" name="lessons[{{index}}][vocabVideo][]" accept="video/*" multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Tài Liệu Từ Vựng</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][vocabDoc][]" accept="application/pdf" multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Video Ngữ Pháp</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][grammarVideo][]" accept="video/*" multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Tài Liệu Ngữ Pháp</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][grammarDoc][]" accept="application/pdf" multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Video Kanji</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][kanjiVideo][]" accept="video/*" multiple />
                    </div>
                    <div class="mb-2">
                        <label class="form-label">Tài Liệu Kanji</label>
                        <input type="file" class="form-control" name="lessons[{{index}}][kanjiDoc][]" accept="application/pdf" multiple />
                    </div>
                    <div class="d-flex gap-2 mt-2">
                        <button type="button" class="btn btn-outline-success btn-save-lesson">Lưu Lesson</button>
                        <button type="button" class="btn btn-outline-info btn-toggle-quiz" data-bs-toggle="modal" data-bs-target="#quizModal" data-lesson-index="{{index}}">
                            Tạo Quiz
                        </button>
                        <button type="button" class="btn btn-outline-danger btn-delete-lesson">Xoá Lesson</button>
                    </div>
                    <div class="quiz-data-block"></div>
                </div>
            </div>
        </template>

        <!-- Modal chứa Quiz -->
        <div class="modal fade" id="quizModal" tabindex="-1" aria-labelledby="quizModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <form id="quizForm">
                        <div class="modal-header">
                            <h5 class="modal-title" id="quizModalLabel">Tạo Quiz</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="quiz-question-list" id="quizQuestionsContainer">
                                <!-- Các quiz-question-block sẽ render ở đây -->
                            </div>
                            <button type="button" class="btn btn-outline-primary mt-3" id="addQuestionBtn">
                                <i class="fas fa-plus"></i> Thêm câu hỏi
                            </button>
                        </div>

                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                            <button type="submit" class="btn btn-success">Lưu Quiz</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- TEMPLATE CÂU HỎI -->
        <template id="quizQuestionTemplate">
            <div class="quiz-question-block mb-4 p-3 border rounded">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h6 class="mb-0">Câu hỏi {{questionLabel}}</h6>
                    <div class="d-flex gap-2">
                        <button class="btn btn-link p-0 text-decoration-none quiz-collapse-toggle" type="button"
                                data-bs-toggle="collapse" data-bs-target="#questionCollapse-{{index}}" aria-expanded="true"
                                aria-controls="questionCollapse-{{index}}">
                            <i class="fas fa-chevron-down"></i>
                        </button>
                        <button type="button" class="btn btn-outline-danger btn-sm btn-delete-question">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>
                <div class="collapse show" id="questionCollapse-{{index}}">
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
                </div>
            </div>
        </template>
        <!-- Form chính của wizard (cần có id="wizardForm") -->
        <form id="wizardForm" action="EditCourseServlet" method="post" enctype="multipart/form-data">
            <!-- Các trường khác của form -->
            <input type="hidden" name="courseId" value="${course.courseID}">

            <!-- Trường ẩn để chứa quiz JSON -->
            <input type="hidden" id="quizJsonInput" name="quizJson" value="">

            <!-- Nội dung form khác... -->

            <button type="submit" class="btn btn-primary">Lưu Khóa Học</button>
        </form>
        <%@ include file="/Home/footer.jsp" %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/js/all.min.js"></script>

        <!-- Initialize course data for JavaScript -->
        <script>
// Initialize allCourseData with existing course data from server
var allCourseData = ${quizDataJson};

// Add course metadata
allCourseData.courseId = ${course.courseID};
allCourseData.title = "${course.title}";
allCourseData.description = "${course.description}";
allCourseData.hidden = ${course.hidden};
allCourseData.suggested = ${course.suggested};
        </script>
<script>
    var contextPath = "${pageContext.request.contextPath}";
    window.lessonIndexToIdMap = {
        <c:forEach var="lesson" items="${lessons}" varStatus="loop">
            "${loop.index}": ${lesson.lessonID}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    };
</script>

        <script src="<c:url value='/js/edit_course.js'/>"></script> <%-- JS động như bên create, chỉ sửa tên --%>
    </body>
</html>
