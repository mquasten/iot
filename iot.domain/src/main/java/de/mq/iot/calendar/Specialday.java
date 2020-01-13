package de.mq.iot.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;



public interface Specialday extends Comparable<Specialday> {

	public enum Type {
		Gauss(),
		Fix,
		Vacation(true),
		Weekend,
		SpecialWorkingDate(true),
		SpecialWorkingDay;
		
		
		private boolean withYear;
		
		Type(){
			withYear=false;
		}
		Type(final boolean withYear){
			this.withYear=withYear;
		}
		public final boolean isWithYear() {
			return withYear;
		}
		
	}
	
	
	public enum VariantSpecialDay {
		GoodFriday(-2),
		Easter(0),
		EasterMonday(1),
	 	Ascension(39),
		WhitMonday(50),
		CorpusChristi(60);
		private VariantSpecialDay(final int daysFromEasterDay) {
			this.daysFromEasterDay=daysFromEasterDay;
		}
		private final int daysFromEasterDay;
		
		public final  int daysFromEasterDay() {
			return daysFromEasterDay;
		}
	}

	
 	public enum FixedSpecialDay{
 	 	NewYear(MonthDay.of(1,1)),
 		LaborDay(MonthDay.of(5,1)) ,
 		GermanUnity(MonthDay.of(10,3)),
 		AllHallows(MonthDay.of(11,1)),
 		ChristmasDay(MonthDay.of(12,25)),
 		BoxingDay(MonthDay.of(12,26));
 		
 		private final MonthDay monthDay;
 		
 		private FixedSpecialDay(MonthDay monthDay) {
			this.monthDay=monthDay;
		}
		
		public final  MonthDay monthDay() {
			return monthDay;
		}
 		
 	}
	

	
	
	

	
	
	
	
	LocalDate date(final int year);










	boolean isVacation();










	DayOfWeek dayOfWeek();






}