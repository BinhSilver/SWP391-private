/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import Dao.UserDAO;
import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
@WebServlet("/avatar")
public class AvatarServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User user = userDAO.getUserById(userId);
            if (user != null && user.getAvatar() != null) {
                response.setContentType("image/jpeg"); // Hoặc loại MIME phù hợp
                response.getOutputStream().write(user.getAvatar());
            } else {
                response.sendRedirect("https://via.placeholder.com/100");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("https://via.placeholder.com/100");
        }
    }
}