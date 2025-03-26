<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Admin Dashboard</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
  <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</head>
<body>
<header>
  <div class="nav-links">
  </div>
  <span class="logo"><i class="fa-solid fa-toolbox"></i> Admin Dashboard</span>
  <div class="header-icons">
    <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-icon"><i class="fa fa-sign-out"></i></a>
  </div>
</header>

<div class="profile-container">
  <div class="user-info">
    <div class="user-info-header">
      <h2>Admin Info</h2>
    </div>
    <div class="user-details">
      <div class="user-details-left">
        <div class="user-icon">
          <i class="fas fa-user-circle"></i>
        </div>
        <div class="user-details-text">
          <%
            // Debug: Log the session attributes
            System.out.println("adminPage.jsp - adminNumber: " + session.getAttribute("adminNumber"));
            System.out.println("adminPage.jsp - adminEmail: " + session.getAttribute("adminEmail"));
          %>
          <p><strong>Admin Number:</strong> <span id="adminNumberDisplay"><%= session.getAttribute("adminNumber") != null ? session.getAttribute("adminNumber") : "Not available" %></span></p>
          <p><strong>Email:</strong> <span id="emailDisplay"><%= session.getAttribute("adminEmail") != null ? session.getAttribute("adminEmail") : "Unknown" %></span></p>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>