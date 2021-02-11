package guru.springframework.ssm.ssm.actions;

import java.util.Random;

import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import guru.springframework.ssm.domain.PaymentEvent;
import guru.springframework.ssm.domain.PaymentState;
import guru.springframework.ssm.services.PaymentServiceImpl;

@Component
public class Actions {

	public Action<PaymentState, PaymentEvent> preAuthAction() {
			
		return context -> {
			
			System.out.println("PreAuth was called");
			
			if (new Random().nextInt(10) < 8) {
				
				System.out.println("Approved");
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			} else {
				
				System.out.println("Declined No Credit");
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			}
		};
	}
	
	public Action<PaymentState, PaymentEvent> authAction() {
		
		return context -> {
			
			System.out.println("Auth was called");
			
			if (new Random().nextInt(10) < 8) {
				
				System.out.println("Payment Authorized");
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APROVED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			} else {
				
				System.out.println("Payment Declined");
				context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
						.setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
						.build());
			}
		};
	}
}
