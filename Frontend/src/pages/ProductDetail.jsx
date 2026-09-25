import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getProductById } from "../services/productService";

function ProductDetail() {

    const { id } = useParams();

    const navigate = useNavigate();

    const [product, setProduct] = useState(null);

    const [loading, setLoading] = useState(true);

    const [error, setError] = useState("");


    useEffect(() => {

        const fetchProduct = async () => {

            try {

                setLoading(true);

                const data = await getProductById(id);

                console.log(data);

                setProduct(data.data);

            } catch (error) {

                console.error(error);

                setError("Không thể tải thông tin sản phẩm");

            } finally {

                setLoading(false);

            }

        };

        fetchProduct();

    }, [id]);


    if (loading) {
        return (
            <div className="loading">
                Loading product...
            </div>
        );
    }


    if (error) {
        return (
            <div className="error">
                {error}
            </div>
        );
    }


    if (!product) {
        return (
            <div>
                Không tìm thấy sản phẩm
            </div>
        );
    }


    return (

        <div className="product-detail">

            <button onClick={() => navigate("/products")}>
                ← Back to Products
            </button>


            <div className="product-detail-content">

                <div className="product-detail-image">
                    {/* Sau này thêm hình ảnh ở đây */}
                    <div>
                        Product Image
                    </div>
                </div>


                <div className="product-detail-info">

                    <p className="small-title">
                        {product.brand.name}
                    </p>

                    <h1>
                        {product.name}
                    </h1>

                    <p className="product-price">
                        {product.price.toLocaleString("vi-VN")} ₫
                    </p>

                    <p className="product-description">
                        {product.description}
                    </p>


                    <div className="product-category">

                        <strong>
                            Category:
                        </strong>

                        <span>
                            {product.category.name}
                        </span>

                    </div>


                    <div className="product-quantity">

                        <strong>
                            Stock:
                        </strong>

                        <span>
                            {product.quantity}
                        </span>

                    </div>


                    <button className="add-to-cart">
                        Add to Cart
                    </button>

                </div>

            </div>

        </div>

    );
}

export default ProductDetail;
