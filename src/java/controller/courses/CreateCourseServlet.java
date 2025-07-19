package controller.courses;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import Dao.VocabularyDAO;
import model.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import config.S3Util;
import service.CourseFlashcardService;

@MultipartConfig
@WebServlet(name = "CreateCourseServlet", urlPatterns = {"/CreateCourseServlet"})
public class CreateCourseServlet extends HttpServlet {

    // Đường dẫn tuyệt đối cho tài liệu và thumbnail
    private static final String ABSOLUTE_UPLOAD_PATH = "D:\\SUM25_FPT\\SWP\\SWP391-private\\web\\files";
    // Đường dẫn tương đối trong thư mục webapp cho hình ảnh từ vựng
    private static final String VOCAB_IMAGE_PATH = "/imgvocab";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("create_course.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        System.out.println("===== PARAMETER MAP =====");
        request.getParameterMap().forEach((k, v) -> System.out.println(k + " = " + Arrays.toString(v)));
        System.out.println("=========================");

        try {
            User user = getCurrentUser(request);
            if (user == null) {
                System.out.println("[ERROR] Không có user đăng nhập, chuyển hướng về trang đăng nhập.");
                response.sendRedirect("login.jsp");
                return;
            }

            // Xử lý ảnh thumbnail khóa học
            String imageUrl = null;
            Part imagePart = request.getPart("thumbnailFile");
            if (imagePart != null && imagePart.getSize() > 0) {
                String fileName = getFileName(imagePart);
                if (!isValidImage(fileName)) {
                    throw new IllegalArgumentException("Thumbnail phải là file ảnh (jpg, jpeg, png, gif).");
                }
                System.out.println("[LOG] Nhận file thumbnail: " + fileName + ", size: " + imagePart.getSize());
                try (InputStream is = imagePart.getInputStream()) {
                    // Tạm thời chưa có courseId, upload vào temp, sau đó sẽ update lại key nếu cần
                    String tempKey = "course/temp/" + System.currentTimeMillis() + "_" + fileName;
                    imageUrl = S3Util.uploadFile(is, imagePart.getSize(), tempKey, imagePart.getContentType());
                    System.out.println("[LOG] Đã upload thumbnail lên S3: " + imageUrl);
                } catch (Exception ex) {
                    System.out.println("[ERROR] Upload thumbnail lên S3 thất bại: " + ex.getMessage());
                    ex.printStackTrace();
                    throw ex;
                }
            }

            // Lấy thông tin khóa học
            Course course = getCourseInfoFromRequest(request, user, imageUrl);
            course.setCreatedBy(user.getUserID());
            System.out.println("[LOG] Thông tin course: " + course);
            int courseId = saveCourseAndReturnId(course);

            // Nếu có thumbnail, đổi key về đúng courseId
            if (imageUrl != null) {
                String fileName = getFileName(request.getPart("thumbnailFile"));
                try (InputStream is = request.getPart("thumbnailFile").getInputStream()) {
                    String key = "course/" + courseId + "/" + fileName;
                    imageUrl = S3Util.uploadFile(is, request.getPart("thumbnailFile").getSize(), key, request.getPart("thumbnailFile").getContentType());
                    System.out.println("[LOG] Đã upload lại thumbnail vào folder đúng courseId: " + imageUrl);
                    // Update lại imageUrl cho course
                    CoursesDAO dao = new CoursesDAO();
                    course.setImageUrl(imageUrl);
                    dao.update(course);
                } catch (Exception ex) {
                    System.out.println("[ERROR] Upload lại thumbnail vào folder courseId thất bại: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

            int maxLesson = getMaxLessonIndex(request);
            System.out.println("[LOG] Số lượng lesson: " + (maxLesson + 1));

            handleAllLessons(request, courseId, maxLesson);

            // Tự động tạo flashcard cho khóa học sau khi đã có từ vựng
            try {
                CourseFlashcardService flashcardService = new CourseFlashcardService();
                int flashcardId = flashcardService.createFlashcardFromCourse(courseId, user.getUserID());
                System.out.println("[LOG] Đã tự động tạo flashcard cho khóa học " + courseId + ", flashcardId = " + flashcardId);
            } catch (Exception ex) {
                System.out.println("[ERROR] Không thể tự động tạo flashcard cho khóa học: " + ex.getMessage());
                ex.printStackTrace();
                // Không throw exception để tiếp tục quá trình tạo khóa học
            }

            response.sendRedirect("CourseDetailServlet?id=" + courseId);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("create_course.jsp").forward(request, response);
        }
    }

    private User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (User) session.getAttribute("authUser") : null;
    }

    private Course getCourseInfoFromRequest(HttpServletRequest request, User user, String imageUrl) {
        String title = request.getParameter("courseTitle");
        String description = request.getParameter("courseDescription");
        boolean isHidden = request.getParameter("isHidden") != null;
        boolean isSuggested = user != null && user.getRoleID() == 4 && request.getParameter("isSuggested") != null;

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setHidden(isHidden);
        course.setSuggested(isSuggested);
        course.setImageUrl(imageUrl);
        return course;
    }

    private int saveCourseAndReturnId(Course course) throws SQLException {
        CoursesDAO dao = new CoursesDAO();
        return dao.addAndReturnID(course);
    }

    private int getMaxLessonIndex(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();
        int maxLesson = -1;
        for (String key : paramMap.keySet()) {
            if (key.startsWith("lessons[")) {
                int idx1 = key.indexOf('[') + 1;
                int idx2 = key.indexOf(']', idx1);
                String idxStr = key.substring(idx1, idx2);
                try {
                    int idx = Integer.parseInt(idxStr);
                    if (idx > maxLesson) {
                        maxLesson = idx;
                    }
                } catch (Exception ignore) {
                }
            }
        }
        return maxLesson;
    }

    private void handleAllLessons(HttpServletRequest request, int courseId, int maxLesson)
            throws Exception {
        LessonsDAO lessonsDao = new LessonsDAO();
        LessonMaterialsDAO materialsDao = new LessonMaterialsDAO();
        VocabularyDAO vocabDao = new VocabularyDAO();

        for (int i = 0; i <= maxLesson; i++) {
            String title = request.getParameter("lessons[" + i + "][name]");
            String description = request.getParameter("lessons[" + i + "][description]");
            boolean isHidden = request.getParameter("lessons[" + i + "][isHidden]") != null;

            System.out.println("[LOG] Lesson " + i + ": title=" + title + ", description=" + description + ", isHidden=" + isHidden);

            Lesson lesson = new Lesson();
            lesson.setTitle(title);
            lesson.setDescription(description);
            lesson.setCourseID(courseId);
            lesson.setIsHidden(isHidden);

            int lessonId = lessonsDao.addAndReturnID(lesson);

            saveMaterialsForLesson(request, i, lessonId, courseId, materialsDao);
            saveVocabularyForLesson(request, i, lessonId, vocabDao);
            saveQuizForLesson(request, i, lessonId);
        }
    }

    private void saveMaterialsForLesson(HttpServletRequest request, int lessonIndex, int lessonId, int courseId, LessonMaterialsDAO materialsDao)
            throws Exception {
        String[] fieldTypes = {"vocabVideo", "vocabDoc", "grammarVideo", "grammarDoc", "kanjiVideo", "kanjiDoc"};
        Collection<Part> parts = request.getParts();

        for (Part part : parts) {
            for (String type : fieldTypes) {
                String fieldName = "lessons[" + lessonIndex + "][" + type + "][]";
                if (part.getName().equals(fieldName) && part.getSize() > 0) {
                    String originalName = part.getSubmittedFileName();
                    if (originalName == null || originalName.isEmpty()) {
                        continue;
                    }
                    // Kiểm tra định dạng tệp
                    if (type.endsWith("Video") && !isValidVideo(originalName)) {
                        throw new IllegalArgumentException("File video không hợp lệ: " + originalName + ". Chỉ chấp nhận mp4, avi, mov.");
                    }
                    if (type.endsWith("Doc") && !isValidPDF(originalName)) {
                        throw new IllegalArgumentException("File tài liệu không hợp lệ: " + originalName + ". Chỉ chấp nhận PDF.");
                    }

                    System.out.println("[LOG] Lesson " + lessonIndex + " nhận file: " + originalName + " (" + type + ") size: " + part.getSize());
                    String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
                    String savedFileName = "lesson" + lessonId + "_" + type + "_" + System.currentTimeMillis() + ext;
                    String s3Key = "course/" + courseId + "/lesson" + lessonId + "/" + savedFileName;
                    String fileUrl = null;
                    try (InputStream is = part.getInputStream()) {
                        fileUrl = S3Util.uploadFile(is, part.getSize(), s3Key, part.getContentType());
                        System.out.println("[LOG] Đã upload file tài liệu lên S3: " + fileUrl);
                    } catch (Exception ex) {
                        System.out.println("[ERROR] Upload file tài liệu lên S3 thất bại: " + ex.getMessage());
                        ex.printStackTrace();
                        throw ex;
                    }

                    String materialType = type.startsWith("vocab") ? "Từ Vựng"
                            : type.startsWith("grammar") ? "Ngữ pháp"
                            : type.startsWith("kanji") ? "Kanji" : "";
                    String fileType = type.endsWith("Video") ? "video" : "PDF";

                    LessonMaterial material = new LessonMaterial();
                    material.setLessonID(lessonId);
                    material.setMaterialType(materialType);
                    material.setFileType(fileType);
                    material.setFilePath(fileUrl);
                    materialsDao.add(material);
                }
            }
        }
    }

    private void saveVocabularyForLesson(HttpServletRequest request, int lessonIndex, int lessonId, VocabularyDAO vocabDao)
            throws Exception {
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String key : paramMap.keySet()) {
            if (key.startsWith("lessons[" + lessonIndex + "][vocabText]")) {
                int vocabIndex = Integer.parseInt(key.substring(key.lastIndexOf('[') + 1, key.lastIndexOf(']')));
                String vocabText = request.getParameter(key);
                if (vocabText != null && !vocabText.isEmpty()) {
                    String[] parts = vocabText.split(":");
                    if (parts.length == 4) {
                        Vocabulary vocab = new Vocabulary();
                        vocab.setWord(parts[0].trim());
                        vocab.setMeaning(parts[1].trim());
                        vocab.setReading(parts[2].trim());
                        vocab.setExample(parts[3].trim());
                        vocab.setLessonID(lessonId);

                        // Xử lý ảnh từ vựng
                        Part imagePart = request.getPart("lessons[" + lessonIndex + "][vocabImage][" + vocabIndex + "]");
                        if (imagePart != null && imagePart.getSize() > 0) {
                            String originalName = imagePart.getSubmittedFileName();
                            if (originalName != null && !originalName.isEmpty()) {
                                if (!isValidImage(originalName)) {
                                    throw new IllegalArgumentException("Hình ảnh từ vựng không hợp lệ: " + originalName + ". Chỉ chấp nhận jpg, jpeg, png, gif.");
                                }
                                String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
                                String savedFileName = vocab.getWord().replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ext;
                                String s3Key = "course/" + lessonId + "/vocab/" + savedFileName;
                                String imageUrl = null;
                                try (InputStream is = imagePart.getInputStream()) {
                                    imageUrl = S3Util.uploadFile(is, imagePart.getSize(), s3Key, imagePart.getContentType());
                                    System.out.println("[LOG] Đã upload hình ảnh từ vựng lên S3: " + imageUrl);
                                } catch (Exception ex) {
                                    System.out.println("[ERROR] Upload hình ảnh từ vựng lên S3 thất bại: " + ex.getMessage());
                                    ex.printStackTrace();
                                    throw ex;
                                }
                                vocab.setImagePath(imageUrl);
                            }
                        }

                        vocabDao.add(vocab);
                    } else {
                        throw new IllegalArgumentException("Định dạng từ vựng không đúng: " + vocabText + ". Yêu cầu Word:Meaning:Reading:Example");
                    }
                }
            }
        }
    }

    private void saveQuizForLesson(HttpServletRequest request, int lessonIndex, int lessonId) {
        List<QuizQuestion> questions = new ArrayList<>();
        Map<String, String[]> paramMap = request.getParameterMap();

        Set<Integer> questionIndexes = new HashSet<>();
        String questionPrefix = "lessons[" + lessonIndex + "][questions][";
        for (String key : paramMap.keySet()) {
            if (key.startsWith(questionPrefix) && key.endsWith("][question]")) {
                String sub = key.substring(questionPrefix.length(), key.length() - "][question]".length());
                try {
                    questionIndexes.add(Integer.parseInt(sub));
                } catch (Exception ignore) {
                }
            }
        }
        System.out.println("[DEBUG] Lesson " + lessonIndex + " có " + questionIndexes.size() + " câu hỏi quiz.");
        for (int qIdx : questionIndexes) {
            String base = "lessons[" + lessonIndex + "][questions][" + qIdx + "]";
            String questionText = request.getParameter(base + "[question]");
            String optionA = request.getParameter(base + "[optionA]");
            String optionB = request.getParameter(base + "[optionB]");
            String optionC = request.getParameter(base + "[optionC]");
            String optionD = request.getParameter(base + "[optionD]");
            String answer = request.getParameter(base + "[answer]");
            System.out.println("[DEBUG] Quiz lesson " + lessonIndex + " - Q" + qIdx + ": " + questionText + " | A=" + optionA + ", B=" + optionB + ", C=" + optionC + ", D=" + optionD + ", answer=" + answer);
            if (questionText == null || answer == null) {
                continue;
            }
            int correct = switch (answer) {
                case "B" -> 2;
                case "C" -> 3;
                case "D" -> 4;
                default -> 1;
            };

            List<Answer> answers = List.of(
                    new Answer(0, 0, optionA, 1, correct == 1 ? 1 : 0),
                    new Answer(0, 0, optionB, 2, correct == 2 ? 1 : 0),
                    new Answer(0, 0, optionC, 3, correct == 3 ? 1 : 0),
                    new Answer(0, 0, optionD, 4, correct == 4 ? 1 : 0)
            );
            QuizQuestion quizQuestion = new QuizQuestion();
            quizQuestion.setQuestion(questionText);
            quizQuestion.setTimeLimit(60);
            quizQuestion.setCorrectAnswer(correct);
            quizQuestion.setAnswers(answers);

            questions.add(quizQuestion);
        }
        if (!questions.isEmpty()) {
            System.out.println("[DEBUG] Đang lưu " + questions.size() + " câu quiz cho lessonId = " + lessonId);
            QuizDAO.saveQuestions(lessonId, questions);
        } else {
            System.out.println("[DEBUG] Không có câu hỏi nào được lưu cho lessonId = " + lessonId);
        }
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String s : contentDisp.split(";")) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }

    private void saveFile(Part part, String savePath) throws IOException {
        try (InputStream is = part.getInputStream();
             FileOutputStream os = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }

    private boolean isValidImage(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg") ||
               lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".gif");
    }

    private boolean isValidVideo(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".mp4") || lowerCaseFileName.endsWith(".avi") ||
               lowerCaseFileName.endsWith(".mov");
    }

    private boolean isValidPDF(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }
}
