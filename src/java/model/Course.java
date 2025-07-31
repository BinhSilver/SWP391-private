package model;

// ===== COURSE ENTITY MODEL =====
/**
 * Course - Entity model cho bảng Courses trong database
 * Đại diện cho một khóa học trong hệ thống Wasabii
 * 
 * Thuộc tính chính:
 * - Thông tin cơ bản: ID, title, description, imageUrl
 * - Trạng thái: isHidden (ẩn/hiện), isSuggested (đề xuất)
 * - Quản lý: createdBy (người tạo)
 * - Hỗ trợ: Teacher-specific courses, Admin management
 */
public class Course {

    // ===== INSTANCE VARIABLES =====
    private int courseID;                   // ID duy nhất của khóa học
    private String title;                   // Tiêu đề khóa học
    private String description;             // Mô tả khóa học
    private boolean isHidden;               // Trạng thái ẩn/hiện khóa học
    private boolean isSuggested;            // Có được đề xuất không
    private int createdBy;                  // ID người tạo khóa học
    private String imageUrl;                // URL ảnh đại diện khóa học
    private double averageRating;           // Điểm đánh giá trung bình
    private int ratingCount;                // Số lượng đánh giá

    // ===== CONSTRUCTORS =====
    
    // ===== DEFAULT CONSTRUCTOR =====
    /**
     * Constructor mặc định
     */
    public Course() {
    }

    // ===== FULL CONSTRUCTOR WITH IMAGE =====
    /**
     * Constructor đầy đủ với image URL
     * @param courseID ID của khóa học
     * @param title Tiêu đề khóa học
     * @param description Mô tả khóa học
     * @param isHidden Trạng thái ẩn/hiện
     * @param isSuggested Có được đề xuất không
     * @param imageUrl URL ảnh đại diện
     */
    public Course(int courseID, String title, String description, boolean isHidden, boolean isSuggested, String imageUrl) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
        this.isHidden = isHidden;
        this.isSuggested = isSuggested;
        this.imageUrl = imageUrl;
    }

    // ===== FULL CONSTRUCTOR WITH CREATOR =====
    /**
     * Constructor đầy đủ với thông tin người tạo
     * @param courseID ID của khóa học
     * @param title Tiêu đề khóa học
     * @param description Mô tả khóa học
     * @param isHidden Trạng thái ẩn/hiện
     * @param isSuggested Có được đề xuất không
     * @param createdBy ID người tạo khóa học
     */
    public Course(int courseID, String title, String description, boolean isHidden, boolean isSuggested, int createdBy) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
        this.isHidden = isHidden;
        this.isSuggested = isSuggested;
        this.createdBy = createdBy;
    }

    // ===== BASIC CONSTRUCTOR =====
    /**
     * Constructor cơ bản với thông tin tối thiểu
     * @param courseID ID của khóa học
     * @param title Tiêu đề khóa học
     * @param description Mô tả khóa học
     */
    public Course(int courseID, String title, String description) {
        this.courseID = courseID;
        this.title = title;
        this.description = description;
    }

    // ===== TO STRING METHOD =====
    /**
     * Chuyển đổi Course object thành String để debug
     * @return String representation của Course
     */
    @Override
    public String toString() {
        return "Course{"
                + "courseID=" + courseID
                + ", title='" + title + '\''
                + ", description='" + description + '\''
                + ", isHidden=" + isHidden
                + ", isSuggested=" + isSuggested
                + ", createdBy=" + createdBy
                + ", imageUrl='" + imageUrl + '\''
                + ", averageRating=" + averageRating
                + ", ratingCount=" + ratingCount
                + '}';
    }

    // ===== GETTERS AND SETTERS =====
    
    // ===== COURSE ID GETTER/SETTER =====
    /**
     * Lấy ID của khóa học
     * @return ID khóa học
     */
    public int getCourseID() {
        return courseID;
    }

    /**
     * Đặt ID cho khóa học
     * @param courseID ID mới
     */
    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    // ===== TITLE GETTER/SETTER =====
    /**
     * Lấy tiêu đề khóa học
     * @return Tiêu đề khóa học
     */
    public String getTitle() {
        return title;
    }

    /**
     * Đặt tiêu đề cho khóa học
     * @param title Tiêu đề mới
     */
    public void setTitle(String title) {
        this.title = title;
    }

    // ===== DESCRIPTION GETTER/SETTER =====
    /**
     * Lấy mô tả khóa học
     * @return Mô tả khóa học
     */
    public String getDescription() {
        return description;
    }

    /**
     * Đặt mô tả cho khóa học
     * @param description Mô tả mới
     */
    public void setDescription(String description) {
        this.description = description;
    }

    // ===== HIDDEN STATUS GETTER/SETTER =====
    /**
     * Lấy trạng thái ẩn/hiện của khóa học
     * @return true nếu ẩn, false nếu hiện
     */
    public boolean getHidden() {
        return isHidden;
    }

    /**
     * Đặt trạng thái ẩn/hiện cho khóa học
     * @param hidden true để ẩn, false để hiện
     */
    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    // ===== SUGGESTED STATUS GETTER/SETTER =====
    /**
     * Lấy trạng thái đề xuất của khóa học
     * @return true nếu được đề xuất, false nếu không
     */
    public boolean isSuggested() {
        return isSuggested;
    }

    /**
     * Đặt trạng thái đề xuất cho khóa học
     * @param suggested true để đề xuất, false để không đề xuất
     */
    public void setSuggested(boolean suggested) {
        isSuggested = suggested;
    }

    // ===== CREATED BY GETTER/SETTER =====
    /**
     * Lấy ID người tạo khóa học
     * @return ID người tạo
     */
    public int getCreatedBy() {
        return createdBy;
    }

    /**
     * Đặt ID người tạo cho khóa học
     * @param createdBy ID người tạo mới
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    // ===== IMAGE URL GETTER/SETTER =====
    /**
     * Lấy URL ảnh đại diện khóa học
     * @return URL ảnh
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Đặt URL ảnh đại diện cho khóa học
     * @param imageUrl URL ảnh mới
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // ===== AVERAGE RATING GETTER/SETTER =====
    /**
     * Lấy điểm đánh giá trung bình của khóa học
     * @return Điểm đánh giá trung bình
     */
    public double getAverageRating() {
        return averageRating;
    }

    /**
     * Đặt điểm đánh giá trung bình cho khóa học
     * @param averageRating Điểm đánh giá trung bình mới
     */
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // ===== RATING COUNT GETTER/SETTER =====
    /**
     * Lấy số lượng đánh giá của khóa học
     * @return Số lượng đánh giá
     */
    public int getRatingCount() {
        return ratingCount;
    }

    /**
     * Đặt số lượng đánh giá cho khóa học
     * @param ratingCount Số lượng đánh giá mới
     */
    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
