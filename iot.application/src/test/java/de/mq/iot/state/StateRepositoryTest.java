package de.mq.iot.state;

import org.junit.jupiter.api.Test;

import com.mashape.unirest.http.exceptions.UnirestException;



class StateRepositoryTest {
	
	
	
	
	
	private final HomematicXmlApiStateRepositoryImpl stateRepository = new HomematicXmlApiStateRepositoryImpl();
	
	@Test
	void findStates() throws UnirestException {
	
		stateRepository.findStates();
	
	}

}


