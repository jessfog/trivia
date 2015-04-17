package db;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.magnum.videoup.client.CallableTask;
import org.magnum.videoup.client.TaskCallback;

import client.Challenge;
import client.ChallengeSvc;
import client.ChallengeSvcApi;
import client.FriendsActivity;
import client.MovieSet;
import client.MovieSetSvcApi;
import client.Player;
import client.PlayerSvc;
import client.PlayerSvcApi;
import client.QuestionActivity;
import client.Score;
import client.ScoreSvc;
import client.ScoreSvcApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class SpringDataREST {

	private static SharedPreferences mSharedPrefs;
	private SharedPreferences.Editor mSharedPrefsEditor;
	private static String TAG = "SpringDataREST";
	private static Context mContext;
	private static String mUser;
	private static String mPass;
	private static String mServer;
	private static Collection<Score> mScores;
	private static Collection<Player> mPlayers;
	private Collection<MovieSet> mSets;
	private Collection<Challenge> mChallenges;
	private MovieSetSvcApi movieSvc;
	private static long mPlayerId;
	private static SpringDataREST sSpringDataREST;
	private Map<String, Boolean> mPlayedSets;
	private String mFirst;
	private String mLast;
	private float mFinalScore;
	private static ScoreSvcApi scoreSvc;
	private static PlayerSvcApi playerSvc;
	private static ChallengeSvcApi challengeSvc;
	
	public SpringDataREST(Context c) {
		mContext = c;
		mPlayedSets = new TreeMap<String, Boolean>();
		mSharedPrefs = mContext.getSharedPreferences("mutibo", 0); // 0 = MODE_PRIVATE
		mSharedPrefsEditor = mSharedPrefs.edit();

		mUser = mSharedPrefs.getString("user", null);
		mPass = mSharedPrefs.getString("pass", null);
		mServer = mSharedPrefs.getString("server", null);
		mPlayerId = mSharedPrefs.getLong("player_id", -1);
		mFirst = mSharedPrefs.getString("first", null);
		mLast = mSharedPrefs.getString("last", null);
		mFinalScore = mSharedPrefs.getFloat("final_score", 10);
		playerSvc = PlayerSvc.init(mServer, mUser, mPass);
		challengeSvc = ChallengeSvc.init(mServer, mUser, mPass);
		scoreSvc = ScoreSvc.init(mServer, mUser, mPass);
	}

	public static SpringDataREST get(Context c) {
		if(sSpringDataREST == null) {
			sSpringDataREST = new SpringDataREST(c);
		}
		return sSpringDataREST;
	}
	
	public boolean sendChallenge(long playerToChallengeId) {
		Log.i(TAG, "Will attempt to initialize ChallengeSvcApi by: " + mUser);
		final Challenge myChallenge = new Challenge(mPlayerId, playerToChallengeId);
		myChallenge.setChallengerFirst(mFirst);
		myChallenge.setChallengerLast(mLast);
		myChallenge.setScore(mFinalScore);
		Log.i(TAG, "Will attempt to send Challenge to: " + playerToChallengeId );
		CallableTask.invoke(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				Log.d(TAG, "Will attempt to send Challenge to user: " + myChallenge.getChallengedUserId());
				challengeSvc.addPlayerChallenge(myChallenge);
				return null;
			}
		}, new TaskCallback<Void>() {

			@Override
			public void success(Void result) {
				Log.d(TAG, "Challenge was added!");
			}

			@Override
			public void error(Exception e) {
				Log.e(TAG, "Error logging in via OAuth.", e);
				
				Toast.makeText(
						mContext,
						"Login failed, check your Internet connection and credentials.",
						Toast.LENGTH_SHORT).show();
			}
		});
		Log.i(TAG, "Challenge saved! ");
		return false;
	}
	
	public boolean saveFinalScore(float score) {
		if(score < 3)
			return true;

		Log.i(TAG, "Will attempt to initialize ScoreSvcApi by: " + mUser);
		final Score myScore = new Score(mPlayerId, mFirst, mLast, score);
		Log.i(TAG, "Will attempt to Store Score of: " + score + " points, by " + myScore.getFirst());
		CallableTask.invoke(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				Log.d(TAG, "Will attempt to Store Score of: 18 points, by " + myScore.getFirst());
				scoreSvc.add(myScore);
				return null;
			}
		}, new TaskCallback<Void>() {

			@Override
			public void success(Void result) {
				
				Log.d(TAG, "Score was added!");
				
				
			}

			@Override
			public void error(Exception e) {
				Log.e(TAG, "Error logging in via OAuth.", e);
				
				Toast.makeText(
						mContext,
						"Login failed, check your Internet connection and credentials.",
						Toast.LENGTH_SHORT).show();
			}
		});
		Log.i(TAG, "Score saved! ");
		return false;
	}
	
	public Collection<Player> retrieveFriendsList() {
		
		//TODO figure out how to return mPlayers once the success method is triggered
		// currently returning null since success has not been triggered when reaching the return statement
		CallableTask.invoke(new Callable<Collection<Player>>() {

			@Override
			public Collection<Player> call() throws Exception {
				return playerSvc.getPlayerList();
			}
		}, new TaskCallback<Collection<Player>>() {

			@Override
			public void success(Collection<Player> result) {
				mPlayers = result;
				int added = 0;
				if(result.size() > 0) {
					//Log.d("MovieSetInventory", "retrieved set: " + ((MovieSet)result.toArray()[1]).getId());
					for(Player p:result) {
						Log.d(TAG, "retrieved player First: " + p.getFirst() + ", Last: " + p.getLast());
					}
				}
				Log.d(TAG, result.size() + " players were found.");
			}

			@Override
			public void error(Exception e) {
				Log.e(TAG, "Error logging in via OAuth.", e);
				
				Toast.makeText(
						mContext,
						"Login failed, check your Internet connection and credentials.",
						Toast.LENGTH_SHORT).show();
			}
		});
		
		return mPlayers;
	}
}
