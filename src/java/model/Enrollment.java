package model;

import java.util.Date;

/**
 * Enrollment - Entity model cho bảng Enrollment trong database
 * Đại diện cho việc đăng ký tham gia khóa học của user
 *
 * Các thuộc tính:
 * - enrollmentID: ID duy nhất của enrollment
 * - userID: ID của user đăng ký
 * - courseID: ID của khóa học được đăng ký
 * - enrolledAt: Thời gian đăng ký tham gia
 *
 * Sử dụng để:
 * - Theo dõi user nào đã tham gia khóa học nào
 * - Kiểm tra quyền truy cập khóa học
 * - Tính số lượng học viên của khóa học
 * - Hiển thị danh sách khóa học đã đăng ký cho user
 */
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
