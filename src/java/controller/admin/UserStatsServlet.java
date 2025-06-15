package controller.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import Dao.UserDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@WebServlet("/api/userstats")
public class UserStatsServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String period = request.getParameter("period");
        List<JsonObject> stats = new ArrayList<>();
        try {
            if ("year".equalsIgnoreCase(period)) {
                stats = userDAO.getUserCountByYear();
            } else { // default = month
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                stats = userDAO.getUserCountByMonth(currentYear);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonArray jsonArray = new JsonArray();
        for (JsonObject obj : stats) {
            jsonArray.add(obj);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonArray.toString());
    }
}
