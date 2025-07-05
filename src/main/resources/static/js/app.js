/**
 * Local Business Support - Main JavaScript File
 * Interview Note: Modular JavaScript with Razorpay integration and cart management
 */

// Global variables
let cart = JSON.parse(localStorage.getItem('cart')) || [];
let currentUser = null;

// Initialize application
$(document).ready(function() {
    initializeApp();
    setupEventListeners();
    updateCartDisplay();
});

/**
 * Initialize the application
 */
function initializeApp() {
    console.log('Local Business Support App initialized');
    
    // Load user data from session
    loadUserData();
    
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Setup event listeners
 */
function setupEventListeners() {
    // Add to cart functionality
    $(document).on('click', '.add-to-cart', function(e) {
        e.preventDefault();
        const productId = $(this).data('product-id');
        const productName = $(this).data('product-name');
        const productPrice = $(this).data('product-price');
        addToCart(productId, productName, productPrice);
    });
    
    // Remove from cart
    $(document).on('click', '.remove-from-cart', function(e) {
        e.preventDefault();
        const productId = $(this).data('product-id');
        removeFromCart(productId);
    });
    
    // Checkout functionality
    $(document).on('click', '#checkout-btn', function(e) {
        e.preventDefault();
        if (cart.length === 0) {
            showAlert('Your cart is empty!', 'warning');
            return;
        }
        initiateCheckout();
    });
    
    // Quantity change
    $(document).on('change', '.cart-quantity', function() {
        const productId = $(this).data('product-id');
        const quantity = parseInt($(this).val());
        updateCartQuantity(productId, quantity);
    });
}

/**
 * Cart Management Functions
 */
function addToCart(productId, productName, productPrice) {
    const existingItem = cart.find(item => item.id === productId);
    
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        cart.push({
            id: productId,
            name: productName,
            price: productPrice,
            quantity: 1
        });
    }
    
    saveCart();
    updateCartDisplay();
    showAlert(`${productName} added to cart!`, 'success');
}

function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    saveCart();
    updateCartDisplay();
    showAlert('Item removed from cart!', 'info');
}

function updateCartQuantity(productId, quantity) {
    const item = cart.find(item => item.id === productId);
    if (item) {
        if (quantity <= 0) {
            removeFromCart(productId);
        } else {
            item.quantity = quantity;
            saveCart();
            updateCartDisplay();
        }
    }
}

function saveCart() {
    localStorage.setItem('cart', JSON.stringify(cart));
}

function getCartTotal() {
    return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
}

function updateCartDisplay() {
    const cartCount = cart.reduce((total, item) => total + item.quantity, 0);
    const cartTotal = getCartTotal();
    
    // Update cart badge
    $('.cart-badge').text(cartCount);
    
    // Update cart modal if open
    if ($('#cartModal').length) {
        updateCartModal();
    }
}

function updateCartModal() {
    const cartContainer = $('#cartItems');
    cartContainer.empty();
    
    if (cart.length === 0) {
        cartContainer.html('<p class="text-muted">Your cart is empty</p>');
        return;
    }
    
    cart.forEach(item => {
        const itemHtml = `
            <div class="cart-item d-flex justify-content-between align-items-center mb-2">
                <div>
                    <h6 class="mb-0">${item.name}</h6>
                    <small class="text-muted">₹${item.price} x ${item.quantity}</small>
                </div>
                <div class="d-flex align-items-center">
                    <input type="number" class="form-control form-control-sm cart-quantity me-2" 
                           style="width: 60px;" value="${item.quantity}" 
                           data-product-id="${item.id}" min="1">
                    <button class="btn btn-sm btn-outline-danger remove-from-cart" 
                            data-product-id="${item.id}">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `;
        cartContainer.append(itemHtml);
    });
    
    $('#cartTotal').text(`₹${getCartTotal()}`);
}

/**
 * Checkout and Payment Functions
 */
function initiateCheckout() {
    if (!currentUser) {
        showAlert('Please login to checkout!', 'warning');
        return;
    }
    
    const orderData = {
        items: cart,
        total: getCartTotal(),
        userId: currentUser.id
    };
    
    // Create order via API
    $.ajax({
        url: '/api/orders',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(orderData),
        success: function(response) {
            initiateRazorpayPayment(response.orderId, response.total);
        },
        error: function(xhr) {
            showAlert('Failed to create order. Please try again.', 'error');
        }
    });
}

function initiateRazorpayPayment(orderId, amount) {
    const options = {
        key: 'rzp_test_your_test_key_id', // Replace with actual key
        amount: amount * 100, // Razorpay expects amount in paise
        currency: 'INR',
        name: 'Local Business Support',
        description: 'Order Payment',
        order_id: orderId,
        handler: function(response) {
            // Handle successful payment
            handlePaymentSuccess(response, orderId);
        },
        prefill: {
            name: currentUser.name,
            email: currentUser.email
        },
        theme: {
            color: '#007bff'
        }
    };
    
    const rzp = new Razorpay(options);
    rzp.open();
}

function handlePaymentSuccess(response, orderId) {
    // Verify payment with backend
    $.ajax({
        url: '/api/payments/verify',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            orderId: orderId,
            paymentId: response.razorpay_payment_id,
            signature: response.razorpay_signature
        }),
        success: function(result) {
            if (result.verified) {
                // Clear cart
                cart = [];
                saveCart();
                updateCartDisplay();
                
                showAlert('Payment successful! Your order has been placed.', 'success');
                setTimeout(() => {
                    window.location.href = '/orders';
                }, 2000);
            } else {
                showAlert('Payment verification failed. Please contact support.', 'error');
            }
        },
        error: function() {
            showAlert('Payment verification failed. Please contact support.', 'error');
        }
    });
}

/**
 * Utility Functions
 */
function showAlert(message, type = 'info') {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    // Remove existing alerts
    $('.alert').remove();
    
    // Add new alert
    $('main').prepend(alertHtml);
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        $('.alert').fadeOut();
    }, 5000);
}

function loadUserData() {
    // Load user data from session or API
    $.ajax({
        url: '/api/user/current',
        method: 'GET',
        success: function(user) {
            currentUser = user;
        },
        error: function() {
            // User not logged in
        }
    });
}

/**
 * AJAX Utilities
 */
function apiCall(url, method = 'GET', data = null) {
    return $.ajax({
        url: url,
        method: method,
        contentType: 'application/json',
        data: data ? JSON.stringify(data) : null
    });
}

// Interview Note: This JavaScript file provides modular functionality for cart management,
// Razorpay integration, and AJAX utilities. The code is organized for maintainability
// and follows best practices for e-commerce applications. 