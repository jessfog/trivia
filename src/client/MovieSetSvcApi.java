package client;

import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * This interface defines an API for a MovieSetSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * 
 * 
 * @author jessfog
 *
 */
public interface MovieSetSvcApi {

	public static final String PASSWORD_PARAMETER = "password";

	public static final String USERNAME_PARAMETER = "username";
	
	public static final String DURATION_PARAMETER = "duration";
	
	public static final String TOKEN_PATH = "/oauth/token";
	
	public static final String QUESTION_PARAMETER = "question";
	
	public static final String CHOICE_1_PARAMETER = "choice1";
	public static final String CHOICE_2_PARAMETER = "choice2";
	public static final String CHOICE_3_PARAMETER = "choice3";
	public static final String CHOICE_4_PARAMETER = "choice4";
	public static final String CORRECT_CHOICE_PARAMETER = "correctChoice";
	public static final String GENRE_PARAMETER = "genre";
	public static final String DIFFICULTY_PARAMETER = "difficulty";
	public static final String POINTS_PARAMETER = "points";

	// The path where we expect the MovieSetSvc to live
	public static final String MOVIESET_SVC_PATH = "/movieset";

	// The path to search movieSet by genre
	public static final String GENRE_PARAMETER_SEARCH_PATH = MOVIESET_SVC_PATH + "/search/findByGenre";

	// The path to search videos by title
	public static final String DIFFICULTY_PARAMETER_SEARCH_PATH = MOVIESET_SVC_PATH + "/search/findByDifficultyLessThan";
	

	@GET(MOVIESET_SVC_PATH)
	public Collection<MovieSet> getMovieSetList();
	
	@POST(MOVIESET_SVC_PATH)
	public Void addMovieSet(@Body MovieSet v);
	
	@PUT(MOVIESET_SVC_PATH)
	public Void save(@Body MovieSet v);
	
	@GET(GENRE_PARAMETER_SEARCH_PATH)
	public Collection<MovieSet> findByGenre(@Query(GENRE_PARAMETER) String email);
	
	// The path to search videos by difficulty
	@GET(DIFFICULTY_PARAMETER_SEARCH_PATH)
	public Collection<MovieSet> findByDifficultyLessThan(@Query(DIFFICULTY_PARAMETER) int difficulty);
	
}


//package client;
//
//import java.util.Collection;
//
//import retrofit.http.Body;
//import retrofit.http.GET;
//import retrofit.http.POST;
//import retrofit.http.Query;
//
///**
// * This interface defines an API for a VideoSvc. The
// * interface is used to provide a contract for client/server
// * interactions. The interface is annotated with Retrofit
// * annotations so that clients can automatically convert the
// * 
// * 
// * @author jules
// *
// */
//public interface MovieSetSvcApi {
//	
//	public static final String PASSWORD_PARAMETER = "password";
//
//	public static final String USERNAME_PARAMETER = "username";
//
//	public static final String TITLE_PARAMETER = "title";
//	
//	public static final String DURATION_PARAMETER = "duration";
//	
//	public static final String TOKEN_PATH = "/oauth/token";
//	
//	// The path where we expect the VideoSvc to live
//	public static final String VIDEO_SVC_PATH = "/video";
//
//	// The path to search videos by title
//	public static final String VIDEO_TITLE_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByName";
//	
//	// The path to search videos by title
//	public static final String VIDEO_DURATION_SEARCH_PATH = VIDEO_SVC_PATH + "/search/findByDurationLessThan";
//	
//	@GET(VIDEO_SVC_PATH)
//	public Collection<MovieSet> getMovieSetList();
//	
//	@POST(VIDEO_SVC_PATH)
//	public Void addVideo(@Body MovieSet v);
//	
//	@GET(VIDEO_TITLE_SEARCH_PATH)
//	public Collection<MovieSet> findByTitle(@Query(TITLE_PARAMETER) String title);
//	
//	@GET(VIDEO_DURATION_SEARCH_PATH)
//	public Collection<MovieSet> findByDurationLessThan(@Query(DURATION_PARAMETER) String title);
//	
//}
