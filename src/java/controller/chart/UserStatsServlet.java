package servlet;

import dao.UserDAO;
import dao.UserPremiumDAO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import DB.JDBCConnection;

@WebServlet("/UserStatsServlet")
public class UserStatsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int totalUsers = 0;
        int currentMonthUsers = 0;
        int previousMonthUsers = 0;
        double userGrowthRate = 0.0;
        int currentMonthPremium = 0;
        int previousMonthPremium = 0;
        double premiumGrowthRate = 0.0;

        try (Connection conn = JDBCConnection.getConnection()) {
            // Total users
            String totalSql = "SELECT COUNT(*) AS Total FROM Users";
            PreparedStatement totalStmt = conn.prepareStatement(totalSql);
            ResultSet totalRs = totalStmt.executeQuery();
            if (totalRs.next()) {
                totalUsers = totalRs.getInt("Total");
            }

            // Current month users (June 2025)
            String currentUserSql = "SELECT COUNT(*) AS Count FROM Users WHERE MONTH(CreatedAt) = 6 AND YEAR(CreatedAt) = 2025";
            PreparedStatement currentUserStmt = conn.prepareStatement(currentUserSql);
            ResultSet currentUserRs = currentUserStmt.executeQuery();
            if (currentUserRs.next()) {
                currentMonthUsers = currentUserRs.getInt("Count");
            }

            // Previous month users (May 2025)
            String previousUserSql = "SELECT COUNT(*) AS Count FROM Users WHERE MONTH(CreatedAt) = 5 AND YEAR(CreatedAt) = 2025";
            PreparedStatement previousUserStmt = conn.prepareStatement(previousUserSql);
            ResultSet previousUserRs = previousUserStmt.executeQuery();
            if (previousUserRs.next()) {
                previousMonthUsers = previousUserRs.getInt("Count");
            }

            // Calculate user growth rate
            if (previousMonthUsers > 0) {
                userGrowthRate = ((double)(currentMonthUsers - previousMonthUsers) / previousMonthUsers) * 100;
            } else if (currentMonthUsers > 0) {
                userGrowthRate = 100.0;
            }

            // Current month premium registrations (June 2025)
            String currentPremiumSql = "SELECT COUNT(*) AS Count FROM UserPremium WHERE MONTH(StartDate) = 6 AND YEAR(StartDate) = 2025";
            PreparedStatement currentPremiumStmt = conn.prepareStatement(currentPremiumSql);
            ResultSet currentPremiumRs = currentPremiumStmt.executeQuery();
            if (currentPremiumRs.next()) {
                currentMonthPremium = currentPremiumRs.getInt("Count");
            }

            // Previous month premium registrations (May 2025)
            String previousPremiumSql = "SELECT COUNT(*) AS Count FROM UserPremium WHERE MONTH(StartDate) = 5 AND YEAR(StartDate) = 2025";
            PreparedStatement previousPremiumStmt = conn.prepareStatement(previousPremiumSql);
            ResultSet previousPremiumRs = previousPremiumStmt.executeQuery();
            if (previousPremiumRs.next()) {
                previousMonthPremium = previousPremiumRs.getInt("Count");
            }

            // Calculate premium growth rate
            if (previousMonthPremium > 0) {
                premiumGrowthRate = ((double)(currentMonthPremium - previousMonthPremium) / previousMonthPremium) * 100;
            } else if (currentMonthPremium > 0) {
                premiumGrowthRate = 100.0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("userGrowthRate", String.format("%.1f", userGrowthRate));
        request.setAttribute("currentMonthPremium", currentMonthPremium);
        request.setAttribute("premiumGrowthRate", String.format("%.1f", premiumGrowthRate));
        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}