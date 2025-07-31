package model;

import java.util.Date;

/**
 * Block - Entity model cho bảng Blocks trong database
 * Đại diện cho việc chặn user trong hệ thống
 *
 * Các thuộc tính:
 * - blockID: ID duy nhất của record chặn
 * - blockerID: ID của user thực hiện chặn
 * - blockedID: ID của user bị chặn
 * - reason: Lý do chặn (có thể null)
 * - createdAt: Thời gian thực hiện chặn
 * - isActive: Trạng thái chặn còn hiệu lực không
 *
 * Sử dụng để:
 * - Quản lý việc chặn user trong hệ thống
 * - Ngăn user bị chặn gửi tin nhắn, comment
 * - Hiển thị danh sách user đã chặn
 * - Bảo vệ user khỏi spam, quấy rối
 */
public class Block {
    private int blockerId;
    private int blockedId;
    private Date createdAt;

    // Constructors
    public Block() {}

    public Block(int blockerId, int blockedId, Date createdAt) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}