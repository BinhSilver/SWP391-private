/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import Dao.UserDAO;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

            if (user != null && user.getAvatar() != null && user.getAvatar().length > 0) {
                // User has a custom avatar, serve it
                response.setContentType("image/jpeg");
                response.getOutputStream().write(user.getAvatar());
            } else {
                // User has no avatar, serve a default one based on gender
                String defaultAvatarPath = "/assets/avatar/nam.jpg"; // Default to male avatar
                if (user != null && "Nữ".equalsIgnoreCase(user.getGender())) {
                    defaultAvatarPath = "/assets/avatar/nu.jpg";
                }
                
                response.setContentType("image/jpeg");
                try (InputStream in = getServletContext().getResourceAsStream(defaultAvatarPath);
                     OutputStream out = response.getOutputStream()) {
                    
                    if (in == null) {
                        // If for some reason the default avatar is not found, send a 404 error
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                    
                    // Stream the default avatar to the response
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.");
        } catch (SQLException e) {
            throw new ServletException("Database error while retrieving user avatar.", e);
        }
    }
}