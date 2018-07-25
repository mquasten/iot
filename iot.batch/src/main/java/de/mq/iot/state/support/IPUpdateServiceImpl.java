package de.mq.iot.state.support;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.resource.ResourceIdentifier;
import de.mq.iot.resource.ResourceIdentifier.ResourceType;
import de.mq.iot.resource.support.ResourceIdentifierRepository;
import reactor.core.publisher.Mono;

@Service 
class IPUpdateServiceImpl implements IPUpdateService {
	
	
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
		final Mono<ResourceIdentifier> mono =  resourceIdentifierRepository.findById(ResourceType.XmlApi);
		
		final ResourceIdentifier resourceIdentifier  = mono.block(Duration.ofMillis(500)); 
		
		final String host = resourceIdentifier.parameters().get(HOST_PARAMETER_NAME);
		
		Assert.notNull(host, "Host is mandatory.");
		System.out.println("Existing host: " + host);
	}

}
