package db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import client.MovieSet;
import client.MovieSetInventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieSetsDataSource {
	private final String TAG = "MovieSetDataSource";
	SQLiteOpenHelper dbHelper;
	SQLiteDatabase db;
	
	private static MovieSetsDataSource sMovieSetDataSource;
    private Context mAppContext;


    public static MovieSetsDataSource get(Context c) {
        if (sMovieSetDataSource == null) {
        	sMovieSetDataSource = new MovieSetsDataSource(c.getApplicationContext());
        }
        //sMovieSetDataSource.open();
        
        return sMovieSetDataSource;
    }
    
	private final static String[] allColumns = {
		MovieSetDBOpenHelper.MOVIE_SET_ID, MovieSetDBOpenHelper.MOVIE_SET_ACTIVE, MovieSetDBOpenHelper.MOVIE_SET_CHOICE1,
		MovieSetDBOpenHelper.MOVIE_SET_CHOICE2, MovieSetDBOpenHelper.MOVIE_SET_CHOICE3, MovieSetDBOpenHelper.MOVIE_SET_CHOICE4,
		MovieSetDBOpenHelper.MOVIE_SET_CORRECT_CHOICE, MovieSetDBOpenHelper.MOVIE_SET_DIFFICULTY, MovieSetDBOpenHelper.MOVIE_SET_GENRE,
		MovieSetDBOpenHelper.MOVIE_SET_POINTS, MovieSetDBOpenHelper.MOVIE_SET_QUESTION, MovieSetDBOpenHelper.MOVIE_SET_RATED,
		MovieSetDBOpenHelper.MOVIE_SET_RATING, MovieSetDBOpenHelper.MOVIE_SET_TIMER_DURATION, MovieSetDBOpenHelper.MOVIE_SET_URL,
		MovieSetDBOpenHelper.MOVIE_SET_EXPLANATION, MovieSetDBOpenHelper.MOVIE_SET_PLAYED_ALREADY
	};
	
	public MovieSetsDataSource(Context context) {
		Log.i(TAG, "Instantiated MovieSetDataSource");
        mAppContext = context;
		dbHelper = new MovieSetDBOpenHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	public void open() {
		db = dbHelper.getWritableDatabase();
		Log.i(TAG, "Opened database");
	}

	public void close() {
		dbHelper.close();
		Log.i(TAG, "Closed database");
	}
	
	public void create(MovieSet set) {
		ContentValues values = new ContentValues();
		values.put(MovieSetDBOpenHelper.MOVIE_SET_ID, set.getId());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_ACTIVE, set.isActive()?1:0);
		values.put(MovieSetDBOpenHelper.MOVIE_SET_CHOICE1, set.getChoice1());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_CHOICE2, set.getChoice2());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_CHOICE3, set.getChoice3());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_CHOICE4, set.getChoice4());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_CORRECT_CHOICE, set.getCorrectChoice());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_DIFFICULTY, set.getDifficulty());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_GENRE, set.getGenre());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_POINTS, set.getPoints());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_QUESTION, set.getQuestion());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_RATED, set.isRated()?1:0);
		values.put(MovieSetDBOpenHelper.MOVIE_SET_RATING, set.getRating());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_TIMER_DURATION, set.getTimerDuration());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_URL, set.getUrl());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_EXPLANATION, set.getExplanation());
		values.put(MovieSetDBOpenHelper.MOVIE_SET_PLAYED_ALREADY, 0);
		
		long id = db.insert(MovieSetDBOpenHelper.TABLE_MOVIE_SETS, null, values);
		if(id > 0) {
			Log.i(TAG ,"Inserted record in Database: set " + set.getCorrectChoice() + " with ID = " +set.getId());
            //set.setDbRowId(id); OLD.. we already have a ID created by Java Spring, let's keep it.
		}
		else
			Log.i(TAG ,"Failed to insert record in Database: set ID " + set.getCorrectChoice()+ ", " + set.getId());
	}
	
	public long updateRating(MovieSet set, int rating) {
		String movieSetId = String.valueOf(set.getId());
		ContentValues values = new ContentValues();
		values.put(MovieSetDBOpenHelper.MOVIE_SET_ID, movieSetId);
		values.put(MovieSetDBOpenHelper.MOVIE_SET_RATING, rating);
		values.put(MovieSetDBOpenHelper.MOVIE_SET_RATED, 1);
		String[] where = {movieSetId};
		long id = db.update(MovieSetDBOpenHelper.TABLE_MOVIE_SETS, values, MovieSetDBOpenHelper.MOVIE_SET_ID+"=?", where);
		if(id > 0) {
			set.setRating(rating);
			//Log.i(TAG ,"Updated record in Database: with localUrl for movieSetId: " + movieSetId + " in: " + localUrl);
		}
		else {
			Log.w(TAG ,"Failed to update record's rating in Database for movieSetId: " + movieSetId);
			//TODO Check to see if MovieSet ID exists and update otherwise INSERT new set row
			this.create(set);
			id = db.update(MovieSetDBOpenHelper.TABLE_MOVIE_SETS, values, MovieSetDBOpenHelper.MOVIE_SET_ID+"=?", where);
		}
		return set.getId(); // id; we could return this id if we weren't creating one with Spring Data REST
	}
	
	public long updatePlayed(MovieSet set, boolean played) {
		String movieSetId = String.valueOf(set.getId());
		ContentValues values = new ContentValues();
		values.put(MovieSetDBOpenHelper.MOVIE_SET_ID, movieSetId);
		values.put(MovieSetDBOpenHelper.MOVIE_SET_PLAYED_ALREADY, played);
		String[] where = {movieSetId};
		Log.i(TAG ,"MovieSetDataSource : updatePlayed() where played: " + played);
		long id = db.update(MovieSetDBOpenHelper.TABLE_MOVIE_SETS, values, MovieSetDBOpenHelper.MOVIE_SET_ID+"=?", where);
		if(id > 0) {
			set.setPlayed(played);
		}
		else {
			Log.w(TAG ,"Failed to update record's rating in Database for movieSetId: " + movieSetId);
		}
		return set.getId(); // id; we could return this id if we weren't creating one with Spring Data REST
	}
	
	public List<MovieSet> findAll() {	
		Log.i(TAG ,"Database querying for " + allColumns.toString());
		Cursor cursor = db.query(MovieSetDBOpenHelper.TABLE_MOVIE_SETS, allColumns, null, null, null, null, null);
		Log.i(TAG ,"Database returned " + cursor.getCount() + " set rows");
		List<MovieSet> sets = cursorToList(cursor);
		
		return sets;
	}
	
	public List<MovieSet> fetchUnplayedSets() {
		Log.i(TAG ,"MovieSetDataSource : fetchUnplayedSets() where hasNotBeenPlayed");
		String selection = MovieSetDBOpenHelper.MOVIE_SET_PLAYED_ALREADY + "=" + 0 ; //+ " AND "+ MovieSetDBOpenHelper.EVENT_START_DATE + ">" + timeNow;
		String orderBy = MovieSetDBOpenHelper.MOVIE_SET_DIFFICULTY;
		Cursor cursor = db.query(MovieSetDBOpenHelper.TABLE_MOVIE_SETS, allColumns, selection, null, null, null, orderBy);
		Log.i(TAG ,"Database returned " + cursor.getCount() + " set rows filtered by: " + selection);
		List<MovieSet> sets = cursorToList(cursor);
		
		return sets;
	}
	
	public List<MovieSet> fetchSetsByDifficulty(int difficulty) {
		Log.i(TAG ,"MovieSetDataSource : fetchSetsByDifficulty() where difficulty: " + difficulty);
		String selection = MovieSetDBOpenHelper.MOVIE_SET_DIFFICULTY + "=" + difficulty; // + " AND "+ MovieSetDBOpenHelper.EVENT_START_DATE + ">" + timeNow;
		String orderBy = MovieSetDBOpenHelper.MOVIE_SET_DIFFICULTY;
		Cursor cursor = db.query(MovieSetDBOpenHelper.TABLE_MOVIE_SETS, allColumns, selection, null, null, null, orderBy);
		Log.i(TAG ,"Database returned " + cursor.getCount() + " set rows filtered by difficulty: " + selection);
		List<MovieSet> sets = cursorToList(cursor);
		
		return sets;
	}
