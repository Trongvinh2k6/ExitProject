import { Link } from "react-router-dom";

// Ảnh SVG dự phòng dạng Data URI (không cần kết nối mạng)
const DEFAULT_IMAGE = `data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="300" height="250" viewBox="0 0 300 250" fill="%23e2e8f0"><rect width="300" height="250"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="sans-serif" font-size="16" fill="%2394a3b8">No Image Available</text></svg>`;

function ProductCard({ product }) {
    // Tự động chuẩn hóa đường dẫn ảnh từ CSDL
    const getImageUrl = (path) => {
        if (!path) return DEFAULT_IMAGE;
        if (path.startsWith("http://") || path.startsWith("https://")) return path;
        return path.startsWith("/") ? path : `/${path}`;
    };

    return (
        <div className="product-card">
            <div className="product-image">
                <img
                    src={getImageUrl(product?.image)}
                    alt={product?.name || "Product"}
                    onError={(e) => {
                        e.target.onerror = null; // Tránh lặp vô tận
                        e.target.src = DEFAULT_IMAGE;
                    }}
                />
            </div>

            <div className="product-info">
                <p className="product-brand">{product?.brand?.name}</p>
                <h3>{product?.name}</h3>
                <p className="product-description">{product?.description}</p>
                <div className="product-bottom">
                    <span className="product-price">
                        {product?.price?.toLocaleString("vi-VN")} ₫
                    </span>
                    <Link to={`/products/${product?.id}`} className="view-btn">
                        View
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default ProductCard;