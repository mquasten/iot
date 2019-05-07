package de.mq.iot.support;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.rule.support.RulesDefinitionRepository;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.synonym.SynonymService;

@Service
public class CsvExportServiceImpl {

	private final Function<String, Writer> supplier = name -> newWriter(Paths.get(name));

	private Map<CsvType, Supplier<Collection<?>>> suppliers = new HashMap<>();

	private final ConversionService conversionService;

	@Autowired
	public CsvExportServiceImpl(final SynonymService synonymService, final AuthentificationService authentificationService, final SpecialdayService specialdayService, final ResourceIdentifierRepository resourceIdentifierRepository, final ConversionService conversionService, final RulesDefinitionRepository rulesDefinitionRepository) {
		suppliers.put(CsvType.Synonym, () -> synonymService.deviveSynonyms());
		suppliers.put(CsvType.User, () -> authentificationService.authentifications());
		suppliers.put(CsvType.Specialday, () -> specialdayService.specialdays());
		suppliers.put(CsvType.RulesDefinition, () -> rulesDefinitionRepository.findAll().collectList().block());
		suppliers.put(CsvType.ResourceIdentifier, () -> resourceIdentifierRepository.findAll().collectList().block());
		
		
		this.conversionService = conversionService;
	}

	Writer newWriter(final Path path) {
		try {
			return Files.newBufferedWriter(path);
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@Commands(commands = { @Command(name = "export", arguments = { "c", "f" }) })
	public void export(final String typeName, final String fileName) {

		final CsvType type = CsvType.valueOf(typeName);

		try (final Writer writer = supplier.apply(fileName);) {
			write(type, writer);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private void write(final CsvType type, final Writer writer) {
		final Collection<Field> fields = type.fields();

		Assert.isTrue(suppliers.containsKey(type), String.format("CsvType not supported: %s", type));
		final Collection<?> exportedObjects = suppliers.get(type).get();

		try (final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(fields.stream().map(Field::getName).collect(Collectors.toList()).toArray(new String[fields.size()])).withQuoteMode(QuoteMode.MINIMAL).withDelimiter(';'))) {
			process(fields, exportedObjects, csvPrinter);
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private void process(final Collection<Field> fields, final Collection<?> exportedObjects, final CSVPrinter csvPrinter) throws IOException {

		for (Object entity : exportedObjects) {
			final Collection<String> values = fields.stream().map(field -> {
				field.setAccessible(true);
				return ReflectionUtils.getField(field, entity);
			}).map(value -> convert(value)).collect(Collectors.toList());
			csvPrinter.printRecord(values.toArray(new Object[values.size()]));
		}
	}

	protected String convert(Object value) {
		
		if (value instanceof Map) {
			
		return ((Map<?,?>)value).entrySet()
                .stream()
                .map(e -> e.getKey() + "=" +  conversionService.convert(e.getValue(), String.class)  )
                .collect(Collectors.joining(","));

		}
		
		return conversionService.convert(value, String.class);
	}
	


}
