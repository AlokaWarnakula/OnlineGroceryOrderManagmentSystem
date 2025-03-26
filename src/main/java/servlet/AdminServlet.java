package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String adminEmail = (String) session.getAttribute("adminEmail");

        // Check if an admin is logged in
        if (adminEmail == null) {
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp");
        } else {
            request.getRequestDispatcher("/adminPages/adminPage.jsp").forward(request, response);
        }
    }
}