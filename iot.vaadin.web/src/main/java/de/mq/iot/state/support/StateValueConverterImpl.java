package de.mq.iot.state.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

class StateValueConverterImpl implements Converter<State<?>, String> {

	private final Map<Class<? extends State<?>>, Function<State<?>, String>> converters = new HashMap<>();

	private final ConversionService conversionService;

	StateValueConverterImpl(final ConversionService conversionService) {
		this.conversionService = conversionService;
		converters.put(ItemsStateImpl.class, state -> ((ItemsStateImpl) state).items().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)).get(state.value()));
	}

	@Override
	public String convert(final State<?> state) {
		return Optional.ofNullable(converters.get(state.getClass())).orElse(aState -> conversionService.convert(aState.value(), String.class)).apply(state);
	}

}
