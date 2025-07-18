// service/QuizService.java
package service;

import Dao.QuizDAO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import model.QuizQuestion;
import model.Answer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuizService {

    public void processQuizForLesson(HttpServletRequest request, int lessonId, int idx) {
        Set<Integer> questionIndexes = new HashSet<>();
        for (String param : request.getParameterMap().keySet()) {
            Matcher m = Pattern.compile("lessons\\[" + idx + "]\\[questions\\]\\[(\\d+)\\]\\[question\\]").matcher(param);
            if (m.find()) {
                questionIndexes.add(Integer.parseInt(m.group(1)));
            }
        }
        System.out.println("[QuizService] lessonIdx=" + idx + ", lessonId=" + lessonId + ", found questionIndexes=" + questionIndexes);
        List<QuizQuestion> questions = new ArrayList<>();
        for (Integer qIdx : questionIndexes) {
            String qText = request.getParameter("lessons[" + idx + "][questions][" + qIdx + "][question]");
            String optionA = request.getParameter("lessons[" + idx + "][questions][" + qIdx + "][optionA]");
            String optionB = request.getParameter("lessons[" + idx + "][questions][" + qIdx + "][optionB]");
            String optionC = request.getParameter("lessons[" + idx + "][questions][" + qIdx + "][optionC]");
            String optionD = request.getParameter("lessons[" + idx + "][questions][" + qIdx + "][optionD]");
            String answer = request.getParameter("lessons[" + idx + "][questions][" + qIdx + "][answer]");
            System.out.println("[QuizService] Parse Q" + qIdx + ": " + qText + " | A=" + optionA + ", B=" + optionB + ", C=" + optionC + ", D=" + optionD + ", answer=" + answer);
            int correctAnswer = "A".equals(answer) ? 1 : "B".equals(answer) ? 2 : "C".equals(answer) ? 3 : 4;

            List<Answer> answers = Arrays.asList(
                    new Answer(0, 0, optionA, 1, "A".equals(answer) ? 1 : 0),
                    new Answer(0, 0, optionB, 2, "B".equals(answer) ? 1 : 0),
                    new Answer(0, 0, optionC, 3, "C".equals(answer) ? 1 : 0),
                    new Answer(0, 0, optionD, 4, "D".equals(answer) ? 1 : 0)
            );
            QuizQuestion q = new QuizQuestion();
            q.setQuestion(qText);
            q.setCorrectAnswer(correctAnswer);
            q.setAnswers(answers);
            q.setTimeLimit(60);
            questions.add(q);
        }
        System.out.println("[QuizService] Parsed QuizQuestion list for lessonIdx=" + idx + ": " + questions);
        if (!questions.isEmpty()) {
            QuizDAO.deleteQuestionsByLessonId(lessonId);
            QuizDAO.saveQuestions(lessonId, questions);
        }
    }
    
    public void processQuizFromJson(HttpServletRequest request, Map<Integer, Integer> lessonIndexToIdMap) {
        String quizJson = request.getParameter("quizJson");
        System.out.println("processQuizFromJson quizJson=" + quizJson);
        if (quizJson == null || quizJson.trim().isEmpty()) {
            System.out.println("Không có quizJson để process!");
            return;
        }
        
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> quizList = gson.fromJson(quizJson, listType);

            for (Map<String, Object> quizObj : quizList) {
                Integer lessonIndex = ((Double) quizObj.get("lessonIndex")).intValue();
                Integer lessonId = lessonIndexToIdMap.get(lessonIndex);

                if (lessonId == null) {
                    System.out.println("Bỏ qua lessonIndex " + lessonIndex + " vì không tìm thấy lessonId.");
                    continue;
                }
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> questions = (List<Map<String, Object>>) quizObj.get("questions");
                
                if (questions == null || questions.isEmpty()) {
                    QuizDAO.deleteQuestionsByLessonId(lessonId);
                    System.out.println("Không có question nào cho lessonId " + lessonId + ", xóa hết quiz.");
                    continue;
                }
                
                List<QuizQuestion> quizQuestions = new ArrayList<>();
                for (Map<String, Object> q : questions) {
                    String questionText = (String) q.get("question");
                    String optionA = (String) q.get("optionA");
                    String optionB = (String) q.get("optionB");
                    String optionC = (String) q.get("optionC");
                    String optionD = (String) q.get("optionD");
                    String answer = (String) q.get("answer");
                    if (questionText == null || questionText.trim().isEmpty()) {
                        continue;
                    }
                    int correctAnswer = "A".equals(answer) ? 1 : "B".equals(answer) ? 2 : "C".equals(answer) ? 3 : 4;
                    List<Answer> answers = Arrays.asList(
                            new Answer(0, 0, optionA, 1, "A".equals(answer) ? 1 : 0),
                            new Answer(0, 0, optionB, 2, "B".equals(answer) ? 1 : 0),
                            new Answer(0, 0, optionC, 3, "C".equals(answer) ? 1 : 0),
                            new Answer(0, 0, optionD, 4, "D".equals(answer) ? 1 : 0)
                    );
                    QuizQuestion quizQuestion = new QuizQuestion();
                    quizQuestion.setQuestion(questionText);
                    quizQuestion.setCorrectAnswer(correctAnswer);
                    quizQuestion.setAnswers(answers);
                    quizQuestion.setTimeLimit(60);
                    quizQuestions.add(quizQuestion);
                }
                if (!quizQuestions.isEmpty()) {
                    QuizDAO.deleteQuestionsByLessonId(lessonId);
                    QuizDAO.saveQuestions(lessonId, quizQuestions);
                    System.out.println("Đã lưu quiz cho lessonId=" + lessonId + ", count=" + quizQuestions.size());
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: processQuizFromJson - Parse lỗi!");
            e.printStackTrace();
            throw e;
        }
    }
    
    public static boolean saveQuestions(int lessonId, List<QuizQuestion> questions) {
        return QuizDAO.saveQuestions(lessonId, questions);
    }

    public static List<QuizQuestion> loadQuestions(int lessonId) {
        return QuizDAO.getQuestionsWithAnswersByLessonId(lessonId);
    }

    public static boolean deleteQuiz(int lessonId) {
        return QuizDAO.deleteQuestionsByLessonId(lessonId);
    }

    public static List<Answer> loadAnswersByQuestionId(int questionId) {
        return QuizDAO.getAnswersByQuestionId(questionId);
    }

    public static boolean updateQuestions(int lessonId, List<QuizQuestion> questions) {
        boolean deleted = deleteQuiz(lessonId);
        if (!deleted) {
            return false;
        }
        return saveQuestions(lessonId, questions);
    }

    public static List<QuizQuestion> loadQuizWithAnswers(int lessonId) {
        return QuizDAO.getQuestionsWithAnswersByLessonId(lessonId);
    }

    public static int getCourseIdByLessonId(int lessonId) {
        return QuizDAO.getCourseIdByLessonId(lessonId);
    }
    
    // ✅ Cập nhật câu hỏi quiz
    public static boolean updateQuestion(int questionId, String questionText, int timeLimit) {
        return QuizDAO.updateQuestion(questionId, questionText, timeLimit);
    }
    
    // ✅ Cập nhật đáp án
    public static boolean updateAnswer(int answerId, String answerText, int isCorrect) {
        return QuizDAO.updateAnswer(answerId, answerText, isCorrect);
    }
    
    // ✅ Xóa câu hỏi cụ thể
    public static boolean deleteQuestion(int questionId) {
        return QuizDAO.deleteQuestion(questionId);
    }
    
    // ✅ Lấy thống kê quiz
    public static Map<String, Object> getQuizStats(int lessonId) {
        return QuizDAO.getQuizStatsByLessonId(lessonId);
    }
    
    // ✅ Kiểm tra lesson có quiz hay không
    public static boolean hasQuiz(int lessonId) {
        return QuizDAO.hasQuiz(lessonId);
    }
    
    // ✅ Lấy tất cả quiz theo course
    public static Map<Integer, List<QuizQuestion>> getAllQuizzesByCourse(int courseId) {
        return QuizDAO.getAllQuizzesByCourseId(courseId);
    }
    
    // ✅ Backup quiz
    public static boolean backupQuiz(int lessonId, String backupName) {
        return QuizDAO.backupQuiz(lessonId, backupName);
    }
    
    // ✅ Restore quiz
    public static boolean restoreQuiz(int lessonId, String backupName) {
        return QuizDAO.restoreQuiz(lessonId, backupName);
    }
    
    // ✅ Validate quiz data
    public static boolean validateQuizData(List<QuizQuestion> questions) {
        if (questions == null || questions.isEmpty()) {
            return false;
        }
        
        for (QuizQuestion question : questions) {
            // Check question text
            if (question.getQuestion() == null || question.getQuestion().trim().isEmpty()) {
                return false;
            }
            
            // Check answers
            if (question.getAnswers() == null || question.getAnswers().size() != 4) {
                return false;
            }
            
            // Check that exactly one answer is correct
            int correctCount = 0;
            for (Answer answer : question.getAnswers()) {
                if (answer.getAnswerText() == null || answer.getAnswerText().trim().isEmpty()) {
                    return false;
                }
                if (answer.getIsCorrect() == 1) {
                    correctCount++;
                }
            }
            
            if (correctCount != 1) {
                return false;
            }
        }
        
        return true;
    }
    
    // ✅ Duplicate quiz from one lesson to another
    public static boolean duplicateQuiz(int sourceLessonId, int targetLessonId) {
        List<QuizQuestion> sourceQuestions = loadQuestions(sourceLessonId);
        if (sourceQuestions.isEmpty()) {
            return false;
        }
        
        return saveQuestions(targetLessonId, sourceQuestions);
    }
    
    // ✅ Get quiz summary for course
    public static Map<String, Object> getCourseQuizSummary(int courseId) {
        Map<String, Object> summary = new java.util.HashMap<>();
        Map<Integer, List<QuizQuestion>> courseQuizzes = getAllQuizzesByCourse(courseId);
        
        int totalLessons = courseQuizzes.size();
        int totalQuestions = 0;
        
        for (List<QuizQuestion> questions : courseQuizzes.values()) {
            totalQuestions += questions.size();
        }
        
        summary.put("totalLessons", totalLessons);
        summary.put("totalQuestions", totalQuestions);
        summary.put("averageQuestionsPerLesson", totalLessons > 0 ? (double) totalQuestions / totalLessons : 0.0);
        
        return summary;
    }
}
