<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.FileUtil.Order" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Order Admin Dashboard</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/orderDashboard.css">
    <script src="${pageContext.request.contextPath}/js/orderDashboard.js"></script>
</head>
<body>
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
%>
<header>
    <div class="nav-bar">
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/AdminServlet" class="back-icon"><i class="fa fa-arrow-left"></i></a>
            <a href="#" class="nav-link active" onclick="showSection('active')">Active</a>
            <a href="#" class="nav-link" onclick="showSection('cancelled')">Cancelled</a>
            <a href="#" class="nav-link" onclick="showSection('delivered')">Delivered</a>
            <span class="logo"><i class="fa-solid fa-toolbox"></i> Order Admin Dashboard</span>
        </div>
    </div>
</header>

<!-- Active Orders Section -->
<div id="active-section" class="order-section">
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
            <button class="info-btn" onclick="showOrderDetails('<%= order.getOrderNumber() %>', 'active')">Info</button>
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
<div id="cancelled-section" class="order-section" style="display: none;">
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
            <button class="info-btn" onclick="showOrderDetails('<%= order.getOrderNumber() %>', 'cancelled')">Info</button>
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
<div id="delivered-section" class="order-section" style="display: none;">
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
            <button class="info-btn" onclick="showOrderDetails('<%= order.getOrderNumber() %>', 'delivered')">Info</button>
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