package spring.student.aws.graphql.resolver;

import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import spring.student.aws.model.Student;

@Controller
public class StudentSubscriptionResolver {

	private final Sinks.Many<Student> studentSink = Sinks.many().multicast().onBackpressureBuffer();

	@SubscriptionMapping
	public Flux<Student> studentAdded() {
		return studentSink.asFlux();
	}

	// Call this after creating a student in your REST/GraphQL mutation:
	public void publishNewStudent(Student student) {
		if (student != null) {
			System.out.println(">>> publishNewStudent >>>>>>>>>>");
			studentSink.tryEmitNext(student);
		}

	}
}
