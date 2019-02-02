package de.mq.iot.state.support;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.convert.ConversionService;
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
	
	private Map<DeviceType, Consumer<Object>> consumers = new HashMap<>();
	
	private Map<String, String> deviceSynonyms = new HashMap<>();
	
	
private Map<DeviceType, Function<Object,Object>> converters = new HashMap<>();
	
	
private final ConversionService conversionService;
	

	DeviceModelImpl(final Subject<Events, DeviceModel> subject, final ConversionService conversionService) {
		this.subject = subject;
		this.conversionService=conversionService;
		consumers.put(DeviceType.Level, val -> assignDouble( (String) val) ) ;
		consumers.put(DeviceType.State, val -> this.value=val ) ;
		
		
		converters.put(DeviceType.Level, value -> ""  + (int)  Math.round(((Double) value) * 100d) );
		converters.put(DeviceType.State, value -> value   );
		
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
	public Optional<Object> selectedDistinctSingleViewValue() {
		
		final List<?>   states = selectedDevices().stream().map(state -> (Object)  state.value()).distinct().limit(2).collect(Collectors.toList());
		if( states.size() != 1) {
			
			return Optional.empty();
		}
		
		converterAwareGuard();
		
		return Optional.of(converters.get(type).apply(states.get(0)));
		
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
		
		Assert.notNull(type, "DeviceType must be aware.");
		Assert.isTrue(consumers.containsKey(type), "Consumer undefined for" + type);
		
		consumers.get(this.type).accept(value);
		
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

	/*
	 * (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public String convert(final State<?> state) {
		converterAwareGuard();
		return conversionService.convert(converters.get(type).apply(state.value()), String.class);
	}

	private void converterAwareGuard() {
		Assert.notNull(type, "DeviceType must be aware.");
		Assert.isTrue(converters.containsKey(type), "Converter undefined for " + type);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.
	 * mq.iot.state.support.DeviceModel#changedValues()
	 */
	@Override
	public final Collection<State<Object>> changedValues() {
		final Collection<State<Object>> results = 	selectedDevices();
		results.forEach(state -> state.assign(value));
		return results;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#description(de.mq.iot.state.State)
	 */
	@Override
	public String synonym(final State<?> state) {
		final String function = state.function().isPresent() ? state.function().get() + ": " : "";
		final String name =  deviceSynonyms.containsKey(state.name()) ? deviceSynonyms.get(state.name()) : state.name();
		
		return function + name;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.state.support.DeviceModel#assign(java.util.Collection)
	 */
	@Override
	public void assign(final Collection<Entry<String, String>> deviceSynonyms) {
		this.deviceSynonyms.clear();
		this.deviceSynonyms.putAll(deviceSynonyms.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		
	}
	
	
}
