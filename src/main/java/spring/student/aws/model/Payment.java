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
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Student student;

	private Double amountPaid;

	private LocalDate paymentDate;

	private String paymentMethod; // "Cash", "Online", etc.

	private String notes;
}
