package org.digitalcampus.oppia.service;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.activity.UpdateMonthlyTargetsActivity;
import org.digitalcampus.oppia.activity.UpdateWeeklyTargetsActivity;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.service.UpdateTargetsWeeklyService.MyBinder;

import com.bugsense.trace.BugSenseHandler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class UpdateMonthlyTargetService extends Service {
	public static final String TAG = UpdateTargetsService.class.getSimpleName();
	private final IBinder mBinder = new MyBinder();
	private SharedPreferences prefs;
	private int mId;
	
	@Override
	public void onCreate() {
		super.onCreate();
		BugSenseHandler.initAndStartSession(this,MobileLearning.BUGSENSE_API_KEY);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "Starting target update Service");
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.app_icon)
		        .setContentTitle("CHN on the go")
		        .setContentText("You have monthly targets to update.");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, UpdateMonthlyTargetsActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(UpdateMonthlyTargetsActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
		
		return Service.START_NOT_STICKY;
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public class MyBinder extends Binder {
		public UpdateMonthlyTargetService getService() {
			return UpdateMonthlyTargetService.this;
		}
	}
}
