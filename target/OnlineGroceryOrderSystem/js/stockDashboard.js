function searchItems() {
    // Get the search input value
    let input = document.getElementById("stock-search").value.toLowerCase();
    // Get all stock rows
    let stockRows = document.getElementsByClassName("stock-row");

    // Loop through all stock rows and hide those that don't match the search query
    for (let i = 0; i < stockRows.length; i++) {
        let productId = stockRows[i].getAttribute("data-product-id").toLowerCase();
        if (productId.includes(input)) {
            stockRows[i].style.display = "flex";
        } else {
            stockRows[i].style.display = "none";
        }
    }
}