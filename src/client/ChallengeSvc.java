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

public class ChallengeSvc {

	public static final String CLIENT_ID = "mobile";

	private static ChallengeSvcApi challengeSvc_;

	public static synchronized ChallengeSvcApi init(String server, String user,
			String pass) {

		challengeSvc_ = new SecuredRestBuilder()
				.setLoginEndpoint(server + ChallengeSvcApi.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
				.setClient(
						new ApacheClient(new EasyHttpClient()))
				.setEndpoint(server).setLogLevel(LogLevel.FULL).build()
				.create(ChallengeSvcApi.class);
		Log.i("ChallengeSvc", "Initialized ChallengeSvcApi by: " + user);
		
		return challengeSvc_;
	}
}
