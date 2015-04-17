package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieSetDBOpenHelper extends SQLiteOpenHelper {

	private static final String TAG = "MovieSetDBOpenHelper";
	private static final String DATABASE_NAME = "mutibo.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_MOVIE_SETS = "movie_set";
	public static final String MOVIE_SET_ID = "id";
	public static final String MOVIE_SET_ACTIVE = "active";
	public static final String MOVIE_SET_CHOICE1 = "choice1";
	public static final String MOVIE_SET_CHOICE2 = "choice2";
	public static final String MOVIE_SET_CHOICE3 = "choice3";
	public static final String MOVIE_SET_CHOICE4 = "choice4";
	public static final String MOVIE_SET_CORRECT_CHOICE = "correct_choice";
	public static final String MOVIE_SET_DIFFICULTY = "difficutly";
	public static final String MOVIE_SET_GENRE = "genre";
	public static final String MOVIE_SET_POINTS = "points";
	public static final String MOVIE_SET_QUESTION = "question";
	public static final String MOVIE_SET_RATED = "rated";
	public static final String MOVIE_SET_RATING = "rating";
	public static final String MOVIE_SET_TIMER_DURATION = "timer_duration";
	public static final String MOVIE_SET_URL = "url";
	public static final String MOVIE_SET_EXPLANATION = "explanation";
	
	// This column is for client only; set to true after MovieSet has been played/answered
	public static final String MOVIE_SET_PLAYED_ALREADY = "played"; 
	
	public static final String TABLE_CREATE = 
			"CREATE TABLE " + TABLE_MOVIE_SETS + " ("+ MOVIE_SET_ID + " TEXT PRIMARY KEY, " + MOVIE_SET_ACTIVE + " INTEGER, " +
					MOVIE_SET_POINTS + " INTEGER," +  MOVIE_SET_CHOICE1 + " TEXT, " + MOVIE_SET_CHOICE2 + " TEXT, " +
					MOVIE_SET_CHOICE3 + " TEXT, " +MOVIE_SET_CHOICE4 + " TEXT, " +MOVIE_SET_CORRECT_CHOICE + " TEXT, " +
					MOVIE_SET_DIFFICULTY + " TEXT, " +MOVIE_SET_GENRE + " TEXT, " +MOVIE_SET_QUESTION + " TEXT, " +
					MOVIE_SET_RATED + " INTEGER," +MOVIE_SET_RATING + " TEXT, "+ MOVIE_SET_TIMER_DURATION + " TEXT, "+
					MOVIE_SET_URL + " TEXT, "+ MOVIE_SET_EXPLANATION + " TEXT, " + MOVIE_SET_PLAYED_ALREADY + " INTEGER)" ;

	public MovieSetDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.i(TAG, "Instantiated MovieSetDBOpenHelper");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
		Log.i(TAG, "MovieSetDBOpenHelper: Created database");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIE_SETS);
		Log.i(TAG, "MovieSetDBOpenHelper: Dropped database");
		onCreate(db);
	}

}
