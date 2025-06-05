package controller.chart;

import com.google.gson.Gson;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/registrations")
public class RegistrationServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String period = req.getParameter("period") != null ? req.getParameter("period") : "month";
        resp.getWriter().write(gson.toJson(userDAO.getRegistrationsByPeriod(period)));
    }
}