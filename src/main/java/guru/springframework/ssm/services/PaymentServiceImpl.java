package guru.springframework.ssm.services;

import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import guru.springframework.ssm.domain.Payment;
import guru.springframework.ssm.domain.PaymentEvent;
import guru.springframework.ssm.domain.PaymentState;
import guru.springframework.ssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
	
	@Override
	public Payment newPayment(Payment payment) {
		
		payment.setPaymentState(PaymentState.NEW);
		return paymentRepository.save(payment);
	}

	@Override
	public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
		
		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		
		return null;
	}

	@Override
	public StateMachine<PaymentState, PaymentEvent> authorisePayment(Long paymentId) {

		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
		
		return null;
	}

	@Override
	public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {

		StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);

		return null;
	}

	private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
		
		Payment payment = paymentRepository.getOne(paymentId);
		
		StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));
		
		sm.stop();
		
		sm.getStateMachineAccessor()
			.doWithAllRegions(stateMachineAccess ->{
				stateMachineAccess.resetStateMachine(new DefaultStateMachineContext<>(payment.getPaymentState(), null, null, null));
			});
		
		sm.start();
		
		return sm;
	}
}
