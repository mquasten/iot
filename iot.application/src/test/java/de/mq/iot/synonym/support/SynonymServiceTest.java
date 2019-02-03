package de.mq.iot.synonym.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.Synonym.Type;
import de.mq.iot.synonym.SynonymService;
import reactor.core.publisher.Flux;

public class SynonymServiceTest {
	
	private static final List<Synonym> synonyms = Arrays.asList(Mockito.mock(Synonym.class));

	private final SynonymRepository synonymRepository = Mockito.mock(SynonymRepository.class);
	
	private final SynonymService synonymService = new SynonymServiceImpl(synonymRepository, 500);
	
	
	@BeforeEach
	void SetupContext() {
		final Flux<Synonym> flux = Flux.fromStream(synonyms.stream());
		Mockito.when(synonymRepository.findByType(Type.Devive)).thenReturn(flux);
	}
	
	@Test
	void synonyms() {
		assertEquals(synonyms, synonymService.synonyms(Type.Devive));
	}
	
	
	

}
