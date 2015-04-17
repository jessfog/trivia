package client;

import java.util.List;
import java.util.concurrent.Callable;

import org.magnum.videoup.client.CallableTask;
import org.magnum.videoup.client.LoginScreenActivity;
import org.magnum.videoup.client.R;
import org.magnum.videoup.client.TaskCallback;
import org.magnum.videoup.client.R.id;
import org.magnum.videoup.client.R.layout;
import org.magnum.videoup.client.R.menu;

import db.MovieSetsDataSource;
import db.SpringDataREST;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class QuestionActivity extends Activity {
	private static PlaceholderFragment questionFragment;

	private static SharedPreferences mSharedPrefs;
	private static SharedPreferences.Editor mSharedPrefsEditor;

	private static String TAG = "QuestionActivity";
	private static Context mContext;
	private static MovieSetsDataSource dataSource;
	private static List<MovieSet> mSetsInGame;
	private static boolean fromSQLite = false;
	private static boolean gameOver = false;

	private int mDifficultyOfGame = 1;

	private static Animation mAlphaAnim;
	private static Animation mCorrectAnim;
	//UI
	private static Button choice1;
	private static Button choice2;
	private static Button choice3;
	private static Button choice4;
	
	static TextView timerTextView;
	static TextView correctTextView;
    static int TIME_PER_ROUND = 5;
    static long startTime = TIME_PER_ROUND;
    static boolean mTimerStarted = false;

    //runs without a timer by reposting this handler at the end of the runnable
    static Handler timerHandler = new Handler();
    static Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
			timerHandler.removeCallbacks(timerRunnable);
            long millis = System.currentTimeMillis() + startTime;
            if(startTime > 0) {
	            int seconds = (int) (millis / 1000);
	            int minutes = seconds / 60;
	            seconds = seconds % 60;
	            if(seconds > 0)
	            	millis = millis%seconds;
	
	            timerHandler.postDelayed(this, 1000);
            }
            else {
                timerHandler.removeCallbacks(timerRunnable);
                mTimerStarted = false;
                questionFragment.popupLoginDialog("none");
            }
            timerTextView.setText(String.format("%d", startTime));
            startTime -= 1;
            
        }
    };

	private static ImageButton mTimerButton;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question);
		
		if (savedInstanceState == null) {
			questionFragment = new PlaceholderFragment();
			getFragmentManager().beginTransaction()
					.add(R.id.container, questionFragment).commit();
		}
		mContext = this.getApplicationContext();
		mSharedPrefs = getApplicationContext().getSharedPreferences("mutibo", MODE_PRIVATE);
		mSharedPrefsEditor = mSharedPrefs.edit();

		dataSource = MovieSetsDataSource.get(this);
		dataSource.open();
		mSetsInGame = dataSource.fetchSetsByDifficulty(mDifficultyOfGame);
		if(mSetsInGame.size() > 0) {
			fromSQLite = true;
		}
		Log.d(TAG , "Database contains " + mSetsInGame.size() + " with difficulty of: " + mDifficultyOfGame);
		
		//animation
		mAlphaAnim = AnimationUtils.loadAnimation(this, R.anim.question_choice_animation);
		mCorrectAnim = AnimationUtils.loadAnimation(this, R.anim.correct_choice_animation);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus) {
			
		}
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
		//remove callback if game is paused or destroyed
		timerHandler.removeCallbacks(timerRunnable);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.question, menu);
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

		private MovieSet mSet;
		private TextView questionTV;
		private Button askFriendButton, skipButton;
		private TextView roundTV, pointsTV, scoreTV;
		private int mRound = 0;
		private int mScore = 0;
		private int mLives = 3;
		private int mCorrectCount = 0;
		private int mSkipped = 0;
		
		
		public PlaceholderFragment() {
		}
		
		public void popupLoginDialog(String title) {
			View popupView = getActivity().getLayoutInflater().inflate(R.layout.movie_dialog, null);

		    final PopupWindow popupWindow = new PopupWindow(popupView, 
		                           LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		    //popupWindow.setFocusable(true);
		    ColorDrawable cd = new ColorDrawable();
		    cd.setColor(Color.LTGRAY);
		    popupWindow.setBackgroundDrawable(cd);
		    popupWindow.setAnimationStyle(Animation.REVERSE);
		    popupWindow.showAtLocation(this.getView(), Gravity.CENTER, 0, 0);
		    
		    TextView questionTV = (TextView) popupView.findViewById(R.id.movieQuestionTextView);
		    questionTV.setText(mSet.getQuestion());
		    TextView correctTV = (TextView) popupView.findViewById(R.id.movieCorrectTextView);
		    correctTV.setText("Correct choice: " + mSet.getCorrectChoice());
	        TextView titleTV = (TextView)popupView.findViewById(R.id.incorrectChoiceTextView);
	        titleTV.setText("Your choice: " + title);
	        TextView explanationTV = (TextView)popupView.findViewById(R.id.movieExplanationTextView);
	        explanationTV.setText(mSet.getExplanation());
	        TextView movieDetails = (TextView)popupView.findViewById(R.id.movieDetailsTextView);
	        movieDetails.setText("Movie summary goes here for " + mSet.getCorrectChoice());
 
	        Button closeButton=(Button)popupView.findViewById(R.id.movieDialogCloseButton);
	        closeButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "closeButton was clicked");
					popupWindow.dismiss();
					choiceNotCorrect();
				}

			});
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_question,
					container, false);
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			
			//initialize TextViews
			questionTV = (TextView) getView().findViewById(R.id.questionTextView);
			roundTV = (TextView) getView().findViewById(R.id.roundTextView);
			pointsTV = (TextView) getView().findViewById(R.id.pointsTextView);
			scoreTV = (TextView) getView().findViewById(R.id.scoreTextView);
			
			//initialize choice buttons
			choice1 = (Button)getView().findViewById(R.id.choiceOne);
			choice2 = (Button)getView().findViewById(R.id.choiceTwo);
			choice3 = (Button)getView().findViewById(R.id.choiceThree);
			choice4 = (Button)getView().findViewById(R.id.choiceFour);
			askFriendButton = (Button)getView().findViewById(R.id.askFriendButton);
			skipButton = (Button)getView().findViewById(R.id.skipButton);
			
			choice1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					checkAnswer(v);
				}
				
			});
			choice2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					checkAnswer(v);
				}
				
			});
			choice3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					checkAnswer(v);
				}
				
			});
			choice4.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					checkAnswer(v);
				}
				
			});
			askFriendButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					askFriend(v);
				}
				
			});
			skipButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					skipSet(v);
				}
				
			});

			timerTextView = (TextView) this.getActivity().findViewById(R.id.timeLeftTextView);
            timerTextView.setText(String.format("%d", TIME_PER_ROUND));

            correctTextView = (TextView) this.getActivity().findViewById(R.id.correctTextView);
            correctTextView.setText("Ready!");
	        mTimerButton = (ImageButton) this.getActivity().findViewById(R.id.timerImageButton);
