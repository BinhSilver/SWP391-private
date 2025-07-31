package model;

/**
 * Tag - Entity model cho bảng Tags trong database
 * Đại diện cho một tag/label được sử dụng để phân loại nội dung
 *
 * Các thuộc tính:
 * - tagID: ID duy nhất của tag
 * - tagName: Tên của tag (ví dụ: "N5", "Grammar", "Vocabulary")
 * - description: Mô tả chi tiết về tag
 *
 * Sử dụng để:
 * - Phân loại khóa học, bài học, từ vựng
 * - Tìm kiếm và lọc nội dung theo tag
 * - Tạo hệ thống tag phổ biến
 */
public class Tag {
    private int tagID;
    private String tagName;

    public Tag() {}

    public Tag(int tagID, String tagName) {
        this.tagID = tagID;
        this.tagName = tagName;
    }

    public int getTagID() {
        return tagID;
    }

    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

   
}
