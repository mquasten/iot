package de.mq.iot.domain.state;

import java.time.ZonedDateTime;

class BooleanStateImpl implements State<Boolean>{

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.domain.state.State#id()
	 */
	@Override
	public final long id() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.domain.state.State#name()
	 */
	@Override
	public final String name() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.domain.state.State#value()
	 */
	@Override
	public final  Boolean value() {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.domain.state.State#lastupdate()
	 */
	@Override
	public final ZonedDateTime lastupdate() {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.domain.state.State#assign(java.lang.Object)
	 */
	@Override
	public void assign(Boolean value) {
		// TODO Auto-generated method stub
		
	}
	
	

}
