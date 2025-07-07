import api from './api';

export const productService = {
  // Get all products
  getAllProducts: async () => {
    const response = await api.get('/products');
    return response.data;
  },

  // Get product by ID
  getProduct: async (id) => {
    const response = await api.get(`/products/${id}`);
    return response.data;
  },

  // Search products
  searchProducts: async (name, category, minPrice, maxPrice) => {
    const params = {};
    if (name) params.name = name;
    if (category) params.category = category;
    if (minPrice) params.minPrice = minPrice;
    if (maxPrice) params.maxPrice = maxPrice;
    
    const response = await api.get('/products/search', { params });
    return response.data;
  },

  // Get seller products (for sellers only)
  getSellerProducts: async () => {
    const response = await api.get('/products/my');
    return response.data;
  },

  // Add product (for sellers only)
  addProduct: async (productData) => {
    const response = await api.post('/products', productData);
    return response.data;
  },

  // Update product (for sellers only)
  updateProduct: async (id, productData) => {
    const response = await api.put(`/products/${id}`, productData);
    return response.data;
  },

  // Delete product (for sellers only)
  deleteProduct: async (id) => {
    const response = await api.delete(`/products/${id}`);
    return response.data;
  },
}; 