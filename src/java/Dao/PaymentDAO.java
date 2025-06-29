package Dao;

import DB.JDBCConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PaymentDAO {

    public JsonObject getRevenueStatsByPeriod(String periodType) throws SQLException {
        JsonObject result = new JsonObject();

        // Get current date
        LocalDate currentDate = LocalDate.now();
        String startDate;
        String endDate;

        // Determine date range based on periodType
        if (periodType.equalsIgnoreCase("month")) {
            // First day of the current month
            startDate = currentDate.withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            // First day of the next month
            endDate = currentDate.plusMonths(1).withDayOfMonth(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            // First day of the current year
            startDate = currentDate.withDayOfYear(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            // First day of the next year
            endDate = currentDate.plusYears(1).withDayOfYear(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        try (Connection conn = JDBCConnection.getConnection()) {
            // Query for plan purchase counts and percentages
            String sql = "SELECT pp.PlanName AS plan, COUNT(p.PaymentID) AS count, " +
                        "CAST(COUNT(p.PaymentID) * 100.0 / SUM(COUNT(p.PaymentID)) OVER () AS DECIMAL(5, 1)) AS percent " +
                        "FROM Payments p JOIN PremiumPlans pp ON p.PlanID = pp.PlanID " +
                        "WHERE p.PaymentDate >= ? AND p.PaymentDate < ? AND p.TransactionStatus = 'Success' " +
                        "GROUP BY pp.PlanName";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();

            JsonArray plansArray = new JsonArray();
            while (rs.next()) {
                JsonObject plan = new JsonObject();
                plan.addProperty("plan", rs.getString("plan"));
                plan.addProperty("count", rs.getInt("count"));
                plan.addProperty("percent", rs.getString("percent"));
                plansArray.add(plan);
                System.out.println("Plan: " + rs.getString("plan") + ", Count: " + rs.getInt("count") + ", Percent: " + rs.getString("percent"));
            }
            result.add("plans", plansArray);

            // Query for total revenue
            sql = "SELECT SUM(Amount) AS totalRevenue FROM Payments WHERE PaymentDate >= ? AND PaymentDate < ? AND TransactionStatus = 'Success'";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            rs = stmt.executeQuery();
            double totalRevenue = rs.next() ? rs.getDouble("totalRevenue") : 0;
            result.addProperty("totalRevenue", totalRevenue);

            // Query for number of purchasers
            sql = "SELECT COUNT(DISTINCT UserID) AS purchaserCount FROM Payments WHERE PaymentDate >= ? AND PaymentDate < ? AND TransactionStatus = 'Success'";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            rs = stmt.executeQuery();
            int purchaserCount = rs.next() ? rs.getInt("purchaserCount") : 0;
            result.addProperty("purchaserCount", purchaserCount);

            System.out.println("JSON trả về: " + result.toString());
        }

        return result;
    }
}