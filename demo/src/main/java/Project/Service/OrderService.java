package Project.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.Order;
import Project.Model.OrderItem;
import Project.Model.Product;
import Project.Model.DTO.CreateOrderItemDTO;
import Project.Model.DTO.OrderItemRequestDTO;
import Project.Model.DTO.OrderItemResponseDTO;
import Project.Model.DTO.OrderResponseDTO;
import Project.Repository.OrderRepository;
import Project.Repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemResponseDTO convertOrderItemToDTO(OrderItem orderItem) {

        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .product_Id(orderItem.getProduct().getId())
                .product_Name(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }

    public OrderResponseDTO convertOrderToDTO(Order order) {

        List<OrderItemResponseDTO> orderItems = order.getOrderItems().stream()
                .map(orderItem -> {
                    return convertOrderItemToDTO(orderItem);
                })
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .orderItems(orderItems)
                .build();
    }

    public OrderResponseDTO createOrder(CreateOrderItemDTO request) {

        Order order = new Order();

        order.setStatus("PENGDING");
        order.setCreatedAt(Instant.now());

        int totalPrice = 0;

        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequestDTO itemRequest : request.getItems()) {

            OrderItem orderItem = new OrderItem();

            Product product = this.productRepository
                    .findById(itemRequest.getProduct_Id())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Khong co product"));

            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setOrder(order);

            totalPrice += orderItem.getPrice() * orderItem.getQuantity();

            orderItems.add(orderItem);
        }

        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);

        return convertOrderToDTO(
                this.orderRepository.save(order)
        );
    }

    public List<OrderResponseDTO> fetchOrders() {

        List<OrderResponseDTO> orderResponseDTO = this.orderRepository
                .findAll()
                .stream()
                .map(order -> {
                    return convertOrderToDTO(order);
                })
                .collect(Collectors.toList());

        return orderResponseDTO;
    }

    public OrderResponseDTO fetchOrderById(int id) {

        Order order = this.orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Khong co don hang"));

        return convertOrderToDTO(order);
    }

    public OrderResponseDTO cancelOrder(int id) {

        Order order = orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Khong co don hang"));

        order.setStatus("CANCELLED");

        return convertOrderToDTO(
                orderRepository.save(order)
        );
    }

    public OrderResponseDTO updateOrderStatus(int id, String status) {

        Order order = orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Khong co don hang"));

        order.setStatus(status);

        return convertOrderToDTO(
                orderRepository.save(order)
        );
    }

    public OrderResponseDTO updateOrderById(int id, CreateOrderItemDTO updateOrderItem) {

        Order order = this.orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Khong co don hang"));

        int totalPrice = 0;

        // Xóa các OrderItem cũ
        order.getOrderItems().clear();

        // Thêm các OrderItem mới
        for (OrderItemRequestDTO itemRequest : updateOrderItem.getItems()) {

            Product product = this.productRepository
                    .findById(itemRequest.getProduct_Id())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Khong co product"));

            OrderItem orderItem = new OrderItem();

            orderItem.setProduct(product);
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());

            // Thiết lập quan hệ OrderItem -> Order
            orderItem.setOrder(order);

            // Thêm vào List mà Hibernate đang quản lý
            order.getOrderItems().add(orderItem);

            totalPrice += product.getPrice() * itemRequest.getQuantity();
        }

        order.setTotalPrice(totalPrice);

        return convertOrderToDTO(
                this.orderRepository.save(order)
        );
    }
}