package controller.courses;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import model.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@MultipartConfig
@WebServlet(name = "CreateCourseServlet", urlPatterns = {"/CreateCourseServlet"})
public class CreateCourseServlet extends HttpServlet {

    // Đường dẫn tuyệt đối trên ổ đĩa D
    private static final String ABSOLUTE_UPLOAD_PATH = "D:\\SUM25_FPT\\SWR\\SWP391-private\\build\\web\\files";

    private static final String UPLOAD_COURSE_IMAGE_DIR = "files";

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

        try {
            User user = getCurrentUser(request);

            // ======== XỬ LÝ ẢNH COURSE (THUMBNAIL) =========
            String imageUrl = null;
            Part imagePart = request.getPart("thumbnailFile");
            if (imagePart != null && imagePart.getSize() > 0) {
                String fileName = getFileName(imagePart);

                File uploadDir = new File(ABSOLUTE_UPLOAD_PATH);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String filePath = ABSOLUTE_UPLOAD_PATH + File.separator + fileName;
                try (InputStream is = imagePart.getInputStream();
                     FileOutputStream os = new FileOutputStream(filePath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }

                // Lưu path tương đối vào DB
                imageUrl = "files/" + fileName;
            }

            // ======== LẤY THÔNG TIN KHÓA HỌC =========
            Course course = getCourseInfoFromRequest(request, user, imageUrl);
            int courseId = saveCourseAndReturnId(course);

            int maxLesson = getMaxLessonIndex(request);

            handleAllLessons(request, courseId, maxLesson, request);

            response.sendRedirect("CourseDetailServlet?id=" + courseId);
        } catch (Exception e) {
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

    private void handleAllLessons(HttpServletRequest request, int courseId, int maxLesson, HttpServletRequest req)
            throws Exception {
        LessonsDAO lessonsDao = new LessonsDAO();
        LessonMaterialsDAO materialsDao = new LessonMaterialsDAO();

        for (int i = 0; i <= maxLesson; i++) {
            String title = request.getParameter("lessons[" + i + "][name]");
            String description = request.getParameter("lessons[" + i + "][description]");
            boolean isHidden = request.getParameter("lessons[" + i + "][isHidden]") != null;

            Lesson lesson = new Lesson();
            lesson.setTitle(title);
            lesson.setDescription(description);
            lesson.setCourseID(courseId);
            lesson.setIsHidden(isHidden);

            int lessonId = lessonsDao.addAndReturnID(lesson);

            saveMaterialsForLesson(request, i, lessonId, req, materialsDao);
            saveQuizForLesson(request, i, lessonId);
        }
    }

    private void saveMaterialsForLesson(HttpServletRequest request, int lessonIndex, int lessonId,
                                        HttpServletRequest req, LessonMaterialsDAO materialsDao)
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
                    String ext = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : "";
                    String savedFileName = "lesson" + lessonId + "_" + type + "_" + System.currentTimeMillis() + ext;

                    // Sử dụng ABSOLUTE_UPLOAD_PATH
                    File uploadDir = new File(ABSOLUTE_UPLOAD_PATH);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    String savePath = ABSOLUTE_UPLOAD_PATH + File.separator + savedFileName;

                    try (InputStream is = part.getInputStream();
                         FileOutputStream os = new FileOutputStream(savePath)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }

                    String materialType = type.startsWith("vocab") ? "Từ Vựng"
                            : type.startsWith("grammar") ? "Ngữ pháp"
                            : type.startsWith("kanji") ? "Kanji" : "";
                    String fileType = type.endsWith("Video") ? "video" : "PDF";

                    LessonMaterial material = new LessonMaterial();
                    material.setLessonID(lessonId);
                    material.setMaterialType(materialType);
                    material.setFileType(fileType);
                    material.setFilePath("files/" + savedFileName);
                    materialsDao.add(material);
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
        for (int qIdx : questionIndexes) {
            String base = "lessons[" + lessonIndex + "][questions][" + qIdx + "]";
            String questionText = request.getParameter(base + "[question]");
            String optionA = request.getParameter(base + "[optionA]");
            String optionB = request.getParameter(base + "[optionB]");
            String optionC = request.getParameter(base + "[optionC]");
            String optionD = request.getParameter(base + "[optionD]");
            String answer = request.getParameter(base + "[answer]");
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
            System.out.println("📥 Đang lưu " + questions.size() + " câu quiz cho lessonId = " + lessonId);
            QuizDAO.saveQuestions(lessonId, questions);
        } else {
            System.out.println("⚠️ Không có câu hỏi nào được lưu cho lessonId = " + lessonId);
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
}
