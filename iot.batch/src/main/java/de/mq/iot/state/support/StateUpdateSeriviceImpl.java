package de.mq.iot.state.support;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import de.mq.iot.calendar.SpecialdayService;
import de.mq.iot.state.StateService;
import de.mq.iot.state.StateUpdateService;

@Service
class StateUpdateSeriviceImpl implements StateUpdateService {

	private final SpecialdayService specialdayService;
	private final StateService  stateService;
	
	@Autowired
	StateUpdateSeriviceImpl(final SpecialdayService specialdayService,final StateService  stateService) {
		
		this.specialdayService = specialdayService;
		this.stateService=stateService;
	}

	/* (non-Javadoc)
	 * @see de.mq.iot.state.support.StateUpdateService#update(int)
	 */
	@Override
	public void update(final int offsetDays) {
		final LocalDate localDate = LocalDate.now().plusDays(offsetDays);
		Assert.isTrue(offsetDays >=0 , "Offset days should be greather or equals 0." );
		
		@SuppressWarnings("unchecked")
		final State<Boolean> workingDayState = (State<Boolean>) stateService.states().stream().filter(state -> state.name().equals("Workingday")).findAny().orElseThrow(() -> new IllegalStateException("Workingday Sttae expected."));
		final boolean expectedWorkingDayStateValue = isWorkingsday(localDate);
		if( ! workingDayState.value().equals(expectedWorkingDayStateValue)) {
			System.out.println("update needed ...");
			workingDayState.assign(expectedWorkingDayStateValue);
			stateService.update(workingDayState);
			
			System.out.println("update workingday to:" + workingDayState.value() );
		}
		
		
		
	}

	private  boolean  isWorkingsday(final LocalDate date) {
		if ( Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(date.getDayOfWeek())) {
			return false;
		}
		
		final Collection<LocalDate> specialdates = specialdayService.specialdays(Year.from(date)).stream().map(specialday -> specialday.date(date.getYear())).collect(Collectors.toSet());
		if(specialdates.contains(date)) {
			return false;
		}
	    
		return true;
	}
	
}
