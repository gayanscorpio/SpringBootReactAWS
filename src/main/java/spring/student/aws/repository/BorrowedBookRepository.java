package spring.student.aws.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import spring.student.aws.model.BorrowedBook;

public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
	Page<BorrowedBook> findByStudentId(Long studentId, Pageable pageable);

}
