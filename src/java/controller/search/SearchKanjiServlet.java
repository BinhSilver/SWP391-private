/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.search;
import java.sql.*;
import DB.JDBCConnection;
import com.google.gson.Gson;
import Dao.KanjiDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import model.Kanji;
@WebServlet("/SearchKanji")
public class SearchKanjiServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String keyword = req.getParameter("query");
        ArrayList<Kanji> result = KanjiDAO.searchKanji(keyword);

        PrintWriter out = res.getWriter();
        out.print(new Gson().toJson(result));
        out.flush();
    }
}