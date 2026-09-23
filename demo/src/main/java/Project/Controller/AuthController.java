package Project.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Project.Config.JWTService;
import Project.Helper.ApiResponse;
import Project.Model.RefreshToken;
import Project.Model.User;
import Project.Model.DTO.ExchangeTokenResponse;
import Project.Model.DTO.LoginRequestDTO;
import Project.Model.DTO.LoginResponseDTO;
import Project.Model.DTO.LoginResponseDTO.UserLogin;
import Project.Service.RefreshTokenService;
import Project.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
	private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
	private final AuthenticationManager authenticationManager;

    @Value("${project.jwt.refresh-token-validity-in-seconds}")
	private int refreshTokenExpiration;

    @PostMapping("/auth/login")
    public ResponseEntity<?> postLogin(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        UsernamePasswordAuthenticationToken username_and_password 
                            = new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword());
        
        Authentication authentication = authenticationManager.authenticate(username_and_password);

        User currentUser = this.userService.findUserByEmail(authentication.getName());
        
        String accessToken = jwtService.createAccessToken(authentication, currentUser.getId());

        String refreshToken = jwtService.createRefreshToken(currentUser);

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setAccessToken(accessToken);
        responseDTO.setRefreshToken(refreshToken);
        responseDTO.setUser(new UserLogin(currentUser.getId(), currentUser.getEmail(), currentUser.getRole().getName()));

        ResponseCookie resCookies = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).secure(true)
				.path("/").maxAge(refreshTokenExpiration).build();

		ApiResponse<LoginResponseDTO> finalData = new ApiResponse<>(HttpStatus.OK, "", responseDTO, "");

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(finalData);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<ExchangeTokenResponse>> postRefreshToken(@RequestParam("token") String refreshToken) {
        ExchangeTokenResponse res = jwtService.handleExchangeToken(refreshToken);
        return ApiResponse.success(res);
    }

    @PostMapping("/auth/refresh-with-cookie")
	public ResponseEntity<?> postRefreshTokenWithCookie(@CookieValue(required = false) String refreshToken) {

		ExchangeTokenResponse res = this.jwtService.handleExchangeToken(refreshToken);

	    ResponseCookie resCookies = ResponseCookie
	            .from("refreshToken", res.getRefreshToken())
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(refreshTokenExpiration)
	            .build();

	    ApiResponse<ExchangeTokenResponse> finalData = new ApiResponse<>(
	            HttpStatus.OK, "", res, "");

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(finalData);
	}

    @GetMapping("/auth/account")
	public ResponseEntity<ApiResponse<UserLogin>> getAccount() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Jwt jwt = (Jwt) auth.getPrincipal();

		String userId = jwt.getClaimAsString("id");
		String username = jwt.getSubject();
		String role = jwt.getClaimAsString("scope");

		LoginResponseDTO.UserLogin user = new LoginResponseDTO.UserLogin();
		user.setId(Integer.valueOf(userId));
		user.setUsername(username);
		user.setRole(role);

		return ApiResponse.success(user);
	}

	@PostMapping("/auth/logout")
	public ResponseEntity<?> postLogout(@AuthenticationPrincipal Jwt jwt,
			@CookieValue(required = false) String refreshToken) {
		String userId = jwt.getClaimAsString("id");
		String username = jwt.getSubject();

		RefreshToken currentTokenInDB = this.refreshTokenService.findRefreshTokenByToken(refreshToken);
		this.refreshTokenService.deleteById(currentTokenInDB.getId());

	    ResponseCookie deleteSpringCookie = ResponseCookie
	            .from("refreshToken", null)
	            .httpOnly(true)
	            .secure(true)
	            .path("/")
	            .maxAge(0)
	            .build();
		ApiResponse<String> finalData = new ApiResponse<>(HttpStatus.OK, "", "ok", "");

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(finalData);
	}
}
