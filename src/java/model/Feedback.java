package model;

import java.util.Date;

/**
 * Feedback - Entity model cho bảng Feedback trong database
 * Đại diện cho một đánh giá/feedback của user về khóa học
 *
 * Các thuộc tính chính:
 * - feedbackID: ID duy nhất của feedback
 * - userID: ID của user viết feedback
 * - courseID: ID của khóa học được đánh giá
 * - content: Nội dung feedback
 * - rating: Đánh giá sao (1-5 sao)
 * - createdAt: Thời gian tạo feedback
 *
 * Các thuộc tính hiển thị (không lưu trong DB):
 * - userName: Tên user để hiển thị
 * - userAvatar: Avatar user để hiển thị
 * - totalLikes: Tổng số lượt like
 * - totalDislikes: Tổng số lượt dislike
 */
public class Feedback {
    private int feedbackID;
    private int userID;
    private int courseID;
    private String content;
    private int rating; // 1-5 sao
    private Date createdAt;
    // Thông tin hiển thị
    private String userName;
    private String userAvatar;
    private int totalLikes;
    private int totalDislikes;

    public Feedback() {}

    public Feedback(int feedbackID, int userID, int courseID, String content, int rating, Date createdAt) {
        this.feedbackID = feedbackID;
        this.userID = userID;
        this.courseID = courseID;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    // Getter/setter
    public int getFeedbackID() { return feedbackID; }
    public void setFeedbackID(int feedbackID) { this.feedbackID = feedbackID; }
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    public int getCourseID() { return courseID; }
    public void setCourseID(int courseID) { this.courseID = courseID; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
    public int getTotalLikes() { return totalLikes; }
    public void setTotalLikes(int totalLikes) { this.totalLikes = totalLikes; }
    public int getTotalDislikes() { return totalDislikes; }
    public void setTotalDislikes(int totalDislikes) { this.totalDislikes = totalDislikes; }
} 