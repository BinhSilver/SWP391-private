package model;

public class Room {
    private int roomID;
    private int hostUserID;
    private String languageLevel;
    private String genderPreference;
    private int minAge;
    private int maxAge;
    private boolean allowApproval;
    private boolean isActive;

    public Room() {}

    public Room(int hostUserID, String languageLevel, String genderPreference, int minAge, int maxAge, boolean allowApproval) {
        this.hostUserID = hostUserID;
        this.languageLevel = languageLevel;
        this.genderPreference = genderPreference;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.allowApproval = allowApproval;
        this.isActive = true; // Mặc định là true
    }

    public int getRoomID() { return roomID; }
    public void setRoomID(int roomID) { this.roomID = roomID; }

    public int getHostUserID() { return hostUserID; }
    public void setHostUserID(int hostUserID) { this.hostUserID = hostUserID; }

    public String getLanguageLevel() { return languageLevel; }
    public void setLanguageLevel(String languageLevel) { this.languageLevel = languageLevel; }

    public String getGenderPreference() { return genderPreference; }
    public void setGenderPreference(String genderPreference) { this.genderPreference = genderPreference; }

    public int getMinAge() { return minAge; }
    public void setMinAge(int minAge) { this.minAge = minAge; }

    public int getMaxAge() { return maxAge; }
    public void setMaxAge(int maxAge) { this.maxAge = maxAge; }

    public boolean isAllowApproval() { return allowApproval; }
    public void setAllowApproval(boolean allowApproval) { this.allowApproval = allowApproval; }

    public boolean isActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}