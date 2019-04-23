package de.mq.iot.support;

import java.time.LocalTime;
import java.time.Month;

public interface SunDownCalculationService {

	LocalTime sunDownTime(int dayOfYear, int timeZoneOffsetInHours);

	LocalTime sunDownTime(Month month, final int timeZoneOffsetInHours);

	LocalTime sunUpTime(int i, int j);

	LocalTime sunUpTime(Month month, int timeZoneOffsetInHours);

}