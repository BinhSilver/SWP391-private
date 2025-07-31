package model;

/**
 * Answer - Entity model cho bảng Answers trong database
 * Đại diện cho một đáp án của câu hỏi quiz
 *
 * Các thuộc tính:
 * - answerID: ID duy nhất của đáp án
 * - questionID: ID của câu hỏi chứa đáp án này
 * - answerText: Nội dung đáp án
 * - isCorrect: Có phải đáp án đúng không (1: đúng, 0: sai)
 * - answerNumber: Số thứ tự đáp án (1, 2, 3, 4)
 *
 * Sử dụng để:
 * - Quản lý các đáp án của câu hỏi quiz
 * - Hiển thị các lựa chọn cho user
 * - Kiểm tra đáp án đúng khi user trả lời
 * - Tính điểm và thống kê kết quả quiz
 */
public class Answer {

    private int id; // AnswerID
    private int questionId; // FK
    private String answerText;
    private int answerNumber; // 1=A, 2=B, 3=C, 4=D
    private int isCorrect; // 1 = đúng, 0 = sai

    public int getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(int isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Answer() {
    }

    // Constructor đầy đủ
    public Answer(int id, int questionId, String answerText, int answerNumber, int isCorrect) {
        this.id = id;
        this.questionId = questionId;
        this.answerText = answerText;
        this.answerNumber = answerNumber;
        this.isCorrect = isCorrect;
    }

    // Constructor cũ vẫn giữ lại nếu cần
    public Answer(int id, int questionId, String answerText, int answerNumber) {
        this(id, questionId, answerText, answerNumber, 0); // mặc định isCorrect = 0
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public int getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(int answerNumber) {
        this.answerNumber = answerNumber;
    }
}
