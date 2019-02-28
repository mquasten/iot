package de.mq.iot.synonym.support;

import java.time.Duration;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.SynonymService;
import de.mq.iot.synonym.Synonym.Type;



	
	@Service
	public class SynonymServiceImpl implements SynonymService  {
		
		private final SynonymRepository synonymRepository;
		private final Duration duration;
		
		@Autowired
		SynonymServiceImpl(final SynonymRepository synonymRepository, @Value("${mongo.timeout:500}") final Integer timeout) {
			this.synonymRepository=synonymRepository;
			this.duration=Duration.ofMillis(timeout);
		}
		
		
		/* (non-Javadoc)
		 * @see de.mq.iot.synonym.support.SynonymService#synonyms(de.mq.iot.synonym.Synonym.Type)
		 */
		@Override
		public final Collection<Synonym> deviveSynonyms() {
			return synonymRepository.findByType(Type.Devive).collectList().block(duration);
			
		}
		/*
		 * (non-Javadoc)
		 * @see de.mq.iot.synonym.SynonymService#save(de.mq.iot.synonym.Synonym)
		 */
		@Override
		public final void save(final Synonym synonym) {
			synonymRepository.save(synonym).block(duration);
		}

}
