document.addEventListener('DOMContentLoaded', () => {
    const addToCartButtons = document.querySelectorAll('.add-cart, .add-cart-btn');
    const cartIcon = document.getElementById('cart-icon');
    const cart = document.querySelector('.cart');
    const cartClose = document.getElementById('cart-close');
    const dropdownToggle = document.getElementById('dropdown-toggle');
    const dropdownMenu = document.getElementById('dropdown-menu');
    let cartItemCount = 0;
    let totalPrice = 0;

    // Dropdown toggle functionality
    if (dropdownToggle && dropdownMenu) {
        dropdownToggle.addEventListener('click', () => {
            dropdownMenu.classList.toggle('active');
        });

        document.addEventListener('click', (event) => {
            if (!dropdownToggle.contains(event.target) && !dropdownMenu.contains(event.target)) {
                dropdownMenu.classList.remove('active');
            }
        });
    }

    addToCartButtons.forEach(button => {
        button.addEventListener('click', () => {
            if (button.disabled) return;

            const itemId = button.getAttribute('data-item-id');
            fetch(`${window.contextPath}/CartServlet`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `action=add&itemId=${itemId}`
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        updateCartUI(data.cart, data.totalPrice);
                    } else {
                        console.log(data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        });
    });

    if (cartIcon && cart && !window.location.pathname.includes('/cartAndOrders/checkOut.jsp')) {
        cartIcon.addEventListener('click', () => {
            cart.classList.add('active');
        });
    }

    if (cartClose && cart) {
        cartClose.addEventListener('click', () => {
            cart.classList.remove('active');
        });
    }

    function updateCartUI(cart, totalPriceValue) {
        const cartContent = document.querySelector('.cart-content');
        const totalContainer = document.getElementById('total-container');
        const totalPriceElement = document.getElementById('total-price');
        cartContent.innerHTML = '';

        cartItemCount = cart.length;
        totalPrice = parseFloat(totalPriceValue);

        if (cartItemCount === 0) {
            cartContent.innerHTML = '<p>Your cart is empty.</p>';
            totalContainer.style.display = 'none';
        } else {
            cart.forEach(item => {
                const cartBox = document.createElement('div');
                cartBox.classList.add('cart-box');
                const isAtStockLimit = item.quantity >= item.totalAvailableStock;
                cartBox.innerHTML = `
                    <img src="${item.productImageLink}" alt="${item.productName}" class="cart-img">
                    <div class="cart-details">
                        <div class="cart-product-title">${item.productName}</div>
                        <div class="cart-price">Rs. ${parseFloat(item.productPrice).toFixed(2)}</div>
                        <div class="cart-quantity">
                            <button class="decrease-quantity" data-item-id="${item.productID}">-</button>
                            <div class="number">${item.quantity}</div>
                            <button class="increase-quantity" data-item-id="${item.productID}" ${isAtStockLimit ? 'disabled' : ''}>+</button>
                        </div>
                    </div>
                    <i class="fas fa-trash cart-remove" data-item-id="${item.productID}"></i>
                `;
                cartContent.appendChild(cartBox);
            });
            totalContainer.style.display = 'block';
            totalPriceElement.textContent = `Total Rs. ${totalPrice.toFixed(2)}`;

            document.querySelectorAll('.decrease-quantity').forEach(button => {
                button.addEventListener('click', () => updateQuantity(button.getAttribute('data-item-id'), -1));
            });
            document.querySelectorAll('.increase-quantity').forEach(button => {
                button.addEventListener('click', () => updateQuantity(button.getAttribute('data-item-id'), 1));
            });
            document.querySelectorAll('.cart-remove').forEach(button => {
                button.addEventListener('click', () => removeFromCart(button.getAttribute('data-item-id')));
            });
        }

        document.querySelector('.cart-item-count').textContent = cartItemCount;
        document.querySelector('.cart-item-count').style.visibility = cartItemCount > 0 ? 'visible' : 'hidden';
    }

    function updateQuantity(itemId, change) {
        fetch(`${window.contextPath}/CartServlet`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `action=update&itemId=${itemId}&change=${change}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    updateCartUI(data.cart, data.totalPrice);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    function removeFromCart(itemId) {
        fetch(`${window.contextPath}/CartServlet`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `action=remove&itemId=${itemId}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    updateCartUI(data.cart, data.totalPrice);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    // Initial cart load
    fetch(`${window.contextPath}/CartServlet?action=getCart`)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                updateCartUI(data.cart, data.totalPrice);
            } else {
                console.error('Failed to load initial cart:', data.message);
            }
        })
        .catch(error => console.error('Error loading cart:', error));
});