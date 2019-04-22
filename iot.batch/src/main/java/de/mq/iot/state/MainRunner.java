package de.mq.iot.state;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

import de.mq.iot.state.support.SimpleServiceCommandlineRunnerImpl;
import de.mq.iot.support.ApplicationConfiguration;



@Mains({ @Main(name = "updateWorkingday", parameters = { @MainParameter(name = "d", desc = "Offset in days from current date", defaultValue = "0") }),
	
	@Main(name = "processRules", parameters = {  @MainParameter(name = "n", desc = "Name of the ruleengine") , @MainParameter(name = "u", hasArg=false, desc = "UpdateMode (use current day)") ,  @MainParameter(name = "t", hasArg=false, desc = "TestMode (without states update)")}) ,
	@Main(name = "updateCalendar", parameters = { @MainParameter(name = "d", desc = "Offset in days from current date", defaultValue = "0") }) ,
@Main(name = "updateTemperature", parameters = { @MainParameter(name = "d", desc = "Offset in days from current date", defaultValue = "0") }) ,
@Main(name = "updateAll", parameters = { }), 
@Main(name = "updateIP", parameters = { }) ,
@Main(name = "updateResources", parameters = { }), 
@Main(name = "export", parameters = { @MainParameter(name = "c", desc = "Class that should be exported"),  @MainParameter(name = "f", desc = "Filename (default: export.csv)", defaultValue="export.csv")}) 
})
public class MainRunner {
	private final Class<MainRunner> mainDefinitionClass = MainRunner.class;
	private final Class<?> configurationClass = ApplicationConfiguration.class;
	private static Class<? extends MainRunner> mainRunnerClass = MainRunner.class;
	private final SimpleServiceCommandlineRunnerImpl commandlineRunner = new SimpleServiceCommandlineRunnerImpl();

	public static void main(final String[] arguments) {
		final MainRunner mainRunner = BeanUtils.instantiateClass(mainRunnerClass);
		mainRunner.run(arguments).ifPresent(System::exit);

	}

	
	Optional<Integer> run(final String[] arguments) {

		final Map<String, Collection<MainParameter>> mainDefinitions = commandlineRunner.mainDefinitions(mainDefinitionClass);

		final Optional<String> cmd = Arrays.asList(arguments).stream().filter(arg -> mainDefinitions.containsKey(arg)).findFirst();
		if (!cmd.isPresent()) {
			System.err.println("Command not found expected values: " + StringUtils.collectionToCommaDelimitedString(mainDefinitions.keySet()));
			return Optional.of(1);
		}
		final Options options = new Options();
		mainDefinitions.get(cmd.get()).forEach(mainParameter -> options.addOption(mainParameter.name(), mainParameter.hasArg(), mainParameter.desc()));

		final Map<String, Object> argumentValues = new HashMap<>();
		final CommandLineParser parser = new DefaultParser();
		try {
			final CommandLine commandLine = parser.parse(options, arguments);
			if (commandLine.getArgs().length != 1) {
				return showHelpAndExitWithError(options, cmd.get());
			}
			addArgumentValues(mainDefinitions, cmd.get(), argumentValues, commandLine);

		} catch (final ParseException ex) {
			return showHelpAndExitWithError(options, cmd.get());
		}

		final Collection<String> emptyFields = argumentValues.entrySet().stream().filter(entry -> (entry.getValue() instanceof String)).filter(entry -> StringUtils.isEmpty((String) entry.getValue())).map(entry -> entry.getKey()).collect(Collectors.toList());
		if (emptyFields.size() > 0) {
			System.err.println("Mandatory Arguments missing: " + StringUtils.collectionToCommaDelimitedString(emptyFields) + "\n");
			;
			return showHelpAndExitWithError(options, cmd.get());
		}

		try  (final ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(configurationClass)) {
			final Collection<Entry<Method, Collection<String>>> methodEntries = commandlineRunner.servicesMethods(cmd.get());
			commandlineRunner.execute(methodEntries, argumentValues, applicationContext);
			System.out.println(cmd.get() + " finished ...");
			return Optional.of(0);
		}  catch( final Exception ex ) {
			ex.printStackTrace(System.err);
			return Optional.of(1);	
		}
		
		
		
	}

	private static Optional<Integer> showHelpAndExitWithError(final Options options, final String cmd) {
		final HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("MainRunner " + cmd, options);
		return Optional.of(1);
	}

	private static void addArgumentValues(final Map<String, Collection<MainParameter>> mainDefinitions, final String cmd, Map<String, Object> argumentValues, final CommandLine commandLine) {
		mainDefinitions.get(cmd).forEach(mainParameter -> {
			if (mainParameter.hasArg()) {
				final String value = StringUtils.hasText(commandLine.getOptionValue(mainParameter.name())) ? commandLine.getOptionValue(mainParameter.name()) : mainParameter.defaultValue();
				argumentValues.put(mainParameter.name(), value);
			} else {
				argumentValues.put(mainParameter.name(), commandLine.hasOption(mainParameter.name()));
			}

		});
	}

}
