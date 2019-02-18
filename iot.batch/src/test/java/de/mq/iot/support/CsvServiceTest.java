package de.mq.iot.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.time.LocalDate;
import java.time.Year;
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
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.authentication.support.TestAuthentication;
import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.calendar.support.TestSpecialday;
import de.mq.iot.support.CsvServiceImpl.Type;
import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.SynonymService;
import de.mq.iot.synonym.support.TestSynonym;

class CsvServiceTest {

	private static final String FILENAME = "filename.csv";

	private final SynonymService synonymService = Mockito.mock(SynonymService.class);

	private final AuthentificationService authentificationService = Mockito.mock(AuthentificationService.class);

	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);

	private final CsvServiceImpl csvService = new CsvServiceImpl(synonymService, authentificationService, specialdayService, new DefaultConversionService());

	private final StringWriter writer = new StringWriter();

	@BeforeEach
	void setup() {

		ReflectionTestUtils.setField(csvService, "supplier", (Function<String, Writer>) name -> writer);
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

		final Map<String, String> map = new HashMap<>();
		IntStream.range(0, 4).forEach(i -> map.put(results.get(0).get(i).trim(), results.get(1).get(i).trim()));

		Type.Synonym.fields().stream().map(Field::getName).forEach(field -> assertTrue(map.containsKey(field)));

		assertEquals(synonym.key(), map.get("key"));
		assertEquals(synonym.value(), map.get("value"));
		assertEquals(synonym.type().name(), map.get("type"));
		assertEquals(synonym.description(), map.get("description"));

	}

	@Test
	void authentication() {
		final Authentication authentication = TestAuthentication.authentication();
		Mockito.when(authentificationService.authentifications()).thenReturn(Arrays.asList(authentication));
		csvService.export("User", "export.csv");
		final List<List<String>> results = lines();

		assertEquals(3, results.get(0).size());

		assertEquals(3, results.get(1).size());
		final Map<String, String> map = new HashMap<>();
		IntStream.range(0, 3).forEach(i -> map.put(results.get(0).get(i).trim(), results.get(1).get(i).trim()));
		Type.User.fields().stream().map(Field::getName).forEach(field -> assertTrue(map.containsKey(field)));

		assertEquals(authentication.username(), map.get("username"));
		assertEquals(ReflectionTestUtils.getField(authentication, "credentials"), map.get("credentials"));
		assertEquals(StringUtils.collectionToCommaDelimitedString(authentication.authorities()), map.get("authorities"));

	}

	private List<List<String>> lines() {

		return Arrays.asList(writer.getBuffer().toString().split("\n")).stream().map(line -> Arrays.asList(line.split(";"))).collect(Collectors.toList());
	}

	@Test
	void exceptionSupplier() {
		final Function<?, ?> supplier = Mockito.mock(Function.class);
		ReflectionTestUtils.setField(csvService, "supplier", supplier);

		Mockito.doThrow(IOException.class).when(supplier).apply(Mockito.any());

		assertThrows(IllegalStateException.class, () -> csvService.export("Synonym", "export.csv"));
	}

	@Test
	void exceptionWriter() throws IOException {
		final Writer writer = Mockito.mock(Writer.class);
		ReflectionTestUtils.setField(csvService, "supplier", (Function<String, Writer>) name -> writer);

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
		try (final Writer writer = ((CsvServiceImpl) csvService).newWriter(path);) {

		}

		Mockito.verify(os).close();

	}

	@Test
	void writerException() throws IOException {
		Path path = Mockito.mock(Path.class);
		Mockito.doThrow(IOException.class).when(path).getFileSystem();
		assertThrows(IllegalStateException.class, () -> ((CsvServiceImpl) csvService).newWriter(path));
	}

	@Test
	void specialdays() {
		Specialday specialday = TestSpecialday.specialday();
		Mockito.when(specialdayService.specialdays()).thenReturn(Arrays.asList(specialday));
		csvService.export("Specialday", "export.csv");
		final List<List<String>> results = lines();

		assertEquals(6, results.get(0).size());
		assertEquals(6, results.get(1).size());

		final Map<String, String> map = new HashMap<>();
		IntStream.range(0, 6).forEach(i -> map.put(results.get(0).get(i).trim(), results.get(1).get(i).trim()));

		Type.Specialday.fields().stream().map(Field::getName).forEach(field -> assertTrue(map.containsKey(field)));

		assertEquals(ReflectionTestUtils.getField(specialday, "id"), map.get("id"));
		assertEquals("Vacation", map.get("type"));
		assertFalse(StringUtils.hasText(map.get("offset")));
		assertEquals(LocalDate.now().getDayOfMonth(), (int) Integer.valueOf(map.get("dayOfMonth")));
		assertEquals(LocalDate.now().getMonthValue(), (int) Integer.valueOf(map.get("month")));
		assertEquals(Year.now().getValue(), (int) Integer.valueOf(map.get("year")));
	}

	@Test
	void type() {

		Type.User.fields(Long.class).stream().forEach(field -> assertFalse(Modifier.isStatic(field.getModifiers())));

	}

	@Test()
	void supplier() {

		final CsvServiceImpl service = new CsvServiceImpl(synonymService, authentificationService, specialdayService, new DefaultConversionService());

		@SuppressWarnings("unchecked")
		final Function<String, Writer> function = (Function<String, Writer>) DataAccessUtils
				.requiredSingleResult(Arrays.asList(CsvServiceImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Function.class)).map(field -> ReflectionTestUtils.getField(service, field.getName())).collect(Collectors.toList()));

		final String property = "java.io.tmpdir";

		
		final String file = System.getProperty(property) + FILENAME;
		clean(file);
		try {
			function.apply(file);
			assertTrue(Paths.get(file).toFile().exists());
		} finally {

			clean(file);
		}

	}

	private void clean(String file) {
		try {
			Files.deleteIfExists(Paths.get(file));
		} catch (Exception ex) {

		}
	}

}
