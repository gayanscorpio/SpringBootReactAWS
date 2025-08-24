package spring.student.aws.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spring.student.aws.model.User;
import spring.student.aws.repository.UserRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepo;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepo) {
		this.jwtUtil = jwtUtil;
		this.userRepo = userRepo;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		System.out.println(">>> Incoming request: " + request.getRequestURI());
		System.out.println(">>> Authorization header: " + request.getHeader("Authorization"));

		// Checks for an Authorization header
		String authHeader = request.getHeader("Authorization");

		// Verifies the format
		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			// Extracts the token
			// Removes the "Bearer " part (first 7 characters) to get the raw JWT token string.
			String token = authHeader.substring(7);

			try {
				String username = jwtUtil.getSubjectFromToken(token);
				User user = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

				if (!jwtUtil.isTokenValid(token, user)) {
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid or outdated");
					return;
				}

				String role = jwtUtil.getAllClaimsFromToken(token).get("role", String.class);
				List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

				Authentication auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(auth);
				
				System.out.println("User authenticated with roles: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

			} catch (Exception e) {
		        System.out.println("Exception in JWT filter: " + e.getMessage());
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Expired JWT Token");
				return;
			}
		}

		chain.doFilter(request, response);
	}

}
