package de.mq.iot.state;

public interface StateUpdateService {

	void updateWorkingday(final int offsetDays);

	void updateTime(final int offsetDays);

}