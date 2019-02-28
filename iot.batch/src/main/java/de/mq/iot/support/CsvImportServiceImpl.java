package de.mq.iot.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.SynonymService;

public class CsvImportServiceImpl {
	
	private final Function<String, BufferedReader> supplier = name -> newReaderr(Paths.get(name));
	
	private final ConversionService conversionService;
	
	
	private final Map<CsvType,Consumer<Object>> consumers = new HashMap<>();
	
	CsvImportServiceImpl(final ConversionService conversionService, final SynonymService synonymService) {
		this.conversionService = conversionService;
		consumers.put(CsvType.Synonym, synonym -> synonymService.save((Synonym) synonym));
	}

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
			parse(reader,type);
		} catch (IOException io) {
			throw new IllegalStateException("Error reading file: " + fileName, io);
		}
		
	}

	private void parse(BufferedReader reader, CsvType type) throws IOException {
		final Collection<String> fieldNames = type.fields().stream().map(Field::getName).collect(Collectors.toList());
		
		
		try (final CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withIgnoreEmptyLines().withQuoteMode(QuoteMode.MINIMAL).withIgnoreSurroundingSpaces().withFirstRecordAsHeader().withDelimiter(';')) ) {
		
			Assert.notNull(parser.getHeaderMap() , "Header is missing");
			
			Assert.isTrue(parser.getHeaderMap().keySet().containsAll(fieldNames), String.format("Header did not match to type expected: %s, found:  %s" , StringUtils.collectionToCommaDelimitedString(fieldNames), StringUtils.collectionToCommaDelimitedString(parser.getHeaderMap().keySet())));
			
			Assert.isTrue(consumers.containsKey(type), String.format("No persistence defined for type: %s", type));
			parser.getRecords().stream().map(record -> newEntity(type, fieldNames, record)).collect(Collectors.toList()).forEach(entity -> consumers.get(type).accept(entity));
			
			
		}
	}

	private Object newEntity(CsvType type, final Collection<String> fieldNames, CSVRecord record) {
		final Object entity = BeanUtils.instantiateClass(type.target());
		fieldNames.forEach(name -> {
			
			final Field field = ReflectionUtils.findField(type.target(), name);
			
			final Object value = conversionService.convert(record.get(name), field.getType());
			field.setAccessible(true);
			ReflectionUtils.setField(field, entity, value);
			
		});
		return entity;
	}

}
