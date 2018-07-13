package de.mq.iot.support;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

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
	public double sunDownTime(final int dayOfYear, final int timeZoneOffsetInHours) {
		
		final double declination =0.4095*Math.sin(0.016906*(dayOfYear-80.086));
		
		
		final double timeDelta = 12 * Math.acos(( Math.sin(h)-Math.sin(latitude)*Math.sin(declination))/ (Math.cos(latitude)*Math.cos(declination)))/Math.PI;
		

		
		
		final double timeOffset = -0.171*Math.sin(0.0337*dayOfYear+0.465)-0.1299*Math.sin(0.01787*dayOfYear-0.168);
		
		
		double result = 12d + timeDelta-timeOffset-longitude/15+timeZoneOffsetInHours;
	//	System.out.println(">>>"+(int) result +":"  +Math.round(60 * (result % 1)) );
		
		
	//	LocalDate date = LocalDate.of(2018, Month.MARCH, 26);
		
		return result;
		
	}
	
	
	public final LocalTime sunDownTime(final Month month, final int timeZoneOffsetInHours ) {
		LocalDate firstDay = LocalDate.of(18, month, 01);
		
		
		final int daysInMonth = firstDay.lengthOfMonth();
		
		final double result =   IntStream.rangeClosed(1, daysInMonth).mapToDouble(i -> {
	
			final LocalDate localDate = LocalDate.of(18, month, i);
			
			//System.out.println( localDate + "=" + localDate.getDayOfYear());
			
			
			
			return sunDownTime(localDate.getDayOfYear() , timeZoneOffsetInHours);
			
			
		}).sum()/daysInMonth;
				
		return LocalTime.of((int) result, (int)  Math.round(60 * (result % 1)));
		
		
	}
		
		
		

	
	

}
