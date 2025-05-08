// Search products by product ID
function searchProducts() {
    let input = document.getElementById("product-search").value.toLowerCase();
    let productRows = document.getElementsByClassName("product-row");

    for (let i = 0; i < productRows.length; i++) {
        let productId = productRows[i].getAttribute("data-product-id").toLowerCase();
        if (productId.includes(input)) {
            productRows[i].style.display = "flex";
        } else {
            productRows[i].style.display = "none";
        }
    }
}

// Preview image when the image link is updated
function previewImage() {
    let imageLink = document.getElementById("productImageLink").value;
    let imageElement = document.getElementById("product-image");
    let noImageText = document.getElementById("no-image-text");

    if (imageLink && imageLink.trim() !== "") {
        imageElement.src = imageLink;
        imageElement.style.display = "block";
        if (noImageText) {
            noImageText.style.display = "none";
        }
    } else {
        imageElement.src = "";
        imageElement.style.display = "none";
        if (noImageText) {
            noImageText.style.display = "block";
        }
    }
}

// Confirm deletion of a product
function confirmDelete(productID) {
    if (confirm("Are you sure you want to delete this product?")) {
        window.location.href = "/OnlineGroceryOrderSystem/ProductAdminServlet?action=delete&productID=" + productID;
    }
}