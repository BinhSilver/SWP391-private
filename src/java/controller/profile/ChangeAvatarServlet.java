package controller.profile;

import Dao.UserDAO;
import model.User;
import config.CloudinaryUtil;
import com.cloudinary.Cloudinary;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

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

            // Upload ảnh lên Cloudinary
            Cloudinary cloudinary = CloudinaryUtil.getCloudinary();
            Map<String, Object> options = new HashMap<>();
            options.put("folder", "avatars");
            options.put("public_id", "user_" + user.getUserID());
            options.put("overwrite", true);

            Map uploadResult = cloudinary.uploader().upload(filePart.getInputStream(), options);

            // Lấy URL của ảnh từ kết quả upload
            String avatarUrl = (String) uploadResult.get("secure_url");

            // Cập nhật URL avatar trong database
            UserDAO dao = new UserDAO();
            dao.updateAvatar(user.getUserID(), avatarUrl);

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