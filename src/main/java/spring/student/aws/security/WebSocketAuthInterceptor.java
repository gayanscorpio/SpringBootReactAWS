package spring.student.aws.security;

import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import spring.student.aws.model.User;
import spring.student.aws.repository.UserRepository;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements WebSocketGraphQlInterceptor {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepo;

	public WebSocketAuthInterceptor(JwtUtil jwtUtil, UserRepository userRepo) {
		this.jwtUtil = jwtUtil;
		this.userRepo = userRepo;
	}

	@Override
	public Mono<Object> handleConnectionInitialization(WebSocketSessionInfo sessionInfo,
			Map<String, Object> connectionInitPayload) {

		Map<String, Object> payload = connectionInitPayload;
		String authHeader = payload != null ? (String) payload.get("authorization") : null;

		System.out.println(">>> [WS Auth] Authorization from payload: " + authHeader);

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);

			try {
				String username = jwtUtil.getSubjectFromToken(token);
				User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

				if (!jwtUtil.isTokenValid(token, user)) {
					return Mono.error(new RuntimeException("Token is invalid or expired"));
				}

				String role = jwtUtil.getAllClaimsFromToken(token).get("role", String.class);
				var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

				var authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

				SecurityContextHolder.getContext().setAuthentication(authentication);

				System.out.println(">>> [WS Auth] Authenticated user: " + username + " with role: " + role);

			} catch (Exception e) {
				System.out.println(">>> [WS Auth] Token validation error: " + e.getMessage());
				return Mono.error(new RuntimeException("Authentication failed: " + e.getMessage()));
			}
		}

		return Mono.empty();
	}

	@Override
	public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, WebSocketGraphQlInterceptor.Chain chain) {
		return chain.next(request);
	}

}
