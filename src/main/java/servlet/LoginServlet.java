package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.User;

import java.io.IOException;

public class LoginServlet extends HttpServlet {
    private String LOGGED_IN_USER_FILE;
    private String USERS_FILE;

    @Override
    public void init() throws ServletException {
        String basePath = "C:\\Users\\LENOVO\\IdeaProjects\\OnlineGroceryOrderManagmentSystem\\src\\main\\webapp\\data";
        LOGGED_IN_USER_FILE = basePath + "loggedInUser.txt";
        USERS_FILE = basePath + "users.txt";
        System.out.println("LOGGED_IN_USER_FILE path: " + LOGGED_IN_USER_FILE);
        System.out.println("USERS_FILE path: " + USERS_FILE);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null) {
            System.out.println("Email or password missing: email=" + email + ", password=" + password);
            request.setAttribute("error", "Email and password are required.");
            request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
            return;
        }

        System.out.println("Attempting login for email: " + email);

        // Read users from file to authenticate
        for (User user : FileUtil.readUsers(USERS_FILE)) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                System.out.println("User authenticated: " + user.toString());
                // User authenticated, create session and write to loggedInUser.txt
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                try {
                    FileUtil.writeLoggedInUser(LOGGED_IN_USER_FILE, user);
                    System.out.println("Successfully wrote to loggedInUser.txt for user: " + user.getEmail());
                } catch (Exception e) {
                    System.err.println("Failed to write to loggedInUser.txt: " + e.getMessage());
                    e.printStackTrace();
                }
                // Redirect to Success.jsp with type=login
                response.sendRedirect(request.getContextPath() + "/userLogin/Success.jsp?type=login");
                return;
            }
        }

        // Authentication failed
        System.out.println("Authentication failed for email: " + email);
        request.setAttribute("error", "Invalid email or password.");
        request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
    }
}