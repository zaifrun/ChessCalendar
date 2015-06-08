package org.pondar.chesscalendar;

public class ChessEvent {
	String event;
	int year,month,day;
	
	
	
	
	public ChessEvent(String event, int month, int day) {
		super();
		this.event = event;
		this.month = month;
		this.day = day;
	}
	public ChessEvent(String event, int year, int month, int day) {
		super();
		this.event = event;
		this.year = year;
		this.month = month;
		this.day = day;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	@Override
	public String toString() {
		return "org.pondar.chesscalendar.ChessEvent [month=" + month + ", day=" + day + ", year=" + year
				+ ", event=" + event + "]";
	}
	

}
