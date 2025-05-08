<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Order Confirmation - Grocery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/processCheckOut.css">
</head>
<body style="
background: rgb(255,255,255);
background: radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(244,255,240,1) 100%);
">
<div class="container">
    <h1>Order Confirmation</h1>
    <p>Thank you for your order!</p>
    <div class="order-details">
        <p><strong>Order Number:</strong> <%= request.getAttribute("orderNumber") %></p>
        <%-- Debug: Log the userNumber value --%>
        <% System.out.println("User ID in JSP: " + request.getAttribute("userNumber")); %>
        <p><strong>User ID:</strong> <%= request.getAttribute("userNumber") != null ? request.getAttribute("userNumber") : "" %></p>
        <p><strong>Full Name:</strong> <%= request.getAttribute("fullName") %></p>
        <p><strong>Phone Number:</strong> <%= request.getAttribute("phoneNumber") %></p>
        <p><strong>Address:</strong> <%= request.getAttribute("address") %></p>
        <p><strong>Delivery Method:</strong> <%= request.getAttribute("deliveryMethod") %></p>
        <% String deliveryDate = (String) request.getAttribute("deliveryDate"); %>
        <% if (deliveryDate != null && !deliveryDate.isEmpty()) { %>
        <p><strong>Delivery Date:</strong> <%= deliveryDate %></p>
        <% } %>
        <p><strong>Payment Method:</strong> <%= request.getAttribute("paymentMethod") %></p>
        <p><strong>Payment Status:</strong> <%= request.getAttribute("paymentStatus") %></p>
        <p><strong>Delivery Status:</strong> <%= request.getAttribute("deliveryStatus") %></p>
        <p><strong>Order Status:</strong> <%= request.getAttribute("orderStatus") %></p>
        <p><strong>Total Price:</strong> RS. <%= request.getAttribute("totalPrice") %></p>
    </div>
    <div class="links">
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn">Back to Home</a>
        <a href="${pageContext.request.contextPath}/CartServlet?category=Produce" class="btn">Back to Shopping</a>
    </div>
</div>
</body>
</html>