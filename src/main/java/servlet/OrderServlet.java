package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.FileUtil;
import model.GroceryItem;
import model.User;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

public class OrderServlet extends HttpServlet {
    private static final String ITEMS_FILE = "C:\\Users\\Nimesh\\IdeaProjects\\OnlineGroceryOrderManagmentSystem\\src\\main\\webapp\\data\\items.txt";
    private static final String CART_FILE = "C:\\Users\\Nimesh\\IdeaProjects\\OnlineGroceryOrderManagmentSystem\\src\\main\\webapp\\data\\cart.txt";
    private static final String ORDERS_FILE = "C:\\Users\\Nimesh\\IdeaProjects\\OnlineGroceryOrderManagmentSystem\\src\\main\\webapp\\data\\orders.txt";
    private static final String LOGGED_IN_USER_FILE = "C:\\Users\\Nimesh\\IdeaProjects\\OnlineGroceryOrderManagmentSystem\\src\\main\\webapp\\data\\loggedInUser.txt";

    @Override
    public void init() throws ServletException {
        File ordersDir = new File("C:\\Users\\Nimesh\\IdeaProjects\\OnlineGroceryOrderManagmentSystem\\src\\main\\webapp\\data");
        if (!ordersDir.exists()) {
            ordersDir.mkdirs();
            System.out.println("Created directory: " + ordersDir.getAbsolutePath());
        }
        System.out.println("ITEMS_FILE path: " + ITEMS_FILE);
        System.out.println("CART_FILE path: " + CART_FILE);
        System.out.println("ORDERS_FILE path: " + ORDERS_FILE);
        System.out.println("LOGGED_IN_USER_FILE path: " + LOGGED_IN_USER_FILE);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Read the logged-in user from loggedInUser.txt
        User loggedInUser = FileUtil.readLoggedInUser(LOGGED_IN_USER_FILE);
        if (loggedInUser == null) {
            System.out.println("No logged-in user found in " + LOGGED_IN_USER_FILE);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No logged-in user found");
            return;
        }
        System.out.println("Logged-in user: " + loggedInUser);

        ArrayList<GroceryItem> cart;
        synchronized (this) {
            cart = FileUtil.readItems(CART_FILE);
            if (cart == null || cart.isEmpty()) {
                System.out.println("Cart is empty or null in " + CART_FILE);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cart is empty");
                return;
            }
            System.out.println("Loaded cart from " + CART_FILE + ": " + cart);
        }

        // Declare variables at method scope
        double totalPrice;
        String orderNumber;
        String deliveryDate;
        String userNumber;

        ArrayList<GroceryItem> items = FileUtil.readItems(ITEMS_FILE);

        synchronized (this) {
            // Only check if items exist, no stock validation since CartServlet reserved them
            for (GroceryItem cartItem : cart) {
                GroceryItem stockItem = items.stream()
                        .filter(item -> item.getProductID() == cartItem.getProductID())
                        .findFirst()
                        .orElse(null);
                if (stockItem == null) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Item ID " + cartItem.getProductID() + " not found in inventory");
                    return;
                }
            }

            totalPrice = cart.stream().mapToDouble(GroceryItem::getTotalPrice).sum();

            String fullName = request.getParameter("fullName");
            String phoneNumber = request.getParameter("phoneNumber");
            String address = request.getParameter("address");
            String deliveryMethod = request.getParameter("deliveryMethod");
            String paymentMethod = request.getParameter("paymentMethod");

            if ("same-day".equals(deliveryMethod)) {
                deliveryDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            } else {
                deliveryDate = request.getParameter("deliveryDate");
            }

            if (fullName == null || phoneNumber == null || address == null ||
                    deliveryMethod == null || paymentMethod == null ||
                    (("scheduled".equals(deliveryMethod) || "store pickup".equals(deliveryMethod)) && (deliveryDate == null || deliveryDate.isEmpty()))) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
                return;
            }

            do {
                orderNumber = generateOrderNumber();
            } while (!FileUtil.isOrderNumberUnique(orderNumber, ORDERS_FILE));

            userNumber = loggedInUser.getUserNumber();
            if (userNumber == null) {
                System.err.println("User number is null for logged-in user: " + loggedInUser);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User number is missing for the logged-in user");
                return;
            }
            System.out.println("User Number: " + userNumber);

            String confirmationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String paymentStatus = "online card".equals(paymentMethod) ? "Paid" : "Pending";
            String deliveryStatus = "Pending";
            String orderStatus = "active";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDERS_FILE, true))) {
                writer.write("--- Order Start: " + orderNumber + " ---\n");
                writer.write("orderNumber=" + orderNumber + "\n");
                writer.write("userNumber=" + userNumber + "\n");
                writer.write("name=" + (fullName != null ? fullName : "") + "\n");
                writer.write("phoneNum=" + (phoneNumber != null ? phoneNumber : "") + "\n");
                writer.write("address=" + (address != null ? address : "") + "\n");
                writer.write("deliveryMethod=" + (deliveryMethod != null ? deliveryMethod : "") + "\n");
                writer.write("paymentMethod=" + (paymentMethod != null ? paymentMethod : "") + "\n");
                writer.write("deliveryDate=" + (deliveryDate != null ? deliveryDate : "") + "\n");
                writer.write("confirmationDate=" + confirmationDate + "\n");
                writer.write("paymentStatus=" + paymentStatus + "\n");
                writer.write("deliveryStatus=" + deliveryStatus + "\n");
                writer.write("orderStatus=" + orderStatus + "\n");
                writer.write("[products]\n");
                for (GroceryItem item : cart) {
                    writer.write("productID=" + item.getProductID() + ", quantity=" + item.getQuantity() + "\n");
                }
                writer.write("[total]\n");
                writer.write("totalPrice=" + String.format("%.2f", totalPrice) + "\n");
                writer.write("--- Order End ---\n");
                writer.write("\n");
                System.out.println("Appended order to " + ORDERS_FILE);
            } catch (IOException e) {
                System.err.println("Failed to append order to " + ORDERS_FILE + ": " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save order: " + e.getMessage());
                return;
            }

            cart.clear();
            FileUtil.writeItems(CART_FILE, cart);
            System.out.println("Cleared cart in " + CART_FILE);
        }

        // Set request attributes for processCheckOut.jsp
        request.setAttribute("orderNumber", orderNumber);
        request.setAttribute("userNumber", userNumber);
        request.setAttribute("fullName", request.getParameter("fullName"));
        request.setAttribute("phoneNumber", request.getParameter("phoneNumber"));
        request.setAttribute("address", request.getParameter("address"));
        request.setAttribute("deliveryMethod", request.getParameter("deliveryMethod"));
        request.setAttribute("deliveryDate", deliveryDate);
        request.setAttribute("paymentMethod", request.getParameter("paymentMethod"));
        request.setAttribute("totalPrice", String.format("%.2f", totalPrice));
        request.setAttribute("confirmationDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        request.setAttribute("paymentStatus", "online card".equals(request.getParameter("paymentMethod")) ? "Paid" : "Pending");
        request.setAttribute("deliveryStatus", "Pending");
        request.setAttribute("orderStatus", "active");

        // Debug log to confirm userNumber before forwarding
        System.out.println("Setting userNumber in request attribute: " + userNumber);

        request.getRequestDispatcher("/cartAndOrders/processCheckOut.jsp").forward(request, response);
    }

    private String generateOrderNumber() {
        Random random = new Random();
        long number = Math.abs(random.nextLong() % 1000000000000L);
        return "OD" + String.format("%012d", number);
    }
}