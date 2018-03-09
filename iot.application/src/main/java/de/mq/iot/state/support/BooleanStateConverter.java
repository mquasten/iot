package de.mq.iot.state.support;

import java.time.LocalDateTime;

class BooleanStateConverter  extends AbstractStateConverter<Boolean>{

	@Override
	public String key() {
		return "2";
	}

	@Override
	State<Boolean> createState(final Long id, final LocalDateTime lastupdate, final String name, final String  valueAsString) {
		final State<Boolean> state = new BooleanStateImpl(id, name, lastupdate);
		final Boolean value = conversionService().convert(valueAsString, Boolean.class);
		state.assign(value);
		return state;
	}

}
