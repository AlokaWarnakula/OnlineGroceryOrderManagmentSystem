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
  <title>Cancel Order - Grocery</title>
  <base href="${pageContext.request.contextPath}/">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderCancel.css">
  <link href="https://cdn.jsdelivr.net/npm/remixicon@4.5.0/fonts/remixicon.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
<header>
  <a href="${pageContext.request.contextPath}/UserProfileServlet" class="back-link"><i class="fas fa-arrow-left"></i> Back</a>
  <a href="${pageContext.request.contextPath}/index.jsp" class="logo"><i class="fa-solid fa-basket-shopping"></i> Grocery</a>
</header>

<section class="cancel-container">
  <div class="order-section">
    <h2 class="section-title">Ordered Products</h2>
    <div class="order-items" id="order-items">
      <%
        // File paths
        String ordersFile = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/orders.txt";
        String itemsFile = "/Users/alokawarnakula/TestOOPProjectFolder/OnlineGroceryOrderSystem/src/main/webapp/data/items.txt";

        // Get the orderNumber from the request
        String orderNumber = request.getParameter("orderNumber");
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
      %>
      <p>Error: Invalid order number.</p>
      <%
      } else {
        // Read the order from orders.txt
        List<Order> orders = FileUtil.readAllOrders(ordersFile);
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
  </div>

  <div class="cancel-form">
    <h2 class="section-title">CANCEL ORDER</h2>
    <form action="${pageContext.request.contextPath}/UserProfileServlet" method="get" id="cancel-form">
      <input type="hidden" name="action" value="cancelOrder">
      <input type="hidden" name="orderNumber" value="<%= orderNumber %>">
      <div class="total-info" id="total-info">
        <span>Total Quantity: <span id="total-quantity"><%= totalQuantity %></span></span>
        <span>Total Price: RS. <span id="total-price"><%= String.format("%.2f", totalPrice) %></span></span>
      </div>
      <button type="submit" class="cancel-btn">CANCEL</button>
    </form>
  </div>
  <%
      }
    }
  %>
</section>

<script src="${pageContext.request.contextPath}/js/orderCancel.js"></script>
</body>
</html>