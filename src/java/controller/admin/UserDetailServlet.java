package controller.admin;

import Dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import model.User;

@WebServlet("/userDetail")
public class UserDetailServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("UserDetailServlet doGet started at " + new java.util.Date());

        String userIdParam = request.getParameter("userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            System.out.println("Missing userId parameter");
            response.sendRedirect(request.getContextPath() + "/userManagement?error=Missing user ID");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdParam);
            System.out.println("Fetching user with ID: " + userId);
            User user = userDAO.getUserById(userId);
            if (user != null) {
                System.out.println("User found: " + user.getFullName() + ", Email: " + user.getEmail());
                request.setAttribute("user", user);
                request.getRequestDispatcher("/userDetail.jsp").forward(request, response);
            } else {
                System.out.println("User not found for ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/userManagement?error=User not found");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid userId format: " + userIdParam);
            response.sendRedirect(request.getContextPath() + "/userManagement?error=Invalid user ID");
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e.getMessage() + 
                              ", SQLState: " + e.getSQLState() + ", ErrorCode: " + e.getErrorCode());
            response.sendRedirect(request.getContextPath() + "/userManagement?error=Error loading user details");
        }

        System.out.println("UserDetailServlet doGet ended at " + new java.util.Date());
    }
}