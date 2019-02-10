package de.mq.iot.synonym.support;

import java.util.UUID;

import de.mq.iot.synonym.Synonym;

public interface TestSynonym {
	
	public static  Synonym synonym() {
		return new SynonymImpl(UUID.randomUUID().toString(), "value" , Synonym.Type.Devive,  "description");
	}

}
