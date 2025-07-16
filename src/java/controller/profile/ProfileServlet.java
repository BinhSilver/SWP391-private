/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.profile;

import Dao.UserDAO;
import Dao.UserPremiumDAO;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import model.User;
import model.UserPremium;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final UserPremiumDAO userPremiumDAO = new UserPremiumDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User authUser = (User) session.getAttribute("authUser");
        
        if (authUser == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            // Lấy thông tin user từ database để đảm bảo dữ liệu mới nhất
            User user = userDAO.getUserById(authUser.getUserID());
            if (user != null) {
                // Lấy thông tin premium của user
                UserPremium premiumInfo = null;
                try {
                    premiumInfo = userPremiumDAO.getCurrentUserPremium(user.getUserID());
                    System.out.println("Profile: Loading premium info for userID=" + user.getUserID());
                    if (premiumInfo != null) {
                        System.out.println("Premium found - End Date: " + premiumInfo.getEndDate());
                    } else {
                        System.out.println("No premium found for user");
                    }
                } catch (SQLException e) {
                    System.out.println("Error loading premium info: " + e.getMessage());
                    e.printStackTrace();
                }
                
                request.setAttribute("user", user);
                request.setAttribute("premiumInfo", premiumInfo);
                request.getRequestDispatcher("/Profile/profile-view.jsp").forward(request, response);
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
        User authUser = (User) session.getAttribute("authUser");

        if (authUser == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            User user = userDAO.getUserById(authUser.getUserID());
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
            user.setAvatar(null); // Bỏ qua hoặc set null nếu không dùng link ngoài

            // Xử lý ngày sinh (nếu có)
            String birthDateStr = request.getParameter("birthDate");
            if (birthDateStr != null && !birthDateStr.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                user.setBirthDate(sdf.parse(birthDateStr));
            }

            userDAO.updateUser(user);

            // Lấy thông tin premium
            UserPremium premiumInfo = null;
            try {
                premiumInfo = userPremiumDAO.getCurrentUserPremium(user.getUserID());
            } catch (SQLException e) {
                System.out.println("Error loading premium info: " + e.getMessage());
            }

            // Gửi lại thông tin để hiển thị
            request.setAttribute("user", user);
            request.setAttribute("premiumInfo", premiumInfo);
            request.setAttribute("successMessage", "Cập nhật thành công!");
            request.getRequestDispatcher("Profile/profile-view.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật.");
            request.getRequestDispatcher("Profile/profile-view.jsp").forward(request, response);
        }
    }
}
