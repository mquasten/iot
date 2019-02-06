package de.mq.iot.support;

import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.synonym.SynonymService;

class CsvServiceTest {
	
	private SynonymService synonymService = Mockito.mock(SynonymService.class);
	
	private final CsvServiceImpl csvService = new CsvServiceImpl(synonymService);
	
	private StringWriter writer = new StringWriter();
	
	@BeforeEach
	void setup() {
		
		ReflectionTestUtils.setField(csvService, "supplier" , (Function<String, Writer>) name -> writer);
	}
	
	@Test
	void export() {
		csvService.export();
		
		System.out.println(writer.getBuffer());
	}
	

}
