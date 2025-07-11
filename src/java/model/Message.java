package model;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int conversationId;
    private int senderId;
    private String content;
    private String type;
    private boolean isRead;
    private boolean isRecall;
    private Timestamp sentAt;

    // Constructors
    public Message() {}

    public Message(int messageId, int conversationId, int senderId, String content, String type, boolean isRead, boolean isRecall, Timestamp sentAt) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.type = type;
        this.isRead = isRead;
        this.isRecall = isRecall;
        this.sentAt = sentAt;
    }

    // Getters and Setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean isRecall() {
        return isRecall;
    }

    public void setRecall(boolean isRecall) {
        this.isRecall = isRecall;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
}