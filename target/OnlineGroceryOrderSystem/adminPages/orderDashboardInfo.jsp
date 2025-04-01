<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.FileUtil, model.FileUtil.Order, model.User, model.GroceryItem, java.util.List, java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Order Details - Admin</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderDashboardInfo.css?v=<%= System.currentTimeMillis() %>">
    <script src="${pageContext.request.contextPath}/js/orderDashboardInfo.js"></script>
</head>
<body>
<%
    // Check session attributes
    if (session.getAttribute("adminNumber") == null || session.getAttribute("adminEmail") == null) {
        response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=sessionExpired");
        return;
    }

    String adminRole = (String) session.getAttribute("adminRole");
    if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "order".equalsIgnoreCase(adminRole))) {
        response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
        return;
    }

    // Get request attributes
    Order order = (Order) request.getAttribute("order");
    User user = (User) request.getAttribute("user");
    String tab = (String) request.getAttribute("tab");

    if (order == null || user == null || tab == null) {
        response.sendRedirect(request.getContextPath() + "/OrderAdminServlet?error=missingData");
        return;
    }

    // Debug: Print the tab value to confirm it's correct
    System.out.println("orderDashboardInfo.jsp - Tab value: " + tab);

    String sectionTitle = "Ordered Products";
    if ("delivered".equalsIgnoreCase(tab)) {
        sectionTitle = "Delivered Products";
    } else if ("cancelled".equalsIgnoreCase(tab)) {
        sectionTitle = "Cancelled Products";
    }
%>
<header>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/OrderAdminServlet?tab=<%= java.net.URLEncoder.encode(tab, "UTF-8") %>" class="back-icon"><i class="fa fa-arrow-left"></i></a>
        <a href="#" class="nav-link <%= "active".equalsIgnoreCase(tab) ? "active" : "" %>" onclick="navigateToTab('active')">Active</a>
        <a href="#" class="nav-link <%= "delivered".equalsIgnoreCase(tab) ? "active" : "" %>" onclick="navigateToTab('delivered')">Delivered</a>
        <a href="#" class="nav-link <%= "cancelled".equalsIgnoreCase(tab) ? "active" : "" %>" onclick="navigateToTab('cancelled')">Cancelled</a>
    </div>
    <div class="logo">
        <i class="fa-solid fa-box"></i> Order Admin Dashboard
    </div>
</header>

