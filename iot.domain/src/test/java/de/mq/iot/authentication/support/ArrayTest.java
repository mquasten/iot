package de.mq.iot.authentication.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class ArrayTest {

	@Test
	void splitt() throws NoSuchMethodException, SecurityException {
		final Long[] results = splitt("4711,4712,4713", Long.class);

		assertEquals(3, results.length);
		assertEquals(Long.valueOf(4711L), results[0]);
		assertEquals(Long.valueOf(4712L), results[1]);
		assertEquals(Long.valueOf(4713L), results[2]);
	}

	@Test
	void splittString() throws NoSuchMethodException, SecurityException {
		final String[] results = splitt("4711,4712,4713", String.class);

		assertEquals(3, results.length);
		assertEquals("4711", results[0]);
		assertEquals("4712", results[1]);
		assertEquals("4713", results[2]);
	}

	@Test
	void splittEmpty() {
		final Long[] results = splitt(" ", Long.class);

		assertEquals(0, results.length);
	}

	@Test
	void splittNull() {

		final Long[] results = splitt(null, Long.class);

		assertEquals(0, results.length);
	}

	@Test
	void toDelimitedString() {

		final String result = delimitedString(new Long[] { 4711L, 4712L, 4713L });

		assertEquals("4711,4712,4713", result);
	}

	@Test
	void toDelimitedStringEmpty() {

		final String result = delimitedString(new Long[] {});

		assertEquals("", result);
	}

	@Test
	void toDelimitedStringNull() {

		final String result = delimitedString(null);

		assertEquals("", result);
	}

	String delimitedString(final Object[] array) {
		return StringUtils.arrayToCommaDelimitedString(array);
	}

	@SuppressWarnings("unchecked")
	<T> T[] splitt(final String text, final Class<T> clazz) {

		final String[] values = StringUtils.delimitedListToStringArray(text != null ? text.trim() : "", ",");

		final Constructor<?> constructor = ClassUtils.getConstructorIfAvailable(clazz, String.class);
		Assert.notNull(constructor, "Constructor with String.class not found class: " + clazz);

		final T[] results = (T[]) Array.newInstance(clazz, values.length);

		IntStream.range(0, values.length).forEach(i -> results[i] = (T) BeanUtils.instantiateClass(constructor, values[i].trim()));

		return results;
	}

}
