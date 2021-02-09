package guru.springframework.ssm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import guru.springframework.ssm.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
