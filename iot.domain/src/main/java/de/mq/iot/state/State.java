package de.mq.iot.state;

import java.time.LocalDateTime;
import java.util.Optional;
/**
 * One state of a finite state machine.
 * @author Admin
 *
 * @param <T> type of the state, Boolean, Long, String etc.
 */
public interface State<T> {
	
	
	
	/**
	 * internal id ccu2
	 * Request Parameter ise_id.
	 * @return the ccu2 id 
	 */
	long id();
	
	/**
	 * The name of the state in ccu2.
	 * @return
	 */
	String name();
	
	/**
	 * The value of the state, Boolean, Long, String etc.
	 * @return the state value
	 */
	T value();
	
	/**
	 * Last changed date. 
	 * @return time last changed
	 */
	LocalDateTime lastupdate();
	
	/**
	 * Change the state value
	 * @param value the value to that the state should be changed
	 */
	void assign(T value);
	

	boolean validate(T value); 
	
	/**
	 * Function of a State
	 * @return States Function
	 */
	default Optional<String> function() {
		return Optional.empty();
		
	}
	/**
	 * Assign a function to a State
	 * @param function sttaes function
	 */
	void assignFunction(String function);

	
	/**
	 * check if the value is equals.
	 * @param value value that should be compared
	 * @return true if the value is equals the current state value otherwise false
	 */
	boolean hasValue(T value);

}
