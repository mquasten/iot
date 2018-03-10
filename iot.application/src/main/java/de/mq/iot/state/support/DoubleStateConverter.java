package de.mq.iot.state.support;

import java.util.Map;

public class DoubleStateConverter extends AbstractStateConverter<Double> {

	@Override
	public String key() {
		return "4";
	}

	@Override
	State<Double> createState(State<Double> state , final Map<String,String> values) {
		state.assign(conversionService().convert(values, Double.class));
		return state;
	}

	@Override
	Class<? extends State<Double>> target() {
		return DoubleState.class;
	}

}
