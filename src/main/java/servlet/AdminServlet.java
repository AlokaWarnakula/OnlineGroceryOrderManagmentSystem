package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.FileUtil;

import java.io.IOException;
import java.util.List;

public class AdminServlet extends HttpServlet {
    private String ORDERS_FILE;
    private String DELIVERED_ORDERS_FILE;

    @Override
    public void init() throws ServletException {
        String basePath = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/";
        ORDERS_FILE = basePath + "orders.txt";
        DELIVERED_ORDERS_FILE = basePath + "deliveredOrders.txt";
        System.out.println("ORDERS_FILE path: " + ORDERS_FILE);
        System.out.println("DELIVERED_ORDERS_FILE path: " + DELIVERED_ORDERS_FILE);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminEmail") == null) {
            System.out.println("No admin session found. Redirecting to login page.");
            response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp");
            return;
        }

        // Read orders from orders.txt
        List<FileUtil.Order> orders = FileUtil.readAllOrders(ORDERS_FILE);
        int totalOrders = orders != null ? orders.size() : 0;

        // Read delivered orders from deliveredOrders.txt
        List<FileUtil.Order> deliveredOrders = FileUtil.readAllDeliveredOrders(DELIVERED_ORDERS_FILE);
        int deliveredCount = deliveredOrders != null ? (int) deliveredOrders.stream()
                .filter(order -> "delivered".equalsIgnoreCase(order.getDeliveryStatus()))
                .count() : 0;

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
                    System.out.println("Order in orders.txt " + order.getOrderNumber() + " is not cancelled, orderStatus: " + order.getOrderStatus());
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
                    System.out.println("Order in deliveredOrders.txt " + order.getOrderNumber() + " is not cancelled, orderStatus: " + order.getOrderStatus());
                }
            }
        }

        // Log the counts for debugging
        System.out.println("Total Orders: " + totalOrders);
        System.out.println("Cancelled Orders (based on orderStatus): " + cancelledOrders);
        System.out.println("Delivered Orders (based on deliveryStatus): " + deliveredCount);

        // Set counts as request attributes
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("cancelledOrders", cancelledOrders);
        request.setAttribute("deliveredOrders", deliveredCount);

        // Forward to adminPage.jsp
        request.getRequestDispatcher("/adminPages/adminPage.jsp").forward(request, response);
    }
}