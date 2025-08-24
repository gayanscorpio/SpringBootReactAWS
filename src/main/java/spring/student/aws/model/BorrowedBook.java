package spring.student.aws.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
public class BorrowedBook {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// One student can borrow many books (up to 5, as per your rule).
	@ManyToOne
	private Student student;

	// One book title (e.g., "Java Basics") can be borrowed many times.
	@ManyToOne
	private Book book;

	private LocalDate borrowDate;

	private LocalDate dueDate;

	private LocalDate returnDate;

	private Double fineAmount;
}
