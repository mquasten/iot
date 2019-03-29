package de.mq.iot.state.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ItemsStateTest {

	private static final String VALUE_1_LABLE = "Value1";
	private static final String VALUE_0_LABLE = "Value0";
	private static final Integer DEFAULT_VALUE = Integer.valueOf(0);
	private static final Integer VALUE = 1;
	private static final String NAME = "ItemState";
	private static final long ID = 19680528L;

	private final ItemsStateImpl state = new ItemsStateImpl(ID, NAME, LocalDateTime.now());

	@Test
	final void defaultValue() {
		assertEquals(DEFAULT_VALUE, state.value());
	}

	@Test
	final void assignInvalidValue() {
		assertThrows(IllegalArgumentException.class, () -> state.assign((int) Math.random() * 100));
	}

	@Test
	final void validate() {
		assertFalse(state.validate(VALUE));
		setPermittedValues();
		assertTrue(state.validate(VALUE));
	}

	private void setPermittedValues() {
		final Map<Integer, String> permittedValues = new HashMap<>();
		permittedValues.put(DEFAULT_VALUE, VALUE_0_LABLE);
		permittedValues.put(VALUE, VALUE_1_LABLE);
		ReflectionTestUtils.setField(state, "items", permittedValues);
	}

	@Test
	final void value() {
		setPermittedValues();
		assertEquals(DEFAULT_VALUE, state.value());

		state.assign(VALUE);
		assertEquals(VALUE, state.value());

		state.assign((Integer) null);
		assertEquals(DEFAULT_VALUE, state.value());
	}

	@Test
	final void items() {
		setPermittedValues();
		final List<Entry<Integer, String>> items = new ArrayList<>(state.items());
		assertEquals(2, items.size());

		assertEquals(DEFAULT_VALUE, items.get(DEFAULT_VALUE).getKey());
		assertEquals(VALUE_0_LABLE, items.get(DEFAULT_VALUE).getValue());
		assertEquals(VALUE, items.get(VALUE).getKey());
		assertEquals(VALUE_1_LABLE, items.get(VALUE).getValue());
	}
	
	@Test
	final void assignString() {
		setPermittedValues();
		assertEquals(DEFAULT_VALUE, state.value());
		
		state.assign(VALUE_1_LABLE.toLowerCase());
		
		assertEquals(VALUE, state.value());
	}
	
	@Test
	final void assignStringInvalid() {
		setPermittedValues();
		assertThrows(IllegalArgumentException.class, () -> state.assign("don'tLetMeGetMe"));
	}
	
	@Test
	final void stringValue() {
		setPermittedValues();
		assertEquals(VALUE_0_LABLE, state.stringValue());
		
		state.assign(VALUE);
		
		assertEquals(VALUE_1_LABLE, state.stringValue());
	}

}
