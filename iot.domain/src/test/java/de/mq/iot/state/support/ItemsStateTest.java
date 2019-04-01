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

	private static final String INVALID_LABEL= "don'tLetMeGetMe";
	private static final String VALUE_1_LABEL = "Value1";
	private static final String VALUE_0_LABEL = "Value0";
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
		permittedValues.put(DEFAULT_VALUE, VALUE_0_LABEL);
		permittedValues.put(VALUE, VALUE_1_LABEL);
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
		assertEquals(VALUE_0_LABEL, items.get(DEFAULT_VALUE).getValue());
		assertEquals(VALUE, items.get(VALUE).getKey());
		assertEquals(VALUE_1_LABEL, items.get(VALUE).getValue());
	}
	
	@Test
	final void assignString() {
		setPermittedValues();
		assertEquals(DEFAULT_VALUE, state.value());
		
		state.assign(VALUE_1_LABEL.toLowerCase());
		
		assertEquals(VALUE, state.value());
	}
	
	@Test
	final void assignStringInvalid() {
		setPermittedValues();
		assertThrows(IllegalArgumentException.class, () -> state.assign(INVALID_LABEL));
	}
	
	@Test
	final void stringValue() {
		setPermittedValues();
		assertEquals(VALUE_0_LABEL, state.stringValue());
		
		state.assign(VALUE);
		
		assertEquals(VALUE_1_LABEL, state.stringValue());
	}
	
	@Test
	final void hasValue() {
		setPermittedValues();
		
		assertFalse(state.hasValue(null));
		
		assertFalse(state.hasValue(VALUE));
		
		state.assign(VALUE);
		assertTrue(state.hasValue(VALUE));
	}
	
	@Test
	final void hasLabel() {
		setPermittedValues();
		
		assertFalse(state.hasLabel(null));
		
		assertFalse(state.hasLabel(VALUE_1_LABEL));
		
		assertFalse(state.hasLabel(INVALID_LABEL));
		
		state.assign(VALUE);
		
		assertTrue(state.hasLabel(VALUE_1_LABEL));
	
		
	}

}
