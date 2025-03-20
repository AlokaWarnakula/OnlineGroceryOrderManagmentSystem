package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.User;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class RegisterServlet extends HttpServlet {
    private static final String USERS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/users.txt";
    private static final String LOGGED_IN_USER_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/loggedInUser.txt";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String password = request.getParameter("password");

        if (fullName == null || email == null || phoneNumber == null || address == null || password == null ||
                fullName.trim().isEmpty() || email.trim().isEmpty() || phoneNumber.trim().isEmpty() || address.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
            return;
        }

        List<User> users = FileUtil.readUsers(USERS_FILE);
        if (users == null) {
            request.setAttribute("error", "Error reading user data. Please try again later.");
            request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
            return;
        }

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

        // Use email as username
        String username = email;
        User user = new User(username, password, userNumber, fullName, email, phoneNumber, address);

        // Add the new user to the list and write to users.txt
        synchronized (this) {
            users.add(user);
            try {
                FileUtil.writeUsers(USERS_FILE, users);
                System.out.println("Added new user to users.txt: " + user.toString());
            } catch (IOException e) {
                System.err.println("Error writing user to file: " + e.getMessage());
                request.setAttribute("error", "Error saving user data. Please try again later.");
                request.getRequestDispatcher("/userLogin/login.jsp").forward(request, response);
                return;
            }
        }

        // Set user in session before redirecting to Success.jsp
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        System.out.println("Set user in session: " + user.toString());

        // Write to loggedInUser.txt
        FileUtil.writeLoggedInUser(LOGGED_IN_USER_FILE, user);
        System.out.println("Wrote new user to loggedInUser.txt: " + user.toString());

        // Redirect to success page
        response.sendRedirect(request.getContextPath() + "/userLogin/Success.jsp?type=signup");
    }
}