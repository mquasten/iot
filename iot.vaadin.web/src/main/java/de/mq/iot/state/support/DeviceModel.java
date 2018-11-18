package de.mq.iot.state.support;

import de.mq.iot.calendar.support.CalendarModel;
import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;

public interface DeviceModel  extends Subject<DeviceModel.Events, CalendarModel> , LocaleAware  {

	public enum Events {

		ChangeLocale;

	}
	
	

}
