package de.mq.iot.state.support;

import java.time.LocalDateTime;
import java.util.Optional;

class DoubleStateImpl extends AbstractState<Double> implements MinMaxRange {

	private  double value=0d;
	
	private Double min;
	
	
	private Double max; 
	
	@Override
	public Optional<Double> getMin() {
		return Optional.ofNullable(min);
	}


	@Override
	public Optional<Double> getMax() {
		return Optional.ofNullable(max);
	}

	
	
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
