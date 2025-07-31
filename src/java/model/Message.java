package model;

import java.util.Date;

/**
 * Message - Entity model cho bảng Messages trong database
 * Đại diện cho một tin nhắn trong hệ thống chat
 *
 * Các thuộc tính:
 * - messageID: ID duy nhất của tin nhắn
 * - senderID: ID của người gửi
 * - receiverID: ID của người nhận
 * - content: Nội dung tin nhắn
 * - messageType: Loại tin nhắn (text, image, file)
 * - isRead: Trạng thái đã đọc chưa
 * - createdAt: Thời gian gửi tin nhắn
 * - updatedAt: Thời gian cập nhật cuối cùng
 *
 * Sử dụng để:
 * - Lưu trữ tin nhắn giữa các user
 * - Hiển thị lịch sử chat
 * - Đánh dấu tin nhắn đã đọc
 * - Hỗ trợ nhiều loại tin nhắn (text, file, image)
 */
public class Message {
    private int messageId;
    private int conversationId;
    private int senderId;
    private String content;
    private String type;
    private boolean isRead;
    private boolean isRecall;
    private Date sentAt;

    // Constructors
    public Message() {}

    public Message(int messageId, int conversationId, int senderId, String content, String type, boolean isRead, boolean isRecall, Date sentAt) {
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

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }
}