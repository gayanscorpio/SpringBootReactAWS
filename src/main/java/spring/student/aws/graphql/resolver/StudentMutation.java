package spring.student.aws.graphql.resolver;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
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
public class StudentMutation {

	private final StudentRepository studentRepository;
	private final StudentSubscriptionResolver subscriptionResolver;

	@MutationMapping
	public Student addStudent(@Argument String name, @Argument String email) {
		System.out.println(">>> createStudent >>>>>>>>>>");
		Student s = new Student();
		s.setName(name);
		s.setEmail(email);

		studentRepository.save(s);
		subscriptionResolver.publishNewStudent(s); // 🔁 push to subscribers

		return s;
	}

	@MutationMapping
	public Student updateStudent(Long id, Student student) {
		Student s = studentRepository.findById(id).orElseThrow();
		if (student.getName() != null)
			s.setName(student.getName());
		if (student.getEmail() != null)
			s.setEmail(student.getEmail());
		return studentRepository.save(s);
	}

	@MutationMapping
	public Boolean deleteStudent(Long id) {
		studentRepository.deleteById(id);
		return true;
	}
}
