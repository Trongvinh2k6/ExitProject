package Project.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Project.Helper.ApiResponse;
import Project.Model.DTO.CreateOrderItemDTO;
import Project.Model.DTO.OrderResponseDTO;
import Project.Model.DTO.OrderWebSocketEvent;
import Project.Service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/orders/create")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> postOrder(
            @Valid @RequestBody CreateOrderItemDTO request) {

        OrderResponseDTO order = this.orderService.createOrder(request);

        return ApiResponse.created(order);
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrders() {

        List<OrderResponseDTO> orders = this.orderService.fetchOrders();

        return ApiResponse.success(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrder(
            @PathVariable int id) {

        OrderResponseDTO order = this.orderService.fetchOrderById(id);

        return ApiResponse.success(order);
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> cancelOrder(
            @PathVariable int id) {

        OrderResponseDTO order = this.orderService.cancelOrder(id);

        int userId = this.orderService.getUserIdByOrderId(id);
        
        messagingTemplate.convertAndSend(
            "/topic/orders/user/" + userId,
            order
        );

        return ApiResponse.success(order);
    }

    @PutMapping("/orders/update/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(
            @PathVariable int id,
            @RequestParam String status) {

        OrderResponseDTO order =
                this.orderService.updateOrderStatus(id, status);

        int userId = this.orderService.getUserIdByOrderId(id);
        
        messagingTemplate.convertAndSend(
            "/topic/orders/user/" + userId,
            order
        );

        return ApiResponse.success(order);
    }
}