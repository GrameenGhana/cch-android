package org.grameenfoundation.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.grameenfoundation.cch.activity.PlanEventActivity;
import org.grameenfoundation.cch.model.MyCalendarEvents;
import org.joda.time.DateTime;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;


public class CalendarEvents {
	Context mContext;
    private ArrayList<MyEvent> calEvents = new ArrayList<MyEvent>();

    private int todaysEventsNum = 0;
    private int tomorrowsEventsNum = 0;
    private int futureEventsNum = 0;
    private int thismonthEventsNum = 0;
    private int thismonthEventsDone = 0;
    private String previousLocations = "";
	private SharedPreferences prefs;
	private String category;
    private static DbHelper dbh;
    
	public CalendarEvents(Context c){
		this.mContext=c;
		readCalendarEvent(c);
		dbh = new DbHelper(c);
	}
	
	public boolean addRecurringEvent(String evt, String location, String desc,String rrule)
    {			
		Calendar cal = Calendar.getInstance();
		Intent	intent = new Intent(Intent.ACTION_EDIT)
		        .setData(Events.CONTENT_URI)
		        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis())
		        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,  cal.getTimeInMillis()+60*60*1000)
		        .putExtra(Events.TITLE, evt)
		        .putExtra(Events.DESCRIPTION, desc)
		        .putExtra(Events.EVENT_LOCATION, location)
		        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
		 		.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY , false);
		Bundle b=new Bundle();
		b.putString(Events.RRULE,rrule);
		intent.putExtras(b);
		mContext.startActivity(intent);
		return true;
    }
	public boolean addEvent(String evt, String location, String desc)
    {	
		
		Calendar cal = Calendar.getInstance();
		Intent	intent = new Intent(Intent.ACTION_EDIT)
		        .setData(Events.CONTENT_URI)
		        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis())
		        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,  cal.getTimeInMillis()+60*60*1000)
		        .putExtra(Events.TITLE, evt)
		        .putExtra(Events.DESCRIPTION, desc)
		        .putExtra(Events.EVENT_LOCATION, location)
		        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
		 		.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY , false);
		mContext.startActivity(intent);
		
		return true;
		
	
    }
	public boolean editEvent(long event_id,String evt, String location, String desc,String startdate,String enddate,int availability)
    {	
		ContentResolver cr = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		Uri updateUri = null;
		// The new title for the event
		values.put(Events.TITLE,evt); 
		values.put(Events.DESCRIPTION,desc); 
		values.put(Events.EVENT_LOCATION,location); 
		values.put(Events.DTSTART,Long.parseLong(startdate)); 
		values.put(Events.DTEND,Long.parseLong(enddate));
		values.put(Events.AVAILABILITY,availability); 
		updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
		int rows =  mContext.getContentResolver().update(updateUri, values, null, null);
		Log.i("Calendar edit", "Rows updated: " + rows);  
		System.out.println(startdate);
		if(rows==1){
			Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
			Intent intent = new Intent(Intent.ACTION_EDIT)
			   .setData(uri)
			   .putExtra(Events.TITLE, evt)
			   .putExtra(Events.DESCRIPTION, desc)
			   .putExtra(Events.EVENT_LOCATION, location)
			   .putExtra("beginTime", Long.parseLong(startdate))
			   .putExtra("endTime",Long.parseLong(enddate))
			   .putExtra(Events.AVAILABILITY, availability);
			if(availability==Events.AVAILABILITY_BUSY){
				category="personal";
			}else if(availability==Events.AVAILABILITY_FREE){
				category="not_personal";
			}
			dbh.editCalendarEvent(event_id, evt, desc, category, location, "", "", "", startdate, enddate);
			mContext.startActivity(intent);
			return true;
		}else {
			return false;
		}
		
	
    }
	public boolean updateEvent(long event_id,int status)
    {	
		ContentResolver cr = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		Uri updateUri = null;
		// The new title for the event
		values.put(Events.STATUS,status); 
		updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
		int rows =  mContext.getContentResolver().update(updateUri, values, null, null);
		Log.i("Calendar edit", "Rows updated: " + rows);  
		if(rows==1){
			return true;
		}else {
			return false;
		}
    }
	public boolean deleteEvent(long event_id)
    {	
		ContentResolver cr = mContext.getContentResolver();
		ContentValues values = new ContentValues();
		Uri deleteUri = null;
		deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, event_id);
		int rows = cr.delete(deleteUri, null, null);
		Log.i("Calendar delete", "Rows deleted: " + rows);  
		if(rows==1){
			dbh.deleteCalendarEvent(event_id);
			return true;
			}else {
				return false;
			}
    }
	public class MyEvent
    {
    	public long eventId;
    	public String eventType;
    	public String location;
    	public String description;
    	public Long startDate;
    	public Long endDate;
    	
    	public MyEvent(){
    		
    	}
    	public boolean isToday()
    	{
    			long milliSeconds = this.startDate;
    	    	String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date(System.currentTimeMillis()));
    	        return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds))
    	       				.toString().equals(today)) ? true : false;
    	}
    	
    	public boolean isTomorrow()
    	{
    			/*long milliSeconds = this.startDate;
    	    	Calendar c = Calendar.getInstance();
    	    	c.add(Calendar.DATE, 1);
    	    	String tomorrow = new SimpleDateFormat("MM/dd/yyyy").format(new Date(c.getTimeInMillis()));
    	        return (DateFormat.format("MM/dd/yyyy", new Date(milliSeconds))
    	       				.toString().equals(tomorrow)) ? true : false;*/
    		long milliSeconds = this.startDate;
			DateTime previous = new DateTime(milliSeconds);
			DateTime now = new DateTime();
			if(now.plusDays(1)==previous){
				return true;
			}else {
				return false;
			}
    	}
    	public boolean isYesterday()
    	{
    			long milliSeconds = this.startDate;
    			DateTime previous = new DateTime(milliSeconds);
    			DateTime now = new DateTime();
    			if(now.minusDays(1)==previous){
    				return true;
    			}else {
    				return false;
    			}
    	}
    	public boolean isFuture()
    	{
    			/*long milliSeconds = this.startDate;
    	    	Calendar c = Calendar.getInstance();
    	    	
    	    	c.add(Calendar.DATE, 2);
    	        return (milliSeconds >= c.getTimeInMillis()) ? true : false;*/
    		long milliSeconds = this.startDate;
			DateTime previous = new DateTime(milliSeconds);
			DateTime now = new DateTime();
			if(now.plusDays(2).getMillis()>=previous.getMillis()){
				return true;
			}else {
				return false;
			}
    	}
    	public boolean isPastThisMonth() {
    		long milliSeconds = this.startDate;
			DateTime previous = new DateTime(milliSeconds);
			DateTime now = new DateTime();
			if((now.minusDays(2).getMillis()>=previous.getMillis())&&(previous.getMonthOfYear()==now.getMonthOfYear())){
				return true;
			}else {
				return false;
			}
    		}
    	public boolean isPastLastMonth() {
    		long milliSeconds = this.startDate;
			DateTime previous = new DateTime(milliSeconds);
			DateTime lastmonth = new DateTime().minusMonths(1);
			if(previous.getMonthOfYear()==lastmonth.getMonthOfYear()){
				return true;
			}else {
				return false;
			}
    		}
    	public boolean isThisMonth() {
    		return isThisMonth(false); 
    		}
    	public boolean isThisMonth(boolean completed)
    	{	
    		boolean resp = false;
    		
    		long milliSeconds = this.startDate;
	    	Calendar c = Calendar.getInstance();
	    	String today = new SimpleDateFormat("MM/yyyy").format(new Date(c.getTimeInMillis()));
	        
	    	// is it this month?
	    	resp =  (DateFormat.format("MM/yyyy", new Date(milliSeconds))
	       				.toString().equals(today)) ? true : false;
 
	    	if (resp && completed)
	    	{
	    		resp =  (milliSeconds < c.getTimeInMillis()) ? true : false;
	    	}
	    	
    		return resp;
    	}
              
        public String getDate(String format) {
			   long milliSeconds = this.startDate;
               SimpleDateFormat formatter = new SimpleDateFormat(format);
               Calendar calendar = Calendar.getInstance();
               calendar.setTimeInMillis(milliSeconds);
               return formatter.format(calendar.getTime());
        }
        
    }
	       public ArrayList<MyCalendarEvents> getTodaysEvents(Boolean showDay) {
				ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		       int evNum = 0;
    	       String dformat ="MMM-dd hh:mm a";
    	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
    	       Calendar calendar = Calendar.getInstance();
    	       long milliSeconds = 0;
    	       calendar.setTimeInMillis(milliSeconds);
    	    	String d =formatter.format(calendar.getTime());
		    	   for(MyEvent ev: calEvents){
		    		   MyCalendarEvents calendarEvents=new MyCalendarEvents();
		        	   if (ev.isToday())
		        	   {
		        		   calendarEvents.setEventType(ev.eventType);
		        		   calendarEvents.setEventDescription(ev.description);
		        		   calendarEvents.setEventLocation(ev.location);
		        		   calendarEvents.setEventTime(ev.getDate(dformat));
		        		   calendarEvents.setEventId(String.valueOf(ev.eventId));
		        		   calendarEvents.setEventStartDate(String.valueOf(ev.startDate));
		        		   calendarEvents.setEventEndDate(String.valueOf(ev.endDate));
		        		   evNum++;
		        		   list.add(calendarEvents);
		        	   }
		        	   
		    	   }
		       
		       return list;
		   	}
	       public ArrayList<MyCalendarEvents> getYesterdaysEvents(Boolean showDay) {
				ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		       int evNum = 0;
   	       String dformat ="MMM-dd hh:mm a";
   	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
   	       Calendar calendar = Calendar.getInstance();
   	       long milliSeconds = 0;
   	       calendar.setTimeInMillis(milliSeconds);
   	    	String d =formatter.format(calendar.getTime());
		    	   for(MyEvent ev: calEvents){
		    		   MyCalendarEvents calendarEvents=new MyCalendarEvents();
		        	   if (ev.isYesterday())
		        	   {
		        		   calendarEvents.setEventType(ev.eventType);
		        		   calendarEvents.setEventDescription(ev.description);
		        		   calendarEvents.setEventLocation(ev.location);
		        		   calendarEvents.setEventTime(ev.getDate(dformat));
		        		   calendarEvents.setEventId(String.valueOf(ev.eventId));
		        		   calendarEvents.setEventStartDate(String.valueOf(ev.startDate));
		        		   calendarEvents.setEventEndDate(String.valueOf(ev.endDate));
		        		   evNum++;
		        		   list.add(calendarEvents);
		        	   }
		        	   
		    	   }
		       
		       return list;
		   	}
	       public ArrayList<MyCalendarEvents> getPastLastMonthEvents(Boolean showDay) {
				ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		       int evNum = 0;
  	       String dformat ="MMM-dd hh:mm a";
  	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
  	       Calendar calendar = Calendar.getInstance();
  	       long milliSeconds = 0;
  	       calendar.setTimeInMillis(milliSeconds);
  	    	String d =formatter.format(calendar.getTime());
		    	   for(MyEvent ev: calEvents){
		    		   MyCalendarEvents calendarEvents=new MyCalendarEvents();
		        	   if (ev.isPastLastMonth())
		        	   {
		        		   calendarEvents.setEventType(ev.eventType);
		        		   calendarEvents.setEventDescription(ev.description);
		        		   calendarEvents.setEventLocation(ev.location);
		        		   calendarEvents.setEventTime(ev.getDate(dformat));
		        		   calendarEvents.setEventId(String.valueOf(ev.eventId));
		        		   calendarEvents.setEventStartDate(String.valueOf(ev.startDate));
		        		   calendarEvents.setEventEndDate(String.valueOf(ev.endDate));
		        		   evNum++;
		        		   list.add(calendarEvents);
		        	   }
		        	   
		    	   }
		       
		       return list;
		   	}
	       public ArrayList<MyCalendarEvents> getPastThisMonthEvents(Boolean showDay) {
	  				ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
	  		       int evNum = 0;
	    	       String dformat ="MMM-dd hh:mm a";
	    	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
	    	       Calendar calendar = Calendar.getInstance();
	    	       long milliSeconds = 0;
	    	       calendar.setTimeInMillis(milliSeconds);
	    	    	String d =formatter.format(calendar.getTime());
	  		    	   for(MyEvent ev: calEvents){
	  		    		   MyCalendarEvents calendarEvents=new MyCalendarEvents();
	  		        	   if (ev.isPastThisMonth())
	  		        	   {
	  		        		   calendarEvents.setEventType(ev.eventType);
	  		        		   calendarEvents.setEventDescription(ev.description);
	  		        		   calendarEvents.setEventLocation(ev.location);
	  		        		   calendarEvents.setEventTime(ev.getDate(dformat));
	  		        		   calendarEvents.setEventId(String.valueOf(ev.eventId));
	  		        		   calendarEvents.setEventStartDate(String.valueOf(ev.startDate));
	  		        		   calendarEvents.setEventEndDate(String.valueOf(ev.endDate));
	  		        		   evNum++;
	  		        		   list.add(calendarEvents);
	  		        	   }
	  		        	   
	  		    	   }
	  		       
	  		       return list;
	  		   	}
	       public ArrayList<MyCalendarEvents> getEventsToUpdate(Boolean showDay) {
				ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		       int evNum = 0;
   	       String dformat ="MMM-dd hh:mm a";
   	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
   	       Calendar calendar = Calendar.getInstance();
   	       long milliSeconds = 0;
   	       calendar.setTimeInMillis(milliSeconds);
   	    	String d =formatter.format(calendar.getTime());
		    	   for(MyEvent ev: calEvents){
		    		   MyCalendarEvents calendarEvents=new MyCalendarEvents();
		        	   if (ev.isToday())
		        	   {
		        		   calendarEvents.setEventType(ev.eventType);
		        		   calendarEvents.setEventDescription(ev.description);
		        		   calendarEvents.setEventLocation(ev.location);
		        		   calendarEvents.setEventTime(ev.getDate(dformat));
		        		   calendarEvents.setEventId(String.valueOf(ev.eventId));
		        		   calendarEvents.setEventStartDate(String.valueOf(ev.startDate));
		        		   calendarEvents.setEventEndDate(String.valueOf(ev.endDate));
		        		   evNum++;
		        		   list.add(calendarEvents);
		        	   }
		        	   
		    	   }
		       
		       return list;
		   	}
	       public ArrayList<MyCalendarEvents> getTomorrowsEvents(Boolean showDay) {
				ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		       int evNum = 0;
		   
   	       String dformat = "MMM-dd hh:mm a";
   	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
   	       Calendar calendar = Calendar.getInstance();
   	       long milliSeconds = 0;
   	       calendar.setTimeInMillis(milliSeconds);
   	    	String d =formatter.format(calendar.getTime());
		    	   for(MyEvent ev: calEvents){
		    		   MyCalendarEvents calendarEvents=new MyCalendarEvents();
		        	   if (ev.isTomorrow())
		        	   {
		        		   calendarEvents.setEventType(ev.eventType);
		        		   calendarEvents.setEventDescription(ev.description);
		        		   calendarEvents.setEventLocation(ev.location);
		        		   calendarEvents.setEventTime(ev.getDate(dformat));
		        		   calendarEvents.setEventId(String.valueOf(ev.eventId));
		        		   calendarEvents.setEventStartDate(String.valueOf(ev.startDate));
		        		   calendarEvents.setEventEndDate(String.valueOf(ev.endDate));
		        		   evNum++;
		        		   list.add(calendarEvents);
		        	   }
		        	   
		    	   }
		       
		       return list;
		   	}
	       
	       public ArrayList<MyCalendarEvents> getFutureEvents(Boolean showDay) {
				ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		       int evNum = 0;
  	       String dformat ="MMM-dd hh:mm a";
  	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
  	       Calendar calendar = Calendar.getInstance();
  	       long milliSeconds = 0;
  	       calendar.setTimeInMillis(milliSeconds);
  	    	String d =formatter.format(calendar.getTime());
		    	   for(MyEvent ev: calEvents){
		    		   MyCalendarEvents calendarEvents=new MyCalendarEvents();
		        	   if (ev.isFuture())
		        	   {
		        		   calendarEvents.setEventType(ev.eventType);
		        		   calendarEvents.setEventDescription(ev.description);
		        		   calendarEvents.setEventLocation(ev.location);
		        		   calendarEvents.setEventTime(ev.getDate(dformat));
		        		   calendarEvents.setEventId(String.valueOf(ev.eventId));
		        		   calendarEvents.setEventStartDate(String.valueOf(ev.startDate));
		        		   calendarEvents.setEventEndDate(String.valueOf(ev.endDate));
		        		   evNum++;
		        		   list.add(calendarEvents);
		        	   }
		        	   
		    	   }
		       
		       return list;
		   	}
	
	
	   	
	       public int readCalendarEventForEdit(Context context,long id)
	       	{
	       		int availability = 0;
	       		
	       		String selection="(( "+Events._ID+" = "+id+" ))";
	               Cursor cursor = context.getContentResolver()
	                       .query(
	                               Uri.parse("content://com.android.calendar/events"),
	                               new String[] { Events.AVAILABILITY}, selection, null, null);
	               cursor.moveToFirst();
	               
	               // fetching calendars name
	               String CNames[] = new String[cursor.getCount()];


	               Calendar c = Calendar.getInstance();
	               for (int i = 0; i < CNames.length; i++) {
	                   // CNames[i] = cursor.getString(0);
	                  
	            	    try {
	            	    	availability = cursor.getInt(cursor.getColumnIndex(Events.AVAILABILITY));
	            	    } catch(NumberFormatException e) {e.printStackTrace();}
	            	   
	                   cursor.moveToNext();
	               }  
	               
	               cursor.close();
	   			return availability;
	        }
	     
	       
    public void readCalendarEvent(Context context){
    	{
    		
    		Uri uri = CalendarContract.Calendars.CONTENT_URI;
    		 long calID = 0;
    		String[] projection = new String[] {
    		       CalendarContract.Calendars._ID
    		};

    		Cursor calendarCursor =  context.getContentResolver()
                    .query(uri, projection, null, null, null);
    		while(calendarCursor.moveToNext()){
    			
    			 calID=calendarCursor.getLong(0);
    		}
    		String selection="(( "+Events.DELETED+" != 1 ))";
            Cursor cursor = context.getContentResolver()
                    .query(
                            Uri.parse("content://com.android.calendar/events"),
                            new String[] { "calendar_id", "title", "description",
                                    "dtstart", "dtend", "eventLocation", "_id as max_id" }, selection, null, "dtstart");
            cursor.moveToFirst();
            
            // fetching calendars name
            String CNames[] = new String[cursor.getCount()];

            calEvents.clear();
            todaysEventsNum = 0;
            tomorrowsEventsNum = 0;
            futureEventsNum = 0;
            thismonthEventsNum = 0;
            thismonthEventsDone = 0;
            previousLocations = "";

            Calendar c = Calendar.getInstance();
            for (int i = 0; i < CNames.length; i++) {
                 CNames[i] = cursor.getString(1);

         	    long start = Long.parseLong(cursor.getString(3));
         	    long end = start+60*60*1000;
         	    try {
         		   end = Long.parseLong(cursor.getString(4));
         	    } catch(NumberFormatException e) {}
         	   
         	   MyEvent payload = new MyEvent();
         	   payload.eventId = cursor.getLong(cursor.getColumnIndex("max_id"));
         	   payload.eventType = cursor.getString(1);
         	   payload.description = cursor.getString(2);
         	   payload.startDate = start;
         	   payload.endDate = end;
         	   payload.location = cursor.getString(5);
     	       addToPreviousLocations(cursor.getString(5));
         	   calEvents.add(payload);
        	   
         	   if (payload.isToday())              { todaysEventsNum++;    } 
         	   else if (payload.isTomorrow())      { tomorrowsEventsNum++; } 
         	   else if (payload.isFuture())        { futureEventsNum++;    }
         	   
         	   if (payload.isThisMonth())     { thismonthEventsNum++; }
         	   if (payload.isThisMonth(true)) { thismonthEventsDone++; }
         	   
                cursor.moveToNext();
            }  
            
            cursor.close();
            
     }
    	
    }
   //Read past events for a particular month 
