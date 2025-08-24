package spring.student.aws.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import spring.student.aws.model.Student;
import spring.student.aws.repository.StudentRepository;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/students")
@SecurityRequirement(name = "bearerAuth")  // <- Secures this controller
public class StudentController {

	private final StudentRepository repository;

	public StudentController(StudentRepository repository) {
		this.repository = repository;
	}

	@PostMapping
	public Student saveStudent(@Valid @RequestBody Student student) {
		return repository.save(student);
	}

	// ✅ Update student (only ADMINs allowed)
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Student> updateStudent(@PathVariable Long id, @Valid @RequestBody Student studentDetails) {
		Student student = repository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
		student.setName(studentDetails.getName());
		student.setEmail(studentDetails.getEmail());
		return ResponseEntity.ok(repository.save(student));
	}

	// ✅ Delete student (only ADMINs allowed)
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
		Student student = repository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
		repository.delete(student);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public Student getStudent(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));
	}

	// ✅ Get all students with pagination, sorting, and filtering
	// Example: GET /students?page=0&size=5&sort=name,asc&nameFilter=john
	@GetMapping
	public ResponseEntity<Page<Student>> getAllStudents(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String[] sort,
			@RequestParam(required = false) String nameFilter) {
		Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
		Pageable pageable = PageRequest.of(page, size, sorting);

		Page<Student> result;
		if (nameFilter != null && !nameFilter.isBlank()) {
			result = repository.findByNameContainingIgnoreCase(nameFilter, pageable);
		} else {
			result = repository.findAll(pageable);
		}

		return ResponseEntity.ok(result);
	}
}
