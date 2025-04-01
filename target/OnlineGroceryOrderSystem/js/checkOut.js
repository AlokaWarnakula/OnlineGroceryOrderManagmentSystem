document.addEventListener('DOMContentLoaded', () => {
    const cartItemsContainer = document.getElementById('cart-items');
    const totalQuantityElement = document.getElementById('total-quantity');
    const totalPriceElement = document.getElementById('total-price');
    const deliveryMethodSelect = document.getElementById('deliveryMethod');
    const deliveryDateInput = document.getElementById('deliveryDate');

    if (!cartItemsContainer || !totalQuantityElement || !totalPriceElement || !deliveryMethodSelect || !deliveryDateInput) {
        console.error('Required elements not found in DOM');
        return;
    }

    const contextPath = document.head.querySelector('base') ? document.head.querySelector('base').href : '/OnlineGroceryOrderSystem';
    console.log('Using context path:', contextPath);

    // Fetch cart items
    fetch(`${contextPath}/CartServlet?action=getCart`, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response.json();
        })
        .then(data => {
            console.log('Fetched cart data:', data);
            if (!data.success) {
                cartItemsContainer.innerHTML = '<p>' + (data.message || 'Error loading cart items.') + '</p>';
                totalQuantityElement.textContent = '0';
                totalPriceElement.textContent = '0.00';
                return;
            }

            const cartItems = data.cart;
            if (cartItems.length === 0) {
                cartItemsContainer.innerHTML = '<p>No items in cart.</p>';
                totalQuantityElement.textContent = '0';
                totalPriceElement.textContent = '0.00';
                return;
            }

            let totalQuantity = 0;
            let totalPrice = 0;

            cartItemsContainer.innerHTML = '';
            cartItems.forEach(item => {
                const cartItem = document.createElement('div');
                cartItem.classList.add('cart-item');
                const itemTotal = (item.productPrice || 0) * (item.quantity || 1);
                const imageSrc = item.productImageLink || 'https://via.placeholder.com/50';
                cartItem.innerHTML = `
                    <img src="${imageSrc}" alt="${item.productName || 'Unknown Item'}" class="cart-item-img">
                    <div class="cart-item-details">
                        <span class="cart-product-title">${item.productName || 'Unknown Item'}</span>
                        <span class="cart-price">RS. ${item.productPrice ? item.productPrice.toFixed(2) : '0.00'} / Product X ${item.quantity || 1}</span>
                        <span class="cart-total">RS. ${itemTotal.toFixed(2)}</span>
                    </div>
                `;
                cartItemsContainer.appendChild(cartItem);

                totalQuantity += item.quantity || 1;
                totalPrice += itemTotal;
            });

            totalQuantityElement.textContent = totalQuantity;
            totalPriceElement.textContent = totalPrice.toFixed(2);
        })
        .catch(error => {
            console.error('Error fetching cart:', error);
            cartItemsContainer.innerHTML = '<p>Error loading cart items.</p>';
            totalQuantityElement.textContent = '0';
            totalPriceElement.textContent = '0.00';
        });

    // Toggle delivery date input based on delivery method
    deliveryMethodSelect.addEventListener('change', () => {
        if (deliveryMethodSelect.value === 'scheduled' || deliveryMethodSelect.value === 'store pickup') {
            deliveryDateInput.style.display = 'block';
            deliveryDateInput.setAttribute('required', 'required');
        } else {
            deliveryDateInput.style.display = 'none';
            deliveryDateInput.removeAttribute('required');
            deliveryDateInput.value = '';
        }
    });

    // Form validation
    const checkoutForm = document.getElementById('checkout-form');
    if (checkoutForm) {
        let isSubmitting = false;

        checkoutForm.addEventListener('submit', (e) => {
            if (isSubmitting) {
                e.preventDefault();
                return;
            }

            const fullName = document.querySelector('input[name="fullName"]').value;
            const phoneNumber = document.querySelector('input[name="phoneNumber"]').value;
            const address = document.querySelector('input[name="address"]').value;
            const deliveryMethod = deliveryMethodSelect.value;
            const paymentMethod = document.querySelector('select[name="paymentMethod"]').value;
            const deliveryDate = deliveryDateInput.value;

            if (!fullName || !phoneNumber || !address || !deliveryMethod || !paymentMethod) {
                alert('Please fill in all fields.');
                e.preventDefault();
                return;
            }

            if ((deliveryMethod === 'scheduled' || deliveryMethod === 'store pickup') && !deliveryDate) {
                alert('Please select a delivery or pickup date.');
                e.preventDefault();
                return;
            }

            isSubmitting = true;
        });
    }
});