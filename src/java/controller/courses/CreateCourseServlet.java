package controller.courses;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.VocabularyDAO;
import model.Course;
import model.Lesson;
import model.LessonMaterial;
import model.User;
import model.Vocabulary;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@MultipartConfig
@WebServlet(name = "CreateCourseServlet", urlPatterns = {"/CreateCourseServlet"})
public class CreateCourseServlet extends HttpServlet {

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
            Course course = getCourseInfoFromRequest(request, user);
            int courseId = saveCourseAndReturnId(course);
            int maxLesson = getMaxLessonIndex(request);

            String uploadRoot = "D:\\SWP_NETBEAN\\SWP_HUY 12.7\\SWP391-private\\web\\files";
            File uploadDir = new File(uploadRoot);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String imgVocabDir = "D:\\SWP_NETBEAN\\SWP_HUY 12.7\\SWP391-private\\web\\imgvocab"; // Đường dẫn thực tế
            File imgVocabRoot = new File(imgVocabDir);
            if (!imgVocabRoot.exists()) {
                imgVocabRoot.mkdirs();
            }

            handleAllLessons(request, courseId, maxLesson, uploadRoot, imgVocabDir);

            // Xử lý quiz (chỉ log tạm thời, bạn cần thêm logic lưu vào database)
            String quizJson = request.getParameter("quizJson");
            if (quizJson != null && !quizJson.isEmpty()) {
                System.out.println("Quiz Data: " + quizJson);
                // TODO: Thêm logic lưu quiz vào database (bảng riêng nếu có)
            }

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

    private Course getCourseInfoFromRequest(HttpServletRequest request, User user) {
        String title = request.getParameter("courseTitle");
        String description = request.getParameter("courseDescription");
        boolean isHidden = request.getParameter("isHidden") != null;
        boolean isSuggested = user != null && user.getRoleID() == 4 && request.getParameter("isSuggested") != null;

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setHidden(isHidden);
        course.setSuggested(isSuggested);
        
        // Lưu ID người tạo khóa học
        if (user != null) {
            course.setCreatedBy(user.getUserID());
        }
        
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
                } catch (Exception ignore) {}
            }
        }
        return maxLesson;
    }

    private void handleAllLessons(HttpServletRequest request, int courseId, int maxLesson, String uploadRoot, String imgVocabDir)
            throws Exception {
        LessonsDAO lessonsDao = new LessonsDAO();
        LessonMaterialsDAO materialsDao = new LessonMaterialsDAO();
        VocabularyDAO vocabDao = new VocabularyDAO();

        for (int i = 0; i <= maxLesson; i++) {
            String lessonTitle = request.getParameter("lessons[" + i + "][name]");
            boolean isHidden = request.getParameter("lessons[" + i + "][isHidden]") != null;

            Lesson lesson = new Lesson();
            lesson.setTitle(lessonTitle);
            lesson.setCourseID(courseId);
            lesson.setIsHidden(isHidden);

            int lessonId = lessonsDao.addAndReturnID(lesson);
            lesson.setLessonID(lessonId);

            // Lưu tài liệu (video, PDF)
            saveMaterialsForLesson(request, i, lessonId, uploadRoot, materialsDao);

            // Lưu từ vựng text và ảnh
            saveVocabularyForLesson(request, i, lessonId, imgVocabDir, vocabDao);
        }
    }

    private void saveMaterialsForLesson(HttpServletRequest request, int lessonIndex, int lessonId, String uploadRoot, LessonMaterialsDAO materialsDao)
            throws Exception {
        String[] fieldTypes = {"vocabVideo", "vocabDoc", "grammarVideo", "grammarDoc", "kanjiVideo", "kanjiDoc"};
        for (Part part : request.getParts()) {
            for (String type : fieldTypes) {
                String fieldName = "lessons[" + lessonIndex + "][" + type + "][]";
                if (part.getName().equals(fieldName) && part.getSize() > 0) {
                    String originalName = part.getSubmittedFileName();
                    if (originalName == null || originalName.isEmpty()) continue;

                    String ext = "";
                    int dotIdx = originalName.lastIndexOf('.');
                    if (dotIdx > 0) ext = originalName.substring(dotIdx);
                    String savedFileName = "lesson" + lessonId + "_" + type + "_" + System.currentTimeMillis() + ext;
                    String savePath = uploadRoot + File.separator + savedFileName;
                    part.write(savePath);

                    String materialType = "";
                    if (type.startsWith("vocab")) materialType = "Từ Vựng";
                    else if (type.startsWith("grammar")) materialType = "Ngữ pháp";
                    else if (type.startsWith("kanji")) materialType = "Kanji";

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

    private void saveVocabularyForLesson(HttpServletRequest request, int lessonIndex, int lessonId, String imgVocabDir, VocabularyDAO vocabDao)
            throws Exception {
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String key : paramMap.keySet()) {
            if (key.startsWith("lessons[" + lessonIndex + "][vocabText]")) {
                int vocabIndex = Integer.parseInt(key.substring(key.lastIndexOf('[') + 1, key.lastIndexOf(']')));
                String vocabText = request.getParameter(key);
                if (vocabText != null && !vocabText.isEmpty()) {
                    String[] parts = vocabText.split(":");
                    if (parts.length == 4) { // Kiểm tra định dạng
                        Vocabulary vocab = new Vocabulary();
                        vocab.setWord(parts[0].trim());
                        vocab.setMeaning(parts[1].trim());
                        vocab.setReading(parts[2].trim());
                        vocab.setExample(parts[3].trim());
                        vocab.setLessonID(lessonId);

                        // Xử lý ảnh nếu có
                        Part imagePart = request.getPart("lessons[" + lessonIndex + "][vocabImage][" + vocabIndex + "]");
                        if (imagePart != null && imagePart.getSize() > 0) {
                            String originalName = imagePart.getSubmittedFileName();
                            if (originalName != null && !originalName.isEmpty()) {
                                String ext = "";
                                int dotIdx = originalName.lastIndexOf('.');
                                if (dotIdx > 0) ext = originalName.substring(dotIdx);
                                String savedFileName = vocab.getWord().replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ext;
                                String savePath = imgVocabDir + File.separator + savedFileName;
                                imagePart.write(savePath);
                                vocab.setImagePath(savedFileName); // Đường dẫn tương đối
                            }
                        }

                        vocabDao.add(vocab); // Thêm vào bảng Vocabulary
                    } else {
                        throw new IllegalArgumentException("Định dạng từ vựng không đúng: " + vocabText + ". Yêu cầu Word:Meaning:Reading:Example");
                    }
                }
            }
        }
    }
}