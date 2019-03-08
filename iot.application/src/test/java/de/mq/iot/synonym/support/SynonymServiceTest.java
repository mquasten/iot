package de.mq.iot.synonym.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.Synonym.Type;
import de.mq.iot.synonym.SynonymService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SynonymServiceTest {
	
	private static final int TIMEOUT = 500;

	private  final Synonym synonym = Mockito.mock(Synonym.class);

	private  final List<Synonym> synonyms = Arrays.asList(synonym);

	private final SynonymRepository synonymRepository = Mockito.mock(SynonymRepository.class);
	
	private final SynonymService synonymService = new SynonymServiceImpl(synonymRepository, TIMEOUT);
	
	
	@BeforeEach
	void SetupContext() {
		final Flux<Synonym> flux = Flux.fromStream(synonyms.stream());
		Mockito.when(synonymRepository.findByType(Type.Devive)).thenReturn(flux);
	}
	
	@Test
	void synonyms() {
		assertEquals(synonyms, synonymService.deviveSynonyms());
	}
	
	@Test
	void save() {
		@SuppressWarnings("unchecked")
		final Mono<Synonym> mono = Mockito.mock(Mono.class);
		Mockito.doReturn(mono).when(synonymRepository).save(synonym);
		synonymService.save(synonym);
		
		Mockito.verify(synonymRepository).save(synonym);
		Mockito.verify(mono).block(Duration.ofMillis(TIMEOUT));
	}

}
