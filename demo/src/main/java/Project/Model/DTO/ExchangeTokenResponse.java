package Project.Model.DTO;

import Project.Model.DTO.LoginResponseDTO.UserLogin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeTokenResponse {

	private String accessToken;

	private String refreshToken;

	private String tokenType = "Bearer";

	private UserLogin user;
}
