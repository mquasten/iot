package de.mq.iot.state.support;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
class BooleanStateConverterImpl  extends AbstractStateConverter<Boolean>{

	@Override
	public String key() {
		return "2";
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
