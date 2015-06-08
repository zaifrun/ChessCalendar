package org.pondar.chesscalendar;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Stack;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class Utils {
	
	private static Resources resources = null;
	
	public static boolean FREE_VERSION = true; //if true then only 2 categories will be show to the user
	public static boolean PREMIUM_VERSION = !FREE_VERSION; //if true then ALL categories will be there
	
	private static int BUFFER_SIZE = 100000;
	
	private static Stack<Long> timerStack = new Stack<Long>();
	
	public static void sendEmail(Context context,String to, String subject, String body)
	{
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{to});
		i.putExtra(Intent.EXTRA_SUBJECT, subject);
		i.putExtra(Intent.EXTRA_TEXT   , body);
		try {
		    context.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    showToast(context,"There are no email clients installed.");
		}
	
	}
	
	 public static boolean isInstalled(Context context, String packageName) {
	   		boolean installed = true;
	   		try {
	   			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
	   					packageName, 0);
	   		} catch (android.content.pm.PackageManager.NameNotFoundException e) {
	   			installed = false;
	   		}
	   		return installed;
	   	}
	
	public static long getFileTimeURL(String filename) throws IOException
	{
		//File root = Environment.getExternalStorageDirectory();
		URL u = new URL(filename);
		
	/*	  URL url = new URL("http://www.xyz.com/documents/files/xyz-china.pdf");
		    HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

		    long date = httpCon.getLastModified();*/
		
		
		HttpURLConnection c = (HttpURLConnection) u.openConnection();
		
		long dt=c.getLastModified();  //File Modified Date!!!
		//printtime("File Time ",dt);
		System.out.println("file: "+filename+" last modified on "+dt);
		c.disconnect();       //close connection  ????
		return dt;
	}
	
	
	
	//if succces then returns a stringbuffer
	//if not succes then returns null
	public static StringBuilder readTxtFileFromURL(String filename) throws IOException
	{
		StringBuilder buffer = new StringBuilder();
	
	    // Create a URL for the desired page
	    URL url = new URL(filename);

	    // Read all the text returned by the server
	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    String str;
	    while ((str = in.readLine()) != null) 
	    {
	        // str is one line of text; readLine() strips the newline character(s)
	    	buffer.append(str+"\n");
	    }
	    in.close();
	    return buffer; 
	}
	
	
	public static void startTimer()
	{
		long time = System.currentTimeMillis();
		timerStack.push(Long.valueOf(time));
	}

	public static boolean isVersion8()
	{
		if (Build.VERSION.SDK_INT>Build.VERSION_CODES.ECLAIR_MR1)
			return true;
		else
			return false;				  
	}
	
	public static boolean isKitKatOrHigher()
	{
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
			return true;
		else
			return false;				  
	}
	
	public static long endTimer(String activity)
	{
		long time2 = System.currentTimeMillis();
		long time = timerStack.pop().longValue();
		long dif = time2 - time;
		double d = (double) dif / 1000.0d;
		if (activity!="")
			Log.d("TIMER RESULT: "+activity, " result = "+d+" seconds");
		return dif;
	}
	
	
	public static boolean isFreeVersion()
	{
		return (FREE_VERSION==true);
	}
	
	public static void setResources(Resources res)
	{
		resources=res;		
	}
	
	public static boolean isPremiumVersion()
	{
		return (PREMIUM_VERSION==true);
	}
	
	
	public static String get(int id)
	{
		return resources.getString(id);
		
	}
	
	public static Resources getResources()
	{
		return resources;
	}
	
	public static void showToastShort(Context context,String message)
	{
		Toast toast = Toast.makeText(context,message, Toast.LENGTH_SHORT);
    	toast.show();
		
	}
	
	public static void showToastLong(Context context,String message)
	{
		Toast toast = Toast.makeText(context,message, Toast.LENGTH_SHORT);
    	toast.show();
		
	}
	
	public static void showToast(Context context,String message)
	{
		showToastLong(context,message);
	}
	
	
	public static Bitmap viewToBitmap(View view)
	  
	   {
		   view.setDrawingCacheEnabled(true);

	   	   view.buildDrawingCache();

	   	   Bitmap bm = view.getDrawingCache();
	   
	   	   return bm;
	   }
	
	public static String getDate()
	{
		GregorianCalendar date = new GregorianCalendar();
		//int month = date.get(GregorianCalendar.MONTH)+1;
		int day = date.get(GregorianCalendar.DAY_OF_MONTH);
		int year = date.get(GregorianCalendar.YEAR);
		Calendar cal=Calendar.getInstance();
	
		SimpleDateFormat month_date = new SimpleDateFormat("MMMMMMMMM");
		String month_name = month_date.format(cal.getTime());
		return day+" "+month_name+" "+year;
	}
	
	public static boolean haveInternet(Context ctx) {

	    NetworkInfo info = (NetworkInfo) ((ConnectivityManager) ctx
	            .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

	    if (info == null || !info.isConnected()) {
	        return false;
	    }
	    if (info.isRoaming()) {
	        // here is the roaming option you can change it if you want to
	        // disable internet while roaming, just return false
	        return true;
	    }
	    return true;
	}
	   
	 public static Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom 
		    Bitmap cs = null; 
		 
		    int width, height = 0; 
		     
		    if(c.getWidth() > s.getWidth()) { 
		      width = c.getWidth(); 
		      height = c.getHeight() + s.getHeight(); 
		    } else { 
		      width = s.getWidth(); 
		      height = c.getHeight() + s.getHeight(); 
		    } 
		 
		    cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); 
		 
		    Canvas comboImage = new Canvas(cs); 
		 
		    comboImage.drawBitmap(c, 0f, 0f, null); 
		    comboImage.drawBitmap(s, 0f, c.getHeight(), null); 
		 
		     return cs; 
		  } 
	 
	 public static int copy(InputStream input, OutputStream output) throws Exception, IOException {
         byte[] buffer = new byte[BUFFER_SIZE];

         BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
         BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
         int count = 0, n = 0;
         try {
                 while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                         out.write(buffer, 0, n);
                         count += n;
                 }
                 out.flush();
         } finally {
                 try {
                         out.close();
                 } catch (IOException e) {
                         Log.e("IOException in pondarlibary.copy",e.getMessage());
                 }
                 try {
                         in.close();
                 } catch (IOException e) {
                         Log.e("IOException in pondarlibrary.copy",e.getMessage());
                 }
         }
         return count;
	 }

}
