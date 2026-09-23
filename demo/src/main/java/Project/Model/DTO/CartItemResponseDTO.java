package Project.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {

    private int id;

    private ProductResponseDTO productResponseDTO;

    private int quantity;

    private int price;

}