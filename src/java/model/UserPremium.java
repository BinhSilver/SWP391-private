package model;

import java.util.Date;

/**
 * UserPremium - Entity model cho bảng UserPremium trong database
 * Đại diện cho thông tin premium của một user
 *
 * Các thuộc tính:
 * - userID: ID của user
 * - planID: ID của gói premium (1: Basic, 2: Pro, 3: Enterprise)
 * - startDate: Ngày bắt đầu gói premium
 * - endDate: Ngày kết thúc gói premium
 *
 * Sử dụng để:
 * - Theo dõi trạng thái premium của user
 * - Kiểm tra premium có còn hạn không
 * - Tự động downgrade khi hết hạn
 */
public class UserPremium {
    private int userID;
    private int planID;
    private Date startDate;
    private Date endDate;

    public UserPremium() {}

  

    public UserPremium(int userID, int planID, Date startDate, Date endDate) {
        this.userID = userID;
        this.planID = planID;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
}
