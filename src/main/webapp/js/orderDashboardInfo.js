// orderDashboardInfo.js

function navigateToTab(tab) {
    window.location.href = `${window.location.pathname}?tab=${tab}`;
}

function handleStatusChange() {
    // This function can be used for future client-side validation or UI updates
    // For now, it can be empty since the form submission handles the status update
}