package de.mq.iot.state.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import de.mq.iot.state.State;

@Component
class StringStateConverterImpl extends AbstractStateConverter<String> {

	static final List<String> STRING_STATE_TYPES = Arrays.asList("20");

	StringStateConverterImpl(final ConversionService conversionService) {
		super(conversionService);
	}

	@Override
	public Collection<String> keys() {
		return STRING_STATE_TYPES;
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
