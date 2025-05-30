/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

public class User {

    private int userID;
    private int roleID;
    private String email;
    private String passwordHash;
    private String googleID;
    private String fullName;
    private Date createdAt;
    private boolean isActive;
    private boolean isLocked;
    private Date birthDate;
    private String phoneNumber;
    private String japaneseLevel;
    private String address;
    private String country;
    private String avatar;
    private String gender;

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
// Thêm vào constructor đầy đủ nếu cần

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

// Getter/setter
    public Date getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

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

    @Override
    public String toString() {
        return "User{" + "userID=" + userID + ", roleID=" + roleID + ", email=" + email + ", passwordHash=" + passwordHash + ", googleID=" + googleID + ", fullName=" + fullName + ", createdAt=" + createdAt + ", isActive=" + isActive + ", isLocked=" + isLocked + ", birthDate=" + birthDate + ", phoneNumber=" + phoneNumber + ", japaneseLevel=" + japaneseLevel + ", address=" + address + ", country=" + country + ", avatar=" + avatar + ", gender=" + gender + '}';
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
}