public ArrayList<MyCalendarEvents> readPastCalendarEvents(Context context, int month,int year,Boolean showDay){
    	{
    		ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
    		 long today_date = new Date().getTime();
    		 String dformat =(showDay)? "MMM dd" : "hh:mm a";
    	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
    	       
    		 String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, //0
    				 							  CalendarContract.Events.TITLE,       //1
    				 							  CalendarContract.Events.DESCRIPTION, //2
    				 							  CalendarContract.Events.DTSTART,     //3
    				 							  CalendarContract.Events.DTEND,       //4
    				 							  CalendarContract.Events.ALL_DAY,     //5
    				 							  CalendarContract.Events.EVENT_LOCATION,//6
    				 							 CalendarContract.Events.STATUS};//7

    		// 0 = January, 1 = February, ...

   		
    		Calendar first_day_of_month = Calendar.getInstance();
    		first_day_of_month.set(year, month, 1);
    		Calendar last_day_of_month = Calendar.getInstance();
    		if(month==2){
    			last_day_of_month.set(year, month	, 28);
    		}else{
    			last_day_of_month.set(year, month, 30);
    		}

    		String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + first_day_of_month.getTimeInMillis() + 
    					" ) AND ( " + CalendarContract.Events.DTSTART + " <= " + last_day_of_month.getTimeInMillis() +
    					" ) AND ( "+ CalendarContract.Events.STATUS+" = "+Events.STATUS_CONFIRMED+
    					" ) AND ( "+ CalendarContract.Events.STATUS+" != "+Events.STATUS_CANCELED+
    					" ) AND ( "+Events.DELETED+" !=1 "+" ))";

    		Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), projection, selection, null, null );
    	
    		 cursor.moveToFirst();
    		 
    		     while ( cursor.isAfterLast()==false){
    		    	 Long startDate=Long.valueOf(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
    		    	 MyCalendarEvents calendarEvents=new MyCalendarEvents();
    		    	 calendarEvents.setEventStartDate(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
    		    	 calendarEvents.setEventEndDate(cursor.getString(cursor.getColumnIndex("dtend")));
    		    	 calendarEvents.setEventDescription(cursor.getString(cursor.getColumnIndex("description")));
    		    	 calendarEvents.setEventLocation(cursor.getString(cursor.getColumnIndex("eventLocation")));
    		    	 calendarEvents.setEventType(cursor.getString(cursor.getColumnIndex("title")));
    		    	 calendarEvents.setEventNumberCompleted(String.valueOf(cursor.getCount()));
    		    	 calendarEvents.setEventTime(formatter.format(startDate));
    		    	 list.add(calendarEvents);
    		    	 cursor.moveToNext();		
    		    }
    		     
            cursor.close();
        	return list;    
     }
    }
