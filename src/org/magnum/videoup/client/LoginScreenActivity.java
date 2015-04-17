package org.magnum.videoup.client;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import client.MainActivity;
import client.MovieSet;
import client.MovieSetInventory;
import client.MovieSetSvc;
import client.MovieSetSvcApi;
import client.Player;
import client.PlayerSvc;
import client.PlayerSvcApi;
import client.QuestionActivity;
import client.Score;
import client.ScoreSvc;
import client.ScoreSvcApi;
import db.MovieSetsDataSource;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/** 
 * 
 * This application uses ButterKnife. AndroidStudio has better support for
 * ButterKnife than Eclipse, but Eclipse was used for consistency with the other
 * courses in the series. If you have trouble getting the login button to work,
 * please follow these directions to enable annotation processing for this
 * Eclipse project: 8443
 * 
 * http://jakewharton.github.io/butterknife/ide-eclipse.html
 * 
 */
public class LoginScreenActivity extends Activity {

	protected static final String TAG = "LoginScreenActivity";

	@InjectView(R.id.userName)
	protected EditText userName_;

	@InjectView(R.id.password)
	protected EditText password_;

	@InjectView(R.id.server)
	protected EditText server_;

	Context mContext;
	Collection<MovieSet> mSets;
	private SharedPreferences mSharedPrefs;
	private SharedPreferences.Editor mSharedPrefsEditor;

	private Button mFBLoginButton;

	private MovieSetsDataSource dataSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_screen);
        Log.d(TAG , "LoginScreenActivity: onCreate()");
    // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "JTNXQePj9WmT2rxnsdb8I3ruV0sKQ6VBOqq7HxEe", "vHD7DHeLPp7svN433NT40Ns2USqZig8iL4yqzkCO");
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

		mContext = this.getApplicationContext();
		ButterKnife.inject(this);
		mSharedPrefs = getApplicationContext().getSharedPreferences("mutibo", MODE_PRIVATE);
		mSharedPrefsEditor = mSharedPrefs.edit();
		
		dataSource = MovieSetsDataSource.get(this);
//		dataSource.dropTable();
//		dataSource.createTable();
//		List<MovieSet> eventsInDb = dataSource.findAll();
//		Log.d(TAG , "Database contains " + eventsInDb.size() + " event rows");
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Opening local SQLite Database connection
		dataSource.open();
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Closing local SQLite Database 
		dataSource.close();
	}

    @OnClick(R.id.loginButton)
    public void parseLogin() {
        final String user = userName_.getText().toString();
        final String pass = password_.getText().toString();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.whereEqualTo("email", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.d("user", "Retrieved " + scoreList.get(0).get("email") + " scores");
                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });
    }

	//@OnClick(R.id.loginButton)
	public void login() {
		final String user = userName_.getText().toString();
		final String pass = password_.getText().toString();
		String server = server_.getText().toString();
		mSharedPrefsEditor.putString("user", user);
		mSharedPrefsEditor.putString("pass", pass);
		mSharedPrefsEditor.putString("server", server);
		//mSharedPrefsEditor.commit();

        parseLogin();
//		final VideoSvcApi svc = VideoSvc.init(server, user, pass);
//		final PlayerSvcApi playerSvc = PlayerSvc.init(server, user, pass);
//
//		CallableTask.invoke(new Callable<Collection<Player>>() {
//
//			@Override
//			public Collection<Player> call() throws Exception {
//				return playerSvc.findByEmail(user);
//			}
//		}, new TaskCallback<Collection<Player>>() {
//
//			@Override
//			public void success(Collection<Player> result) {
//
//				int added = 0;
//				if(result.size() > 0) {
//					//Log.d("MovieSetInventory", "retrieved set: " + ((MovieSet)result.toArray()[1]).getId());
//					for(Player p:result) {
//						Log.d("Player", "retrieved player: " + p.getFirst() + ", password: " + p.getPassword() + ", id: " + p.getId());
//
//						mSharedPrefsEditor.putString("first", p.getFirst());
//						mSharedPrefsEditor.putString("last", p.getLast());
//						mSharedPrefsEditor.putLong("player_id", p.getId());
//					}
//					mSharedPrefsEditor.commit();
//				}
//				Log.d("CallableTask", result.size() + " players were found.");
//				// OAuth 2.0 grant was successful and we
//				// can talk to the server, open up the video listing
//				startActivity(new Intent(
//						LoginScreenActivity.this,
//						MainActivity.class));
//			}
//
//			@Override
//			public void error(Exception e) {
//				Log.e(LoginScreenActivity.class.getName(), "Error logging in via OAuth.", e);
//
//				Toast.makeText(
//						LoginScreenActivity.this,
//						"Login failed, check your Internet connection and credentials.",
//						Toast.LENGTH_SHORT).show();
//			}
//		});

		
//		CallableTask.invoke(new Callable<Collection<Video>>() {
//
//			@Override
//			public Collection<Video> call() throws Exception {
//				return svc.getVideoList();
//			}
//		}, new TaskCallback<Collection<Video>>() {
//
//			@Override
//			public void success(Collection<Video> result) {
//				// OAuth 2.0 grant was successful and we
//				// can talk to the server, open up the video listing
//				startActivity(new Intent(
//						LoginScreenActivity.this,
//						VideoListActivity.class));
//			}
//
//			@Override
//			public void error(Exception e) {
//				Log.e(LoginScreenActivity.class.getName(), "Error logging in via OAuth.", e);
//				
//				Toast.makeText(
//						LoginScreenActivity.this,
//						"Login failed, check your Internet connection and credentials.",
//						Toast.LENGTH_SHORT).show();
//			}
//		});
	}
	
	public void popupRegistrationDialog(View v) {
		final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.login_dialog);
        dialog.setTitle("New Player Registration");

        //TextView headerTV = (TextView)dialog.findViewById(R.id.guestListDialogHeader);

		//mCalendarTempImage = (ImageView)findViewById(R.id.calendarImageView);
		
//		mFBLoginButton = (Button) findViewById(R.id.loginButton);
//		mFBLoginButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Log.d(TAG, "fbLoginButton was clicked");
//				//loginFacebook();
//			}
//		});
        Button closeButton=(Button)dialog.findViewById(R.id.loginCloseButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "closeButton was clicked");
				dialog.cancel();
			}

		});
        Button loginOpenButton=(Button)dialog.findViewById(R.id.loginOpenButton);
        loginOpenButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "loginOpenButton was clicked");
				//loginFacebook();
				dialog.cancel();
			}

		});
        dialog.show();
	}

}
