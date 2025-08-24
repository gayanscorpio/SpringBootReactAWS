package spring.student.aws.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import spring.student.aws.config.JwtProperties;
import spring.student.aws.model.User;
import java.time.Instant;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

	private final PrivateKey privateKey;
	private final PublicKey publicKey;
	private final JwtProperties jwtProperties;

	public JwtUtil(RsaKeyProperties rsaKeyProperties, JwtProperties JwtProperties) {
		this.privateKey = rsaKeyProperties.getPrivateKey();
		this.publicKey = rsaKeyProperties.getPublicKey();

		this.jwtProperties = JwtProperties;
	}

	// Generate JWT token with a subject (usually user name)
	public String generateToken(String subject, String role, Instant lastUpdated) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

		return Jwts.builder().setSubject(subject).setIssuedAt(now).claim("role", role)
				.claim("lastUpdated", lastUpdated.toEpochMilli()).setExpiration(expiryDate)
				.signWith(privateKey, SignatureAlgorithm.RS256).compact();
	}

	// Extract username/subject from token
	public String getSubjectFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// Extract expiration date from token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	// General method to extract claims - Payload (Claims)
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// Parse the token and return claims - Payload (Claims)
	public Claims getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
	}

	public boolean isTokenValid(String token, User userFromDb) {
		Claims claims = getAllClaimsFromToken(token);

		long tokenLastUpdated = claims.get("lastUpdated", Long.class);
		long dbLastUpdated = userFromDb.getLastUpdated().toEpochMilli();

		return tokenLastUpdated == dbLastUpdated;
	}

	// Validate token (checks signature and expiration)
	public boolean validateToken(String token, String username) {
		try {
			final String tokenSubject = getSubjectFromToken(token);
			return (tokenSubject.equals(username) && !isTokenExpired(token));
		} catch (SignatureException e) {
			// invalid signature/claims
			return false;
		} catch (Exception e) {
			// any other errors means invalid token
			return false;
		}
	}

	// Check if token is expired
	private boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
}
