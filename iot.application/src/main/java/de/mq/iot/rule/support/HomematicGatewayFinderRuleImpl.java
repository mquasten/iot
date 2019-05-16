package de.mq.iot.rule.support;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import de.mq.iot.state.StateService;


@Rule(name = "homematicGateWayFinderRule", priority = 0)
public class HomematicGatewayFinderRuleImpl {

	private static final int DEFAULT_NUM_IPS = 10;
	private static final String MAX_IP_COUNT = "maxIpCount";
	
	static final String HOST_PARAMETER_NAME = "host";
	private final ConversionService conversionService;

	private final int firstIp=100;
	
	private final StateService stateService;
	HomematicGatewayFinderRuleImpl(final StateService stateService, final ConversionService conversionService) {
		this.stateService=stateService;
		this.conversionService = conversionService;
		
	}

	@Condition
	public boolean evaluate() {

		return true;
	}

	@Action
	public void mapping(@Fact(RulesAggregate.RULE_INPUT_MAP_FACT) final Map<String, String> ruleInputMap) {

		final int maxIps = maxIps(ruleInputMap.get(MAX_IP_COUNT));
		System.out.println(maxIps);
		
	
		
		final Optional<String> ip = findHomematic( router(), firstIp,  maxIps);
		if( ip.isPresent()) {
			System.out.println("Homematic XmpApi: " +ip.get() );
		}
	   
	}

	private int maxIps(final String days) {

		if (!StringUtils.hasText(days)) {
			return DEFAULT_NUM_IPS;
		}

		int result = conversionService.convert(days, Integer.class);
		if (result <= 0) {
			return DEFAULT_NUM_IPS;
		}

		if (result > 100) {
			return DEFAULT_NUM_IPS;
		}
		return result;
	}

	final String router() {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			final String localhost = socket.getLocalAddress().getHostAddress();
			return localhost.replaceAll("[.][0-9]+$", "");
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

	}
	
	
	
private Optional<String> findHomematic(final String router, final int firstIp, final int maxIps) {
		
		//final ResourceIdentifier result = new ResourceIdentifierImpl(resourceIdentifier.id(), resourceIdentifier.uri());
		//result.assign(resourceIdentifier.parameters());
		
	
	
	for(int i=firstIp;i <maxIps+firstIp; i++) {
			final String ip = router + "." + i;
			if( stateService.pingAndUpdateIp(ip) ) {
				return Optional.of(ip);
			}
			
			
		
			
		}
		return Optional.empty();
				
	}


}
