<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.FileUtil" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Checkout - Grocery</title>
  <base href="${pageContext.request.contextPath}/">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/checkOut.css">
  <link href="https://cdn.jsdelivr.net/npm/remixicon@4.5.0/fonts/remixicon.css" rel="stylesheet" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body style="
background: rgb(255,255,255);
background: radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(244,255,240,1) 100%);
">
<header>
  <a href="${pageContext.request.contextPath}/index.jsp" class="logo"><i class="fa-solid fa-basket-shopping"></i> Grocery</a>
  <nav class="navbar">
    <a href="${pageContext.request.contextPath}/CartServlet?category=Produce">Produce</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Proteins">Proteins</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Dairy">Dairy</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Bakery">Bakery</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Pantry">Pantry</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Snacks">Snacks</a>
    <a href="${pageContext.request.contextPath}/cartAndOrders/checkOut.jsp">Checkout</a>
  </nav>
  <div class="search-container">
    <form class="search-bar" id="searchForm">
      <input type="text" name="name" id="name" placeholder="Search groceries">
      <button type="submit" class="search-btn">Search</button>
      <span class="dropdown-toggle" id="dropdown-toggle">▼</span>
    </form>
    <div class="dropdown-menu" id="dropdown-menu">
      <div class="filter-item">
        <label for="category">Product Category</label>
        <select name="category" id="category">
          <option value="">All</option>
          <option value="produce">Produce</option>
          <option value="proteins">Proteins</option>
          <option value="dairy">Dairy</option>
          <option value="bakery">Bakery</option>
          <option value="pantry">Pantry</option>
          <option value="snacks">Snacks</option>
        </select>
      </div>
      <div class="filter-item">
        <label for="minPrice">Min Price</label>
        <input type="number" name="minPrice" id="minPrice" min="0" step="0.01" placeholder="0" value="0">
      </div>
      <div class="filter-item">
        <label for="maxPrice">Max Price</label>
        <input type="number" name="maxPrice" id="maxPrice" min="0" step="0.01" placeholder="100" value="100">
      </div>
    </div>
  </div>
  <!--<div class="cart-icon" id="cart-icon">
    <i class="ri-shopping-cart-line"></i>
    <span class="cart-item-count">0</span>
  </div>-->
</header>

<div class="cart">
  <h2 class="cart-title">Your Cart</h2>
  <div class="cart-content"></div>
  <div class="total">
    <div class="total-price">Total RS. 0</div>
    <button class="btn-buy">Buy Now</button>
    <i class="ri-close-line" id="cart-close"></i>
  </div>
</div>

<section class="checkout-container">
  <div class="cart-section">
    <h2 class="section-title">List Product In Cart</h2>
    <div class="cart-items" id="cart-items"></div>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Produce" class="keep-shopping">Keep shopping</a>
  </div>

  <div class="checkout-form">
    <h2 class="section-title">CHECKOUT</h2>
    <%
      String loggedInUserFile = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/loggedInUser.txt";
      User loggedInUser = FileUtil.readLoggedInUser(loggedInUserFile);
      String fullName = "";
      String phoneNumber = "";
      String address = "";
      if (loggedInUser != null) {
        fullName = loggedInUser.getFullName() != null ? loggedInUser.getFullName() : "";
        phoneNumber = loggedInUser.getPhoneNumber() != null ? loggedInUser.getPhoneNumber() : "";
        address = loggedInUser.getAddress() != null ? loggedInUser.getAddress() : "";
      }
    %>
    <form action="${pageContext.request.contextPath}/OrderServlet" method="post" id="checkout-form">
      <input type="text" name="fullName" placeholder="Full Name" value="<%= fullName %>" required>
      <input type="text" name="phoneNumber" placeholder="Phone Number" value="<%= phoneNumber %>" required>
      <input type="text" name="address" placeholder="Address" value="<%= address %>" required>
      <select name="deliveryMethod" id="deliveryMethod" required>
        <option value="">Select Delivery Method</option>
        <option value="store pickup">Store Pickup</option>
        <option value="same-day">Same-Day Delivery</option>
        <option value="scheduled">Scheduled Delivery</option>
      </select>
      <input type="date" name="deliveryDate" id="deliveryDate" style="display: none;" min="<%= java.time.LocalDate.now().toString() %>">
      <select name="paymentMethod" id="paymentMethod" required>
        <option value="">Select Payment Method</option>
        <option value="online card">Online Card Payment</option>
        <option value="cash on delivery">Cash on Delivery</option>
      </select>
      <div class="total-info" id="total-info">
        <span>Total Quantity: <span id="total-quantity">0</span></span>
        <span>Total Price: RS. <span id="total-price">0.00</span></span>
      </div>
      <button type="submit" class="checkout-btn">CHECKOUT</button>
    </form>
  </div>
</section>

<div class="search-results" id="search-results"></div>

<script src="${pageContext.request.contextPath}/js/cart.js"></script>
<script src="${pageContext.request.contextPath}/js/checkOut.js"></script>
</body>
</html> <!-- checkOut branch -->