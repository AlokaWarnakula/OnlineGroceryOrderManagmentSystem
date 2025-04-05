<%@ page import="model.GroceryItem" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Product Detail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@2.5.0/fonts/remixicon.css">
</head>
<body style="
background: rgb(255,255,255);
background: radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(244,255,240,1) 100%);
">
<header>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Produce" class="back-link"><i class="fas fa-arrow-left"></i> Back</a>
    <a href="${pageContext.request.contextPath}/index.jsp" class="logo"><i class="fa-solid fa-basket-shopping"></i> GROCERY</a>
    <div class="cart-icon" id="cart-icon">
        <i class="fas fa-shopping-cart"></i>
        <span class="cart-item-count">0</span>
    </div>
</header>

<div class="cart">
    <h2 class="cart-title">Your Cart</h2>
    <i class="ri-close-line" id="cart-close"></i>
    <div class="cart-content">
        <p>Your cart is empty.</p>
    </div>
    <div class="total" id="total-container" style="display: none;">
        <div class="total-price" id="total-price">Total Rs. 0.00</div>
        <a href="${pageContext.request.contextPath}/cartAndOrders/checkOut.jsp" class="btn-buy">Buy Now</a>
    </div>
</div>

<section class="shop product-detail-section">
    <h2 class="section-title">Product Detail</h2>
    <div class="product-content">
        <%
            GroceryItem item = (GroceryItem) request.getAttribute("item");
            if (item == null) {
                out.println("<p>Product not found.</p>");
            } else {
                boolean isOutOfStock = item.getQuantity() <= 0;
        %>
        <div class="product-detail-box">
            <div class="img-box">
                <img src="<%= item.getProductImageLink() %>" alt="<%= item.getProductName() %>">
            </div>
            <h2 class="product-title"><%= item.getProductName() %></h2>
            <span class="price">Rs. <%= String.format("%.2f", item.getProductPrice()) %></span>
            <p class="description">
                <%= item.getDescription() != null && !item.getDescription().isEmpty() ? item.getDescription() : "No description available." %>
            </p>
            <p class="category">Category: <%= item.getProductCategory() %></p>
            <% if (isOutOfStock) { %>
            <p class="out-of-stock" style="font-size: 16px; color: #e74c3c; margin: 10px 0; font-weight: 500;">Out of Stock</p>
            <% } else { %>
            <button class="add-cart-btn" data-item-id="<%= item.getProductID() %>">Add To Cart</button>
            <% } %>
        </div>
        <%
            }
        %>
    </div>
</section>

<script>
    window.contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
</body>
</html>