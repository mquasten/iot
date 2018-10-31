package de.mq.iot.state.support;

import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import de.mq.iot.state.State;

@Component
class StringStateConverterImpl extends AbstractStateConverter<String> {

	StringStateConverterImpl(final ConversionService conversionService) {
		super(conversionService);
	}

	@Override
	public String key() {
		return "20";
	}

	@Override
	State<String> createState(final State<String> state, final Map<String, String> values) {
		state.assign(values.get(StateConverter.KEY_VALUE));
		return state;
	}

	@Override
	Class<? extends State<String>> target() {
		return StringStateImpl.class;
	}
	

}