//	private final static String[] allColumns = {
//		MovieSetDBOpenHelper.MOVIE_SET_ID, MovieSetDBOpenHelper.MOVIE_SET_ACTIVE, MovieSetDBOpenHelper.MOVIE_SET_CHOICE1,
//		MovieSetDBOpenHelper.MOVIE_SET_CHOICE2, MovieSetDBOpenHelper.MOVIE_SET_CHOICE3, MovieSetDBOpenHelper.MOVIE_SET_CHOICE4,
//		MovieSetDBOpenHelper.MOVIE_SET_CORRECT_CHOICE, MovieSetDBOpenHelper.MOVIE_SET_DIFFICULTY, MovieSetDBOpenHelper.MOVIE_SET_GENRE,
//		MovieSetDBOpenHelper.MOVIE_SET_POINTS, MovieSetDBOpenHelper.MOVIE_SET_QUESTION, MovieSetDBOpenHelper.MOVIE_SET_RATED,
//		MovieSetDBOpenHelper.MOVIE_SET_RATING, MovieSetDBOpenHelper.MOVIE_SET_TIMER_DURATION, MovieSetDBOpenHelper.MOVIE_SET_URL,
//		MovieSetDBOpenHelper.MOVIE_SET_EXPLANATION, MovieSetDBOpenHelper.MOVIE_SET_PLAYED_ALREADY
//	};
//	
	/**
	 * @param cursor
	 * @return
	 */
	private List<MovieSet> cursorToList(Cursor cursor) {
		List<MovieSet> sets = new ArrayList<MovieSet>();
		if(cursor.getCount() > 0) {
			while(cursor.moveToNext()) {
				MovieSet e = new MovieSet(cursor.getLong(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_ID)));
				e.setActive(cursor.getInt(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_ACTIVE))>0?true:false);
				e.setChoice1(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_CHOICE1)));
				e.setChoice2(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_CHOICE2)));
				e.setChoice3(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_CHOICE3)));
				e.setChoice4(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_CHOICE4)));
				e.setCorrectChoice(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_CORRECT_CHOICE)));
				e.setDifficulty(cursor.getInt(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_DIFFICULTY)));
				e.setGenre(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_GENRE)));
				e.setPoints(cursor.getInt(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_POINTS)));
				e.setQuestion(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_QUESTION)));
				e.setActive(cursor.getInt(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_RATED))>0?true:false);
				e.setRating(cursor.getInt(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_RATING)));
				e.setTimerDuration(cursor.getInt(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_TIMER_DURATION)));
				e.setUrl(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_URL)));
				e.setExplanation(cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_EXPLANATION)));
				e.setPlayed(cursor.getInt(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_PLAYED_ALREADY))>0?true:false);
				sets.add(e);
				Log.i(TAG ,"Database returned MOVIE_SET " + cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_CORRECT_CHOICE)));
				Log.i(TAG ,"ID: " + cursor.getString(cursor.getColumnIndex(MovieSetDBOpenHelper.MOVIE_SET_ID)));
				
			}
			MovieSetInventory.get(mAppContext).addMovieSets(sets); //TODO keep or remove?
		}
		return sets;
	}
	
	public void dropTable() {
		db.execSQL("DROP TABLE IF EXISTS " + MovieSetDBOpenHelper.TABLE_MOVIE_SETS);
	}
	
	public void createTable() {
		db.execSQL(MovieSetDBOpenHelper.TABLE_CREATE);
	}
}
