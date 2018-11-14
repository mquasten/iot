package de.mq.iot.state.support;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.state.Main;
import de.mq.iot.state.MainParameter;
import de.mq.iot.state.Mains;

public class SimpleServiceCommandlineRunnerImpl {

	static final String RESULT_PREFIX = "r";

	private static final String BASE_PACKAGE = "de.mq.iot";

	final ConversionService conversionService = new DefaultConversionService();

	public  Map<String, Collection<MainParameter>> mainDefinitions(final Class<?> mainDefinitionClass) {
		Assert.isTrue(mainDefinitionClass.isAnnotationPresent(Mains.class), "Mains annotation expected.");

		final List<Main> mainList = Arrays.asList(mainDefinitionClass.getAnnotation(Mains.class).value());
		Assert.isTrue(mainList.size() > 0, "At least one Main annotation should be in list");
		final Map<String, Collection<MainParameter>> mainsMap = new HashMap<>();
		mainList.forEach(main -> {
			mainsMap.put(main.name(), Arrays.asList(main.parameters()));

		});
		return mainsMap;

	}

	public  Collection<Entry<Method, Collection<String>>> servicesMethods(final String commandName) {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(Service.class));
		final Collection<BeanDefinition> beans = provider.findCandidateComponents(BASE_PACKAGE);
		final Map<Method, Command> methods = new HashMap<>();
		beans.stream().forEach(beanDefinition -> analyzeClass(commandName, methods, beanDefinition));

		return methods.entrySet().stream().sorted((e1, e2) -> (int) Math.signum(e1.getValue().order() - e2.getValue().order())).map(entry -> new AbstractMap.SimpleImmutableEntry<Method, Collection<String>>(entry.getKey(), Arrays.asList(entry.getValue().arguments()))).collect(Collectors.toList());
	}

	void analyzeClass(final String commandName, final Map<Method, Command> methods, BeanDefinition beanDefinition) {
		try {
			final Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
			Arrays.asList(clazz.getMethods()).stream().filter(method -> method.isAnnotationPresent(Commands.class)).forEach(method -> Arrays.asList(AnnotationUtils.findAnnotation(method, Commands.class).commands()).stream().filter(command -> command.name().equalsIgnoreCase(commandName)).forEach(command -> methods.put(method, command)));
		} catch (final ClassNotFoundException ex) {
			throw new IllegalStateException(ex);
		}
	}

	final Optional<Object> execute(final Entry<Method, Collection<String>> methodEntry, final Map<String, Object> environment, final Object bean) {

		final Class<?>[] types = methodEntry.getKey().getParameterTypes();
		final String[] names = methodEntry.getValue().toArray(new String[types.length]);

		Assert.isTrue(types.length == methodEntry.getValue().size(), String.format("Wrong number of Arguments: types %s assigned Values %s.", types.length, methodEntry.getValue().size()));

		final Object[] values = new Object[types.length];

		IntStream.range(0, types.length).forEach(i -> {
			values[i] = conversionService.convert(environment.get(names[i]), types[i]);
			Assert.notNull(values[i], String.format("Argument %s not found in Arguments.", names[i]));
		});

		methodEntry.getKey().setAccessible(true);
		return Optional.ofNullable(ReflectionUtils.invokeMethod(methodEntry.getKey(), bean, values));
	}

	public void execute(final Collection<Entry<Method, Collection<String>>> methodEntries, final Map<String, Object> environment, final ApplicationContext applicationContext) {
		final int[] counter = { 0 };
		
		methodEntries.forEach(entry ->{
		
			final Class<?> clazz = entry.getKey().getDeclaringClass();
			final Object bean = applicationContext.getBean(clazz);
			Assert.notNull(bean, String.format("Bean not found : %s ", clazz));
		
			final Optional<Object> result = execute(entry, environment, bean);
			result.ifPresent(r -> environment.put(RESULT_PREFIX + counter[0], result.get()));
		
			
			counter[0]++;
		});

	}

}