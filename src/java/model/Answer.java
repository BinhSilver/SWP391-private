package model;

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
