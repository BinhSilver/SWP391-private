package model;

import java.util.Date;

public class RoomParticipant {
    private int roomID;
    private int userID;
    private Date joinedAt;

    public RoomParticipant() {
    }

    public RoomParticipant(int roomID, int userID, Date joinedAt) {
        this.roomID = roomID;
        this.userID = userID;
        this.joinedAt = joinedAt;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }
}
