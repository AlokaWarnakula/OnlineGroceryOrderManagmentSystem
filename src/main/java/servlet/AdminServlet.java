package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.Admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AdminServlet extends HttpServlet {
    private static final String ORDERS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/orders.txt";
    private static final String DELIVERED_ORDERS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/deliveredOrders.txt";
    private static final String ADMINS_FILE = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/admins.txt";

    @Override
    public void init() throws ServletException {
        // Log the file paths for debugging
        System.out.println("AdminServlet initialized with ORDERS_FILE path: " + ORDERS_FILE);
        System.out.println("AdminServlet initialized with DELIVERED_ORDERS_FILE path: " + DELIVERED_ORDERS_FILE);
        System.out.println("AdminServlet initialized with ADMINS_FILE path: " + ADMINS_FILE);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("AdminServlet - doGet invoked with request URI: " + request.getRequestURI());
        System.out.println("AdminServlet - Context Path: " + request.getContextPath());
        System.out.println("AdminServlet - Servlet Path: " + request.getServletPath());

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminEmail") == null) {
            System.out.println("AdminServlet - No admin session found. Redirecting to admin login page.");
            response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=notLoggedIn");
            return;
        }

        // Fetch admin details including role
        String adminEmail = (String) session.getAttribute("adminEmail");
        Admin admin = FileUtil.readAdminByEmail(adminEmail, ADMINS_FILE);
        if (admin != null) {
            session.setAttribute("adminNumber", admin.getAdminNumber());
            session.setAttribute("adminRole", admin.getRole());
            System.out.println("AdminServlet - Admin details fetched: " + admin);
        } else {
            System.out.println("AdminServlet - Admin not found for email: " + adminEmail);
            session.setAttribute("adminRole", "unknown");
        }

        // Read orders from orders.txt
        List<FileUtil.Order> orders = FileUtil.readAllOrders(ORDERS_FILE);
        int totalOrders = 0;
        if (orders != null) {
            for (FileUtil.Order order : orders) {
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;
                if (orderStatus != null && !orderStatus.equals("cancelled")) {
                    totalOrders++;
                    System.out.println("AdminServlet - Counting order in orders.txt as Total Order: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                } else {
                    System.out.println("AdminServlet - Order in orders.txt not counted as Total Order: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                }
            }
        } else {
            System.out.println("AdminServlet - No orders found in orders.txt");
        }

        // Read delivered orders from deliveredOrders.txt
        List<FileUtil.Order> deliveredOrders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
        int deliveredCount = 0;
        if (deliveredOrders != null) {
            for (FileUtil.Order order : deliveredOrders) {
                String deliveryStatus = order.getDeliveryStatus() != null ? order.getDeliveryStatus().trim().toLowerCase() : null;
                if ("delivered".equalsIgnoreCase(deliveryStatus)) {
                    deliveredCount++;
                    System.out.println("AdminServlet - Counting order in deliveredOrders.txt as Delivered: " + order.getOrderNumber() + " with deliveryStatus: " + deliveryStatus);
                } else {
                    System.out.println("AdminServlet - Order in deliveredOrders.txt not counted as Delivered: " + order.getOrderNumber() + " with deliveryStatus: " + deliveryStatus);
                }
            }
        } else {
            System.out.println("AdminServlet - No orders found in deliveredOrders.txt");
        }

        // Count cancelled orders from both orders.txt and deliveredOrders.txt, avoiding double-counting
        Set<String> cancelledOrderNumbers = new HashSet<>();
        int cancelledOrders = 0;

        // Check orders.txt
        if (orders != null) {
            for (FileUtil.Order order : orders) {
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;
                if (orderStatus != null && orderStatus.equals("cancelled")) {
                    cancelledOrderNumbers.add(order.getOrderNumber());
                    System.out.println("AdminServlet - Found cancelled order in orders.txt: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                } else {
                    System.out.println("AdminServlet - Order in orders.txt not cancelled: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                }
            }
        }

        // Check deliveredOrders.txt
        if (deliveredOrders != null) {
            for (FileUtil.Order order : deliveredOrders) {
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;
                if (orderStatus != null && orderStatus.equals("cancelled")) {
                    cancelledOrderNumbers.add(order.getOrderNumber());
                    System.out.println("AdminServlet - Found cancelled order in deliveredOrders.txt: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                } else {
                    System.out.println("AdminServlet - Order in deliveredOrders.txt not cancelled: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                }
            }
        }

        cancelledOrders = cancelledOrderNumbers.size();
        System.out.println("AdminServlet - Unique Cancelled Orders (orderStatus=cancelled in both files): " + cancelledOrders);

        // Group delivered and cancelled orders by month for the chart
        Map<String, Integer> deliveredByMonth = new TreeMap<>(); // TreeMap to sort by month
        Map<String, Integer> cancelledByMonth = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");

        // Process delivered orders
        if (deliveredOrders != null) {
            for (FileUtil.Order order : deliveredOrders) {
                String deliveryStatus = order.getDeliveryStatus() != null ? order.getDeliveryStatus().trim().toLowerCase() : null;
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;

                // Use deliveredDate for delivered orders, confirmationDate for cancelled orders
                String dateStr = "delivered".equalsIgnoreCase(deliveryStatus) ? order.getDeliveredDate() : order.getConfirmationDate();

                if (dateStr != null && !dateStr.isEmpty()) {
                    try {
                        LocalDate date = LocalDate.parse(dateStr, formatter);
                        String monthKey = date.format(monthFormatter); // e.g., "Jan 2025"

                        if ("delivered".equalsIgnoreCase(deliveryStatus)) {
                            deliveredByMonth.put(monthKey, deliveredByMonth.getOrDefault(monthKey, 0) + 1);
                        } else if ("cancelled".equalsIgnoreCase(orderStatus)) {
                            cancelledByMonth.put(monthKey, cancelledByMonth.getOrDefault(monthKey, 0) + 1);
                        }
                    } catch (Exception e) {
                        System.out.println("AdminServlet - Error parsing date: " + dateStr + ", error: " + e.getMessage());
                    }
                }
            }
        }

        // Process cancelled orders from orders.txt (if not already counted in deliveredOrders.txt)
        if (orders != null) {
            for (FileUtil.Order order : orders) {
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;
                String dateStr = order.getConfirmationDate();

                if (orderStatus != null && orderStatus.equals("cancelled") && !cancelledOrderNumbers.contains(order.getOrderNumber())) {
                    if (dateStr != null && !dateStr.isEmpty()) {
                        try {
                            LocalDate date = LocalDate.parse(dateStr, formatter);
                            String monthKey = date.format(monthFormatter); // e.g., "Jan 2025"
                            cancelledByMonth.put(monthKey, cancelledByMonth.getOrDefault(monthKey, 0) + 1);
                        } catch (Exception e) {
                            System.out.println("AdminServlet - Error parsing confirmationDate: " + dateStr + ", error: " + e.getMessage());
                        }
                    }
                }
            }
        }

        // Prepare lists for the chart
        List<String> chartLabels = new ArrayList<>(deliveredByMonth.keySet());
        chartLabels.addAll(cancelledByMonth.keySet());
        chartLabels = new ArrayList<>(new TreeSet<>(chartLabels)); // Remove duplicates and sort

        List<Integer> deliveredData = new ArrayList<>();
        List<Integer> cancelledData = new ArrayList<>();
        for (String month : chartLabels) {
            deliveredData.add(deliveredByMonth.getOrDefault(month, 0));
            cancelledData.add(cancelledByMonth.getOrDefault(month, 0));
        }

        // Log the chart data for debugging
        System.out.println("AdminServlet - Chart Labels: " + chartLabels);
        System.out.println("AdminServlet - Delivered Data: " + deliveredData);
        System.out.println("AdminServlet - Cancelled Data: " + cancelledData);

        // Set counts and chart data as request attributes
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("deliveredOrders", deliveredCount);
        request.setAttribute("cancelledOrders", cancelledOrders);
        request.setAttribute("chartLabels", chartLabels);
        request.setAttribute("deliveredData", deliveredData);
        request.setAttribute("cancelledData", cancelledData);

        // Forward to adminPage.jsp
        System.out.println("AdminServlet - Forwarding to /adminPages/adminPage.jsp");
        request.getRequestDispatcher("/adminPages/adminPage.jsp").forward(request, response);
    }
}