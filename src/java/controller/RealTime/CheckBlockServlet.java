/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.RealTime;

import Dao.BlockDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/checkBlock")
public class CheckBlockServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String user1Id = req.getParameter("user1");
        String user2Id = req.getParameter("user2");

        PrintWriter out = resp.getWriter();
        try {
            if (user1Id == null || user2Id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\": \"Thiếu tham số\"}");
                return;
            }

            BlockDAO blockDAO = new BlockDAO();
            boolean blockedByUser1 = blockDAO.isBlocked(Integer.parseInt(user1Id), Integer.parseInt(user2Id));
            boolean blockedByUser2 = blockDAO.isBlocked(Integer.parseInt(user2Id), Integer.parseInt(user1Id));

            String jsonResponse = String.format(
                    "{\"blockedByMe\": %b, \"blockedMe\": %b}", blockedByUser1, blockedByUser2);
            out.write(jsonResponse);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\": \"Lỗi hệ thống\"}");
        } finally {
            out.flush();
        }
    }
}