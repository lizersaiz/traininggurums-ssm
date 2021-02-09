package guru.springframework.ssm.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import guru.springframework.ssm.domain.PaymentEvent;
import guru.springframework.ssm.domain.PaymentState;

@SpringBootTest
class StateMachineConfigTest {

	@Autowired
	StateMachineFactory<PaymentState, PaymentEvent> factory;
	
	@Test
	void testStateMachine() {
		
		StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
		
		sm.start();
		System.out.println(sm.getState().toString());
		
		sm.sendEvent(PaymentEvent.PRE_AUTHORISE);
		System.out.println(sm.getState().toString());
		
		sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
		System.out.println(sm.getState().toString());
	}
}