<section class="info-container">
    <!-- User Info Section -->
    <div class="user-info">
        <h2>User Info</h2>
        <div class="user-details">
            <i class="fas fa-user-circle user-icon"></i>
            <div class="details">
                <p><strong>User Number:</strong> <%= user.getUserNumber() %></p>
                <p><strong>Email:</strong> <%= user.getEmail() %></p>
                <p><strong>Full Name:</strong> <%= user.getFullName() %></p>
                <p><strong>Address:</strong> <%= user.getAddress() %></p>
                <p><strong>Phone Number:</strong> <%= user.getPhoneNumber() %></p>
            </div>
        </div>
    </div>

    <!-- Order Info Section -->
    <div class="order-info">
        <h2 class="section-title"><%= sectionTitle %></h2>
        <div class="order-items">
            <%
                // Use relative path for items.txt
                String itemsFile = application.getRealPath("/data/items.txt");
                List<GroceryItem> items = null;
                try {
                    items = FileUtil.readItems(itemsFile);
                } catch (Exception e) {
                    out.println("Error reading items: " + e.getMessage());
                }
                if (items == null) {
                    items = new ArrayList<>();
                }

                int totalQuantity = 0;
                double totalPrice = order.getTotalPrice();
                List<String[]> products = order.getProducts();

                for (String[] product : products) {
                    String productID = product[0];
                    int quantity = Integer.parseInt(product[1]);
                    totalQuantity += quantity;

                    GroceryItem item = null;
                    for (GroceryItem groceryItem : items) {
                        if (String.valueOf(groceryItem.getProductID()).equals(productID)) {
                            item = groceryItem;
                            break;
                        }
                    }

                    if (item != null) {
                        double itemTotalPrice = item.getProductPrice() * quantity;
            %>
            <div class="order-item">
                <img src="<%= item.getProductImageLink() %>" alt="<%= item.getProductName() %>" class="order-item-img">
                <div class="order-item-details">
                    <span class="order-product-title"><%= item.getProductName() %></span>
                    <span class="order-price">RS. <%= String.format("%.2f", item.getProductPrice()) %> / Product X <%= quantity %></span>
                    <span class="order-total">RS. <%= String.format("%.2f", itemTotalPrice) %></span>
                </div>
            </div>
            <%
                    }
                }
            %>
        </div>
        <div class="total-info">
            <span>Total Quantity: <span id="total-quantity"><%= totalQuantity %></span></span>
            <span>Total Price: RS. <span id="total-price"><%= String.format("%.2f", totalPrice) %></span></span>
        </div>
    </div>

    <!-- Status Update Section -->
    <div class="status-update">
        <h3>Update Order Status</h3>
        <form action="${pageContext.request.contextPath}/OrderAdminServlet" method="post">
            <input type="hidden" name="action" value="updateStatus">
            <input type="hidden" name="orderNumber" value="<%= order.getOrderNumber() %>">
            <input type="hidden" name="tab" value="<%= tab %>">
            <div class="status-field">
                <label for="status">Order Status:</label>
                <select name="status" id
                        ="status" onchange="handleStatusChange()">
                    <option value="Pending" <%= "Pending".equalsIgnoreCase(order.getDeliveryStatus()) ? "selected" : "" %>>Pending</option>
                    <option value="Delivered" <%= "Delivered".equalsIgnoreCase(order.getDeliveryStatus()) ? "selected" : "" %>>Delivered</option>
                    <option value="Cancelled" <%= "Cancelled".equalsIgnoreCase(order.getDeliveryStatus()) ? "selected" : "" %>>Cancelled</option>
                </select>
            </div>
            <% if ("cash on delivery".equalsIgnoreCase(order.getPaymentMethod())) { %>
            <div class="status-field">
                <label for="paymentStatus">Payment Status:</label>
                <select name="paymentStatus" id="paymentStatus">
                    <option value="Pending" <%= "Pending".equalsIgnoreCase(order.getPaymentStatus()) ? "selected" : "" %>>Pending</option>
                    <option value="Completed" <%= "Completed".equalsIgnoreCase(order.getPaymentStatus()) ? "selected" : "" %>>Completed</option>
                    <option value="Cancelled" <%= "Cancelled".equalsIgnoreCase(order.getPaymentStatus()) ? "selected" : "" %>>Cancelled</option>
                </select>
            </div>
            <% } %>
            <button type="submit" class="save-btn">Save</button>
        </form>
    </div>

    <!-- Order Stats Section -->
    <div class="order-stats">
        <h3>Order Stats</h3>
        <div class="stats-container">
            <div class="stat-box">
                <span class="stat-label">Order Date</span>
                <span class="stat-value"><%= order.getConfirmationDate() != null ? order.getConfirmationDate() : "N/A" %></span>
            </div>
            <div class="stat-box">
                <span class="stat-label">Delivered Date</span>
                <span class="stat-value"><%= order.getDeliveredDate() != null && !order.getDeliveredDate().isEmpty() ? order.getDeliveredDate() : "N/A" %></span>
            </div>
            <div class="stat-box">
                <span class="stat-label">Delivery Method</span>
                <span class="stat-value"><%= order.getDeliveryMethod() != null ? order.getDeliveryMethod() : "N/A" %></span>
            </div>
            <div class="stat-box">
                <span class="stat-label">Payment Method</span>
                <span class="stat-value"><%= order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A" %></span>
            </div>
            <div class="stat-box">
                <span class="stat-label">Payment Status</span>
                <span class="stat-value <%= order.getPaymentStatus().toLowerCase() %>"><%= order.getPaymentStatus() %></span>
            </div>
            <div class="stat-box">
                <span class="stat-label">Status</span>
                <span class="stat-value <%= order.getDeliveryStatus().toLowerCase() %>"><%= order.getDeliveryStatus() %></span>
            </div>
            <div class="stat-box">
                <span class="stat-label">Order Number</span>
                <span class="stat-value <%= order.getOrderNumber().toLowerCase() %>"><%= order.getOrderNumber() %></span>
            </div>
        </div>
    </div>
</section>
</body>
</html>