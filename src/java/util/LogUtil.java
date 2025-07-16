package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for application logging
 */
public class LogUtil {
    private static final Logger LOGGER = Logger.getLogger(LogUtil.class.getName());
    private static final String LOG_DIRECTORY = "logs";
    private static final String PROGRESS_LOG_FILE = "progress.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    static {
        // Ensure log directory exists
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            try {
                boolean created = logDir.mkdir();
                if (!created) {
                    LOGGER.log(Level.WARNING, "Could not create log directory: {0}", LOG_DIRECTORY);
                }
            } catch (SecurityException e) {
                LOGGER.log(Level.SEVERE, "Security exception creating log directory", e);
            }
        }
    }
    
    /**
     * Log progress update to file
     * 
     * @param userId User ID
     * @param lessonId Lesson ID
     * @param courseId Course ID
     * @param completionPercent Progress percentage
     */
    public static void logProgress(int userId, int lessonId, int courseId, int completionPercent) {
        try {
            File logFile = new File(LOG_DIRECTORY, PROGRESS_LOG_FILE);
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                String timestamp = DATE_FORMAT.format(new Date());
                String logEntry = String.format("[%s] User ID: %d, Course ID: %d, Lesson ID: %d, Progress: %d%%",
                        timestamp, userId, courseId, lessonId, completionPercent);
                writer.println(logEntry);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to progress log", e);
        }
    }
    
    /**
     * Log completed lesson to file
     * 
     * @param userId User ID
     * @param lessonId Lesson ID
     * @param courseId Course ID
     */
    public static void logLessonCompletion(int userId, int lessonId, int courseId) {
        try {
            File logFile = new File(LOG_DIRECTORY, PROGRESS_LOG_FILE);
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                String timestamp = DATE_FORMAT.format(new Date());
                String logEntry = String.format("[%s] COMPLETION - User ID: %d, Course ID: %d, Lesson ID: %d",
                        timestamp, userId, courseId, lessonId);
                writer.println(logEntry);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to progress log", e);
        }
    }
} 