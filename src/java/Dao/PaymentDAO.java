package Dao;

import DB.JDBCConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class PaymentDAO {

    /**
     * Insert new payment record into database
     * @param payment Payment object to insert
     * @return PaymentID if successful, -1 if failed
     */
    public int insertPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO Payments (UserID, PlanID, Amount, OrderInfo, TransactionStatus, " +
                    "OrderCode, CheckoutUrl, Status, CreatedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("=== PaymentDAO.insertPayment: Starting payment insertion ===");
        System.out.println("Payment details: " + payment.toString());
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, payment.getUserID());
            stmt.setInt(2, payment.getPlanID());
            stmt.setDouble(3, payment.getAmount());
            stmt.setString(4, payment.getOrderInfo());
            stmt.setString(5, payment.getTransactionStatus());
            stmt.setLong(6, payment.getOrderCode());
            stmt.setString(7, payment.getCheckoutUrl());
            stmt.setString(8, payment.getStatus());
            stmt.setTimestamp(9, payment.getCreatedAt());
            
            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: UserID=" + payment.getUserID() + 
                             ", PlanID=" + payment.getPlanID() + 
                             ", Amount=" + payment.getAmount() + 
                             ", OrderCode=" + payment.getOrderCode());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int paymentId = generatedKeys.getInt(1);
                        System.out.println("✓ Payment inserted successfully with PaymentID: " + paymentId);
                        return paymentId;
                    }
                }
            }
            
            System.out.println("✗ Payment insertion failed - no rows affected");
            return -1;
            
        } catch (SQLException e) {
            System.out.println("✗ PaymentDAO.insertPayment failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Update payment status - using provided connection for transaction support
     * @param orderCode Order code to update
     * @param transactionStatus New transaction status
     * @param transactionNo Transaction number from PayOS
     * @param responseCode Response code from PayOS
     * @param conn Database connection to use (for transaction support)
     * @return true if update successful, false otherwise
     */
    public boolean updatePaymentStatus(long orderCode, String transactionStatus, 
                                     String transactionNo, String responseCode, Connection conn) throws SQLException {
        String sql = "UPDATE Payments SET TransactionStatus = ?, TransactionNo = ?, " +
                    "ResponseCode = ?, PaymentDate = ?, Status = ? WHERE OrderCode = ?";
        
        System.out.println("=== PaymentDAO.updatePaymentStatus (with connection): Starting payment update ===");
        System.out.println("OrderCode: " + orderCode);
        System.out.println("New Status: " + transactionStatus);
        System.out.println("TransactionNo: " + transactionNo);
        System.out.println("ResponseCode: " + responseCode);
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transactionStatus);
            stmt.setString(2, transactionNo);
            stmt.setString(3, responseCode);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Payment date as current time
            stmt.setString(5, "COMPLETED"); // Status field
            stmt.setLong(6, orderCode);
            
            System.out.println("Executing update SQL with provided connection: " + sql);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                System.out.println("✓ Payment status updated successfully for OrderCode: " + orderCode);
                return true;
            } else {
                System.out.println("✗ Payment update failed - no rows affected for OrderCode: " + orderCode);
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("✗ PaymentDAO.updatePaymentStatus (with connection) failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Update payment status - original method creating its own connection
     * @param orderCode Order code to update
     * @param transactionStatus New transaction status
     * @param transactionNo Transaction number from PayOS
     * @param responseCode Response code from PayOS
     * @return true if update successful, false otherwise
     */
    public boolean updatePaymentStatus(long orderCode, String transactionStatus, 
                                     String transactionNo, String responseCode) throws SQLException {
        String sql = "UPDATE Payments SET TransactionStatus = ?, TransactionNo = ?, " +
                    "ResponseCode = ?, PaymentDate = ?, Status = ? WHERE OrderCode = ?";
        
        System.out.println("=== PaymentDAO.updatePaymentStatus: Starting payment update ===");
        System.out.println("OrderCode: " + orderCode);
        System.out.println("New Status: " + transactionStatus);
        System.out.println("TransactionNo: " + transactionNo);
        System.out.println("ResponseCode: " + responseCode);
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, transactionStatus);
            stmt.setString(2, transactionNo);
            stmt.setString(3, responseCode);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // Payment date as current time
            stmt.setString(5, "COMPLETED"); // Status field
            stmt.setLong(6, orderCode);
            
            System.out.println("Executing update SQL: " + sql);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                System.out.println("✓ Payment status updated successfully for OrderCode: " + orderCode);
                return true;
            } else {
                System.out.println("✗ Payment update failed - no rows affected for OrderCode: " + orderCode);
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("✗ PaymentDAO.updatePaymentStatus failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Get payment by order code
     * @param orderCode Order code to search for
     * @return Payment object if found, null otherwise
     */
    public Payment getPaymentByOrderCode(long orderCode) throws SQLException {
        String sql = "SELECT * FROM Payments WHERE OrderCode = ?";
        
        System.out.println("=== PaymentDAO.getPaymentByOrderCode: Searching for payment ===");
        System.out.println("OrderCode: " + orderCode);
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, orderCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = extractPaymentFromResultSet(rs);
                    System.out.println("✓ Payment found: " + payment.toString());
                    return payment;
                } else {
                    System.out.println("✗ No payment found for OrderCode: " + orderCode);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("✗ PaymentDAO.getPaymentByOrderCode failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Extract Payment object from ResultSet
     */
    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentID(rs.getInt("PaymentID"));
        payment.setUserID(rs.getInt("UserID"));
        payment.setPlanID(rs.getInt("PlanID"));
        payment.setAmount(rs.getDouble("Amount"));
        payment.setPaymentDate(rs.getTimestamp("PaymentDate"));
        payment.setTransactionNo(rs.getString("TransactionNo"));
        payment.setOrderInfo(rs.getString("OrderInfo"));
        payment.setResponseCode(rs.getString("ResponseCode"));
        payment.setTransactionStatus(rs.getString("TransactionStatus"));
        payment.setOrderCode(rs.getLong("OrderCode"));
        payment.setCheckoutUrl(rs.getString("CheckoutUrl"));
        payment.setStatus(rs.getString("Status"));
        payment.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return payment;
    }

    public JsonObject getRevenueStatsByPeriod(String periodType) throws SQLException {
        JsonObject result = new JsonObject();

        // Get current date
        LocalDate currentDate = LocalDate.now();
        String startDate;
        String endDate;

        // Determine date range based on periodType
        if (periodType.equalsIgnoreCase("month")) {
            // Query all data from the beginning of 2024 to now (since we have data from 2024-12)
            startDate = "2024-01-01";
            endDate = currentDate.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            // Query all data from the beginning of 2024 to now
            startDate = "2024-01-01";
            endDate = currentDate.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }

        System.out.println("PaymentDAO: Querying revenue data for period: " + periodType);
        System.out.println("Current date: " + currentDate);
        System.out.println("Start date: " + startDate + ", End date: " + endDate);

        try (Connection conn = JDBCConnection.getConnection()) {
            // Initialize with default values
            JsonArray plansArray = new JsonArray();
            double totalRevenue = 0.0;
            int purchaserCount = 0;

            // Query for plan purchase counts and percentages (fixed reserved keyword issue)
            String sql = "SELECT pp.PlanName AS planName, COUNT(p.PaymentID) AS planCount, " +
                        "CAST(COUNT(p.PaymentID) * 100.0 / SUM(COUNT(p.PaymentID)) OVER () AS DECIMAL(5, 1)) AS planPercent " +
                        "FROM Payments p JOIN PremiumPlans pp ON p.PlanID = pp.PlanID " +
                        "WHERE p.PaymentDate >= ? AND p.PaymentDate < ? AND p.TransactionStatus = 'Success' " +
                        "GROUP BY pp.PlanName";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            
            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: startDate=" + startDate + ", endDate=" + endDate);
            
            ResultSet rs = stmt.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                JsonObject plan = new JsonObject();
                plan.addProperty("plan", rs.getString("planName"));
                plan.addProperty("count", rs.getInt("planCount"));
                plan.addProperty("percent", rs.getString("planPercent"));
                plansArray.add(plan);
                System.out.println("Plan: " + rs.getString("planName") + ", Count: " + rs.getInt("planCount") + ", Percent: " + rs.getString("planPercent"));
            }

            // If no data found, add a default entry (using English to avoid encoding issues)
            if (!hasData) {
                System.out.println("No payment data found for the specified period");
                JsonObject defaultPlan = new JsonObject();
                defaultPlan.addProperty("plan", "No Data Available");
                defaultPlan.addProperty("count", 0);
                defaultPlan.addProperty("percent", "0");
                plansArray.add(defaultPlan);
            }

            result.add("plans", plansArray);

            // Query for total revenue
            sql = "SELECT ISNULL(SUM(Amount), 0) AS totalRevenue FROM Payments WHERE PaymentDate >= ? AND PaymentDate < ? AND TransactionStatus = 'Success'";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            rs = stmt.executeQuery();
            if (rs.next()) {
                totalRevenue = rs.getDouble("totalRevenue");
            }
            result.addProperty("totalRevenue", totalRevenue);
            System.out.println("Total revenue: " + totalRevenue);

            // Query for number of purchasers
            sql = "SELECT COUNT(DISTINCT UserID) AS purchaserCount FROM Payments WHERE PaymentDate >= ? AND PaymentDate < ? AND TransactionStatus = 'Success'";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            rs = stmt.executeQuery();
            if (rs.next()) {
                purchaserCount = rs.getInt("purchaserCount");
            }
            result.addProperty("purchaserCount", purchaserCount);
            System.out.println("Purchaser count: " + purchaserCount);

            System.out.println("PaymentDAO: JSON result: " + result.toString());
        } catch (SQLException e) {
            System.out.println("PaymentDAO SQL Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return result;
    }

    public JsonObject getPaymentStatsByStatus(String periodType) throws SQLException {
        JsonObject result = new JsonObject();

        // Get current date and set date range
        LocalDate currentDate = LocalDate.now();
        String startDate = "2024-01-01";
        String endDate = currentDate.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);

        System.out.println("PaymentDAO: Querying payment status stats for period: " + periodType);
        System.out.println("Date range: " + startDate + " to " + endDate);

        try (Connection conn = JDBCConnection.getConnection()) {
            // Query for payment status statistics
            String sql = "SELECT " +
                        "TransactionStatus, " +
                        "COUNT(*) AS statusCount, " +
                        "CAST(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER () AS DECIMAL(5, 1)) AS statusPercent " +
                        "FROM Payments " +
                        "WHERE PaymentDate >= ? AND PaymentDate < ? " +
                        "GROUP BY TransactionStatus " +
                        "ORDER BY statusCount DESC";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            
            System.out.println("Executing status SQL: " + sql);
            ResultSet rs = stmt.executeQuery();

            JsonArray statusArray = new JsonArray();
            int totalTransactions = 0;
            
            while (rs.next()) {
                JsonObject status = new JsonObject();
                String statusName = rs.getString("TransactionStatus");
                int count = rs.getInt("statusCount");
                String percent = rs.getString("statusPercent");
                
                status.addProperty("status", statusName);
                status.addProperty("count", count);
                status.addProperty("percent", percent);
                statusArray.add(status);
                totalTransactions += count;
                
                System.out.println("Status: " + statusName + ", Count: " + count + ", Percent: " + percent + "%");
            }

            result.add("statusStats", statusArray);
            result.addProperty("totalTransactions", totalTransactions);

            // Query for total transactions by users
            sql = "SELECT COUNT(*) AS totalTransactions, COUNT(DISTINCT UserID) AS uniqueUsers FROM Payments WHERE PaymentDate >= ? AND PaymentDate < ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                result.addProperty("totalTransactionCount", rs.getInt("totalTransactions"));
                result.addProperty("uniqueUserCount", rs.getInt("uniqueUsers"));
            }

            System.out.println("PaymentDAO Status Stats: " + result.toString());
        } catch (SQLException e) {
            System.out.println("PaymentDAO Status SQL Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return result;
    }

    public JsonObject getPaymentTrendsByMonth() throws SQLException {
        JsonObject result = new JsonObject();

        System.out.println("PaymentDAO: Querying payment trends by month");

        try (Connection conn = JDBCConnection.getConnection()) {
            // Query for monthly trends (revenue and transaction count)
            String sql = "SELECT " +
                        "FORMAT(PaymentDate, 'yyyy-MM') AS monthYear, " +
                        "COUNT(*) AS transactionCount, " +
                        "SUM(CASE WHEN TransactionStatus = 'Success' THEN Amount ELSE 0 END) AS monthlyRevenue, " +
                        "COUNT(CASE WHEN TransactionStatus = 'Success' THEN 1 END) AS successCount, " +
                        "COUNT(CASE WHEN TransactionStatus = 'Failed' THEN 1 END) AS failedCount " +
                        "FROM Payments " +
                        "WHERE PaymentDate >= '2024-01-01' " +
                        "GROUP BY FORMAT(PaymentDate, 'yyyy-MM') " +
                        "ORDER BY monthYear";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            System.out.println("Executing trends SQL: " + sql);
            ResultSet rs = stmt.executeQuery();

            JsonArray monthsArray = new JsonArray();
            JsonArray revenueArray = new JsonArray();
            JsonArray transactionArray = new JsonArray();
            JsonArray successArray = new JsonArray();
            
            while (rs.next()) {
                String month = rs.getString("monthYear");
                int transactionCount = rs.getInt("transactionCount");
                double revenue = rs.getDouble("monthlyRevenue");
                int successCount = rs.getInt("successCount");
                
                monthsArray.add(month);
                revenueArray.add(revenue);
                transactionArray.add(transactionCount);
                successArray.add(successCount);
                
                System.out.println("Month: " + month + ", Transactions: " + transactionCount + 
                                 ", Revenue: " + revenue + ", Success: " + successCount);
            }

            result.add("months", monthsArray);
            result.add("revenue", revenueArray);
            result.add("transactions", transactionArray);
            result.add("successTransactions", successArray);

            System.out.println("PaymentDAO Trends: " + result.toString());
        } catch (SQLException e) {
            System.out.println("PaymentDAO Trends SQL Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return result;
    }
}