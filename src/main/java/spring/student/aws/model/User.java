package spring.student.aws.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "user_table") // use a safer name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false) // 🚨 Enforce uniqueness here
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role;

	@Column(name = "last_updated")
	private Instant lastUpdated = Instant.now();

}
