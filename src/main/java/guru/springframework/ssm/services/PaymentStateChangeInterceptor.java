package guru.springframework.ssm.services;

import java.util.Optional;

import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import guru.springframework.ssm.domain.Payment;
import guru.springframework.ssm.domain.PaymentEvent;
import guru.springframework.ssm.domain.PaymentState;
import guru.springframework.ssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent>{

	private final PaymentRepository paymentRepository;
	
	@Override
	public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
								Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> sm) {
		
		Optional.ofNullable(message).ifPresent( msg -> {
			Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)))
				.ifPresent(paymentId -> {
					Payment payment = paymentRepository.getOne(paymentId);
					payment.setPaymentState(state.getId());
					paymentRepository.save(payment);
				});
		});
	}
}
