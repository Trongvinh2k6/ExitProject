import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartService } from '../services/cartService';
import { orderService } from '../services/orderService';

export default function Checkout() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const userId = localStorage.getItem('userId');

  useEffect(() => {
    if (!userId) {
      navigate('/login');
      return;
    }
    const loadCart = async () => {
      try {
        const data = await cartService.getCart(userId);
        setCart(data);
      } catch (err) {
        console.error("Lỗi khi tải giỏ hàng:", err);
      } finally {
        setLoading(false);
      }
    };
    loadCart();
  }, [userId, navigate]);

  const calculateTotal = () => {
    if (!cart?.items) return 0;
    return cart.items.reduce((sum, item) => sum + (item.price || 0), 0);
  };

  const handlePlaceOrder = async (e) => {
    e.preventDefault();
    if (!cart?.items || cart.items.length === 0) return;

    try {
      setSubmitting(true);

      // 1. Chuẩn bị danh sách items đúng định dạng CreateOrderItemDTO
      const itemsPayload = cart.items.map((item) => {
        const productId = item.productResponseDTO?.id || item.productId;
        return {
          product_Id: Number(productId),
          quantity: Number(item.quantity)
        };
      });

      // 2. Gọi API tạo đơn hàng
      await orderService.createOrder(itemsPayload);

      // 3. Xóa các item trong giỏ hàng sau khi tạo đơn thành công
      for (const item of cart.items) {
        await cartService.deleteCartItem(userId, item.id);
      }

      // 4. Chuyển hướng sang trang danh sách đơn hàng
      navigate('/orders');
    } catch (error) {
      console.error("Đặt hàng thất bại:", error);
      alert("Đặt hàng không thành công. Vui lòng thử lại!");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="text-center py-20">Đang tải thông tin thanh toán...</div>;

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Thanh Toán Đơn Hàng</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Thông tin đơn hàng */}
        <div className="bg-white p-6 border rounded-lg shadow-sm">
          <h2 className="text-lg font-bold mb-4 border-b pb-2">Sản phẩm trong đơn</h2>
          <div className="space-y-4 max-h-80 overflow-y-auto pr-2">
            {cart?.items?.map((item) => {
              const product = item.productResponseDTO || {};
              return (
                <div key={item.id} className="flex justify-between items-center text-sm border-b pb-2">
                  <div>
                    <p className="font-semibold">{product.name || 'Sản phẩm'}</p>
                    <p className="text-gray-500">SL: {item.quantity}</p>
                  </div>
                  <p className="font-medium">{item.price?.toLocaleString('vi-VN')} ₫</p>
                </div>
              );
            })}
          </div>

          <div className="border-t mt-4 pt-4 space-y-2">
            <div className="flex justify-between font-bold text-lg">
              <span>Tổng thanh toán:</span>
              <span className="text-blue-600">{calculateTotal().toLocaleString('vi-VN')} ₫</span>
            </div>
          </div>
        </div>

        {/* Nút xác nhận */}
        <div className="bg-gray-50 p-6 border rounded-lg flex flex-col justify-between">
          <div>
            <h2 className="text-lg font-bold mb-4">Phương thức thanh toán</h2>
            <div className="p-3 border rounded bg-white font-medium text-sm text-gray-700 mb-4">
              💵 Thanh toán khi nhận hàng (COD)
            </div>
          </div>

          <button
            onClick={handlePlaceOrder}
            disabled={submitting}
            className="w-full bg-black text-white py-3.5 rounded-lg font-bold hover:bg-gray-800 transition disabled:opacity-50"
          >
            {submitting ? 'Đang xử lý...' : 'Xác Nhận Đặt Hàng'}
          </button>
        </div>
      </div>
    </div>
  );
}