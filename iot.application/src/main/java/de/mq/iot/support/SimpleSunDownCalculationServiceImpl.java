package de.mq.iot.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class SimpleSunDownCalculationServiceImpl implements SunDownCalculationService {
	
private final double  latitude;
	
	private final double longitude;
	
	private final double h = -50d/60d*Math.PI/180d;
	//private final double h = -6d*Math.PI/180d;
	
	SimpleSunDownCalculationServiceImpl(final double latitudeDegrees, final double longitudeDegrees) {
		this.latitude = latitudeDegrees*Math.PI/ 180d;
		this.longitude = longitudeDegrees;
	}
	
	@Autowired
	SimpleSunDownCalculationServiceImpl() {
		this(51.1423399, 6.2815922);
		
	}

	
	/* (non-Javadoc)
	 * @see de.mq.iot.support.SunDownCalculationService#sunDownTime(int, int)
	 */
	@Override
	public LocalTime sunDownTime(final int dayOfYear, final int timeZoneOffsetInHours) {
		return localTime(time(dayOfYear, timeZoneOffsetInHours, true));
	}
	
	
	
	private double time(final int dayOfYear, final int timeZoneOffsetInHours, boolean isDown ) {
		
		final double declination =0.4095*Math.sin(0.016906*(dayOfYear-80.086));
		
		
		final double timeDelta = 12 * Math.acos(( Math.sin(h)-Math.sin(latitude)*Math.sin(declination))/ (Math.cos(latitude)*Math.cos(declination)))/Math.PI;
		

		
		
		final double timeOffset = -0.171*Math.sin(0.0337*dayOfYear+0.465)-0.1299*Math.sin(0.01787*dayOfYear-0.168);
		
		if( isDown ) {
		return 12d + timeDelta-timeOffset-longitude/15+timeZoneOffsetInHours;
		} else {
			
			return 12d - timeDelta-timeOffset-longitude/15+timeZoneOffsetInHours;
		}
	//	System.out.println(">>>"+(int) result +":"  +Math.round(60 * (result % 1)) );
		
		
	//	LocalDate date = LocalDate.of(2018, Month.MARCH, 26);
		
		//return result;
		
	}
	
	@Override
	public final LocalTime sunDownTime(final Month month, final int timeZoneOffsetInHours ) {
		 return time(month, timeZoneOffsetInHours, true );
	}
	
	@Override
	public final LocalTime sunUpTime(final Month month, final int timeZoneOffsetInHours ) {
		 return time(month, timeZoneOffsetInHours, false );
	}
	
	
	private final LocalTime time(final Month month, final int timeZoneOffsetInHours, boolean isDown ) {
		LocalDate firstDay = LocalDate.of(Year.now().getValue(), month, 01);
		
		
		final int daysInMonth = firstDay.lengthOfMonth();
		
		final double result =   IntStream.rangeClosed(1, daysInMonth).mapToDouble(i -> {
	
			final LocalDate localDate = LocalDate.of(18, month, i);
			
			//System.out.println( localDate + "=" + localDate.getDayOfYear());
			
			return time(localDate.getDayOfYear(), timeZoneOffsetInHours, isDown);
			
			//return sunDownTime(localDate.getDayOfYear() , timeZoneOffsetInHours);
			
			
		}).sum()/daysInMonth;
		
	
		
		return localTime(result);
		
		
	}

	private LocalTime localTime(final double result) {
		int min = (int)  Math.round(60 * (result % 1));
		return LocalTime.of((int) result, (min  != 60) ?  min : 59);
	}

	@Override
	public LocalTime sunUpTime(int dayOfYear, int timeZoneOffsetInHours) {
	
		return localTime(time(dayOfYear, timeZoneOffsetInHours, false));
	}
		
		
		

	
	

}
