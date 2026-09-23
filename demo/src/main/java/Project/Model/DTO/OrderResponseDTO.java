package Project.Model.DTO;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private int id;

    private int totalPrice;

    private String status;
    
    private Instant createdAt;

    private List<OrderItemResponseDTO> orderItems;
}
