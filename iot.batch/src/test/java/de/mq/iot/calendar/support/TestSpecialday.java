package de.mq.iot.calendar.support;

import java.time.LocalDate;

import de.mq.iot.calendar.Specialday;

public interface TestSpecialday {
	
	public static Specialday specialday() {
		return new SpecialdayImpl(LocalDate.now());
		
	}

}
