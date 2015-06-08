package org.pondar.chesscalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChessCalendarMain extends Activity {

	Context context; 
	Resources resources;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
   //     Intent callerIntent = getIntent();
        System.out.println("Chess Calendar Main: onCreate");
        context = this;
        setContentView(R.layout.activity_chess_calendar_main);
        this.resources = getResources();
        
        ListView listView = (ListView) findViewById(R.id.listView);
        
        ArrayList<String> list = new ArrayList<String>();
		
        GregorianCalendar date = new GregorianCalendar();
		int month = date.get(GregorianCalendar.MONTH)+1;
		int day = date.get(GregorianCalendar.DAY_OF_MONTH);
	
		Calendar cal=Calendar.getInstance();
	
		SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
		String month_name = month_date.format(cal.getTime());
		
		String text = day+" "+month_name+" in Chess History:";// "+month+"."+day;
		SpannableStringBuilder spanString = new SpannableStringBuilder(text);
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
		TextAppearanceSpan span = new TextAppearanceSpan(null,Typeface.NORMAL,30,null,null);
		spanString.setSpan(span, 0, spanString.length(), 0);
		TextView textView = (TextView) findViewById(R.id.listHeader);
		textView.setText(spanString);
		
		ArrayList<ChessEvent> events = CalendarData.getData(month, day, context, true);
		
		System.out.println("Chess Events from data file");
		
		for (ChessEvent event : events)
		{
		

			String eventText = event.getYear()+": "+event.getEvent();
		
			list.add(eventText);
		
		}
		 
		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.event_item, list));
		  
			
        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//exit button
				finish();
				
			}
		});
        
        
        
        final EditText editText = (EditText) findViewById(R.id.searchText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId== EditorInfo.IME_ACTION_SEARCH)
				{
					String strip = editText.getText().toString().trim();
					if (strip!="" && strip.length()>0)
						doSearch(strip);
					else
						Utils.showToastShort(context, "No search term entered");
					return true;
				}
				return false;
			}
		});
        
        
        Button searchBut = (Button) findViewById(R.id.searchButton);
        searchBut.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Do search
				
				EditText edit = (EditText) findViewById(R.id.searchText);
				String searchString = edit.getText().toString();
				searchString = searchString.trim();
				doSearch(searchString);
				
			}
		});
        
        Button but = (Button) findViewById(R.id.calendarButton);
        but.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//	org.pondar.chesscalendar.CalendarData data = new org.pondar.chesscalendar.CalendarData(R.raw.calendar_final,resources,context);
			
				Intent caller = getIntent();
				int id = caller.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
				
				System.out.println("Change settings button pressed");
				try
				{
				
					//data.parse();
					Intent intent = new Intent(ChessCalendarMain.this,PrefsMenu.class);
					intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
					context.startActivity(intent);
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
				}
				
			}
		});
    }
    
    public void doSearch(String searchString)
    {
    	if (searchString.length()>0 && searchString!="")
		{
			Utils.showToastShort(context, "Searching...");
			//Utils.showToast(context, "Searching...please wait");
			EditText edit = (EditText) findViewById(R.id.searchText);
			InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
											
			ArrayList<String> results = CalendarData.search(context, searchString);
			ListView listView = (ListView) findViewById(R.id.listView);
			
			listView.setAdapter(new ArrayAdapter<String>(context, R.layout.event_item, results));
			
			if (results.size()==0)
				Utils.showToastShort(context, "No results found");
		}
		else
			Utils.showToastShort(context, "No keyword entered");
    	
    }
    
    
    public void displayStatusNotification(String message,Context context)
	{
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		
		int icon = R.drawable.ic_action_search;
		CharSequence tickerText = "Today in Chess History";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
	
		CharSequence contentTitle = "Today in Chess History";
		CharSequence contentText = message;
		Intent notificationIntent = new Intent(context, ChessCalendarMain.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		//notification.setLatestEventInfo(context, contentTitle, contentText, null);
		
		
		
		final int HELLO_ID = 1;

		mNotificationManager.notify(HELLO_ID, notification);
	
	}

   
    
  

}
