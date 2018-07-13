package de.mq.iot.support;

import java.time.LocalTime;
import java.time.Month;

public interface SunDownCalculationService {

	double sunDownTime(int dayOfYear, int timeZoneOffsetInHours);

	LocalTime sunDownTime(Month month, final int timeZoneOffsetInHours);

}