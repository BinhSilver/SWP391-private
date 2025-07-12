/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import Dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.sql.SQLException;

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

            String avatarUrl;
            if (user != null && user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
                // User has a custom avatar URL
                avatarUrl = user.getAvatar();
            } else {
                // User has no avatar, use default one based on gender
                String defaultAvatarPath = "/assets/avatar/nam.jpg"; // Default to male avatar
                if (user != null && "Nữ".equalsIgnoreCase(user.getGender())) {
                    defaultAvatarPath = "/assets/avatar/nu.jpg";
                }
                avatarUrl = request.getContextPath() + defaultAvatarPath;
            }
            
            // Redirect to the avatar URL
            response.sendRedirect(avatarUrl);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.");
        } catch (SQLException e) {
            throw new ServletException("Database error while retrieving user avatar.", e);
        }
    }
}