package de.mq.iot.rule.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

public interface ValidFieldValues {
	
	default boolean valid() {
		return ! Arrays.asList(getClass().getDeclaredFields()).stream().filter(field -> ! Modifier.isStatic(field.getModifiers())).filter(field -> ! field.isAnnotationPresent(Nullable.class)).filter(field -> isNull(this, field)).findAny().isPresent();
	
	}

	static boolean isNull(final Object object, final Field field) {
		field.setAccessible(true);
		return ReflectionUtils.getField(field, object)==null;
	}

}
