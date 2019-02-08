package de.mq.iot.support;

import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.authentication.AuthentificationService;
import de.mq.iot.synonym.SynonymService;

class CsvServiceTest {
	
	private final SynonymService synonymService = Mockito.mock(SynonymService.class);
	
	private final AuthentificationService authentificationService = Mockito.mock(AuthentificationService.class);
	
	private final CsvServiceImpl csvService = new CsvServiceImpl(synonymService,authentificationService, new DefaultConversionService());
	
	private final StringWriter writer = new StringWriter();
	
	@BeforeEach
	void setup() {
		
		ReflectionTestUtils.setField(csvService, "supplier" , (Function<String, Writer>) name -> writer);
	}
	
	@Test
	void export() {
		csvService.export("Synonym", "export.csv");
		
		System.out.println(writer.getBuffer());
	}
	

}
