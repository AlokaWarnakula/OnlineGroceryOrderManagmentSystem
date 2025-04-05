<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.ArrayList, model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Admin Dashboard</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/userDashboard.css?v=<%= System.currentTimeMillis() %>">
    <script src="${pageContext.request.contextPath}/js/userDashboard.js"></script>
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

    // Check if the user has the correct role (Super Admin)
    String adminRole = (String) session.getAttribute("adminRole");
    if (adminRole == null || !"super".equalsIgnoreCase(adminRole)) {
        response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
        return;
    }

    List<User> users = (List<User>) request.getAttribute("users");
    if (users == null) {
        users = new ArrayList<>();
    }

    String errorMessage = (String) request.getAttribute("errorMessage");
%>
<header>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/AdminServlet" class="back-icon"><i class="fa fa-arrow-left"></i></a>
    </div>
    <div class="logo">
        <i class="fa-solid fa-users"></i> User Admin Dashboard
    </div>
    <div class="search-bar">
        <input type="text" id="user-search" placeholder="Search User Number" onkeyup="searchUsers()">
        <i class="fa fa-search"></i>
    </div>
</header>

<div class="content">
    <div class="profile-container">
        <div class="section-header">
            <h2>Manage Users</h2>
        </div>
        <% if (errorMessage != null) { %>
        <p class="error-message"><%= errorMessage %></p>
        <% } %>
        <div class="user-list" id="user-items">
            <%
                if (users.isEmpty()) {
            %>
            <p class="error-message">No users found in the system.</p>
            <%
            } else {
                for (User user : users) {
            %>
            <div class="user-row" data-user-number="<%= user.getUserNumber() %>">
                <span class="user-number">User #<%= user.getUserNumber() %></span>
                <span class="user-email"><%= user.getEmail() %></span>
                <span class="user-fullname"><%= user.getFullName() %></span>
                <a href="${pageContext.request.contextPath}/UserAdminServlet?action=info&userNumber=<%= user.getUserNumber() %>" class="cancel-link">Info</a>
            </div>
            <%
                    }
                }
            %>
        </div>
    </div>
</div>
</body>
</html>