package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.support.CsvServiceImpl.Type;
import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.SynonymService;
import de.mq.iot.synonym.support.TestSynonym;

class CsvServiceTest {
	
	private final SynonymService synonymService = Mockito.mock(SynonymService.class);
	
	private final AuthentificationService authentificationService = Mockito.mock(AuthentificationService.class);
	
	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	
	private final CsvServiceImpl csvService = new CsvServiceImpl(synonymService,authentificationService, specialdayService, new DefaultConversionService());
	
	private final StringWriter writer = new StringWriter();
	
	@BeforeEach
	void setup() {
		
		ReflectionTestUtils.setField(csvService, "supplier" , (Function<String, Writer>) name -> writer);
	}
	
	@Test
	void synonyms() {
		final Synonym synonym = TestSynonym.synonym();
		Mockito.when(synonymService.deviveSynonyms()).thenReturn(Arrays.asList(synonym));
		csvService.export("Synonym", "export.csv");
		
		final List<List<String>> results = lines();
		assertEquals(2, results.size());
		
		assertEquals(4, results.get(0).size());
		assertEquals(4, results.get(1).size());
		
		final Map<String,String> map = new HashMap<>();
		IntStream.range(0, 4).forEach(i -> map.put(results.get(0).get(i).trim(),results.get(1).get(i).trim() ));
		
		Type.Synonym.fields().stream().map(Field::getName).forEach(field -> map.containsKey(field));
		
		assertEquals(synonym.key(), map.get("key"));
		assertEquals(synonym.value(), map.get("value"));
		assertEquals(synonym.type().name(), map.get("type"));
		assertEquals(synonym.description(), map.get("description"));
		
	}

	private List<List<String>>  lines() {
		 return  Arrays.asList(writer.getBuffer().toString().split("\n")).stream().map(line -> Arrays.asList(line.split(";"))).collect(Collectors.toList());
	}
	
	@Test
	void exceptionSupplier() {
	final Function<?,?> supplier = Mockito.mock(Function.class);
	ReflectionTestUtils.setField(csvService, "supplier" , supplier);
	
	Mockito.doThrow(IOException.class).when(supplier).apply(Mockito.any());
	
	assertThrows(IllegalStateException.class,  () -> csvService.export("Synonym", "export.csv")); 
	}
	
	@Test
	void exceptionWriter() throws IOException {
		final Writer writer = Mockito.mock(Writer.class);
		ReflectionTestUtils.setField(csvService, "supplier" , (Function<String, Writer>) name -> writer);
		
		Mockito.doThrow(IOException.class).when(writer).append(Mockito.anyChar());
		
		assertThrows(IllegalStateException.class, () -> csvService.export("Synonym", "export.csv"));
	}
	@Test
	void writer() throws IOException {
		final Path path = Mockito.mock(Path.class);
		final FileSystem fileSystem = Mockito.mock(FileSystem.class);
		final FileSystemProvider provider = Mockito.mock(FileSystemProvider.class);
		Mockito.doReturn(provider).when(fileSystem).provider();
		Mockito.doReturn(fileSystem).when(path).getFileSystem();
		OutputStream os = Mockito.mock(OutputStream.class);
		Mockito.when(provider.newOutputStream(Mockito.any(), Mockito.any())).thenReturn(os);
		 try (final  Writer writer = ( (CsvServiceImpl) csvService).newWriter(path);) {
			 
		 }
		 
		 Mockito.verify(os).close();
		 
		
	}
	
	@Test
	void writerException() throws IOException {
		Path path = Mockito.mock(Path.class);
		Mockito.doThrow(IOException.class).when(path).getFileSystem();
		assertThrows(IllegalStateException.class, () -> ( (CsvServiceImpl) csvService).newWriter(path));
	}
	

}
