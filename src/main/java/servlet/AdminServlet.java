package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;
import model.Admin;

import java.io.IOException;
import java.util.List;

public class AdminServlet extends HttpServlet {
    private String ORDERS_FILE;
    private String DELIVERED_ORDERS_FILE;
    private String ADMINS_FILE;

    @Override
    public void init() throws ServletException {
        String basePath = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/";
        ORDERS_FILE = basePath + "orders.txt";
        DELIVERED_ORDERS_FILE = basePath + "deliveredOrders.txt";
        ADMINS_FILE = basePath + "admins.txt";
        System.out.println("ORDERS_FILE path: " + ORDERS_FILE);
        System.out.println("DELIVERED_ORDERS_FILE path: " + DELIVERED_ORDERS_FILE);
        System.out.println("ADMINS_FILE path: " + ADMINS_FILE);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminEmail") == null) {
            System.out.println("No admin session found. Redirecting to login page.");
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp");
            return;
        }

        // Fetch admin details including role
        String adminEmail = (String) session.getAttribute("adminEmail");
        Admin admin = FileUtil.readAdminByEmail(adminEmail, ADMINS_FILE);
        if (admin != null) {
            session.setAttribute("adminNumber", admin.getAdminNumber());
            session.setAttribute("adminRole", admin.getRole());
            System.out.println("Admin details fetched: " + admin);
        } else {
            System.out.println("Admin not found for email: " + adminEmail);
            session.setAttribute("adminRole", "Unknown");
        }

        // Read orders from orders.txt
        List<FileUtil.Order> orders = FileUtil.readAllOrders(ORDERS_FILE);
        int totalOrders = 0;
        if (orders != null) {
            for (FileUtil.Order order : orders) {
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;
                // Count all orders that are not cancelled as "Total Orders"
                if (orderStatus != null && !orderStatus.equals("cancelled")) {
                    totalOrders++;
                    System.out.println("Counting order in orders.txt as Total Order: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                } else {
                    System.out.println("Order in orders.txt not counted as Total Order: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                }
            }
        }

        // Read delivered orders from deliveredOrders.txt
        List<FileUtil.Order> deliveredOrders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
        int deliveredCount = 0;
        if (deliveredOrders != null) {
            for (FileUtil.Order order : deliveredOrders) {
                String deliveryStatus = order.getDeliveryStatus() != null ? order.getDeliveryStatus().trim().toLowerCase() : null;
                if ("delivered".equalsIgnoreCase(deliveryStatus)) {
                    deliveredCount++;
                    System.out.println("Counting order in deliveredOrders.txt as Delivered: " + order.getOrderNumber() + " with deliveryStatus: " + deliveryStatus);
                } else {
                    System.out.println("Order in deliveredOrders.txt not counted as Delivered: " + order.getOrderNumber() + " with deliveryStatus: " + deliveryStatus);
                }
            }
        }

        // Count cancelled orders from both orders.txt and deliveredOrders.txt (orderStatus=cancelled)
        int cancelledOrders = 0;
        // Check orders.txt
        if (orders != null) {
            for (FileUtil.Order order : orders) {
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;
                if (orderStatus != null && orderStatus.equals("cancelled")) {
                    cancelledOrders++;
                    System.out.println("Found cancelled order in orders.txt: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                } else {
                    System.out.println("Order in orders.txt not cancelled: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                }
            }
        }
        // Check deliveredOrders.txt
        if (deliveredOrders != null) {
            for (FileUtil.Order order : deliveredOrders) {
                String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus().trim().toLowerCase() : null;
                if (orderStatus != null && orderStatus.equals("cancelled")) {
                    cancelledOrders++;
                    System.out.println("Found cancelled order in deliveredOrders.txt: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                } else {
                    System.out.println("Order in deliveredOrders.txt not cancelled: " + order.getOrderNumber() + " with orderStatus: " + orderStatus);
                }
            }
        }

        // Log the counts for debugging
        System.out.println("Total Orders (non-cancelled orders in orders.txt): " + totalOrders);
        System.out.println("Cancelled Orders (orderStatus=cancelled in both files): " + cancelledOrders);
        System.out.println("Delivered Orders (deliveryStatus=delivered in deliveredOrders.txt): " + deliveredCount);

        // Set counts as request attributes
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("cancelledOrders", cancelledOrders);
        request.setAttribute("deliveredOrders", deliveredCount);

        // Forward to adminPage.jsp
        request.getRequestDispatcher("/adminPages/adminPage.jsp").forward(request, response);
    }
}