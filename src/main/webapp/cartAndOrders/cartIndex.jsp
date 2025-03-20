<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.GroceryItem" %>
<%@ page import="model.User" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cart Page</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/remixicon@2.5.0/fonts/remixicon.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
</head>
<body>
<%
  User loggedInUser = (User) session.getAttribute("user");
  if (loggedInUser == null) {
    response.sendRedirect(request.getContextPath() + "/userLogin/login.jsp?error=Please log in to access the cart.");
    return;
  }

  List<GroceryItem> items = (List<GroceryItem>) request.getAttribute("items");
  if (items == null) {
    items = new ArrayList<>();
  }
  String category = (String) request.getAttribute("category");
  if (category == null || category.isEmpty()) {
    category = "All"; // Default category for display
  }
  Boolean isSearchResult = (Boolean) request.getAttribute("isSearchResult");
  if (isSearchResult == null) {
    isSearchResult = false;
  }
  String minPrice = request.getParameter("minPrice");
  String maxPrice = request.getParameter("maxPrice");
  String name = request.getParameter("name");
%>
<header>
  <a href="${pageContext.request.contextPath}/index.jsp" class="logo"><i class="fa-solid fa-basket-shopping"></i> GROCERY</a>
  <nav class="navbar">
    <a href="${pageContext.request.contextPath}/CartServlet?category=Produce">Produce</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Proteins">Proteins</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Dairy">Dairy</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Bakery">Bakery</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Pantry">Pantry</a>
    <a href="${pageContext.request.contextPath}/CartServlet?category=Snacks">Snacks</a>
  </nav>
  <div class="search-container">
    <form class="search-bar" id="search-form" action="${pageContext.request.contextPath}/CartServlet" method="get">
      <input type="text" placeholder="Search groceries..." name="name" id="name" value="<%= name != null ? name : "" %>">
      <button type="submit" class="search-btn">Search</button>
      <span class="dropdown-toggle" id="dropdown-toggle">▼</span>
      <div class="dropdown-menu" id="dropdown-menu">
        <div class="filter-item">
          <label for="category">Category:</label>
          <select name="category" id="category">
            <option value="All" <%= (category == null || "All".equals(category)) ? "selected" : "" %>>All</option>
            <option value="Produce" <%= "Produce".equals(category) ? "selected" : "" %>>Produce</option>
            <option value="Proteins" <%= "Proteins".equals(category) ? "selected" : "" %>>Proteins</option>
            <option value="Dairy" <%= "Dairy".equals(category) ? "selected" : "" %>>Dairy</option>
            <option value="Bakery" <%= "Bakery".equals(category) ? "selected" : "" %>>Bakery</option>
            <option value="Pantry" <%= "Pantry".equals(category) ? "selected" : "" %>>Pantry</option>
            <option value="Snacks" <%= "Snacks".equals(category) ? "selected" : "" %>>Snacks</option>
          </select>
        </div>
        <div class="filter-item">
          <label for="minPrice">Min Price:</label>
          <input type="number" name="minPrice" id="minPrice" min="0" step="0.01" placeholder="0.00" value="<%= minPrice != null ? minPrice : "" %>">
        </div>
        <div class="filter-item">
          <label for="maxPrice">Max Price:</label>
          <input type="number" name="maxPrice" id="maxPrice" min="0" step="0.01" placeholder="100.00" value="<%= maxPrice != null ? maxPrice : "" %>">
        </div>
      </div>
    </form>
  </div>
  <div class="cart-icon" id="cart-icon">
    <i class="fas fa-shopping-cart"></i>
    <span class="cart-item-count">0</span>
  </div>
</header>
<section class="shop">
  <h2 class="section-title">
    <%= isSearchResult ? "Search Results" : "Shop " + category %>
  </h2>
  <div class="product-content">
    <%
      if (items.isEmpty()) {
        String message;
        if (category != null && !category.equals("All")) {
          message = "No products available in the " + category + ".";
        } else {
          message = "No products available in the Entire Shop.";
        }
        out.println("<p>" + message + "</p>");
      } else {
        for (GroceryItem item : items) {
          boolean outOfStock = item.getQuantity() <= 0;
    %>
    <div class="product-box">
      <div class="img-box">
        <a href="${pageContext.request.contextPath}/ProductDetailsServlet?productId=<%= item.getProductID() %>">
          <img src="<%= item.getProductImageLink() %>" alt="<%= item.getProductName() %>">
        </a>
      </div>
      <h2 class="product-title"><%= item.getProductName() %></h2>
      <div class="price-and-cart">
        <span class="price">Rs. <%= String.format("%.2f", item.getProductPrice()) %></span>
        <% if (outOfStock) { %>
        <p class="out-of-stock" style="font-size: 16px; color: #e74c3c; margin: 10px 0; font-weight: 500;">Out of Stock</p>
        <% } else { %>
        <button class="add-cart" data-item-id="<%= item.getProductID() %>"><i class="fas fa-shopping-cart"></i></button>
        <% } %>
      </div>
    </div>
    <%
        }
      }
    %>
  </div>
</section>
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
<script>
  window.contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
</body>
</html>