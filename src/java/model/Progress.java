package model;

import java.util.Date;

/**
 * Progress - Entity model cho bảng Progress trong database
 * Đại diện cho tiến độ học tập của user trong một bài học
 *
 * Các thuộc tính:
 * - progressID: ID duy nhất của progress record
 * - userID: ID của user
 * - courseID: ID của khóa học
 * - lessonID: ID của bài học
 * - completionPercent: Phần trăm hoàn thành (0-100)
 * - lastAccessed: Thời gian truy cập cuối cùng
 * - createdAt: Thời gian tạo record
 * - updatedAt: Thời gian cập nhật cuối cùng
 *
 * Sử dụng để:
 * - Theo dõi tiến độ học tập của user
 * - Kiểm tra bài học đã hoàn thành chưa
 * - Unlock bài học tiếp theo dựa trên tiến độ
 * - Hiển thị progress bar cho user
 */
public class Progress {
    private int progressID;
    private int userID;
    private int courseID;
    private int lessonID;
    private int completionPercent;
    private Date lastAccessed;

    public Progress() {
    }

    public Progress(int progressID, int userID, int courseID, int lessonID, int completionPercent, Date lastAccessed) {
        this.progressID = progressID;
        this.userID = userID;
        this.courseID = courseID;
        this.lessonID = lessonID;
        this.completionPercent = completionPercent;
        this.lastAccessed = lastAccessed;
    }

    public int getProgressID() {
        return progressID;
    }

    public void setProgressID(int progressID) {
        this.progressID = progressID;
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

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public int getCompletionPercent() {
        return completionPercent;
    }

    public void setCompletionPercent(int completionPercent) {
        this.completionPercent = completionPercent;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }
} 