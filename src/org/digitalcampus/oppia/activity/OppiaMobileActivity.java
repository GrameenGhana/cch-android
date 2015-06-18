/* 
 * This file is part of OppiaMobile - http://oppia-mobile.org/
 * 
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digitalcampus.oppia.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.adapter.CourseListAdapter;
import org.digitalcampus.oppia.adapter.NewCourseListAdapter;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.application.MobileLearning;
import org.digitalcampus.oppia.exception.CourseNotFoundException;
import org.digitalcampus.oppia.listener.ScanMediaListener;
import org.digitalcampus.oppia.model.Activity;
import org.digitalcampus.oppia.model.Course;
import org.digitalcampus.oppia.model.Lang;
import org.digitalcampus.oppia.model.Tag;
import org.digitalcampus.oppia.task.Payload;
import org.digitalcampus.oppia.task.ScanMediaTask;
import org.digitalcampus.oppia.utils.FileUtils;
import org.digitalcampus.oppia.utils.UIUtils;
import org.grameenfoundation.cch.activity.CourseGroupActivity;
import org.grameenfoundation.cch.activity.HomeActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class OppiaMobileActivity extends AppActivity implements OnSharedPreferenceChangeListener, ScanMediaListener {

	public static final String TAG = OppiaMobileActivity.class.getSimpleName();
	private SharedPreferences prefs;
	private ArrayList<Course> courses;
	private Course tempCourse;
	private Bundle bundle;
	private String courseGroup;
	private Button download;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
		getActionBar().setDisplayShowHomeEnabled(true);
	    getActionBar().setTitle("Learning Center");
	    getActionBar().setSubtitle("Learning Modules");
	    download=(Button) this.findViewById(R.id.button_download);
		// set preferred lang to the default lang
		if (prefs.getString(getString(R.string.prefs_language), "").equals("")) {
			Editor editor = prefs.edit();
			editor.putString(getString(R.string.prefs_language), Locale.getDefault().getLanguage());
			editor.commit();
		}
		Bundle extras = getIntent().getExtras(); 
		 if(extras != null) {
	        	//Course t = (Course) bundle.getSerializable(Course.TAG);
	        	//System.out.println(t.getCourseGroup());
	        	courseGroup = extras.getString("courseGroup");
	        } else {
	        	courseGroup="Family planning";
	        }
		
			download.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					startActivity(new Intent(OppiaMobileActivity.this, TagSelectActivity.class));
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				}
				
			});
	}

	@Override
	public void onStart() {
		super.onStart();		
		displayCourses();		
	}

	@Override
	public void onResume(){
		super.onResume();
		this.updateReminders();
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	private void displayCourses() {

		DbHelper db = new DbHelper(this);
		courses = db.getCoursesWithGroups(courseGroup);
		db.close();
		
		if(MobileLearning.createDirs()){
			// only remove courses if the SD card is present 
			//- else it will remove the courses just because the SD card isn't in
			ArrayList<Course> removeCourses = new ArrayList<Course>();
			for (Course c : courses) {
				try {
					c.validate();
				} catch (CourseNotFoundException cnfe){
					// remove from database
					cnfe.deleteCourse(this, c.getModId());
					removeCourses.add(c);
				}
			}
			
			for(Course c: removeCourses){
				// remove from current list
				courses.remove(c);
			}
		}

		LinearLayout llLoading = (LinearLayout) this.findViewById(R.id.loading_courses);
		llLoading.setVisibility(View.GONE);
		LinearLayout llNone = (LinearLayout) this.findViewById(R.id.no_courses);
		if (courses.size() > 0) {
			llNone.setVisibility(View.GONE);
		} else {
			llNone.setVisibility(View.VISIBLE);
			Button manageBtn = (Button) this.findViewById(R.id.manage_courses_btn);
			download.setVisibility(View.GONE);
			manageBtn.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(OppiaMobileActivity.this, TagSelectActivity.class));
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				}
			});
		}
		ArrayList<String> c=new ArrayList<String>();
		for(int i=0;i<courses.size();i++){
			c.add(courses.get(i).getTitle(prefs.getString(this.getString(R.string.prefs_language), Locale.getDefault().getLanguage())));
			Collections.sort(c);
		}
		CourseListAdapter mla = new CourseListAdapter(this, courses,c);
		ListView listView = (ListView) findViewById(R.id.course_list);
		listView.setAdapter(mla);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Course m = (Course) view.getTag();
				Intent i = new Intent(OppiaMobileActivity.this, CourseIndexActivity.class);
				Bundle tb = new Bundle();
				tb.putSerializable(Course.TAG, m);
				i.putExtras(tb);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			}
		});

		this.updateReminders();
		
		// scan media
		this.scanMedia();
	}

	private void updateReminders(){
		if(prefs.getBoolean(getString(R.string.prefs_schedule_reminders_show), false)){
			DbHelper db = new DbHelper(OppiaMobileActivity.this);
			int max = Integer.valueOf(prefs.getString(getString(R.string.prefs_schedule_reminders_no), "2"));
			ArrayList<Activity> activities = db.getActivitiesDue(max);
			db.close();
			this.drawReminders(activities);
		} else {
			LinearLayout ll = (LinearLayout) findViewById(R.id.schedule_reminders);
			ll.setVisibility(View.GONE);
		}		
	}
	
	private void scanMedia() {
		long now = System.currentTimeMillis()/1000;
		if (prefs.getLong(getString(R.string.prefs_last_media_scan), 0)+3600 > now) {
			LinearLayout ll = (LinearLayout) this.findViewById(R.id.home_messages);
			ll.setVisibility(View.GONE);
			return;
		}
		ScanMediaTask task = new ScanMediaTask();
		Payload p = new Payload(this.courses);
		task.setScanMediaListener(this);
		task.execute(p);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		UIUtils.showUserData(menu,this);
	    return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.menu_about) {
			startActivity(new Intent(this, AboutActivity.class));
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			return true;
		} else if (itemId == R.id.menu_download) {
			startActivity(new Intent(this, TagSelectActivity.class));
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			return true;
		} else if (itemId == R.id.menu_settings) {
			Intent i = new Intent(this, PrefsActivity.class);
			Bundle tb = new Bundle();
			ArrayList<Lang> langs = new ArrayList<Lang>();
			for(Course m: courses){
				langs.addAll(m.getLangs());
			}
			tb.putSerializable("langs", langs);
			i.putExtras(tb);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			return true;
		} else if (itemId == R.id.menu_help) {
			startActivity(new Intent(this, HelpActivity.class));
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			return true;
		} else if (itemId == R.id.menu_logout) {
			logout();
			return true;
		}
		return true;
	}
	


	private void createLanguageDialog() {
		ArrayList<Lang> langs = new ArrayList<Lang>();
		for(Course m: courses){
			langs.addAll(m.getLangs());
		}
		
		UIUtils ui = new UIUtils();
    	ui.createLanguageDialog(this, langs, prefs, new Callable<Boolean>() {	
			public Boolean call() throws Exception {
				OppiaMobileActivity.this.onStart();
				return true;
			}
		});
	}

	private void logout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.logout);
		builder.setMessage(R.string.logout_confirm);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// wipe activity data
				DbHelper db = new DbHelper(OppiaMobileActivity.this);
				db.onLogout();
				db.close();

				// restart the app
				OppiaMobileActivity.this.startActivity(new Intent(OppiaMobileActivity.this, StartUpActivity.class));
				OppiaMobileActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_left);

			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return; // do nothing
			}
		});
		builder.show();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.course_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		android.widget.AdapterView.AdapterContextMenuInfo info = (android.widget.AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		tempCourse = (Course) info.targetView.getTag();
		int itemId = item.getItemId();
		if (itemId == R.id.course_context_delete) {
			confirmCourseDelete();
			return true;
		} else if (itemId == R.id.course_context_reset) {
			confirmCourseReset();
			return true;
		}
		return true;
	}

	private void confirmCourseDelete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.course_context_delete);
		builder.setMessage(R.string.course_context_delete_confirm);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// remove db records
				DbHelper db = new DbHelper(OppiaMobileActivity.this);
				db.deleteCourse(tempCourse.getModId());
				db.close();
				// remove files
				File f = new File(tempCourse.getLocation());
				FileUtils.deleteDir(f);
				Editor e = prefs.edit();
				e.putLong(getString(R.string.prefs_last_media_scan), 0);
				e.commit();
				displayCourses();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				tempCourse = null;
			}
		});
		builder.show();
	}

	private void confirmCourseReset() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.course_context_reset);
		builder.setMessage(R.string.course_context_reset_confirm);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				DbHelper db = new DbHelper(OppiaMobileActivity.this);
				db.resetCourse(tempCourse.getModId());
				db.close();
				displayCourses();
			}
		});
		builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				tempCourse = null;
			}
		});
		builder.show();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, key + " changed");
		if(key.equalsIgnoreCase(getString(R.string.prefs_server))){
			Editor editor = sharedPreferences.edit();
			if(!sharedPreferences.getString(getString(R.string.prefs_server), "").endsWith("/")){
				String newServer = sharedPreferences.getString(getString(R.string.prefs_server), "").trim()+"/";
				editor.putString(getString(R.string.prefs_server), newServer);
		    	editor.commit();
			}
		}
		if(key.equalsIgnoreCase(getString(R.string.prefs_schedule_reminders_show)) || key.equalsIgnoreCase(getString(R.string.prefs_schedule_reminders_no))){
			displayCourses();
		}
		if(key.equalsIgnoreCase(getString(R.string.prefs_points)) || key.equalsIgnoreCase(getString(R.string.prefs_badges))){
			supportInvalidateOptionsMenu();
		}
	}

	public void scanStart() {
		TextView tv = (TextView) this.findViewById(R.id.home_message);
		tv.setText(this.getString(R.string.info_scan_media_start));
	}

	public void scanProgressUpdate(String msg) {
		TextView tv = (TextView) this.findViewById(R.id.home_message);
		tv.setText(this.getString(R.string.info_scan_media_checking, msg));
	}

	public void scanComplete(Payload response) {
		Editor e = prefs.edit();
		LinearLayout ll = (LinearLayout) this.findViewById(R.id.home_messages);
		TextView tv = (TextView) this.findViewById(R.id.home_message);
		Button btn = (Button) this.findViewById(R.id.message_action_button);
		
		if (response.getResponseData().size() > 0) {
			ll.setVisibility(View.VISIBLE);
			tv.setText(this.getString(R.string.info_scan_media_missing));
			btn.setText(this.getString(R.string.scan_media_download_button));
			btn.setTag(response.getResponseData());
			btn.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {
					@SuppressWarnings("unchecked")
					ArrayList<Object> m = (ArrayList<Object>) view.getTag();
					Intent i = new Intent(OppiaMobileActivity.this, DownloadMediaActivity.class);
					Bundle tb = new Bundle();
					tb.putSerializable(DownloadMediaActivity.TAG, m);
					i.putExtras(tb);
					startActivity(i);
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
				}
			});
			e.putLong(getString(R.string.prefs_last_media_scan), 0);
			e.commit();
		} else {
			ll.setVisibility(View.GONE);
			tv.setText("");
			btn.setText("");
			btn.setOnClickListener(null);
			btn.setTag(null);
			long now = System.currentTimeMillis()/1000;
			e.putLong(getString(R.string.prefs_last_media_scan), now);
			e.commit();
		}
	}
}
