package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.stereotype.Component;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;

@Component
class SpecialWorkingDateRuleImpl extends AbstractSpecialdaysRule {
	
	

	SpecialWorkingDateRuleImpl() {
		super(1);
	}

	@Override
	final Optional<Entry<DayType, String>> execute(final Collection<Specialday> specialday, final LocalDate date) {
		return   specialday.stream().filter(day -> Specialday.Type.SpecialWorkingDate == day.type()).filter(day -> day.date(date.getYear()).equals(date))
		.map(day -> (Entry<DayType, String>) entry(date)).findAny();
	
	}

	private SimpleImmutableEntry<DayType, String> entry(final LocalDate date) {
		return new AbstractMap.SimpleImmutableEntry<DayType, String>(DayType.SpecialWorkingDay, String.format(DAY_TYPE_INFO_FORMAT, SpecialdayImpl.Type.SpecialWorkingDate, date));
	}

}
