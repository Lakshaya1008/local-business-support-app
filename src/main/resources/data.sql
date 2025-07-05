-- Users
INSERT INTO users (id, email, password, name, enabled) VALUES
  (1, 'admin@local.com', '$2a$10$7Qw1Qw1Qw1Qw1Qw1Qw1QwOQw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw', 'Admin User', true),
  (2, 'vendor@local.com', '$2a$10$7Qw1Qw1Qw1Qw1Qw1Qw1QwOQw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw', 'Local Vendor', true),
  (3, 'customer@local.com', '$2a$10$7Qw1Qw1Qw1Qw1Qw1Qw1QwOQw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw1Qw', 'John Customer', true);

-- User Roles
INSERT INTO users_roles (user_id, roles) VALUES
  (1, 'ADMIN'),
  (2, 'SELLER'),
  (3, 'USER');

-- Products
INSERT INTO products (id, name, description, price, quantity, category, seller_id, pincode, location, created_at, status) VALUES
  (1, 'Organic Honey', 'Pure local honey from natural beehives', 299.00, 50, 'Grocery', 2, '110001', 'Delhi', NOW(), 'ACTIVE'),
  (2, 'Handmade Soap', 'Natural herbal soap with essential oils', 99.00, 100, 'Personal Care', 2, '110001', 'Delhi', NOW(), 'ACTIVE'),
  (3, 'Millet Cookies', 'Healthy cookies made with organic millet', 149.00, 75, 'Snacks', 2, '110001', 'Delhi', NOW(), 'ACTIVE');

-- Vendor Application
INSERT INTO vendor_applications (id, user_id, business_name, business_description, business_address, pincode, phone_number, gst_number, pan_number, status, application_date) VALUES
  (1, 2, 'Local Organic Store', 'Selling organic and natural products', '123 Main Street, Delhi', '110001', '9876543210', 'GST123456789', 'ABCDE1234F', 'PENDING', NOW());

-- Orders
INSERT INTO orders (id, customer_id, seller_id, total_amount, status, order_date, delivery_address, payment_method, payment_status_enum) VALUES
  (1, 3, 2, 448.00, 'PENDING', NOW(), '456 Customer Street, Delhi', 'RAZORPAY', 'PENDING'),
  (2, 3, 2, 99.00, 'CONFIRMED', NOW(), '456 Customer Street, Delhi', 'RAZORPAY', 'PAID');

-- Order Items
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, total_price) VALUES
  (1, 1, 1, 1, 299.00, 299.00),
  (2, 1, 2, 1, 99.00, 99.00),
  (3, 1, 3, 1, 149.00, 149.00),
  (4, 2, 2, 1, 99.00, 99.00);

-- Reviews
INSERT INTO reviews (id, product_id, user_id, rating, comment, review_date, status) VALUES
  (1, 1, 3, 5, 'Excellent quality honey!', NOW(), 'APPROVED'),
  (2, 2, 3, 4, 'Great natural soap', NOW(), 'PENDING'); 