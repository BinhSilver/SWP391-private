package model;

public class Course {
    private int courseID;
    private String title;
    private String description;
    private boolean isHidden;
    private boolean isSuggested; // thêm thuộc tính mới

    public Course() {}

    public Course(int courseID, String title, String description, boolean isHidden, boolean isSuggested) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
        this.isHidden = isHidden;
        this.isSuggested = isSuggested;
    }

    public Course(int courseID, String title, String description) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseID=" + courseID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isHidden=" + isHidden +
                ", isSuggested=" + isSuggested +
                '}';
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

    public boolean isIsHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isIsSuggested() {
        return isSuggested;
    }

    public void setIsSuggested(boolean isSuggested) {
        this.isSuggested = isSuggested;
    }
}
