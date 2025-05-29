package model;

import java.util.Date;

public class Enrollment {
    private int enrollmentID;
    private int userID;
    private int courseID;
    private Date enrolledAt;

    public Enrollment() {}

    public Enrollment(int enrollmentID, int userID, int courseID, Date enrolledAt) {
        this.enrollmentID = enrollmentID;
        this.userID = userID;
        this.courseID = courseID;
        this.enrolledAt = enrolledAt;
    }

    public int getEnrollmentID() {
        return enrollmentID;
    }

    public void setEnrollmentID(int enrollmentID) {
        this.enrollmentID = enrollmentID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public Date getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Date enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

 
}
