package Project.Model.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private Integer id;

    private String name;

    private String description;

    private Integer price;

    private Integer quantity;

    private BrandDTO brand;
    
    private CategoryDTO category;
}
