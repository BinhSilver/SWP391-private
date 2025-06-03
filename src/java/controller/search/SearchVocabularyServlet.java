/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.search;

import com.google.gson.Gson;
import dao.VocabularyDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import model.Vocabulary;
@WebServlet("/SearchVocabulary")
public class SearchVocabularyServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String keyword = req.getParameter("query");
        ArrayList<Vocabulary> result = VocabularyDAO.searchVocabulary(keyword);

        PrintWriter out = res.getWriter();
        out.print(new Gson().toJson(result));
        out.flush();
    }
}
