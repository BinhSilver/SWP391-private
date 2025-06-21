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

@WebServlet("/userDetail")
public class UserDetailServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy userId từ request
            String userIdParam = request.getParameter("userId");
            if (userIdParam == null || userIdParam.isEmpty()) {
                request.getSession().setAttribute("error", "Không tìm thấy ID người dùng.");
                response.sendRedirect(request.getContextPath() + "/userManagement");
                return;
            }

            int userId = Integer.parseInt(userIdParam);
            // Lấy thông tin người dùng từ UserDAO
            User user = userDAO.getUserById(userId);

            if (user == null) {
                request.getSession().setAttribute("error", "Không tìm thấy người dùng với ID: " + userId);
                response.sendRedirect(request.getContextPath() + "/userManagement");
                return;
            }

            // Đặt đối tượng user vào request để JSP sử dụng
            request.setAttribute("user", user);

            // Chuyển hướng đến userDetail.jsp
            request.getRequestDispatcher("userDetail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "ID người dùng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/userManagement");
        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi lấy thông tin người dùng: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/userManagement");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            int userId = Integer.parseInt(request.getParameter("userId"));

            // Lấy thông tin người dùng
            User user = userDAO.getUserById(userId);
            if (user == null) {
                request.getSession().setAttribute("error", "Không tìm thấy người dùng.");
                response.sendRedirect(request.getContextPath() + "/userManagement");
                return;
            }

            // Ngăn chặn khóa tài khoản Admin
            if ("block".equals(action) && user.isAdmin()) {
                request.getSession().setAttribute("error", "Không thể khóa tài khoản Admin.");
                response.sendRedirect(request.getContextPath() + "/userDetail?userId=" + userId);
                return;
            }

            // Xử lý hành động block/active
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

            // Chuyển hướng về trang chi tiết người dùng
            response.sendRedirect(request.getContextPath() + "/userDetail?userId=" + userId);

        } catch (SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi cập nhật trạng thái người dùng: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/userManagement");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "ID người dùng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/userManagement");
        }
    }
}