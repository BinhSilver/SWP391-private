package controller.profile;

import Dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@WebServlet("/changeAvatar")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 15
)
public class ChangeAvatarServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("authUser");
        if (user == null) {
            session.setAttribute("error", "Vui lòng đăng nhập.");
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            Part filePart = request.getPart("avatar");
            if (filePart == null || filePart.getSize() == 0) {
                session.setAttribute("error", "Vui lòng chọn file ảnh.");
                redirectToProfile(user.getRoleID(), request, response);
                return;
            }

            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            if (!extension.matches("\\.(jpg|jpeg|png|gif)$")) {
                session.setAttribute("error", "Chỉ chấp nhận file ảnh (jpg, jpeg, png, gif)");
                redirectToProfile(user.getRoleID(), request, response);
                return;
            }

            // Lưu avatar vào DB
            InputStream avatarStream = filePart.getInputStream();
            UserDAO dao = new UserDAO();
            dao.updateAvatarBlob(user.getUserID(), avatarStream);
            // Cập nhật lại avatar cho user trong session
            user = dao.getUserById(user.getUserID());
            session.setAttribute("authUser", user);

            session.setAttribute("message", "Cập nhật avatar thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Lỗi cập nhật avatar: " + e.getMessage());
        }

        redirectToProfile(user.getRoleID(), request, response);
    }

    private void redirectToProfile(int roleID, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/profile");
    }
}