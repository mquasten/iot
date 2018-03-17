package de.mq.iot.state.support;

import java.util.Map;

public class StringStateConverterImpl extends AbstractStateConverter<String> {

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
