<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en" dir="ltr">
<head>
    <meta charset="UTF-8">
    <title>Login and Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/logIn.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="background: url('https://wallpapers.com/images/hd/japan-farm-anime-landscape-yvqut0t4ra7u0j6l.jpg') no-repeat center center fixed; background-size: cover">
<div class="container">
    <input type="checkbox" id="flip">
    <div class="cover">
        <div class="front">
            <img src="https://i.pinimg.com/736x/6a/3a/1c/6a3a1c241f4e81ac57975037ab928d8b.jpg" alt="">
            <div class="text">
                <span class="text-1">Every new friend is a <br> new adventure</span>
                <span class="text-2">Let's get connected</span>
            </div>
        </div>
        <div class="back">
            <img src="https://cdn.donmai.us/original/56/9f/569f8455732d366e27f396611ead70b8.jpg">
            <div class="text">
                <span class="text-1">Complete miles of journey <br> with one step</span>
                <span class="text-2">Let's get started</span>
            </div>
        </div>
    </div>
    <div class="forms">
        <div class="form-content">
            <div class="login-form">
                <div class="title">Login</div>
                <%
                    // Check for error from LoginServlet (invalid credentials)
                    String error = (String) request.getAttribute("error");
                    if (error != null) {
                %>
                <p style="color: red;"><%= error %></p>
                <%
                    }
                    // Check for error from CartServlet (not logged in)
                    String loginError = request.getParameter("error");
                    if ("notLoggedIn".equals(loginError)) {
                %>
                <p style="color: red;">Login first before shopping</p>
                <%
                    }
                %>
                <form action="${pageContext.request.contextPath}/LoginServlet" method="post">
                    <div class="input-boxes">
                        <div class="input-box">
                            <i class="fas fa-envelope"></i>
                            <input type="email" name="email" placeholder="Enter your email" required>
                        </div>
                        <div class="input-box">
                            <i class="fas fa-lock"></i>
                            <input type="password" name="password" placeholder="Enter your password" required>
                        </div>
                        <div class="text"><a href="#">Forgot password?</a></div>
                        <div class="button input-box">
                            <input type="submit" value="Submit">
                        </div>
                        <div class="text sign-up-text">Don't have an account? <label for="flip">Signup now</label></div>
                    </div>
                </form>
            </div>
            <div class="signup-form">
                <div class="title">Signup</div>
                <%
                    error = (String) request.getAttribute("error");
                    if (error != null) {
                %>
                <p style="color: red;"><%= error %></p>
                <%
                    }
                %>
                <form action="${pageContext.request.contextPath}/RegisterServlet" method="post">
                    <div class="input-boxes">
                        <div class="input-box">
                            <i class="fas fa-user"></i>
                            <input type="text" name="fullName" placeholder="Enter your full name" required>
                        </div>
                        <div class="input-box">
                            <i class="fas fa-envelope"></i>
                            <input type="email" name="email" placeholder="Enter your email" required>
                        </div>
                        <div class="input-box">
                            <i class="fas fa-phone"></i>
                            <input type="text" name="phoneNumber" placeholder="Enter your phone number" required>
                        </div>
                        <div class="input-box">
                            <i class="fas fa-home"></i>
                            <input type="text" name="address" placeholder="Enter your address" required>
                        </div>
                        <div class="input-box">
                            <i class="fas fa-lock"></i>
                            <input type="password" name="password" placeholder="Enter your password" required>
                        </div>
                        <div class="button input-box">
                            <input type="submit" value="Submit">
                        </div>
                        <div class="text sign-up-text">Already have an account? <label for="flip">Login now</label></div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html> <!-- LogIn branch-->