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
import java.util.HashMap;
import java.util.Map;
import com.cloudinary.Cloudinary;
import config.CloudinaryUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

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
                // Tạo file tạm thời
                File tempFile = File.createTempFile("avatar_", "_upload");
                try {
                    // Copy dữ liệu từ Part vào file tạm
                    try (InputStream input = filePart.getInputStream();
                         FileOutputStream output = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                    }
                    
                    // Upload to Cloudinary
                    Cloudinary cloudinary = CloudinaryUtil.getCloudinary();
                    Map<String, Object> options = new HashMap<>();
                    options.put("folder", "avatars");
                    options.put("public_id", "user_" + currentUser.getUserID());
                    options.put("overwrite", true);

                    Map uploadResult = cloudinary.uploader().upload(tempFile, options);
                    String avatarUrl = (String) uploadResult.get("secure_url");
                    currentUser.setAvatar(avatarUrl);
                } finally {
                    // Xóa file tạm sau khi upload
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                }
            }

            UserDAO dao = new UserDAO();
            boolean success = dao.updateProfile(currentUser);

            if (success) {
                session.setAttribute("authUser", currentUser); // Cập nhật session
                // Set user attribute cho request
                request.setAttribute("user", currentUser);
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
