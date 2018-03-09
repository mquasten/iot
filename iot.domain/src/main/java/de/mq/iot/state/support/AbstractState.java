package de.mq.iot.state.support;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

abstract   class AbstractState<T> implements State<T> {

	private final long id; 
	private final String name;
	private final LocalDateTime lastupdate;
	
	
	
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



	
}