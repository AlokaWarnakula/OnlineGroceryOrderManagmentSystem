<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.FileUtil.Order" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User Profile</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userProfile.css">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body style="
background: rgb(255,255,255);
background: radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(244,255,240,1) 100%);
">
<header>
  <a href="${pageContext.request.contextPath}/index.jsp" class="back-link"><i class="fas fa-arrow-left"></i> Back</a>
  <a  class="logo"><i class="fa-solid fa-user"></i> User Profile</a>
<%--  <a href="${pageContext.request.contextPath}/index.jsp"></a>--%>
</header>

<div class="content">
  <%
    User loggedInUser = (User) session.getAttribute("user");
    if (loggedInUser == null) {
  %>
  <p>You must be logged in to view this page.</p>
  <p><a href="${pageContext.request.contextPath}/userLogin/login.jsp">Log In</a></p>
  <% } else { %>
  <div class="profile-container">
    <div class="user-info">
      <div class="user-info-header">
        <h2>User Info</h2>
        <button id="edit-btn" class="edit-btn"><i class="fas fa-edit"></i> Edit Profile</button>
      </div>
      <div class="user-details">
        <div class="user-details-left">
          <div class="user-icon">
            <i class="fas fa-user-circle"></i>
          </div>
          <div class="user-details-text">
            <p><strong>User Number:</strong> <span id="userNumberDisplay"><%= loggedInUser.getUserNumber() != null ? loggedInUser.getUserNumber() : "Not available" %></span></p>
            <p><strong>Email:</strong> <span id="emailDisplay"><%= loggedInUser.getEmail() %></span></p>
            <p><strong>Full Name:</strong> <span id="fullNameDisplay"><%= loggedInUser.getFullName() %></span></p>
            <p><strong>Address:</strong> <span id="addressDisplay"><%= loggedInUser.getAddress() != null ? loggedInUser.getAddress() : "Not provided" %></span></p>
            <p><strong>Phone Number:</strong> <span id="phoneNumberDisplay"><%= loggedInUser.getPhoneNumber() %></span></p>
          </div>
        </div>
      </div>
    </div>

    <div id="profile-form-container" style="display: none;">
      <form id="profile-form" action="${pageContext.request.contextPath}/UserProfileServlet" method="post">
        <div class="form-group">
          <label for="userNumber">User Number:</label>
          <input type="text" id="userNumber" name="userNumber" value="<%= loggedInUser.getUserNumber() != null ? loggedInUser.getUserNumber() : "Not available" %>" readonly>
        </div>
        <div class="form-group">
          <label for="email">Email:</label>
          <input type="email" id="email" name="email" value="<%= loggedInUser.getEmail() %>" readonly>
        </div>
        <div class="form-group">
          <label for="fullName">Full Name:</label>
          <input type="text" id="fullName" name="fullName" value="<%= loggedInUser.getFullName() %>" required>
        </div>
        <div class="form-group">
          <label for="address">Address:</label>
          <textarea id="address" name="address" required><%= loggedInUser.getAddress() != null ? loggedInUser.getAddress() : "" %></textarea>
        </div>
        <div class="form-group">
          <label for="phoneNumber">Phone Number:</label>
          <input type="text" id="phoneNumber" name="phoneNumber" value="<%= loggedInUser.getPhoneNumber() %>" required>
        </div>
        <div class="form-buttons">
          <button type="submit" name="action" value="save" class="save-btn">Save</button>
          <button type="button" id="delete-btn" class="delete-btn">Delete Account</button>
        </div>
      </form>
    </div>
  </div>

  <div class="profile-container">
    <div class="activity-section">
      <div class="activity-tabs">
        <a href="${pageContext.request.contextPath}/UserProfileSearchServlet?tab=Active" class="tab <%= "Active".equals(request.getAttribute("activeTab")) ? "active" : "" %>">Active</a>
        <a href="${pageContext.request.contextPath}/UserProfileSearchServlet?tab=Delivered" class="tab <%= "Delivered".equals(request.getAttribute("activeTab")) ? "active" : "" %>">Delivered</a>
        <div class="search-bar">
          <form action="${pageContext.request.contextPath}/UserProfileSearchServlet" method="get">
            <input type="hidden" name="tab" value="${activeTab != null ? activeTab : 'Active'}">
            <input type="text" name="searchQuery" placeholder="Search OrderID" value="${param.searchQuery}">
            <button type="submit"><i class="fas fa-search"></i></button>
          </form>
        </div>
      </div>
      <div class="activity-list">
        <%
          List<Order> orders = (List<Order>) request.getAttribute("orders");
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
            <% if ("Active".equals(request.getAttribute("activeTab")) && !"Cancelled".equalsIgnoreCase(order.getOrderStatus())) { %>
            <a href="${pageContext.request.contextPath}/userLogin/orderCancel.jsp?orderNumber=<%= order.getOrderNumber() %>" class="cancel-link">Info</a>
            <% } else if ("Delivered".equals(request.getAttribute("activeTab"))) { %>
            <a href="${pageContext.request.contextPath}/userLogin/orderCheck.jsp?orderNumber=<%= order.getOrderNumber() %>" class="cancel-link">Info</a>
            <% } %>
          </p>
          <span class="timestamp">
            <% if ("Delivered".equals(request.getAttribute("activeTab"))) { %>
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

  <!-- Delete Confirmation Modal -->
  <div id="deleteModal" class="modal">
    <div class="modal-content">
      <div class="modal-header">
        <h3>Warning</h3>
      </div>
      <div class="modal-body">
        <p>Are you sure you want to delete your account? This action cannot be undone.</p>
        <div class="modal-buttons">
          <button id="confirmDelete" class="modal-btn confirm-btn">Confirm</button>
          <button id="cancelDelete" class="modal-btn cancel-btn">Cancel</button>
        </div>
      </div>
    </div>
  </div>
  <% } %>
</div>

<script src="${pageContext.request.contextPath}/js/userProfile.js"></script>
</body>
</html>