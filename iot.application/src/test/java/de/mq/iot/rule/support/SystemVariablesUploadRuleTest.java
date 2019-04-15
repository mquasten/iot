package de.mq.iot.rule.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.state.State;
import de.mq.iot.state.StateService;

class SystemVariablesUploadRuleTest {
	
	private StateService stateService = Mockito.mock(StateService.class);
	
	private final SystemVariablesUploadRuleImpl systemVariablesUploadRule = new SystemVariablesUploadRuleImpl(stateService);
	
	private final DefaultRuleInput ruleInput = new DefaultRuleInput();
	@Test
	void evaluate() {
		assertTrue(systemVariablesUploadRule.evaluate(ruleInput));
	}
	
	@Test
	void evaluateUpdateMode() {
		ruleInput.useTestMode();
		assertFalse(systemVariablesUploadRule.evaluate(ruleInput));
	}
	
	@Test
	void updateSystemVariables() {
		@SuppressWarnings("unchecked")
		final Collection<State<Object>> states = Arrays.asList(Mockito.mock(State.class));
		
		systemVariablesUploadRule.updateSystemVariables(states);
		
		Mockito.verify(stateService).update(states);
		
	}

}
