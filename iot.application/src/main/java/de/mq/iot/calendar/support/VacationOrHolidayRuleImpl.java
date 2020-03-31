package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Collection;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.AbstractMap.SimpleImmutableEntry;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;


public class VacationOrHolidayRuleImpl extends AbstractSpecialdaysRule {

	private final Collection<Specialday.Type> types = Set.of( Specialday.Type.Fix,  Specialday.Type.Gauss ,  Specialday.Type.Vacation);
	
	VacationOrHolidayRuleImpl() {
		super(2);
	}

	
	
	@Override
	Optional<Entry<DayType, String>> execute(final Collection<Specialday> specialday, final LocalDate date) {
		return   specialday.stream().filter(day -> types.contains(day.type())).filter(day -> day.date(date.getYear()).equals(date))
				.map(day -> (Entry<DayType, String>) entry(date, day)).findAny();
			
	}
	
	private SimpleImmutableEntry<DayType, String> entry(final LocalDate date, Specialday day) {
		return new AbstractMap.SimpleImmutableEntry<DayType, String>(DayType.NonWorkingDay, String.format(DAY_TYPE_INFO_FORMAT, day.type() , date));
	}

}
