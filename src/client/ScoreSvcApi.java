package client;

import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * This interface defines an API for a Svc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * 
 * 
 * @author jessfog
 *
 */
public interface ScoreSvcApi {
	
	public static final String FIRST_NAME_PARAMETER = "first";
	
	public static final String LAST_NAME_PARAMETER = "last";

	public static final String TOKEN_PATH = "/oauth/token";
	// The path where we expect the Svc to live
	public static final String SCORE_SVC_PATH = "/score";

	// The path to search scores by email
	public static final String NAME_PARAMETER_SEARCH_PATH = SCORE_SVC_PATH + "/search/findByFirst";
	

	@GET(SCORE_SVC_PATH)
	public Collection<Score> getList();
	
	@POST(SCORE_SVC_PATH)
	public Void add(@Body Score v);
	
	@GET(NAME_PARAMETER_SEARCH_PATH)
	public Collection<Score> findByFirst(@Query(FIRST_NAME_PARAMETER) String name);
	
}
