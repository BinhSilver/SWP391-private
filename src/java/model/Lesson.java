package model;

public class Lesson {

    private int lessonID;
    private int courseID;
    private String title;
    private boolean isHidden;
    private String description;
    private int orderIndex;

    public Lesson() {
    }

    // ✅ Constructor 5 tham số (không có orderIndex) để code cũ dùng
    public Lesson(int lessonID, int courseID, String title, boolean isHidden, String description) {
        this.lessonID = lessonID;
        this.courseID = courseID;
        this.title = title;
        this.isHidden = isHidden;
        this.description = description;
    }

    // ✅ Constructor 6 tham số (có orderIndex)
    public Lesson(int lessonID, int courseID, String title, boolean isHidden, String description, int orderIndex) {
        this.lessonID = lessonID;
        this.courseID = courseID;
        this.title = title;
        this.isHidden = isHidden;
        this.description = description;
        this.orderIndex = orderIndex;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
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

    public boolean isIsHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}
