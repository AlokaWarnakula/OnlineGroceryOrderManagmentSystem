<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.GroceryItem" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Product Details</title>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css" integrity="sha512-Evv84Mr4kqVGRNSgIGL/F/aIDqQb7xQ2vcrdIwxfjThSH8CSR7PBEakCr51Ck+w+/U6swU2Im1vVX0SVk9ABhg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
  <link rel='stylesheet' href='//cdnjs.cloudflare.com/ajax/libs/animate.css/3.2.3/animate.min.css'>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/productDashboard.css?v=<%= System.currentTimeMillis() %>">
  <script src="${pageContext.request.contextPath}/js/productDashboard.js"></script>
  <style>
    .product-info-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 20px;
      max-width: 600px;
      margin: 0 auto;
    }
    .product-image-preview {
      margin-bottom: 20px;
      text-align: center;
    }
    .product-image-preview img {
      max-width: 200px;
      max-height: 200px;
      border-radius: 8px;
    }
    .product-form {
      width: 100%;
      display: flex;
      flex-direction: column;
      gap: 15px;
    }
    .form-group {
      display: flex;
      align-items: center;
      gap: 10px;
    }
    .form-group label {
      flex: 0 0 120px;
      font-weight: 500;
    }
    .form-group input,
    .form-group select,
    .form-group textarea,
    .form-group span {
      flex: 1;
      padding: 8px;
      border: 1px solid #ccc;
      border-radius: 4px;
      font-size: 14px;
    }
    .form-group textarea {
      resize: vertical;
      min-height: 80px;
    }
    .form-actions {
      display: flex;
      justify-content: center;
      gap: 10px;
      margin-top: 20px;
    }
    .save-btn, .delete-btn, .confirm-btn, .cancel-btn {
      padding: 10px 20px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
    }
    .save-btn {
      background-color: #28a745;
      color: white;
    }
    .delete-btn {
      background-color: #dc3545;
      color: white;
    }
    .confirm-btn {
      background-color: #dc3545;
      color: white;
    }
    .cancel-btn {
      background-color: #6c757d;
      color: white;
    }

    /* Modal Styles */
    .modal {
      display: none;
      position: fixed;
      z-index: 1000;
      left: 0;
      top: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      justify-content: center;
      align-items: center;
    }
    .modal-content {
      width: 500px;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 10px;
      overflow: hidden;
      text-align: center;
      font-family: 'Source Sans Pro', sans-serif;
      animation: fadeIn 1s ease-in-out;
    }
    .modal-upper {
      padding: 2em;
      background-color: #dc3545;
      background: rgba(255, 0, 0, 0.3);
      color: #fff;
      border-top-right-radius: 8px;
      border-top-left-radius: 8px;
    }
    .modal-lower {
      padding: 2em;
      background: #fff;
      border-bottom-right-radius: 8px;
      border-bottom-left-radius: 8px;
    }
    .modal-status {
      font-weight: lighter;
      text-transform: uppercase;
      letter-spacing: 2px;
      font-size: 1.2em;
      margin-top: -0.2em;
      margin-bottom: 10px;
      color: #721c24;
    }
    .modal-message {
      margin-top: -0.5em;
      color: #757575;
      letter-spacing: 1px;
      font-size: 1.1em;
      line-height: 1.5;
    }
    .modal-actions {
      display: flex;
      justify-content: center;
      gap: 10px;
      margin-top: 1.5em;
    }
    .modal-actions a, .modal-actions button {
      text-decoration: none;
      padding: 0.8em 3em;
      border-radius: 25px;
      transition: all 0.4s ease;
      font-size: 1.1em;
      text-transform: uppercase;
    }
    .confirm-btn:hover, .cancel-btn:hover {
      box-shadow: 0px 15px 30px rgba(50, 50, 50, 0.41);
    }
  </style>
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

  // Check if the user has the correct role (Super Admin or Product Admin)
  String adminRole = (String) session.getAttribute("adminRole");
  if (adminRole == null || !("super".equalsIgnoreCase(adminRole) || "product".equalsIgnoreCase(adminRole))) {
    response.sendRedirect(request.getContextPath() + "/AdminServlet?error=unauthorized");
    return;
  }

  // Get the product and action from request attributes
  GroceryItem product = (GroceryItem) request.getAttribute("product");
  String action = request.getParameter("action");
  boolean isEdit = "info".equals(action) && product != null;
  String productID = isEdit ? String.valueOf(product.getProductID()) : (String) request.getAttribute("nextProductID");
%>
<header>
  <div class="nav-links">
    <a href="${pageContext.request.contextPath}/ProductAdminServlet" class="back-icon"><i class="fa fa-arrow-left"></i></a>
  </div>
  <div class="logo">
    <i class="fa-solid fa-folder-open"></i> <%= isEdit ? "Edit Product" : "Add New Product" %>
  </div>
