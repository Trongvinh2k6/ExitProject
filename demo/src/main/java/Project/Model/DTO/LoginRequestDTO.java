package Project.Model.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

	@NotBlank(message = "username không được để trống")
	private String username;

	@NotBlank(message = "password không được để trống")
	private String password;

}
