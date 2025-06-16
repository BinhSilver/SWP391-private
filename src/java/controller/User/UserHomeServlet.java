/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.User;

import Dao.CoursesDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Course;

/**
 *
 * @author LAPTOP LENOVO
 */
@WebServlet(name = "UserHomeServlet", urlPatterns = {"/UserHomeServlet"})
public class UserHomeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CoursesDAO dao = new CoursesDAO();
        List<Course> suggestedCourses = dao.getSuggestedCourses(); // Bạn định nghĩa hàm này

        request.setAttribute("suggestedCourses", suggestedCourses);
        request.getRequestDispatcher("/Home/user_home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

}
