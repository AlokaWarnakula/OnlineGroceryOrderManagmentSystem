<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Admin Page</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
  <!-- Include Chart.js -->
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  <script src="${pageContext.request.contextPath}/js/admin.js"></script>
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

  // Check if statistics are set; if not, redirect to AdminServlet
  if (request.getAttribute("totalOrders") == null || request.getAttribute("deliveredOrders") == null || request.getAttribute("cancelledOrders") == null) {
    System.out.println("adminPage.jsp - Statistics not set, redirecting to AdminServlet");
    response.sendRedirect(request.getContextPath() + "/AdminServlet");
    return;
  }
%>
<header>
  <div class="nav-links"></div>
  <span class="logo"><i class="fa-solid fa-toolbox"></i> Admin Dashboard</span>
  <div class="header-icons">
    <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-icon"><i class="fa fa-sign-out"></i></a>
  </div>
</header>

<div class="profile-container">
  <div class="user-info">
    <h2>Admin Info</h2>
    <div class="user-details">
      <i class="fas fa-user-circle user-icon"></i>
      <div class="user-details-text">
        <%
          // Debug: Log the session attributes
          System.out.println("adminPage.jsp - adminNumber: " + session.getAttribute("adminNumber"));
          System.out.println("adminPage.jsp - adminEmail: " + session.getAttribute("adminEmail"));
          System.out.println("adminPage.jsp - adminRole: " + session.getAttribute("adminRole"));
        %>
        <p><strong>Admin Number:</strong> <%= session.getAttribute("adminNumber") != null ? session.getAttribute("adminNumber") : "Not available" %></p>
        <p><strong>Email:</strong> <%= session.getAttribute("adminEmail") != null ? session.getAttribute("adminEmail") : "Unknown" %></p>
        <p><strong>Role:</strong> <%= session.getAttribute("adminRole") != null ? session.getAttribute("adminRole") : "Unknown" %></p>
      </div>
    </div>
  </div>
</div>

<h1 class="heading">Current <span>Statistics</span></h1>

<div class="stats-container">
  <div class="stat-box">
    <div class="stat-header">
      <h2>Orders</h2>
    </div>
    <div class="stat-number">
      <%
        Object totalOrders = request.getAttribute("totalOrders");
        if (totalOrders == null) {
          System.out.println("adminPage.jsp - totalOrders attribute is null");
      %>
      0
      <%
      } else {
      %>
      <%= totalOrders %>
      <%
        }
      %>
    </div>
  </div>
  <div class="stat-box">
    <div class="stat-header">
      <h2>Delivered</h2>
    </div>
    <div class="stat-number">
      <%
        Object deliveredOrders = request.getAttribute("deliveredOrders");
        if (deliveredOrders == null) {
          System.out.println("adminPage.jsp - deliveredOrders attribute is null");
      %>
      0
      <%
      } else {
      %>
      <%= deliveredOrders %>
      <%
        }
      %>
    </div>
  </div>
  <div class="stat-box">
    <div class="stat-header">
      <h2>Cancelled</h2>
    </div>
    <div class="stat-number">
      <%
        Object cancelledOrders = request.getAttribute("cancelledOrders");
        if (cancelledOrders == null) {
          System.out.println("adminPage.jsp - cancelledOrders attribute is null");
      %>
      0
      <%
      } else {
      %>
      <%= cancelledOrders %>
      <%
        }
      %>
    </div>
  </div>
</div>

<!-- Chart Section -->
<h1 class="heading">Order <span>Trends</span></h1>
<div class="chart-container">
  <canvas id="ordersChart"></canvas>
</div>

<!-- Admin Dashboards Section with Role-Based Access -->
<h1 class="heading">Admin <span>Dashboards</span></h1>

