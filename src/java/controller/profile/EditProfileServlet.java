package controller.profile;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.User;
import dao.UserDAO;

@WebServlet("/editprofile")
public class EditProfileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setCharacterEncoding("UTF-8");

            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("authUser");

            if (currentUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Lấy dữ liệu từ form
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phoneNumber");
            String birthDateStr = request.getParameter("birthDate");
            String japaneseLevel = request.getParameter("japaneseLevel");
            String address = request.getParameter("address");
            String country = request.getParameter("country");
            String avatar = request.getParameter("avatar");

            // Parse ngày sinh
            Date birthDate = null;
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDateStr);
            }

            // Cập nhật thông tin người dùng
            currentUser.setEmail(email);
            currentUser.setFullName(fullName);
            currentUser.setPhoneNumber(phone);
            currentUser.setBirthDate(birthDate);
            currentUser.setJapaneseLevel(japaneseLevel);
            currentUser.setAddress(address);
            currentUser.setCountry(country);
            currentUser.setAvatar(avatar);

            UserDAO dao = new UserDAO();
            boolean success = dao.updateProfile(currentUser);

            if (success) {
                session.setAttribute("authUser", currentUser); // Cập nhật session
                response.sendRedirect("profile-view.jsp"); // Hoặc redirect lại trang profile
            } else {
                request.setAttribute("error", "Cập nhật không thành công.");
                request.getRequestDispatcher("editprofile.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("editprofile.jsp").forward(request, response);
        }
    }
}