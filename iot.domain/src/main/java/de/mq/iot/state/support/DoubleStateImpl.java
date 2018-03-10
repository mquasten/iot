package de.mq.iot.state.support;

import java.time.LocalDateTime;

class DoubleStateImpl extends AbstractState<Double> {

	private  double value=0d;
	
	DoubleStateImpl(final long id, final String name, final LocalDateTime lastupdate) {
		super(id, name, lastupdate);
	}

	

	@Override
	public Double value() {
		return value;
	}

	@Override
	public void assign(final Double value) {
		if( ! validate(value)) {
			throw new IllegalArgumentException("Value is invalid.");
		}
		
		this.value=value!=null?value:0d;
	}

}
