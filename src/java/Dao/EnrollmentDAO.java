package Dao;

// ===== IMPORT STATEMENTS =====
import java.sql.*;                          // SQL database operations
import model.Enrollment;                    // Enrollment model
import DB.JDBCConnection;                   // Database connection utility
import com.google.gson.JsonArray;           // JSON array utility
import com.google.gson.JsonObject;          // JSON object utility

// ===== ENROLLMENT DATA ACCESS OBJECT =====
/**
 * EnrollmentDAO - Data Access Object cho Enrollment
 * Quản lý tất cả các thao tác CRUD với bảng Enrollment trong database
 * Bao gồm: thêm, sửa, xóa enrollment, kiểm tra enrollment status
 * Hỗ trợ: Statistics và analytics cho enrollment
 */
public class EnrollmentDAO {

    // ===== ADD ENROLLMENT =====
    /**
     * Thêm enrollment mới (user join khóa học)
     * @param e Enrollment object cần thêm
     * @throws SQLException nếu có lỗi database
     */
    public void add(Enrollment e) throws SQLException {
        // SQL query để insert enrollment mới
        String sql = "INSERT INTO Enrollment (UserID, CourseID) VALUES (?, ?)";
        
        System.out.println("=== [EnrollmentDAO] USER " + e.getUserID() + " JOIN KHÓA HỌC " + e.getCourseID() + " ===");
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setInt(1, e.getUserID());    // UserID
            stmt.setInt(2, e.getCourseID());  // CourseID
            
            // Thực thi query và lấy số rows bị ảnh hưởng
            int result = stmt.executeUpdate();
            System.out.println("[EnrollmentDAO] Kết quả thêm enrollment: " + result + " bản ghi");
            System.out.println("[EnrollmentDAO] User " + e.getUserID() + " đã join thành công khóa học " + e.getCourseID());
            
        } catch (SQLException ex) {
            System.out.println("[EnrollmentDAO] Lỗi khi thêm enrollment: " + ex.getMessage());
            throw ex;
        }
    }

    // ===== UPDATE ENROLLMENT =====
    /**
     * Cập nhật thông tin enrollment
     * @param e Enrollment object cần cập nhật
     * @throws SQLException nếu có lỗi database
     */
    public void update(Enrollment e) throws SQLException {
        // SQL query để update enrollment
        String sql = "UPDATE Enrollment SET UserID=?, CourseID=? WHERE EnrollmentID=?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setInt(1, e.getUserID());        // UserID
            stmt.setInt(2, e.getCourseID());      // CourseID
            stmt.setInt(3, e.getEnrollmentID());  // EnrollmentID
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }

    // ===== DELETE ENROLLMENT =====
    /**
     * Xóa enrollment theo ID
     * @param enrollmentID ID của enrollment cần xóa
     * @throws SQLException nếu có lỗi database
     */
    public void delete(int enrollmentID) throws SQLException {
        // SQL query để xóa enrollment theo ID
        String sql = "DELETE FROM Enrollment WHERE EnrollmentID=?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentID);  // Set EnrollmentID parameter
            
            // Thực thi query
            stmt.executeUpdate();
        }
    }
    
    // ===== GET ENROLLMENTS BY PERIOD =====
    /**
     * Lấy thống kê enrollment theo khoảng thời gian (tháng hoặc năm)
     * @param periodType Loại khoảng thời gian ("month" hoặc "year")
     * @return JsonArray chứa thông tin khoảng thời gian và số lượng enrollment
     */
    public JsonArray getEnrollmentsByPeriod(String periodType) {
        JsonArray jsonArray = new JsonArray();
        
        // SQL query động dựa trên periodType
        String sql = periodType.equals("month") ?
                "SELECT FORMAT(EnrolledAt, 'yyyy-MM') AS Period, COUNT(*) AS EnrollmentCount " +
                "FROM [dbo].[Enrollment] GROUP BY FORMAT(EnrolledAt, 'yyyy-MM') ORDER BY Period" :
                "SELECT YEAR(EnrolledAt) AS Period, COUNT(*) AS EnrollmentCount " +
                "FROM [dbo].[Enrollment] GROUP BY YEAR(EnrolledAt) ORDER BY Period";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // Lặp qua kết quả và tạo JSON objects
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("period", rs.getString("Period"));      // Khoảng thời gian
                obj.addProperty("count", rs.getInt("EnrollmentCount")); // Số lượng enrollment
                jsonArray.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    // ===== CHECK USER ENROLLMENT STATUS =====
    /**
     * Kiểm tra user đã join vào khóa học chưa
     * @param userID ID của user cần kiểm tra
     * @param courseID ID của khóa học cần kiểm tra
     * @return true nếu user đã join, false nếu chưa
     * @throws SQLException nếu có lỗi database
     */
    public boolean isUserEnrolled(int userID, int courseID) throws SQLException {
        // SQL query để kiểm tra enrollment
        String sql = "SELECT COUNT(*) FROM Enrollment WHERE UserID = ? AND CourseID = ?";
        
        System.out.println("=== [EnrollmentDAO] KIỂM TRA USER " + userID + " ĐÃ JOIN KHÓA HỌC " + courseID + " ===");
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Set các tham số cho prepared statement
            stmt.setInt(1, userID);    // UserID
            stmt.setInt(2, courseID);  // CourseID
            
            // Thực thi query và kiểm tra kết quả
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean isEnrolled = rs.getInt(1) > 0;  // Nếu count > 0 thì đã join
                    System.out.println("[EnrollmentDAO] User " + userID + " đã " + (isEnrolled ? "đã" : "chưa") + " join khóa học " + courseID);
                    return isEnrolled;
                }
            }
        } catch (SQLException ex) {
            System.out.println("[EnrollmentDAO] Lỗi khi kiểm tra enrollment: " + ex.getMessage());
            throw ex;
        }
        
        System.out.println("[EnrollmentDAO] User " + userID + " chưa join khóa học " + courseID);
        return false;
    }
}
