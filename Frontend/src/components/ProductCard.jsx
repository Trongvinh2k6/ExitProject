import { Link } from "react-router-dom";

function ProductCard({ product }) {

    return (
        <div className="product-card">

            <div className="product-image">
                <div className="shoe-placeholder">
                    👟
                </div>
            </div>

            <div className="product-info">

                <p className="product-brand">
                    {product.brand?.name}
                </p>

                <h3>
                    {product.name}
                </h3>

                <p className="product-description">
                    {product.description}
                </p>

                <div className="product-bottom">

                    <span className="product-price">
                        {product.price?.toLocaleString("vi-VN")} ₫
                    </span>

                    <Link
                        to={`/products/${product.id}`}
                        className="view-btn"
                    >
                        View
                    </Link>

                </div>

            </div>

        </div>
    );
}

export default ProductCard;