//	        mTimerButton.setText("start");
	        mTimerButton.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View v) {
	            	ImageButton b = (ImageButton) v;
	                if (mTimerStarted) {
	                    resetTimer();
	                } else {
	                	startTimer();
	                }
	            }

	        });
	        
			beginGame();
			setUpNewQuestion();
		}

		private void startTimer() {
			startTime = TIME_PER_ROUND;
			timerHandler.postDelayed(timerRunnable, 0);
//                b.setText("stop");
			mTimerStarted = true;
		}

		private void resetTimer() {
			timerHandler.removeCallbacks(timerRunnable);
			mTimerStarted = false;
//                b.setText("start");
		}
		
		private void beginGame() {
			askFriendButton.setVisibility(View.VISIBLE);
			askFriendButton.setEnabled(false);
			skipButton.setText("Skip");
		}
		
		private void endGame() {
			gameOver = true;
//			askFriendButton.setVisibility(View.GONE);
			askFriendButton.setEnabled(true);
			skipButton.setText("Home");

			skipButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getActivity().finish();
				}
				
			});
			askFriendButton.setText("Challenge Friend");

			askFriendButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mSharedPrefsEditor.putFloat("final_score", mScore);
					mSharedPrefsEditor.commit();
					launchFriends(v);
				}
				
			});
			SpringDataREST.get(mContext).saveFinalScore(mScore);
		}
		
		private int addToCorrectTally() {
			return ++mCorrectCount;
		}
		
		private int getCorrectTally() {
			return mCorrectCount;
		}

		//TODO Will need to make a server call to db
		public boolean retrieveMovieSet() {
			boolean gameActive = false;
			if(fromSQLite && mSetsInGame.size() > 0) {
				mSet = mSetsInGame.remove(0);
				dataSource.updatePlayed(mSet, true);
				dataSource.fetchUnplayedSets();
				gameActive = true;
			}
			else {
				Toast.makeText(this.getActivity(), "GREAT JOB ON THIS ROUND!", Toast.LENGTH_LONG).show();
				this.endGame();
				//IF we want to use the old Inventory
				// TODO phase out MovieSetInventory
				//mSet = MovieSetInventory.get(getActivity()).getRandomSet();
			}
			this.pointsTV.setText("Points: " + mSet.getPoints());
			mRound++;
			this.roundTV.setText("Round: " + mRound);
			
			return gameActive;
		}
		
		// Sets Choices (titles) on buttons 
		public void setUpNewQuestion() {
			if(retrieveMovieSet()) {

				sleepFor(500);
				correctTextView.setText("");
				correctTextView.setVisibility(View.GONE);
				this.questionTV.setText(mSet.getQuestion());
				choice1.setText(mSet.getChoice1());
				choice2.setText(mSet.getChoice2());
				choice3.setText(mSet.getChoice3());
				choice4.setText(mSet.getChoice4());

				choice1.startAnimation(mAlphaAnim);
				choice2.startAnimation(mAlphaAnim);
				choice3.startAnimation(mAlphaAnim);
				choice4.startAnimation(mAlphaAnim);
				
				this.scoreTV.setText("Score: " + mScore); 
				startTimer();
			}
		}
		
		//TODO might have to move this method in the Fragment class
		// Will need to unplug the onClick events from the fragment_question.xml layout file
		// and add a click listener to all the buttons in the fragment class
		public void checkAnswer(View v) {
			resetTimer();
			if(mLives < 1 || gameOver)
				return;
			//((Button) v).getText()
			Button b = (Button) v;
			if(b != null && ((String) b.getText()).equalsIgnoreCase(mSet.getCorrectChoice())) {
				Log.d(TAG, "Selected: " + b.getText() + ", correct is: " + mSet.getCorrectChoice());
//	            correctTextView.setText("Correct!");
//				correctTextView.setVisibility(View.VISIBLE);
				Toast.makeText(this.getActivity(), "CORRECT! " + mSet.getExplanation(), Toast.LENGTH_SHORT).show();
				mScore += mSet.getPoints();
				scoreTV.setText("Score:" + mScore);
				MovieSetInventory.get(getActivity()).markAsCorrect(mSet.getId());

//				b.startAnimation(mAlphaAnim);
				addToCorrectTally();
				setUpNewQuestion();
			}
			else {
	            correctTextView.setText("NOT correct");
				popupLoginDialog(b.getText().toString());
				//Toast.makeText(this.getActivity(), "WRONG :( " + mSet.getExplanation(), Toast.LENGTH_LONG).show();
				//choiceNotCorrect();
			}
			//TODO check answer in MovieSet object
		}

		public void sleepFor(int milli) {
			try {
			    Thread.sleep(milli);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
		}
		
		public void skipSet(View v) {
			int correct = (int)getCorrectTally()/3;
			Log.i("skipSet", "getCorrectTally(): " + getCorrectTally() + ", correct: " + correct);
			if( correct >= 1 && correct > mSkipped) {
				Toast.makeText(getActivity(), "Round Skipped", Toast.LENGTH_SHORT).show();
				setUpNewQuestion();
				mSkipped++;
			}
			else {
				Log.i("skipSet", "mSkipped: " + mSkipped);
				Toast.makeText(getActivity(), "Gain a Skip for every 3 correct answers.", Toast.LENGTH_SHORT).show();
			}
		}
		
		public void choiceNotCorrect() {
			if(--mLives <= 0) {
				endGame();
				Toast.makeText(getActivity(), "Game Over!", Toast.LENGTH_SHORT).show();
			}
			else {
				setUpNewQuestion();
			}
		}
		
		public void askFriend(View v) {
			Toast.makeText(getActivity(), ((Button) v).getText(), Toast.LENGTH_SHORT).show();
			
		}

		public void launchFriends(View v) {
			startActivity(new Intent(mContext, FriendsActivity.class));
		}
	}

	
}
