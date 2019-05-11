package de.mq.iot.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileCopyUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.authentication.support.AuthenticationRepository;
import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.rule.RulesDefinition;
import de.mq.iot.rule.support.RulesDefinitionImpl;
import de.mq.iot.rule.support.RulesDefinitionRepository;
import de.mq.iot.rule.support.TestRulesDefinition;
import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.Synonym.Type;
import de.mq.iot.synonym.SynonymService;
import reactor.core.publisher.Mono;

public class CsvImportServiceTest {
	
	private static final String FILENAME = "filename";

	private static final int TIMEOUT = 500;

	private static final LocalDate EASTER_2019 = LocalDate.of(2019, 4, 19);

	private final SynonymService synonymService = Mockito.mock(SynonymService.class);
	
	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	
	private final AuthenticationRepository authenticationRepository = Mockito.mock(AuthenticationRepository.class);
	
	private final RulesDefinitionRepository rulesDefinitionRepository = Mockito.mock(RulesDefinitionRepository.class);
	
	private final ResourceIdentifierRepository resourceIdentifierRepository = Mockito.mock(ResourceIdentifierRepository.class);
	private final CsvImportServiceImpl csvImportService = new CsvImportServiceImpl(new DefaultConversionService(), synonymService, specialdayService,authenticationRepository, resourceIdentifierRepository, rulesDefinitionRepository,TIMEOUT);
	
	private final String firstKey = "HMW-LC-Bl1-DR OEQ2305342:3";
	
	private final String firstValue = "Fenster";
	
	private final String secondKey = "HMW-LC-Bl1-DR NEQ1415509:3";
	
	private final String secondValue = "Fenster links";
	
	
	private final String firstId="00000000-0001-13f5-0000-000000000041";
	private final String dayOfMonth="25";
	private final String month="12";
	private final String offset="-2";
	private final String secondId="00000000-0416-625b-ffff-fffffffffffe";
	
	private final String user="mquasten";
	private final String password = "0b6bff8b997f50c48bfaea170eab7ce7";
	
	private final String XML_API_URI = "http://{host}:{port}/addons/xmlapi/{resource}";
	
	private final String XML_API_HOST_KEY = "host";
	private final String XML_API_HOST_VALUE =	"192.168.2.102";
	
	private final String XML_API_PORT_KEY = "port";
	private final String XML_API_PORT_VALUE =	"80";

	
	@BeforeEach
	void setup() {
		final Function<String, BufferedReader> supplier = name -> new BufferedReader(new StringReader("key;type;value;description\r\n" + 
				String.format("%s;Devive;%s;\r\n%s;Devive;%s;", firstKey, firstValue, secondKey, secondValue)));
		ReflectionTestUtils.setField(csvImportService, "supplier",  supplier);
	}
	
	@Test
	final void synonyms() {
		csvImportService.importCsv(CsvType.Synonym.name(), "egal");
		final ArgumentCaptor<Synonym> synonymsCaptor = ArgumentCaptor.forClass(Synonym.class);
		 Mockito.verify(synonymService, Mockito.times(2)).save(synonymsCaptor.capture());
		 assertEquals(2, synonymsCaptor.getAllValues().size());
		 
		 assertEquals(firstKey, synonymsCaptor.getAllValues().get(0).key());
		 assertEquals(Type.Devive, synonymsCaptor.getAllValues().get(0).type());
		 assertEquals(firstValue, synonymsCaptor.getAllValues().get(0).value());
		 assertNotNull( synonymsCaptor.getAllValues().get(0).description());
		 
		 
		 assertEquals(secondKey, synonymsCaptor.getAllValues().get(1).key());
		 assertEquals(Type.Devive, synonymsCaptor.getAllValues().get(1).type());
		 assertEquals(secondValue, synonymsCaptor.getAllValues().get(1).value());
		 assertNotNull( synonymsCaptor.getAllValues().get(1).description());
	}
	
