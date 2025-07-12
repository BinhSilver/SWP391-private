package model;

import java.sql.Timestamp;
import java.util.Date;

public class Payment {
    private int paymentID;
    private int userID;
    private int planID;
    private double amount;
    private Date paymentDate;
    private String transactionNo;
    private String bankCode;
    private String orderInfo;
    private String responseCode;
    private String transactionStatus;
    private long orderCode;
    private String checkoutUrl;
    private String status;
    private Timestamp createdAt;

    // Constructors
    public Payment() {}

    public Payment(int userID, int planID, double amount, String orderInfo, 
                   String transactionStatus, long orderCode, String checkoutUrl) {
        this.userID = userID;
        this.planID = planID;
        this.amount = amount;
        this.orderInfo = orderInfo;
        this.transactionStatus = transactionStatus;
        this.orderCode = orderCode;
        this.checkoutUrl = checkoutUrl;
        this.status = "PENDING";
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
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

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(long orderCode) {
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID=" + paymentID +
                ", userID=" + userID +
                ", planID=" + planID +
                ", amount=" + amount +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", orderCode=" + orderCode +
                ", status='" + status + '\'' +
                '}';
    }
} 