// service/QuizService.java
package service;

import Dao.QuizDAO;
import model.QuizQuestion;
import model.Answer;

import java.util.List;

public class QuizService {

    public static boolean saveQuestions(int lessonId, List<QuizQuestion> questions) {
        return QuizDAO.saveQuestions(lessonId, questions);
    }

    public static List<QuizQuestion> loadQuestions(int lessonId) {
        return QuizDAO.getQuestionsByLessonId(lessonId);
    }

    public static boolean deleteQuiz(int lessonId) {
        return QuizDAO.deleteQuestionsByLessonId(lessonId);
    }

    public static List<Answer> loadAnswersByQuestionId(int questionId) {
        return QuizDAO.getAnswersByQuestionId(questionId);
    }

    public static boolean updateQuestions(int lessonId, List<QuizQuestion> questions) {
        boolean deleted = deleteQuiz(lessonId);
        if (!deleted) return false;
        return saveQuestions(lessonId, questions);
    }

    public static List<QuizQuestion> loadQuizWithAnswers(int lessonId) {
        List<QuizQuestion> questions = QuizDAO.getQuestionsByLessonId(lessonId);
        for (QuizQuestion q : questions) {
            q.setAnswers(QuizDAO.getAnswersByQuestionId(q.getId()));
        }
        return questions;
    }
}
