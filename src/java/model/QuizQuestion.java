package model;

import java.util.List;

/**
 * QuizQuestion - Entity model cho bảng Questions trong database
 * Đại diện cho một câu hỏi trong bài quiz
 *
 * Các thuộc tính:
 * - questionID: ID duy nhất của câu hỏi
 * - quizID: ID của quiz chứa câu hỏi này
 * - question: Nội dung câu hỏi
 * - correctAnswer: Đáp án đúng (số thứ tự từ 1-4)
 * - timeLimit: Thời gian giới hạn trả lời (giây)
 * - answers: Danh sách các đáp án (Answer objects)
 *
 * Sử dụng để:
 * - Tạo và quản lý câu hỏi quiz
 * - Hiển thị câu hỏi cho user
 * - Kiểm tra đáp án đúng
 * - Tính thời gian trả lời
 */
public class QuizQuestion {
    private int id; // QuestionID
    private int quizId;
    private String question;
    private int correctAnswer; // 1 = A, 2 = B, 3 = C, 4 = D
    private int timeLimit; // seconds

    private List<Answer> answers; // danh sách các đáp án

    public QuizQuestion() {}

    public QuizQuestion(int id, int quizId, String question, int correctAnswer, int timeLimit, List<Answer> answers) {
        this.id = id;
        this.quizId = quizId;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.timeLimit = timeLimit;
        this.answers = answers;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    // Optional: get correct Answer Text
    public String getCorrectAnswerText() {
        if (answers != null) {
            for (Answer a : answers) {
                if (a.getAnswerNumber() == correctAnswer) {
                    return a.getAnswerText();
                }
            }
        }
        return null;
    }
}
