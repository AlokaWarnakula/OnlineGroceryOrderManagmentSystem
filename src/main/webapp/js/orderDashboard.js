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
        document.querySelector('a[onclick="showSection(\'active\')"]').classList.add('active');
    } else if (section === 'cancelled') {
        document.getElementById('cancelled-section').style.display = 'block';
        document.querySelector('a[onclick="showSection(\'cancelled\')"]').classList.add('active');
    } else if (section === 'delivered') {
        document.getElementById('delivered-section').style.display = 'block';
        document.querySelector('a[onclick="showSection(\'delivered\')"]').classList.add('active');
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

// Show the Active section by default on page load
document.addEventListener('DOMContentLoaded', function() {
    showSection('active');
});