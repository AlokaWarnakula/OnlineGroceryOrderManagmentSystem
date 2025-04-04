<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.FileUtil" %>
<%@ page import="model.FileUtil.Order" %>
<%@ page import="model.GroceryItem" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Details - Admin</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userDashboard.css?v=<%= System.currentTimeMillis() %>">
    <script src="${pageContext.request.contextPath}/js/userDashboard.js"></script>
</head>
<body>
<header>
    <%
        String userNumber = request.getParameter("userNumber");
        String source = request.getParameter("source");
        String backLink = userNumber != null ?
                "/UserAdminServlet?action=info&userNumber=" + userNumber + "&tab=" + ("active".equalsIgnoreCase(source) ? "Active" : "Delivered") :
                "/UserAdminServlet";
    %>
    <a href="${pageContext.request.contextPath}<%= backLink %>" class="back-link"><i class="fas fa-arrow-left"></i> Back</a>
    <a class="logo"><i class="fa-solid fa-basket-shopping"></i> Order Details - Admin</a>
</header>

<div class="content">
    <div class="profile-container">
        <div class="activity-section">
            <div class="section-header">
                <%
                    // File paths
                    String deliveredOrdersFile = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/deliveredOrders.txt";
                    String ordersFile = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/orders.txt";
                    String itemsFile = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/items.txt";

                    // Get the orderNumber and source from the request
                    String orderNumber = request.getParameter("orderNumber");
                    source = request.getParameter("source");
                    if (orderNumber == null || orderNumber.trim().isEmpty()) {
                %>
                <p class="error-message">Error: Invalid order number.</p>
                <%
                } else {
                    // Read the order based on source
                    List<Order> orders = "delivered".equalsIgnoreCase(source) ?
                            FileUtil.readAllDeliveredOrders(deliveredOrdersFile) :
                            FileUtil.readAllOrders(ordersFile);
                    Order order = null;
                    for (Order o : orders) {
                        if (o.getOrderNumber().equals(orderNumber)) {
                            order = o;
                            break;
                        }
                    }

                    if (order == null) {
                %>
                <p class="error-message">Error: Order not found.</p>
                <%
                } else {
                    // Determine the title based on deliveryStatus
                    String sectionTitle = "Delivered Products";
                    if ("delivered".equalsIgnoreCase(source)) {
                        sectionTitle = "Delivered Products";
                    } else if ("active".equalsIgnoreCase(source)) {
                        sectionTitle = "Active Order Products";
                    }
                    if ("Cancelled".equalsIgnoreCase(order.getDeliveryStatus())) {
                        sectionTitle = "Canceled Products";
                    }
                %>
                <h2><%= sectionTitle %></h2>
                <%
                        }
                    }
                %>
            </div>
            <div class="activity-list">
                <%
                    if (orderNumber != null && !orderNumber.trim().isEmpty()) {
                        List<Order> orders = "delivered".equalsIgnoreCase(source) ?
                                FileUtil.readAllDeliveredOrders(deliveredOrdersFile) :
                                FileUtil.readAllOrders(ordersFile);
                        Order order = null;
                        for (Order o : orders) {
                            if (o.getOrderNumber().equals(orderNumber)) {
                                order = o;
                                break;
                            }
                        }

                        if (order != null) {
                            // Read items from items.txt to get product details
                            List<GroceryItem> items = FileUtil.readItems(itemsFile);
                            if (items == null) {
                                items = new ArrayList<>();
                            }

                            // Calculate total quantity and total price
                            int totalQuantity = 0;
                            double totalPrice = order.getTotalPrice();
                            List<String[]> products = order.getProducts();

                            // Display each product in the order
                            for (String[] product : products) {
                                String productID = product[0];
                                int quantity = Integer.parseInt(product[1]);
                                totalQuantity += quantity;

                                // Find the product in items.txt to get its details
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
                <div class="activity-item">
                    <img src="<%= item.getProductImageLink() %>" alt="<%= item.getProductName() %>" class="activity-item-img">
                    <p>
                        <%= item.getProductName() %> - RS. <%= String.format("%.2f", item.getProductPrice()) %> / Product X <%= quantity %>
                        (Total: RS. <%= String.format("%.2f", itemTotalPrice) %>)
                    </p>
                    <span class="timestamp">
                            <%= "delivered".equalsIgnoreCase(source) ? (order.getDeliveredDate() != null && !order.getDeliveredDate().isEmpty() ? order.getDeliveredDate() : "N/A") : order.getConfirmationDate() %>
                        </span>
                </div>
                <%
                        }
                    }
                %>
                <div class="total-info">
                    <span>Total Quantity: <%= totalQuantity %></span>
                    <span>Total Price: RS. <%= String.format("%.2f", totalPrice) %></span>
                </div>
                <%
                        }
                    }
                %>
            </div>
        </div>
    </div>
</div>
</body>
</html>