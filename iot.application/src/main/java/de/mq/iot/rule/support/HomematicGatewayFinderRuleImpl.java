package de.mq.iot.rule.support;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.temporal.ValueRange;
import java.util.Collection;
import java.util.Optional;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;

import de.mq.iot.state.StateService;


@Rule(name = "homematicGateWayFinderRule", priority = 1)
public class HomematicGatewayFinderRuleImpl {

	
	static final String NOT_FOUND_MESSAGE = "Homematic XmpApi not found on %s";
	static final String SUCCESS_MESSAGE_TEST = "Homematic XmpApi found ip: %s";
	static final String SUCCESS_MESSAGE = "Homematic XmpApi udated ip: %s";
	private  String dns = "8.8.8.8";
	
	
	
	static final String HOST_PARAMETER_NAME = "host";

	
	
	private final StateService stateService;
	HomematicGatewayFinderRuleImpl(final StateService stateService, final String dns) {
		this.stateService=stateService;
		this.dns=dns;
	}

	@Condition
	public boolean evaluate(@Fact(RulesAggregate.RULE_INPUT) final EndOfDayRuleInput ruleInput) {

		return ruleInput.valid();
	}

	@Action
	public void update(@Fact(RulesAggregate.RULE_INPUT) final EndOfDayRuleInput ruleInput , @Fact(RulesAggregate.RULE_OUTPUT_MAP_FACT) final Collection<String> results   ) {
		
		final String router = router();
		
		results.add(result(router, findHomematic( router, ruleInput.ipRange()), ruleInput.isTestMode()));
	   
	}

	private String result(final String router, final Optional<String> ip, final boolean testmode) {
		if( ip.isPresent()) {
			return  String.format(testmode?SUCCESS_MESSAGE_TEST:SUCCESS_MESSAGE , ip.get()) ;
		}else {
		     return String.format(NOT_FOUND_MESSAGE, router)
;
		}
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
	
	
	
private Optional<String> findHomematic(final String router, final ValueRange ipRange) {
		
	
	for(long i=ipRange.getMinimum();i <ipRange.getMaximum(); i++) {
			final String ip = router + "." + i;
			
			if( stateService.pingAndUpdateIp(ip,false) ) {
				return Optional.of(ip);
			}
			
			
		
			
		}
		return Optional.empty();
				
	}


}
