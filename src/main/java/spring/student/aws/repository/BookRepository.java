package spring.student.aws.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import spring.student.aws.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
