import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderService } from '../services/orderService';
import { Client } from "@stomp/stompjs";

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await orderService.getOrders();
      setOrders(data || []);
    } catch (error) {
      console.error("Lỗi khi tải danh sách đơn hàng:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {

    // 1. Lấy danh sách Order ban đầu
      fetchOrders();

      // 2. Tạo WebSocket client
      const client = new Client({
          brokerURL: "ws://localhost:8090/ws",

          onConnect: () => {

              console.log("WebSocket connected");

              // 3. Lấy user hiện tại
              const user = JSON.parse(
                  localStorage.getItem("user")
              );

              const userId = user?.id;

              console.log("Subscribe user:", userId);

              // 4. Subscribe topic riêng của user
              client.subscribe(
                  `/topic/orders/user/${userId}`,
                  (message) => {

                      const updatedOrder =
                          JSON.parse(message.body);

                      console.log(
                          "Order updated:",
                          updatedOrder
                      );

                      // 5. Cập nhật Order trong state
                      setOrders(prevOrders =>
                          prevOrders.map(order =>
                              order.id === updatedOrder.id
                                  ? updatedOrder
                                  : order
                          )
                      );
                  }
              );
          },

          onStompError: (frame) => {
              console.error(
                  "STOMP error:",
                  frame
              );
          },

          onWebSocketError: (error) => {
              console.error(
                  "WebSocket error:",
                  error
              );
          }
      });

      // 6. Kết nối
      client.activate();

      // 7. Cleanup khi rời trang
      return () => {
          client.deactivate();
      };

  }, []);

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm("Bạn có chắc muốn hủy đơn hàng này?")) return;
    try {
      await orderService.cancelOrder(orderId);
      fetchOrders(); // Tải lại danh sách sau khi hủy thành công
    } catch (error) {
      console.error("Hủy đơn hàng thất bại:", error);
      alert("Không thể hủy đơn hàng này.");
    }
  };

  const renderStatusBadge = (status) => {
    switch (status) {
      case 'CANCELLED':
        return <span className="px-3 py-1 bg-red-100 text-red-700 rounded-full text-xs font-bold">Đã hủy</span>;
      case 'PENGDING':
      case 'PENDING':
        return <span className="px-3 py-1 bg-amber-100 text-amber-700 rounded-full text-xs font-bold">Đang chờ xử lý</span>;
      case 'COMPLETED':
        return <span className="px-3 py-1 bg-green-100 text-green-700 rounded-full text-xs font-bold">Hoàn thành</span>;
      default:
        return <span className="px-3 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-bold">{status}</span>;
    }
  };

  if (loading) {
    return <div className="text-center py-20 font-medium">Đang tải lịch sử đơn hàng...</div>;
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Đơn Hàng Của Tôi</h1>

      {orders.length === 0 ? (
        <div className="text-center py-16 bg-gray-50 rounded-lg border">
          <p className="text-gray-500 mb-4">Bạn chưa mua đơn hàng nào.</p>
          <button
            onClick={() => navigate('/products')}
            className="bg-black text-white px-6 py-2.5 rounded-md text-sm font-medium hover:bg-gray-800 transition"
          >
            Khám phá sản phẩm
          </button>
        </div>
      ) : (
        <div className="space-y-6">
          {orders.map((order) => (
            <div key={order.id} className="border rounded-lg bg-white overflow-hidden shadow-sm">
              {/* Header của Đơn hàng */}
              <div className="bg-gray-50 p-4 border-b flex flex-wrap justify-between items-center gap-2">
                <div className="flex items-center gap-4">
                  <span className="font-bold text-gray-900">Mã đơn: #{order.id}</span>
                  <span className="text-xs text-gray-500">
                    {order.createdAt ? new Date(order.createdAt).toLocaleString('vi-VN') : ''}
                  </span>
                </div>
                <div>{renderStatusBadge(order.status)}</div>
              </div>

              {/* Danh sách items trong Đơn hàng */}
              <div className="p-4 divide-y">
                {order.orderItems?.map((item) => (
                  <div key={item.id} className="py-3 flex justify-between items-center text-sm">
                    <div>
                      <p className="font-bold text-gray-800">{item.product_Name}</p>
                      <p className="text-gray-500 text-xs">Số lượng: {item.quantity}</p>
                    </div>
                    <div className="text-right">
                      <p className="font-semibold text-gray-900">
                        {item.price?.toLocaleString('vi-VN')} ₫
                      </p>
                    </div>
                  </div>
                ))}
              </div>

              {/* Footer của Đơn hàng */}
              <div className="p-4 bg-gray-50 border-t flex justify-between items-center">
                <div className="text-sm">
                  Tổng tiền thanh toán:{' '}
                  <span className="font-bold text-blue-600 text-base ml-1">
                    {order.totalPrice?.toLocaleString('vi-VN')} ₫
                  </span>
                </div>

                {(order.status === 'PENGDING' || order.status === 'PENDING') && (
                  <button
                    onClick={() => handleCancelOrder(order.id)}
                    className="px-4 py-1.5 border border-red-500 text-red-600 hover:bg-red-50 rounded text-xs font-semibold transition"
                  >
                    Hủy đơn hàng
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}