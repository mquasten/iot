package de.mq.iot.state;



import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl  {
	
	
	
	

	
	@Commands(commands = { @Command(arguments = { "d" }, name = "updateWorkingday") })
	public void updateWorkingday(final int offsetDays) {

	}
	
	
	@Commands(commands = { @Command(arguments = { "d" }, name = "updateCalendar") })
	public void updateTime(final int offsetDays) {
	
		
	}
	
	@Commands(commands = { @Command(arguments = { "d" }, name = "updateTemperature") })
	public void updateTemperature(final int offsetDays) {

		
		
	}



	@Commands(commands = {  @Command( name = "updateAll", arguments = {}) })
	public void update() {
		
	}

	
	


}
