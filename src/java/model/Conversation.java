package model;

import java.util.Date;

/**
 * Conversation - Entity model cho bảng Conversations trong database
 * Đại diện cho một cuộc hội thoại trong hệ thống chat
 *
 * Các thuộc tính:
 * - conversationID: ID duy nhất của cuộc hội thoại
 * - userID: ID của user tham gia hội thoại
 * - title: Tiêu đề cuộc hội thoại (có thể tự động tạo)
 * - lastMessage: Tin nhắn cuối cùng trong hội thoại
 * - createdAt: Thời gian tạo cuộc hội thoại
 * - updatedAt: Thời gian cập nhật cuối cùng
 * - isActive: Trạng thái hoạt động
 *
 * Sử dụng để:
 * - Quản lý các cuộc hội thoại của user
 * - Hiển thị danh sách chat cho user
 * - Lưu trữ lịch sử hội thoại
 * - Phân loại và tìm kiếm cuộc hội thoại
 */
public class Conversation {
    private int conversationId;
    private int user1Id;
    private int user2Id;
    private Date createdAt;

    // Constructors
    public Conversation() {}

    public Conversation(int conversationId, int user1Id, int user2Id, Date createdAt) {
        this.conversationId = conversationId;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(int user1Id) {
        this.user1Id = user1Id;
    }

    public int getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(int user2Id) {
        this.user2Id = user2Id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}