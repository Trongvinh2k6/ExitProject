package Project.Model.DTO;

import Project.Model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

	@NotBlank(message = "name không được để trống")
	private String name;

	@NotBlank(message = "email không được để trống")
    private String email;
    
    @NotBlank(message = "password không được để trống")
    private String password;

    @NotBlank(message = "phone không được để trống")
    private String phone;

    @NotBlank(message = "address không được để trống")
    private String address;

	private Role role;
}
