/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Role - Entity model cho bảng Roles trong database
 * Đại diện cho vai trò của user trong hệ thống
 *
 * Các thuộc tính:
 * - roleID: ID duy nhất của role
 * - roleName: Tên vai trò (Free, Premium, Teacher, Admin)
 * - description: Mô tả chi tiết về vai trò
 *
 * Các vai trò trong hệ thống:
 * - 1: Free User - Người dùng miễn phí
 * - 2: Premium User - Người dùng trả phí
 * - 3: Teacher - Giáo viên
 * - 4: Admin - Quản trị viên
 */
public class Role {
    private int roleID;
    private String roleName;

    public Role(int roleID, String roleName) {
        this.roleID = roleID;
        this.roleName = roleName;
    }

    public int getRoleID() { return roleID; }
    public void setRoleID(int roleID) { this.roleID = roleID; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    
}
