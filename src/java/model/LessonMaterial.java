package model;

import java.sql.Timestamp;

/**
 * LessonMaterial - Entity model cho bảng LessonMaterials trong database
 * Đại diện cho tài liệu học tập của một bài học
 *
 * Các thuộc tính:
 * - materialID: ID duy nhất của tài liệu
 * - lessonID: ID của bài học chứa tài liệu này
 * - title: Tiêu đề tài liệu
 * - description: Mô tả tài liệu
 * - fileUrl: URL của file tài liệu (PDF, video, etc.)
 * - fileType: Loại file (pdf, video, audio, image)
 * - fileSize: Kích thước file (bytes)
 * - orderIndex: Thứ tự hiển thị trong bài học
 * - isActive: Trạng thái hoạt động
 *
 * Sử dụng để:
 * - Quản lý tài liệu học tập cho bài học
 * - Hiển thị danh sách tài liệu cho user
 * - Download và xem tài liệu
 * - Sắp xếp thứ tự tài liệu trong bài học
 */
public class LessonMaterial {

    private int materialID;
    private int lessonID;
    private String materialType;
    private String fileType;
    private String title;
    private String filePath;
    private boolean isHidden;
    private Timestamp createdAt;

    public LessonMaterial() {
    }

    public LessonMaterial(int materialID, int lessonID, String materialType, String fileType,
            String title, String filePath, boolean isHidden, Timestamp createdAt) {
        this.materialID = materialID;
        this.lessonID = lessonID;
        this.materialType = materialType;
        this.fileType = fileType;
        this.title = title;
        this.filePath = filePath;
        this.isHidden = isHidden;
        this.createdAt = createdAt;
    }

    // Getter và Setter
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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
