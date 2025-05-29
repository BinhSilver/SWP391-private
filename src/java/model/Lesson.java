package model;

public class Lesson {
    private int lessonID;
    private int courseID;
    private String title;
    private boolean isHidden;

    public Lesson() {}

    public Lesson(int lessonID, int courseID, String title, boolean isHidden) {
        this.lessonID = lessonID;
        this.courseID = courseID;
        this.title = title;
        this.isHidden = isHidden;
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

  
}
