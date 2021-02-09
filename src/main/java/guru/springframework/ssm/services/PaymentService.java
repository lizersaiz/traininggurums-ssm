package guru.springframework.ssm.services;

import org.springframework.statemachine.StateMachine;

import guru.springframework.ssm.domain.Payment;
import guru.springframework.ssm.domain.PaymentEvent;
import guru.springframework.ssm.domain.PaymentState;

public interface PaymentService {

	Payment newPayment(Payment payment);
	
	StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

	StateMachine<PaymentState, PaymentEvent> authorisePayment(Long paymentId);
	
	StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId);
}
