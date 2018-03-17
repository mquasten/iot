package de.mq.iot.state.support;

import java.time.LocalDateTime;

class StringStateImpl extends AbstractState<String> {

	static final String NULL_VALUE_CCU = "???";

	static final String DEFAULT_VALUE = "";

	private String value=DEFAULT_VALUE;
	
	StringStateImpl(final long id, final String name, final LocalDateTime lastupdate) {
		super(id, name, lastupdate);
		assign(value -> value != null ? value.split("[\\s]").length == 1 : true );
	}

	
	
	@Override
	public String value() {
		return value;
		
	}

	@Override
	public void assign(final String value) {
		if( ! validate(value)) {
			throw new IllegalArgumentException("String should not contain whitespaces.");
		}
		this.value =  value == null ? DEFAULT_VALUE : value.equals(NULL_VALUE_CCU) ? DEFAULT_VALUE : value; 
	}

}
