/* 
 **
 ** Copyright 2014, jessfog
 **
 ** 
 */
package client;

import org.magnum.videoup.client.oauth.SecuredRestBuilder;
import org.magnum.videoup.client.unsafe.EasyHttpClient;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.util.Log;

public class ScoreSvc {

	public static final String CLIENT_ID = "mobile";

	private static ScoreSvcApi scoreSvc_;

	public static synchronized ScoreSvcApi init(String server, String user,
			String pass) {

		scoreSvc_ = new SecuredRestBuilder()
				.setLoginEndpoint(server + ScoreSvcApi.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
				.setClient(
						new ApacheClient(new EasyHttpClient()))
				.setEndpoint(server).setLogLevel(LogLevel.FULL).build()
				.create(ScoreSvcApi.class);
		Log.i("ScoreSvc", "Initialized ScoreSvcApi by: " + user);
		
		return scoreSvc_;
	}
}
