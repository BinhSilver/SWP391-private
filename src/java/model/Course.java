package model;

public class Course {

    private int courseID;
    private String title;
    private String description;
    private boolean isHidden;
    private boolean isSuggested; // thêm thuộc tính mới
    private int createdBy; // ID người tạo khóa học

    public Course() {
    }

    public Course(int courseID, String title, String description, boolean isHidden, boolean isSuggested) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
        this.isHidden = isHidden;
        this.isSuggested = isSuggested;
    }

    public Course(int courseID, String title, String description, boolean isHidden, boolean isSuggested, int createdBy) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
        this.isHidden = isHidden;
        this.isSuggested = isSuggested;
        this.createdBy = createdBy;
    }

    public Course(int courseID, String title, String description) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Course{"
                + "courseID=" + courseID
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", isHidden=" + isHidden
                + ", isSuggested=" + isSuggested
                + ", createdBy=" + createdBy
                + '}';
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isSuggested() {
        return isSuggested;
    }

    public void setSuggested(boolean suggested) {
        this.isSuggested = suggested;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

}
