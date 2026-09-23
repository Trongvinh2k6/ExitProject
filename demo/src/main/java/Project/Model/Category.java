package Project.Model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "name không được để trống")
    private String name;

    @NotBlank(message = "description không được để trống")
	private String description;

    @OneToMany(mappedBy = "category")
    List<Product> product;
}
