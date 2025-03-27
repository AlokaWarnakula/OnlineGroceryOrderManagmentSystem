package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.Admin;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminServlet extends HttpServlet {
    private String ORDERS_FILE;
    private String DELIVERED_ORDERS_FILE;
    private String ADMINS_FILE;

    @Override
    public void init() throws ServletException {
        String basePath = getServletContext().getRealPath("/data/");
        ORDERS_FILE = basePath + "orders.txt";
        DELIVERED_ORDERS_FILE = basePath + "deliveredOrders.txt";
        ADMINS_FILE = basePath + "admins.txt";
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

        // Log the counts for debugging
        System.out.println("AdminServlet - Total Orders (non-cancelled orders in orders.txt): " + totalOrders);
        System.out.println("AdminServlet - Delivered Orders (deliveryStatus=delivered in deliveredOrders.txt): " + deliveredCount);
        System.out.println("AdminServlet - Cancelled Orders (unique count): " + cancelledOrders);

        // Set counts as request attributes
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("deliveredOrders", deliveredCount);
        request.setAttribute("cancelledOrders", cancelledOrders);

        // Forward to adminPage.jsp
        System.out.println("AdminServlet - Forwarding to /adminPages/adminPage.jsp");
        request.getRequestDispatcher("/adminPages/adminPage.jsp").forward(request, response);
    }
}