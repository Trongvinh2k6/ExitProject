import api from "./api";

export const getProducts = async (page = 1, size = 6) => {

    const response = await api.get("/products", {
        params: {
            page,
            size
        }
    });

    return response.data;
};

export const getProductById = async (id) => {
    const response = await api.get(`/products/${id}`);

    return response.data;
};
