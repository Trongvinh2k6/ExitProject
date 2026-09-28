import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { cartService } from '../services/cartService';

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  // Lấy userId từ Auth Context hoặc LocalStorage
  const userId = 1; 

  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [adding, setAdding] = useState(false);
  const [message, setMessage] = useState(null);

  const DEFAULT_IMAGE = "https://images.unsplash.com/photo-1542291026-7eec264c27ff";

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`http://localhost:8090/products/${id}`);
        // API trả về dạng ApiResponse { message, data: { ... } }
        const data = response.data?.data || response.data;
        setProduct(data);
      } catch (error) {
        console.error("Lỗi khi tải chi tiết sản phẩm:", error);
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      fetchProduct();
    }
  }, [id]);

  const handleQuantityChange = (type) => {
    if (type === 'decrease' && quantity > 1) {
      setQuantity((prev) => prev - 1);
    } else if (type === 'increase' && quantity < (product?.quantity || 99)) {
      setQuantity((prev) => prev + 1);
    }
  };

  const handleAddToCart = async () => {
    if (!product) return;

    const targetProductId = product.id || id;

    console.log("userId:", userId);
    console.log("productId:", targetProductId);
    console.log("quantity:", quantity);

    try {
      setAdding(true);
      setMessage(null);

      // Gọi cartService thêm vào giỏ hàng
      await cartService.addToCart(userId, targetProductId, quantity);

      setMessage({ type: 'success', text: 'Đã thêm sản phẩm vào giỏ hàng!' });
    } catch (error) {
      console.error('Lỗi thêm sản phẩm:', error);
      console.log("STATUS:", error.response?.status);
      console.log("DATA:", error.response?.data);
      console.log("REQUEST:", error.config?.data);
      setMessage({ type: 'error', text: 'Thêm vào giỏ hàng thất bại. Vui lòng thử lại!' });
    } finally {
      setAdding(false);
    }
  };

  if (loading) {
    return <div className="text-center py-20 font-medium">Đang tải thông tin sản phẩm...</div>;
  }

  if (!product) {
    return (
      <div className="text-center py-20">
        <p className="text-red-500 font-medium mb-4">Không tìm thấy sản phẩm!</p>
        <button onClick={() => navigate('/products')} className="underline">Quay lại danh sách</button>
      </div>
    );
  }

  const imageUrl = product.image || product.imageUrl || DEFAULT_IMAGE;

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <button 
        onClick={() => navigate(-1)} 
        className="text-gray-500 hover:text-black mb-6 inline-flex items-center gap-1 text-sm font-medium"
      >
        ← Quay lại
      </button>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
        <div className="bg-gray-100 rounded-2xl p-8 flex items-center justify-center min-h-[350px]">
          <img 
            src={imageUrl} 
            alt={product.name} 
            className="w-full h-auto object-contain max-h-[400px]"
            onError={(e) => {
              e.target.src = DEFAULT_IMAGE;
            }}
          />
        </div>

        <div className="space-y-6">
          <span className="text-xs font-bold tracking-wider text-gray-400 uppercase">
            {product.category?.name || 'SPORT'}
          </span>

          <h1 className="text-3xl font-bold text-gray-900">{product.name}</h1>

          <p className="text-2xl font-bold text-blue-600">
            {product.price?.toLocaleString('vi-VN')} ₫
          </p>

          <p className="text-gray-500 text-sm leading-relaxed">
            {product.description || 'Chưa có mô tả cho sản phẩm này.'}
          </p>

          <hr className="border-gray-200" />

          {/* Tình trạng kho - Đã sửa chính xác thuộc tính product.quantity */}
          <p className="text-sm">
            Tình trạng:{' '}
            {product.quantity > 0 ? (
              <span className="text-green-600 font-semibold">
                Còn hàng ({product.quantity} sản phẩm)
              </span>
            ) : (
              <span className="text-red-600 font-semibold">
                Hết hàng
              </span>
            )}
          </p>

          <div className="flex items-center gap-4">
            <span className="text-sm font-medium">Số lượng:</span>
            <div className="flex items-center border rounded-md">
              <button
                onClick={() => handleQuantityChange('decrease')}
                className="px-3 py-1 hover:bg-gray-100 text-gray-600 font-medium"
                disabled={quantity <= 1}
              >
                -
              </button>
              <span className="px-4 py-1 font-semibold text-sm">{quantity}</span>
              <button
                onClick={() => handleQuantityChange('increase')}
                className="px-3 py-1 hover:bg-gray-100 text-gray-600 font-medium"
                disabled={quantity >= product.quantity}
              >
                +
              </button>
            </div>
          </div>

          <button
            onClick={handleAddToCart}
            disabled={adding || product.quantity <= 0}
            className="w-full bg-black text-white py-3.5 rounded-lg font-bold hover:bg-gray-800 transition duration-200 disabled:opacity-50"
          >
            {adding ? 'Đang thêm...' : product.quantity <= 0 ? 'Sản phẩm đã hết hàng' : 'Thêm Vào Giỏ Hàng'}
          </button>

          {message && (
            <div
              className={`p-3 rounded-md text-sm text-center ${
                message.type === 'success'
                  ? 'bg-green-50 text-green-700 border border-green-200'
                  : 'bg-red-50 text-red-700 border border-red-200'
              }`}
            >
              {message.text}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}