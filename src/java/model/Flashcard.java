package model;

import java.sql.Timestamp;

public class Flashcard {
    private int flashcardID;
    private int userID;
    private String title;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean publicFlag;
    private String description;
    private String coverImage;
    private int courseID;

    public Flashcard() {
    }

    public Flashcard(int flashcardID, int userID, String title, Timestamp createdAt, Timestamp updatedAt, boolean publicFlag, String description, String coverImage) {
        this.flashcardID = flashcardID;
        this.userID = userID;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publicFlag = publicFlag;
        this.description = description;
        this.coverImage = coverImage;
    }

    public int getFlashcardID() {
        return flashcardID;
    }

    public void setFlashcardID(int flashcardID) {
        this.flashcardID = flashcardID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isPublicFlag() {
        return publicFlag;
    }

    public void setPublicFlag(boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public int getCourseID() {
        return courseID;
    }
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }
} 