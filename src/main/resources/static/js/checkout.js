/**
 * Cashfree Checkout Integration
 * Handles payment processing with Cashfree
 */

document.addEventListener('DOMContentLoaded', function () {
    const checkoutButton = document.getElementById('checkout-button');
    
    if (checkoutButton) {
        checkoutButton.addEventListener('click', function () {
            initiateCashfreeCheckout();
        });
    }
});

function initiateCashfreeCheckout() {
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
        currency: 'INR',
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
        // Initialize Cashfree payment
        const options = {
            sessionId: data.sessionId,
            returnUrl: "http://localhost:8085/orders/success?order_id=" + data.orderId,
            onSuccess: function(data) {
                console.log('Payment successful:', data);
                handlePaymentSuccess(data);
            },
            onFailure: function(data) {
                console.log('Payment failed:', data);
                alert('Payment failed. Please try again.');
            },
            onClose: function() {
                console.log('Payment window closed');
            }
        };
        
        // Load Cashfree SDK and initialize payment
        loadCashfreeSDK().then(() => {
            const cashfree = new Cashfree(options);
            cashfree.initialisePayment();
        });
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Payment initialization failed. Please try again.');
    });
}

function loadCashfreeSDK() {
    return new Promise((resolve, reject) => {
        if (window.Cashfree) {
            resolve();
            return;
        }
        
        const script = document.createElement('script');
        script.src = 'https://sdk.cashfree.com/js/ui/2.0.0/cashfree.prod.js';
        script.onload = resolve;
        script.onerror = reject;
        document.head.appendChild(script);
    });
}

function handlePaymentSuccess(paymentData) {
    // Verify payment with backend
    fetch('/api/payments/verify', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            orderId: paymentData.orderId,
            paymentId: paymentData.paymentId,
            signature: paymentData.signature
        })
    })
    .then(response => response.json())
    .then(result => {
        if (result.verified) {
            // Clear cart
            clearCart();
            
            // Show success message
            showSuccessMessage('Payment successful! Your order has been placed.');
            
            // Redirect to orders page after a delay
            setTimeout(() => {
                window.location.href = '/orders';
            }, 3000);
        } else {
            alert('Payment verification failed. Please contact support.');
        }
    })
    .catch(error => {
        console.error('Payment verification error:', error);
        alert('Payment verification failed. Please contact support.');
    });
}

function getCartItems() {
    // This function should return cart items in the format expected by the backend
    // Implementation depends on your cart structure
    const cartItems = [];
    // Add logic to get cart items from localStorage or DOM
    return cartItems;
}

function calculateTotal() {
    const totalElement = document.getElementById('total-amount');
    if (totalElement) {
        return parseFloat(totalElement.textContent.replace('₹', '')) || 0;
    }
    return 0;
}

function clearCart() {
    // Clear cart from localStorage
    localStorage.removeItem('cart');
    // Update cart display if needed
    updateCartDisplay();
}

function updateCartDisplay() {
    // Update cart display logic
    // Implementation depends on your cart display structure
}

function showSuccessMessage(message) {
    // Show success message to user
    alert(message);
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

// Check if we're on success page
if (window.location.pathname.includes('/orders/success')) {
    handlePaymentSuccess();
} 