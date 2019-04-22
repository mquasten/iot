package de.mq.iot.state;

public interface StateUpdateService {

	void updateWorkingday(final int offsetDays);

	void updateTime(final int offsetDays);

	void updateTemperature(int offsetDays);

	void update();



	void processRules(final String name, final boolean update, final boolean test);

}