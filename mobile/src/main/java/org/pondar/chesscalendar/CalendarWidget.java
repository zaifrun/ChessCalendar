package org.pondar.chesscalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

public class CalendarWidget extends AppWidgetProvider {
	

	private static boolean onEnabledCalled = false;
	static boolean showDark = true;
	static Context widgetContext = null;
	
	public static void updateSettings(int fontSize,boolean dark,
			Context context, AppWidgetManager appWidgetManager,
			int appWidgetId)
	{

		showDark = dark;
		
		if (widgetContext!=null)
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(widgetContext);
	          SharedPreferences.Editor ed = prefs.edit();	
	          ed.putBoolean(PrefsMenu.SETTINGS_showDark, showDark);
	          ed.putInt(PrefsMenu.SETTINGS_FONTSIZE, fontSize);
	          ed.commit();
		}
		else
		{
			System.out.println("IN UPDATE SETTINGS WIDGET CONTEXT = NULL!!!!");
		}
		
	    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mainwidget);
	    
	    System.out.println("IN UPDATE SETTINGS: showDark = "+showDark);
	    //white text on dark background
	    if (showDark)
	    {
	    	
	    	views.setTextColor(R.id.widget_text_event, Color.WHITE);
	    	views.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.appwidget_dark_bg);
	    }
	    else
	    {
	    	views.setTextColor(R.id.widget_text_event, Color.BLACK);
	    	views.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.appwidget_bg);

	    }
	    update(context,views,appWidgetId);
	  
        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
		
	}
	
  
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		System.out.println("onUpdate Called");
		widgetContext = context;
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.mainwidget);
		
	/*	Intent configIntent = new Intent(context, org.pondar.chesscalendar.ChessCalendarMain.class);
		configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appWidgetIds[0]);
		PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.widget_container, configPendingIntent);
	*/
		
		appWidgetManager.updateAppWidget(appWidgetIds, update(context,remoteViews,appWidgetIds[0]));
		
		 
	}
	
	public static RemoteViews update(Context context,RemoteViews remoteViews,int id)
	{
	
	
		Intent configIntent = new Intent(context, ChessCalendarMain.class);
		configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,id);
		PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent,PendingIntent.FLAG_UPDATE_CURRENT);
	 
		remoteViews.setOnClickPendingIntent(R.id.widget_container, configPendingIntent);
	
		
		GregorianCalendar date = new GregorianCalendar();
		int month = date.get(GregorianCalendar.MONTH)+1;
		int day = date.get(GregorianCalendar.DAY_OF_MONTH);
	
		Calendar cal=Calendar.getInstance();
		SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
		String month_name = month_date.format(cal.getTime());
		
		String text = day+" "+month_name+" in Chess History:";// "+month+"."+day;
		SpannableStringBuilder spanString = new SpannableStringBuilder(text);
	
		//spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
	
		//	TextAppearanceSpan span = new TextAppearanceSpan(null,Typeface.NORMAL,22,null,null);
	//	spanString.setSpan(span, 0, spanString.length(), 0);
		
		
    //	views.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.appwidget_bg);

		
		ArrayList<ChessEvent> events = CalendarData.getData(month, day, context, true);
		System.out.println("Got Chess Events from data file");
		SharedPreferences prefs = null;
		int fontSize = PrefsMenu.SETTINGS_DEFAULTFONTSIZE;
		if (widgetContext==null)
		{
			System.out.println("widgetContext = null FATAL!!!!!!");
		}
		else
		{
			prefs = PreferenceManager.getDefaultSharedPreferences(widgetContext);
			showDark = prefs.getBoolean(PrefsMenu.SETTINGS_showDark, true);
			fontSize = prefs.getInt(PrefsMenu.SETTINGS_FONTSIZE, PrefsMenu.SETTINGS_DEFAULTFONTSIZE);
		}
		System.out.println("IN WIDGET: SHOW DARK = "+showDark);
		if (showDark)
		    {
		    	
		    	remoteViews.setTextColor(R.id.widget_text_event, Color.WHITE);
		    	remoteViews.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.appwidget_dark_bg);
		    }
		    else
		    {
		    	remoteViews.setTextColor(R.id.widget_text_event, Color.BLACK);
		    	remoteViews.setInt(R.id.widget_container, "setBackgroundResource", R.drawable.appwidget_bg);

		    }
		
		
		int factNumber=0;
		if (prefs==null)
			System.out.println("PREFS IS NULL!!");
		else
			factNumber = prefs.getInt("factNumber",0);
	   
		System.out.println("FactNumber loaded is "+factNumber);
		
		if (factNumber>=events.size())
			factNumber=0;
		ChessEvent event = events.get(factNumber);
		
		if (!onEnabledCalled)
			factNumber++;
		else
			onEnabledCalled=false;
		
		if (prefs!=null)
		{
			SharedPreferences.Editor ed = prefs.edit();	
			if (ed!=null)
			{
				ed.putInt("factNumber", factNumber);
				ed.commit();
				System.out.println("commiting factnumber: "+factNumber);
			}
		}
		
		String eventText = event.getYear()+": "+event.getEvent();
		
		int start = spanString.length();
		spanString.append("\n" + eventText.toString());
		int lastMonthNotification =  prefs.getInt("Month",0);
		int lastDayNotification = prefs.getInt("Day",0);
		if (lastDayNotification!=day && lastMonthNotification!=month)
		{
			//TODO a new day - show the notification.
			Intent viewIntent = new Intent(context, ChessCalendarMain.class);
			//viewIntent.putExtra(EXTRA_EVENT_ID, 1);
			PendingIntent viewPendingIntent =
					PendingIntent.getActivity(context, 0, viewIntent, 0);
			// Specify the 'big view' content to display the long
// event description that may not fit the normal content text.
			NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
			bigStyle.bigText(event.getYear() + ": " + event.getEvent().toString());

			NotificationCompat.Builder notificationBuilder =
					new NotificationCompat.Builder(context)
							.setSmallIcon(R.drawable.icon)
							.setContentTitle("Today in Chess History")
							.setContentText(event.getYear() + ": " + event.getEvent().toString())
							.setStyle(bigStyle)
							.setContentIntent(viewPendingIntent);;

			// Get an instance of the NotificationManager service
			NotificationManagerCompat notificationManager =
					NotificationManagerCompat.from(context);

			// Issue the notification with notification manager.
			notificationManager.notify(1, notificationBuilder.build());

			SharedPreferences.Editor ed = prefs.edit();
			if (ed!=null)
			{
				//put new values - now we have showed the notification
				ed.putInt("Month", month);
				ed.putInt("Day",day);
				ed.commit();
			}

		}

		//check if the current day is different from the last time
		//we showed a notification (only show once a day).
		//if yes, then show the notification.
		
		float headerSize = fontSize;
		float textSize = fontSize-4;
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int pix1  = (int) (headerSize * dm.scaledDensity);
		int pix2 = (int) (textSize * dm.scaledDensity);
		
		System.out.println("start = "+start+", scaled density = "+dm.scaledDensity+" , pix1 = "+pix1+", pix2 = "+pix2);
		
		if (pix1<1)
			pix1 = 1;
		if (pix2<1)
			pix2 = 1;
		TextAppearanceSpan span = new TextAppearanceSpan(null,Typeface.BOLD,pix1,null,null);
		TextAppearanceSpan span2 = new TextAppearanceSpan(null,Typeface.NORMAL,pix2,null,null);
		
		spanString.setSpan(span, 0, start, 0);
		spanString.setSpan(span2, start+1, spanString.length(), 0);
		
		
		remoteViews.setTextViewText(R.id.widget_text_event, spanString);
		
		
		return remoteViews;
		
	}
	
	//do data loading into database.
	@Override
	public void onEnabled(Context context) {
		
		//DataBase db = new DataBase(context);
		//load from text file
		//insert into database
		//
		widgetContext = context;
		System.out.println("onEnabled Called"); 
		onEnabledCalled = true;
		super.onEnabled(context);
	}
	
	@Override
	public void onDisabled(Context context) {
		
		super.onDisabled(context);
	}
	
	
	

}
