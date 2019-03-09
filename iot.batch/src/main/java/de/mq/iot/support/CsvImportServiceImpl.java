package de.mq.iot.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.support.AuthenticationRepository;
import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.SynonymService;

public class CsvImportServiceImpl {
	
	private final Function<String, BufferedReader> supplier = name -> newReader(Paths.get(name));
	
	private final ConversionService conversionService;
	
	
	private final Map<CsvType,Consumer<Object>> consumers = new HashMap<>();
	
	CsvImportServiceImpl(final ConversionService conversionService, final SynonymService synonymService,final SpecialdayService specialdayService, final AuthenticationRepository authenticationRepository, final ResourceIdentifierRepository resourceIdentifierRepository, @Value("${mongo.timeout:500}") final Integer timeout) {
		this.conversionService = conversionService;
		consumers.put(CsvType.Synonym, synonym -> synonymService.save((Synonym) synonym));
		
		consumers.put(CsvType.Specialday, specialday -> specialdayService.save((Specialday)specialday));
		
		consumers.put(CsvType.User, user -> authenticationRepository.save((Authentication) user).block(Duration.ofMillis(timeout)));
		
		consumers.put(CsvType.ResourceIdentifier, resourceIdentifier -> resourceIdentifierRepository.save((ResourceIdentifier) resourceIdentifier).block(Duration.ofMillis(timeout)) );
		
		
	}

	BufferedReader newReader(final Path path) {
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
			
			final Object value = convert(record, name, field);
			
			
			field.setAccessible(true);
			ReflectionUtils.setField(field, entity, value);
			
		});
		return entity;
	}

	private Object convert(CSVRecord record, String name, final Field field) {
		if (Collection.class.isAssignableFrom(field.getType())) {
			return conversionService.convert(Arrays.asList(StringUtils.commaDelimitedListToStringArray(record.get(name))), TypeDescriptor.collection(Collection.class, TypeDescriptor.valueOf(String.class)), TypeDescriptor.collection(Collection.class, TypeDescriptor.valueOf((Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0])));
		}
		
		
		if(Map.class.isAssignableFrom(field.getType())) {
			return StringUtils.commaDelimitedListToSet(record.get(name)).stream().map(str -> str.split("[=]")).filter(stringArray -> stringArray.length==2).map(array -> new SimpleEntry<>(array[0], array[1])).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}
		
		return  conversionService.convert(record.get(name), field.getType());
	}
	
	

}
