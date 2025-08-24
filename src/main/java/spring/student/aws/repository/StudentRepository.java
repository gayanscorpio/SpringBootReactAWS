package spring.student.aws.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import spring.student.aws.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
	Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
