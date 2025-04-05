<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.GroceryItem" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Stock Admin Dashboard</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/stockDashboard.css?v=<%= System.currentTimeMillis() %>">
    <script src="${pageContext.request.contextPath}/js/stockDashboard.js"></script>
</head>
<body style="
background: rgb(255,255,255);
background: radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(244,255,240,1) 100%);
">
<%
    // Check if session attributes are set; if not, redirect to admin login
    if (session.getAttribute("adminNumber") == null || session.getAttribute("adminEmail") == null) {
        response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=sessionExpired");
        return;
    }

    // Check if the user has the correct role (Super Admin or Stock Admin)
    String adminRole = (String) session.getAttribute("adminRole");
    if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "stock".equalsIgnoreCase(adminRole))) {
        response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
        return;
    }

    // Get the low stock items from the request attribute
    List<GroceryItem> lowStockItems = (List<GroceryItem>) request.getAttribute("lowStockItems");
    if (lowStockItems == null) {
        response.sendRedirect(request.getContextPath() + "/StockAdminServlet?error=missingData");
        return;
    }
%>
<header>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/AdminServlet" class="back-icon"><i class="fa fa-arrow-left"></i></a>
    </div>
    <div class="logo">
        <i class="fa-solid fa-chart-column"></i> Stock Admin Dashboard
    </div>
    <div class="search-bar">
        <input type="text" id="stock-search" placeholder="Search Product ID" onkeyup="searchItems()">
        <i class="fa fa-search"></i>
    </div>
</header>

<!-- Refill Stock Section -->
<div id="stock-section" class="stock-section">
    <div class="section-header">
        <h2>Refill Stock</h2>
    </div>
    <div class="stock-list" id="stock-items">
        <%
            if (lowStockItems != null && !lowStockItems.isEmpty()) {
                for (GroceryItem item : lowStockItems) {
        %>
        <div class="stock-row" data-product-id="<%= item.getProductID() %>">
            <span class="product-id">Product #<%= item.getProductID() %></span>
            <span class="product-image">
                <% if (item.getProductImageLink() != null && !item.getProductImageLink().isEmpty()) { %>
                    <img src="<%= item.getProductImageLink() %>" alt="<%= item.getProductName() %>" class="item-image">
                <% } else { %>
                    No Image
                <% } %>
            </span>
            <span class="product-name"><%= item.getProductName() %></span>
            <span class="product-category"><%= item.getProductCategory() %></span>
            <span class="stock-count">Stock: <%= item.getQuantity() %></span>
            <form action="${pageContext.request.contextPath}/StockAdminServlet" method="post" class="stock-update-form">
                <input type="hidden" name="action" value="updateStock">
                <input type="hidden" name="productID" value="<%= item.getProductID() %>">
                <textarea name="stockCount" placeholder="Enter new stock count" class="stock-input" required></textarea>
                <button type="submit" class="save-btn">Save</button>
            </form>
        </div>
        <%
            }
        } else {
        %>
        <p>No items with low stock found.</p>
        <%
            }
        %>
    </div>
</div>

</body>
</html>