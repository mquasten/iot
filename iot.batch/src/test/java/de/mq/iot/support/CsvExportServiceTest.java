package de.mq.iot.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import de.mq.iot.calendar.Day;
import de.mq.iot.calendar.support.DayService;
import de.mq.iot.calendar.support.TestDays;
import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.resource.support.TestResourceIdentifier;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.support.RulesDefinitionImpl;
import de.mq.iot.rule.support.RulesDefinitionRepository;
import de.mq.iot.rule.support.TestRulesDefinition;
import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.SynonymService;
import de.mq.iot.synonym.support.TestSynonym;
import reactor.core.publisher.Flux;

class CsvExportServiceTest {

	private static final String DAY_GROUP_FIELD = "dayGroup";

	private static final String OFFSET_FIELD = "offset";

	private static final String ID_FIELD = "id";

	private static final String FILENAME = "filename.csv";

	private final SynonymService synonymService = Mockito.mock(SynonymService.class);

	private final AuthentificationService authentificationService = Mockito.mock(AuthentificationService.class);

	private final DayService specialdayService = Mockito.mock(DayService.class);

	private final ResourceIdentifierRepository resourceIdentifierRepository = Mockito.mock(ResourceIdentifierRepository.class);

	private final RulesDefinitionRepository rulesDefinitionRepository = Mockito.mock(RulesDefinitionRepository.class);
	

	private final CsvExportServiceImpl csvService = new CsvExportServiceImpl(synonymService, authentificationService, specialdayService, resourceIdentifierRepository, TestRulesDefinition.conversionService(), rulesDefinitionRepository);

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

		CsvType.Synonym.fields().stream().map(Field::getName).forEach(field -> assertTrue(map.containsKey(field)));

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
		CsvType.User.fields().stream().map(Field::getName).forEach(field -> assertTrue(map.containsKey(field)));

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

		Mockito.doThrow(RuntimeException.class).when(supplier).apply(Mockito.any());

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
		try (final Writer writer = ((CsvExportServiceImpl) csvService).newWriter(path);) {

		}

		Mockito.verify(os).close();

	}

	@Test
	void writerException() throws IOException {
		Path path = Mockito.mock(Path.class);
		Mockito.doThrow(RuntimeException.class).when(path).getFileSystem();
		assertThrows(IllegalStateException.class, () -> ((CsvExportServiceImpl) csvService).newWriter(path));
	}

	@Test
	void gaussDays() {
		final Day<LocalDate> specialday = TestDays.gaussDay();
		Mockito.when(specialdayService.days()).thenReturn(Arrays.asList(specialday));
		csvService.export("GaussDay", "export.csv");
		final List<List<String>> results = lines();

		assertEquals(3, results.get(0).size());
		assertEquals(3, results.get(1).size());

		final Map<String, String> map = new HashMap<>();
		IntStream.range(0, 3).forEach(i -> map.put(results.get(0).get(i).trim(), results.get(1).get(i).trim()));
		
		CsvType.GaussDay.fields().stream().map(Field::getName).forEach(field -> assertTrue(map.containsKey(field)));

		assertEquals(ReflectionTestUtils.getField(specialday, ID_FIELD), map.get(ID_FIELD));
		
		assertEquals("" +ReflectionTestUtils.getField(specialday, OFFSET_FIELD), map.get(OFFSET_FIELD));
		assertEquals(specialday.dayGroup().name(), map.get(DAY_GROUP_FIELD));
	
	}

	@Test
	void type() {

		CsvType.User.fields(Long.class).stream().forEach(field -> assertFalse(Modifier.isStatic(field.getModifiers())));

	}

	@Test()
	void supplier() {

		final CsvExportServiceImpl service = new CsvExportServiceImpl(synonymService, authentificationService, specialdayService, resourceIdentifierRepository, new DefaultConversionService(), null);

		@SuppressWarnings("unchecked")
		final Function<String, Writer> function = (Function<String, Writer>) DataAccessUtils
				.requiredSingleResult(Arrays.asList(CsvExportServiceImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Function.class)).map(field -> ReflectionTestUtils.getField(service, field.getName())).collect(Collectors.toList()));

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

	@Test()
	void resourceIdentifier() {
		final ResourceIdentifier resourceIdentifier = TestResourceIdentifier.resourceIdentifier();
		Mockito.when(resourceIdentifierRepository.findAll()).thenReturn(Flux.just(resourceIdentifier));
		csvService.export("ResourceIdentifier", "export.csv");

		final List<List<String>> results = lines();
		assertEquals(3, results.get(0).size());
		assertEquals(3, results.get(1).size());
		final Map<String, String> map = new HashMap<>();
		IntStream.range(0, 3).forEach(i -> map.put(results.get(0).get(i).trim(), results.get(1).get(i).trim()));
		assertEquals(ResourceType.XmlApi.name(), map.get(ID_FIELD));
		assertEquals(TestResourceIdentifier.URI, map.get("uri"));

		assertEquals(String.format("%s=%s,%s=%s", TestResourceIdentifier.HOST_KEY, TestResourceIdentifier.HOST_VALUE, TestResourceIdentifier.PORT_KEY, TestResourceIdentifier.PORT_VALUE), map.get("parameters"));
		;

	}

	@Test
	void rulesDefinition() {
		final RulesDefinition rulesDefinition = TestRulesDefinition.rulesDefinition();
		Mockito.when(rulesDefinitionRepository.findAll()).thenReturn(Flux.just(rulesDefinition));

		csvService.export("RulesDefinition", "export.csv");

		final List<List<String>> results = lines();
		assertEquals(2, results.size());

		assertEquals(3, results.get(0).size());
		assertEquals(3, results.get(1).size());

		final Map<String, String> map = new HashMap<>();
		IntStream.range(0, 3).forEach(i -> map.put(results.get(0).get(i).trim(), results.get(1).get(i).trim()));

		CsvType.RulesDefinition.fields().stream().map(Field::getName).forEach(field -> assertTrue(map.containsKey(field)));

		assertEquals(rulesDefinition.id().name(), map.get(ID_FIELD));

		final Map<String, String> inputData = new HashMap<>();
		Arrays.asList(map.get("inputData").split("[,]")).stream().forEach(line -> {
			String[] cols = line.split("[=]");
			assertTrue(cols.length == 2);
			inputData.put(cols[0], cols[1]);
		});

		assertEquals(3, inputData.size());
		assertEquals(TestRulesDefinition.WORKINGDAY_ALARM_TIME, inputData.get(RulesDefinitionImpl.WORKINGDAY_ALARM_TIME_KEY));
		assertEquals(TestRulesDefinition.HOLIDAY_ALARM_TIME, inputData.get(RulesDefinitionImpl.HOLIDAY_ALARM_TIME_KEY));
		assertEquals(TestRulesDefinition.MIN_SUN_DOWN_TIME, inputData.get(RulesDefinitionImpl.MIN_SUN_DOWN_TIME_KEY));

		assertEquals(TestRulesDefinition.TEMPERATURE_RULE, map.get("optionalRules"));

		
		
	}

}