public ArrayList<MyCalendarEvents> readAllCalendarEvents(Context context){
	{
		ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		 long today_date = new Date().getTime();
	       
		 String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, //0
				 							  CalendarContract.Events.TITLE,       //1
				 							  CalendarContract.Events.DESCRIPTION, //2
				 							  CalendarContract.Events.DTSTART,     //3
				 							  CalendarContract.Events.DTEND,       //4
				 							  CalendarContract.Events.ALL_DAY,     //5
				 							  CalendarContract.Events.EVENT_LOCATION,//6
				 							  CalendarContract.Events.STATUS, //7 completed or not
				 							  CalendarContract.Events.AVAILABILITY, //8 personal or not
				 							  CalendarContract.Events.DELETED, //9
				 							  CalendarContract.Events._ID,//10
				 							 };//7
		 String[] projection2 = new String[] { CalendarContract.Reminders.MINUTES, //0
				 };//7
		String selection = "(( " + Events.DELETED+" !=1 "+" ))";

		Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.android.calendar/events"), projection, selection, null, null );
		
		 cursor.moveToFirst();
		 
		     while ( cursor.isAfterLast()==false){
		    	 Long startDate=Long.valueOf(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
		    	 long start = Long.parseLong(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
			  	    long end = start+60*60*1000;
			  	    try {
			  		   end = Long.parseLong(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTEND)));
			  	    } catch(NumberFormatException e) {}
		    	 MyCalendarEvents calendarEvents=new MyCalendarEvents();
		    	 calendarEvents.setEventStartDate(String.valueOf(start));
		    	 calendarEvents.setEventId(cursor.getString(cursor.getColumnIndex(CalendarContract.Events._ID)));
		    	 calendarEvents.setEventEndDate(String.valueOf(end));
		    	 calendarEvents.setEventDescription(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
		    	 calendarEvents.setEventLocation(cursor.getString(cursor.getColumnIndex( CalendarContract.Events.EVENT_LOCATION)));
		    	 calendarEvents.setEventType(cursor.getString(cursor.getColumnIndex( CalendarContract.Events.TITLE)));
		    	 if(Integer.valueOf(cursor.getString(cursor.getColumnIndex( CalendarContract.Events.AVAILABILITY)))==CalendarContract.Events.AVAILABILITY_BUSY){
		    		 calendarEvents.setEventCategory("personal");
		    	 }else if(Integer.valueOf(cursor.getString(cursor.getColumnIndex( CalendarContract.Events.AVAILABILITY)))==CalendarContract.Events.AVAILABILITY_FREE){
		    		 calendarEvents.setEventCategory("not_personal");
		    	 }
	    	 if(cursor.getString(cursor.getColumnIndex( CalendarContract.Events.STATUS))!=null){
		    		 if(Integer.valueOf(cursor.getString(cursor.getColumnIndex( CalendarContract.Events.STATUS)))==CalendarContract.Events.STATUS_CANCELED){
		    			 calendarEvents.setEventStatus("incomplete");
		    		 }else if(Integer.valueOf(cursor.getString(cursor.getColumnIndex( CalendarContract.Events.STATUS)))==CalendarContract.Events.STATUS_CONFIRMED){
		    		 calendarEvents.setEventStatus("complete");
		    		 }
		    	 }else{
		    		 calendarEvents.setEventStatus("");
		    	 }
		    	 calendarEvents.setEventDeleted(cursor.getString(cursor.getColumnIndex(Events.DELETED)));
		    	 calendarEvents.setEventNumberCompleted(String.valueOf(cursor.getCount()));
		    	 list.add(calendarEvents);
		    	 cursor.moveToNext();		
		    }
		     
        cursor.close();
    	return list;    
 }

	
}

