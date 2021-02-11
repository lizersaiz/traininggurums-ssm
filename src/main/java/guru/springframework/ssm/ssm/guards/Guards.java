package guru.springframework.ssm.ssm.guards;

import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import guru.springframework.ssm.domain.PaymentEvent;
import guru.springframework.ssm.domain.PaymentState;
import guru.springframework.ssm.services.PaymentServiceImpl;

@Component
public class Guards {

	public Guard<PaymentState, PaymentEvent> paymentIdGuard() {
		
		return context -> {
			
			return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
		};
	}
}
