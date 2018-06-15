package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;

import de.mq.iot.state.Main;
import de.mq.iot.state.MainParameter;
import de.mq.iot.state.Mains;

public class ServiceCommandlineRunnerTest {

	private static final int METHOD_RESULT = 4711;
	private static final String UPDATE_METHOD_TIME = "updateTime";
	private static final String DAYS_ARG_NAME = "d";
	private static final Integer OFFSET_ARG_VALUE = 10;
	private static final String UPDATE_METHOD_WORKING_DAY = "updateWorkingday";
	static final String PARAMETER_OFFSET_DAYS = DAYS_ARG_NAME;
	static final String UPDATE_WORKINGDAY = "updateWorkingday";
	final SimpleServiceCommandlineRunnerImpl serviceCommandlineRunner = new SimpleServiceCommandlineRunnerImpl();

	@Test
	void mainDefinitions() {
		final Map<String, Collection<MainParameter>> mainsList = serviceCommandlineRunner.mainDefinitions(MainDefinitionWithParameter.class);

		assertEquals(1, mainsList.size());

		assertTrue(mainsList.containsKey(UPDATE_WORKINGDAY));

		assertEquals(UPDATE_WORKINGDAY, mainsList.keySet().stream().findAny().get());

		final List<MainParameter> idGenerationParameters = new ArrayList<>(mainsList.get(UPDATE_WORKINGDAY));
		assertEquals(1, idGenerationParameters.size());

		final Map<String, MainParameter> parameters = idGenerationParameters.stream().collect(Collectors.toMap(parameter -> parameter.name(), parameter -> parameter));

		assertTrue(parameters.containsKey(PARAMETER_OFFSET_DAYS));

		assertEquals(PARAMETER_OFFSET_DAYS, parameters.get(PARAMETER_OFFSET_DAYS).name());
		assertEquals("0", parameters.get(PARAMETER_OFFSET_DAYS).defaultValue());

		assertTrue(parameters.get(PARAMETER_OFFSET_DAYS).hasArg());

	}

	@Test
	void mainDefinitionsMainDefinitionMissing() {
		assertThrows(IllegalArgumentException.class, () -> serviceCommandlineRunner.mainDefinitions(MainDefinitionWithParameterNoMain.class));

	}

	@Test
	final void servicesMethods() {
		final Collection<Entry<Method, Collection<String>>> methods = serviceCommandlineRunner.servicesMethods(UPDATE_WORKINGDAY);
		assertEquals(1, methods.size());

		assertEquals(UPDATE_METHOD_WORKING_DAY, methods.stream().findFirst().get().getKey().getName());
		assertEquals(1, methods.stream().findFirst().get().getValue().size());
		assertEquals(DAYS_ARG_NAME, methods.stream().findFirst().get().getValue().stream().findFirst().get());

	}

	@Test
	final void servicesMethodsSort() {
		final List<Entry<Method, Collection<String>>> methods = new ArrayList<>(serviceCommandlineRunner.servicesMethods("updateCalendar"));
		assertEquals(2, methods.size());

		assertEquals(UPDATE_METHOD_TIME, methods.get(0).getKey().getName());
		assertEquals(UPDATE_METHOD_WORKING_DAY, methods.get(1).getKey().getName());

		methods.stream().map(entry -> entry.getValue().size()).forEach(size -> assertEquals(Integer.valueOf(1), size));

		methods.stream().map(entry -> entry.getValue().iterator().next()).forEach(value -> assertEquals(DAYS_ARG_NAME, value));

	}

	@Test
	final void analyzeClassNotFoundException() {
		final BeanDefinition beanDefinition = Mockito.mock(BeanDefinition.class);
		Mockito.doReturn("Invalid").when(beanDefinition).getBeanClassName();
		assertThrows(IllegalStateException.class, () -> serviceCommandlineRunner.analyzeClass(UPDATE_WORKINGDAY, new HashMap<>(), beanDefinition));
	}

