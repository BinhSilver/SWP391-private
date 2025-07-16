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
                    quizData.put("question", question.getQuestion());

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
                File uploadDir = new File(ABSOLUTE_UPLOAD_PATH);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                String filePath = ABSOLUTE_UPLOAD_PATH + File.separator + fileName;
                saveFile(imagePart, filePath);
                imageUrl = "files/" + fileName;
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
                e.printStackTrace();
                request.setAttribute("error", "Lỗi khi cập nhật khóa học: " + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            LessonsDAO lDao = new LessonsDAO();
            LessonMaterialsDAO mDao = new LessonMaterialsDAO();
            VocabularyDAO vDao = new VocabularyDAO();

            Set<Integer> lessonIndexes = new HashSet<>();
            for (String param : request.getParameterMap().keySet()) {
                Matcher m = Pattern.compile("lessons\\[(\\d+)]\\[name]").matcher(param);
                if (m.find()) {
                    lessonIndexes.add(Integer.parseInt(m.group(1)));
                }
            }

            List<Lesson> oldLessons = lDao.getLessonsByCourseID(courseId);
            Set<Integer> oldLessonIds = new HashSet<>();
            for (Lesson l : oldLessons) {
                oldLessonIds.add(l.getLessonID());
            }
            Set<Integer> keptLessonIds = new HashSet<>();
            Map<Integer, Integer> lessonIndexToIdMap = new HashMap<>();

            for (Integer idx : lessonIndexes) {
                String lessonIdStr = request.getParameter("lessons[" + idx + "][id]");
                String lessonName = request.getParameter("lessons[" + idx + "][name]");
                String lessonDesc = request.getParameter("lessons[" + idx + "][desc]");
                int lessonId = 0;

                if (lessonIdStr != null && !lessonIdStr.isEmpty()) {
                    lessonId = Integer.parseInt(lessonIdStr);
                    Lesson lesson = new Lesson(lessonId, courseId, lessonName, false, lessonDesc);
                    try {
                        lDao.update(lesson);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        continue;
                    }
                    keptLessonIds.add(lessonId);
                } else {
                    Lesson lesson = new Lesson(0, courseId, lessonName, false, lessonDesc);
                    try {
                        lessonId = lDao.addAndReturnID(lesson);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        continue;
                    }
                    keptLessonIds.add(lessonId);
                }

                lessonIndexToIdMap.put(idx, lessonId);

                try {
                    processMaterialsForLesson(request, lessonId, idx, mDao, request.getParts());
                    processVocabularyForLesson(request, lessonId, idx, vDao, request.getParts());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    new QuizService().processQuizForLesson(request, lessonId, idx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                new QuizService().processQuizFromJson(request, lessonIndexToIdMap);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Lỗi khi xử lý quiz: " + e.getMessage());
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }

            // Xóa các lesson không còn trong danh sách
            for (Integer oldLessonId : oldLessonIds) {
                if (!keptLessonIds.contains(oldLessonId)) {
                    try {
                        QuizDAO.deleteQuestionsByLessonId(oldLessonId);
                        List<LessonMaterial> oldMats = mDao.getMaterialsByLessonID(oldLessonId);
                        for (LessonMaterial mat : oldMats) {
                            mDao.delete(mat.getMaterialID());
                            String realPath = ABSOLUTE_UPLOAD_PATH + File.separator + mat.getFilePath().substring("files/".length());
                            File f = new File(realPath);
                            if (f.exists()) {
                                f.delete();
                            }
                        }
                        List<Vocabulary> oldVocabs = vDao.getVocabularyByLessonId(oldLessonId);
                        for (Vocabulary vocab : oldVocabs) {
                            vDao.delete(vocab.getVocabID());
                            if (vocab.getImagePath() != null && !vocab.getImagePath().isEmpty()) {
                                String realPath = getServletContext().getRealPath(VOCAB_IMAGE_PATH) + File.separator + vocab.getImagePath();
                                File f = new File(realPath);
                                if (f.exists()) {
                                    f.delete();
                                }
                            }
                        }
                        lDao.delete(oldLessonId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            response.sendRedirect("CourseDetailServlet?id=" + courseId);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private void processMaterialsForLesson(HttpServletRequest request, int lessonId, int idx, LessonMaterialsDAO mDao, Collection<Part> parts) throws Exception {
        String[] types = {"vocabVideo", "vocabDoc", "grammarVideo", "grammarDoc", "kanjiVideo", "kanjiDoc"};
        for (String type : types) {
            for (Part part : parts) {
                if (part.getName().equals("lessons[" + idx + "][" + type + "][]") && part.getSize() > 0) {
                    String fileName = getFileName(part);
                    if (fileName == null || fileName.isEmpty()) {
                        continue;
                    }
                    // Kiểm tra định dạng tệp
                    if (type.endsWith("Video") && !isValidVideo(fileName)) {
                        throw new IllegalArgumentException("File video không hợp lệ: " + fileName + ". Chỉ chấp nhận mp4, avi, mov.");
                    }
                    if (type.endsWith("Doc") && !isValidPDF(fileName)) {
                        throw new IllegalArgumentException("File tài liệu không hợp lệ: " + fileName + ". Chỉ chấp nhận PDF.");
                    }

                    String savePath = ABSOLUTE_UPLOAD_PATH + File.separator + fileName;
                    saveFile(part, savePath);

                    LessonMaterial material = new LessonMaterial();
                    material.setLessonID(lessonId);
                    material.setMaterialType(type.startsWith("vocab") ? "Từ Vựng" : type.startsWith("grammar") ? "Ngữ pháp" : "Kanji");
                    material.setFileType(type.endsWith("Video") ? "Video" : "PDF");
                    material.setTitle(fileName);
                    material.setFilePath("files/" + fileName);
                    material.setIsHidden(false);
                    mDao.add(material);
                }
            }
        }

        String[] deleteMatArr = request.getParameterValues("lessons[" + idx + "][deleteMaterials][]");
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
                        String realPath = ABSOLUTE_UPLOAD_PATH + File.separator + mat.getFilePath().substring("files/".length());
                        File f = new File(realPath);
                        if (f.exists()) {
                            f.delete();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void processVocabularyForLesson(HttpServletRequest request, int lessonId, int lessonIndex, VocabularyDAO vDao, Collection<Part> parts) throws Exception {
        // Xử lý từ vựng hiện có (cập nhật)
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String key : paramMap.keySet()) {
            if (key.startsWith("lessons[" + lessonIndex + "][existingVocabulary][")) {
                Matcher m = Pattern.compile("lessons\\[" + lessonIndex + "\\]\\[existingVocabulary\\]\\[(\\d+)\\]\\[(\\w+)\\]").matcher(key);
                if (m.find()) {
                    int vocabId = Integer.parseInt(m.group(1));
                    String field = m.group(2);
                    Vocabulary vocab = null;
                    List<Vocabulary> existingVocabs = vDao.getVocabularyByLessonId(lessonId);
                    for (Vocabulary v : existingVocabs) {
                        if (v.getVocabID() == vocabId) {
                            vocab = v;
                            break;
                        }
                    }
                    if (vocab == null) {
                        continue;
                    }

                    switch (field) {
                        case "word" -> vocab.setWord(request.getParameter(key));
                        case "meaning" -> vocab.setMeaning(request.getParameter(key));
                        case "reading" -> vocab.setReading(request.getParameter(key));
                        case "example" -> vocab.setExample(request.getParameter(key));
                    }

                    // Xử lý hình ảnh mới cho từ vựng hiện có
                    Part imagePart = request.getPart("lessons[" + lessonIndex + "][existingVocabulary][" + vocabId + "][image]");
                    if (imagePart != null && imagePart.getSize() > 0) {
                        String originalName = imagePart.getSubmittedFileName();
                        if (originalName != null && !originalName.isEmpty()) {
                            if (!isValidImage(originalName)) {
                                throw new IllegalArgumentException("Hình ảnh từ vựng không hợp lệ: " + originalName + ". Chỉ chấp nhận jpg, jpeg, png, gif.");
                            }
                            // Xóa hình ảnh cũ nếu có
                            if (vocab.getImagePath() != null && !vocab.getImagePath().isEmpty()) {
                                String oldImagePath = getServletContext().getRealPath(VOCAB_IMAGE_PATH) + File.separator + vocab.getImagePath();
                                File oldImage = new File(oldImagePath);
                                if (oldImage.exists()) {
                                    oldImage.delete();
                                }
                            }
                            // Lưu hình ảnh mới
                            String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
                            String savedFileName = vocab.getWord().replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ext;
                            String savePath = getServletContext().getRealPath(VOCAB_IMAGE_PATH) + File.separator + savedFileName;
                            saveFile(imagePart, savePath);
                            vocab.setImagePath(savedFileName); // Chỉ lưu tên file
                        }
                    }

                    vDao.update(vocab);
                }
            }
        }

        // Xử lý từ vựng mới
        for (String key : paramMap.keySet()) {
            if (key.startsWith("lessons[" + lessonIndex + "][vocabText]")) {
                int vocabIndex = Integer.parseInt(key.substring(key.lastIndexOf('[') + 1, key.lastIndexOf(']')));
                String vocabText = request.getParameter(key);
                if (vocabText != null && !vocabText.isEmpty()) {
                    String[] vocabParts = vocabText.split(":");
                    if (vocabParts.length == 4) {
                        Vocabulary vocab = new Vocabulary();
                        vocab.setWord(vocabParts[0].trim());
                        vocab.setMeaning(vocabParts[1].trim());
                        vocab.setReading(vocabParts[2].trim());
                        vocab.setExample(vocabParts[3].trim());
                        vocab.setLessonID(lessonId);

                        // Xử lý hình ảnh từ vựng mới
                        Part imagePart = request.getPart("lessons[" + lessonIndex + "][vocabImage][" + vocabIndex + "]");
                        if (imagePart != null && imagePart.getSize() > 0) {
                            String originalName = imagePart.getSubmittedFileName();
                            if (originalName != null && !originalName.isEmpty()) {
                                if (!isValidImage(originalName)) {
                                    throw new IllegalArgumentException("Hình ảnh từ vựng không hợp lệ: " + originalName + ". Chỉ chấp nhận jpg, jpeg, png, gif.");
                                }
                                String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
                                String savedFileName = vocab.getWord().replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ext;
                                String savePath = getServletContext().getRealPath(VOCAB_IMAGE_PATH) + File.separator + savedFileName;
                                saveFile(imagePart, savePath);
                                vocab.setImagePath(savedFileName); // Chỉ lưu tên file
                            }
                        }

                        vDao.add(vocab);
                    } else {
                        throw new IllegalArgumentException("Định dạng từ vựng không đúng: " + vocabText + ". Yêu cầu Word:Meaning:Reading:Example");
                    }
                }
            }
        }

        // Xử lý xóa từ vựng
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
                        String realPath = getServletContext().getRealPath(VOCAB_IMAGE_PATH) + File.separator + vocabToDelete.getImagePath();
                        File f = new File(realPath);
                        if (f.exists()) {
                            f.delete();
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