<div class="dashboard-container">
  <%
    String adminRole = (String) session.getAttribute("adminRole");
    if (adminRole == null) {
      adminRole = "unknown"; // Fallback if role is not set
      System.out.println("adminPage.jsp - adminRole is null, setting to 'unknown'");
    }

    // Super Admin can access all dashboards
    if ("super".equalsIgnoreCase(adminRole)) {
  %>
  <a href="${pageContext.request.contextPath}/OrderAdminServlet" class="dashboard-box">
    <div class="dashboard-header">
      <h2>Order</h2>
    </div>
  </a>
  <a href="${pageContext.request.contextPath}/StockAdminServlet" class="dashboard-box">
    <div class="dashboard-header">
      <h2>Stock</h2>
    </div>
  </a>
  <a href="${pageContext.request.contextPath}/ProductAdminServlet" class="dashboard-box">
    <div class="dashboard-header">
      <h2>Product</h2>
    </div>
  </a>
  <a href="${pageContext.request.contextPath}/UserAdminServlet" class="dashboard-box">
    <div class="dashboard-header">
      <h2>User</h2>
    </div>
  </a>
  <%
    // Order Admin can only access Order dashboard
  } else if ("order".equalsIgnoreCase(adminRole)) {
  %>
  <a href="${pageContext.request.contextPath}/OrderAdminServlet" class="dashboard-box">
    <div class="dashboard-header">
      <h2>Order</h2>
    </div>
  </a>
  <%
    // Stock Admin can only access Stock dashboard
  } else if ("stock".equalsIgnoreCase(adminRole)) {
  %>
  <a href="${pageContext.request.contextPath}/StockAdminServlet" class="dashboard-box">
    <div class="dashboard-header">
      <h2>Stock</h2>
    </div>
  </a>
  <%
    // Product Admin can only access Product dashboard
  } else if ("product".equalsIgnoreCase(adminRole)) {
  %>
  <a href="${pageContext.request.contextPath}/ProductAdminServlet" class="dashboard-box">
    <div class="dashboard-header">
      <h2>Product</h2>
    </div>
  </a>
  <%
  } else {
    // If role is unknown or invalid, show a message
  %>
  <p>You do not have access to any dashboards.</p>
  <%
    }
  %>
</div>

<!-- JavaScript to Render the Chart -->
<script>
  // Dynamic data from JSP
  const labels = [
    <%
        List<String> chartLabels = (List<String>) request.getAttribute("chartLabels");
        if (chartLabels != null) {
            for (int i = 0; i < chartLabels.size(); i++) {
                out.print("'" + chartLabels.get(i) + "'");
                if (i < chartLabels.size() - 1) out.print(",");
            }
        }
    %>
  ];
  const deliveredData = [
    <%
        List<Integer> deliveredData = (List<Integer>) request.getAttribute("deliveredData");
        if (deliveredData != null) {
            for (int i = 0; i < deliveredData.size(); i++) {
                out.print(deliveredData.get(i));
                if (i < deliveredData.size() - 1) out.print(",");
            }
        }
    %>
  ];
  const cancelledData = [
    <%
        List<Integer> cancelledData = (List<Integer>) request.getAttribute("cancelledData");
        if (cancelledData != null) {
            for (int i = 0; i < cancelledData.size(); i++) {
                out.print(cancelledData.get(i));
                if (i < cancelledData.size() - 1) out.print(",");
            }
        }
    %>
  ];

  const ctx = document.getElementById('ordersChart').getContext('2d');
  new Chart(ctx, {
    type: 'bar', // Line chart
    data: {
      labels: labels,
      datasets: [
        {
          label: 'Delivered Orders',
          data: deliveredData,
          borderColor: '#2ECC71', // Darker green for the line
          backgroundColor: 'rgba(46, 204, 113, 0.2)', // Semi-transparent fill under the line
          fill: true, // Fill the area under the line
          borderWidth: 2, // Thickness of the line
          pointRadius: 4, // Size of the data points
          pointBackgroundColor: '#2ECC71' // Color of the data points
        },
        {
          label: 'Cancelled Orders',
          data: cancelledData,
          borderColor: '#E74C3C', // Darker red for the line
          backgroundColor: 'rgba(231, 76, 60, 0.2)', // Semi-transparent fill under the line
          fill: true, // Fill the area under the line
          borderWidth: 2, // Thickness of the line
          pointRadius: 4, // Size of the data points
          pointBackgroundColor: '#E74C3C' // Color of the data points
        }
      ]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {
          position: 'top',
        },
        title: {
          display: true,
          text: 'Delivered vs Cancelled Orders Over Time'
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Month'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Number of Orders'
          },
          beginAtZero: true
        }
      }
    }
  });
</script>

</body>
</html>