	@Test
	final void execute() {

		final Method updateStateMethod = updateMethod(StateUpdateSeriviceImpl.class);
		final Map<String, Object> environment = new HashMap<>();
		environment.put(DAYS_ARG_NAME, OFFSET_ARG_VALUE);

		final StateUpdateSeriviceImpl stateUpdateService = Mockito.mock(StateUpdateSeriviceImpl.class);

		serviceCommandlineRunner.execute(new AbstractMap.SimpleImmutableEntry<>(updateStateMethod, Arrays.asList(DAYS_ARG_NAME)), environment, stateUpdateService);

		Mockito.verify(stateUpdateService).updateWorkingday(10);

	}

	@Test
	final void executeWronfNumberOfArguments() {
		final Method updateStateMethod = updateMethod(StateUpdateSeriviceImpl.class);
		final Map<String, Object> environment = new HashMap<>();
		environment.put(DAYS_ARG_NAME, OFFSET_ARG_VALUE);
		final StateUpdateSeriviceImpl stateUpdateService = Mockito.mock(StateUpdateSeriviceImpl.class);
		assertThrows(IllegalArgumentException.class, () -> serviceCommandlineRunner.execute(new AbstractMap.SimpleImmutableEntry<>(updateStateMethod, Arrays.asList(DAYS_ARG_NAME, DAYS_ARG_NAME)), environment, stateUpdateService));
	}

	private Method updateMethod(Class<?> clazz) {
		return Arrays.asList(clazz.getMethods()).stream().filter(method -> method.getName().equals(UPDATE_METHOD_WORKING_DAY)).findFirst().orElseThrow(() -> new IllegalArgumentException(UPDATE_METHOD_WORKING_DAY + " is missing"));
	}

	@Test
	final void executeList() {
		final StateUpdateSeriviceImpl service = Mockito.mock(StateUpdateSeriviceImpl.class);

		final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
		Mockito.doReturn(service).when(applicationContext).getBean(StateUpdateSeriviceImpl.class);

		final Method workingdayUpdateMethod = updateMethod(StateUpdateSeriviceImpl.class);

		final Map<String, Object> environment = new HashMap<>();
		environment.put(DAYS_ARG_NAME, "" + OFFSET_ARG_VALUE);

		serviceCommandlineRunner.execute(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(workingdayUpdateMethod, Arrays.asList(DAYS_ARG_NAME))), environment, applicationContext);

		Mockito.verify(service).updateWorkingday(OFFSET_ARG_VALUE);

	}

	@Test
	final void executeListWithResult() {
		final ArtistService service = Mockito.mock(ArtistService.class);
		Mockito.doReturn(METHOD_RESULT).when(service).updateWorkingday();

		final ApplicationContext applicationContext = Mockito.mock(ApplicationContext.class);
		Mockito.doReturn(service).when(applicationContext).getBean(ArtistService.class);

		final Method calculateScoreMethod = updateMethod(ArtistService.class);

		final Map<String, Object> environment = new HashMap<>();

		serviceCommandlineRunner.execute(Arrays.asList(new AbstractMap.SimpleImmutableEntry<>(calculateScoreMethod, Arrays.asList())), environment, applicationContext);

		Mockito.verify(service).updateWorkingday();

		assertEquals(1, environment.size());
		assertEquals(METHOD_RESULT, environment.get(SimpleServiceCommandlineRunnerImpl.RESULT_PREFIX + "0"));

	}

}

@Mains({ @Main(name = ServiceCommandlineRunnerTest.UPDATE_WORKINGDAY, parameters = { @MainParameter(name = ServiceCommandlineRunnerTest.PARAMETER_OFFSET_DAYS, desc = "", defaultValue = "0") }),

})
class MainDefinitionWithParameter {

}

@Mains(value = {})
class MainDefinitionWithParameterNoMain {

}

interface ArtistService {
	int updateWorkingday();
}
