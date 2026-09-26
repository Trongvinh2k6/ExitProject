import { useEffect, useState } from "react";
import ProductCard from "../components/ProductCard";
import { getProducts, getProductsByName } from "../services/productService"; // Thêm API getProductsByName nếu có

function Products() {
    const [products, setProducts] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // State cho bộ lọc & tìm kiếm
    const [selectedBrand, setSelectedBrand] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("");
    const [searchTerm, setSearchTerm] = useState(""); // 1. Thêm State lưu từ khóa search

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                setLoading(true);
                setError(""); // Reset lỗi cũ

                if (searchTerm.trim() !== "") {
                    const data = await getProductsByName(searchTerm);
                    const result = data.data || [];
                    setProducts(Array.isArray(result) ? result : [result]);
                    setTotalPages(1);
                } else {
                    const data = await getProducts(page, 6, selectedBrand, selectedCategory);
                    setProducts(data.data.content);
                    setTotalPages(data.data.totalPages);
                }
            } catch (error) {
                console.error(error);
                
                // Xử lý riêng: Nếu backend trả về 404 Not Found khi search -> Coi như không tìm thấy SP
                if (error.response && error.response.status === 404) {
                    setProducts([]);
                    setError(""); // Bỏ thông báo lỗi màu đỏ
                } else {
                    // Các lỗi kết nối/server khác mới hiện thông báo đỏ
                    setProducts([]);
                    setError("Không thể tải danh sách sản phẩm");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, [page, selectedBrand, selectedCategory, searchTerm]);

    // Hàm xử lý khi người dùng gõ vào ô Search
    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
        setPage(1); // Reset về trang 1 khi tìm kiếm
    };

    // Hàm xử lý chọn/bỏ chọn Brand
    const handleBrandChange = (brand) => {
        setSelectedBrand(prev => prev === brand ? "" : brand);
        setPage(1);
    };

    // Hàm xử lý chọn/bỏ chọn Category
    const handleCategoryChange = (category) => {
        setSelectedCategory(prev => prev === category ? "" : category);
        setPage(1);
    };

    if (loading) return <div className="loading">Loading products...</div>;

    return (
        <div className="products-page">
            {/* HEADER CONTAINING SEARCH BAR IN THE MIDDLE */}
            <div className="products-header" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div>
                    <p className="small-title">OUR COLLECTION</p>
                    <h1>All Shoes</h1>
                </div>

                {/* 4. Ô TÌM KIẾM ĐẶT BÊN TRONG HEADER (VỊ TRÍ KHOANH TRÒN) */}
                <div className="search-box" style={{ flex: 1, maxWidth: '400px', margin: '0 20px' }}>
                    <input
                        type="text"
                        placeholder="Search products by name..."
                        value={searchTerm}
                        onChange={handleSearchChange}
                        style={{
                            width: '100%',
                            padding: '10px 16px',
                            borderRadius: '20px',
                            border: '1px solid #ccc',
                            outline: 'none',
                            fontSize: '14px'
                        }}
                    />
                </div>

                <div className="product-count">{products.length} products</div>
            </div>

            {error && <div className="error">{error}</div>}

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
                    {products.length > 0 ? (
                        products.map(product => (
                            <ProductCard key={product.id} product={product} />
                        ))
                    ) : (
                        <p style={{ gridColumn: '1 / -1', textAlign: 'center', color: '#666' }}>
                            No products found matching your search.
                        </p>
                    )}
                </main>
            </div>

            {/* PAGINATION - Ẩn khi đang tìm kiếm bằng tên */}
            {searchTerm.trim() === "" && (
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
            )}
        </div>
    );
}

export default Products;