package de.mq.iot.support;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

public class CsvImportServiceTest {
	
	private final CsvImportServiceImpl csvImportService = new CsvImportServiceImpl(new DefaultConversionService());
	
	private final Function<String, BufferedReader> supplier = name -> new BufferedReader(new StringReader("key;type;value;description\r\n" + 
			"HMW-LC-Bl1-DR OEQ2305342:3;Devive;Fenster;\r\n" + 
			"HMW-LC-Bl1-DR NEQ1415509:3;Devive;Fenster links;"));
	
	@BeforeEach
	void setup() {
		
		ReflectionTestUtils.setField(csvImportService, "supplier",  supplier);
	}
	
	@Test
	final void test() {
		csvImportService.importCsv(CsvType.Synonym.name(), "egal");
	}

}
