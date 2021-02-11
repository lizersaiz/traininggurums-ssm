package guru.springframework.ssm.config;

import java.util.EnumSet;
import java.util.Random;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import guru.springframework.ssm.domain.PaymentEvent;
import guru.springframework.ssm.domain.PaymentState;
import guru.springframework.ssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent>{

	@Override
	public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
		
		states.withStates()
			.initial(PaymentState.NEW)
			.states(EnumSet.allOf(PaymentState.class))
			.end(PaymentState.AUTH)
			.end(PaymentState.PRE_AUTH_ERROR)
			.end(PaymentState.AUTH_ERROR);
	}
	
	@Override
	public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
		
		//SOURCE (source state) will change when a EVENT happens into TARGET and it will call an ACTION 
		transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORISE)
				.action(preAuthAction()).guard(paymentIdGuard())
			.and()
			.withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
			.and()
			.withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
			.and()
			.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE)
				.action(authAction())
			.and()
			.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APROVED)
			.and()
			.withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED);
	}
	
	public Guard<PaymentState, PaymentEvent> paymentIdGuard() {
		
		return context -> {
			
			return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
		};
	}
	
	@Override
	public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
		
		StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
			
			@Override
			public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
				
				log.info(String.format("stateChanged(from %s, to: %s)", from, to));
			}
		};
		
		config.withConfiguration().listener(adapter);
	}
	
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
