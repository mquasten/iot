package de.mq.iot.state.support;

import java.time.LocalDateTime;


class BooleanStateImpl extends AbstractState<Boolean> {

	private boolean value = false; 
	
	BooleanStateImpl(long id, String name, LocalDateTime lastupdate) {
		super(id, name, lastupdate);
	}

	@Override
	public Boolean value() {
		return value;
	}

	@Override
	public void assign(final Boolean value) {
		this.value = value != null  ? value : false;
	}
	

	
	
	

}
