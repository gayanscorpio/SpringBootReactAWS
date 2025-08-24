package spring.student.aws.graphql.resolver;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import spring.student.aws.model.Student;
import spring.student.aws.repository.StudentRepository;

@RequiredArgsConstructor
@Component
@SecurityRequirement(name = "bearerAuth")
@Controller
public class StudentQueryResolver {

	private final StudentRepository repository;

	@QueryMapping
	public List<Student> allStudents(@Argument int page, @Argument int size) {

		System.out.println(">>> In allStudents: page = " + page + ", size = " + size);

		try {
			Pageable pageable = PageRequest.of(page, size);

			if (pageable == null) {
				System.out.println(">>> repository.findAll() returned null");
				return Collections.emptyList();
			}

			List<Student> students = repository.findAll(pageable).getContent();

			System.out.println(">>> Students fetched: " + (students != null ? students.size() : "null"));

			System.out.println("Fetched students: " + students.size());
			return students == null ? Collections.emptyList() : students;

		} catch (Exception e) {
			System.err.println(">>> Exception in allStudents(): " + e.getMessage());

			e.printStackTrace(); // <== You will see the real issue here in console
			return Collections.emptyList();
		}
	}

	@QueryMapping
	public Student studentById(@Argument String id) {
		 Long longId = Long.valueOf(id);
		System.out.println(">>> repository.findById(longId) returned null");
		return repository.findById(longId).orElse(null);
	}

}
