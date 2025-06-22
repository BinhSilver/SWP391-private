package controller.profile;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import model.User;
import Dao.UserDAO;
import jakarta.servlet.annotation.MultipartConfig;

@WebServlet("/editprofile")
@MultipartConfig
public class EditProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("authUser");
        session.setAttribute("authUser", currentUser);
        req.getRequestDispatcher("/Profile/profile-edit.jsp").forward(req, resp);
    }

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

            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0) {
                byte[] avatarBytes = filePart.getInputStream().readAllBytes();
                currentUser.setAvatar(avatarBytes);
            }

            UserDAO dao = new UserDAO();
            boolean success = dao.updateProfile(currentUser);

            if (success) {
                session.setAttribute("authUser", currentUser); // Cập nhật session
                // Điều hướng về trang xem hồ sơ (profile-view.jsp)
                request.getRequestDispatcher("/Profile/profile-view.jsp").forward(request, response);

            } else {
                // Nếu cập nhật thất bại, chuyển về trang chỉnh sửa và hiển thị lỗi
                request.setAttribute("error", "Cập nhật không thành công.");
                request.getRequestDispatcher("/Profile/profile-edit.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/Profile/profile-edit.jsp").forward(request, response);
        }
    }
}
