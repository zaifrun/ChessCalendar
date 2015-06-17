package org.pondar.chesscalendar;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;


public class PrefsMenu extends Activity implements OnClickListener{

	
	public static final String SETTINGS_showNotification = "showNotification";
	public static final String SETTINGS_showDark = "showDark";
	public static final String SETTINGS_FONTSIZE = "settingsFontSize";
	public static final int SETTINGS_DEFAULTFONTSIZE = 17;

	Context context;
    static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    
 /*   public static int getWidgetId()
    {
    	return mAppWidgetId;
    }*/
    
    @Override
    protected void onRestart() {
    
    	//System.out.println("restarting prefsmenu");
    	super.onRestart();
    }
    
    @Override
    protected void onResume() {
 
    	//.out.println("resuming prefsmenu");
    	super.onResume();
    }
    
	
  
    
	protected void onCreate(Bundle savedBundle)
	{
		System.out.println("creating prefsmenu");
		super.onCreate(savedBundle);
		context = this;
		//Log.d("SETTINGS","IN ONCREATE");
		
		setContentView(R.layout.prefslayout);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);	  					
	    boolean showDark = prefs.getBoolean(SETTINGS_showDark, true);
	    
	    //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);	  					
	    int fontSize = prefs.getInt(SETTINGS_FONTSIZE, SETTINGS_DEFAULTFONTSIZE);
	    RadioGroup radio = (RadioGroup) findViewById(R.id.radioGroup);
	    if (showDark)
	    	radio.check(R.id.radioDark);
	    else
	    	radio.check(R.id.radioLight);

		CheckBox box = (CheckBox) findViewById(R.id.notificationBox);
		boolean notify = prefs.getBoolean(SETTINGS_showNotification,true);
		box.setChecked(notify);

        setResult(RESULT_CANCELED);
        findViewById(R.id.applybutton).setOnClickListener(this);
        findViewById(R.id.resetfontbutton).setOnClickListener(this);
        
        SeekBar bar = (SeekBar) findViewById(R.id.fontSizeBar);
        bar.setProgress(fontSize);
	        // Set the result to CANCELED.  This will cause the widget host to cancel
	        // out of the widget placement if they press the back button.
	
  // Find the widget id from the intent. 
	        Intent intent = getIntent();
	        
	        if (intent==null)
	        {
	        	System.out.println("getIntent is null");
	        }
	        Bundle extras = intent.getExtras();
	        if (extras != null) {
	            int id= extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	            mAppWidgetId = id;	          
	        }

	        // If they gave us an intent without the widget id, just bail.
	        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
	        	System.out.println("invalid widget ID");
	            finish();
	        }

		
		System.out.println("onCreate done in Prefsmenu!");
	}
	
	
	  public void onClick(View v) {
		  if (v.getId() == R.id.resetfontbutton)
		  {
			  SeekBar bar = (SeekBar) findViewById(R.id.fontSizeBar);
			  //int size = bar.getProgress();
			  bar.setProgress(SETTINGS_DEFAULTFONTSIZE);
	          Utils.showToastShort(context,context.getResources().getString(R.string.fontResetDone));

		  }		  
        else if (v.getId() == R.id.applybutton)
		  {
			  
	          System.out.println("clicked on save settings button");
	          // Push widget update to surface with newly set prefix
	          AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	          int[] id = new int[1];
	          id[0] =mAppWidgetId;
	          //update the widget     
	
	          // Make sure we pass back the original appWidgetId
	         
	      //    boolean notification = false;
	      
	          RadioGroup radio = (RadioGroup) findViewById(R.id.radioGroup);
			  CheckBox box = (CheckBox) findViewById(R.id.notificationBox);

			  boolean showDark = (radio.getCheckedRadioButtonId()==R.id.radioDark);
	          System.out.println("starting to update settings");
	          SeekBar bar = (SeekBar) findViewById(R.id.fontSizeBar);
	          
	          CalendarWidget.updateSettings(bar.getProgress(), showDark, context, appWidgetManager, mAppWidgetId);
	          System.out.println("settings updated");
	          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
	          SharedPreferences.Editor ed = prefs.edit();	
	          ed.putBoolean(SETTINGS_showDark, showDark);
	          ed.putInt(SETTINGS_FONTSIZE, bar.getProgress());
			  if (box==null)
			  {
				  Toast.makeText(this,"box is null",Toast.LENGTH_LONG).show();

			  } else
			  ed.putBoolean(SETTINGS_showNotification,box.isChecked());
	          
	          ed.commit();
	          
	          System.out.println("commited stuff");
	          
	          Intent resultValue = new Intent();
	          resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
	          setResult(RESULT_OK, resultValue);
	          System.out.println("created result intent value");
	          Utils.showToastShort(context, context.getResources().getString(R.string.settingsSaved));
	          finish();
		  }
          
      }
	
	
	

}
