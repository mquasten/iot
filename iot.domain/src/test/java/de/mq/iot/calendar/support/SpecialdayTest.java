package de.mq.iot.calendar.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpecialdayTest {
	
private Map<Integer,LocalDate> easterDates = new  HashMap<>();
	
	@BeforeEach
	void setup() {
		
		easterDates.put(2000, LocalDate.of(2000, 4, 23));
		easterDates.put(2001, LocalDate.of(2001, 4, 15));
		easterDates.put(2002, LocalDate.of(2002, 3, 31));
		easterDates.put(2003, LocalDate.of(2003, 4, 20));
		easterDates.put(2004, LocalDate.of(2004, 4, 11));
		easterDates.put(2005, LocalDate.of(2005, 3, 27));
		easterDates.put(2006, LocalDate.of(2006, 4, 16));
		easterDates.put(2007, LocalDate.of(2007, 4, 8));
		easterDates.put(2008, LocalDate.of(2008, 3,23));
		easterDates.put(2009, LocalDate.of(2009, 4,12));
		easterDates.put(2010, LocalDate.of(2010, 4,4));
		easterDates.put(2011, LocalDate.of(2011, 4,24));
		easterDates.put(2012, LocalDate.of(2012, 4,8));
		easterDates.put(2013, LocalDate.of(2013, 3,31));
		easterDates.put(2014, LocalDate.of(2014, 4,20));
		easterDates.put(2015, LocalDate.of(2015, 4,5));
		easterDates.put(2016, LocalDate.of(2016, 3,27));
		easterDates.put(2017, LocalDate.of(2017, 4,16));
		easterDates.put(2018, LocalDate.of(2018, 4,1));
		easterDates.put(2019, LocalDate.of(2019, 4,21));
		easterDates.put(2020, LocalDate.of(2020, 4,12));
		easterDates.put(2021, LocalDate.of(2021, 4,4));
		easterDates.put(2022, LocalDate.of(2022, 4,17));
		easterDates.put(2023, LocalDate.of(2023, 4,9));
		easterDates.put(2024, LocalDate.of(2024, 3,31));
		easterDates.put(2025, LocalDate.of(2025,4,20));
		easterDates.put(2026, LocalDate.of(2026,4,5));
		easterDates.put(2027, LocalDate.of(2027,3,28));
		easterDates.put(2028, LocalDate.of(2028,4,16));
		
		easterDates.put(2029, LocalDate.of(2029,4,1));
		easterDates.put(2030, LocalDate.of(2030,4,21));
		easterDates.put(2031, LocalDate.of(2031,4,13));
		easterDates.put(2032, LocalDate.of(2032,3,28));
		easterDates.put(2033, LocalDate.of(2033,4,17));
		easterDates.put(2034, LocalDate.of(2034,4,9));
		easterDates.put(2035, LocalDate.of(2035,3,25));
		easterDates.put(2036, LocalDate.of(2036,4,13));
		easterDates.put(2037, LocalDate.of(2037,4,5));
		easterDates.put(2038, LocalDate.of(2038,4,25));
		easterDates.put(2039, LocalDate.of(2039,4,10));
		easterDates.put(2040, LocalDate.of(2040,4,1));
		
		
		assertEquals(41, easterDates.size());
		
	}
	
	
	@Test
	void easterdate() {
		final SpecialdayImpl specialday =  new SpecialdayImpl();
		assertEquals(LocalDate.of(2018, 4, 1), specialday.easterdate(2018));
	}
	
	@Test
	void easterdates() {
		final SpecialdayImpl specialday =  new SpecialdayImpl();
		easterDates.entrySet().forEach(entry -> assertEquals(easterDates.get(entry.getKey()), specialday.easterdate(entry.getKey())));
		
	}


}
