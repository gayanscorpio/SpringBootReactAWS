package spring.student.aws.controller;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.student.aws.model.User;
import spring.student.aws.repository.UserRepository;
import spring.student.aws.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	private final UserRepository userRepo;
	private final PasswordEncoder encoder;
	private final JwtUtil jwtUtil;

	public AuthController(UserRepository userRepo, PasswordEncoder encoder, JwtUtil jwtUtil) {
		this.userRepo = userRepo;
		this.encoder = encoder;
		this.jwtUtil = jwtUtil;
	}

	@GetMapping("/test")
	public String test() {
		return "AuthController is accessible";
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {

		if (userRepo.findByUsername(user.getUsername()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken.");
		}

		// Password must not be empty or too short
		if (user.getPassword() == null || user.getPassword().trim().isEmpty() || user.getPassword().length() < 6) {
			return ResponseEntity.badRequest().body("Password must be at least 6 characters.");
		}

		// Optional: check if it's numeric only
		if (user.getPassword().matches("\\d+")) {
			return ResponseEntity.badRequest().body("Password must include letters or symbols — not just numbers.");
		}

		// Optional: check common weak passwords
		if (user.getPassword().equalsIgnoreCase("password") || user.getPassword().equals("123456")) {
			return ResponseEntity.badRequest().body("Please choose a stronger password.");
		}

		user.setPassword(encoder.encode(user.getPassword()));
		user.setLastUpdated(Instant.now());

		userRepo.save(user);
		logger.info("User registered with ID: {}", user.getId());
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
		User user = userRepo.findByUsername(credentials.get("username"))
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (encoder.matches(credentials.get("password"), user.getPassword())) {
			String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getLastUpdated());
			return ResponseEntity.ok(Map.of("token", token));
		} else {
			throw new RuntimeException("Invalid password");
		}
	}

}