//Count total number of events for a particular month
public ArrayList<MyCalendarEvents> readCalendarEventsTotal(Context context, int month,int year){
	{
		ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		 String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, //0
				 							  CalendarContract.Events.TITLE,       //1
				 							  CalendarContract.Events.DESCRIPTION, //2
				 							  CalendarContract.Events.DTSTART,     //3
				 							  CalendarContract.Events.DTEND,       //4
				 							  CalendarContract.Events.ALL_DAY,     //5
				 							  CalendarContract.Events.EVENT_LOCATION };//6

		Calendar first_day_of_month = Calendar.getInstance();
		first_day_of_month.set(year, month, 1);
		
		Calendar last_day_of_month = Calendar.getInstance();
		if(month==2){
			last_day_of_month.set(year, month, 28);
		}else{
			last_day_of_month.set(year, month, 30);
		}

		String selection2 = "(( " + CalendarContract.Events.DTSTART + " >= " + first_day_of_month.getTimeInMillis() +
							" ) AND ( " + CalendarContract.Events.DTSTART + " <= " + last_day_of_month.getTimeInMillis() +
							" ) AND ( "+Events.DELETED+" !=1 "+" ))";
		  Cursor cursor2 = context.getContentResolver()
                  .query(Uri.parse("content://com.android.calendar/events"),projection, selection2, null, null);
		// output the events 
		
		 cursor2.moveToFirst();
		
		     while (cursor2.isAfterLast()==false){
		    	 MyCalendarEvents calendarEvents=new MyCalendarEvents();
		    	 calendarEvents.setEventTotalNumber(String.valueOf(cursor2.getCount()));
		    	 list.add(calendarEvents);
		    	 cursor2.moveToNext();		
		    }
		     
        cursor2.close();
    	return list;    
 }

	
}

