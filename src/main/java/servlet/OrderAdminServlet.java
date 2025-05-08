package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.User;
import model.FileUtil.Order;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OrderAdminServlet extends HttpServlet {
    private static final String ORDERS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/orders.txt";
    private static final String DELIVERED_ORDERS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/deliveredOrders.txt";
    private static final String USERS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/users.txt";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        // Log the file paths for debugging
        System.out.println("OrderAdminServlet initialized with ORDERS_FILE path: " + ORDERS_FILE);
        System.out.println("OrderAdminServlet initialized with DELIVERED_ORDERS_FILE path: " + DELIVERED_ORDERS_FILE);
        System.out.println("OrderAdminServlet initialized with USERS_FILE path: " + USERS_FILE);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("OrderAdminServlet - doGet invoked with request URI: " + request.getRequestURI());
        System.out.println("OrderAdminServlet - Context Path: " + request.getContextPath());
        System.out.println("OrderAdminServlet - Servlet Path: " + request.getServletPath());

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminEmail") == null) {
            System.out.println("OrderAdminServlet - No admin session found. Redirecting to admin login page.");
            response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=notLoggedIn");
            return;
        }

        // Check if the user has the correct role (Order Admin or Super Admin)
        String adminRole = (String) session.getAttribute("adminRole");
        if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "order".equalsIgnoreCase(adminRole))) {
            System.out.println("OrderAdminServlet - Unauthorized access. Redirecting to AdminServlet.");
            response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");

        if ("info".equals(action)) {
            // Handle navigation to orderDashboardInfo.jsp
            String orderNumber = request.getParameter("orderNumber");
            String tab = request.getParameter("tab");
            if (orderNumber == null || tab == null) {
                System.out.println("OrderAdminServlet - Missing orderNumber or tab parameter for info action.");
                response.sendRedirect(request.getContextPath() + "/OrderAdminServlet?error=invalidParameters");
                return;
            }

            // Determine which file to read based on the tab
            List<Order> orders;
            if ("active".equalsIgnoreCase(tab)) {
                orders = FileUtil.readAllOrders(ORDERS_FILE);
            } else {
                orders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
            }

            // Find the order
            Order order = orders.stream()
                    .filter(o -> o.getOrderNumber().equals(orderNumber))
                    .findFirst()
                    .orElse(null);

            if (order == null) {
                System.out.println("OrderAdminServlet - Order not found: " + orderNumber);
                response.sendRedirect(request.getContextPath() + "/OrderAdminServlet?error=orderNotFound");
                return;
            }

            // Fetch user details
            List<User> users = FileUtil.readUsers(USERS_FILE);
            User user = users.stream()
                    .filter(u -> u.getUserNumber().equals(order.getUserNumber()))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                System.out.println("OrderAdminServlet - User not found for userNumber: " + order.getUserNumber());
                response.sendRedirect(request.getContextPath() + "/OrderAdminServlet?error=userNotFound");
                return;
            }

            // Set attributes for orderDashboardInfo.jsp
            request.setAttribute("order", order);
            request.setAttribute("user", user);
            request.setAttribute("tab", tab);
            System.out.println("OrderAdminServlet - Forwarding to /adminPages/orderDashboardInfo.jsp with orderNumber: " + orderNumber + ", tab: " + tab);
            request.getRequestDispatcher("/adminPages/orderDashboardInfo.jsp").forward(request, response);
            return;
        }

        // Read active orders from orders.txt
        List<Order> activeOrders = FileUtil.readAllOrders(ORDERS_FILE);
        if (activeOrders != null) {
            activeOrders = activeOrders.stream()
                    .filter(order -> {
                        String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : "";
                        boolean isActive = "pending".equals(orderStatus);
                        System.out.println("OrderAdminServlet - Order " + order.getOrderNumber() + " isActive: " + isActive + " (orderStatus: " + orderStatus + ")");
                        return isActive;
                    })
                    .collect(Collectors.toList());

            // Sort active orders by confirmationDate in ascending order (oldest first)
            Collections.sort(activeOrders, new Comparator<Order>() {
                @Override
                public int compare(Order o1, Order o2) {
                    String date1Str = o1.getConfirmationDate();
                    String date2Str = o2.getConfirmationDate();
                    LocalDate date1 = parseDate(date1Str);
                    LocalDate date2 = parseDate(date2Str);
                    System.out.println("OrderAdminServlet - Comparing Active Orders: " + o1.getOrderNumber() + " (" + date1 + ") vs " + o2.getOrderNumber() + " (" + date2 + ")");
                    return date1.compareTo(date2); // Ascending order
                }
            });
            System.out.println("OrderAdminServlet - Loaded " + activeOrders.size() + " active orders from orders.txt");
        } else {
            activeOrders = Collections.emptyList();
            System.out.println("OrderAdminServlet - No active orders found in orders.txt");
        }

        // Read all orders from deliveredOrders.txt (for both cancelled and delivered)
        List<Order> allDeliveredOrders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
        if (allDeliveredOrders != null) {
            System.out.println("OrderAdminServlet - Total orders in deliveredOrders.txt: " + allDeliveredOrders.size());
            for (Order order : allDeliveredOrders) {
                System.out.println("OrderAdminServlet - Order in deliveredOrders.txt: " + order.getOrderNumber() + ", deliveryStatus: " + order.getDeliveryStatus() + ", deliveredDate: " + order.getDeliveredDate());
            }
        } else {
            allDeliveredOrders = Collections.emptyList();
            System.out.println("OrderAdminServlet - No orders found in deliveredOrders.txt");
        }

        // Filter cancelled orders from deliveredOrders.txt
        List<Order> cancelledOrders = allDeliveredOrders.stream()
                .filter(order -> {
                    String deliveryStatus = order.getDeliveryStatus() != null ? order.getDeliveryStatus().trim().toLowerCase() : "";
                    boolean isCancelled = "cancelled".equals(deliveryStatus) || "canceled".equals(deliveryStatus);
                    System.out.println("OrderAdminServlet - Order " + order.getOrderNumber() + " isCancelled: " + isCancelled + " (deliveryStatus: " + deliveryStatus + ")");
                    return isCancelled;
                })
                .collect(Collectors.toList());

        // Sort cancelled orders by deliveredDate in descending order (newest first)
        Collections.sort(cancelledOrders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                String date1Str = o1.getDeliveredDate();
                String date2Str = o2.getDeliveredDate();
                LocalDate date1 = parseDate(date1Str);
                LocalDate date2 = parseDate(date2Str);
                System.out.println("OrderAdminServlet - Comparing Cancelled Orders: " + o1.getOrderNumber() + " (" + date1 + ") vs " + o2.getOrderNumber() + " (" + date2 + ")");
                return date2.compareTo(date1); // Descending order (newest first)
            }
        });
        System.out.println("OrderAdminServlet - Loaded " + cancelledOrders.size() + " cancelled orders from deliveredOrders.txt");

        // Filter delivered orders from deliveredOrders.txt
        List<Order> deliveredOrders = allDeliveredOrders.stream()
                .filter(order -> {
                    String deliveryStatus = order.getDeliveryStatus() != null ? order.getDeliveryStatus().trim().toLowerCase() : "";
                    boolean isDelivered = "delivered".equals(deliveryStatus);
                    System.out.println("OrderAdminServlet - Order " + order.getOrderNumber() + " isDelivered: " + isDelivered + " (deliveryStatus: " + deliveryStatus + ")");
                    return isDelivered;
                })
                .collect(Collectors.toList());

        // Sort delivered orders by deliveredDate in descending order (newest first)
        Collections.sort(deliveredOrders, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                String date1Str = o1.getDeliveredDate();
                String date2Str = o2.getDeliveredDate();
                LocalDate date1 = parseDate(date1Str);
                LocalDate date2 = parseDate(date2Str);
                System.out.println("OrderAdminServlet - Comparing Delivered Orders: " + o1.getOrderNumber() + " (" + date1 + ") vs " + o2.getOrderNumber() + " (" + date2 + ")");
                return date2.compareTo(date1); // Descending order (newest first)
            }
        });
        System.out.println("OrderAdminServlet - Loaded " + deliveredOrders.size() + " delivered orders from deliveredOrders.txt");

        // Set orders as request attributes
        request.setAttribute("activeOrders", activeOrders);
        request.setAttribute("cancelledOrders", cancelledOrders);
        request.setAttribute("deliveredOrders", deliveredOrders);

        // Forward to orderDashboard.jsp
        System.out.println("OrderAdminServlet - Forwarding to /adminPages/orderDashboard.jsp");
        request.getRequestDispatcher("/adminPages/orderDashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminEmail") == null) {
            System.out.println("OrderAdminServlet - No admin session found. Redirecting to admin login page.");
            response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=notLoggedIn");
            return;
        }

        String action = request.getParameter("action");
        if ("updateStatus".equals(action)) {
            String orderNumber = request.getParameter("orderNumber");
            String tab = request.getParameter("tab");
            String newStatus = request.getParameter("status");
            String newPaymentStatus = request.getParameter("paymentStatus"); // New parameter for cash on delivery

            if (orderNumber == null || tab == null || newStatus == null) {
                System.out.println("OrderAdminServlet - Missing parameters for updateStatus action.");
                response.sendRedirect(request.getContextPath() + "/OrderAdminServlet?error=invalidParameters");
                return;
            }

            synchronized (this) {
                // Determine the source file based on the tab
                List<Order> orders;
                String sourceFile;
                if ("active".equalsIgnoreCase(tab)) {
                    sourceFile = ORDERS_FILE;
                    orders = FileUtil.readAllOrders(ORDERS_FILE);
                } else {
                    sourceFile = DELIVERED_ORDERS_FILE;
                    orders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
                }

                // Find the order
                Order order = orders.stream()
                        .filter(o -> o.getOrderNumber().equals(orderNumber))
                        .findFirst()
                        .orElse(null);

                if (order == null) {
                    System.out.println("OrderAdminServlet - Order not found for update: " + orderNumber);
                    response.sendRedirect(request.getContextPath() + "/OrderAdminServlet?error=orderNotFound");
                    return;
                }

                // Update the order status
                String previousStatus = order.getDeliveryStatus();
                order.setDeliveryStatus(newStatus);
                order.setOrderStatus(newStatus.equalsIgnoreCase("Pending") ? "Pending" : newStatus.toLowerCase());

                // Handle paymentStatus
                if ("cash on delivery".equalsIgnoreCase(order.getPaymentMethod())) {
                    // For cash on delivery, use the admin-provided paymentStatus
                    if (newPaymentStatus != null && ("Pending".equalsIgnoreCase(newPaymentStatus) || "Completed".equalsIgnoreCase(newPaymentStatus) || "Cancelled".equalsIgnoreCase(newPaymentStatus))) {
                        order.setPaymentStatus(newPaymentStatus);
                    } else {
                        // Fallback to current paymentStatus if the provided value is invalid
                        System.out.println("OrderAdminServlet - Invalid paymentStatus provided for cash on delivery: " + newPaymentStatus + ". Keeping existing value: " + order.getPaymentStatus());
                    }
                } else {
                    // For other payment methods (e.g., online card), set paymentStatus automatically
                    order.setPaymentStatus(newStatus.equalsIgnoreCase("Delivered") ? "Completed" : newStatus.equalsIgnoreCase("Cancelled") ? "Cancelled" : "Pending");
                }

                // Set deliveredDate for "Delivered" or "Cancelled" status
                if ("Delivered".equalsIgnoreCase(newStatus) || "Cancelled".equalsIgnoreCase(newStatus)) {
                    order.setDeliveredDate(LocalDateTime.now().format(DATETIME_FORMATTER));
                } else if ("Delivered".equalsIgnoreCase(previousStatus) || "Cancelled".equalsIgnoreCase(previousStatus)) {
                    // Clear deliveredDate if the status is changed from Delivered or Cancelled to Pending
                    order.setDeliveredDate("");
                }

                // Determine the target file and move the order if necessary
                if ("active".equalsIgnoreCase(tab) && !newStatus.equalsIgnoreCase("Pending")) {
                    // Move from orders.txt to deliveredOrders.txt
                    orders.removeIf(o -> o.getOrderNumber().equals(orderNumber));
                    FileUtil.writeAllOrders(ORDERS_FILE, orders);
                    FileUtil.writeDeliveredOrder(DELIVERED_ORDERS_FILE, order);
                    tab = newStatus.equalsIgnoreCase("Delivered") ? "delivered" : "cancelled";
                } else if (!"active".equalsIgnoreCase(tab) && newStatus.equalsIgnoreCase("Pending")) {
                    // Move from deliveredOrders.txt to orders.txt
                    orders.removeIf(o -> o.getOrderNumber().equals(orderNumber));
                    FileUtil.writeAllDeliveredOrders(DELIVERED_ORDERS_FILE, orders);
                    FileUtil.writeOrder(ORDERS_FILE, order);
                    tab = "active";
                } else {
                    // Update in the same file
                    if ("active".equalsIgnoreCase(tab)) {
                        FileUtil.writeAllOrders(ORDERS_FILE, orders);
                    } else {
                        FileUtil.writeAllDeliveredOrders(DELIVERED_ORDERS_FILE, orders);
                    }
                }

                System.out.println("OrderAdminServlet - Updated order " + orderNumber + " with new status: " + newStatus + ", new paymentStatus: " + order.getPaymentStatus() + ", new tab: " + tab);
            }

            // Redirect back to orderDashboard.jsp with the updated tab
            response.sendRedirect(request.getContextPath() + "/OrderAdminServlet?tab=" + tab);
        }
    }

    // Helper method to parse date strings that may include time
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            System.out.println("OrderAdminServlet - parseDate: Date string is null or empty, returning LocalDate.MIN");
            return LocalDate.MIN; // Use a very early date for sorting (e.g., 0001-01-01)
        }

        try {
            // First try parsing as a full datetime (e.g., "2025-03-25 08:19:14")
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DATETIME_FORMATTER);
            return dateTime.toLocalDate();
        } catch (DateTimeParseException e1) {
            try {
                // If that fails, try parsing as just a date (e.g., "2025-03-25")
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e2) {
                System.err.println("OrderAdminServlet - parseDate: Invalid date format for date: " + dateStr + ". Using default date for sorting.");
                return LocalDate.MIN; // Fallback to a default date if parsing fails
            }
        }
    }
}