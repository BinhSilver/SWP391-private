/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    public Answer(int id, int questionId, String answerText, int answerNumber) {
        this.id = id;
        this.questionId = questionId;
        this.answerText = answerText;
        this.answerNumber = answerNumber;
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
