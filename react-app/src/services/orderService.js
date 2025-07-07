import api from './api';

export const orderService = {
  // Create new order
  createOrder: async (orderData) => {
    const response = await api.post('/orders', orderData);
    return response.data;
  },

  // Get customer orders
  getCustomerOrders: async () => {
    const response = await api.get('/orders/customer');
    return response.data;
  },

  // Get seller orders
  getSellerOrders: async () => {
    const response = await api.get('/orders/seller');
    return response.data;
  },

  // Update order status
  updateOrderStatus: async (orderId, status) => {
    const response = await api.put(`/orders/${orderId}/status`, { status });
    return response.data;
  },

  // Get order by ID
  getOrder: async (orderId) => {
    const response = await api.get(`/orders/${orderId}`);
    return response.data;
  },
}; 