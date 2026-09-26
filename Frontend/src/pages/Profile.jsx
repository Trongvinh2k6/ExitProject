import React, { useState, useEffect } from "react";
import axios from "axios";

const getUserIdFromToken = () => {
  const token = localStorage.getItem("accessToken");
  if (!token) return null;
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(c => {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));
    const parsed = JSON.parse(jsonPayload);
    return parsed.id || parsed.userId || null;
  } catch (e) {
    return null;
  }
};

// Đã bổ sung onUpdateUser vào props ở dòng dưới
function Profile({ currentUser, onUpdateUser }) {
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        phone: "",
        address: ""
    });

    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState({ type: "", text: "" });

    const savedUser = localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : null;
    const userId = currentUser?.id || localStorage.getItem("userId") || savedUser?.id || getUserIdFromToken();
    const token = localStorage.getItem("accessToken");

    useEffect(() => {
        if (!userId) {
            console.error("Không tìm thấy userId trong LocalStorage hoặc Props!");
            setMessage({ type: "error", text: "Không tìm thấy thông tin đăng nhập. Vui lòng đăng nhập lại." });
            setLoading(false);
            return;
        }

        const fetchUserData = async () => {
            try {
                const response = await axios.get(`http://localhost:8090/users/${userId}`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });

                const userData = response.data?.data || response.data;

                setFormData({
                    name: userData.name || userData.fullname || "",
                    email: userData.email || "",
                    phone: userData.phone || "",
                    address: userData.address || ""
                });
            } catch (error) {
                console.error("Lỗi khi gọi API /users/{id}:", error);
                setMessage({ type: "error", text: "Lỗi tải thông tin người dùng từ máy chủ." });
            } finally {
                setLoading(false);
            }
        };

        fetchUserData();
    }, [userId, token]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage({ type: "", text: "" });

        const updatePayload = {
            id: userId,
            name: formData.name,
            fullname: formData.name, 
            email: formData.email,
            phone: formData.phone,
            address: formData.address
        };

        try {
            const response = await axios.put(
                `http://localhost:8090/users/update/${userId}`, 
                updatePayload,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                }
            );

            if (response.status >= 200 && response.status < 300) {
                setMessage({ type: "success", text: "Cập nhật thông tin thành công!" });

                const rawUser = localStorage.getItem("user");
                const currentUserObj = rawUser ? JSON.parse(rawUser) : {};

                const updatedUser = { 
                    ...currentUserObj, 
                    name: formData.name, 
                    fullname: formData.name,
                    phone: formData.phone, 
                    address: formData.address 
                };

                // 1. Cập nhật localStorage
                localStorage.setItem("user", JSON.stringify(updatedUser));

                // 2. Phát event custom cho các component lắng nghe
                window.dispatchEvent(new Event("storage"));

                // 3. Nếu component cha có truyền handler
                if (typeof onUpdateUser === "function") {
                    onUpdateUser(updatedUser);
                } else {
                    // Fallback reload trang nếu không có callback từ props
                    setTimeout(() => {
                        window.location.reload();
                    }, 400);
                }
            }
        } catch (error) {
            console.error("Lỗi cập nhật người dùng:", error);
            const errorMsg = error.response?.data?.message || "Cập nhật thất bại, vui lòng kiểm tra lại dữ liệu.";
            setMessage({ type: "error", text: errorMsg });
        }
    };

    if (loading) {
        return (
            <div style={{ padding: "40px", textAlign: "center", fontSize: "16px" }}>
                Đang tải thông tin...
            </div>
        );
    }

    return (
        <div style={{ maxWidth: "600px", margin: "40px auto", padding: "24px", border: "1px solid #e2e8f0", borderRadius: "12px", backgroundColor: "#fff" }}>
            <h2 style={{ fontSize: "20px", fontWeight: "bold", marginBottom: "20px" }}>Thông Tin Cá Nhân</h2>

            {message.text && (
                <div style={{
                    padding: "10px 14px",
                    borderRadius: "6px",
                    marginBottom: "16px",
                    color: message.type === "success" ? "#155724" : "#721c24",
                    backgroundColor: message.type === "success" ? "#d4edda" : "#f8d7da"
                }}>
                    {message.text}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: "16px" }}>
                    <label style={{ display: "block", marginBottom: "6px", fontWeight: "500" }}>Họ và tên</label>
                    <input
                        type="text"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                        style={{ width: "100%", padding: "10px", borderRadius: "6px", border: "1px solid #ccc" }}
                    />
                </div>

                <div style={{ marginBottom: "16px" }}>
                    <label style={{ display: "block", marginBottom: "6px", fontWeight: "500" }}>Email (Không thể thay đổi)</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        disabled
                        style={{ width: "100%", padding: "10px", borderRadius: "6px", border: "1px solid #ccc", backgroundColor: "#f1f5f9", cursor: "not-allowed" }}
                    />
                </div>

                <div style={{ marginBottom: "16px" }}>
                    <label style={{ display: "block", marginBottom: "6px", fontWeight: "500" }}>Số điện thoại</label>
                    <input
                        type="text"
                        name="phone"
                        value={formData.phone}
                        onChange={handleChange}
                        style={{ width: "100%", padding: "10px", borderRadius: "6px", border: "1px solid #ccc" }}
                    />
                </div>

                <div style={{ marginBottom: "20px" }}>
                    <label style={{ display: "block", marginBottom: "6px", fontWeight: "500" }}>Địa chỉ</label>
                    <textarea
                        name="address"
                        rows="3"
                        value={formData.address}
                        onChange={handleChange}
                        style={{ width: "100%", padding: "10px", borderRadius: "6px", border: "1px solid #ccc" }}
                    />
                </div>

                <button
                    type="submit"
                    style={{
                        padding: "10px 24px",
                        backgroundColor: "#000",
                        color: "#fff",
                        border: "none",
                        borderRadius: "6px",
                        fontWeight: "600",
                        cursor: "pointer"
                    }}
                >
                    Lưu thay đổi
                </button>
            </form>
        </div>
    );
}

export default Profile;