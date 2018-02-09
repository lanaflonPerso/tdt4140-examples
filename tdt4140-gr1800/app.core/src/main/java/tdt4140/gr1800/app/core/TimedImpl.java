package tdt4140.gr1800.app.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class TimedImpl implements Timed {
	
	private ZoneId zone;

	public ZoneId getZone() {
		return zone;
	}
	
	public void setZone(ZoneId zone) {
		this.zone = zone;
	}
	
	private LocalDate date;
	private LocalTime time;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}
}
