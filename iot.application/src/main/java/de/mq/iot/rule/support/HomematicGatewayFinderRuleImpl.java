package de.mq.iot.rule.support;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collection;
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

	private static final int MAX_IP_COUNT = 154;
	static final String NOT_FOUND_MESSAGE = "Homematic XmpApi not found on %s";
	static final String SUCCESS_MESSAGE = "Homematic XmpApi found ip: %s";
	private  String dns = "8.8.8.8";
	private static final int DEFAULT_NUM_IPS = 10;
	static final String MAX_IP_COUNT_KEY = "maxIpCount";
	
	static final String HOST_PARAMETER_NAME = "host";
	private final ConversionService conversionService;

	private final int firstIp=100;
	
	private final StateService stateService;
	HomematicGatewayFinderRuleImpl(final StateService stateService, final ConversionService conversionService, final String dns) {
		this.stateService=stateService;
		this.conversionService = conversionService;
		this.dns=dns;
	}

	@Condition
	public boolean evaluate() {

		return true;
	}

	@Action
	public void update(@Fact(RulesAggregate.RULE_INPUT_MAP_FACT) final Map<String, String> ruleInputMap, @Fact(RulesAggregate.RULE_OUTPUT_MAP_FACT) final Collection<String> results   ) {
		
		final String router = router();
		
		results.add(result(router, findHomematic( router, firstIp,  maxIps(ruleInputMap.get(MAX_IP_COUNT_KEY)))));
	   
	}

	private String result(final String router, final Optional<String> ip) {
		if( ip.isPresent()) {
			return  String.format(SUCCESS_MESSAGE , ip.get()) ;
		}else {
		     return String.format(NOT_FOUND_MESSAGE, router)
;
		}
	}

	private int maxIps(final String maxIps) {

		if (!StringUtils.hasText(maxIps)) {
			return DEFAULT_NUM_IPS;
		}

		int result = conversionService.convert(maxIps, Integer.class);
		if (result <= 0) {
			return DEFAULT_NUM_IPS;
		}

		if (result > MAX_IP_COUNT) {
			return DEFAULT_NUM_IPS;
		}
		
		return result;
	}

	final String router() {
		try (final DatagramSocket socket = new DatagramSocket()) {
			socket.connect(InetAddress.getByName(dns), 10002);
			final String localhost = socket.getLocalAddress().getHostAddress();
			return localhost.replaceAll("[.][0-9]+$", "");
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}

	}
	
	
	
private Optional<String> findHomematic(final String router, final int firstIp, final int maxIps) {
		
	
	for(int i=firstIp;i <maxIps+firstIp; i++) {
			final String ip = router + "." + i;
			
			if( stateService.pingAndUpdateIp(ip,false) ) {
				return Optional.of(ip);
			}
			
			
		
			
		}
		return Optional.empty();
				
	}


}
