/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

// ===== IMPORT STATEMENTS =====
import java.time.LocalDate;                 // Local date utility
import java.time.Period;                    // Period calculation
import java.time.ZoneId;                    // Zone ID utility
import java.util.Date;                      // Date utility

// ===== USER ENTITY MODEL =====
/**
 * User - Entity model cho bảng Users trong database
 * Đại diện cho một user trong hệ thống Wasabii
 * 
 * Các thuộc tính chính:
 * - Thông tin cơ bản: ID, email, password, fullName
 * - Vai trò: roleID (1=Free, 2=Premium, 3=Teacher, 4=Admin)
 * - Trạng thái: isActive, isLocked, isTeacherPending
 * - Thông tin cá nhân: birthDate, phoneNumber, address, country
 * - Hỗ trợ Google OAuth: googleID
 * - Teacher features: certificatePath
 */
public class User {

    // ===== INSTANCE VARIABLES =====
    private int userID;                     // ID duy nhất của user
    private int roleID;                     // Vai trò: 1=Free, 2=Premium, 3=Teacher, 4=Admin
    private String email;                   // Email đăng nhập
    private String passwordHash;            // Password đã hash
    private String googleID;                // ID từ Google OAuth (có thể null)
    private String fullName;                // Họ và tên đầy đủ
    private Date createdAt;                 // Ngày tạo tài khoản
    private boolean isActive;               // Trạng thái hoạt động
    private boolean isLocked;               // Trạng thái khóa tài khoản
    private Date birthDate;                 // Ngày sinh
    private String phoneNumber;             // Số điện thoại
    private String japaneseLevel;           // Trình độ tiếng Nhật (N5, N4, N3, N2, N1)
    private String address;                 // Địa chỉ
    private String country;                 // Quốc gia
    private String avatar;                  // URL avatar
    private String gender;                  // Giới tính
    private boolean isTeacherPending;       // Đang chờ xác nhận làm giáo viên
    private String certificatePath;         // Đường dẫn file chứng chỉ (cho teacher)

    // ===== CONSTRUCTORS =====
    
    // ===== FULL CONSTRUCTOR =====
    /**
     * Constructor đầy đủ với tất cả thông tin
     * @param userID ID của user
     * @param roleID Vai trò của user
     * @param email Email của user
     * @param passwordHash Password đã hash
     * @param googleID ID từ Google OAuth
     * @param fullName Họ và tên đầy đủ
     * @param createdAt Ngày tạo tài khoản
     * @param isActive Trạng thái hoạt động
     * @param isLocked Trạng thái khóa tài khoản
     * @param birthDate Ngày sinh
     * @param phoneNumber Số điện thoại
     * @param japaneseLevel Trình độ tiếng Nhật
     * @param address Địa chỉ
     * @param country Quốc gia
     * @param avatar URL avatar
     * @param gender Giới tính
     */
    public User(int userID, int roleID, String email, String passwordHash, String googleID, String fullName, Date createdAt, boolean isActive, boolean isLocked, Date birthDate, String phoneNumber, String japaneseLevel, String address, String country, String avatar, String gender) {
        this.userID = userID;
        this.roleID = roleID;
        this.email = email;
        this.passwordHash = passwordHash;
        this.googleID = googleID;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.isLocked = isLocked;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.japaneseLevel = japaneseLevel;
        this.address = address;
        this.country = country;
        this.avatar = avatar;
        this.gender = gender;
    }

    // ===== BASIC CONSTRUCTOR =====
    /**
     * Constructor cơ bản với thông tin tối thiểu
     * @param userID ID của user
     * @param roleID Vai trò của user
     * @param email Email của user
     * @param passwordHash Password đã hash
     * @param googleID ID từ Google OAuth
     * @param fullName Họ và tên đầy đủ
     * @param createdAt Ngày tạo tài khoản
     * @param isActive Trạng thái hoạt động
     * @param isLocked Trạng thái khóa tài khoản
     */
    public User(int userID, int roleID, String email, String passwordHash, String googleID, String fullName, Date createdAt, boolean isActive, boolean isLocked) {
        this.userID = userID;
        this.roleID = roleID;
        this.email = email;
        this.passwordHash = passwordHash;
        this.googleID = googleID;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.isLocked = isLocked;
    }

    // ===== EXTENDED CONSTRUCTOR =====
    /**
     * Constructor mở rộng với thông tin cá nhân
     * @param userID ID của user
     * @param roleID Vai trò của user
     * @param email Email của user
     * @param passwordHash Password đã hash
     * @param googleID ID từ Google OAuth
     * @param fullName Họ và tên đầy đủ
     * @param createdAt Ngày tạo tài khoản
     * @param isActive Trạng thái hoạt động
     * @param isLocked Trạng thái khóa tài khoản
     * @param birthDate Ngày sinh
     * @param phoneNumber Số điện thoại
     * @param japaneseLevel Trình độ tiếng Nhật
     * @param address Địa chỉ
     * @param country Quốc gia
     * @param avatar URL avatar
     */
    public User(int userID, int roleID, String email, String passwordHash, String googleID,
            String fullName, Date createdAt, boolean isActive, boolean isLocked,
            Date birthDate, String phoneNumber, String japaneseLevel, String address,
            String country, String avatar) {
        this.userID = userID;
        this.roleID = roleID;
        this.email = email;
        this.passwordHash = passwordHash;
        this.googleID = googleID;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.isLocked = isLocked;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.japaneseLevel = japaneseLevel;
        this.address = address;
        this.country = country;
        this.avatar = avatar;
    }

    // ===== GETTERS AND SETTERS =====
    
    // ===== BIRTH DATE GETTER/SETTER =====
    /**
     * Lấy ngày sinh của user
     * @return Ngày sinh
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Lấy giới tính của user
     * @return Giới tính
     */
    public String getGender() {
        return gender;
    }

    /**
     * Đặt giới tính cho user
     * @param gender Giới tính mới
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Đặt ngày sinh cho user
     * @param birthDate Ngày sinh mới
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJapaneseLevel() {
        return japaneseLevel;
    }

    public void setJapaneseLevel(String japaneseLevel) {
        this.japaneseLevel = japaneseLevel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isTeacherPending() {
        return isTeacherPending;
    }
    public void setTeacherPending(boolean isTeacherPending) {
        this.isTeacherPending = isTeacherPending;
    }
    public String getCertificatePath() {
        return certificatePath;
    }
    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", roleID=" + roleID +
                ", email='" + email + '\'' +
                ", isTeacherPending=" + isTeacherPending +
                ", certificatePath='" + certificatePath + '\'' +
                ", fullName=" + fullName +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                ", isLocked=" + isLocked +
                ", birthDate=" + birthDate +
                ", phoneNumber=" + phoneNumber +
                ", japaneseLevel=" + japaneseLevel +
                ", address=" + address +
                ", country=" + country +
                ", avatar=" + avatar +
                ", gender=" + gender +
                '}';
    }

  

    public User() {
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isIsLocked() {
        return isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isAdmin() {
        return this.roleID == 4; // Assuming roleID 4 is for admin -- chỉnh chỗ này theo code của mng. T set để test 
    }

    public int getAge() {
        if (birthDate == null) {
            return -1;
        }

        LocalDate birth;
        if (birthDate instanceof java.sql.Date) {
            birth = ((java.sql.Date) birthDate).toLocalDate();
        } else {
            birth = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        LocalDate today = LocalDate.now();
        return Period.between(birth, today).getYears();
    }
}

