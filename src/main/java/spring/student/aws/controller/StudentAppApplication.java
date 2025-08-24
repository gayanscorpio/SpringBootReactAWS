package spring.student.aws.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "spring.student.aws")
@EntityScan(basePackages = "spring.student.aws.model")
@EnableJpaRepositories(basePackages = "spring.student.aws.repository")
@ComponentScan(basePackages = {"spring.student.aws", "spring.student.aws.graphql.resolver"})
public class StudentAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudentAppApplication.class, args);
	}
}
