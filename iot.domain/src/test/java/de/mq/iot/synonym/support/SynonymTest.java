package de.mq.iot.synonym.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import de.mq.iot.synonym.Synonym;
import de.mq.iot.synonym.Synonym.Type;

public class SynonymTest {

	private static final String DESCRIPTION = "description";
	private static final String VALUE = "value";
	private static final String KEY = "key";
	private final Synonym synonym = new SynonymImpl(KEY, VALUE, Type.Devive, DESCRIPTION);

	@Test
	void key() {
		assertEquals(KEY, synonym.key());
	}

	@Test
	void value() {
		assertEquals(VALUE, synonym.value());
	}

	@Test
	void type() {
		assertEquals(Type.Devive, synonym.type());
	}

	@Test
	void description() {
		assertEquals(DESCRIPTION, synonym.description());
	}

	@Test
	void hash() {
		assertEquals(KEY.hashCode(), synonym.hashCode());
	}

	@Test
	void equals() {
		assertTrue(synonym.equals(new SynonymImpl(KEY, VALUE, Type.Devive, DESCRIPTION)));

		assertFalse(synonym.equals(new SynonymImpl(VALUE, VALUE, Type.Devive, DESCRIPTION)));

		assertFalse(synonym.equals(new Object()));
	}

	@Test
	void Constructor3Args() {
		final Synonym synonym = new SynonymImpl(KEY, VALUE, Type.Devive);

		assertEquals(KEY, synonym.key());
		assertEquals(VALUE, synonym.value());

		assertEquals(Type.Devive, synonym.type());
		assertNull(synonym.description());

	}

	@Test
	void Constructor2Args() {
		final Synonym synonym = new SynonymImpl(KEY, VALUE);

		assertEquals(KEY, synonym.key());
		assertEquals(VALUE, synonym.value());

		assertEquals(Type.Devive, synonym.type());
		assertNull(synonym.description());

	}

	@Test
	void Constructor() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		final Synonym synonym = BeanUtils.instantiateClass(SynonymImpl.class);
		assertNull(synonym.key());
		assertNull(synonym.value());
		assertNull(synonym.description());
	}
}
