import api from "./api";

export const getProducts = async (page = 1, size = 6, brand = "", category = "") => {
    const params = { page, size };

    // Chỉ thêm params nếu có giá trị
    if (brand) params.brand = brand;
    if (category) params.category = category;

    const response = await api.get("/products", { params });
    return response.data;
};

export const getProductById = async (id) => {
    const response = await api.get(`/products/${id}`);
    return response.data;
};

// SỬA Ở ĐÂY: Dùng instance `api` để gọi endpoint `/products/name`
export const getProductsByName = async (name) => {
    const response = await api.get(`/products/name`, {
        params: { name }
    });
    return response.data;
};