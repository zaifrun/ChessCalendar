package org.pondar.chesscalendar;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/**
 * @author mk
 * This class is used to load the calendar data into data structures and do some organizing.
 */
public class CalendarData {
	
	private BufferedReader input;
	private Context context;
	
	HashMap<String, ArrayList<String>> data = new HashMap<String,ArrayList<String>>();
	ArrayList<ChessEvent> events = new ArrayList<ChessEvent>();
	
	public static ArrayList<String> search(Context context,String searchString)
	{
		ArrayList<ChessEvent> events = new ArrayList<ChessEvent>();
		ArrayList<String> results = new ArrayList<String>();
		int m = 0,d = 0,y = 0;
		String text = "";
		
		try
		{
			InputStream input = context.getResources().openRawResource(R.raw.events);
			DataInputStream in = new DataInputStream(input);
						
			boolean notDone = true;
			while (true && notDone)
			{
				/* Bin�rt format for chess data
				 * m�ned,day,�r,text*/
				//kunne lave en index fil, hvis n�dvendigt, som bare beskriver
				//hvilket index hver m�ned starter i 
				//ville g�re indl�sningen 12 x hurtig
				m = in.readInt();
				d = in.readInt();
				y = in.readInt();
				text = in.readUTF();
				
				events.add(new ChessEvent(text,y,m,d));
			
			}	
		}	
		catch (EOFException e)
		{ 
		 //pust last entry into list
			events.add(new ChessEvent(text,y,m,d));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

		
		
		/*Now search the events and add matches to the result array */
		
		String query = searchString.toLowerCase();	
		LocalAlignment align = new LocalAlignment();

		//sort search
	    Collections.sort(events,new ComparatorEventsNewFirst());
		
		
		for(ChessEvent event : events) 
		{
			String lower = event.getEvent().toLowerCase();
			//lower = lower.toLowerCase();
			
			//now both strings are in lower case - so in-case sensitive search is what I want 
			align.run(query,lower);
			if (align.getPercentage()>0.8)
			{
				String str = event.getYear()+"."+event.getMonth()+"."+event.getDay()+": "+event.getEvent();
				results.add(str);	
			}
		}
		
		
	
		return results;
	}
	
	
	public static ArrayList<ChessEvent> getData(int month, int day,Context context,boolean optimize)
	{
		ArrayList<ChessEvent> events = new ArrayList<ChessEvent>();
		int m=0,d=0,y=0;
		String text = "";
		try
		{
			//File f = new File(context.getExternalFilesDir(null), "events.raw");
		//	DataInputStream in = new DataInputStream(new FileInputStream(f));
			
			//InputStream input = Utils.getResources().openRawResource(R.raw.index);
			InputStream input = context.getResources().openRawResource(R.raw.events);
			DataInputStream in = new DataInputStream(input);
			
			System.out.println("Starting to read binary data");
			//Utils.startTimer();
			boolean notDone = true;
			while (true && notDone)
			{
				/* Bin�rt format for chess data
				 * m�ned,day,�r,text*/
				//kunne lave en index fil, hvis n�dvendigt, som bare beskriver
				//hvilket index hver m�ned starter i 
				//ville g�re indl�sningen 12 x hurtig
				m = in.readInt();
				d = in.readInt();
				y = in.readInt();
				text = in.readUTF();
				if (m==month && d == day)
				{
					events.add(new ChessEvent(text,y,m,d));
					
				}	
				
				if (optimize && ((m>month) || (m==month && d>day)))
					notDone = false;
			}	
		}	
		catch (EOFException e)
		{ 
		 //pust last entry into list
			events.add(new ChessEvent(text,y,m,d));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		//Utils.endTimer("loaded binary data");
		return events;
	
	}
	public CalendarData(int resource,Resources resources,Context context)
	{
		//this.filename=filename;
		this.context = context;
		try 
		{
			this.input = 
				new BufferedReader(
						new InputStreamReader(resources.openRawResource(resource),"ISO-8859-1"),100000);						
			
		} catch (Exception e)
		{
			Log.d("BOOK ERROR","Could not open file: "+resource+". "+e.getMessage());
			System.exit(-1);
		}
		
	}
	
	
	private void parse() throws IOException
	{
		  	String line = input.readLine();
	        int linenr = 0;
	        StringTokenizer st;
	        int fullDates = 0;
	    	int max = -1;
        	int min = 1000;
        	final boolean WRITEFILE = true;
        	
	        
	        
	       String regex = "(\\d{4})\\.(\\d{1,2})\\.(\\d{1,2})"; //Hello, (\\S+)");
	        
	        while (line != null && !line.equals("") && !line.equals("\n")) {
	        	
	        	
	            st = new StringTokenizer(line, " \t", false);  //her laves ny tokenizer

	           // validate(Tokens.TAGBEGIN, st.nextToken(), tag);
	            String date =  st.nextToken();
	            int start = line.indexOf(" ");
	            String text = line.substring(start,line.length());
	            text = text.trim();
	        
	            /*Matcher m = p.matcher(inputString);*/
	            if (Pattern.matches(regex,date))
	            {
	            	//its a valid date
	            	//System.out.println(date+":"+text);
	            	fullDates++;
	            	String[] values = date.split("\\.");
	            	int year = new Integer(values[0]).intValue();
	            	int month = new Integer(values[1]).intValue();
	            	int day = new Integer(values[2]).intValue();
	            	GregorianCalendar gc = new GregorianCalendar(year, month, day);
	            	date = month+"."+day;
	            	//date already exists - add event to list.
	            	boolean died = false;
	            	boolean born = false;
	            	if (text.contains("born"))
	            		born = true;
	            	if (text.contains("died") || text.contains("dies"))
	            		died = true;
	            	//events.add(new org.pondar.chesscalendar.ChessEvent(text,year,month,day));
	            	if (day==0)
	            		System.out.println("ZERO DAY: "+year+"."+month+"."+day);
	            	
	            	
		            	events.add(new ChessEvent(text,year,month,day));
		            	if (data.containsKey(date))
		            	{
		            		ArrayList<String> list = data.get(date);
		            		list.add(text);
		            		if (list.size()>max)
		            			max = list.size();
		            		if (list.size()<min)
		            			min = list.size();
		            	}
		            	//date does not exist - create new list of events
		            	else
		            	{
		            		ArrayList<String> list = new ArrayList<String>();
		            		list.add(text);
		            		data.put(date,list);
		            
		            	}
	            	
	            }
	            	

	            line = input.readLine();
	            linenr++;
	        }
	        
	        Collections.sort(events,new ComparatorEventsMonthDay());
	        System.out.println("events size = "+events.size()+"min per day ="+min+", max per day ="+max);
	        System.out.println("lines = "+linenr+", fulldates = "+fullDates+", unique dates ="+data.size());
	        int previousMonth = 0;
	        int previousDay = 0;
	    	File f = new File(context.getExternalFilesDir(null), "events.raw");
    		DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
    		//File textFileHandle = new File(context.getExternalFilesDir(null), "calendar.txt");
    		 
    		//BufferedWriter textWriter  = new BufferedWriter(new FileWriter(textFileHandle));
    		 
    		
	        for (ChessEvent event : events)
	        {
	        	if (event.getMonth()!=previousMonth || event.getDay()!=previousDay)
	        	{
	        		//System.out.println(event);
	        		previousMonth = event.getMonth();
	        		previousDay = event.getDay();
	        	}
	        	//writes the file to the destination
	        	if (WRITEFILE)
	        	{
	        	
	        		
	        		//System.out.println("Starting to write binary data");
	        		try
	        		{
	        			 // m�ned,day,�r,text
	        			out.writeInt(event.getMonth());
	        			out.writeInt(event.getDay());
	        			out.writeInt(event.getYear());
	        			out.writeUTF(event.getEvent());
	        			//String text = event.getYear()+"."+event.getMonth()+"."+event.getDay()+" "+event.getEvent();
	        		//	textWriter.write(text, 0, text.length());
	        			//textWriter.newLine();
	        			
	        		}
	        		catch (Exception e)
	    			{
	    				System.out.println(e.getMessage());
	    				System.out.println(e.getStackTrace());
	    			}
	        		
	   			 
	        	}
	        } //loop through all events
	        //textWriter.flush();
			System.out.println("Done writing new binary data!");
			

	        
	}
	
	/* Bin�rt format for chess data
	 * m�ned,day,�r,text
	 * load hele lortet ind i en database
	 * index fil: 
	 * 
	 */

}
