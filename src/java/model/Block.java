package model;

import java.sql.Timestamp;

public class Block {
    private int blockerId;
    private int blockedId;
    private Timestamp createdAt;

    // Constructors
    public Block() {}

    public Block(int blockerId, int blockedId, Timestamp createdAt) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getBlockerId() {
        return blockerId;
    }

    public void setBlockerId(int blockerId) {
        this.blockerId = blockerId;
    }

    public int getBlockedId() {
        return blockedId;
    }

    public void setBlockedId(int blockedId) {
        this.blockedId = blockedId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}