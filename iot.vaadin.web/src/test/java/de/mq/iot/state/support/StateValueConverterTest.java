package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.test.util.ReflectionTestUtils;

import de.mq.iot.state.State;

public class StateValueConverterTest {

	private static final LocalDateTime LASTUPDATE = LocalDateTime.now();
	private static final String VARIABLE_NAME = "VariableName";
	private static final long ID = 4771L;
	private static final int WINTER_ITEM_KEY = 1;
	private static final int SUMMER_ITEM_KEY = 0;
	private static final String WINTER_ITEM_VALUE = "WINTER";
	private static final String SUMMER_ITEM_VALUE = "SUMMER";

	private final Converter<State<?>, String> converter = new StateValueConverterImpl(new DefaultConversionService());

	@Test
	final void convertDoubleState() {
		final State<Double> state = new DoubleStateImpl(ID, VARIABLE_NAME, LASTUPDATE);
		state.assign(47.11);

		assertEquals(String.valueOf(state.value()), converter.convert(state));
	}

	@Test
	final void convertStringState() {
		final State<String> state = new StringStateImpl(ID, VARIABLE_NAME, LASTUPDATE);
		state.assign("Kylie_is_nice");

		assertEquals(state.value(), converter.convert(state));
	}

	@Test
	final void convertBooleanState() {
		final State<Boolean> state = new BooleanStateImpl(ID, VARIABLE_NAME, LASTUPDATE);

		state.assign(true);
		assertEquals(Boolean.TRUE.toString(), converter.convert(state));

		state.assign(false);
		assertEquals(Boolean.FALSE.toString(), converter.convert(state));
	}

	@Test
	final void convertItemState() {
		final ItemsStateImpl itemState = new ItemsStateImpl(ID, VARIABLE_NAME, LASTUPDATE);
		@SuppressWarnings("unchecked")
		final Map<Integer, String> items = (Map<Integer, String>) DataAccessUtils
				.requiredSingleResult(Arrays.asList(ItemsStateImpl.class.getDeclaredFields()).stream().filter(field -> field.getType().equals(Map.class)).map(field -> ReflectionTestUtils.getField(itemState, field.getName())).collect(Collectors.toList()));
		items.put(SUMMER_ITEM_KEY, SUMMER_ITEM_VALUE);
		items.put(WINTER_ITEM_KEY, WINTER_ITEM_VALUE);

		itemState.assign(1);
		assertEquals(WINTER_ITEM_VALUE, converter.convert(itemState));

		itemState.assign(SUMMER_ITEM_KEY);
		assertEquals(SUMMER_ITEM_VALUE, converter.convert(itemState));
	}

}
