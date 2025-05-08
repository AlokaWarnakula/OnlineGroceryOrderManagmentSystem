<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Fresh And Organic - Online Grocery Order Management System</title>
    <link rel="icon" type="image/icon" href="../indexCJI/Images/favicon.PNG">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/features.css">
</head>
<body style="
background: rgb(255,255,255);
background: radial-gradient(circle, rgba(255,255,255,1) 0%, rgba(244,255,240,1) 100%);
">
<header class="Header">
    <a href="${pageContext.request.contextPath}/index.jsp" class="logo"><i class="fa-solid fa-basket-shopping"></i> Grocery</a>
    <nav class="navbar">
        <a href="${pageContext.request.contextPath}/index.jsp#Banner">Home</a>
        <a href="${pageContext.request.contextPath}/index.jsp#features">Features</a>
        <a href="${pageContext.request.contextPath}/index.jsp#Deals">Deals</a>
        <a href="${pageContext.request.contextPath}/index.jsp#categories">Categories</a>
    </nav>
    <div class="icons">
        <div class="fa-solid fa-bars" id="menu-btn"></div>
        <%
            User loggedInUser = (User) session.getAttribute("user");
            if (loggedInUser != null) {
        %>
        <a href="${pageContext.request.contextPath}/UserProfileServlet" class="icon-link">
            <i class="fa-solid fa-user profile-icon"></i>
        </a>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="icon-link">
            <i class="fa-solid fa-sign-out-alt logout-icon"></i>
        </a>
        <% } else { %>
        <a href="${pageContext.request.contextPath}/userLogin/login.jsp" class="icon-link">
            <i class="fa-solid fa-user login-icon"></i>
        </a>
        <% } %>
    </div>
</header>

<section class="feature-detail">
    <h1 class="heading">Fresh And <span>Organic</span></h1>
    <div class="content">
        <p>At Grocery, we pride ourselves on offering only the freshest and highest-quality organic produce. Sourced directly from trusted local farmers, our fruits, vegetables, and other grocery items are free from harmful pesticides and chemicals, ensuring that you and your family enjoy the purest flavors nature has to offer.</p>
        <p>Choosing fresh and organic means making a healthier choice for your body and the environment. Our products are packed with essential nutrients, free from artificial additives, and grown sustainably to support eco-friendly farming practices. From crisp greens to juicy fruits, every bite is a step towards a more vibrant lifestyle.</p>
        <p>We believe in transparency and quality, which is why we carefully select each item to meet strict organic standards. Whether you're preparing a wholesome meal or snacking on the go, our fresh and organic range guarantees exceptional taste and nutrition, straight from the farm to your table.</p>
        <a href="${pageContext.request.contextPath}/index.jsp#features" class="back-btn">Back to Features</a>
    </div>
</section>

<section class="footer" id="footer">
    <div class="box-container">
        <div class="box">
            <h3><i class="fa-solid fa-basket-shopping"></i> Grocery</h3>
            <p>Your one-stop shop for fresh, affordable, organic groceries delivered fast.</p>
            <div class="share">
                <a href="#" class="fa-brands fa-facebook-f"></a>
                <a href="#" class="fa-brands fa-x-twitter"></a>
                <a href="#" class="fa-brands fa-instagram"></a>
                <a href="#" class="fa-brands fa-linkedin-in"></a>
            </div>
        </div>
        <div class="box">
            <h3>Contact Info</h3>
            <a href="#" class="links"> <i class="fa-solid fa-phone"></i> +94 711641638</a>
            <a href="#" class="links"> <i class="fa-solid fa-phone"></i> +94 704563428</a>
            <a href="#" class="links"> <i class="fa-solid fa-envelope"></i> example@gmail.com</a>
            <a href="#" class="links"> <i class="fa-solid fa-location-dot"></i> Sliit, Sri Lanka - 10115</a>
        </div>
        <div class="box">
            <h3>Quick Links</h3>
            <a href="${pageContext.request.contextPath}/index.jsp#Banner" class="links"><i class="fa-solid fa-arrow-right"></i> Home</a>
            <a href="${pageContext.request.contextPath}/index.jsp#features" class="links"><i class="fa-solid fa-arrow-right"></i> Features</a>
            <a href="${pageContext.request.contextPath}/index.jsp#Deals" class="links"><i class="fa-solid fa-arrow-right"></i> Deals</a>
            <a href="${pageContext.request.contextPath}/index.jsp#categories" class="links"><i class="fa-solid fa-arrow-right"></i> Categories</a>
        </div>
        <div class="box">
            <h3>News Letter</h3>
            <p>Subscribe For Latest Grocery Update</p>
            <input type="email" placeholder="Your Email" class="email">
            <input type="submit" value="Subscribe" class="btn">
            <img src="https://www.shutterstock.com/image-vector/rivne-ukraine-may-25-2023-260nw-2308151527.jpg" class="payment-img">
        </div>
    </div>
    <div class="credit">PGNO-<span>278</span> | All Rights Reserved</div>
</section>

<script>
    // Toggle mobile menu
    document.getElementById('menu-btn').onclick = function() {
        document.querySelector('.Header .navbar').classList.toggle('active');
    };
</script>

</body>
</html>