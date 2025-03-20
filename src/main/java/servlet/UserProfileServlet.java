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

public class UserProfileServlet extends HttpServlet {
    private static final String USERS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/users.txt";
    private static final String LOGGED_IN_USER_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/loggedInUser.txt";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp?error=notLoggedIn");
            return;
        }

        request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp?error=notLoggedIn");
            return;
        }

        String action = request.getParameter("action");
        if ("save".equals(action)) {
            // Update user details
            String fullName = request.getParameter("fullName");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");

            if (fullName == null || phoneNumber == null || address == null || fullName.trim().isEmpty() || phoneNumber.trim().isEmpty() || address.trim().isEmpty()) {
                request.setAttribute("error", "All fields are required.");
                request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
                return;
            }

            // Create updated user object
            User updatedUser = new User(
                    loggedInUser.getUsername(),
                    loggedInUser.getPassword(),
                    loggedInUser.getUserNumber(),
                    fullName,
                    loggedInUser.getEmail(),
                    phoneNumber,
                    address
            );

            // Update users.txt
            synchronized (this) {
                List<User> users = FileUtil.readUsers(USERS_FILE);
                if (users != null) {
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getUsername().equals(loggedInUser.getUsername())) {
                            users.set(i, updatedUser);
                            break;
                        }
                    }
                    FileUtil.writeUsers(USERS_FILE, users);
                    System.out.println("Updated users.txt with: " + updatedUser.toString());
                } else {
                    request.setAttribute("error", "Error updating user data. Please try again later.");
                    request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
                    return;
                }
            }

            // Update session with new user data
            session.setAttribute("user", updatedUser);
            System.out.println("Updated session with: " + updatedUser.toString());

            // Update loggedInUser.txt with the updated user data
            FileUtil.writeLoggedInUser(LOGGED_IN_USER_FILE, updatedUser);
            System.out.println("Updated loggedInUser.txt with: " + updatedUser.toString());

            response.sendRedirect(request.getContextPath() + "/UserProfileServlet?success=Profile updated successfully");
        } else if ("delete".equals(action)) {
            // Delete user account
            synchronized (this) {
                List<User> users = FileUtil.readUsers(USERS_FILE);
                if (users != null) {
                    users.removeIf(user -> user.getUsername().equals(loggedInUser.getUsername()));
                    FileUtil.writeUsers(USERS_FILE, users);
                    System.out.println("Deleted user from users.txt: " + loggedInUser.getUsername());
                }
            }

            // Redirect to LogoutServlet with success message
            response.sendRedirect(request.getContextPath() + "/LogoutServlet?success=Account deleted successfully");
        }
    }
}