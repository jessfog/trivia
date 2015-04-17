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
public interface ChallengeSvcApi {

	public static final String PASSWORD_PARAMETER = "password";
	public static final String USERNAME_PARAMETER = "username";
	public static final String TOKEN_PATH = "/oauth/token";
	public static final String CHALLENGER_ID_PARAMETER = "challengerId";
	public static final String CHALLENGED_USER_ID_PARAMETER = "challenged_user_id";
	public static final String CHALLENGE_COMPLETED = "completed";

	// The path where we expect the PlayerSvc to live
	public static final String PLAYER_CHALLENGE_SVC_PATH = "/playerchallenge";

	// The path to search players by email
	public static final String PLAYER_ID_PARAMETER_SEARCH_PATH = PLAYER_CHALLENGE_SVC_PATH + "/search/findByChallengedUserId";
	

	@GET(PLAYER_CHALLENGE_SVC_PATH)
	public Collection<Challenge> getPlayerChallengeList();
	
	@POST(PLAYER_CHALLENGE_SVC_PATH)
	public Void addPlayerChallenge(@Body Challenge pC);
	
	@GET(PLAYER_ID_PARAMETER_SEARCH_PATH)
	public Collection<Challenge> findByChallengedUserId(@Query(CHALLENGED_USER_ID_PARAMETER) long playerId);
		
}
