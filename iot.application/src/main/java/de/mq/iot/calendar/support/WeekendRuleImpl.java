package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.AbstractMap.SimpleImmutableEntry;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;

public class WeekendRuleImpl extends AbstractSpecialdaysRule {
	
	

	WeekendRuleImpl() {
		super(3);
	}

	@Override
	final Optional<Entry<DayType, String>> execute(final Collection<Specialday> specialday, final LocalDate date) {
		return   specialday.stream().filter(day -> Specialday.Type.Weekend == day.type()).filter(day -> day.dayOfWeek()==date.getDayOfWeek())
		.map(day -> (Entry<DayType, String>) entry(date.getDayOfWeek())).findAny();
	
	}

	private SimpleImmutableEntry<DayType, String> entry(final DayOfWeek dayOfWeek) {
		return new AbstractMap.SimpleImmutableEntry<DayType, String>(DayType.NonWorkingDay, String.format(DAY_TYPE_INFO_FORMAT, Specialday.Type.Weekend, dayOfWeek));
	}

}
