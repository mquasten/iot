package de.mq.iot.model;

import java.util.Locale;

public interface LocaleAware {
	Locale locale();
	
	void assign(final Locale locale);

}
