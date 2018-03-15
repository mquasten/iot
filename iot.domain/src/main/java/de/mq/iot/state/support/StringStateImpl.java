package de.mq.iot.state.support;

import java.time.LocalDateTime;

public class StringStateImpl extends AbstractState<String> {

	StringStateImpl(final long id, final String name, final LocalDateTime lastupdate) {
		super(id, name, lastupdate);
		assign(value -> value != null ? value.split("[\\s]").length == 1 : true );
	}

	private String value;
	
	@Override
	public String value() {
		return value == null ? "" : value.replaceFirst("^[?]{3, 3}$","");
		
	}

	@Override
	public void assign(final String value) {
		if( ! validate(value)) {
			throw new IllegalArgumentException("String should not containo whitespaces.");
		}
		
	}

}
