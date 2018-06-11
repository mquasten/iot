package de.mq.iot.state.support;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class ServiceCommandlineRunnerTest {
	

static final String SCORE_CALCULATION_MAIN = "scoreCalculation";
static final String WORKING_DAY_UPDATE_MAIN = "workingDayUpdate";
static final String NAME_PARAMETER = "n";
static final String ALGORITHM_PARAMETER = "a";
static final String ALGORITHM_PARAMETER_DEFAULT = "basicScore";
static final String DAY_PARAMETER = "d";
static final String DAY_PARAMETER_DEFAULT = "0";
	private final SimpleServiceCommandlineRunnerImpl serviceCommandlineRunner = new SimpleServiceCommandlineRunnerImpl(); 
	
	@Test
	void parseParameters() {
		final Map<String, Collection<MainParameter>>  mainsList = serviceCommandlineRunner.mainDefinitions(MainDefinitionWithParameter.class);
		
		assertEquals(2, mainsList.size());
		
		assertTrue(mainsList.containsKey(SCORE_CALCULATION_MAIN));
		assertTrue(mainsList.containsKey(WORKING_DAY_UPDATE_MAIN));
		
		
		
		final List<MainParameter> scoreCalculationParameters = new ArrayList<>( mainsList.get(SCORE_CALCULATION_MAIN));
		assertEquals(2, scoreCalculationParameters.size());
		assertEquals(NAME_PARAMETER, scoreCalculationParameters.get(0).name());
		assertEquals("", scoreCalculationParameters.get(0).defaultValue());
		assertEquals(ALGORITHM_PARAMETER, scoreCalculationParameters.get(1).name());
		assertEquals(ALGORITHM_PARAMETER_DEFAULT, scoreCalculationParameters.get(1).defaultValue());
		
		final List<MainParameter>workingDayParameters = new ArrayList<>( mainsList.get(WORKING_DAY_UPDATE_MAIN));
		assertEquals(1, workingDayParameters.size());
		assertEquals(DAY_PARAMETER, workingDayParameters.get(0).name());
		assertEquals(DAY_PARAMETER_DEFAULT, workingDayParameters.get(0).defaultValue());
	}

}




@Mains( {@Main(name =ServiceCommandlineRunnerTest.SCORE_CALCULATION_MAIN,  parameters = { @MainParameter(name = ServiceCommandlineRunnerTest.NAME_PARAMETER ) ,  @MainParameter(name = ServiceCommandlineRunnerTest.ALGORITHM_PARAMETER, defaultValue=ServiceCommandlineRunnerTest.ALGORITHM_PARAMETER_DEFAULT) }), 

@Main(name =ServiceCommandlineRunnerTest.WORKING_DAY_UPDATE_MAIN,  parameters = { @MainParameter(name = ServiceCommandlineRunnerTest.DAY_PARAMETER , defaultValue=ServiceCommandlineRunnerTest.DAY_PARAMETER_DEFAULT) })

})
class MainDefinitionWithParameter {

	

	

	

	

	

	

	
}
