package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.User;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserAdminServlet extends HttpServlet {
    private static final String USERS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/users.txt";
    private static final String ORDERS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/orders.txt";
    private static final String DELIVERED_ORDERS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/deliveredOrders.txt";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("UserAdminServlet - doGet invoked with request URI: " + request.getRequestURI());

        // Check for admin session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminEmail") == null) {
            System.out.println("UserAdminServlet - No admin session found. Redirecting to admin login page.");
            response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=notLoggedIn");
            return;
        }

        // Log session attributes
        System.out.println("UserAdminServlet - Session adminNumber: " + session.getAttribute("adminNumber"));
        System.out.println("UserAdminServlet - Session adminEmail: " + session.getAttribute("adminEmail"));
        System.out.println("UserAdminServlet - Session adminRole: " + session.getAttribute("adminRole"));

        // Check if the admin has the correct role (Super Admin)
        String adminRole = (String) session.getAttribute("adminRole");
        if (adminRole == null || !"super".equalsIgnoreCase(adminRole)) {
            System.out.println("UserAdminServlet - Unauthorized access attempt by role: " + adminRole);
            response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");

        if ("info".equals(action)) {
            String userNumber = request.getParameter("userNumber");
            System.out.println("UserAdminServlet - Info action requested for userNumber: " + userNumber);
            if (userNumber == null || userNumber.trim().isEmpty()) {
                request.setAttribute("errorMessage", "Invalid user number.");
                doDefault(request, response);
                return;
            }

            // Fetch user details
            List<User> users = FileUtil.readUsers(USERS_FILE);
            User selectedUser = null;
            if (users != null) {
                for (User user : users) {
                    if (user.getUserNumber().equals(userNumber)) {
                        selectedUser = user;
                        System.out.println("UserAdminServlet - Found user: " + user.getUserNumber());
                        break;
                    }
                }
            }

            if (selectedUser == null) {
                System.out.println("UserAdminServlet - User not found for userNumber: " + userNumber);
                request.setAttribute("errorMessage", "User not found.");
                doDefault(request, response);
                return;
            }

            // Fetch user's active and delivered orders
            List<FileUtil.Order> activeOrders = FileUtil.readAllOrders(ORDERS_FILE);
            List<FileUtil.Order> deliveredOrders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);

            // Get search query
            String searchQuery = request.getParameter("searchQuery");
            System.out.println("UserAdminServlet - SearchQuery: " + searchQuery);

            // Filter orders by userNumber and searchQuery
            if (activeOrders != null) {
                System.out.println("UserAdminServlet - Total active orders before filtering: " + activeOrders.size());
                activeOrders = activeOrders.stream()
                        .filter(order -> userNumber.equals(order.getUserNumber()))
                        .filter(order -> searchQuery == null || searchQuery.trim().isEmpty() || order.getOrderNumber().toLowerCase().contains(searchQuery.toLowerCase()))
                        .collect(Collectors.toList());
                System.out.println("UserAdminServlet - Active orders after filtering: " + activeOrders.size());

                // Sort by confirmationDate (oldest first)
                activeOrders.sort((o1, o2) -> {
                    try {
                        LocalDateTime date1 = LocalDateTime.parse(o1.getConfirmationDate(), DATE_TIME_FORMATTER);
                        LocalDateTime date2 = LocalDateTime.parse(o2.getConfirmationDate(), DATE_TIME_FORMATTER);
                        return date1.compareTo(date2); // Oldest first (ascending)
                    } catch (Exception e) {
                        System.err.println("Error parsing confirmationDate: " + e.getMessage());
                        return 0;
                    }
                });
            } else {
                activeOrders = new ArrayList<>();
            }

            if (deliveredOrders != null) {
                System.out.println("UserAdminServlet - Total delivered orders before filtering: " + deliveredOrders.size());
                deliveredOrders = deliveredOrders.stream()
                        .filter(order -> userNumber.equals(order.getUserNumber()))
                        .filter(order -> searchQuery == null || searchQuery.trim().isEmpty() || order.getOrderNumber().toLowerCase().contains(searchQuery.toLowerCase()))
                        .collect(Collectors.toList());
                System.out.println("UserAdminServlet - Delivered orders after filtering: " + deliveredOrders.size());

                // Sort by deliveredDate (newest first)
                deliveredOrders.sort((o1, o2) -> {
                    try {
                        if (o1.getDeliveredDate() == null || o1.getDeliveredDate().isEmpty()) return 1;
                        if (o2.getDeliveredDate() == null || o2.getDeliveredDate().isEmpty()) return -1;
                        LocalDateTime date1 = LocalDateTime.parse(o1.getDeliveredDate(), DATE_TIME_FORMATTER);
                        LocalDateTime date2 = LocalDateTime.parse(o2.getDeliveredDate(), DATE_TIME_FORMATTER);
                        return date2.compareTo(date1); // Newest first (descending)
                    } catch (Exception e) {
                        System.err.println("Error parsing deliveredDate: " + e.getMessage());
                        return 0;
                    }
                });
            } else {
                deliveredOrders = new ArrayList<>();
            }

            // Set attributes for userInfo.jsp
            request.setAttribute("selectedUser", selectedUser);
            request.setAttribute("activeOrders", activeOrders);
            request.setAttribute("deliveredOrders", deliveredOrders);

            // Forward to userInfo.jsp
            System.out.println("UserAdminServlet - Forwarding to /adminPages/userInfo.jsp for userNumber: " + userNumber);
            request.getRequestDispatcher("/adminPages/userInfo.jsp").forward(request, response);
        } else {
            doDefault(request, response);
        }
    }

    private void doDefault(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("UserAdminServlet - Attempting to read users from: " + USERS_FILE);

        // Check if the file exists
        File usersFile = new File(USERS_FILE);
        System.out.println("UserAdminServlet - Does users.txt exist? " + usersFile.exists());
        System.out.println("UserAdminServlet - Is users.txt readable? " + usersFile.canRead());
        System.out.println("UserAdminServlet - Users file absolute path: " + usersFile.getAbsolutePath());

        // Read users from users.txt
        List<User> users = new ArrayList<>();
        try {
            users = FileUtil.readUsers(USERS_FILE);
            if (users == null) {
                System.out.println("UserAdminServlet - FileUtil.readUsers returned null. Using empty list.");
                users = new ArrayList<>();
            } else {
                System.out.println("UserAdminServlet - Loaded " + users.size() + " users from users.txt");
                for (User user : users) {
                    System.out.println("UserAdminServlet - User: " + user.getUserNumber() + ", Email: " + user.getEmail() + ", Full Name: " + user.getFullName());
                }
            }
        } catch (Exception e) {
            System.err.println("UserAdminServlet - Unexpected error reading users.txt: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to load users due to an unexpected error: " + e.getMessage());
        }

        // Set users as request attribute
        request.setAttribute("users", users);

        // Forward to userDashboard.jsp
        System.out.println("UserAdminServlet - Forwarding to /adminPages/userDashboard.jsp");
        request.getRequestDispatcher("/adminPages/userDashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            String userNumber = request.getParameter("userNumber");
            System.out.println("UserAdminServlet - Delete action requested for userNumber: " + userNumber);
            if (userNumber != null && !userNumber.trim().isEmpty()) {
                synchronized (this) {
                    List<User> users = FileUtil.readUsers(USERS_FILE);
                    if (users != null) {
                        users.removeIf(user -> user.getUserNumber().equals(userNumber));
                        FileUtil.writeUsers(USERS_FILE, users);
                        System.out.println("UserAdminServlet - Deleted user with userNumber: " + userNumber);
                    } else {
                        System.out.println("UserAdminServlet - Failed to read users for deletion.");
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/UserAdminServlet");
        } else {
            doGet(request, response);
        }
    }
}