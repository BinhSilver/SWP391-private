/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Dao;

import DB.JDBCConnection;
import java.sql.*;
import model.Payment;

/**
 *
 * @author nguye
 */
public class PaymentDAO {
    public static void save(Payment payment) {
        String sql = "INSERT INTO Payments (UserID, PlanID, Amount, OrderCode, CheckoutUrl, Status, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payment.getUserID());
            ps.setInt(2, payment.getPlanID());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, payment.getOrderCode());
            ps.setString(5, payment.getCheckoutUrl());
            ps.setString(6, payment.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateStatus(String orderCode, String newStatus) {
        String sql = "UPDATE Payments SET Status = ? WHERE OrderCode = ?";
        try (Connection conn = JDBCConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, orderCode);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
