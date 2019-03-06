package de.mq.iot.support;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.Year;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;
import de.mq.iot.authentication.support.AuthenticationRepository;
import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.Synonym.Type;
import de.mq.iot.synonym.SynonymService;

public class CsvImportServiceTest {

	private static final LocalDate EASTER_2019 = LocalDate.of(2019, 4, 19);

	private final SynonymService synonymService = Mockito.mock(SynonymService.class);
	
	private final SpecialdayService specialdayService = Mockito.mock(SpecialdayService.class);
	
	private final AuthenticationRepository authenticationRepository = Mockito.mock(AuthenticationRepository.class);
	
	private final CsvImportServiceImpl csvImportService = new CsvImportServiceImpl(new DefaultConversionService(), synonymService, specialdayService,authenticationRepository);
	
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
		
		csvImportService.importCsv(CsvType.User.name(), "egal");
		
		final ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);
		
		Mockito.verify(authenticationRepository, Mockito.times(1)).save(authenticationCaptor.capture());
		
		assertEquals(user, authenticationCaptor.getValue().username());
		assertEquals(1, authenticationCaptor.getValue().authorities().size());
		assertTrue(authenticationCaptor.getValue().authenticate("manfred01"));
		assertEquals(Authority.ModifySystemvariables.name(),  authenticationCaptor.getValue().authorities().stream().findAny().get().name());
		
		
	}

}
