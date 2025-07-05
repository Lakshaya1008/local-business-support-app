/**
 * Stripe Checkout Integration
 * Handles payment processing with Stripe
 */

document.addEventListener('DOMContentLoaded', function () {
    const checkoutButton = document.getElementById('checkout-button');
    
    if (checkoutButton) {
        checkoutButton.addEventListener('click', function () {
            initiateStripeCheckout();
        });
    }
});

function initiateStripeCheckout() {
    // Get cart items and total
    const cartItems = getCartItems();
    const total = calculateTotal();
    
    if (total <= 0) {
        alert('Cart is empty!');
        return;
    }
    
    const deliveryAddress = document.getElementById('deliveryAddress')?.value || '';
    
    const request = {
        amount: total,
        currency: 'inr',
        items: cartItems,
        deliveryAddress: deliveryAddress
    };
    
    fetch('/api/payments/create-checkout-session', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(request)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        return Stripe(data.publishableKey).redirectToCheckout({
            sessionId: data.sessionId
        });
    })
    .then(result => {
        if (result.error) {
            alert('Payment error: ' + result.error.message);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Payment initialization failed. Please try again.');
    });
}

function getCartItems() {
    // This should be implemented based on your cart structure
    // For now, returning empty array - implement based on your cart
    return [];
}

function calculateTotal() {
    const totalElement = document.getElementById('total-amount');
    if (totalElement) {
        return parseFloat(totalElement.textContent.replace('₹', '')) || 0;
    }
    return 0;
}

// Handle success page
function handlePaymentSuccess() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('order_id');
    
    if (orderId) {
        // Clear cart
        clearCart();
        
        // Show success message
        showSuccessMessage('Payment successful! Your order has been placed.');
        
        // Redirect to orders page after a delay
        setTimeout(() => {
            window.location.href = '/orders';
        }, 3000);
    }
}

function clearCart() {
    // Implement cart clearing logic
    localStorage.removeItem('cart');
    updateCartDisplay();
}

function updateCartDisplay() {
    // Implement cart display update logic
    const cartCount = document.getElementById('cart-count');
    if (cartCount) {
        cartCount.textContent = '0';
    }
}

function showSuccessMessage(message) {
    // Create and show success message
    const successDiv = document.createElement('div');
    successDiv.className = 'alert alert-success';
    successDiv.textContent = message;
    
    const container = document.querySelector('.container') || document.body;
    container.insertBefore(successDiv, container.firstChild);
}

// Check if we're on success page
if (window.location.pathname.includes('/orders/success')) {
    handlePaymentSuccess();
} 