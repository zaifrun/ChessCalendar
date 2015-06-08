package org.pondar.chesscalendar;

import java.util.Comparator;

public class ComparatorEventsNewFirst implements Comparator<ChessEvent> {

	@Override
	public int compare(ChessEvent event1, ChessEvent event2) {
	
	   if (event1.getYear()<event2.getYear())
		   return 1;
	   else if (event1.getYear()>event2.getYear())
		   return -1;
	   else 
	   {

		   if (event1.getMonth()<event2.getMonth())
			   return 1;
		   else if (event1.getMonth()>event2.getMonth())
			   return -1;
		   else 
		   {
			   if (event1.getDay()<event2.getDay())
				   return 1;
			   else if (event1.getDay()>event2.getDay())
				   return -1;	
			   else 
				   return 0;
			   
			   
		   }
		   
		   
	   }
	}


}
