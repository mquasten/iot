package de.mq.iot.state.support;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import de.mq.iot.state.StateUpdateService;
import de.mq.iot.support.ApplicationConfiguration;

@Mains( {@Main(name ="workingDayUpdate",  parameters = { @MainParameter(name = "d" , defaultValue="0") })})
public class StateMain {

	public final static void main(String[] args) {

		final CommandLineParser parser = new DefaultParser();

		final Option daysOption = Option.builder("d")
				
				.required(true)
				
				.hasArg(true)

				.desc("Offset in days")

				.build();
		final Options options = new Options();

		options.addOption(daysOption);
		Integer days = 0;

		try {
			final CommandLine cmd = parser.parse(options, args);

			days = Integer.parseInt(cmd.getOptionValue("d"));
		} catch (ParseException e) {
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("iot-batch", options);
			System.exit(1);
		}

		try (final ConfigurableApplicationContext applicationContext = new AnnotationConfigApplicationContext(ApplicationConfiguration.class)) {

			final StateUpdateService stateUpdateService = applicationContext.getBean(StateUpdateService.class);
			stateUpdateService.update(days);
			System.out.println("main...");
		}
	}

}
