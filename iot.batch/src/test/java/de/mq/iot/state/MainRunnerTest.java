package de.mq.iot.state;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.mq.iot.state.support.StateUpdateSeriviceImpl;





@Mains({
	@Main(name = MainRunnerTest.COMMAND, parameters = { @MainParameter(name = "d", desc = "" )}),
})
class MainRunnerTest {

	static final String COMMAND = "updateCalendar";
	private static final int OK_STATE = 0;
	private static final int DAYS_OFFSET = 10;
	final static StateUpdateSeriviceImpl stateUpdateService=Mockito.mock(StateUpdateSeriviceImpl.class);
	
	@BeforeEach
	void setup() {
		Mockito.reset(stateUpdateService);
	}
	
	@Test
	void  run() {
		assertEquals(OK_STATE, MainRunner.run(new String[] {COMMAND,  "-d " + DAYS_OFFSET}, MockServiceConfiguration.class, MainRunner.MAIN_DEFINITION_CLASS ));
		
		Mockito.verify(stateUpdateService).updateWorkingday(DAYS_OFFSET);
		Mockito.verify(stateUpdateService).updateTime(10);
	}
	

	@Test
	void  runDefault() {
		assertEquals(OK_STATE, MainRunner.run(new String[] {COMMAND }, MockServiceConfiguration.class, MainRunner.MAIN_DEFINITION_CLASS ));
		
		Mockito.verify(stateUpdateService).updateWorkingday(0);
		Mockito.verify(stateUpdateService).updateTime(0);
	}
	
	@Test
	void  runMissingCommand() {
		assertEquals(1, MainRunner.run(new String[] {"-d " + DAYS_OFFSET }, MockServiceConfiguration.class, MainRunner.MAIN_DEFINITION_CLASS ));
		
		Mockito.verify(stateUpdateService,Mockito.never()).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService, Mockito.never()).updateTime(Mockito.anyInt());
	}
	
	@Test
	void  runMoreThanOneCommand() {
		assertEquals(1, MainRunner.run(new String[] {COMMAND,  "-d " + DAYS_OFFSET , "xxxx"}, MockServiceConfiguration.class , MainRunner.MAIN_DEFINITION_CLASS));
		
		Mockito.verify(stateUpdateService, Mockito.never()).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService, Mockito.never()).updateTime(Mockito.anyInt());
	}
	@Test
	void  runInvalidOption() {
		assertEquals(1, MainRunner.run(new String[] {COMMAND,  "-d " + DAYS_OFFSET , "-x"}, MockServiceConfiguration.class, MainRunner.MAIN_DEFINITION_CLASS ));
		
		Mockito.verify(stateUpdateService, Mockito.never()).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService, Mockito.never()).updateTime(Mockito.anyInt());
	}
	
	@Test
	void  runValueMissing() {
		assertEquals(1, MainRunner.run(new String[] {COMMAND,  "-d "}, MockServiceConfiguration.class, MainRunnerTest.class ));
		Mockito.verify(stateUpdateService, Mockito.never()).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService, Mockito.never()).updateTime(Mockito.anyInt());
	}
	
	
	
	

}
@Configuration
class MockServiceConfiguration {
	
	@Bean
	StateUpdateService stateUpdateService() {
		return MainRunnerTest.stateUpdateService;
		
	}
	
}

