package de.mq.iot.model;

import java.util.Arrays;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.util.ReflectionUtils;

import com.vaadin.flow.component.HasText;

public interface LocalizeView {
	default void localize(final MessageSource messageSource, final Locale locale) {
		final String prefix =  this.getClass().isAnnotationPresent(I18NKey.class) ? this.getClass().getAnnotation(I18NKey.class).value() :  this.getClass().getSimpleName().toLowerCase()+ "_" ; 
		Arrays.asList(this.getClass().getDeclaredFields()).stream().filter(field -> HasText.class.isAssignableFrom(field.getType())).filter(field -> field.isAnnotationPresent(I18NKey.class)).forEach(field -> {
			field.setAccessible(true);
		
			final String key = prefix + field.getAnnotation(I18NKey.class).value();
			final HasText component = (HasText) ReflectionUtils.getField(field, this);
			component.setText(messageSource.getMessage(key, null, "???", locale));

		});
	}

}
