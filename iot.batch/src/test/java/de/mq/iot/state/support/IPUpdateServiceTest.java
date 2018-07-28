package de.mq.iot.state.support;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.mq.iot.resource.support.ResourceIdentifierRepository;
import de.mq.iot.state.IPUpdateService;

public class IPUpdateServiceTest {

	
	private final ResourceIdentifierRepository resourceIdentifierRepository = Mockito.mock(ResourceIdentifierRepository.class);
	
	private final IPUpdateService ipUpdateService =  Mockito.mock(IPUpdateServiceImpl.class, Mockito.CALLS_REAL_METHODS);
	
	@Test
	void toEntry() {
		final int address = 100;
		final Entry<String,String> result =  ((IPUpdateServiceImpl)ipUpdateService).toEntry(address);
		assertEquals(IPUpdateServiceImpl.IP_PREFIX + address, result.getValue());
		assertNotNull(result.getKey());
		
	}
	
	@Test
	void toEntryBadAddress() {
		assertThrows(IllegalStateException.class, () -> ((IPUpdateServiceImpl)ipUpdateService).toEntry(-1));
	}
	
	
	@Test
	@Ignore
	void update() {
		
		Mockito.doReturn(new AbstractMap.SimpleImmutableEntry<String, String>(IPUpdateServiceImpl.HOMEMATIC_HOST, IPUpdateServiceImpl.IP_PREFIX +100), new AbstractMap.SimpleImmutableEntry<String, String>("192", "192")).when((IPUpdateServiceImpl)ipUpdateService).toEntry(Mockito.anyInt());
		
		ipUpdateService.update();
	}
}
