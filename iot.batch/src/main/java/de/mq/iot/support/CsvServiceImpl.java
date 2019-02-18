package de.mq.iot.support;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;
import de.mq.iot.synonym.SynonymService;

@Service
public class CsvServiceImpl {

	enum Type {
		Synonym("de.mq.iot.synonym.support.SynonymImpl"), 
		User("de.mq.iot.authentication.support.UserAuthenticationImpl", "authorities"), 
		Specialday("de.mq.iot.calendar.support.SpecialdayImpl");

		private final Class<?> clazz;
		
		
		private final Collection<Field> fields;

		private Type(final String clazz, final String ...nonSimpleFields) {
			this.clazz = ClassUtils.resolveClassName(clazz, CsvServiceImpl.class.getClassLoader());
			fields = fields(this.clazz, nonSimpleFields);
			
		}

		List<Field> fields(final Class<?> clazz, final String... nonSimpleFields) {
			return Arrays.asList(this.clazz.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).filter(field -> BeanUtils.isSimpleValueType(field.getType())|| Arrays.asList(nonSimpleFields).contains(field.getName())).collect(Collectors.toList());
		}

		final Collection<Field> fields() {
			return Collections.unmodifiableCollection(fields);
		}
	}

	private final Function<String, Writer> supplier = name -> newWriter(Paths.get(name));

	private Map<Type, Supplier<Collection<?>>> suppliers = new HashMap<>();

	private final ConversionService conversionService;

	@Autowired
	public CsvServiceImpl(final SynonymService synonymService, final AuthentificationService authentificationService, final SpecialdayService specialdayService, final ConversionService conversionService) {
		suppliers.put(Type.Synonym, () -> synonymService.deviveSynonyms());
		suppliers.put(Type.User, () -> authentificationService.authentifications());
		suppliers.put(Type.Specialday, () -> specialdayService.specialdays());
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

		final Type type = Type.valueOf(typeName);

		try (final Writer writer = supplier.apply(fileName);) {
			write(type, writer);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private void write(final Type type, final Writer writer) {
		final Collection<Field> fields = type.fields();

		Assert.isTrue(suppliers.containsKey(type), String.format("Type not supported: %s", type));
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
			}).map(value -> conversionService.convert(value, String.class)).collect(Collectors.toList());
			csvPrinter.printRecord(values.toArray(new Object[values.size()]));
		}
	}
	


}
