// Search users in userDashboard.jsp
function searchUsers() {
    const input = document.getElementById("user-search");
    if (input) {
        const searchValue = input.value.toLowerCase();
        const userRows = document.getElementsByClassName("user-row");

        for (let i = 0; i < userRows.length; i++) {
            const userNumber = userRows[i].getAttribute("data-user-number").toLowerCase();
            if (userNumber.includes(searchValue)) {
                userRows[i].style.display = "";
            } else {
                userRows[i].style.display = "none";
            }
        }
    }
}