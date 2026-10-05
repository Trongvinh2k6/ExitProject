import api from './api'; // Hoặc dùng axios trực tiếp nếu chưa cấu hình baseURL

export const sendChatMessage = async (message) => {
    // Giả sử api.js đã set baseURL = http://localhost:8090
    // Nếu chưa, dùng axios.post('http://localhost:8090/chat', { message })
    const response = await api.post('/chat', { message });
    return response.data; // Trả về ChatResponseDTO { reply, products }
};