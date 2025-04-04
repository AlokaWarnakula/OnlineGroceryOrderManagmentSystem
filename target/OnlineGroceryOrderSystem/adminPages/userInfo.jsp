<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.User, model.FileUtil.Order" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>User Information</title>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userDashboard.css?v=<%= System.currentTimeMillis() %>">
  <script src="${pageContext.request.contextPath}/js/userDashboard.js"></script>
</head>
<body>
<%
  // Check if session attributes are set; if not, redirect to admin login
  if (session.getAttribute("adminNumber") == null || session.getAttribute("adminEmail") == null) {
    response.sendRedirect(request.getContextPath() + "/adminLogin/login.jsp?error=sessionExpired");
    return;
  }

  // Check if the user has the correct role (Super Admin)
  String adminRole = (String) session.getAttribute("adminRole");
  if (adminRole == null || !"super".equalsIgnoreCase(adminRole)) {
    response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
    return;
  }

  User selectedUser = (User) request.getAttribute("selectedUser");
  List<Order> activeOrders = (List<Order>) request.getAttribute("activeOrders");
  List<Order> deliveredOrders = (List<Order>) request.getAttribute("deliveredOrders");
  String errorMessage = (String) request.getAttribute("errorMessage");
  String activeTab = request.getParameter("tab") != null ? request.getParameter("tab") : "Active";

  if (selectedUser == null) {
    response.sendRedirect(request.getContextPath() + "/UserAdminServlet?error=userNotFound");
    return;
  }
%>
<header>
  <a href="${pageContext.request.contextPath}/UserAdminServlet" class="back-link"><i class="fas fa-arrow-left"></i> Back</a>
  <a class="logo"><i class="fa-solid fa-user"></i> User Information</a>
</header>

<div class="content">
  <div class="profile-container">
    <div class="user-info">
      <div class="user-info-header">
        <h2>User Info</h2>
      </div>
      <div class="user-details">
        <div class="user-details-left">
          <div class="user-icon">
            <i class="fas fa-user-circle"></i>
          </div>
          <div class="user-details-text">
            <p><strong>User Number:</strong> <span id="userNumberDisplay"><%= selectedUser.getUserNumber() != null ? selectedUser.getUserNumber() : "Not available" %></span></p>
            <p><strong>Email:</strong> <span id="emailDisplay"><%= selectedUser.getEmail() %></span></p>
            <p><strong>Full Name:</strong> <span id="fullNameDisplay"><%= selectedUser.getFullName() %></span></p>
            <p><strong>Address:</strong> <span id="addressDisplay"><%= selectedUser.getAddress() != null ? selectedUser.getAddress() : "Not provided" %></span></p>
            <p><strong>Phone Number:</strong> <span id="phoneNumberDisplay"><%= selectedUser.getPhoneNumber() %></span></p>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="profile-container">
    <div class="activity-section">
      <div class="activity-tabs">
        <a href="${pageContext.request.contextPath}/UserAdminServlet?action=info&userNumber=<%= selectedUser.getUserNumber() %>&tab=Active" class="tab <%= "Active".equals(activeTab) ? "active" : "" %>">Active</a>
        <a href="${pageContext.request.contextPath}/UserAdminServlet?action=info&userNumber=<%= selectedUser.getUserNumber() %>&tab=Delivered" class="tab <%= "Delivered".equals(activeTab) ? "active" : "" %>">Delivered</a>
        <div class="search-bar">
          <form action="${pageContext.request.contextPath}/UserAdminServlet" method="get">
            <input type="hidden" name="action" value="info">
            <input type="hidden" name="userNumber" value="<%= selectedUser.getUserNumber() %>">
            <input type="hidden" name="tab" value="<%= activeTab %>">
            <input type="text" name="searchQuery" placeholder="Search OrderID" value="${param.searchQuery}">
            <button type="submit"><i class="fas fa-search"></i></button>
          </form>
        </div>
      </div>
      <div class="activity-list">
        <%
          List<Order> orders = "Active".equals(activeTab) ? activeOrders : deliveredOrders;
          if (orders == null || orders.isEmpty()) {
        %>
        <div class="activity-item">
          <p>No orders found.</p>
        </div>
        <%
        } else {
          for (Order order : orders) {
            String statusClass = "pending";
            if ("Delivered".equalsIgnoreCase(order.getDeliveryStatus())) {
              statusClass = "completed";
            } else if ("Cancelled".equalsIgnoreCase(order.getOrderStatus())) {
              statusClass = "disabled";
            }
        %>
        <div class="activity-item">
          <i class="fas fa-shopping-cart"></i>
          <p>
            Order <%= order.getOrderNumber() %> placed on <%= order.getConfirmationDate() %>
            (Status: <span class="status <%= statusClass %>"><%= order.getDeliveryStatus() %></span>)
            <a href="${pageContext.request.contextPath}/adminPages/orderCheck.jsp?orderNumber=<%= order.getOrderNumber() %>&userNumber=<%= selectedUser.getUserNumber() %>&source=<%= "Active".equals(activeTab) ? "active" : "delivered" %>" class="cancel-link">Info</a>
          </p>
          <span class="timestamp">
                            <% if ("Delivered".equals(activeTab)) { %>
                                <%= order.getDeliveredDate() != null && !order.getDeliveredDate().isEmpty() ? order.getDeliveredDate() : "N/A" %>
                            <% } else { %>
                                <%= order.getConfirmationDate() %>
                            <% } %>
                        </span>
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