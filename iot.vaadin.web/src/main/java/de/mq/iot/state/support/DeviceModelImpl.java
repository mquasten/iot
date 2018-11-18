package de.mq.iot.state.support;

import java.util.Locale;

import de.mq.iot.model.Observer;
import de.mq.iot.model.Subject;

public class DeviceModelImpl implements DeviceModel{

	
	private final Subject<DeviceModel.Events, DeviceModel> subject;
	
	DeviceModelImpl(Subject<Events, DeviceModel> subject) {
		this.subject = subject;
	}

	@Override
	public Observer register(Events key, Observer observer) {
		return subject.register(key, observer);
	
	}

	@Override
	public void notifyObservers(final Events key) {
	  subject.notifyObservers(key);
	}

	@Override
	public Locale locale() {
		return Locale.GERMAN;
	}

}