	@Test
	final void specialDays() {
		final Function<String, BufferedReader> supplier = name -> new BufferedReader(new StringReader("id;type;offset;dayOfMonth;month;year\r\n" + 
				String.format("%s;Fix;0;%s;%s;\r\n%s;Gauss;%s;;;", firstId, dayOfMonth, month, secondId, offset)));
		ReflectionTestUtils.setField(csvImportService, "supplier",  supplier);
		
		csvImportService.importCsv(CsvType.Specialday.name(), "egal");
		final ArgumentCaptor<Specialday> specialdaysCaptor = ArgumentCaptor.forClass(Specialday.class);
		
		Mockito.verify(specialdayService, Mockito.times(2)).save(specialdaysCaptor.capture());
		assertEquals(2, specialdaysCaptor.getAllValues().size());
		
		final Specialday fixHoliday =  specialdaysCaptor.getAllValues().get(0);
		assertFalse(fixHoliday.isVacation());
		
		final LocalDate fixDate= fixHoliday.date(Year.now().getValue());
		assertEquals(Integer.valueOf(dayOfMonth).intValue(), fixDate.getDayOfMonth());
		assertEquals(Integer.valueOf(month).intValue(), fixDate.getMonthValue());
		assertEquals(firstId, ReflectionTestUtils.getField(fixHoliday, "id"));
		
		final Specialday offsetEasterHoliday =  specialdaysCaptor.getAllValues().get(1);
		
		assertFalse(offsetEasterHoliday.isVacation());
		
		final LocalDate fixDateOffsetEaster= offsetEasterHoliday.date(EASTER_2019.getYear());
		
		assertEquals(EASTER_2019, fixDateOffsetEaster);
		
	}
	
	
	@Test
	void authentication() {
		final Function<String, BufferedReader> supplier = name -> new BufferedReader(new StringReader("username;credentials;authorities\r\n" + 
				String.format("%s;%s;ModifySystemvariables\r\n", user, password)));
		ReflectionTestUtils.setField(csvImportService, "supplier",  supplier);
		@SuppressWarnings("unchecked")
		final Mono<Authentication> mono = Mockito.mock(Mono.class);
		Mockito.when(authenticationRepository.save(Mockito.any())).thenReturn(mono);
		
		csvImportService.importCsv(CsvType.User.name(), "egal");
		
		final ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);
		
