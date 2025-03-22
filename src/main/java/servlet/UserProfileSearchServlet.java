package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class UserProfileSearchServlet extends HttpServlet {
    private static final String ORDERS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/orders.txt";
    private static final String DELIVERED_ORDERS_FILE = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/deliveredOrders.txt";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp");
            return;
        }

        String userNumber = loggedInUser.getUserNumber();
        String tab = request.getParameter("tab") != null ? request.getParameter("tab") : "Active";
        String searchQuery = request.getParameter("searchQuery");

        List<FileUtil.Order> orders;
        if ("Delivered".equalsIgnoreCase(tab)) {
            orders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
        } else {
            orders = FileUtil.readAllOrders(ORDERS_FILE);
        }

        // Filter orders by userNumber
        orders = orders.stream()
                .filter(order -> userNumber.equals(order.getUserNumber()))
                .collect(Collectors.toList());

        // Apply search filter if searchQuery is provided
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.trim().toLowerCase();
            orders = orders.stream()
                    .filter(order -> order.getOrderNumber().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

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

        // Set attributes and forward to JSP
        request.setAttribute("orders", orders);
        request.setAttribute("activeTab", tab);
        request.getRequestDispatcher("/userLogin/userProfile.jsp").forward(request, response);
    }
}