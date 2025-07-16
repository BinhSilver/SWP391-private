package Dao;

import DB.JDBCConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Progress;

public class ProgressDAO {
    private static final Logger LOGGER = Logger.getLogger(ProgressDAO.class.getName());
    
    /**
     * Get progress for a specific user in a course
     * @param userId
     * @param courseId
     * @return List of Progress objects for the user in the course
     */
    public List<Progress> getUserCourseProgress(int userId, int courseId) {
        List<Progress> progressList = new ArrayList<>();
        
        try (Connection conn = JDBCConnection.getConnection()) {
            String sql = "SELECT * FROM Progress WHERE UserID = ? AND CourseID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, courseId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Progress progress = new Progress();
                        progress.setProgressID(rs.getInt("ProgressID"));
                        progress.setUserID(rs.getInt("UserID"));
                        progress.setCourseID(rs.getInt("CourseID"));
                        progress.setLessonID(rs.getInt("LessonID"));
                        progress.setCompletionPercent(rs.getInt("CompletionPercent"));
                        progress.setLastAccessed(rs.getTimestamp("LastAccessed"));
                        progressList.add(progress);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user course progress", e);
        }
        
        return progressList;
    }
    
    /**
     * Get progress for a specific user in a specific lesson
     * @param userId
     * @param lessonId
     * @return Progress object or null if not found
     */
    public Progress getUserLessonProgress(int userId, int lessonId) {
        try (Connection conn = JDBCConnection.getConnection()) {
            String sql = "SELECT * FROM Progress WHERE UserID = ? AND LessonID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, lessonId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Progress progress = new Progress();
                        progress.setProgressID(rs.getInt("ProgressID"));
                        progress.setUserID(rs.getInt("UserID"));
                        progress.setCourseID(rs.getInt("CourseID"));
                        progress.setLessonID(rs.getInt("LessonID"));
                        progress.setCompletionPercent(rs.getInt("CompletionPercent"));
                        progress.setLastAccessed(rs.getTimestamp("LastAccessed"));
                        return progress;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user lesson progress", e);
        }
        
        return null;
    }
    
    /**
     * Update or create progress record for a user in a lesson
     * @param userId
     * @param courseId
     * @param lessonId
     * @param completionPercent
     * @return true if successful, false otherwise
     */
    public boolean updateProgress(int userId, int courseId, int lessonId, int completionPercent) {
        try (Connection conn = JDBCConnection.getConnection()) {
            // Check if record exists
            String checkSql = "SELECT ProgressID FROM Progress WHERE UserID = ? AND LessonID = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, lessonId);
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Update existing record
                        String updateSql = "UPDATE Progress SET CompletionPercent = ?, LastAccessed = GETDATE() WHERE ProgressID = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, completionPercent);
                            updateStmt.setInt(2, rs.getInt("ProgressID"));
                            updateStmt.executeUpdate();
                            LOGGER.log(Level.INFO, "Updated progress for user {0} in lesson {1} to {2}%", new Object[]{userId, lessonId, completionPercent});
                            return true;
                        }
                    } else {
                        // Insert new record
                        String insertSql = "INSERT INTO Progress (UserID, CourseID, LessonID, CompletionPercent, LastAccessed) VALUES (?, ?, ?, ?, GETDATE())";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setInt(2, courseId);
                            insertStmt.setInt(3, lessonId);
                            insertStmt.setInt(4, completionPercent);
                            insertStmt.executeUpdate();
                            LOGGER.log(Level.INFO, "Created progress for user {0} in lesson {1} with {2}%", new Object[]{userId, lessonId, completionPercent});
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating progress", e);
        }
        