		Mockito.verify(authenticationRepository, Mockito.times(1)).save(authenticationCaptor.capture());
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
		assertEquals(user, authenticationCaptor.getValue().username());
		assertEquals(1, authenticationCaptor.getValue().authorities().size());
		assertTrue(authenticationCaptor.getValue().authenticate("manfred01"));
		assertEquals(Authority.ModifySystemvariables.name(),  authenticationCaptor.getValue().authorities().stream().findAny().get().name());
		
		
	}
	@Test
	void resourceIdentifier() {
	
		
		
		final Function<String, BufferedReader> supplier = name -> new BufferedReader(new StringReader("id;uri;parameters\r\n" + 
				String.format("XmlApi;%s;%s=%s,%s=%s\r\n", XML_API_URI, XML_API_HOST_KEY, XML_API_HOST_VALUE , XML_API_PORT_KEY, XML_API_PORT_VALUE)));
		ReflectionTestUtils.setField(csvImportService, "supplier",  supplier);
		@SuppressWarnings("unchecked")
		final Mono<ResourceIdentifier> mono = Mockito.mock(Mono.class);
		Mockito.when(resourceIdentifierRepository.save(Mockito.any())).thenReturn(mono);
		csvImportService.importCsv(CsvType.ResourceIdentifier.name(), "egal");
		
		final ArgumentCaptor<ResourceIdentifier> resourceIdentifierCaptor = ArgumentCaptor.forClass(ResourceIdentifier.class);
		
		Mockito.verify(resourceIdentifierRepository, Mockito.times(1)).save(resourceIdentifierCaptor.capture());
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
		
		assertEquals(ResourceType.XmlApi, resourceIdentifierCaptor.getValue().id());
		assertEquals(XML_API_URI, resourceIdentifierCaptor.getValue().uri());
		
		final Map<String,String> parameters =  resourceIdentifierCaptor.getValue().parameters();
		assertEquals(2, parameters.size());
		
		assertEquals(XML_API_HOST_VALUE, parameters.get(XML_API_HOST_KEY));
		assertEquals(XML_API_PORT_VALUE, parameters.get(XML_API_PORT_KEY));
		
		
	}
	
	
	@Test
	void resourceIdentifierWrongMapalues() {
	
		
		
		final Function<String, BufferedReader> supplier = name -> new BufferedReader(new StringReader("id;uri;parameters\r\n" + 
				String.format("XmlApi;%s;%s,%s\r\n", XML_API_URI, XML_API_HOST_KEY , XML_API_PORT_KEY )));
		ReflectionTestUtils.setField(csvImportService, "supplier",  supplier);
		@SuppressWarnings("unchecked")
		final Mono<ResourceIdentifier> mono = Mockito.mock(Mono.class);
		Mockito.when(resourceIdentifierRepository.save(Mockito.any())).thenReturn(mono);
		csvImportService.importCsv(CsvType.ResourceIdentifier.name(), "egal");
		
		final ArgumentCaptor<ResourceIdentifier> resourceIdentifierCaptor = ArgumentCaptor.forClass(ResourceIdentifier.class);
		
		Mockito.verify(resourceIdentifierRepository, Mockito.times(1)).save(resourceIdentifierCaptor.capture());
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
		
		assertEquals(ResourceType.XmlApi, resourceIdentifierCaptor.getValue().id());
		assertEquals(XML_API_URI, resourceIdentifierCaptor.getValue().uri());
		
		final Map<String,String> parameters =  resourceIdentifierCaptor.getValue().parameters();
		assertEquals(0, parameters.size());
		
		
		
		
	}
	
	
	@Test
	void readerException() throws IOException {
		BufferedReader reader = Mockito.mock(BufferedReader.class);
		final Function<String, BufferedReader> supplier = name -> reader;
		
		
		
		Mockito.doThrow(IOException.class).when(reader).read(Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
		
		
		ReflectionTestUtils.setField(csvImportService, "supplier",  supplier);
	
		assertThrows(IllegalStateException.class, () ->  csvImportService.importCsv(CsvType.User.name(), "egal"));
	}
	
	@Test
	void newReaderException() {
		CsvImportServiceImpl csvImportService=new CsvImportServiceImpl(new DefaultConversionService(), synonymService, specialdayService, authenticationRepository, resourceIdentifierRepository, rulesDefinitionRepository, TIMEOUT);
		assertThrows(IllegalStateException.class, () ->  csvImportService.importCsv(CsvType.User.name(), "don'tLetMeGetMe"));
	}
	
	@Test
	void newReader() throws IOException {
		final Path path = Mockito.mock(Path.class);
		final FileSystem fileSystem = Mockito.mock(FileSystem.class);
		final FileSystemProvider provider = Mockito.mock(FileSystemProvider.class);
		Mockito.doReturn(provider).when(fileSystem).provider();
		Mockito.doReturn(fileSystem).when(path).getFileSystem();
		InputStream is = Mockito.mock(InputStream.class);
		Mockito.when(provider.newInputStream(Mockito.any(), Mockito.any())).thenReturn(is);
		try (final BufferedReader writer = ((CsvImportServiceImpl) csvImportService).newReader(path);) {

		}

		Mockito.verify(is).close();

	}
	
	
	
	@Test()
	void supplier() throws IOException {

		final CsvImportServiceImpl service = new CsvImportServiceImpl(new DefaultConversionService(), synonymService, specialdayService, authenticationRepository, resourceIdentifierRepository,rulesDefinitionRepository, TIMEOUT);

		@SuppressWarnings("unchecked")
		final Function<String, BufferedReader> function = (Function<String, BufferedReader>) DataAccessUtils
				.requiredSingleResult(Arrays.asList(CsvImportServiceImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Function.class)).map(field -> ReflectionTestUtils.getField(service, field.getName())).collect(Collectors.toList()));

		
		final File file =  File.createTempFile(FILENAME , ".csv");
		
		
		
		final String text = "ein test";
		FileCopyUtils.copy(text.getBytes(), file);
		
		try {
			assertEquals(text, function.apply(file.getPath()).readLine());
			
		} finally {

			clean(file.getPath());
		}

	}

	private void clean(String file) {
		try {
			
			Files.deleteIfExists(Paths.get(file));
		} catch (Exception ex) {

		}
	}
	
	
	@Test
	void rulesDefinition() {
		final Function<String, BufferedReader> supplier = name -> new BufferedReader(new StringReader("id;inputData;optionalRules\r\n" + 
				String.format("DefaultDailyIotBatch;workingdayAlarmTime=%s,minSunDownTime=%s,holidayAlarmTime=%s;temperatureRule\r\n", TestRulesDefinition.WORKINGDAY_ALARM_TIME, TestRulesDefinition.MIN_SUN_DOWN_TIME, TestRulesDefinition.HOLIDAY_ALARM_TIME)));
		ReflectionTestUtils.setField(csvImportService, "supplier",  supplier);
		@SuppressWarnings("unchecked")
		final Mono<RulesDefinition> mono = Mockito.mock(Mono.class);
		Mockito.when(rulesDefinitionRepository.save(Mockito.any())).thenReturn(mono);
		
		csvImportService.importCsv(CsvType.RulesDefinition.name(), "egal");
		
		final ArgumentCaptor<RulesDefinition> rulesDefinitionCaptor = ArgumentCaptor.forClass(RulesDefinition.class);
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
		Mockito.verify(rulesDefinitionRepository, Mockito.times(1)).save(rulesDefinitionCaptor.capture());
	
		assertEquals(RulesDefinition.Id.DefaultDailyIotBatch, rulesDefinitionCaptor.getValue().id());
		
		final Map<?,?> inputData = rulesDefinitionCaptor.getValue().inputData();
		assertEquals(3, inputData.size());
		assertEquals(TestRulesDefinition.HOLIDAY_ALARM_TIME, inputData.get(RulesDefinitionImpl.HOLIDAY_ALARM_TIME_KEY));
		assertEquals(TestRulesDefinition.WORKINGDAY_ALARM_TIME, inputData.get(RulesDefinitionImpl.WORKINGDAY_ALARM_TIME_KEY));
		assertEquals(TestRulesDefinition.MIN_SUN_DOWN_TIME, inputData.get(RulesDefinitionImpl.MIN_SUN_DOWN_TIME_KEY));
		
		assertEquals(1, rulesDefinitionCaptor.getValue().optionalRules().size());
		assertEquals(TestRulesDefinition.TEMPERATURE_RULE, rulesDefinitionCaptor.getValue().optionalRules().stream().findAny().get());
		
		
	}
}
