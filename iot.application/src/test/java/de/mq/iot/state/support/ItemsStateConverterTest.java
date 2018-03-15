package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

class ItemsStateConverterTest {

	private static final String VALUE1 = "Value1";
	private static final String VALUE2 = "Value2";

	ItemsStateConverterImpl converter = new ItemsStateConverterImpl();

	private static final String ID = "4711";
	private static final String NAME = "ItemListValue";
	private static final String TIMESTAMP = "" + new Date().getTime() / 1000;

	private static final Integer VALUE = 1;

	@Test
	void key() {
		assertEquals("16", converter.key());
	}

	@Test
	void target() {
		assertEquals(ItemsStateImpl.class, converter.target());
	}

	@Test
	void convert() {
		final State<Integer> state = converter.convert(valuesMap());

		assertEquals(NAME, state.name());
		assertEquals(ID, "" + state.id());
		assertEquals(VALUE, state.value());
		assertEquals(expectedTime(), state.lastupdate());

		final List<Entry<Integer, String>> items = new ArrayList<>(((ItemsStateImpl) state).items());
		assertEquals(2, items.size());
		assertEquals(Integer.valueOf(0), items.get(0).getKey());
		assertEquals(VALUE1, items.get(0).getValue());

		assertEquals(Integer.valueOf(1), items.get(1).getKey());
		assertEquals(VALUE2, items.get(1).getValue());

	}

	@Test
	void convertNoItems() {
		final Map<String, String> valuesMap = valuesMap();
		valuesMap.remove(StateConverter.KEY_VALUE_LIST);

		assertThrows(IllegalArgumentException.class, () -> converter.convert(valuesMap));

	}

	private LocalDateTime expectedTime() {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(1000 * Long.valueOf(TIMESTAMP)), TimeZone.getDefault().toZoneId());
	}

	private Map<String, String> valuesMap() {
		final Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put(StateConverter.KEY_ID, ID);
		valuesMap.put(StateConverter.KEY_NAME, NAME);
		valuesMap.put(StateConverter.KEY_TIMESTAMP, TIMESTAMP);
		valuesMap.put(StateConverter.KEY_VALUE, "" + VALUE);
		valuesMap.put(StateConverter.KEY_TYPE, "16");
		valuesMap.put(StateConverter.KEY_VALUE_LIST, VALUE1 + ";" + VALUE2);
		return valuesMap;
	}
	
	@Test
	void validate() {
		final State<Integer> state = converter.convert(valuesMap());
		assertTrue(state.validate(0));
		assertTrue(state.validate(1));
		assertFalse(state.validate(2));
	}

}
