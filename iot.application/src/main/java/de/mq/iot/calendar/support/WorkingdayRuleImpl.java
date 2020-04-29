package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.stereotype.Component;

import de.mq.iot.calendar.Specialday;
import de.mq.iot.calendar.SpecialdayService.DayType;
@Component
public class WorkingdayRuleImpl extends AbstractSpecialdaysRule {

	WorkingdayRuleImpl() {
		super(9);
		
	}

	@Override
	Optional<Entry<DayType, String>> execute(Collection<Specialday> specialday, LocalDate date) {
		
		return Optional.of(new AbstractMap.SimpleImmutableEntry<DayType, String>(DayType.WorkingDay, String.format(DAY_TYPE_INFO_FORMAT, DayType.WorkingDay , date)));
	}

}
