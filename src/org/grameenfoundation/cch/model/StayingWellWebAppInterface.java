package org.grameenfoundation.cch.model;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.grameenfoundation.cch.activity.EventsAchievementsActivity;
import org.grameenfoundation.cch.activity.MagicAppRestart;
import org.grameenfoundation.cch.activity.StayingWellActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class StayingWellWebAppInterface {
    
	Context mContext;
    private DbHelper dbh;
    private long thirtydays = 2592000000L;
	private JSONObject d;
    

    /** Instantiate the interface and set the context */
    public StayingWellWebAppInterface(Context c) {
        mContext = c;
       dbh = new DbHelper(c);
    }
        
    @JavascriptInterface
    public String getUsername() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    	return prefs.getString(mContext.getString(R.string.prefs_display_name),
    						   mContext.getString(R.string.prefs_username));
    }
    
    @JavascriptInterface
    public String getGreeting() {
    	String username = getUsername();
    	username = (username.isEmpty()) ? username : " "+username;
    	String g = (dbh.getTime()=="") ? "Hello" : "Good " + dbh.getTime();
    	return g + username;
    }
    
    @JavascriptInterface
    public String getRoutineInfo(String infoType) {
  
       	// if no profile encourage client to sign up
    	String profile = getProfileStatus();
    	String plan = getMonthlyPlan();
    	
    	if (profile.isEmpty()|| plan.isEmpty()) {
    		return "Welcome to Staying Well";
    	} else {
        	ArrayList<RoutineActivity> todos = dbh.getSWRoutineActivities();
        	int numactivities = todos.size();    	
    	    	
        	String title = "You have <span class=\"highlight\">"+numactivities+"</span> activities this "+dbh.getTime();
    	
        	// Get routine items
        	if (infoType.equals("list"))
        	{
        		String val = "<h3 id=\"routine_title\">This "+dbh.getTime()+"'s activities:</h3><br/>";
        		for(RoutineActivity todo: todos) {
        			if (todo.isDone()) {
        				val += "<li><input type=\"checkbox\" disabled checked />&nbsp;&nbsp;"+todo.getAction()+"</li>";
        			} else {
            			val += "<li><input type=\"checkbox\" onclick=\"cch.markActivityDone(this, '"+todo.getUUID()+"');\" id=\""+todo.getUUID()+"\" value=\""+todo.getUUID()+"\" />&nbsp;&nbsp;"+todo.getAction()+"</li>";
        			}
        		}
    		
        		return val;
        	} else {
        		return title + ((infoType.equals("label")) ? " <span class=\"charet\"></span>": "");
        	}
    	}
    }
    
    @JavascriptInterface
    public void markRoutineDone(String uuid) {
    	dbh.insertSWRoutineDoneActivity(uuid);
    }
    
    @JavascriptInterface
    public String getLegalStatus() {
    	String status = dbh.getSWInfo(DbHelper.CCH_SW_LEGAL_STATUS);
    	return (status==null) ? "" : status;
    }
    
    @JavascriptInterface
    public void setLegalStatus(String status) {
    	dbh.updateSWInfo(DbHelper.CCH_SW_LEGAL_STATUS, status);
    }
    
    @JavascriptInterface
    public String getProfileStatus() {
    	String profile = dbh.getSWInfo(DbHelper.CCH_SW_PROFILE_STATUS);
    	return (profile==null) ? "" : profile;
    }
    
    @JavascriptInterface
    public void setProfileStatus(String status) {
    	String[] responses = status.split(",");
    	
    	int acount = 0;
    	int bcount = 0;
    	int ccount = 0;
    	
    	for(int i=0; i < responses.length; i++) {
    		if (responses[i].contains("A")) { acount++; }
    		if (responses[i].contains("B")) { bcount++; }
    		if (responses[i].contains("C")) { ccount++; }
    	}
    	
    	String profile = "";
    	if (acount >= bcount && acount >= ccount) { profile = "naana"; }
    	else if (bcount > acount && bcount >= ccount) { profile = "mary"; }
    	else if (ccount > acount && ccount > bcount) { profile = "michael"; }

    	dbh.updateSWInfo(DbHelper.CCH_SW_PROFILE_STATUS, profile);
    	dbh.updateSWInfo(DbHelper.CCH_SW_PROFILE_RESPONSES, status);
		
		// store results in plan
		Long time = System.currentTimeMillis();
		String data = "{'type':'profile', 'profile':'"+profile+"', 'responses':'"+status+"'}";
		 JSONObject d=new JSONObject();
		    try {
		    	d.put("type", "profile");
		    	d.put("profile", profile);
		    	d.put("responses", status);
		    	d.put("ver", dbh.getVersionNumber(mContext));
		    	d.put("battery", dbh.getBatteryStatus(mContext));
		    	d.put("device", dbh.getDeviceName());
				d.put("imei", dbh.getDeviceImei(mContext));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		this.saveToCCHLog(d.toString(), time.toString(), time.toString());
    }
     
    @JavascriptInterface
    public String getMonthlyPlan() {
    	String plan = dbh.getSWInfo(DbHelper.CCH_SW_MONTH_PLAN);
    	return (plan==null) ? "" : plan;
    }
    
    @JavascriptInterface
    public void setMonthlyPlan(String plan) {
    	Time time = new Time();
		time.setToNow();
    	dbh.updateSWInfo(DbHelper.CCH_SW_MONTH_PLAN, plan);
    	dbh.updateSWInfo(DbHelper.CCH_SW_MONTH_PLAN_LASTUPDATE, String.valueOf(time.toMillis(true)));
    	
    	// store results in plan
		Long t = System.currentTimeMillis();
    	String profile = getProfileStatus();
		String data = "{'type':'plan', 'plan':'"+plan+"', 'profile':'"+profile+"'}";
		 d=new JSONObject();
		    try {
		    	d.put("type", "plan");
		    	d.put("plan", plan);
		    	d.put("profile", profile);
		    	d.put("ver", dbh.getVersionNumber(mContext));
		    	d.put("battery", dbh.getBatteryStatus(mContext));
		    	d.put("device", dbh.getDeviceName());
				d.put("imei", dbh.getDeviceImei(mContext));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		this.saveToCCHLog(d.toString(), t.toString(), t.toString());
    }
    
    @JavascriptInterface
    public String changeMonthlyPlan() {
    	
    	String lastupdate = dbh.getSWInfo(DbHelper.CCH_SW_MONTH_PLAN_LASTUPDATE);
    	if (lastupdate==null || lastupdate.equals("")) {
    			return "false";
    	} else {
    		Long lu = Long.parseLong(lastupdate);
    		
    		Time time = new Time();
    		time.setToNow();
    			
    		if ((time.toMillis(true) - lu) > thirtydays ) {
    				return "true";
    		} else {
    				return "false";
    		}
    	}
    }
     
    @JavascriptInterface
    public String getFileTemplate(String filename)
    {	
    	try {
    		InputStream input = mContext.getAssets().open(filename);
    		byte[] buffer = new byte[input.available()];
    		input.read(buffer);
    		return new String(buffer);
    	} catch (IOException e) {
    		Log.e("CCH","cch: no file found: "+filename);
    		return "";
    	}
    }
    
    @JavascriptInterface
    public void saveToCCHLog(String data, String starttime, String endtime) {	
		dbh.insertCCHLog("Staying Well", data, starttime, endtime);
    }
    
    
    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }  
    
    @JavascriptInterface
    public void restartApp()
    {
         MagicAppRestart.doRestart((Activity) mContext);
    }
}
