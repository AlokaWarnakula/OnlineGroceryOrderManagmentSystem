<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.FileUtil.Order" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Order Admin Dashboard</title>
    <!-- Import Poppins font to match adminPage.css -->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <!-- Add cache-busting query parameter to force CSS reload -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderDashboard.css?v=<%= System.currentTimeMillis() %>">
    <script src="${pageContext.request.contextPath}/js/orderDashboard.js"></script>
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

    // Check if the user has the correct role (Order Admin or Super Admin)
    String adminRole = (String) session.getAttribute("adminRole");
    if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "order".equalsIgnoreCase(adminRole))) {
        response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
        return;
    }

    // Check if orders are set; if not, redirect to OrderAdminServlet
    if (request.getAttribute("activeOrders") == null || request.getAttribute("cancelledOrders") == null || request.getAttribute("deliveredOrders") == null) {
        System.out.println("orderDashboard.jsp - Orders not set, redirecting to OrderAdminServlet");
        response.sendRedirect(request.getContextPath() + "/OrderAdminServlet");
        return;
    }

    // Get the tab parameter from the request attribute
    String tab = (String) request.getAttribute("tab");
    if (tab == null || tab.trim().isEmpty()) {
        tab = "active"; // Default to "active" if no tab is specified
    }
%>
<header>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/AdminServlet" class="back-icon"><i class="fa fa-arrow-left"></i></a>
        <a href="#" class="nav-link <%= "active".equalsIgnoreCase(tab) ? "active" : "" %>" onclick="navigateToTab('active')">Active</a>
        <a href="#" class="nav-link <%= "delivered".equalsIgnoreCase(tab) ? "active" : "" %>" onclick="navigateToTab('delivered')">Delivered</a>
        <a href="#" class="nav-link <%= "cancelled".equalsIgnoreCase(tab) ? "active" : "" %>" onclick="navigateToTab('cancelled')">Cancelled</a>
    </div>
    <div class="logo">
        <i class="fa-solid fa-box"></i> Order Admin Dashboard
    </div>
</header>

<!-- Active Orders Section -->
<div id="active-section" class="order-section" style="display: <%= "active".equalsIgnoreCase(tab) ? "block" : "none" %>;">
    <div class="section-header">
        <h2>Active Orders</h2>
        <div class="search-bar">
            <input type="text" id="active-search" placeholder="Search Order ID" onkeyup="searchOrders('active')">
            <i class="fa fa-search"></i>
        </div>
    </div>
    <div class="order-list" id="active-orders">
        <%
            List<Order> activeOrders = (List<Order>) request.getAttribute("activeOrders");
            if (activeOrders != null && !activeOrders.isEmpty()) {
                for (Order order : activeOrders) {
        %>
        <div class="order-row">
            <i class="fa fa-shopping-cart"></i>
            <span class="order-id">Order <%= order.getOrderNumber() %></span> placed on <%= order.getConfirmationDate() != null ? order.getConfirmationDate() : "N/A" %>
            (Status: <span class="status pending">Pending</span>)
            <a href="${pageContext.request.contextPath}/OrderAdminServlet?action=info&orderNumber=<%= order.getOrderNumber() %>&tab=active" class="info-btn">Info</a>
            <div class="order-details" id="details-<%= order.getOrderNumber() %>-active" style="display: none;">
                <!-- Order details will be populated here -->
            </div>
        </div>
        <%
            }
        } else {
        %>
        <p>No active orders found.</p>
        <%
            }
        %>
    </div>
</div>

<!-- Cancelled Orders Section -->
<div id="cancelled-section" class="order-section" style="display: <%= "cancelled".equalsIgnoreCase(tab) ? "block" : "none" %>;">
    <div class="section-header">
        <h2>Cancelled Orders</h2>
        <div class="search-bar">
            <input type="text" id="cancelled-search" placeholder="Search Order ID" onkeyup="searchOrders('cancelled')">
            <i class="fa fa-search"></i>
        </div>
    </div>
    <div class="order-list" id="cancelled-orders">
        <%
            List<Order> cancelledOrders = (List<Order>) request.getAttribute("cancelledOrders");
            if (cancelledOrders != null && !cancelledOrders.isEmpty()) {
                for (Order order : cancelledOrders) {
        %>
        <div class="order-row">
            <i class="fa fa-shopping-cart"></i>
            <span class="order-id">Order <%= order.getOrderNumber() %></span> cancelled on <%= order.getConfirmationDate() != null ? order.getConfirmationDate() : "N/A" %>
            (Status: <span class="status cancelled">Cancelled</span>)
            <a href="${pageContext.request.contextPath}/OrderAdminServlet?action=info&orderNumber=<%= order.getOrderNumber() %>&tab=cancelled" class="info-btn">Info</a>
            <div class="order-details" id="details-<%= order.getOrderNumber() %>-cancelled" style="display: none;">
                <!-- Order details will be populated here -->
            </div>
        </div>
        <%
            }
        } else {
        %>
        <p>No cancelled orders found.</p>
        <%
            }
        %>
    </div>
</div>

<!-- Delivered Orders Section -->
<div id="delivered-section" class="order-section" style="display: <%= "delivered".equalsIgnoreCase(tab) ? "block" : "none" %>;">
    <div class="section-header">
        <h2>Delivered Orders</h2>
        <div class="search-bar">
            <input type="text" id="delivered-search" placeholder="Search Order ID" onkeyup="searchOrders('delivered')">
            <i class="fa fa-search"></i>
        </div>
    </div>
    <div class="order-list" id="delivered-orders">
        <%
            List<Order> deliveredOrders = (List<Order>) request.getAttribute("deliveredOrders");
            if (deliveredOrders != null && !deliveredOrders.isEmpty()) {
                for (Order order : deliveredOrders) {
        %>
        <div class="order-row">
            <i class="fa fa-shopping-cart"></i>
            <span class="order-id">Order <%= order.getOrderNumber() %></span> delivered on <%= order.getDeliveredDate() != null ? order.getDeliveredDate() : "N/A" %>
            (Status: <span class="status delivered">Delivered</span>)
            <a href="${pageContext.request.contextPath}/OrderAdminServlet?action=info&orderNumber=<%= order.getOrderNumber() %>&tab=delivered" class="info-btn">Info</a>
            <div class="order-details" id="details-<%= order.getOrderNumber() %>-delivered" style="display: none;">
                <!-- Order details will be populated here -->
            </div>
        </div>
        <%
            }
        } else {
        %>
        <p>No delivered orders found.</p>
        <%
            }
        %>
    </div>
</div>

</body>
</html>