<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Admin Login Successful</title>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
  <link rel='stylesheet' href='//cdnjs.cloudflare.com/ajax/libs/animate.css/3.2.3/animate.min.css'>
  <style>
    body {
      margin: 0;
      padding: 0;
      height: 100vh;
      background-image: url('https://static.vecteezy.com/system/resources/previews/006/607/817/non_2x/customer-shopping-and-cashier-at-supermarket-checkout-counter-concept-illustration-vector.jpg');
      background-size: cover;
      background-repeat: no-repeat;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    #card {
      width: 1000px;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 10px;
      overflow: hidden;
      margin: 0 auto;
      text-align: center;
      font-family: 'Source Sans Pro', sans-serif;
      animation: fadeIn 1s ease-in-out;
    }

    #upper-side {
      padding: 4em;
      background-color: #8BC34A;
      background: rgba(0, 255, 0, 0.3);
      display: block;
      color: #fff;
      border-top-right-radius: 8px;
      border-top-left-radius: 8px;
    }

    #checkmark {
      font-weight: lighter;
      fill: #3E7B27;
      margin: -3.5em auto auto 65px;
    }

    #status {
      font-weight: lighter;
      text-transform: uppercase;
      letter-spacing: 2px;
      font-size: 1.2em;
      margin-top: -0.2em;
      margin-bottom: 10px;
      color: #3E7B27;
    }

    #lower-side {
      padding: 2em 2em 5em 2em;
      background: #fff;
      display: block;
      border-bottom-right-radius: 8px;
      border-bottom-left-radius: 8px;
    }

    #message {
      margin-top: -0.5em;
      color: #757575;
      letter-spacing: 1px;
      font-size: 1.1em;
      line-height: 1.5;
    }

    #contBtn {
      position: relative;
      top: 1.5em;
      text-decoration: none;
      background: #8BC34A;
      color: #fff;
      margin: auto;
      padding: 0.8em 3em;
      box-shadow: 0px 15px 30px rgba(50, 50, 50, 0.21);
      border-radius: 25px;
      transition: all 0.4s ease;
      font-size: 1.1em;
      text-transform: uppercase;
    }

    #contBtn:hover {
      box-shadow: 0px 15px 30px rgba(50, 50, 50, 0.41);
      transition: all 0.4s ease;
    }
  </style>
</head>
<body>
<div id='card'>
  <div id='upper-side'>
    <svg version="1.1" id="checkmark" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" xml:space="preserve">
            <path d="M131.583,92.152l-0.026-0.041c-0.713-1.118-2.197-1.447-3.316-0.734l-31.782,20.257l-4.74-12.65
            c-0.483-1.29-1.882-1.958-3.124-1.493l-0.045,0.017c-1.242,0.465-1.857,1.888-1.374,3.178l5.763,15.382
            c0.131,0.351,0.334,0.65,0.579,0.898c0.028,0.029,0.06,0.052,0.089,0.08c0.08,0.073,0.159,0.147,0.246,0.209
            c0.071,0.051,0.147,0.091,0.222,0.133c0.058,0.033,0.115,0.069,0.175,0.097c0.081,0.037,0.165,0.063,0.249,0.091
            c0.065,0.022,0.128,0.047,0.195,0.063c0.079,0.019,0.159,0.026,0.239,0.037c0.074,0.01,0.147,0.024,0.221,0.027
            c0.097,0.004,0.194-0.006,0.292-0.014c0.055-0.005,0.109-0.003,0.163-0.012c0.323-0.048,0.641-0.16,0.933-0.346l34.305-21.865
            C131.967,94.755,132.296,93.271,131.583,92.152z" />
      <circle fill="none" stroke="#3E7B27" stroke-width="5" stroke-miterlimit="10" cx="109.486" cy="104.353" r="32.53" />
        </svg>
    <h3 id='status'>Success</h3>
  </div>
  <div id='lower-side'>
    <p id='message'>
      <%
        String type = request.getParameter("type");
        // Debug: Log session attributes
        System.out.println("adminSuccessful.jsp - adminNumber: " + session.getAttribute("adminNumber"));
        System.out.println("adminSuccessful.jsp - adminEmail: " + session.getAttribute("adminEmail"));
        if ("login".equals(type)) {
      %>
      Login successful! Welcome, Admin.
      <% } else { %>
      An action was completed successfully.
      <% }
      %>
    </p>
    <a href="${pageContext.request.contextPath}/AdminServlet" id="contBtn">Go to Admin Dashboard</a>
  </div>
</div>
</body>
</html>