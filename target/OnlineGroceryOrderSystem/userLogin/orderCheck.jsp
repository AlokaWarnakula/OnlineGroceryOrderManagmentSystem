<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.FileUtil" %>
<%@ page import="model.FileUtil.Order" %>
<%@ page import="model.GroceryItem" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Details - Grocery</title>
    <base href="${pageContext.request.contextPath}/">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderCancel.css">
    <link href="https://cdn.jsdelivr.net/npm/remixicon@4.5.0/fonts/remixicon.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .total-info {
            margin-top: 20px;
            padding: 10px;
            background-color: #f9f9f9;
            border-radius: 5px;
            color: #2c3e50;
            display: flex;
            justify-content: space-between;
            font-size: 16px;
            font-weight: bold;
        }
    </style>
</head>
<body style="
background: rgb(255,255,255);
background: radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(244,255,240,1) 100%);
">
<header>
    <a href="${pageContext.request.contextPath}/UserProfileSearchServlet?tab=Delivered" class="back-link"><i class="fas fa-arrow-left"></i> Back</a>
    <a href="${pageContext.request.contextPath}/index.jsp" class="logo"><i class="fa-solid fa-basket-shopping"></i> Grocery</a>
</header>

<section class="cancel-container">
    <div class="order-section">
        <%
            // File paths
            String deliveredOrdersFile = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/deliveredOrders.txt";
            String itemsFile = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/items.txt";

            // Get the orderNumber from the request
            String orderNumber = request.getParameter("orderNumber");
            if (orderNumber == null || orderNumber.trim().isEmpty()) {
        %>
        <p>Error: Invalid order number.</p>
        <%
        } else {
            // Read the order from deliveredOrders.txt
            List<Order> orders = FileUtil.readAllDeliveredOrders(deliveredOrdersFile);
            Order order = null;
            for (Order o : orders) {
                if (o.getOrderNumber().equals(orderNumber)) {
                    order = o;
                    break;
                }
            }

            if (order == null) {
        %>
        <p>Error: Order not found.</p>
        <%
        } else {
            // Determine the title based on deliveryStatus
            String sectionTitle = "Delivered Products";
            if ("Cancelled".equalsIgnoreCase(order.getDeliveryStatus())) {
                sectionTitle = "Canceled Products";
            }
        %>
        <h2 class="section-title"><%= sectionTitle %></h2>
        <div class="order-items" id="order-items">
            <%
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
        <div class="total-info" id="total-info">
            <span>Total Quantity: <span id="total-quantity"><%= totalQuantity %></span></span>
            <span>Total Price: RS. <span id="total-price"><%= String.format("%.2f", totalPrice) %></span></span>
        </div>
        <%
                }
            }
        %>
    </div>
</section>

<script src="${pageContext.request.contextPath}/js/orderCancel.js"></script>
</body>
</html>