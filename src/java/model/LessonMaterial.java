package model;

public class LessonMaterial {
    private int materialID;
    private int lessonID;
    private String materialType;
    private String title;
    private String filePath;
    private boolean isHidden;

    public LessonMaterial() {}

    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isIsHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public LessonMaterial(int materialID, int lessonID, String materialType, String title, String filePath, boolean isHidden) {
        this.materialID = materialID;
        this.lessonID = lessonID;
        this.materialType = materialType;
        this.title = title;
        this.filePath = filePath;
        this.isHidden = isHidden;
    }

   
}
