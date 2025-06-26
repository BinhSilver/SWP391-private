package controller.courses;

import Dao.CoursesDAO;
import Dao.LessonsDAO;
import Dao.LessonMaterialsDAO;
import model.Course;
import model.Lesson;
import model.LessonMaterial;
import model.User;

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

            String uploadRoot = "D:\\SUM25_FPT\\SWR\\SWP391-private\\web\\files";
            File uploadDir = new File(uploadRoot);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            handleAllLessons(request, courseId, maxLesson, uploadRoot);

            // Nếu có quiz thì xử lý thêm quiz ở đây (không viết phần quiz chi tiết ở đây)
            // handleQuiz(request, lessons);
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
        // Có thể set thêm userId nếu model Course của bạn có trường này
        return course;
    }

    private int saveCourseAndReturnId(Course course) throws SQLException {
        CoursesDAO dao = new CoursesDAO();
        return dao.addAndReturnID(course); // method này cần có trong CoursesDAO
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

    private void handleAllLessons(HttpServletRequest request, int courseId, int maxLesson, String uploadRoot)
            throws Exception {
        LessonsDAO lessonsDao = new LessonsDAO();
        LessonMaterialsDAO materialsDao = new LessonMaterialsDAO();

        for (int i = 0; i <= maxLesson; i++) {
            String lessonTitle = request.getParameter("lessons[" + i + "][name]"); // hoặc [title] nếu form bạn đặt vậy
            // Nếu có checkbox ẩn hiện cho từng bài học:
            boolean isHidden = request.getParameter("lessons[" + i + "][isHidden]") != null;

            Lesson lesson = new Lesson();
            lesson.setTitle(lessonTitle);
            lesson.setCourseID(courseId);
            lesson.setIsHidden(isHidden);

            int lessonId = lessonsDao.addAndReturnID(lesson);
            lesson.setLessonID(lessonId);

            // Lưu từng loại tài liệu
            saveMaterialsForLesson(request, i, lessonId, uploadRoot, materialsDao);
        }
    }

    private void saveMaterialsForLesson(HttpServletRequest request, int lessonIndex, int lessonId, String uploadRoot, LessonMaterialsDAO materialsDao)
            throws Exception {
        String[] fieldTypes = {
            "vocabVideo", "vocabDoc", "grammarVideo", "grammarDoc", "kanjiVideo", "kanjiDoc"
        };
        for (Part part : request.getParts()) {
            for (String type : fieldTypes) {
                String fieldName = "lessons[" + lessonIndex + "][" + type + "][]";
                if (part.getName().equals(fieldName) && part.getSize() > 0) {
                    String originalName = part.getSubmittedFileName();
                    if (originalName == null || originalName.isEmpty()) {
                        continue;
                    }

                    // Đặt tên file lưu trữ duy nhất
                    String ext = "";
                    int dotIdx = originalName.lastIndexOf('.');
                    if (dotIdx > 0) {
                        ext = originalName.substring(dotIdx);
                    }
                    String savedFileName = "lesson" + lessonId + "_" + type + "_" + System.currentTimeMillis() + ext;
                    String savePath = uploadRoot + File.separator + savedFileName;
                    part.write(savePath);

                    // Xác định loại tài liệu (grammar/kanji/vocab), loại file (pdf/video)
                    String materialType = "";
                    if (type.startsWith("vocab")) {
                        materialType = "Từ Vựng";
                    } else if (type.startsWith("grammar")) {
                        materialType = "Ngữ pháp";
                    } else if (type.startsWith("kanji")) {
                        materialType = "Kanji";
                    }

                    String fileType = type.endsWith("Video") ? "video" : "PDF";

                    LessonMaterial material = new LessonMaterial();
                    material.setLessonID(lessonId);
                    material.setMaterialType(materialType);
                    material.setFileType(fileType);
                    material.setFilePath("files/" + savedFileName);
                    materialsDao.add(material); // thêm vào DB
                }
            }
        }
    }

}
