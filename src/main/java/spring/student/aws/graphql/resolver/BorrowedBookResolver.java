package spring.student.aws.graphql.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import spring.student.aws.model.BorrowedBook;
import spring.student.aws.record.BorrowedBookPage;
import spring.student.aws.repository.BorrowedBookRepository;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Controller
public class BorrowedBookResolver {

	@Autowired
	private BorrowedBookRepository borrowedBookRepository;

	@QueryMapping
	public BorrowedBookPage borrowedBooksByStudent(@Argument Long studentId, @Argument int page, @Argument int size) {
		Page<BorrowedBook> result = borrowedBookRepository.findByStudentId(studentId, PageRequest.of(page, size));

		return new BorrowedBookPage(result.getContent(), result.getTotalPages(), result.getNumber());
	}

	@QueryMapping
	public List<BorrowedBook> allBorrowedBooks() {
		return borrowedBookRepository.findAll();
	}
}
