package de.mq.iot.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.state.support.SimpleServiceCommandlineRunnerImpl;
import de.mq.iot.state.support.StateUpdateSeriviceImpl;




@Mains({
	@Main(name = MainRunnerTest.COMMAND, parameters = { @MainParameter(name = "d", desc = "" )}),
})
class MainRunnerTest {

	private static final String MAIN_RUNNER_CLASS_FIELD = "mainRunnerClass";
	private static final String COMMANDLINE_RUNNER_FIELD = "commandlineRunner";
	private static final String MAIN_DEFINITION_CLASS_FIELD = "mainDefinitionClass";
	private static final String CONFIGURATION_CLASS_FIELD = "configurationClass";
	private static final Optional<Integer> ERROR_STATE = Optional.of(1);
	static final String COMMAND = "updateCalendar";
	private static final Optional<Integer> OK_STATE = Optional.of(0);
	private static final int DAYS_OFFSET = 10;
	final static StateUpdateSeriviceImpl stateUpdateService=Mockito.mock(StateUpdateSeriviceImpl.class);
	
	final static boolean called[] = { false};
	
	private final MainRunner mainRunner= new MainRunner();
	@BeforeEach
	void setup() {
		called[0]=false;
		Mockito.reset(stateUpdateService);
		ReflectionTestUtils.setField(mainRunner,CONFIGURATION_CLASS_FIELD, MockServiceConfiguration.class);
	}
	
	@Test
	void  run() {
		assertEquals(OK_STATE, mainRunner.run(new String[] {COMMAND,  "-d " + DAYS_OFFSET}));
		
		Mockito.verify(stateUpdateService).updateWorkingday(DAYS_OFFSET);
		Mockito.verify(stateUpdateService).updateTime(10);
	}
	

	@Test
	void  runDefault() {
		assertEquals(OK_STATE, mainRunner.run(new String[] {COMMAND }));
		
		Mockito.verify(stateUpdateService).updateWorkingday(0);
		Mockito.verify(stateUpdateService).updateTime(0);
	}
	
	@Test
	void  runMissingCommand() {
		assertEquals(ERROR_STATE, mainRunner.run(new String[] {"-d " + DAYS_OFFSET }));
		
		Mockito.verify(stateUpdateService,Mockito.never()).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService, Mockito.never()).updateTime(Mockito.anyInt());
	}
	
	@Test
	void  runMoreThanOneCommand() {
		assertEquals(ERROR_STATE, mainRunner.run(new String[] {COMMAND,  "-d " + DAYS_OFFSET , "xxxx"}));
		
		Mockito.verify(stateUpdateService, Mockito.never()).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService, Mockito.never()).updateTime(Mockito.anyInt());
	}
	@Test
	void  runInvalidOption() {
		assertEquals(ERROR_STATE, mainRunner.run(new String[] {COMMAND,  "-d " + DAYS_OFFSET , "-x"}));
		
		Mockito.verify(stateUpdateService, Mockito.never()).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService, Mockito.never()).updateTime(Mockito.anyInt());
	}
	
	@Test
	void  runValueMissing() {
		ReflectionTestUtils.setField(mainRunner,MAIN_DEFINITION_CLASS_FIELD, MainRunnerTest.class);
		assertEquals(ERROR_STATE, mainRunner.run(new String[] {COMMAND,  "-d "}));
		Mockito.verify(stateUpdateService, Mockito.never()).updateWorkingday(Mockito.anyInt());
		Mockito.verify(stateUpdateService, Mockito.never()).updateTime(Mockito.anyInt());
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void  runWithFlag() {
		final SimpleServiceCommandlineRunnerImpl commandlineRunner = Mockito.mock(SimpleServiceCommandlineRunnerImpl.class);
		final Collection<?> methodEntries = Mockito.mock(Collection.class);
		Mockito.doReturn(methodEntries).when(commandlineRunner).servicesMethods(COMMAND);
		final Map<String, Collection<MainParameter>> mainDefinitions = new HashMap<>();
		final MainParameter mainParameter = Mockito.mock(MainParameter.class);
		Mockito.doReturn("f").when(mainParameter).name();
		Mockito.doReturn(false).when(mainParameter).hasArg();
		mainDefinitions.put(COMMAND, Arrays.asList(mainParameter));
		Mockito.doReturn(mainDefinitions).when(commandlineRunner).mainDefinitions(MainRunner.class);
		
		ReflectionTestUtils.setField(mainRunner,COMMANDLINE_RUNNER_FIELD, commandlineRunner);
		
		assertEquals(OK_STATE, (mainRunner.run(new String[] {COMMAND,   "-f"})));
		
		
		final ArgumentCaptor<Collection> collectionArgumentCaptor = ArgumentCaptor.forClass(Collection.class);
		
		final ArgumentCaptor<Map> argumentValuesCaptor = ArgumentCaptor.forClass(Map.class);
		final ArgumentCaptor<ApplicationContext> applicationContextCaptor = ArgumentCaptor.forClass(ApplicationContext.class);
		
		Mockito.verify(commandlineRunner).execute(collectionArgumentCaptor.capture(), argumentValuesCaptor.capture(), applicationContextCaptor.capture());
	}
	
	@Test
	void  main() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		Field field = MainRunner.class.getDeclaredField(MAIN_RUNNER_CLASS_FIELD);
		field.setAccessible(true);
	
	
		
		final String[] args = new String[] {COMMAND,  "-d " + DAYS_OFFSET };
	
		
		field.set(null, MainRunnerMock.class);
		assertFalse(called[0]);
		
		MainRunner.main(args);
		
		assertTrue(called[0]);
	}

}
@Configuration
class MockServiceConfiguration {
	
	@Bean
	StateUpdateService stateUpdateService() {
		return MainRunnerTest.stateUpdateService;
		
	}
	
}

//Mockito isn't able to do that!!!;
class MainRunnerMock extends MainRunner {

	@Override
	Optional<Integer> run(String[] arguments) {
		
		MainRunnerTest.called[0]=true;
		return Optional.empty();
	}
	
	
}
