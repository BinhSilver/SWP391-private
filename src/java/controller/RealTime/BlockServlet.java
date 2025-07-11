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

@WebServlet("/SWP_HUY/block")
public class BlockServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String blockerId = req.getParameter("blockerId");
        String blockedId = req.getParameter("blockedId");

        if (blockerId == null || blockedId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Thiếu tham số");
            return;
        }

        try {
            boolean success = new BlockDAO().blockUser(Integer.parseInt(blockerId), Integer.parseInt(blockedId));
            resp.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(success ? "Chặn thành công" : "Chặn thất bại");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Định dạng ID không hợp lệ");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String blockerId = req.getParameter("blockerId");
        String blockedId = req.getParameter("blockedId");

        if (blockerId == null || blockedId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Thiếu tham số");
            return;
        }

        try {
            boolean success = new BlockDAO().unblockUser(Integer.parseInt(blockerId), Integer.parseInt(blockedId));
            resp.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(success ? "Bỏ chặn thành công" : "Bỏ chặn thất bại");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Định dạng ID không hợp lệ");
        }
    }
}