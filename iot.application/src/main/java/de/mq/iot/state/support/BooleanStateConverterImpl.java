package de.mq.iot.state.support;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import de.mq.iot.state.State;

@Component
class BooleanStateConverterImpl  extends AbstractStateConverter<Boolean>{

	@Autowired
	BooleanStateConverterImpl(final ConversionService conversionService) {
		super(conversionService);
	}

	static final String BOOLEN_STATE_TYPE = "2";

	@Override
	public String key() {
		return BOOLEN_STATE_TYPE;
	}

	@Override
	State<Boolean> createState(final State<Boolean> state, final Map<String,String> values) {
	
		final Boolean value = conversionService().convert(values.get(StateConverter.KEY_VALUE), Boolean.class);
		state.assign(value);
		return state;
	}

	@Override
	Class<? extends State<Boolean>> target() {
		return BooleanStateImpl.class;
	}
	
	

}
