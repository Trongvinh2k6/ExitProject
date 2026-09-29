import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { cartService } from '../services/cartService';

// Hàm chuẩn hóa đường dẫn lấy thẳng từ thư mục public/images
const getProductImageUrl = (product) => {
  if (!product) return '';
  
  // Lấy giá trị chuỗi ảnh từ bất kỳ thuộc tính nào backend trả về
  const rawImage = product.image || product.imageUrl || product.images?.[0] || '';
  
  if (!rawImage) return '';

  // 1. Nếu là URL đầy đủ (http://...)
  if (typeof rawImage === 'string' && (rawImage.startsWith('http://') || rawImage.startsWith('https://'))) {
    return rawImage;
  }

  // 2. Nếu đã có sẵn /images/ ở đầu
  if (typeof rawImage === 'string' && rawImage.startsWith('/images/')) {
    return rawImage;
  }

  // 3. Nếu chỉ có tên file (vd: "nike-air-max-90.jpg" hoặc "nike-pegasus-41.jpg")
  const fileName = typeof rawImage === 'object' ? (rawImage.url || rawImage.name || '') : rawImage;
  const cleanFileName = fileName.replace(/^\/?(images\/)?/, '');

  return `/images/${cleanFileName}`;
};

// Component hiển thị ảnh sản phẩm
function CartProductImage({ product, itemId }) {
  const [imgSrc, setImgSrc] = useState(() => getProductImageUrl(product));

  useEffect(() => {
    // Nếu thông tin product ban đầu đã có ảnh thì dùng luôn
    const currentUrl = getProductImageUrl(product);
    if (currentUrl) {
      setImgSrc(currentUrl);
      return;
    }

    // Nếu chưa có, gọi API lấy thông tin chi tiết
    const targetId = product?.id || itemId;
    if (targetId) {
      axios.get(`http://localhost:8090/products/${targetId}`)
        .then((response) => {
          const data = response.data?.data || response.data;
          setImgSrc(getProductImageUrl(data));
        })
        .catch((err) => console.error(`Lỗi tải ảnh cho ID ${targetId}:`, err));
    }
  }, [product, itemId]);

  return (
    <img
      src={imgSrc}
      alt={product?.name || "Product"}
      className="w-full h-full object-contain p-1"
    />
  );
}

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [updatingItemId, setUpdatingItemId] = useState(null);
  const navigate = useNavigate();

  const userId = localStorage.getItem('userId');

  const fetchCart = async () => {
    if (!userId) {
      navigate('/login');
      return;
    }
    try {
      setLoading(true);
      const data = await cartService.getCart(userId);
      setCart(data);
    } catch (error) {
      console.error("Lỗi khi tải giỏ hàng:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  const handleUpdateQuantity = async (cartItemId, productId, newQuantity) => {
    if (newQuantity < 1) return;
    try {
      setUpdatingItemId(cartItemId);
      await cartService.updateCartItem(userId, productId, newQuantity);
      
      setCart((prev) => ({
        ...prev,
        items: prev.items.map((item) => {
          if (item.id === cartItemId) {
            const unitPrice = item.productResponseDTO?.price || 0;
            return {
              ...item,
              quantity: newQuantity,
              price: unitPrice * newQuantity,
            };
          }
          return item;
        }),
      }));
    } catch (error) {
      console.error("Cập nhật số lượng thất bại:", error);
    } finally {
      setUpdatingItemId(null);
    }
  };

  const handleDeleteItem = async (cartItemId) => {
    try {
      await cartService.deleteCartItem(userId, cartItemId);
      setCart((prev) => ({
        ...prev,
        items: prev.items.filter((item) => item.id !== cartItemId),
      }));
    } catch (error) {
      console.error("Xóa sản phẩm thất bại:", error);
    }
  };

  const calculateTotal = () => {
    if (!cart || !cart.items) return 0;
    return cart.items.reduce((sum, item) => sum + (item.price || 0), 0);
  };

  if (loading) {
    return <div className="text-center py-20 font-medium">Đang tải giỏ hàng...</div>;
  }

  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="max-w-4xl mx-auto py-16 text-center">
        <h2 className="text-2xl font-bold mb-4">Giỏ hàng của bạn đang trống</h2>
        <p className="text-gray-500 mb-6">Hãy chọn thêm vài sản phẩm để mua sắm nhé!</p>
        <Link
          to="/products"
          className="inline-block bg-black text-white px-6 py-3 rounded-md font-medium hover:bg-gray-800 transition"
        >
          Khám phá sản phẩm
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-8">Giỏ Hàng Của Bạn ({cart.items.length})</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-4">
          {cart.items.map((item) => {
            const product = item.productResponseDTO || {};
            const isUpdating = updatingItemId === item.id;
            const targetProductId = product.id || item.productId;

            return (
              <div
                key={item.id}
                className="flex items-center justify-between border-b pb-4 gap-4"
              >
                <div className="flex items-center gap-4 flex-1">
                  <div className="w-20 h-20 bg-gray-100 rounded-md flex-shrink-0 overflow-hidden flex items-center justify-center">
                    <CartProductImage product={product} itemId={targetProductId} />
                  </div>

                  <div>
                    <h3 className="font-bold text-gray-900 line-clamp-1">
                      {product.name}
                    </h3>
                    <p className="text-sm text-gray-500">
                      Thương hiệu: {product.brand?.name || 'Nike'}
                    </p>
                    <p className="text-sm font-semibold mt-1 text-gray-700">
                      {product.price?.toLocaleString('vi-VN')} ₫
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-6">
                  <div className="flex items-center border rounded-md overflow-hidden bg-white">
                    <button
                      disabled={isUpdating || item.quantity <= 1}
                      onClick={() => handleUpdateQuantity(item.id, targetProductId, item.quantity - 1)}
                      className="px-3 py-1 bg-gray-100 hover:bg-gray-200 disabled:opacity-40 transition"
                    >
                      -
                    </button>
                    <span className="px-3 py-1 text-sm font-bold min-w-[2.5rem] text-center">
                      {item.quantity}
                    </span>
                    <button
                      disabled={isUpdating}
                      onClick={() => handleUpdateQuantity(item.id, targetProductId, item.quantity + 1)}
                      className="px-3 py-1 bg-gray-100 hover:bg-gray-200 disabled:opacity-40 transition"
                    >
                      +
                    </button>
                  </div>

                  <div className="text-right min-w-[100px]">
                    <p className="font-bold text-gray-900">
                      {item.price?.toLocaleString('vi-VN')} ₫
                    </p>
                  </div>

                  <button
                    onClick={() => handleDeleteItem(item.id)}
                    className="text-gray-400 hover:text-red-600 transition p-1"
                    title="Xóa khỏi giỏ hàng"
                  >
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      className="h-5 w-5"
                      fill="none"
                      viewBox="0 0 24 24"
                      stroke="currentColor"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                      />
                    </svg>
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        <div className="bg-gray-50 p-6 rounded-lg h-fit border">
          <h2 className="text-lg font-bold mb-4 border-b pb-2">Tóm tắt đơn hàng</h2>

          <div className="space-y-3 text-sm">
            <div className="flex justify-between text-gray-600">
              <span>Tạm tính</span>
              <span>{calculateTotal().toLocaleString('vi-VN')} ₫</span>
            </div>
            <div className="flex justify-between text-gray-600">
              <span>Phí vận chuyển</span>
              <span>Miễn phí</span>
            </div>
            <div className="border-t pt-3 flex justify-between font-bold text-base text-gray-900">
              <span>Tổng cộng</span>
              <span className="text-blue-600">
                {calculateTotal().toLocaleString('vi-VN')} ₫
              </span>
            </div>
          </div>

          <button
            onClick={() => navigate('/checkout')}
            className="w-full mt-6 bg-black text-white py-3 rounded-md font-bold hover:bg-gray-800 transition"
          >
            Tiến hành thanh toán
          </button>
        </div>
      </div>
    </div>
  );
}