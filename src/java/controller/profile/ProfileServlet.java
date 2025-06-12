/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.profile;

import Dao.UserDAO;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.User;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Giả sử bạn đã lưu UserID trong session sau khi đăng nhập
        HttpSession session = request.getSession();
        Integer userID = (Integer) session.getAttribute("userID");

        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            User user = userDAO.getUserById(userID);
            if (user != null) {
                request.setAttribute("user", user);
                request.getRequestDispatcher("/profile.jsp").forward(request, response);
            } else {
                response.sendRedirect("error.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // Xử lý tiếng Việt

        HttpSession session = request.getSession();
        Integer userID = (Integer) session.getAttribute("userID");

        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            User user = userDAO.getUserById(userID);
            if (user == null) {
                response.sendRedirect("error.jsp");
                return;
            }

            // Cập nhật các trường từ form
            user.setFullName(request.getParameter("fullName"));
            user.setPhoneNumber(request.getParameter("phoneNumber"));
            user.setJapaneseLevel(request.getParameter("japaneseLevel"));
            user.setAddress(request.getParameter("address"));
            user.setCountry(request.getParameter("country"));
            user.setAvatar(request.getParameter("avatar"));

            // Xử lý ngày sinh (nếu có)
            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                user.setBirthDate(sdf.parse(birthDateStr));
            }

            userDAO.updateUser(user);

            // Gửi lại thông tin để hiển thị
            request.setAttribute("user", user);
            request.setAttribute("successMessage", "Cập nhật thành công!");
            request.getRequestDispatcher("Profile/profile-view.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật.");
            request.getRequestDispatcher("Profile/profile-view.jsp").forward(request, response);
        }
    }
}
