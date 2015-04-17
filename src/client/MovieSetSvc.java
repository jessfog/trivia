/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package client;

import org.magnum.videoup.client.LoginScreenActivity;
import org.magnum.videoup.client.oauth.SecuredRestBuilder;
import org.magnum.videoup.client.unsafe.EasyHttpClient;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.content.Context;
import android.content.Intent;

public class MovieSetSvc {

	public static final String CLIENT_ID = "mobile";

	private static MovieSetSvcApi movieSetSvc_;

	public static synchronized MovieSetSvcApi getOrShowLogin(Context ctx) {
		if (movieSetSvc_ != null) {
			return movieSetSvc_;
		} else {
			Intent i = new Intent(ctx, LoginScreenActivity.class);
			ctx.startActivity(i);
			return null;
		}
	}

	public static synchronized MovieSetSvcApi init(String server, String user,
			String pass) {

		movieSetSvc_ = new SecuredRestBuilder()
				.setLoginEndpoint(server + MovieSetSvcApi.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
				.setClient(
						new ApacheClient(new EasyHttpClient()))
				.setEndpoint(server).setLogLevel(LogLevel.FULL).build()
				.create(MovieSetSvcApi.class);

		return movieSetSvc_;
	}
}
