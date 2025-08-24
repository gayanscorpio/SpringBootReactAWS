package spring.student.aws.graphql.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import spring.student.aws.model.Book;
import spring.student.aws.repository.BookRepository;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Controller
public class BookResolver {

	@Autowired
	private BookRepository bookRepository;

	/**
	 * GraphQL Query: allBooks Usage: { allBooks { id title author ISBN
	 * availableCopies totalCopies } }
	 */
	@QueryMapping
	public List<Book> allBooks() {
		return bookRepository.findAll();
	}

	/**
	 * GraphQL Query: bookById Usage: { bookById(id: 1) { title author } }
	 */
	@QueryMapping
	public Book bookById(@Argument Long id) {
		return bookRepository.findById(id).orElse(null);
	}
}