</header>

<div class="product-info-section">
  <div class="product-image-preview">
    <% if (isEdit && product.getProductImageLink() != null && !product.getProductImageLink().isEmpty()) { %>
    <img id="product-image" src="<%= product.getProductImageLink() %>" alt="Product Image">
    <% } else { %>
    <img id="product-image" src="" alt="Product Image" style="display: none;">
    <p id="no-image-text">No Image</p>
    <% } %>
  </div>

  <form action="${pageContext.request.contextPath}/ProductAdminServlet" method="post" class="product-form">
    <input type="hidden" name="action" value="<%= isEdit ? "update" : "add" %>">
    <input type="hidden" name="productID" value="<%= productID %>">

    <div class="form-group">
      <label>Product Number:</label>
      <span><%= productID %></span>
    </div>

    <div class="form-group">
      <label for="productCategory">Category:</label>
      <select id="productCategory" name="productCategory" required>
        <option value="Produce" <%= isEdit && "Produce".equals(product.getProductCategory()) ? "selected" : "" %>>Produce</option>
        <option value="Proteins" <%= isEdit && "Proteins".equals(product.getProductCategory()) ? "selected" : "" %>>Proteins</option>
        <option value="Dairy" <%= isEdit && "Dairy".equals(product.getProductCategory()) ? "selected" : "" %>>Dairy</option>
        <option value="Bakery" <%= isEdit && "Bakery".equals(product.getProductCategory()) ? "selected" : "" %>>Bakery</option>
        <option value="Pantry" <%= isEdit && "Pantry".equals(product.getProductCategory()) ? "selected" : "" %>>Pantry</option>
        <option value="Snacks" <%= isEdit && "Snacks".equals(product.getProductCategory()) ? "selected" : "" %>>Snacks</option>
      </select>
    </div>

    <div class="form-group">
      <label for="productName">Name:</label>
      <input type="text" id="productName" name="productName" value="<%= isEdit ? product.getProductName() : "" %>" required>
    </div>

    <div class="form-group">
      <label for="productPrice">Price:</label>
      <input type="number" step="0.01" id="productPrice" name="productPrice" value="<%= isEdit ? String.format("%.2f", product.getProductPrice()) : "" %>" required>
    </div>

    <div class="form-group">
      <label for="productImageLink">Image Link:</label>
      <input type="url" id="productImageLink" name="productImageLink" value="<%= isEdit ? product.getProductImageLink() : "" %>" oninput="previewImage()" required>
    </div>

    <div class="form-group">
      <label for="quantity">Stock Count:</label>
      <input type="number" id="quantity" name="quantity" value="<%= isEdit ? product.getQuantity() : "" %>" required>
    </div>

    <div class="form-group">
      <label for="description">Description:</label>
      <textarea id="description" name="description" required><%= isEdit ? product.getDescription() : "" %></textarea>
    </div>

    <div class="form-actions">
      <button type="submit" class="save-btn">Save</button>
      <% if (isEdit) { %>
      <button type="button" class="delete-btn" onclick="showDeleteModal('<%= productID %>')">Delete</button>
      <% } %>
    </div>
  </form>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteModal" class="modal">
  <div class="modal-content">
    <div class="modal-upper">
      <h3 class="modal-status">Confirm Deletion</h3>
    </div>
    <div class="modal-lower">
      <p class="modal-message">
        Are you sure you want to delete this product? This action cannot be undone.
      </p>
      <div class="modal-actions">
        <a id="confirmDeleteLink" href="#" class="confirm-btn">Confirm</a>
        <button type="button" class="cancel-btn" onclick="closeDeleteModal()">Cancel</button>
      </div>
    </div>
  </div>
</div>

<script>
  function previewImage() {
    const imageLink = document.getElementById("productImageLink").value;
    const imageElement = document.getElementById("product-image");
    const noImageText = document.getElementById("no-image-text");

    if (imageLink) {
      imageElement.src = imageLink;
      imageElement.style.display = "block";
      noImageText.style.display = "none";
    } else {
      imageElement.style.display = "none";
      noImageText.style.display = "block";
    }
  }

  function showDeleteModal(productID) {
    const modal = document.getElementById("deleteModal");
    const confirmLink = document.getElementById("confirmDeleteLink");
    confirmLink.href = "${pageContext.request.contextPath}/ProductAdminServlet?action=delete&productID=" + productID;
    modal.style.display = "flex";
  }

  function closeDeleteModal() {
    const modal = document.getElementById("deleteModal");
    modal.style.display = "none";
  }
</script>

</body>
</html>