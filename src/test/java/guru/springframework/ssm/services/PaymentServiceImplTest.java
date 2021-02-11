package guru.springframework.ssm.services;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import guru.springframework.ssm.domain.Payment;
import guru.springframework.ssm.domain.PaymentEvent;
import guru.springframework.ssm.domain.PaymentState;
import guru.springframework.ssm.repository.PaymentRepository;

@SpringBootTest
public class PaymentServiceImplTest {

	@Autowired
	PaymentService paymentService;
	
	@Autowired
	PaymentRepository paymentRepository;
	
	Payment payment;
	
	@BeforeEach
	void setUp() {
		payment = Payment.builder().amount(new BigDecimal("12.99")).build();
	}
	
	@Transactional
	@Test
	void preAuth() {
		
		Payment savedPayment = paymentService.newPayment(payment);
		
		StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());
		
		paymentService.preAuth(savedPayment.getId());
		
		Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());
		
		System.out.println(sm.getState().getId());
		
		System.out.println(preAuthedPayment);
	}
	
	@Transactional
	@Test
	void auth() {
		
		System.out.println("");
		System.out.println("authorisation test");
		
		Payment savedPayment = paymentService.newPayment(payment);
		
		StateMachine<PaymentState, PaymentEvent> preAuthSm = paymentService.preAuth(savedPayment.getId());
		
		StateMachine<PaymentState, PaymentEvent> authSm = paymentService.authorise(savedPayment.getId());
		
		System.out.println(authSm.getState().getId());
		System.out.println(savedPayment);
	}
}
