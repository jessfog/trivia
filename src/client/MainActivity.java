package client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import org.magnum.videoup.client.CallableTask;
import org.magnum.videoup.client.LoginScreenActivity;
import org.magnum.videoup.client.R;
import org.magnum.videoup.client.TaskCallback;

import db.MovieSetsDataSource;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static SharedPreferences mSharedPrefs;
	private SharedPreferences.Editor mSharedPrefsEditor;
	private static String TAG = "MainActivity";
	private static String mUser;
	private static Context mContext;
	private static ScoreSvcApi scoreSvc;
	private static Collection<Score> mScores;
	private static Collection<MovieSet> mSets;
	private static Collection<Challenge> mChallenges;
	private String mPass;
	private String mServer;
	private MovieSetSvcApi movieSvc;
	private static ChallengeSvcApi challengeSvc;
	private static long mPlayerId;
	private static MovieSetsDataSource dataSource;
	// Camera/Gallery variables
	private static int GALLERY_CODE = 1;
	private static int CAMERA_CODE = 2;
	private static Bitmap selectedBitamp = null;
	private static Paint mPaint;
	private static ImageView profileImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}

		mSharedPrefs = getApplicationContext().getSharedPreferences("mutibo", MODE_PRIVATE);
		mSharedPrefsEditor = mSharedPrefs.edit();
		
		mUser = mSharedPrefs.getString("user", null);

		mPlayerId = mSharedPrefs.getLong("player_id", -1);
		mPass = mSharedPrefs.getString("pass", null);
		mServer = mSharedPrefs.getString("server", null);
		movieSvc = MovieSetSvc.init(mServer, mUser, mPass);
		
		getMovieSetsFromDBServer(mServer, mPass);

		dataSource = MovieSetsDataSource.get(this);
		dataSource.open();
		int diff = 2;
		List<MovieSet> eventsInDb = dataSource.fetchSetsByDifficulty(diff);
		Log.d(TAG , "Database contains " + eventsInDb.size() + " with difficulty of: " + diff);
		
		mContext = this.getApplicationContext();
		mSharedPrefs = getApplicationContext().getSharedPreferences("mutibo", MODE_PRIVATE);
		mSharedPrefsEditor = mSharedPrefs.edit();

		scoreSvc = ScoreSvc.init(mServer, mUser, mPass);
		challengeSvc = ChallengeSvc.init(mServer, mUser, mPass);
	}

	private void getMovieSetsFromDBServer(final String user, final String pass) {
		CallableTask.invoke(new Callable<Collection<MovieSet>>() {

			@Override
			public Collection<MovieSet> call() throws Exception {
				Log.d(TAG, user + ", " + pass);
				return movieSvc.getMovieSetList();
			}
		}, new TaskCallback<Collection<MovieSet>>() {

			@Override
			public void success(Collection<MovieSet> result) {
				mSets = result;
				int added = 0;
				if(result.size() > 0) {
//					Log.d("MovieSetInventory", "retrieved set: " + ((MovieSet)result.toArray()[1]).getId());
					for(MovieSet set:result) {
						dataSource.create(set);
						Log.d("MovieSetInventory", "retrieved set: " + set.getId());
					}
					added = MovieSetInventory.get(mContext).addMovieSets(result);
				}
				Log.d("CallableTask", mSets.size() + " movieSets were found and " + added + " were added to inventory");
				// OAuth 2.0 grant was successful and we
				// can talk to the server, open up the video listing
			}

			@Override
			public void error(Exception e) {
				Log.e(LoginScreenActivity.class.getName(), "Error logging in via OAuth.", e);
				
				Toast.makeText(
						MainActivity.this,
						"Retrieving MovieSets failed :(, check your Internet connection and credentials.",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Opening local SQLite Database connection
		dataSource.open();
		
		//TODO refresh high score table.
		// Will need to create dataSource for scores table-
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Closing local SQLite Database 
		dataSource.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private TextView mPlayerNameTV;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			profileImage = (ImageView) rootView.findViewById(R.id.imageView1);
			String profile_pic = mSharedPrefs.getString("profile_pic", null);
			Log.d(TAG, "profile_pic retrieved from preferences: " + profile_pic);
			Uri uri = null;
			if(profile_pic != null) {
				uri =Uri.parse(profile_pic);
				Log.d(TAG, "uri retrieved from preferences: " + uri.getEncodedPath());
				createBitmapFromUri(uri);
			}
			else {

				Log.d(TAG, "uri NOT FOUND in preferences");
			}
			getScores();

			getChallengesFromDBServer();

			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			mPlayerNameTV = (TextView) this.getActivity().findViewById(R.id.playerNameTextView);
			mPlayerNameTV.setText(mUser);
//			if(mScores != null && mScores.size() > 0) {
//				updateScoreTable();
//			}
		}

		
		private void getScores() {

			CallableTask.invoke(new Callable<Collection<Score>>() {

				@Override
				public Collection<Score> call() throws Exception {
					return scoreSvc.getList();
				}
			}, new TaskCallback<Collection<Score>>() {

				@Override
				public void success(Collection<Score> result) {
					mScores = result;
					int added = 0;
					if(result.size() > 0) {
						updateScoreTable();
//						for(Score set:result) {
//							Log.d("CallableTask", "retrieved score: " + set.getScore());
//						}
					}
					Log.d("CallableTask", mScores.size() + " scores were found");
					
				}

				@Override
				public void error(Exception e) {
					Log.e(LoginScreenActivity.class.getName(), "Error logging in via OAuth.", e);
					
					Toast.makeText(
							mContext,
							"Login failed, check your Internet connection and credentials.",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		private void updateScoreTable() {
			TableLayout scores = (TableLayout)getActivity().findViewById(R.id.tableLayout2);
		    scores.setStretchAllColumns(true);
		    scores.bringToFront();
		    List<Score> list = new ArrayList<Score>();
		    list.addAll(mScores);
		    Comparator<Score> comparator = new Comparator<Score>() {
		        public int compare(Score c1, Score c2) {
		            return (int) (c2.getScore() - c1.getScore()); // use your logic
		        }
		    };

		    Collections.sort(list, comparator); // use the comparator as much as u want
		    System.out.println(list);
		    
		    for(Score score:list){
		        TableRow tr =  new TableRow(mContext);
		        TextView c1 = new TextView(mContext);
		        StringBuilder sb = new StringBuilder();
		        sb.append(score.getFirst()); 
		        sb.append(" ");
		        sb.append(score.getLast());
		        c1.setText(sb);
		        c1.setTextColor(Color.BLACK);
		        c1.setTextSize(15);
		        c1.setGravity(Gravity.CENTER);
		        c1.setAllCaps(true);
//		        c1.setBackgroundColor(R.drawable.border);
		        TextView c2 = new TextView(mContext);
		        c2.setText((int)score.getScore()+"");
		        c2.setTextColor(Color.BLACK);
		        c2.setTextSize(15);
		        c2.setGravity(Gravity.CENTER);
		        tr.addView(c1,0);
		        tr.addView(c2);
		        scores.addView(tr);
		    }
		}

		private boolean getChallengesFromDBServer() {

			Log.i(TAG, "Will attempt to initialize ChallengeSvcApi by: " + mUser);
			CallableTask.invoke(new Callable<Collection<Challenge>>() {

				@Override
				public Collection<Challenge> call() throws Exception {
					Log.d("CallableTask", "Will attempt to retrieve Challenges for user: " + mPlayerId);
					
					return challengeSvc.findByChallengedUserId(mPlayerId);
				}
			}, new TaskCallback<Collection<Challenge>>() {


				@Override
				public void success(Collection<Challenge> result) {
					Log.d("CallableTask", "Challenges were retrieved!");
					mChallenges = result;
					int added = 0;
					if(result != null && result.size() > 0) {
//						Log.d("MovieSetInventory", "retrieved set: " + ((MovieSet)result.toArray()[1]).getId());
						for(Challenge set:result) {
							//dataSource.create(set); TODO save the challenges in local table or Service
							Log.d("CallableTask", "retrieved challenge: " + set.getChallengerId());
						}
						updateChallengeTable();
					}
					Log.d("CallableTask", mSets.size() + " movieSets were found and " + added + " were added to inventory");
				}

				@Override
				public void error(Exception e) {
					Log.e(MainActivity.class.getName(), "Error logging in via OAuth.", e);
					e.getMessage();
					Toast.makeText(
							mContext,
							"Retrieving getChallengesFromDBServer failed.",
							Toast.LENGTH_SHORT).show();
					
				}
			});
			return false;
		}

		private void updateChallengeTable() {
			TableLayout challenges = (TableLayout)getActivity().findViewById(R.id.tableLayout4);
		    challenges.setStretchAllColumns(true);
		    challenges.bringToFront();
		    List<Challenge> list = new ArrayList<Challenge>();
		    list.addAll(mChallenges);
		    Comparator<Challenge> comparator = new Comparator<Challenge>() {
		        public int compare(Challenge c1, Challenge c2) {
		            return (int) (c1.getScore() - c2.getScore()); // use your logic
		        }
		    };

		    Collections.sort(list, comparator); // use the comparator as much as u want
		    System.out.println(list);
		    
		    for(Challenge challenge:list){
		        TableRow tr =  new TableRow(mContext);
		        TextView c1 = new TextView(mContext);
		        StringBuilder sb = new StringBuilder();
		        sb.append(challenge.getChallengerFirst()); 
		        sb.append(" ");
		        sb.append(challenge.getChallengerLast());
		        c1.setText(sb);
		        c1.setTextColor(Color.BLACK);
		        c1.setTextSize(15);
		        c1.setGravity(Gravity.CENTER);
		        c1.setAllCaps(true);
//		        c1.setBackgroundColor(R.drawable.border);
		        TextView c2 = new TextView(mContext);
		        c2.setText((int)challenge.getScore()+"");
		        c2.setTextColor(Color.BLACK);
		        c2.setTextSize(15);
		        c2.setGravity(Gravity.CENTER);
		        tr.addView(c1,0);
		        tr.addView(c2);
		        challenges.addView(tr);
		    }
		}
	}
	
	public void startGame(View v) {
		Log.i(TAG, "Clicked newGameButton");
		//Toast.makeText(this, "Clicked newGameButton", Toast.LENGTH_LONG).show();
		startActivity(new Intent(MainActivity.this, QuestionActivity.class));
		
	}
	
	public void launchFriends(View v) {
		startActivity(new Intent(mContext, FriendsActivity.class));
	}
	

	//Method to allow user to select a Photo
	public boolean launchPhotoChooser(View v) {
		boolean selected = false;
		Log.w(TAG, "inside launchPhotoChooser");
		//Create intent object to retrieve photos
		Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
		chooserIntent.setType("image/*");
		//Make sure there are apps that can be used to choose photos
		if (chooserIntent.resolveActivity(mContext.getPackageManager()) != null) {
		    //startActivity(chooserIntent);
			this.startActivityForResult(chooserIntent, GALLERY_CODE);
		}
		else {
			Toast toast = Toast.makeText(mContext, "No photo apps outputStreamund", Toast.LENGTH_LONG);
			toast.show();
		}
		
		return selected ;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
			Uri mPhotoUri = data.getData();
			mSharedPrefsEditor.putString("profile_pic", mPhotoUri.toString());
			mSharedPrefsEditor.commit();
        	Log.i(TAG, "onActivityResult; uri: " + mPhotoUri.toString());
			createBitmapFromUri(mPhotoUri);
		}
	}

	private static void createBitmapFromUri(Uri mPhotoUri) {
		InputStream photoStream = null;
		try {
			Log.i(TAG, "createBitmapFromUri = " + mPhotoUri);
			photoStream = mContext.getContentResolver().openInputStream(mPhotoUri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(photoStream, null, options);
			int w = options.outWidth;
			int h = options.outHeight;
			photoStream.close();
			//figure out how large our bitmap should be
			int newW = mContext.getResources().getDisplayMetrics().widthPixels;
			int newH = mContext.getResources().getDisplayMetrics().heightPixels;
			options.inJustDecodeBounds = false;
			int sampleRate = 1;
			while(w > sampleRate * newW || h > sampleRate * newH) {
				sampleRate *= 2;
			}
			Log.i(TAG, "New image sampleRate = " + sampleRate);
			options.inSampleSize = sampleRate;
			photoStream = mContext.getContentResolver().openInputStream(mPhotoUri);
			Bitmap tempBM = BitmapFactory.decodeStream(photoStream, null, options);
			photoStream.close();
			if(selectedBitamp != null) {
				selectedBitamp.recycle();
			}
			createBitmap(tempBM);
			//mCaptionText.setText("");
			//addTextToBitmap();
//				TextPaint tp = new TextPaint();
//				tp.setTextSize(newH/10);
//				tp.setColor(0xff00ff00);
//				mCanvas.drawText(mCaptionText.getText().toString(), 20, newH-40, tp);
			
			profileImage.setImageBitmap(selectedBitamp);
			tempBM.recycle();
		} catch (Exception e) {
			Log.e(TAG, "Error attempting to display selected Photo", e);
			return;
		}
	}
	
	//Create a new bitmap from the selected photo
	public static void createBitmap(Bitmap tempBM) {
		if(tempBM != null) {
			int newW = tempBM.getWidth();
			int newH = tempBM.getHeight();
			selectedBitamp = Bitmap.createBitmap(newW, newH, Bitmap.Config.ARGB_8888);
			
			Canvas mCanvas = new Canvas(selectedBitamp);
			mCanvas.drawColor(0xff000000);
			mPaint = new Paint();
			mPaint.setColor(0xffff0000);
			mPaint.setStrokeWidth(25);
			mCanvas.drawBitmap(tempBM, 0, 0, mPaint);
			mCanvas.drawLine(0, 0, newW, 0, mPaint);
			mCanvas.drawLine(newW, 0, newW, newH, mPaint);
			mCanvas.drawLine(newW, newH, 0, newH, mPaint);
			mCanvas.drawLine(0, newH, 0, 0, mPaint);
			mCanvas.save();
		}
	}
}
