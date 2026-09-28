import api from './api';

export const cartService = {
  // POST /users/{userId}/cart/items
  addToCart: async (userId, productId, quantity) => {
    const payload = {
        productId: Number(productId),
        quantity: Number(quantity)
    };
    console.log("payload:", payload);
    const response = await api.post(
        `/users/${userId}/cart/items`,
        payload
    );

    return response.data;
  },

  // GET /users/{userId}/cart
  getCart: async (userId) => {
    const response = await api.get(`/users/${userId}/cart`);
    return response.data;
  },

  // DELETE /users/{userId}/delete/cart/items/{cartItemId}
  deleteCartItem: async (userId, cartItemId) => {
    const response = await api.delete(`/users/${userId}/delete/cart/items/${cartItemId}`);
    return response.data;
  }
};