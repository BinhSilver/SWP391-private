package controller.courses;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import Dao.QuizDAO;
import model.Course;
import model.Lesson;
import model.LessonMaterial;
import model.QuizQuestion;
import model.Answer;

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

    private static final String UPLOAD_DIR = "image";
    private static final String ABSOLUTE_UPLOAD_PATH = "D:\\SUM25_FPT\\SWR\\SWP391-private\\build\\web\\files";

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

        CoursesDAO cDao = new CoursesDAO();
        LessonsDAO lDao = new LessonsDAO();
        LessonMaterialsDAO mDao = new LessonMaterialsDAO();

        Course course = cDao.getCourseByID(courseId);
        if (course == null) {
            request.setAttribute("error", "Không tìm thấy khóa học với ID " + courseId);
            request.getRequestDispatcher("error.jsp").forward(request, response);
            return;
        }
        List<Lesson> lessons = lDao.getLessonsByCourseID(courseId);
        Map<Integer, List<LessonMaterial>> materialsMap = mDao.getAllMaterialsGroupedByLesson(courseId);
        Map<Integer, List<QuizQuestion>> quizMap = new HashMap<>();
        for (Lesson lesson : lessons) {
            quizMap.put(lesson.getLessonID(), QuizDAO.getQuestionsWithAnswersByLessonId(lesson.getLessonID()));
        }

        // Create JSON data for JavaScript
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
                            case 1 ->
                                optionA = answer.getAnswerText();
                            case 2 ->
                                optionB = answer.getAnswerText();
                            case 3 ->
                                optionC = answer.getAnswerText();
                            case 4 ->
                                optionD = answer.getAnswerText();
                        }
                    }
                    quizData.put("optionA", optionA);
                    quizData.put("optionB", optionB);
                    quizData.put("optionC", optionC);
                    quizData.put("optionD", optionD);

                    String correctAnswer = switch (question.getCorrectAnswer()) {
                        case 1 ->
                            "A";
                        case 2 ->
                            "B";
                        case 3 ->
                            "C";
                        default ->
                            "D";
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

                File uploadDir = new File(ABSOLUTE_UPLOAD_PATH);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String filePath = ABSOLUTE_UPLOAD_PATH + File.separator + fileName;
                try (InputStream is = imagePart.getInputStream(); FileOutputStream os = new FileOutputStream(filePath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
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

//            for (Integer oldLessonId : oldLessonIds) {
//                if (!keptLessonIds.contains(oldLessonId)) {
//                    try {
//                        QuizDAO.deleteQuestionsByLessonId(oldLessonId);
//                        List<LessonMaterial> oldMats = mDao.getMaterialsByLessonID(oldLessonId);
//                        for (LessonMaterial mat : oldMats) {
//                            mDao.delete(mat.getMaterialID());
//                        }
//                        lDao.delete(oldLessonId);
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            response.sendRedirect("CourseDetailServlet?id=" + courseId);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    private void processMaterialsForLesson(HttpServletRequest request, int lessonId, int idx, LessonMaterialsDAO mDao, Collection<Part> parts) {
        String[] types = {"vocabDoc", "vocabVideo", "grammarDoc", "grammarVideo", "kanjiDoc", "kanjiVideo"};
        for (String type : types) {
            for (Part part : parts) {
                if (part.getName().equals("lessons[" + idx + "][" + type + "][]") && part.getSize() > 0) {
                    String fileName = getFileName(part);
                    String uploadPath = request.getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    try (InputStream is = part.getInputStream(); FileOutputStream os = new FileOutputStream(uploadPath + File.separator + fileName)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LessonMaterial material = new LessonMaterial();
                    material.setLessonID(lessonId);
                    material.setMaterialType(type);
                    material.setFileType(type.endsWith("Video") ? "Video" : "PDF");
                    material.setTitle(fileName);
                    material.setFilePath(UPLOAD_DIR + "/" + fileName);
                    material.setIsHidden(false);
                    try {
                        mDao.add(material);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
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
                        String realPath = request.getServletContext().getRealPath("") + File.separator + mat.getFilePath();
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
}
