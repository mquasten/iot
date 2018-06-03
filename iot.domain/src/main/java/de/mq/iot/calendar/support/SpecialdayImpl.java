package de.mq.iot.calendar.support;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

@Document( collection="Specialday")
class SpecialdayImpl {
	
	enum Type {
		Gauss,
		Fix
	}
	
	@Id
	private final String id ; 
	
	private final Type type;
	
	private final Integer offset;
	
	private final Integer dayOfMonth;
	private final Integer month;
	
	
	SpecialdayImpl() {
		this(0);
	}
	
	SpecialdayImpl(final int offset) {
		id= new UUID(Type.Gauss.name().hashCode(), Long.valueOf(offset)).toString();
		type=Type.Gauss;
		this.offset=offset;
		dayOfMonth=null;
		month=null;
	}
	
	SpecialdayImpl(final int month, final int dayOfMonth) {
		id= new UUID(Type.Fix.name().hashCode(), MonthDay.of(month, dayOfMonth).hashCode()).toString();
		type=Type.Fix;
		this.month=month;
		this.dayOfMonth=dayOfMonth;
		this.offset=null;
	}
	
	
	public LocalDate date(final int year) {
		validYearGuard(year);
		if(type == Type.Fix) {
			return LocalDate.of(year, month, dayOfMonth);
		}
		if( type == Type.Gauss) {
			return easterdate(year).plusDays(offset);
		}
		throw new IllegalArgumentException("Invalid type: " + type.name());
		
	}
	
	
	LocalDate easterdate(final int year) {
		validYearGuard(year);
		final int k = year / 100;
		final int m  = 15 + (3*k + 3) / 4 - (8*k + 13) / 25;
		final int s = 2 - (3*k + 3) / 4;
		final int a = year % 19; 
		final int d  = (19*a + m) % 30; 
		final int r = (d + a / 11) / 29;
		final int og = 21 + d - r;
		final int sz = 7 - (year + year / 4 + s) % 7;
		final int oe = 7 - (og - sz) % 7;
		final int daysFromFirstOfMarch = og+oe;
		return LocalDate.of(year, 3, 1).minusDays(1).plusDays(daysFromFirstOfMarch);
			
	}

	private void validYearGuard(final int year) {
		Assert.isTrue(year > 0 , "Year should be > 0.");
	}

}
