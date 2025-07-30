package controller.courses;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import Dao.VocabularyDAO;
import model.Course;
import model.Lesson;
import model.LessonMaterial;
import model.QuizQuestion;
import model.Answer;
import model.Vocabulary;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.*;
import com.google.gson.Gson;
import service.QuizService;
import config.S3Util;

@WebServlet(name = "EditCourseServlet", urlPatterns = {"/EditCourseServlet"})
@MultipartConfig(maxFileSize = 100 * 1024 * 1024, fileSizeThreshold = 1 * 1024 * 1024)
public class EditCourseServlet extends HttpServlet {

    private static final String ABSOLUTE_UPLOAD_PATH = "D:\\SUM25_FPT\\SWP\\SWP391-private\\web\\files";
    private static final String VOCAB_IMAGE_PATH = "/imgvocab";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String courseIdStr = request.getParameter("courseId");
        if (courseIdStr == null) {
            courseIdStr = request.getParameter("id");
        }
        if (courseIdStr == null) {
            request.setAttribute("error", "Thiếu tham số courseId hoặc id!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        int courseId = Integer.parseInt(courseIdStr);

        // Kiểm tra quyền chỉnh sửa
        HttpSession session = request.getSession(false);
        model.User currentUser = (session != null) ? (model.User) session.getAttribute("authUser") : null;
        CoursesDAO cDao = new CoursesDAO();
        Course course = cDao.getCourseByID(courseId);
        if (currentUser == null || course == null || (currentUser.getRoleID() != 4 && course.getCreatedBy() != currentUser.getUserID())) {
            request.setAttribute("error", "Bạn không có quyền chỉnh sửa khóa học này!");
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }

        LessonsDAO lDao = new LessonsDAO();
        LessonMaterialsDAO mDao = new LessonMaterialsDAO();
        VocabularyDAO vDao = new VocabularyDAO();

        List<Lesson> lessons = lDao.getLessonsByCourseID(courseId);
        Map<Integer, List<LessonMaterial>> materialsMap = mDao.getAllMaterialsGroupedByLesson(courseId);
        Map<Integer, List<QuizQuestion>> quizMap = new HashMap<>();
        Map<Integer, List<Vocabulary>> vocabularyMap = new HashMap<>();
        for (Lesson lesson : lessons) {
            quizMap.put(lesson.getLessonID(), QuizDAO.getQuestionsWithAnswersByLessonId(lesson.getLessonID()));
            try {
                vocabularyMap.put(lesson.getLessonID(), vDao.getVocabularyByLessonId(lesson.getLessonID()));
            } catch (SQLException ex) {
                System.getLogger(EditCourseServlet.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }

        // Tạo JSON cho JavaScript
        Gson gson = new Gson();
        Map<String, Object> courseDataForJs = new HashMap<>();
        List<Map<String, Object>> lessonsForJs = new ArrayList<>();

        for (Lesson lesson : lessons) {
            Map<String, Object> lessonData = new HashMap<>();
            lessonData.put("id", lesson.getLessonID());
            lessonData.put("name", lesson.getTitle());
            lessonData.put("description", lesson.getDescription());
            lessonData.put("quizzes", new ArrayList<>());

            List<QuizQuestion> quizQuestions = quizMap.get(lesson.getLessonID());
            if (quizQuestions != null) {
                List<Map<String, Object>> quizzesForJs = new ArrayList<>();
                for (QuizQuestion question : quizQuestions) {
                    Map<String, Object> quizData = new HashMap<>();
                    quizData.put("id", question.getId());
                    // Patch: ensure question text is never null
                    String questionText = question.getQuestion();
                    if (questionText == null) questionText = "";
                    quizData.put("question", questionText);

                    String optionA = "", optionB = "", optionC = "", optionD = "";
                    for (Answer answer : question.getAnswers()) {
                        switch (answer.getAnswerNumber()) {
                            case 1 -> optionA = answer.getAnswerText();
                            case 2 -> optionB = answer.getAnswerText();
                            case 3 -> optionC = answer.getAnswerText();
                            case 4 -> optionD = answer.getAnswerText();
                        }
                    }
                    quizData.put("optionA", optionA);
                    quizData.put("optionB", optionB);
                    quizData.put("optionC", optionC);
                    quizData.put("optionD", optionD);

                    String correctAnswer = switch (question.getCorrectAnswer()) {
                        case 1 -> "A";
                        case 2 -> "B";
                        case 3 -> "C";
                        default -> "D";
                    };
                    quizData.put("answer", correctAnswer);

                    quizzesForJs.add(quizData);
                }
                lessonData.put("quizzes", quizzesForJs);
            }

            lessonsForJs.add(lessonData);
        }

        courseDataForJs.put("lessons", lessonsForJs);
        String quizDataJson = gson.toJson(courseDataForJs);

        request.setAttribute("course", course);
        request.setAttribute("lessons", lessons);
        request.setAttribute("materialsMap", materialsMap);
        request.setAttribute("quizMap", quizMap);
        request.setAttribute("vocabularyMap", vocabularyMap);
        request.setAttribute("quizDataJson", quizDataJson);

        request.getRequestDispatcher("update_course.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        System.out.println("===== EditCourseServlet doPost START =====");

        try {
            String action = request.getParameter("action");
            if ("saveLesson".equals(action)) {
                response.setContentType("application/json;charset=UTF-8");
                try {
                    int courseId = Integer.parseInt(request.getParameter("courseId"));
                    Lesson lesson = saveLesson(request, courseId);
                    response.getWriter().write("{" +
                        "\"success\":true," +
                        "\"lessonId\":" + lesson.getLessonID() +
                        "}");
                } catch (Exception ex) {
                    response.getWriter().write("{" +
                        "\"success\":false," +
                        "\"message\":\"" + ex.getMessage().replace("\"", "'") + "\"}");
                }
                return;
            }

            String courseIdRaw = request.getParameter("courseId");
            String title = request.getParameter("courseTitle");
            String description = request.getParameter("courseDescription");
            String isHiddenRaw = request.getParameter("isHidden");
            String isSuggestedRaw = request.getParameter("isSuggested");

            if (courseIdRaw == null || title == null) {
                request.setAttribute("error", "Thiếu dữ liệu đầu vào!");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            int courseId = Integer.parseInt(courseIdRaw);
            boolean isHidden = isHiddenRaw != null;
            boolean isSuggested = isSuggestedRaw != null;

            // Xử lý upload thumbnail mới
            String imageUrl = null;
            Part imagePart = request.getPart("thumbnailFile");
            if (imagePart != null && imagePart.getSize() > 0) {
                String fileName = getFileName(imagePart);
                if (!isValidImage(fileName)) {
                    throw new IllegalArgumentException("Thumbnail phải là file ảnh (jpg, jpeg, png, gif).");
                }
                try (InputStream is = imagePart.getInputStream()) {
                    String key = "course/" + courseId + "/" + fileName;
                    imageUrl = S3Util.uploadFile(is, imagePart.getSize(), key, imagePart.getContentType());
                    System.out.println("[LOG] Đã upload thumbnail mới lên S3: " + imageUrl);
                } catch (Exception ex) {
                    System.out.println("[ERROR] Upload thumbnail lên S3 thất bại: " + ex.getMessage());
                    ex.printStackTrace();
                    throw ex;
                }
            } else {
                CoursesDAO cDao = new CoursesDAO();
                Course existingCourse = cDao.getCourseByID(courseId);
                if (existingCourse != null) {
                    imageUrl = existingCourse.getImageUrl();
                }
            }

            Course course = new Course(courseId, title, description, isHidden, isSuggested, imageUrl);
            CoursesDAO cDao = new CoursesDAO();
            try {
                cDao.update(course);
            } catch (SQLException e) {
                System.out.println("[ERROR] Lỗi khi cập nhật khóa học: " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("error", "Lỗi khi cập nhật khóa học: " + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            LessonsDAO lDao = new LessonsDAO();
            LessonMaterialsDAO mDao = new LessonMaterialsDAO();
            VocabularyDAO vDao = new VocabularyDAO();

            // Mapping lessonIndex -> lessonId thực tế
            Map<Integer, Integer> lessonIndexToId = new HashMap<>();
            List<Lesson> lessons = lDao.getLessonsByCourseID(courseId);
            for (Lesson lesson : lessons) {
                // Giả sử lessonIndex là thứ tự trong danh sách (hoặc lấy từ request nếu có)
                String idxStr = request.getParameter("lessonIndex_" + lesson.getLessonID());
                if (idxStr != null) {
                    int idx = Integer.parseInt(idxStr);
                    lessonIndexToId.put(idx, lesson.getLessonID());
                }
            }
            System.out.println("[DEBUG] lessonIndexToId mapping: " + lessonIndexToId);

            Set<Integer> lessonIndexes = new HashSet<>();
            for (String param : request.getParameterMap().keySet()) {
                Matcher m = Pattern.compile("lessons\\[(\\d+)]\\[name]").matcher(param);
                if (m.find()) {
                    lessonIndexes.add(Integer.parseInt(m.group(1)));
                }
            }

            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                System.out.println("[DEBUG] Part name: " + part.getName() + ", size: " + part.getSize() + ", filename: " + part.getSubmittedFileName());
            }

            for (int lessonIndex : lessonIndexes) {
                String lessonIdStr = request.getParameter("lessons[" + lessonIndex + "][id]");
                Integer lessonId = null;
                if (lessonIdStr != null && !lessonIdStr.isEmpty() && !lessonIdStr.equals("0")) {
                    // Lesson cũ: update
                    lessonId = Integer.parseInt(lessonIdStr);
                    // Update lesson info
                    String lessonName = request.getParameter("lessons[" + lessonIndex + "][name]");
                    String lessonDesc = request.getParameter("lessons[" + lessonIndex + "][desc]");
                    String orderIndexStr = request.getParameter("lessons[" + lessonIndex + "][orderIndex]");
                    int orderIndex = orderIndexStr != null ? Integer.parseInt(orderIndexStr) : lessonIndex;
                    Lesson lesson = new Lesson();
                    lesson.setLessonID(lessonId);
                    lesson.setCourseID(courseId);
                    lesson.setTitle(lessonName);
                    lesson.setDescription(lessonDesc);
                    lesson.setOrderIndex(orderIndex);
                    lesson.setIsHidden(false); // Nếu có field ẩn thì lấy thêm
                    lDao.update(lesson);
                } else {
                    // Lesson mới: insert
                    String lessonName = request.getParameter("lessons[" + lessonIndex + "][name]");
                    String lessonDesc = request.getParameter("lessons[" + lessonIndex + "][desc]");
                    String orderIndexStr = request.getParameter("lessons[" + lessonIndex + "][orderIndex]");
                    int orderIndex = orderIndexStr != null ? Integer.parseInt(orderIndexStr) : lessonIndex;
                    Lesson lesson = new Lesson();
                    lesson.setCourseID(courseId);
                    lesson.setTitle(lessonName);
                    lesson.setDescription(lessonDesc);
                    lesson.setOrderIndex(orderIndex);
                    lesson.setIsHidden(false); // Nếu có field ẩn thì lấy thêm
                    lessonId = lDao.addAndReturnID(lesson);
                }
                // Xử lý tài liệu, từ vựng cho lessonId vừa có (cũ hoặc mới)
                processMaterialsForLesson(request, lessonIndex, lessonId, courseId, mDao, parts);
                processVocabularyForLesson(request, lessonIndex, lessonId, courseId, vDao, parts);
            }

            // Xử lý xóa lesson nếu có
            String[] lessonsToDelete = request.getParameterValues("lessonsToDelete");
            if (lessonsToDelete != null) {
                for (String lessonIdStr : lessonsToDelete) {
                    try {
                        int lessonId = Integer.parseInt(lessonIdStr);
                        // Xóa quiz, tài liệu, từ vựng liên quan trước khi xóa lesson
                        new QuizDAO().deleteByLessonId(lessonId);
                        mDao.deleteByLessonId(lessonId);
                        vDao.deleteByLessonId(lessonId);
                        lDao.delete(lessonId);
                    } catch (Exception ex) {
                        System.out.println("[ERROR] Lỗi khi xóa lesson: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }

            response.sendRedirect("CourseDetailServlet?id=" + courseId);
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("update_course.jsp").forward(request, response);
        }
    }

    // Hàm lưu lesson riêng lẻ (AJAX hoặc dùng lại ở nơi khác)
    public static Lesson saveLesson(HttpServletRequest request, int courseId) throws Exception {
        LessonsDAO lDao = new LessonsDAO();
        String lessonIdStr = request.getParameter("lessonId");
        String name = request.getParameter("name");
        String desc = request.getParameter("desc");
        String orderIndexStr = request.getParameter("orderIndex");
        int orderIndex = orderIndexStr != null ? Integer.parseInt(orderIndexStr) : 0;
        Lesson lesson = new Lesson();
        lesson.setCourseID(courseId);
        lesson.setTitle(name);
        lesson.setDescription(desc);
        lesson.setOrderIndex(orderIndex);
        lesson.setIsHidden(false); // Nếu có field ẩn thì lấy thêm
        if (lessonIdStr != null && !lessonIdStr.isEmpty() && !lessonIdStr.equals("0")) {
            // Update
            lesson.setLessonID(Integer.parseInt(lessonIdStr));
            lDao.update(lesson);
        } else {
            // Insert
            int newId = lDao.addAndReturnID(lesson);
            lesson.setLessonID(newId);
        }
        return lesson;
    }

    private void processMaterialsForLesson(HttpServletRequest request, int lessonIndex, int lessonId, int courseId, LessonMaterialsDAO mDao, Collection<Part> parts) throws Exception {
        String[] fieldTypes = {"vocabVideo", "vocabDoc", "grammarVideo", "grammarDoc", "kanjiVideo", "kanjiDoc"};
        for (Part part : parts) {
            for (String type : fieldTypes) {
                String fieldName = "lessons[" + lessonIndex + "][" + type + "][]";
                if (part.getName().equals(fieldName) && part.getSize() > 0) {
                    String originalName = part.getSubmittedFileName();
                    if (originalName == null || originalName.isEmpty()) {
                        continue;
                    }
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
                    String fileType = type.endsWith("Video") ? "Video" : "PDF";
                    LessonMaterial material = new LessonMaterial();
                    material.setLessonID(lessonId);
                    material.setMaterialType(materialType);
                    material.setFileType(fileType);
                    material.setTitle(materialType + " - " + originalName);
                    material.setFilePath(fileUrl);
                    // XÓA TÀI LIỆU CŨ CÙNG LOẠI (nếu có)
                    List<LessonMaterial> oldMats = mDao.getMaterialsByLessonID(lessonId);
                    for (LessonMaterial oldMat : oldMats) {
                        if (oldMat.getMaterialType().equalsIgnoreCase(materialType) && oldMat.getFileType().equalsIgnoreCase(fileType)) {
                            mDao.delete(oldMat.getMaterialID());
                            if (oldMat.getFilePath() != null && oldMat.getFilePath().startsWith("http")) {
                                String s3KeyOld = oldMat.getFilePath().replaceFirst("https?://[^/]+/", "");
                                config.S3Util.deleteFile(s3KeyOld);
                                System.out.println("[LOG] Đã xóa file tài liệu cũ trên S3: " + s3KeyOld);
                            }
                        }
                    }
                    mDao.add(material);
                }
            }
        }

        String[] deleteMatArr = request.getParameterValues("lessons[" + lessonIndex + "][deleteMaterials][]");
        if (deleteMatArr != null) {
            for (String midStr : deleteMatArr) {
                try {
                    int matId = Integer.parseInt(midStr);
                    LessonMaterial mat = null;
                    List<LessonMaterial> mats = mDao.getMaterialsByLessonID(lessonId);
                    for (LessonMaterial m : mats) {
                        if (m.getMaterialID() == matId) {
                            mat = m;
                            break;
                        }
                    }
                    mDao.delete(matId);
                    if (mat != null && mat.getFilePath() != null) {
                        String s3Key = mat.getFilePath().substring("https://swp391-private.s3.amazonaws.com/".length());
                        S3Util.deleteFile(s3Key);
                        System.out.println("[LOG] Đã xóa file tài liệu từ S3: " + s3Key);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void processVocabularyForLesson(HttpServletRequest request, int lessonIndex, int lessonId, int courseId, VocabularyDAO vDao, Collection<Part> parts) throws Exception {
        for (Part part : parts) {
            String fieldName = "lessons[" + lessonIndex + "][vocabImage][]";
            if (part.getName().equals(fieldName) && part.getSize() > 0) {
                String originalName = part.getSubmittedFileName();
                if (originalName == null || originalName.isEmpty()) {
                    continue;
                }
                String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
                String savedFileName = "vocab" + lessonId + "_" + System.currentTimeMillis() + ext;
                String s3Key = "course/" + courseId + "/lesson" + lessonId + "/vocab/" + savedFileName;
                String imageUrl = null;
                try (InputStream is = part.getInputStream()) {
                    imageUrl = S3Util.uploadFile(is, part.getSize(), s3Key, part.getContentType());
                    System.out.println("[LOG] Đã upload hình ảnh từ vựng lên S3: " + imageUrl);
                } catch (Exception ex) {
                    System.out.println("[ERROR] Upload hình ảnh từ vựng lên S3 thất bại: " + ex.getMessage());
                    ex.printStackTrace();
                    throw ex;
                }
                // Cập nhật imagePath cho từ vựng tương ứng nếu cần
            }
        }

        String[] deleteVocabArr = request.getParameterValues("lessons[" + lessonIndex + "][deleteVocabulary][]");
        if (deleteVocabArr != null) {
            for (String vocabIdStr : deleteVocabArr) {
                try {
                    int vocabId = Integer.parseInt(vocabIdStr);
                    List<Vocabulary> vocabs = vDao.getVocabularyByLessonId(lessonId);
                    Vocabulary vocabToDelete = null;
                    for (Vocabulary v : vocabs) {
                        if (v.getVocabID() == vocabId) {
                            vocabToDelete = v;
                            break;
                        }
                    }
                    vDao.delete(vocabId);
                    if (vocabToDelete != null && vocabToDelete.getImagePath() != null) {
                        // Lấy S3 key từ URL S3 đầy đủ
                        String imagePath = vocabToDelete.getImagePath();
                        if (imagePath.startsWith("https://")) {
                            // URL S3 đầy đủ, lấy key từ sau domain
                            String s3Key = imagePath.substring(imagePath.indexOf("/", 8)); // Bỏ qua "https://"
                            S3Util.deleteFile(s3Key);
                            System.out.println("[LOG] Đã xóa hình ảnh từ vựng từ S3: " + s3Key);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
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
