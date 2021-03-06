package de.mq.iot.state.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.mq.iot.state.State;

@Component
class DoubleStateConverterImpl extends AbstractStateConverter<Double> {

	static final List<String> DOUBLE_STATE_TYPES = Arrays.asList("4", "LEVEL");

	@Autowired
	DoubleStateConverterImpl(ConversionService conversionService) {
		super(conversionService);
	}

	@Override
	public Collection<String> keys() {
		return DOUBLE_STATE_TYPES;
	}

	@Override
	State<Double> createState(final State<Double> state , final Map<String,String> values) {
		state.assign(conversionService().convert(values.get(StateConverter.KEY_VALUE), Double.class));
		
		if( StringUtils.hasText(values.get(KEY_MIN))) {
			final Double min = conversionService().convert(values.get(KEY_MIN), Double.class);
			((AbstractState<Double>) state).assign(value ->  value >= min);
			setStateField(KEY_MIN, state, min);
		}
		
		if( StringUtils.hasText(values.get(KEY_MAX))) {
			final Double max = conversionService().convert(values.get(KEY_MAX), Double.class);
			((AbstractState<Double>) state).assign(value ->  value <= max);
			setStateField(KEY_MAX, state, max);
		}
		
		return state;
	}

	@Override
	Class<? extends State<Double>> target() {
		return DoubleStateImpl.class;
	}

}
