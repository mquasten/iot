package de.mq.iot.state.support;

import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

class ItemsStateConverterImpl extends AbstractStateConverter<Integer> {

	private static final String DELIMITER_PATTERN = "[;]";

	@Override
	public String key() {
		return "16";
	}

	@Override
	State<Integer> createState(final State<Integer> state, final Map<String, String> values) {

		if (StringUtils.hasText(values.get(StateConverter.KEY_VALUE_LIST))) {
			@SuppressWarnings("unchecked")
			final List<String> valueList = CollectionUtils.arrayToList(values.get(StateConverter.KEY_VALUE_LIST).split(DELIMITER_PATTERN));
			final Map<Integer, String> items = IntStream.range(0, valueList.size()).mapToObj(i -> new AbstractMap.SimpleImmutableEntry<>(i, valueList.get(i))).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
			Arrays.asList(state.getClass().getDeclaredFields()).stream().filter(field -> field.getType().equals(Map.class)).forEach(field -> assignItems(state, field, items));
		}

		state.assign(conversionService().convert(values.get(StateConverter.KEY_VALUE), Integer.class));

		return state;
	}

	private void assignItems(final State<Integer> state, final Field field, final Map<Integer, String> items) {
		field.setAccessible(true);
		ReflectionTestUtils.setField(state, field.getName(), items);
	}

	@Override
	Class<? extends State<Integer>> target() {
		return ItemsStateImpl.class;
	}

}
