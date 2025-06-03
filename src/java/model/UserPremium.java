package model;

import java.util.Date;

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
