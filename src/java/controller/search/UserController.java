package controller.search;

import Dao.UserDAO;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.User;
import java.sql.*;



@WebServlet("/api/users/*")
public class UserController extends HttpServlet {
    private UserDAO userDAO;
    private Gson gson;

    @Override
    public void init() {
        userDAO = new UserDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            List<User> users = userDAO.getAllUsers();
            resp.getWriter().write(gson.toJson(users));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"Database error\"}");
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.matches("/\\d+/lock")) {
            try {
                int userId = Integer.parseInt(pathInfo.split("/")[1]);
                User user = userDAO.getUserById(userId);
                if (user != null) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = req.getReader().readLine()) != null) {
                        sb.append(line);
                    }
                    LockRequest lockRequest = gson.fromJson(sb.toString(), LockRequest.class);
                    user.setLocked(lockRequest.isLocked);
                    userDAO.updateUser(user);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"User not found\"}");
                }
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\": \"Database error\"}");
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Invalid user ID\"}");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid request\"}");
        }
    }

    private static class LockRequest {
        boolean isLocked;
    }
}