package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;
import de.mq.iot.state.StateService.DeviceType;

public class DeviceModelImpl implements DeviceModel {

	private final Subject<DeviceModel.Events, DeviceModel> subject;

	private final Map<String, Collection<State<Object>>> selectedDevices = new HashMap<>();
	
	
	private DeviceType type;
	
	private Object value;

	

	DeviceModelImpl(Subject<Events, DeviceModel> subject) {
		this.subject = subject;
	}

	@Override

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.model.Subject#register(java.lang.Object,
	 * de.mq.iot.model.Observer)
	 */
	public Observer register(Events key, Observer observer) {
		return subject.register(key, observer);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.model.Subject#notifyObservers(java.lang.Object)
	 */
	@Override
	public void notifyObservers(final Events key) {
		subject.notifyObservers(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.model.LocaleAware#locale()
	 */
	@Override
	public Locale locale() {
		return Locale.GERMAN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.support.DeviceModel#assign(de.mq.iot.state.Room,
	 * java.util.Collection)
	 */
	@Override
	public void assign(final Room room, Collection<State<Object>> selectedDevices) {
		Assert.notNull(room, "Room is required.");
		Assert.notNull(selectedDevices, "Should not be null.");
		this.selectedDevices.put(room.name(), selectedDevices);
		notifyObservers(DeviceModel.Events.SeclectionChanged);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mq.iot.state.support.DeviceModel#selectedDevices()
	 */
	@Override
	public Collection<State<Object>> selectedDevices() {
		final Set<State<Object>> states = new HashSet<>();
		selectedDevices.values().forEach(devices4Room -> states.addAll(devices4Room));

		return Collections.unmodifiableSet(states);
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#selectedDistinctPercentValue()
	 */
	public Optional<Object> selectedDistinctSinglePercentValue() {
		
		final List<?>   states = selectedDevices().stream().map(state -> (Object)  state.value()).distinct().limit(2).collect(Collectors.toList());
		if( states.size() != 1) {
			
			return Optional.empty();
		}
		return Optional.of(states.get(0));
		
	}
	@Override
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#isSelected()
	 */
	public boolean isSelected() {
		return selectedDevices().size()>0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#value()
	 */
	@Override
	public Optional<Object> value() {
		return Optional.ofNullable(value);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#assign(java.lang.String)
	 */
	@Override
	public void assign(final Object value) {
		this.value = null;
		
		if (value instanceof String) {
			assignDouble((String) value);
			
		}
		
		if( value instanceof Boolean) {
			this.value=value;
		}
		
		notifyObservers(DeviceModel.Events.ValueChanged);
	}

	private void assignDouble(final String value) {
		if( StringUtils.isEmpty(value) ) {
			
			return;
		}
		if(!value.matches("[0-9]{1,3}")) {
			
			return;
		}
		int result = Integer.parseInt(value);
		if( result > 100) {
			
			return; 
		}
		this.value=new Double(result/100d);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#assignType(java.lang.String)
	 */
	@Override
	public void assignType(final DeviceType type) {
		this.type=type;
		selectedDevices.clear();
		this.value = null;
		notifyObservers(DeviceModel.Events.SeclectionChanged);
		notifyObservers(DeviceModel.Events.TypeChanged);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#type()
	 */
	@Override
	public Optional<DeviceType> type() {
		return  Optional.ofNullable(type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#clearSelection()
	 */
	@Override
	public void clearSelection() {
		this.selectedDevices.clear();
		notifyObservers(DeviceModel.Events.SeclectionChanged);
	}
	
}
