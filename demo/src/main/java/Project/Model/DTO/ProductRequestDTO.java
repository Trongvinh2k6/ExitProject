package Project.Model.DTO;

import Project.Model.Brand;
import Project.Model.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "name không được để trống")
    private String name;

    @NotBlank(message = "description không được để trống")
	private String description;
    
    @NotNull(message = "Price không được để trống")
    @Min(value = 0, message = "Price phải lớn hơn hoặc bằng 0")
    private Integer price;

    @NotNull(message = "Quantity không được để trống")
    @Min(value = 1, message = "Quantity phải lớn hơn hoặc bằng 1")
    private Integer quantity;

    @NotNull(message = "Brand không được để trống")
    private Integer brandId;

    @NotNull(message = "Category không được để trống")
    private Integer categoryId;
}
