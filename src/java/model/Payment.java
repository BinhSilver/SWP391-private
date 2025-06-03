package model;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author nguye
 */

public class Payment {
    private int paymentID;
    private int userID;
    private int planID;
    private double amount;
    private String paymentDate;
    private String orderCode;
    private String checkoutUrl;
    private String status; // 'PAID', 'PENDING', 'CANCELED'
    private String createdAt;

    // Constructor, getter, setter tương ứng
    public Payment(int paymentID, int userID, int planID, double amount, String paymentDate, String orderCode, String checkoutUrl, String status, String createdAt) {
        this.paymentID = paymentID;
        this.userID = userID;
        this.planID = planID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.orderCode = orderCode;
        this.checkoutUrl = checkoutUrl;
        this.status = status;
        this.createdAt = createdAt;
    }
    public int getPaymentID() {
        return paymentID;
    }
    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public int getPlanID() {
        return planID;
    }
    public void setPlanID(int planID) {
        this.planID = planID;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
    public String getOrderCode() {
        return orderCode;
    }
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
    public String getCheckoutUrl() {
        return checkoutUrl;
    }
    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

