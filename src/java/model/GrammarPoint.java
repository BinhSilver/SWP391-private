package model;

/**
 * GrammarPoint - Entity model cho bảng GrammarPoints trong database
 * Đại diện cho một điểm ngữ pháp trong bài học
 *
 * Các thuộc tính:
 * - grammarID: ID duy nhất của điểm ngữ pháp
 * - lessonID: ID của bài học chứa điểm ngữ pháp này
 * - title: Tiêu đề điểm ngữ pháp
 * - explanation: Giải thích chi tiết về ngữ pháp
 * - examples: Các ví dụ sử dụng (JSON string)
 * - difficulty: Mức độ khó (1-5)
 *
 * Sử dụng để:
 * - Quản lý điểm ngữ pháp trong bài học
 * - Hiển thị giải thích ngữ pháp cho user
 * - Cung cấp ví dụ sử dụng
 * - Phân loại theo mức độ khó
 */
public class GrammarPoint {
    private int grammarID;
    private int lessonID;
    private String title;
    private String explanation;

    public GrammarPoint() {}

    public int getGrammarID() {
        return grammarID;
    }

    public void setGrammarID(int grammarID) {
        this.grammarID = grammarID;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

}
