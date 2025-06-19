
package controller.admin;

import Dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

@WebServlet("/userManagement")
public class UserManagementServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    try {
        // Lấy các tham số từ request
        String searchTerm = request.getParameter("search");
        String selectedRole = request.getParameter("role");
        String selectedStatus = request.getParameter("status");
        int page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        int pageSize = 10; // Default pageSize set to 50

        // Đếm tổng số bản ghi đã lọc
        int totalUsers = getTotalFilteredUsers(searchTerm, selectedRole, selectedStatus);
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);

        // Đảm bảo page nằm trong phạm vi hợp lệ
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages > 0 ? totalPages : 1;

        // Lấy danh sách người dùng đã lọc và phân trang
        List<User> users = getFilteredUsers(searchTerm, selectedRole, selectedStatus, page, pageSize);

        // Đặt các thuộc tính để chuyển tới JSP
        request.setAttribute("users", users);
        request.setAttribute("searchTerm", searchTerm != null ? searchTerm : "");
        request.setAttribute("selectedRole", selectedRole != null ? selectedRole : "All Roles");
        request.setAttribute("selectedStatus", selectedStatus != null ? selectedStatus : "All Status");
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalUsers", totalUsers); // Thêm totalUsers để JSP sử dụng

        // Chuyển hướng đến JSP
        request.getRequestDispatcher("userManagement.jsp").forward(request, response);

    } catch (SQLException e) {
        e.printStackTrace();
        request.getSession().setAttribute("error", "Lỗi khi lấy danh sách người dùng: " + e.getMessage());
        response.sendRedirect(request.getContextPath() + "/userManagement");
    } catch (NumberFormatException e) {
        e.printStackTrace();
        request.getSession().setAttribute("error", "Lỗi định dạng tham số: " + e.getMessage());
        response.sendRedirect(request.getContextPath() + "/userManagement");
    }
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            int userId = Integer.parseInt(request.getParameter("userId"));

            User user = userDAO.getUserById(userId);
            if (user != null) {
                if ("block".equals(action)) {
                    user.setLocked(true);
                    user.setActive(false);
                    userDAO.updateUser(user);
                    request.getSession().setAttribute("message", "Tài khoản đã được khóa thành công.");
                } else if ("active".equals(action)) {
                    user.setLocked(false);
                    user.setActive(true);
                    userDAO.updateUser(user);
                    request.getSession().setAttribute("message", "Tài khoản đã được kích hoạt thành công.");
                }
            } else {
                request.getSession().setAttribute("error", "Không tìm thấy người dùng.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi cập nhật trạng thái người dùng: " + e.getMessage());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi định dạng ID người dùng: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/userManagement");
    }

    private List<User> getFilteredUsers(String searchTerm, String role, String status, int page, int pageSize) throws SQLException {
        List<User> allUsers = userDAO.getAllUsers();
        List<User> filteredUsers = new ArrayList<>();

        // Lọc người dùng theo các tiêu chí
        for (User user : allUsers) {
            boolean matchesSearch = searchTerm == null || searchTerm.isEmpty() ||
                    user.getFullName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(searchTerm.toLowerCase());

            boolean matchesRole = role == null || role.equals("All Roles") ||
                    (role.equals("1") && user.getRoleID() == 1) ||
                    (role.equals("2") && user.getRoleID() == 2) ||
                    (role.equals("3") && user.getRoleID() == 3);

            boolean matchesStatus = status == null || status.equals("All Status") ||
                    (status.equals("Active") && user.isActive() && !user.isLocked()) ||
                    (status.equals("Inactive") && !user.isActive()) ||
                    (status.equals("Suspended") && user.isLocked());

            if (matchesSearch && matchesRole && matchesStatus) {
                filteredUsers.add(user);
            }
        }

        // Phân trang
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredUsers.size());
        if (start >= filteredUsers.size()) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu trang không hợp lệ
        }
        return filteredUsers.subList(start, end);
    }

    private int getTotalFilteredUsers(String searchTerm, String role, String status) throws SQLException {
        List<User> allUsers = userDAO.getAllUsers();
        int count = 0;

        for (User user : allUsers) {
            boolean matchesSearch = searchTerm == null || searchTerm.isEmpty() ||
                    user.getFullName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    user.getEmail().toLowerCase().contains(searchTerm.toLowerCase());

            boolean matchesRole = role == null || role.equals("All Roles") ||
                    (role.equals("1") && user.getRoleID() == 1) ||
                    (role.equals("2") && user.getRoleID() == 2) ||
                    (role.equals("3") && user.getRoleID() == 3);

            boolean matchesStatus = status == null || status.equals("All Status") ||
                    (status.equals("Active") && user.isActive() && !user.isLocked()) ||
                    (status.equals("Inactive") && !user.isActive()) ||
                    (status.equals("Suspended") && user.isLocked());

            if (matchesSearch && matchesRole && matchesStatus) {
                count++;
            }
        }
        return count;
    }
}
