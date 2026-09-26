import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

function ProductDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [quantity, setQuantity] = useState(1);
    const [message, setMessage] = useState("");

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const response = await axios.get(`http://localhost:8090/products/${id}`);
                const data = response.data?.data || response.data;
                setProduct(data);
            } catch (error) {
                console.error("Lỗi khi tải chi tiết sản phẩm:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchProduct();
    }, [id]);

    const handleAddToCart = () => {
        setMessage(`Đã thêm ${quantity} sản phẩm vào giỏ hàng!`);
        setTimeout(() => setMessage(""), 3000);
    };

    if (loading) {
        return (
            <div style={{ textAlign: "center", padding: "60px 0", fontSize: "16px", color: "#666" }}>
                Đang tải thông tin sản phẩm...
            </div>
        );
    }

    if (!product) {
        return (
            <div style={{ textAlign: "center", padding: "60px 0" }}>
                <h2>Không tìm thấy sản phẩm!</h2>
                <button 
                    onClick={() => navigate("/products")}
                    style={{ marginTop: "12px", padding: "8px 16px", cursor: "pointer" }}
                >
                    Quay lại danh sách sản phẩm
                </button>
            </div>
        );
    }

    // Giá trị fallback hiển thị dữ liệu
    const imageUrl = product.imageUrl || product.image || "https://via.placeholder.com/500x500?text=Product+Image";
    const formattedPrice = product.price 
        ? new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(product.price)
        : "0 ₫";

    // Xử lý lấy tên danh mục an toàn kể cả khi category là Object
    const categoryName = typeof product.category === 'object' && product.category !== null
        ? product.category.name 
        : (product.category || product.brand || "Skateboard");

    return (
        <div style={{ maxWidth: "1100px", margin: "40px auto", padding: "0 20px", fontFamily: "sans-serif" }}>
            {/* Nút Quay lại */}
            <button
                onClick={() => navigate(-1)}
                style={{
                    background: "none",
                    border: "none",
                    fontSize: "14px",
                    fontWeight: "600",
                    cursor: "pointer",
                    color: "#555",
                    marginBottom: "24px",
                    display: "inline-flex",
                    alignItems: "center",
                    gap: "6px"
                }}
            >
                ← Quay lại
            </button>

            {/* Thông báo thêm giỏ hàng */}
            {message && (
                <div style={{
                    padding: "12px 20px",
                    backgroundColor: "#d4edda",
                    color: "#155724",
                    borderRadius: "8px",
                    marginBottom: "20px",
                    fontWeight: "500"
                }}>
                    {message}
                </div>
            )}

            {/* Khối giao diện chính (Grid 2 cột) */}
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "40px", alignItems: "start" }}>
                
                {/* Cột trái: Hình ảnh */}
                <div style={{
                    backgroundColor: "#f8fafc",
                    borderRadius: "16px",
                    padding: "20px",
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    border: "1px solid #e2e8f0",
                    minHeight: "400px"
                }}>
                    <img
                        src={imageUrl}
                        alt={product.name || "Product"}
                        style={{
                            maxWidth: "100%",
                            maxHeight: "450px",
                            objectFit: "contain",
                            borderRadius: "8px"
                        }}
                    />
                </div>

                {/* Cột phải: Thông tin chi tiết */}
                <div>
                    {/* Danh mục / Thương hiệu */}
                    <div style={{ display: "flex", gap: "8px", marginBottom: "12px" }}>
                        <span style={{
                            fontSize: "12px",
                            fontWeight: "700",
                            textTransform: "uppercase",
                            backgroundColor: "#f1f5f9",
                            padding: "4px 10px",
                            borderRadius: "20px",
                            color: "#475569"
                        }}>
                            {categoryName}
                        </span>
                    </div>

                    {/* Tên sản phẩm */}
                    <h1 style={{ fontSize: "28px", fontWeight: "700", color: "#0f172a", margin: "0 0 12px 0" }}>
                        {product.name}
                    </h1>

                    {/* Giá tiền */}
                    <div style={{ fontSize: "24px", fontWeight: "700", color: "#2563eb", marginBottom: "16px" }}>
                        {formattedPrice}
                    </div>

                    {/* Mô tả sản phẩm */}
                    <p style={{ color: "#64748b", lineHeight: "1.6", fontSize: "15px", marginBottom: "24px" }}>
                        {product.description || "Chưa có mô tả cho sản phẩm này."}
                    </p>

                    <hr style={{ border: "none", borderTop: "1px solid #e2e8f0", margin: "24px 0" }} />

                    {/* Trạng thái tồn kho */}
                    <div style={{ marginBottom: "20px", fontSize: "14px", color: "#334155" }}>
                        <strong>Tình trạng: </strong>
                        <span style={{ color: (product.stock ?? 20) > 0 ? "#16a34a" : "#dc2626", fontWeight: "600" }}>
                            {(product.stock ?? 20) > 0 ? `Còn hàng (${product.stock ?? 20} sản phẩm)` : "Hết hàng"}
                        </span>
                    </div>

                    {/* Bộ chọn số lượng */}
                    <div style={{ display: "flex", alignItems: "center", gap: "12px", marginBottom: "24px" }}>
                        <label style={{ fontWeight: "600", fontSize: "14px", color: "#334155" }}>Số lượng:</label>
                        <div style={{ display: "flex", alignItems: "center", border: "1px solid #cbd5e1", borderRadius: "6px" }}>
                            <button
                                onClick={() => setQuantity(prev => Math.max(1, prev - 1))}
                                style={{ padding: "6px 14px", background: "none", border: "none", cursor: "pointer", fontSize: "16px" }}
                            >
                                -
                            </button>
                            <span style={{ padding: "6px 12px", fontWeight: "600", fontSize: "14px" }}>{quantity}</span>
                            <button
                                onClick={() => setQuantity(prev => prev + 1)}
                                style={{ padding: "6px 14px", background: "none", border: "none", cursor: "pointer", fontSize: "16px" }}
                            >
                                +
                            </button>
                        </div>
                    </div>

                    {/* Nút Thêm vào giỏ hàng */}
                    <button
                        onClick={handleAddToCart}
                        style={{
                            width: "100%",
                            padding: "14px 24px",
                            backgroundColor: "#000",
                            color: "#fff",
                            border: "none",
                            borderRadius: "8px",
                            fontSize: "16px",
                            fontWeight: "600",
                            cursor: "pointer",
                            transition: "background-color 0.2s"
                        }}
                    >
                        Thêm Vào Giỏ Hàng
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ProductDetail;