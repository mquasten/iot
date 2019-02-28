package de.mq.iot.support;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.Synonym.Type;
import de.mq.iot.synonym.SynonymService;

public class CsvImportServiceTest {
	
	private final SynonymService synonymService = Mockito.mock(SynonymService.class);
	
	private final CsvImportServiceImpl csvImportService = new CsvImportServiceImpl(new DefaultConversionService(), synonymService);
	
	private final String firstKey = "HMW-LC-Bl1-DR OEQ2305342:3";
	
	private final String firstValue = "Fenster";
	
	private final String secondKey = "HMW-LC-Bl1-DR NEQ1415509:3";
	
	private final String secondValue = "Fenster links";
	
	private final Function<String, BufferedReader> supplier = name -> new BufferedReader(new StringReader("key;type;value;description\r\n" + 
			String.format("%s;Devive;%s;\r\n%s;Devive;%s;", firstKey, firstValue, secondKey, secondValue)));
	
	@BeforeEach
	void setup() {
		
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

}
