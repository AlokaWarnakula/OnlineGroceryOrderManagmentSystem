package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors; // Added this import

public class OrderAdminServlet extends HttpServlet {
    private String ORDERS_FILE;
    private String DELIVERED_ORDERS_FILE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        String basePath = getServletContext().getRealPath("/data/");
        ORDERS_FILE = basePath + "orders.txt";
        DELIVERED_ORDERS_FILE = basePath + "deliveredOrders.txt";
        System.out.println("OrderAdminServlet initialized with ORDERS_FILE path: " + ORDERS_FILE);
        System.out.println("OrderAdminServlet initialized with DELIVERED_ORDERS_FILE path: " + DELIVERED_ORDERS_FILE);
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

        // Read active orders from orders.txt
        List<FileUtil.Order> activeOrders = FileUtil.readAllOrders(ORDERS_FILE);
        if (activeOrders != null) {
            activeOrders.removeIf(order -> {
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;
                boolean isActive = orderStatus != null && orderStatus.equals("active");
                System.out.println("OrderAdminServlet - Order " + order.getOrderNumber() + " isActive: " + isActive + " (orderStatus: " + orderStatus + ")");
                return !isActive;
            });
            // Sort active orders by confirmationDate in ascending order (oldest first)
            Collections.sort(activeOrders, new Comparator<FileUtil.Order>() {
                @Override
                public int compare(FileUtil.Order o1, FileUtil.Order o2) {
                    String date1Str = o1.getConfirmationDate();
                    String date2Str = o2.getConfirmationDate();
                    if (date1Str == null && date2Str == null) return 0;
                    if (date1Str == null) return 1;
                    if (date2Str == null) return -1;
                    LocalDate date1 = parseDate(date1Str);
                    LocalDate date2 = parseDate(date2Str);
                    System.out.println("OrderAdminServlet - Comparing Active Orders: " + o1.getOrderNumber() + " (" + date1 + ") vs " + o2.getOrderNumber() + " (" + date2 + ")");
                    return date1.compareTo(date2); // Ascending order
                }
            });
            System.out.println("OrderAdminServlet - Loaded " + activeOrders.size() + " active orders from orders.txt");
        } else {
            System.out.println("OrderAdminServlet - No active orders found in orders.txt");
        }

        // Read all orders from deliveredOrders.txt (for both cancelled and delivered)
        List<FileUtil.Order> allDeliveredOrders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
        if (allDeliveredOrders != null) {
            System.out.println("OrderAdminServlet - Total orders in deliveredOrders.txt: " + allDeliveredOrders.size());
            for (FileUtil.Order order : allDeliveredOrders) {
                System.out.println("OrderAdminServlet - Order in deliveredOrders.txt: " + order.getOrderNumber() + ", deliveryStatus: " + order.getDeliveryStatus() + ", deliveredDate: " + order.getDeliveredDate());
            }
        } else {
            System.out.println("OrderAdminServlet - No orders found in deliveredOrders.txt");
        }

        // Filter cancelled orders from deliveredOrders.txt
        List<FileUtil.Order> cancelledOrders = allDeliveredOrders != null ? allDeliveredOrders : Collections.emptyList();
        cancelledOrders = cancelledOrders.stream().filter(order -> {
            String deliveryStatus = order.getDeliveryStatus() != null ? order.getDeliveryStatus().trim().toLowerCase() : null;
            boolean isCancelled = deliveryStatus != null && (deliveryStatus.equals("cancelled") || deliveryStatus.equals("canceled"));
            System.out.println("OrderAdminServlet - Order " + order.getOrderNumber() + " isCancelled: " + isCancelled + " (deliveryStatus: " + deliveryStatus + ")");
            return isCancelled;
        }).collect(Collectors.toList());

        // Sort cancelled orders by deliveredDate in descending order (newest first)
        Collections.sort(cancelledOrders, new Comparator<FileUtil.Order>() {
            @Override
            public int compare(FileUtil.Order o1, FileUtil.Order o2) {
                String date1Str = o1.getDeliveredDate();
                String date2Str = o2.getDeliveredDate();
                if (date1Str == null && date2Str == null) return 0;
                if (date1Str == null) return 1;
                if (date2Str == null) return -1;
                LocalDate date1 = parseDate(date1Str);
                LocalDate date2 = parseDate(date2Str);
                System.out.println("OrderAdminServlet - Comparing Cancelled Orders: " + o1.getOrderNumber() + " (" + date1 + ") vs " + o2.getOrderNumber() + " (" + date2 + ")");
                return date2.compareTo(date1); // Descending order (newest first)
            }
        });
        System.out.println("OrderAdminServlet - Loaded " + cancelledOrders.size() + " cancelled orders from deliveredOrders.txt");

        // Filter delivered orders from deliveredOrders.txt
        List<FileUtil.Order> deliveredOrders = allDeliveredOrders != null ? allDeliveredOrders : Collections.emptyList();
        deliveredOrders = deliveredOrders.stream().filter(order -> {
            String deliveryStatus = order.getDeliveryStatus() != null ? order.getDeliveryStatus().trim().toLowerCase() : null;
            boolean isDelivered = deliveryStatus != null && deliveryStatus.equals("delivered");
            System.out.println("OrderAdminServlet - Order " + order.getOrderNumber() + " isDelivered: " + isDelivered + " (deliveryStatus: " + deliveryStatus + ")");
            return isDelivered;
        }).collect(Collectors.toList());

        // Sort delivered orders by deliveredDate in descending order (newest first)
        Collections.sort(deliveredOrders, new Comparator<FileUtil.Order>() {
            @Override
            public int compare(FileUtil.Order o1, FileUtil.Order o2) {
                String date1Str = o1.getDeliveredDate();
                String date2Str = o2.getDeliveredDate();
                if (date1Str == null && date2Str == null) return 0;
                if (date1Str == null) return 1;
                if (date2Str == null) return -1;
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

    // Helper method to parse date strings that may include time
    private LocalDate parseDate(String dateStr) {
        try {
            // First try parsing as a full datetime (e.g., "2025-03-25 08:19:14")
            LocalDateTime dateTime = LocalDateTime.parse(dateStr, DATETIME_FORMATTER);
            return dateTime.toLocalDate();
        } catch (Exception e) {
            // If that fails, try parsing as just a date (e.g., "2025-03-25")
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        }
    }
}