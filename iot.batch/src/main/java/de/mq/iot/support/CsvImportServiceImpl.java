package de.mq.iot.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;

public class CsvImportServiceImpl {
	
	private final Function<String, BufferedReader> supplier = name -> newReaderr(Paths.get(name));
	
	BufferedReader newReaderr(final Path path) {
		try {
			return Files.newBufferedReader(path);
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	@Commands(commands = { @Command(name = "import", arguments = { "c", "f" }) })
	public void importCsv(final String typeName, final String fileName)  {

		final CsvType type = CsvType.valueOf(typeName);

		try (final BufferedReader reader = supplier.apply(fileName)) {
			parse(reader);
		} catch (IOException io) {
			throw new IllegalStateException("Error reading file: " + fileName, io);
		}
		
	}

	protected void parse(BufferedReader reader) throws IOException {
		try (final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT) ) {
			
			
		}
	}

}
