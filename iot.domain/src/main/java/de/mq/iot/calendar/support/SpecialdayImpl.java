package de.mq.iot.calendar.support;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import de.mq.iot.calendar.Specialday;

@Document( collection="Specialday")
class SpecialdayImpl implements Specialday {
	
	enum Type {
		Gauss,
		Fix,
		Vacation,
		Weekend,
		HomeOfficeDay;
	}
	
	@Id
	private final String id ; 
	
	private final Type type;
	
	private final Integer offset;
	
	private final Integer dayOfMonth;
	private final Integer month;
	private final Integer year;
	private final Integer dayOfWeek;
	
	
	
	SpecialdayImpl() {
		this(0);
	}
	
	SpecialdayImpl(final VariantSpecialDay variantSpecialDay) {
		this(variantSpecialDay.daysFromEasterDay());
	}
	
	SpecialdayImpl(final int offset) {
		id= new UUID(Type.Gauss.name().hashCode(), Long.valueOf(offset)).toString();
		type=Type.Gauss;
		this.offset=offset;
		dayOfMonth=null;
		month=null;
		year=null;
		dayOfWeek=null;
	}
	
	SpecialdayImpl(final FixedSpecialDay fixedSpecialDay) {
	
		this(fixedSpecialDay.monthDay());
	}
	
	SpecialdayImpl(final MonthDay monthDay) {
		id= new UUID(Type.Fix.name().hashCode(), monthDay.hashCode()).toString();
		type=Type.Fix;
		this.month=monthDay.getMonthValue();
		this.dayOfMonth=monthDay.getDayOfMonth();
		this.offset=null;
		this.year=null;
		dayOfWeek=null;
	}
	
	SpecialdayImpl(final LocalDate date){
		id= new UUID(Type.Fix.name().hashCode(), date.hashCode()).toString();
		type=Type.Vacation;
		this.month=date.getMonthValue();
		this.dayOfMonth=date.getDayOfMonth();
		this.offset=null;
		dayOfWeek=null;
		this.year=date.getYear();
	}
	
	SpecialdayImpl(final DayOfWeek dayOfWeek, final boolean isWeekend) {
		dayOfWeekMandatoryGuard(dayOfWeek);
		this.type=weekEndOrHomeOffice(isWeekend);
		id= new UUID(type.name().hashCode(), dayOfWeek.hashCode()).toString();
	
		this.month=null;
		this.dayOfMonth=null;
		this.offset=null;
		this.year=null;
		this.dayOfWeek=dayOfWeek.getValue();
	}
	
	SpecialdayImpl(final DayOfWeek dayOfWeek) {
		this(dayOfWeek, false);
	}

	private Type weekEndOrHomeOffice(final boolean isWeekend) {
		if(isWeekend) {
			return Type.Weekend;
		} 
	    return Type.HomeOfficeDay;
	
	}
	
	
	/* (non-Javadoc)
	 * @see de.mq.iot.calendar.support.Specialday#date(int)
	 */
	@Override
	public LocalDate date(final int year) {
		validYearGuard(year);
		
		if(type == Type.Fix) {
			return LocalDate.of(year, month, dayOfMonth);
		}
		if( type == Type.Gauss) {
			return easterdate(year).plusDays(offset);
		}
		if( type == Type.Vacation) {
			Assert.notNull(this.year , "Year is mandatory.");
			return  LocalDate.of(this.year, month, dayOfMonth);
		}
		throw new IllegalArgumentException("Invalid type: " + type);
		
	}
	@Override
	public final DayOfWeek dayOfWeek() {
		if (type == Type.Weekend) {
			dayOfWeekMandatoryGuard(dayOfWeek);
			return DayOfWeek.of(dayOfWeek);
		}
		if(type == Type.HomeOfficeDay) {
			dayOfWeekMandatoryGuard(dayOfWeek);
			return DayOfWeek.of(dayOfWeek);
		}
		throw new IllegalArgumentException("Invalid type: " + type);
	}

	private void dayOfWeekMandatoryGuard(final Object dayOfWeek) {
		Assert.notNull(dayOfWeek, "DayOfWeek is mandatory.");
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
	
	@Override
	public final boolean isVacation() {
		return type == Type.Vacation;
		
	}

}
