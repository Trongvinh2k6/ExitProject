import { useEffect, useState } from "react";
import ProductCard from "../components/ProductCard";
import { getProducts } from "../services/productService";

function Products() {
    const [products, setProducts] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // State cho bộ lọc
    const [selectedBrand, setSelectedBrand] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("");

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                setLoading(true);
                // Truyền selectedBrand và selectedCategory vào API
                const data = await getProducts(page, 6, selectedBrand, selectedCategory);
                setProducts(data.data.content);
                setTotalPages(data.data.totalPages);
            } catch (error) {
                console.error(error);
                setError("Không thể tải danh sách sản phẩm");
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, [page, selectedBrand, selectedCategory]); // Reset & gọi lại API khi thay đổi trang hoặc lọc

    // Hàm xử lý chọn/bỏ chọn Brand
    const handleBrandChange = (brand) => {
        setSelectedBrand(prev => prev === brand ? "" : brand);
        setPage(1); // Reset về trang 1 khi lọc
    };

    // Hàm xử lý chọn/bỏ chọn Category
    const handleCategoryChange = (category) => {
        setSelectedCategory(prev => prev === category ? "" : category);
        setPage(1); // Reset về trang 1 khi lọc
    };

    if (loading) return <div className="loading">Loading products...</div>;
    if (error) return <div className="error">{error}</div>;

    return (
        <div className="products-page">
            <div className="products-header">
                <div>
                    <p className="small-title">OUR COLLECTION</p>
                    <h1>All Shoes</h1>
                </div>
                <div className="product-count">{products.length} products</div>
            </div>

            <div className="products-container">
                <aside className="filter-sidebar">
                    <h3>Filter</h3>

                    {/* Lọc theo Brand */}
                    <div className="filter-group">
                        <h4>Brand</h4>
                        {["Nike", "Adidas", "Puma"].map((brand) => (
                            <label key={brand}>
                                <input
                                    type="checkbox"
                                    checked={selectedBrand === brand}
                                    onChange={() => handleBrandChange(brand)}
                                />
                                {brand}
                            </label>
                        ))}
                    </div>

                    {/* Lọc theo Category */}
                    <div className="filter-group">
                        <h4>Category</h4>
                        {["Sneaker", "Running", "Basketball"].map((category) => (
                            <label key={category}>
                                <input
                                    type="checkbox"
                                    checked={selectedCategory === category}
                                    onChange={() => handleCategoryChange(category)}
                                />
                                {category}
                            </label>
                        ))}
                    </div>
                </aside>

                <main className="product-grid">
                    {products.map(product => (
                        <ProductCard key={product.id} product={product} />
                    ))}
                </main>
            </div>

            {/* PAGINATION */}
            <div className="pagination">
                <button
                    className="page-btn prev-next"
                    disabled={page === 1}
                    onClick={() => setPage(page - 1)}
                >
                    &laquo; Previous
                </button>

                <div className="page-numbers">
                    {Array.from({ length: totalPages }, (_, index) => index + 1).map(number => (
                        <button
                            key={number}
                            onClick={() => setPage(number)}
                            className={`page-number ${page === number ? "active" : ""}`}
                        >
                            {number}
                        </button>
                    ))}
                </div>

                <button
                    className="page-btn prev-next"
                    disabled={page === totalPages}
                    onClick={() => setPage(page + 1)}
                >
                    Next &raquo;
                </button>
            </div>
        </div>
    );
}

export default Products;