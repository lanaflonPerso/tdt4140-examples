package tdt4140.gr1800.app.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public interface Timed {

	public ZoneId getZone();
	public void setZone(ZoneId zone);

	default 	public void setZone(String zone) {
		setZone(ZoneId.of(zone));
	}

	public LocalDate getDate();
	
	public void setDate(LocalDate date);

	default public void setDate(String date) {
		setDate(LocalDate.parse(date));
	}

	public LocalTime getTime();
	
	public void setTime(LocalTime time);

	default public void setTime(String time) {
		setTime(LocalTime.parse(time));
	}

	default public void setDateTime(LocalDateTime dateTime) {
		setDate(dateTime.toLocalDate());
		setTime(dateTime.toLocalTime());
	}
}
