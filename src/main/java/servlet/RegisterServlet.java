package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.List;

public class RegisterServlet extends HttpServlet {
    private static final String USERS_FILE = "C:\\Users\\LENOVO\\IdeaProjects\\OnlineGroceryOrderManagmentSystem\\src\\main\\webapp\\data\\users.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String password = request.getParameter("password");

        if (fullName == null || email == null || phoneNumber == null || address == null || password == null) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
            return;
        }

        List<User> users = FileUtil.readUsers(USERS_FILE);
        System.out.println("Checking email: " + email + ", existing users: " + users); // Debug log
        if (users.stream().anyMatch(u -> u.getEmail().equals(email))) {
            System.out.println("Email already registered: " + email); // Debug log
            request.setAttribute("error", "Email already registered.");
            request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
            return;
        }

        String userNumber;
        do {
            userNumber = "US" + String.format("%012d", Math.abs(new Random().nextLong() % 1000000000000L));
        } while (!FileUtil.isUserNumberUnique(userNumber, USERS_FILE));

        User user = new User(userNumber, fullName, email, phoneNumber, address, password);

        synchronized (this) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
                writer.write("--- User Start: " + userNumber + " ---\n");
                writer.write("userNumber=" + userNumber + "\n");
                writer.write("fullName=" + fullName + "\n");
                writer.write("email=" + email + "\n");
                writer.write("phoneNumber=" + phoneNumber + "\n");
                writer.write("address=" + address + "\n");
                writer.write("password=" + password + "\n");
                writer.write("--- User End ---\n\n");
            }
        }

        // Set user in session before redirecting to Success.jsp
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        response.sendRedirect(request.getContextPath() + "/userLogin/Success.jsp?type=signup");
    }
}