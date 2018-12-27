package de.mq.iot.state.support;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.util.Assert;

import de.mq.iot.state.State;

abstract   class AbstractState<T> implements State<T> {

	private final long id; 
	private final String name;
	private final LocalDateTime lastupdate;
	private String function;
	
	private final Collection<Predicate<T>> validators = new ArrayList<>();; 
	
	
	AbstractState(final long id, final String name,   final LocalDateTime lastupdate) {
		Assert.hasText(name, "Name is mandatory.");
		Assert.notNull(lastupdate, "Name is mandatory.");
		Assert.isTrue(id > 0, "Id should be > 0." );
		this.id = id;
		this.name = name;
		this.lastupdate = lastupdate;
		
	}
	

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.domain.state.State#id()
	 */
	@Override
	public final long id() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.domain.state.State#name()
	 */
	@Override
	public final String name() {
		return name;
	}

	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.domain.state.State#lastupdate()
	 */
	@Override
	public final LocalDateTime lastupdate() {
		return lastupdate;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.State#validate(java.lang.Object)
	 */
	@Override
	public final boolean validate(T value ) {
		 return validators.stream().map(validator -> validator.test(value)).filter(v -> !v ).findAny().orElseGet(() -> true) ;
	}
	
	/**
	 * assign a validator 
	 * @param validator a validator to test if the value is within the range
	 */
	final void assign(final Predicate<T> validator) {
		validators.add(validator);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.State#assignFunction(java.lang.String)
	 */
	@Override
	public final void assignFunction(final String function) {
		Assert.notNull(function , "Function is required.");
		this.function=function;
	}


	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.State#function()
	 */
	@Override
	final public Optional<String> function() {
		return Optional.ofNullable(function);
	}
	
	
	
}