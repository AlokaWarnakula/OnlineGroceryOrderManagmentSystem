<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Product Dashboard</title>
</head>
<body>
<%
    String adminRole = (String) session.getAttribute("adminRole");
    if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "product".equalsIgnoreCase(adminRole))) {
        response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
        return;
    }
%>
<h1>Product Dashboard</h1>
<p>This is the Product Dashboard.</p>
<%
    String adminServletUrl = request.getContextPath() + "/AdminServlet";
    System.out.println("productDashboard.jsp - Navigating back to AdminServlet: " + adminServletUrl);
%>
<a href="<%= adminServletUrl %>">Back to Admin Page</a>
</body>
</html>