package de.mq.iot.domain.state;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

class BooleanStateImpl implements State<Boolean>{
	
	private final long id; 
	private final String name;
	private final LocalDateTime lastupdate;
	private boolean value; 
	
	BooleanStateImpl(final long id, final String name, boolean value,  final LocalDateTime lastupdate) {
		Assert.hasText(name, "Name is mandatory.");
		Assert.notNull(lastupdate, "Name is mandatory.");
		Assert.isTrue(id > 0, "Id should be > 0." );
		this.id = id;
		this.name = name;
		this.lastupdate = lastupdate;
		this.value = value;
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
	 * @see de.mq.iot.domain.state.State#value()
	 */
	@Override
	public final  Boolean value() {
		return value;
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
	 * @see de.mq.iot.domain.state.State#assign(java.lang.Object)
	 */
	@Override
	public void assign(final Boolean value) {
		Assert.notNull(lastupdate, "Name is mandatory.");
		this.value=value;
		
	}
	
	

}
