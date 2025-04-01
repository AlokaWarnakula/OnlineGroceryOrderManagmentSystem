// orderDashboard.js

function showSection(section) {
    // Hide all sections
    document.getElementById('active-section').style.display = 'none';
    document.getElementById('cancelled-section').style.display = 'none';
    document.getElementById('delivered-section').style.display = 'none';

    // Remove active class from all nav links
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });

    // Show the selected section and set the active class
    if (section === 'active') {
        document.getElementById('active-section').style.display = 'block';
        document.querySelector('a[onclick="navigateToTab(\'active\')"]').classList.add('active');
    } else if (section === 'cancelled') {
        document.getElementById('cancelled-section').style.display = 'block';
        document.querySelector('a[onclick="navigateToTab(\'cancelled\')"]').classList.add('active');
    } else if (section === 'delivered') {
        document.getElementById('delivered-section').style.display = 'block';
        document.querySelector('a[onclick="navigateToTab(\'delivered\')"]').classList.add('active');
    }
}

function searchOrders(section) {
    const searchInput = document.getElementById(section + '-search').value.toLowerCase();
    const orderList = document.getElementById(section + '-orders').getElementsByClassName('order-row');

    for (let i = 0; i < orderList.length; i++) {
        const orderId = orderList[i].getElementsByClassName('order-id')[0].textContent.toLowerCase();
        if (orderId.includes(searchInput)) {
            orderList[i].style.display = '';
        } else {
            orderList[i].style.display = 'none';
        }
    }
}

function showOrderDetails(orderId, section) {
    const detailsDiv = document.getElementById('details-' + orderId + '-' + section);
    if (detailsDiv.style.display === 'none' || detailsDiv.style.display === '') {
        detailsDiv.style.display = 'block';
    } else {
        detailsDiv.style.display = 'none';
    }
}

function navigateToTab(tab) {
    // Update the URL with the new tab parameter and reload the page
    window.location.href = `${window.location.pathname}?tab=${tab}`;
}

// Initialize the correct tab on page load based on the URL parameter
document.addEventListener('DOMContentLoaded', function() {
    // Get the tab parameter from the URL
    const urlParams = new URLSearchParams(window.location.search);
    const tab = urlParams.get('tab') || 'active'; // Default to 'active' if no tab is specified
    console.log("Initial tab from URL: " + tab);
    showSection(tab);
});