package de.mq.iot.calendar.support;

import java.time.LocalDate;

import org.springframework.util.Assert;

class GregorianEasterCalculatorImpl implements EasterCalculator {
	
	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.calendar.support.EasterCalculator#easterdate(int)
	 */
	@Override
	public LocalDate easterdate(final int year) {
		Assert.isTrue(year > 0 , "Year should be > 0.");
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

}
