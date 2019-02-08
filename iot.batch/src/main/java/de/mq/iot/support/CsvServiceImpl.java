package de.mq.iot.support;



import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.state.Command;
import de.mq.iot.state.Commands;

import de.mq.iot.synonym.SynonymService;

@Service
public class CsvServiceImpl  {
	
	enum Type {
		Synonym("de.mq.iot.synonym.support.SynonymImpl"),
		User("de.mq.iot.authentication.support.UserAuthenticationImpl");
		
		private final Class<?> clazz;
	
		private final Collection<Field> fields; 
		private Type(final String clazz) {
			this.clazz=ClassUtils.resolveClassName(clazz, CsvServiceImpl.class.getClassLoader());
			fields=Arrays.asList(this.clazz.getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).filter(field -> BeanUtils.isSimpleValueType(field.getType())).collect(Collectors.toList());
		  
		}
		
		final Collection<Field> fields() {
			return Collections.unmodifiableCollection(fields);
		}
	}
	
	
	
	private Function<String, Writer> supplier = name -> newWriter(name);
	
	private Map<Type, Supplier<Collection<?>>> suppliers = new HashMap<>();

	private final ConversionService conversionService;
	
	@Autowired
	public CsvServiceImpl(final SynonymService synonymService, final AuthentificationService authentificationService, final ConversionService conversionService) {
		 suppliers.put(Type.Synonym, () -> synonymService.deviveSynonyms());
		 suppliers.put(Type.User, () -> authentificationService.authentifications());
		 this.conversionService=conversionService;
	}
	
	private Writer newWriter(String name)  {
		try {
			return Files.newBufferedWriter(Paths.get(name));
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	
	
	


	

	
	
	@Commands(commands = {  @Command( name = "export", arguments = {"c", "f"}) })
	public  void export(final String typeName, final String fileName )   {
		
		
		
		final Type type = Type.valueOf(typeName);
		
		try(final Writer writer = supplier.apply(fileName);) {
		write(type, writer);
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}
	}





	private void write(final Type type, final Writer writer)   {
		final Collection<Field>fields = type.fields();
		
		final Collection<?> exportedObjects = suppliers.get(type).get();
		
		try(final CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(fields.stream().map(Field::getName).collect(Collectors.toList()).toArray(new String[fields.size()])).withQuoteMode(QuoteMode.MINIMAL).withDelimiter(';'))) {
		
		
			exportedObjects.forEach( entity  -> print(csvPrinter, fields.stream().map(field -> { field.setAccessible(true); return ReflectionUtils.getField(field, entity);}).map(value -> conversionService.convert(value, String.class)).collect(Collectors.toList())));
		} catch (final IOException ex) {
			throw new IllegalStateException(ex);
		}
	}





	private void print(final CSVPrinter csvPrinter , final Collection<String> values) {
		
		
		try {
			csvPrinter.printRecord(values.toArray(new Object[values.size()]));
			
		} catch (final IOException e) {
			throw new IllegalStateException();
		}
	}





	
}
