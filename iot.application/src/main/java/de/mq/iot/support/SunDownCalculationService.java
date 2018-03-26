package de.mq.iot.support;

interface SunDownCalculationService {

	double sunDownTime(int dayOfYear, int timeZoneOffsetInHours);

}