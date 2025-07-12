package model;

public class Course {

    private int courseID;
    private String title;
    private String description;
    private boolean isHidden;
    private boolean isSuggested;
    private String imageUrl;

    public Course() {
    }

    public Course(int courseID, String title, String description, boolean isHidden, boolean isSuggested, String imageUrl) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
        this.isHidden = isHidden;
        this.isSuggested = isSuggested;
        this.imageUrl = imageUrl;
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
                + ", imageUrl='" + imageUrl + '\''
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

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isSuggested() {
        return isSuggested;
    }

    public void setSuggested(boolean suggested) {
        isSuggested = suggested;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
