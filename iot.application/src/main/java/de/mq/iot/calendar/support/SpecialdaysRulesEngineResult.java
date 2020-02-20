package de.mq.iot.calendar.support;

import java.util.Optional;

import de.mq.iot.calendar.SpecialdayService.DayType;

public interface SpecialdaysRulesEngineResult {

	DayType dayType();

	String description();

	Optional<String> successRule();

}