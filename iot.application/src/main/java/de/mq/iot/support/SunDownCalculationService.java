package de.mq.iot.support;

import java.time.Month;

interface SunDownCalculationService {

	double sunDownTime(int dayOfYear, int timeZoneOffsetInHours);
	
	 double sunDownTime( Month month, final int timeZoneOffsetInHours);

}