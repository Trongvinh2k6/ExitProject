import api from './api';

export const cartService = {
  // POST /users/{userId}/cart/items
  addToCart: async (userId, productId, quantity) => {
    const payload = {
      productId: Number(productId),
      quantity: Number(quantity)
    };
    const response = await api.post(
      `/users/${userId}/cart/items`,
      payload
    );
    return response.data;
  },

  // GET /users/{userId}/cart
  getCart: async (userId) => {
    const response = await api.get(`/users/${userId}/cart`);
    return response.data?.data || response.data;
  },

  // DELETE /users/{userId}/delete/cart/items/{cartItemId}
  deleteCartItem: async (userId, cartItemId) => {
    const response = await api.delete(`/users/${userId}/delete/cart/items/${cartItemId}`);
    return response.data;
  },

  // Update Cart Item Quantity
  updateCartItem: async (userId, productId, quantity) => {
    const payload = {
      productId: Number(productId),
      quantity: Number(quantity)
    };
    const response = await api.post(
      `/users/${userId}/cart/items`,
      payload
    );
    return response.data;
  }
};