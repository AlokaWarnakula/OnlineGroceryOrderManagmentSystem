<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Easy Payment - Online Grocery Order Management System</title>
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
  <h1 class="heading">Easy <span>Payment</span></h1>
  <div class="content">
    <p>At Grocery, we make your shopping experience seamless with our easy payment options. Whether you prefer credit cards, debit cards, or digital wallets, we’ve got you covered with a variety of secure payment methods to choose from.</p>
    <p>Our payment process is designed to be fast, simple, and secure. With just a few clicks, you can complete your purchase without any hassle, knowing that your personal and financial information is protected by advanced encryption technology.</p>
    <p>We also offer flexible payment plans for larger orders, allowing you to shop with confidence and convenience. At Grocery, easy payment means more time enjoying your groceries and less time worrying about the checkout process.</p>
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