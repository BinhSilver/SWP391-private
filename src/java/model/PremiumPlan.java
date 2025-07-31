package model;

/**
 * PremiumPlan - Entity model cho bảng PremiumPlans trong database
 * Đại diện cho một gói premium có thể mua
 *
 * Các thuộc tính:
 * - planID: ID duy nhất của gói premium
 * - planName: Tên gói (Basic, Pro, Enterprise)
 * - description: Mô tả chi tiết về gói
 * - price: Giá tiền của gói
 * - duration: Thời hạn gói (số ngày)
 * - features: Các tính năng của gói (JSON string)
 *
 * Sử dụng để:
 * - Hiển thị danh sách gói premium cho user
 * - Xử lý thanh toán và mua gói
 * - Quản lý các tính năng của từng gói
 */
public class PremiumPlan {
    private int planID;
    private String planName;
    private double price;
    private int durationInMonths;
    private String description;

    public PremiumPlan() {}

    public PremiumPlan(int planID, String planName, double price, int durationInMonths, String description) {
        this.planID = planID;
        this.planName = planName;
        this.price = price;
        this.durationInMonths = durationInMonths;
        this.description = description;
    }

    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(int durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
