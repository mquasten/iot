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

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;
import de.mq.iot.state.Room;
import de.mq.iot.state.State;

public class DeviceModelImpl implements DeviceModel {

	private final Subject<DeviceModel.Events, DeviceModel> subject;

	private final Map<String, Collection<State<Double>>> selectedDevices = new HashMap<>();

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
	public void assign(final Room room, Collection<State<Double>> selectedDevices) {
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
	public Collection<State<Double>> selectedDevices() {
		final Set<State<Double>> states = new HashSet<>();
		selectedDevices.values().forEach(devices4Room -> states.addAll(devices4Room));

		return Collections.unmodifiableSet(states);
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#selectedDistinctPercentValue()
	 */
	public Optional<Double> selectedDistinctSinglePercentValue() {
		
		final List<Double>  states = selectedDevices().stream().map(state -> state.value()).distinct().limit(2).collect(Collectors.toList());
		if( states.size() != 1) {
			
			return Optional.empty();
		}
		return Optional.of(100d * states.get(0));
		
	}
	@Override
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#isSelected()
	 */
	public boolean isSelected() {
		return selectedDevices().size()>0;
	}
}
