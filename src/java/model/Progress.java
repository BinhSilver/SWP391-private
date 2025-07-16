package model;

import java.util.Date;

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