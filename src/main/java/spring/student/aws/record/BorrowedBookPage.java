package spring.student.aws.record;

import java.util.List;

import spring.student.aws.model.BorrowedBook;

public record BorrowedBookPage(List<BorrowedBook> content, int totalPages, int number) {
}
