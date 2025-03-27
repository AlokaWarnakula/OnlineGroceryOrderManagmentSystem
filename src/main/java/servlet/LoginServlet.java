package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Admin;
import model.FileUtil;
import model.User;
import java.util.List;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    private String LOGGED_IN_USER_FILE;
    private String USERS_FILE;
    private String ADMINS_FILE;

    @Override
    public void init() throws ServletException {
        String basePath = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/";
        LOGGED_IN_USER_FILE = basePath + "loggedInUser.txt";
        USERS_FILE = basePath + "users.txt";
        ADMINS_FILE = basePath + "admins.txt";
        System.out.println("LOGGED_IN_USER_FILE path: " + LOGGED_IN_USER_FILE);
        System.out.println("USERS_FILE path: " + USERS_FILE);
        System.out.println("ADMINS_FILE path: " + ADMINS_FILE);
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
        HttpSession session = request.getSession();

        // Check if the email is for an admin (ends with .admin)
        if (email.toLowerCase().endsWith(".admin")) {
            // Admin login flow
            List<Admin> admins = FileUtil.readAdmins(ADMINS_FILE);
            if (admins == null) {
                System.err.println("Failed to read admins file: " + ADMINS_FILE);
                request.setAttribute("error", "Server error. Please try again later.");
                request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
                return;
            }

            for (Admin admin : admins) {
                if (admin.getEmail().equals(email) && admin.getPassword().equals(password)) {
                    System.out.println("Admin authenticated: " + admin.toString());
                    // Log the admin details to verify
                    System.out.println("Setting adminNumber in session: " + admin.getAdminNumber());
                    System.out.println("Setting adminEmail in session: " + admin.getEmail());
                    System.out.println("Setting adminRole in session: " + admin.getRole());
                    // Store admin details in session
                    session.setAttribute("adminEmail", admin.getEmail());
                    session.setAttribute("adminNumber", admin.getAdminNumber());
                    session.setAttribute("adminRole", admin.getRole()); // Added role to session
                    // Redirect to adminSuccessful.jsp with type=login
                    response.sendRedirect(request.getContextPath() + "/adminPages/adminSuccessful.jsp?type=login");
                    return;
                }
            }

            // Admin authentication failed
            System.out.println("Admin authentication failed for email: " + email);
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
        } else {
            // Regular user login flow
            List<User> users = FileUtil.readUsers(USERS_FILE);
            if (users == null) {
                System.err.println("Failed to read users file: " + USERS_FILE);
                request.setAttribute("error", "Server error. Please try again later.");
                request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
                return;
            }

            for (User user : users) {
                if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                    System.out.println("User authenticated: " + user.toString());
                    // User authenticated, create session and write to loggedInUser.txt
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

            // User authentication failed
            System.out.println("User authentication failed for email: " + email);
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
        }
    }
}