        return false;
    }
    
    /**
     * Check if a lesson is completed by a user
     * @param userId
     * @param lessonId
     * @return true if completed (completion percentage is 100), false otherwise
     */
    public boolean isLessonCompleted(int userId, int lessonId) {
        Progress progress = getUserLessonProgress(userId, lessonId);
        return progress != null && progress.getCompletionPercent() == 100;
    }
    
    /**
     * Get all completed lessons for a user in a course
     * @param userId
     * @param courseId
     * @return List of lesson IDs that are completed
     */
    public List<Integer> getCompletedLessons(int userId, int courseId) {
        List<Integer> completedLessons = new ArrayList<>();
        
        try (Connection conn = JDBCConnection.getConnection()) {
            String sql = "SELECT LessonID FROM Progress WHERE UserID = ? AND CourseID = ? AND CompletionPercent = 100";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, courseId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        completedLessons.add(rs.getInt("LessonID"));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting completed lessons", e);
        }
        
        return completedLessons;
    }
    
    /**
     * Get completion percentage for each lesson in a course for a user
     * @param userId
     * @param courseId
     * @return Map of lessonId -> completionPercent
     */
    public Map<Integer, Integer> getLessonCompletionMap(int userId, int courseId) {
        Map<Integer, Integer> completionMap = new HashMap<>();
        
        try (Connection conn = JDBCConnection.getConnection()) {
            String sql = "SELECT LessonID, CompletionPercent FROM Progress WHERE UserID = ? AND CourseID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, courseId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        completionMap.put(rs.getInt("LessonID"), rs.getInt("CompletionPercent"));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting lesson completion map", e);
        }
        
        return completionMap;
    }
    
    /**
     * Calculate and return the overall course completion percentage for a user
     * @param userId
     * @param courseId
     * @return percentage of course completion (0-100)
     */
    public int getCourseCompletionPercentage(int userId, int courseId) {
        try (Connection conn = JDBCConnection.getConnection()) {
            // First get the total number of lessons in the course
            String countSql = "SELECT COUNT(*) AS TotalLessons FROM Lessons WHERE CourseID = ?";
            int totalLessons = 0;
            
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                countStmt.setInt(1, courseId);
                try (ResultSet countRs = countStmt.executeQuery()) {
                    if (countRs.next()) {
                        totalLessons = countRs.getInt("TotalLessons");
                    }
                }
            }
            
            if (totalLessons == 0) {
                return 0; // Avoid division by zero
            }
            
            // Get completed lessons
            String completedSql = "SELECT COUNT(*) AS CompletedLessons FROM Progress WHERE UserID = ? AND CourseID = ? AND CompletionPercent = 100";
            int completedLessons = 0;
            
            try (PreparedStatement completedStmt = conn.prepareStatement(completedSql)) {
                completedStmt.setInt(1, userId);
                completedStmt.setInt(2, courseId);
                
                try (ResultSet completedRs = completedStmt.executeQuery()) {
                    if (completedRs.next()) {
                        completedLessons = completedRs.getInt("CompletedLessons");
                    }
                }
            }
            
            return (completedLessons * 100) / totalLessons;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating course completion percentage", e);
        }
        
        return 0;
    }
    
    /**
     * Check if a lesson should be unlocked based on course progress
     * @param userId the user ID
     * @param courseId the course ID
     * @param lessonId the lesson to check
     * @param lessons ordered list of lessons in the course
     * @return true if the lesson should be unlocked, false if it should be locked
     */
    public boolean isLessonUnlocked(int userId, int courseId, int lessonId, List<model.Lesson> lessons) {
        // First lesson is always unlocked
        if (lessons.isEmpty() || lessons.get(0).getLessonID() == lessonId) {
            return true;
        }
        
        // Find the index of the current lesson
        int lessonIndex = -1;
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonID() == lessonId) {
                lessonIndex = i;
                break;
            }
        }
        
        if (lessonIndex <= 0) {
            // First lesson or lesson not found, first lesson is always unlocked
            return lessonIndex == 0;
        }
        
        // Check if the previous lesson is completed
        int previousLessonId = lessons.get(lessonIndex - 1).getLessonID();
        return isLessonCompleted(userId, previousLessonId);
    }
}
