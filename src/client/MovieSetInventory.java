package client;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class MovieSetInventory {
	
	private static MovieSetInventory sInventory;  
    private Context mAppContext;
	private Map<String,MovieSet> mSets;
	private Map<String, Boolean> mPlayedSets;
	
	public MovieSetInventory(Context c) {
		mAppContext = c;
		mSets = new TreeMap<String, MovieSet>();
		mPlayedSets = new TreeMap<String, Boolean>();
	}

	public static MovieSetInventory get(Context c) {
		if(sInventory == null) {
			sInventory = new MovieSetInventory(c);
		}
		return sInventory;
	}
	
	public int addMovieSets(Collection<MovieSet> col) {
		for(MovieSet set:col) {
			Log.d("MovieSetInventory", "retrieved set: " + set.getChoice1());
			mSets.put(String.valueOf(set.getId()), set);
		}
		return mSets.size();
	}
	
	public MovieSet getMovieSet(long id) {
		Log.d("MovieSetInventory", mSets.size() + " sets in inventory");
		MovieSet setInPlay = mSets.remove(String.valueOf(id));
		Log.d("MovieSetInventory", mSets.size() + " sets remain in inventory");
		mPlayedSets.put(String.valueOf(id), false);
		return setInPlay;
	}
	
	public MovieSet getRandomSet() {
		Log.d("MovieSetInventory", mSets.size() + " sets remain in inventory");
		String id = (String) mSets.keySet().toArray()[getRandomInteger()];
		Log.d("MovieSetInventory", "Random id: " + id);
		return mSets.remove(id);
	}
	
	public void markAsCorrect(long id) {
		mPlayedSets.put(String.valueOf(id), true);
	}

	private int getRandomInteger() {
		// get the range, casting to long to avoid overflow problems
		long range = (long) mSets.size(); // - initial value if not zero
		Log.d("MovieSetInventory", "Range: " + range);
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * new Random().nextDouble());
		int random = (int) (fraction); // + 1 if NOT zero based
		Log.d("MovieSetInventory", "Random: " + random);
		return random;
	}
}
