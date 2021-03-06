package de.mq.iot.rule.support;

import java.time.Duration;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot.rule.RulesDefinition;


@Service
abstract class RulesServiceImpl implements RulesService {
	
	private final RulesDefinitionRepository rulesDefinitionRepository;
	
	private Duration timeout ; 

	RulesServiceImpl(final RulesDefinitionRepository rulesDefinitionRepository, @Value("${mongo.timeout:500}") final Integer timeout) {
	
		this.rulesDefinitionRepository = rulesDefinitionRepository;
		this.timeout=Duration.ofMillis(timeout);
	} 
	
	
	/* (non-Javadoc)
	 * @see de.mq.iot.rule.support.RulesService#rulesAggregate(de.mq.iot.rule.RulesDefinition.Id)
	 */
	@Override
	public final RulesAggregate<?> rulesAggregate (final RulesDefinition.Id id, final Collection<Entry<String,String>>parameters) {
		final RulesDefinition rulesDefinition = rulesDefinitionRepository.findById(id).block(timeout);
		Assert.notNull(rulesDefinition, String.format("Ruledefinition not found in Database %s", id));
		parameters.stream().filter(entry -> StringUtils.hasText(entry.getValue())).forEach(parameter -> {
			Assert.isTrue(id.parameter().contains(parameter.getKey()), String.format("Invalid Parameter %s for %s", parameter.getKey(),id));
			rulesDefinition.assign(parameter.getKey(), parameter.getValue());
			
		});
		return rulesAggregate(rulesDefinition);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.rule.support.RulesService#rulesAggregate(de.mq.iot.rule.RulesDefinition)
	 */
	@Override
	public RulesAggregate<?> rulesAggregate(final RulesDefinition rulesDefinition) {
		Assert.notNull(rulesDefinition, "RulesDefinition is mandatory.");
		Assert.notNull(rulesDefinition.id(), "Id is mandatory");
		final RulesAggregate<?> result = DataAccessUtils.requiredSingleResult(rulesAggregates().stream().filter(rd -> rd.id() == rulesDefinition.id()).collect(Collectors.toList()));
		result.with(rulesDefinition);
		
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.rule.support.RulesService#rulesDefinitions()
	 */
	@Override
	public final Collection<RulesDefinition> rulesDefinitions(){
		return rulesDefinitionRepository.findAll().collectList().block(timeout);
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.rule.support.RulesService#save(de.mq.iot.rule.RulesDefinition)
	 */
	@Override
	public void save(RulesDefinition rulesDefinition) {		
		final Collection<String> toBeRemoved = rulesDefinition.inputData().entrySet().stream().filter(entry ->  !StringUtils.hasText(entry.getValue())).map(Entry::getKey).collect(Collectors.toSet());
	    toBeRemoved.forEach(key -> rulesDefinition.remove(key));
				
		rulesDefinitionRepository.save(rulesDefinition).block(timeout);
	}
	
	
	
	@Lookup("rulesAggregates")
	abstract Collection<RulesAggregate<?>> rulesAggregates();

}
