package Project.Config;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import Project.Helper.exception.ResourceNotFoundException;
import Project.Model.RefreshToken;
import Project.Model.User;
import Project.Model.DTO.ExchangeTokenResponse;
import Project.Model.DTO.LoginResponseDTO;
import Project.Service.RefreshTokenService;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class JWTService {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;
    private final JwtEncoder jwtEncoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${project.jwt.access-token-validity-in-seconds}")
	private long accessTokenExpiration;

    @Value("${project.jwt.refresh-token-validity-in-seconds}")
	private String refreshTokenExpiration;

    public String getScope(Authentication authentication) {
		if (authentication != null) {
			String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.joining(" "));
			return scope;
		}

		return "UNKNOWN";
	}

    public String generateSecureToken() {
		byte[] randomBytes = new byte[64]; 
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(randomBytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}

    public String createAccessToken(Authentication authentication, int userId) {
        Instant now = Instant.now();
        Instant validity = now.plus(Long.valueOf(accessTokenExpiration), ChronoUnit.SECONDS);

        String scope = this.getScope(authentication);

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                                                .issuedAt(now)
                                                .expiresAt(validity)
                                                .subject(authentication.getName())
                                                .claim("id", userId)
                                                .claim("scope", scope)
                                                .build();
        
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    }

    public String createRefreshToken(User user) {
        Instant now = Instant.now();
        Instant validity = now.plus(Long.valueOf(accessTokenExpiration), ChronoUnit.SECONDS);
        String token = this.generateSecureToken();

        RefreshToken rf = new RefreshToken();
        rf.setCreatedAt(now);
        rf.setExpiresAt(validity);
        rf.setToken(token);
        rf.setUser(user);
        this.refreshTokenService.createRefreshToken(rf);

        return token;
    }

    public ExchangeTokenResponse handleExchangeToken(String inputToken) {
		// check refresh token in db
		RefreshToken currentRefreshToken = this.refreshTokenService.findRefreshTokenByToken(inputToken);

		// validate token
		Instant now = Instant.now();
		if (now.isAfter(currentRefreshToken.getExpiresAt())) {
			throw new ResourceNotFoundException("Refresh token đã hết hạn");
		}

		// create new token
		User currentUser = currentRefreshToken.getUser();
		String newRefreshToken = this.createRefreshToken(currentUser);

		Instant validity = now.plus(Long.valueOf(accessTokenExpiration), ChronoUnit.SECONDS);

		String scope = "ROLE_" + currentUser.getRole().getName();

		JwtClaimsSet claims = JwtClaimsSet.builder().issuedAt(now).expiresAt(validity).subject(currentUser.getEmail())
				.claim("id", currentUser.getId()).claim("scope", scope).build();

		JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

		String accessToken = this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

		// @formatter:on

		ExchangeTokenResponse exToken = new ExchangeTokenResponse();
		exToken.setAccessToken(accessToken);
		exToken.setRefreshToken(newRefreshToken);
		exToken.setUser(new LoginResponseDTO.UserLogin(currentUser.getId(), currentUser.getEmail(), scope));

		// delete old refreshToken
		this.refreshTokenService.deleteById(currentRefreshToken.getId());

		return exToken;
	}
}
