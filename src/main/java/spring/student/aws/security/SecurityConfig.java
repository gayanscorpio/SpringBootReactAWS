package spring.student.aws.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final JwtAuthEntryPoint jwtAuthEntryPoint;


	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtAuthEntryPoint jwtAuthEntryPoint) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtAuthEntryPoint = jwtAuthEntryPoint;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true); // Important for JWT Authorization

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.csrf(csrf -> csrf.disable())
			.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))  
	        .securityContext(context -> context.requireExplicitSave(false)) // Important for GraphQL
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(new AntPathRequestMatcher("/auth/**")).permitAll()
				.requestMatchers(new AntPathRequestMatcher("/graphiql")).hasAnyRole("DEV", "ADMIN")
				.requestMatchers(new AntPathRequestMatcher("/graphiql/**")).hasAnyRole("DEV", "ADMIN")
				.requestMatchers(new AntPathRequestMatcher("/students/**")).hasAnyRole("DEV", "ADMIN")
				.anyRequest().authenticated()
			)
			.httpBasic(httpBasic -> httpBasic.disable())
			.formLogin(form -> form.disable())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
