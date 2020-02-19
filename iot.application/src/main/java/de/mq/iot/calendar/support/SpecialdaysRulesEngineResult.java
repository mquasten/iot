package de.mq.iot.calendar.support;

import de.mq.iot.calendar.SpecialdayService.DayType;

public interface SpecialdaysRulesEngineResult {

	DayType dayType();

	String description();

}