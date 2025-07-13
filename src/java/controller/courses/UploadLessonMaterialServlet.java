/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.courses;

import Dao.LessonMaterialsDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.LessonMaterial;

/**
 *
 * @author LAPTOP LENOVO
 */
@WebServlet(name = "UploadLessonMaterialServlet", urlPatterns = {"/UploadLessonMaterialServlet"})
@MultipartConfig
public class UploadLessonMaterialServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int lessonID = Integer.parseInt(request.getParameter("lessonID"));
        LessonMaterialsDAO dao = new LessonMaterialsDAO();

        try {
            uploadAndSave(request, lessonID, "grammarVideo", "Ngữ pháp", "Video", dao);
            uploadAndSave(request, lessonID, "vocabularyPdf", "Từ vựng", "PDF", dao);
            uploadAndSave(request, lessonID, "kanjiPdf", "Kanji", "PDF", dao);
            uploadAndSave(request, lessonID, "grammarPdf", "Ngữ pháp", "PDF", dao);
        } catch (SQLException ex) {
            Logger.getLogger(UploadLessonMaterialServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        response.sendRedirect("CourseDetailServlet?id=" + request.getParameter("courseID"));
    }

    private void uploadAndSave(HttpServletRequest request, int lessonID, String partName, String materialType, String fileType, LessonMaterialsDAO dao)
            throws IOException, ServletException, SQLException {

        Part filePart = request.getPart(partName);
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String uploadPath = getServletContext().getRealPath("/") + "uploads" + File.separator + fileName;
            filePart.write(uploadPath);

            LessonMaterial material = new LessonMaterial();
            material.setLessonID(lessonID);
            material.setMaterialType(materialType);
            material.setFileType(fileType);
            material.setTitle(materialType + " - " + fileName);
            material.setFilePath("uploads/" + fileName);

            try {
                dao.add(material);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
