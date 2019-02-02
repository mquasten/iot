package de.mq.iot.synonym.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.mq.iot.support.ApplicationConfiguration;
import de.mq.iot.synonym.Synonym;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class })
@Disabled
class SynonymIntegrationTest {

	private static final String VALUE = "Name";
	private static final String KEY = "TestSynoym";
	@Autowired
	private SynonymRepository synonymRepository;
	
	
	@Test
	@Disabled
	final void save() {
		
		final Synonym synonym = new SynonymImpl(KEY, VALUE);
		
		synonymRepository.save(synonym).block();
		
		Collection<Synonym> results =  synonymRepository.findByType(Synonym.Type.Devive).collectList().block();
		
		assertEquals(1, results.size());
		
		assertEquals(synonym, results.stream().findAny().get());
		
		synonymRepository.deleteByKey(KEY).block();
		
		assertEquals(0, synonymRepository.findByType(Synonym.Type.Devive).collectList().block().size());
		
	}
	@Test
	@Disabled
	final void saveAll() {
		Collection<Synonym> synonyms = Arrays.asList(new SynonymImpl("HMW-LC-Bl1-DR OEQ2305342:3", "Fenster"), new SynonymImpl("HMW-LC-Bl1-DR NEQ1415509:3", "Fenster links"), new SynonymImpl("HMW-LC-Bl1-DR OEQ0281682:3", "Fenster rechts"),new SynonymImpl("HM-LC-Sw1-Pl-DN-R1 PEQ0088080:1", "Stehlampe"));
		synonyms.forEach(synonym -> synonymRepository.save(synonym).block() );
		
		
	}
	
}
