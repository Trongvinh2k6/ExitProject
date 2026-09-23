package Project.Model.DTO;

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
public class UserResponseDTO {
	private int id;

	private String name;

	private String email;

	private String phone;

	private String address;

	private RoleResponseDTO roleResponseDTO;

}
