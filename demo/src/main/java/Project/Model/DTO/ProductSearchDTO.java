package Project.Model.DTO;

import java.util.Locale.Category;

import Project.Model.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDTO {
    private String keyword;

    private String brand;

    private Integer minPrice;

    private Integer maxPrice;

    private String category;
}
