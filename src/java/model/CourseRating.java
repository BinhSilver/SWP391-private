package model;

import java.util.Date;

/**
 * CourseRating - Entity model cho bảng CourseRatings trong database
 * Đại diện cho đánh giá sao của user về khóa học
 *
 * Các thuộc tính:
 * - ratingID: ID duy nhất của đánh giá
 * - userID: ID của user đánh giá
 * - courseID: ID của khóa học được đánh giá
 * - rating: Số sao đánh giá (1-5)
 * - comment: Nhận xét chi tiết (có thể null)
 * - createdAt: Thời gian tạo đánh giá
 * - updatedAt: Thời gian cập nhật cuối cùng
 *
 * Sử dụng để:
 * - Lưu trữ đánh giá sao của user về khóa học
 * - Tính điểm trung bình của khóa học
 * - Hiển thị đánh giá cho user khác
 * - Phân tích chất lượng khóa học
 */
public class CourseRating {
    private int ratingID;
    private int userID;
    private int courseID;
    private int rating;
    private String comment;
    private Date ratedAt;

    public CourseRating() {}

    public int getRatingID() {
        return ratingID;
    }

    public void setRatingID(int ratingID) {
        this.ratingID = ratingID;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(Date ratedAt) {
        this.ratedAt = ratedAt;
    }

}
