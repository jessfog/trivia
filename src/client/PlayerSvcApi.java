package client;

import java.util.Collection;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * This interface defines an API for a PlayerSvc. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 * 
 * 
 * @author jessfog
 *
 */
public interface PlayerSvcApi {

	public static final String PASSWORD_PARAMETER = "password";

	public static final String USERNAME_PARAMETER = "username";
	
	public static final String DURATION_PARAMETER = "duration";
	
	public static final String TOKEN_PATH = "/oauth/token";
	
	public static final String FIRST_NAME_PARAMETER = "first";
	
	public static final String LAST_NAME_PARAMETER = "last";
	public static final String EMAIL_PARAMETER = "email";

	// The path where we expect the PlayerSvc to live
	public static final String PLAYER_SVC_PATH = "/player";

	// The path to search players by email
	public static final String EMAIL_PARAMETER_SEARCH_PATH = PLAYER_SVC_PATH + "/search/findByEmail";
	

	@GET(PLAYER_SVC_PATH)
	public Collection<Player> getPlayerList();
	
	@POST(PLAYER_SVC_PATH)
	public Void addPlayer(@Body Player v);
	
	@GET(EMAIL_PARAMETER_SEARCH_PATH)
	public Collection<Player> findByEmail(@Query(EMAIL_PARAMETER) String email);

	
}
