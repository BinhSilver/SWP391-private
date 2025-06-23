package controller.chart;

import Dao.UserDAO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/api/userrolestats")
public class UserRoleStatsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        UserDAO userDAO = new UserDAO();
        try {
            JsonArray userRoleStats = userDAO.getUserCountByRole();
            System.out.println("Dữ liệu từ UserDAO.getUserCountByRole: " + userRoleStats);
            String json = new Gson().toJson(userRoleStats);
            System.out.println("JSON phản hồi: " + json);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"Unable to fetch role data\"}");
            out.flush();
        }
    }
}