// Toggle the edit form visibility
const editBtn = document.getElementById('edit-btn');
const profileFormContainer = document.getElementById('profile-form-container');
const userInfo = document.querySelector('.user-info');

if (editBtn) {
    editBtn.addEventListener('click', () => {
        if (profileFormContainer.style.display === 'none' || profileFormContainer.style.display === '') {
            profileFormContainer.style.display = 'block';
            userInfo.style.display = 'none'; // Hide the user info display
            editBtn.textContent = 'Cancel'; // Change button text to "Cancel"
            editBtn.innerHTML = '<i class="fas fa-times"></i> Cancel'; // Update icon
        } else {
            profileFormContainer.style.display = 'none';
            userInfo.style.display = 'block'; // Show the user info display
            editBtn.textContent = 'Edit Profile'; // Change button text back
            editBtn.innerHTML = '<i class="fas fa-edit"></i> Edit Profile'; // Update icon
        }
    });
}

// Delete Confirmation Modal Logic
const deleteBtn = document.getElementById('delete-btn');
const deleteModal = document.getElementById('deleteModal');
const confirmDelete = document.getElementById('confirmDelete');
const cancelDelete = document.getElementById('cancelDelete');

if (deleteBtn) {
    deleteBtn.addEventListener('click', () => {
        deleteModal.style.display = 'flex'; // Show the modal
    });
}

if (cancelDelete) {
    cancelDelete.addEventListener('click', () => {
        deleteModal.style.display = 'none'; // Hide the modal
    });
}

if (confirmDelete) {
    confirmDelete.addEventListener('click', () => {
        // Programmatically submit the form with action=delete
        const form = document.getElementById('profile-form');
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'delete';
        form.appendChild(actionInput);
        form.submit();
    });
}

// Close modal if user clicks outside of it
window.addEventListener('click', (event) => {
    if (event.target === deleteModal) {
        deleteModal.style.display = 'none';
    }
});