//Read future events within a particular month
public ArrayList<MyCalendarEvents> readFutureCalendarEvents(Context context, int month,int year,Boolean showDay){
	{
		ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
		long today_date = new Date().getTime();
		 String dformat =(showDay)? "MMM dd" : "hh:mm a";
	       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
		 String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, //0
				 							  CalendarContract.Events.TITLE,       //1
				 							  CalendarContract.Events.DESCRIPTION, //2
				 							  CalendarContract.Events.DTSTART,     //3
				 							  CalendarContract.Events.DTEND,       //4
				 							  CalendarContract.Events.ALL_DAY,     //5
				 							  CalendarContract.Events.EVENT_LOCATION,//6
				 							  Events.STATUS};//7

		// 0 = January, 1 = February, ...

		Calendar first_day_of_month = Calendar.getInstance();
		first_day_of_month.set(year, month, 1);
		
		Calendar last_day_of_month = Calendar.getInstance();
		if(month==2){
			last_day_of_month.set(year, month, 28);
		}else{
			last_day_of_month.set(year, month, 30);
		}

		String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + first_day_of_month.getTimeInMillis() + 
							" ) AND ( " + CalendarContract.Events.DTSTART + " <= " + last_day_of_month.getTimeInMillis() +
							//" ) AND ( "+ CalendarContract.Events.STATUS+" = "+Events.STATUS_CANCELED+
							" ) AND ( "+ CalendarContract.Events.STATUS+" != "+Events.STATUS_CONFIRMED+
						//	" ) AND ( "+ CalendarContract.Events.STATUS+" = "+Events.STATUS_TENTATIVE+
							" ) AND ( "+Events.DELETED+" !=1 "+" ))";

		Cursor cursor = context.getContentResolver().query( CalendarContract.Events.CONTENT_URI, projection, selection, null, null );
		 
		 cursor.moveToFirst();
		     while ( cursor.isAfterLast()==false){
		    	 Long startDate=Long.valueOf(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
		    	 MyCalendarEvents calendarEvents=new MyCalendarEvents();
		    	 calendarEvents.setEventStartDate(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
		    	 calendarEvents.setEventEndDate(cursor.getString(cursor.getColumnIndex("dtend")));
		    	 calendarEvents.setEventDescription(cursor.getString(cursor.getColumnIndex("description")));
		    	 calendarEvents.setEventLocation(cursor.getString(cursor.getColumnIndex("eventLocation")));
		    	 calendarEvents.setEventType(cursor.getString(cursor.getColumnIndex("title")));
		    	 calendarEvents.setEventNumberCompleted(String.valueOf(cursor.getCount()));
		    	 calendarEvents.setEventTime(formatter.format(startDate));
		    	 list.add(calendarEvents);
		    	 cursor.moveToNext();		
		    }
        cursor.close();
    	return list;    
	}
 }

	//Read future events within a particular month
	public ArrayList<MyCalendarEvents> readEventsToUpdate(Context context,Boolean showDay){
		{
			ArrayList<MyCalendarEvents> list =new ArrayList<MyCalendarEvents>(); 
			long today_date = new Date().getTime();
			 String dformat =(showDay)? "MMM dd" : "hh:mm a";
		       SimpleDateFormat formatter = new SimpleDateFormat(dformat);
			 String[] projection = new String[] { CalendarContract.Events._ID, //0
					 							  CalendarContract.Events.TITLE,       //1
					 							  CalendarContract.Events.DESCRIPTION, //2
					 							  CalendarContract.Events.DTSTART,     //3
					 							  CalendarContract.Events.DTEND,       //4
					 							  CalendarContract.Events.ALL_DAY,     //5
					 							  CalendarContract.Events.EVENT_LOCATION,//6
					 							  CalendarContract.Events.STATUS};//7

			// 0 = January, 1 = February, ...

			Date today=new Date();

String selection = "(( " + CalendarContract.Events.DTEND + " <= " + today.getTime() + 
					" ) AND ( " + CalendarContract.Events.STATUS + " != " + Events.STATUS_CONFIRMED + 
					" ) AND ( "+ CalendarContract.Events.STATUS+" != "+Events.STATUS_CANCELED+
					" ) AND ( "+Events.DELETED+" !=1 "+" ))";

			Cursor cursor = context.getContentResolver().query( CalendarContract.Events.CONTENT_URI, projection, selection, null, null );
			 
			 cursor.moveToFirst();
			     while ( cursor.isAfterLast()==false){
			    	 Long startDate=Long.valueOf(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
			    	 MyCalendarEvents calendarEvents=new MyCalendarEvents();
			    	 calendarEvents.setEventStartDate(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)));
			    	 calendarEvents.setEventEndDate(cursor.getString(cursor.getColumnIndex("dtend")));
			    	 calendarEvents.setEventDescription(cursor.getString(cursor.getColumnIndex("description")));
			    	 calendarEvents.setEventLocation(cursor.getString(cursor.getColumnIndex("eventLocation")));
			    	 calendarEvents.setEventType(cursor.getString(cursor.getColumnIndex("title")));
			    	 calendarEvents.setEventId(cursor.getString(cursor.getColumnIndex(Events._ID)));
			    	 calendarEvents.setEventTime(formatter.format(startDate));
			    	 list.add(calendarEvents);
			    	 cursor.moveToNext();		
			    }
	        cursor.close();
	    	return list;    
	 }
}
    private void addToPreviousLocations(String s)
    {
    	try {
    	   if ((! s.isEmpty()) && s.length() < 20) 
    	   {
    		   s = s.replace(",","");
    		   s = s.replace("'", "");
    		   s = s.toLowerCase(Locale.UK).trim();
    		   if (! previousLocations.contains(s)) {
        		   if (!  this.previousLocations.equals("")) { this.previousLocations += ","; }
    			   this.previousLocations += "\""+ s + "\"";
    		   }
    	   }
    	} catch (NullPointerException e) {}
    }

   
    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
}
    
    private String getDateTime(Long startDate,Boolean showDay) {
    	 String dformat =(showDay)? "MMM dd" : "hh:mm a";
	      SimpleDateFormat formatter = new SimpleDateFormat(dformat,Locale.getDefault());
        Date date = new Date();
        return formatter.format(date);
}
	

}
