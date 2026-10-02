import api from './api';

export const orderService = {
  // POST /orders/create
  createOrder: async (orderData) => {
    const response = await api.post('/orders/create', orderData);
    return response.data;
  },

  // GET /orders
  getOrders: async () => {
    const response = await api.get('/orders');
    return response.data?.data || response.data;
  },

  // GET /orders/{id}
  getOrderById: async (id) => {
    const response = await api.get(`/orders/${id}`);
    return response.data?.data || response.data;
  },

  // PUT /orders/{id}/cancel
  cancelOrder: async (id) => {
    const response = await api.put(`/orders/${id}/cancel`);
    return response.data;
  }
};