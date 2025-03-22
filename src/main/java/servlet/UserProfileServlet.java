package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class UserProfileServlet extends HttpServlet {
    private static final String USERS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/users.txt";
    private static final String LOGGED_IN_USER_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/loggedInUser.txt";
    private static final String ORDERS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/orders.txt";
    private static final String DELIVERED_ORDERS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/deliveredOrders.txt";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("save".equals(action)) {
            // Update user profile
            String fullName = request.getParameter("fullName");
            String address = request.getParameter("address");
            String phoneNumber = request.getParameter("phoneNumber");

            if (fullName == null || address == null || phoneNumber == null ||
                    fullName.trim().isEmpty() || address.trim().isEmpty() || phoneNumber.trim().isEmpty()) {
                request.setAttribute("error", "All fields are required.");
                request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
                return;
            }

            synchronized (this) {
                List<User> users = FileUtil.readUsers(USERS_FILE);
                if (users == null) {
                    request.setAttribute("error", "Error reading user data. Please try again later.");
                    request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
                    return;
                }

                // Update the user's details
                for (User user : users) {
                    if (user.getUserNumber().equals(loggedInUser.getUserNumber())) {
                        user.setFullName(fullName);
                        user.setAddress(address);
                        user.setPhoneNumber(phoneNumber);
                        break;
                    }
                }

                // Write updated users back to file
                FileUtil.writeUsers(USERS_FILE, users);

                // Update logged-in user
                loggedInUser.setFullName(fullName);
                loggedInUser.setAddress(address);
                loggedInUser.setPhoneNumber(phoneNumber);
                FileUtil.writeLoggedInUser(LOGGED_IN_USER_FILE, loggedInUser);
                session.setAttribute("user", loggedInUser);
            }

            response.sendRedirect(request.getContextPath() + "/UserProfileServlet");
        } else if ("delete".equals(action)) {
            // Delete user account
            synchronized (this) {
                List<User> users = FileUtil.readUsers(USERS_FILE);
                if (users != null) {
                    users.removeIf(user -> user.getUserNumber().equals(loggedInUser.getUserNumber()));
                    FileUtil.writeUsers(USERS_FILE, users);
                }

                FileUtil.clearLoggedInUser(LOGGED_IN_USER_FILE);
                session.invalidate();
            }

            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if ("cancelOrder".equals(action)) {
            String orderNumber = request.getParameter("orderNumber");
            if (orderNumber == null || orderNumber.trim().isEmpty()) {
                request.setAttribute("error", "Invalid order number.");
                request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
                return;
            }

            synchronized (this) {
                // Read all orders
                List<FileUtil.Order> orders = FileUtil.readAllOrders(ORDERS_FILE);
                FileUtil.Order orderToCancel = null;

                // Find the order to cancel
                for (FileUtil.Order order : orders) {
                    if (order.getOrderNumber().equals(orderNumber)) {
                        orderToCancel = order;
                        break;
                    }
                }

                if (orderToCancel == null) {
                    request.setAttribute("error", "Order not found.");
                    request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
                    return;
                }

                // Update order status to Cancelled and set deliveredDate
                orderToCancel.setOrderStatus("Cancelled");
                orderToCancel.setDeliveryStatus("Cancelled");
                orderToCancel.setDeliveredDate(LocalDateTime.now().format(DATE_TIME_FORMATTER));

                // Remove the order from orders.txt
                orders.removeIf(order -> order.getOrderNumber().equals(orderNumber));

                // Rewrite orders.txt without the cancelled order
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE, false))) {
                    for (FileUtil.Order order : orders) {
                        writer.write("--- Order Start: " + order.getOrderNumber() + " ---\n");
                        writer.write("orderNumber=" + order.getOrderNumber() + "\n");
                        writer.write("userNumber=" + order.getUserNumber() + "\n");
                        writer.write("name=" + (order.getFullName() != null ? order.getFullName() : "") + "\n");
                        writer.write("phoneNum=" + (order.getPhoneNum() != null ? order.getPhoneNum() : "") + "\n");
                        writer.write("address=" + (order.getAddress() != null ? order.getAddress() : "") + "\n");
                        writer.write("deliveryMethod=" + (order.getDeliveryMethod() != null ? order.getDeliveryMethod() : "") + "\n");
                        writer.write("paymentMethod=" + (order.getPaymentMethod() != null ? order.getPaymentMethod() : "") + "\n");
                        writer.write("deliveryDate=" + (order.getDeliveryDate() != null ? order.getDeliveryDate() : "") + "\n");
                        writer.write("confirmationDate=" + (order.getConfirmationDate() != null ? order.getConfirmationDate() : "") + "\n");
                        writer.write("paymentStatus=" + (order.getPaymentStatus() != null ? order.getPaymentStatus() : "") + "\n");
                        writer.write("deliveryStatus=" + (order.getDeliveryStatus() != null ? order.getDeliveryStatus() : "") + "\n");
                        writer.write("orderStatus=" + (order.getOrderStatus() != null ? order.getOrderStatus() : "") + "\n");
                        writer.write("deliveredDate=" + (order.getDeliveredDate() != null ? order.getDeliveredDate() : "") + "\n");
                        writer.write("[products]\n");
                        for (String[] product : order.getProducts()) {
                            writer.write("productID=" + product[0] + ", quantity=" + product[1] + "\n");
                        }
                        writer.write("[total]\n");
                        writer.write("totalPrice=" + String.format("%.2f", order.getTotalPrice()) + "\n");
                        writer.write("--- Order End ---\n");
                        writer.write("\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error rewriting orders file: " + e.getMessage());
                    request.setAttribute("error", "Error cancelling order.");
                    request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
                    return;
                }

                // Append the cancelled order to deliveredOrders.txt
                FileUtil.writeDeliveredOrder(DELIVERED_ORDERS_FILE, orderToCancel);
            }

            // Redirect back to the user profile page with the Active tab
            response.sendRedirect(request.getContextPath() + "/UserProfileSearchServlet?tab=Active");
        } else {
            // Load orders for the initial page load (default to Active tab)
            String userNumber = loggedInUser.getUserNumber();
            String tab = request.getParameter("tab") != null ? request.getParameter("tab") : "Active"; // Default to Active tab

            List<FileUtil.Order> orders;
            if ("Delivered".equalsIgnoreCase(tab)) {
                // Read delivered orders
                orders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
            } else {
                // Read active orders (default to "Active" tab)
                orders = FileUtil.readAllOrders(ORDERS_FILE);
            }

            // Filter orders by userNumber
            orders = orders.stream()
                    .filter(order -> userNumber.equals(order.getUserNumber()))
                    .collect(Collectors.toList());

            // Sort orders based on the tab
            if ("Delivered".equalsIgnoreCase(tab)) {
                // Sort by deliveredDate (newest first)
                orders.sort((o1, o2) -> {
                    try {
                        // Handle null or empty deliveredDate
                        if (o1.getDeliveredDate() == null || o1.getDeliveredDate().isEmpty()) return 1;
                        if (o2.getDeliveredDate() == null || o2.getDeliveredDate().isEmpty()) return -1;

                        LocalDateTime date1 = LocalDateTime.parse(o1.getDeliveredDate(), DATE_TIME_FORMATTER);
                        LocalDateTime date2 = LocalDateTime.parse(o2.getDeliveredDate(), DATE_TIME_FORMATTER);
                        return date2.compareTo(date1); // Newest first (descending)
                    } catch (Exception e) {
                        System.err.println("Error parsing deliveredDate: " + e.getMessage());
                        return 0; // If parsing fails, treat as equal
                    }
                });
            } else {
                // Sort by confirmationDate (oldest first) for Active tab
                orders.sort((o1, o2) -> {
                    try {
                        LocalDateTime date1 = LocalDateTime.parse(o1.getConfirmationDate(), DATE_TIME_FORMATTER);
                        LocalDateTime date2 = LocalDateTime.parse(o2.getConfirmationDate(), DATE_TIME_FORMATTER);
                        return date1.compareTo(date2); // Oldest first (ascending)
                    } catch (Exception e) {
                        System.err.println("Error parsing confirmationDate: " + e.getMessage());
                        return 0; // If parsing fails, treat as equal
                    }
                });
            }

            // Removed the 5-order limit
            // if (orders.size() > 5) {
            //     orders = orders.subList(0, 5);
            // }

            // Set the orders and active tab as request attributes
            request.setAttribute("orders", orders);
            request.setAttribute("activeTab", tab);

            // Forward to userProfile.jsp
            request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
        }
    }
}