package spring.student.aws.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.student.aws.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
}
