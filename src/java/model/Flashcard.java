package model;

// ===== IMPORT STATEMENTS =====
import java.sql.Timestamp;                  // SQL timestamp utility

// ===== FLASHCARD ENTITY MODEL =====
/**
 * Flashcard - Entity model cho bảng Flashcards trong database
 * Đại diện cho một bộ flashcard trong hệ thống Wasabii
 * 
 * Thuộc tính chính:
 * - Thông tin cơ bản: ID, title, description, coverImage
 * - Quản lý: userID (chủ sở hữu), courseID (thuộc khóa học)
 * - Trạng thái: publicFlag (công khai/riêng tư)
 * - Thời gian: createdAt, updatedAt
 * - Hỗ trợ: Course-based flashcards, User ownership
 */
public class Flashcard {
    
    // ===== INSTANCE VARIABLES =====
    private int flashcardID;                // ID duy nhất của flashcard
    private int userID;                     // ID chủ sở hữu flashcard
    private String title;                   // Tiêu đề flashcard
    private Timestamp createdAt;            // Thời gian tạo
    private Timestamp updatedAt;            // Thời gian cập nhật cuối
    private boolean publicFlag;             // Trạng thái công khai/riêng tư
    private String description;             // Mô tả flashcard
    private String coverImage;              // URL ảnh bìa
    private int courseID;                   // ID khóa học (có thể null)

    // ===== CONSTRUCTORS =====
    
    // ===== DEFAULT CONSTRUCTOR =====
    /**
     * Constructor mặc định
     */
    public Flashcard() {
    }

    // ===== FULL CONSTRUCTOR =====
    /**
     * Constructor đầy đủ với tất cả thông tin
     * @param flashcardID ID của flashcard
     * @param userID ID chủ sở hữu
     * @param title Tiêu đề flashcard
     * @param createdAt Thời gian tạo
     * @param updatedAt Thời gian cập nhật
     * @param publicFlag Trạng thái công khai/riêng tư
     * @param description Mô tả flashcard
     * @param coverImage URL ảnh bìa
     */
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

    // ===== GETTERS AND SETTERS =====
    
    // ===== FLASHCARD ID GETTER/SETTER =====
    /**
     * Lấy ID của flashcard
     * @return ID flashcard
     */
    public int getFlashcardID() {
        return flashcardID;
    }

    /**
     * Đặt ID cho flashcard
     * @param flashcardID ID mới
     */
    public void setFlashcardID(int flashcardID) {
        this.flashcardID = flashcardID;
    }

    // ===== USER ID GETTER/SETTER =====
    /**
     * Lấy ID chủ sở hữu flashcard
     * @return ID chủ sở hữu
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Đặt ID chủ sở hữu cho flashcard
     * @param userID ID chủ sở hữu mới
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    // ===== TITLE GETTER/SETTER =====
    /**
     * Lấy tiêu đề flashcard
     * @return Tiêu đề flashcard
     */
    public String getTitle() {
        return title;
    }

    /**
     * Đặt tiêu đề cho flashcard
     * @param title Tiêu đề mới
     */
    public void setTitle(String title) {
        this.title = title;
    }

    // ===== CREATED AT GETTER/SETTER =====
    /**
     * Lấy thời gian tạo flashcard
     * @return Thời gian tạo
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Đặt thời gian tạo cho flashcard
     * @param createdAt Thời gian tạo mới
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // ===== UPDATED AT GETTER/SETTER =====
    /**
     * Lấy thời gian cập nhật cuối flashcard
     * @return Thời gian cập nhật cuối
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Đặt thời gian cập nhật cuối cho flashcard
     * @param updatedAt Thời gian cập nhật cuối mới
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ===== PUBLIC FLAG GETTER/SETTER =====
    /**
     * Lấy trạng thái công khai/riêng tư của flashcard
     * @return true nếu công khai, false nếu riêng tư
     */
    public boolean isPublicFlag() {
        return publicFlag;
    }

    /**
     * Đặt trạng thái công khai/riêng tư cho flashcard
     * @param publicFlag true để công khai, false để riêng tư
     */
    public void setPublicFlag(boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    // ===== DESCRIPTION GETTER/SETTER =====
    /**
     * Lấy mô tả flashcard
     * @return Mô tả flashcard
     */
    public String getDescription() {
        return description;
    }

    /**
     * Đặt mô tả cho flashcard
     * @param description Mô tả mới
     */
    public void setDescription(String description) {
        this.description = description;
    }

    // ===== COVER IMAGE GETTER/SETTER =====
    /**
     * Lấy URL ảnh bìa flashcard
     * @return URL ảnh bìa
     */
    public String getCoverImage() {
        return coverImage;
    }

    /**
     * Đặt URL ảnh bìa cho flashcard
     * @param coverImage URL ảnh bìa mới
     */
    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    // ===== COURSE ID GETTER/SETTER =====
    /**
     * Lấy ID khóa học của flashcard
     * @return ID khóa học (có thể 0 nếu không thuộc khóa học nào)
     */
    public int getCourseID() {
        return courseID;
    }
    
    /**
     * Đặt ID khóa học cho flashcard
     * @param courseID ID khóa học mới
     */
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }
} 