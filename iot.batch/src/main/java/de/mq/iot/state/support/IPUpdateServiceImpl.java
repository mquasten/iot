package de.mq.iot.state.support;

import java.net.InetAddress;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import reactor.core.publisher.Mono;

@Service 
class IPUpdateServiceImpl implements IPUpdateService {
	
	
	private static final String HOMEMATIC_HOST = "HOMEMATIC-CCU2";
	private static final String HOST_PARAMETER_NAME = "host";
	private final ResourceIdentifierRepository resourceIdentifierRepository;
	
	IPUpdateServiceImpl(final ResourceIdentifierRepository resourceIdentifierRepository) {
		this.resourceIdentifierRepository = resourceIdentifierRepository;
	}

	
	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.IPUpdateService#update()
	 */
	@Override
	public final void update() {
		
		final  Map<String,String> ips = IntStream.range(100, 111).mapToObj(address -> toEntry(address)).filter(entry -> ! entry.getKey().startsWith("192")).collect(Collectors.toMap( Entry::getKey, Entry::getValue));
		
		if( ! ips.containsKey(HOMEMATIC_HOST) ) {
		   System.out.println("HOMEMATIC-CCU2 not found!");	
		   return;
		}
		
		System.out.println(HOMEMATIC_HOST +":" + ips.get(HOMEMATIC_HOST));
		
		final Mono<ResourceIdentifier> mono =  resourceIdentifierRepository.findById(ResourceType.XmlApi);
		
		final ResourceIdentifier resourceIdentifier  = mono.block(Duration.ofMillis(500)); 
		
		final String host = resourceIdentifier.parameters().get(HOST_PARAMETER_NAME);
		
		Assert.notNull(host, "Host is mandatory.");
		System.out.println("Existing host: " + host);
		
	
		
		
		
	
		
	}


	private Entry<String,String> toEntry(int address) {
		String host2 = "192.168.2." + address;
		try {

			return new AbstractMap.SimpleImmutableEntry<String,String>(InetAddress.getByName(host2).getHostName().split("[.]")[0].toUpperCase(), host2);
			
		
		} catch (final Exception e) {
			throw new IllegalStateException();
		}
	}


	
		
	
	
	
}
