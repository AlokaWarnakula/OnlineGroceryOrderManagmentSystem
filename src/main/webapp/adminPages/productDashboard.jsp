<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.ArrayList, java.util.Collections, java.util.Comparator, model.GroceryItem, model.FileUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Product Admin Dashboard</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/productDashboard.css?v=<%= System.currentTimeMillis() %>">
    <script src="${pageContext.request.contextPath}/js/productDashboard.js"></script>
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

    // Check if the user has the correct role (Super Admin or Product Admin)
    String adminRole = (String) session.getAttribute("adminRole");
    if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "product".equalsIgnoreCase(adminRole))) {
        response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
        return;
    }

    // Read products directly from items.txt
    String itemsFilePath = "/Users/gaganiprabuddhi/Downloads/OnlineGroceryOrderManagmentSystem-master/src/main/webapp/data/items.txt";
    List<GroceryItem> products = FileUtil.readItems(itemsFilePath);
    if (products == null) {
        products = new ArrayList<>();
    }

    // Sort products by productID in ascending order
    Collections.sort(products, new Comparator<GroceryItem>() {
        @Override
        public int compare(GroceryItem item1, GroceryItem item2) {
            return Integer.compare(item1.getProductID(), item2.getProductID());
        }
    });

    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<header>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/AdminServlet" class="back-icon"><i class="fa fa-arrow-left"></i></a>
    </div>
    <div class="logo">
        <i class="fa-solid fa-folder-open"></i> Product Admin Dashboard
    </div>
    <div class="search-bar">
        <input type="text" id="product-search" placeholder="Search Product ID" onkeyup="searchProducts()">
        <i class="fa fa-search"></i>
    </div>
</header>

<!-- Product Section -->
<div id="product-section" class="product-section">
    <div class="section-header">
        <h2>Manage Products</h2>
        <a href="${pageContext.request.contextPath}/ProductAdminServlet?action=add" class="add-btn">Add New Product</a>
    </div>
    <% if ("saveFailed".equals(request.getParameter("error"))) { %>
    <p style="color: red; text-align: center;">Failed to save the product. Please try again.</p>
    <% } %>
    <% if (errorMessage != null) { %>
    <p style="color: red; text-align: center;"><%= errorMessage %></p>
    <% } %>
    <div class="product-list" id="product-items">
        <%
            if (products.isEmpty()) {
        %>
        <p style="color: red; text-align: center;">No products found in the system.</p>
        <%
        } else {
            for (GroceryItem item : products) {
        %>
        <div class="product-row" data-product-id="<%= item.getProductID() %>">
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
            <span class="product-price">Price: <%= String.format("%.2f", item.getProductPrice()) %></span>
            <span class="product-quantity">Stock: <%= item.getQuantity() %></span>
            <span class="product-description"><%= item.getDescription() %></span>
            <a href="${pageContext.request.contextPath}/ProductAdminServlet?action=info&productID=<%= item.getProductID() %>" class="info-btn">Info</a>
        </div>
        <%
                }
            }
        %>
    </div>
</div>

</body>
</html>