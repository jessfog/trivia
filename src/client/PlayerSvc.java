package client;

import org.magnum.videoup.client.LoginScreenActivity;
import org.magnum.videoup.client.oauth.SecuredRestBuilder;
import org.magnum.videoup.client.unsafe.EasyHttpClient;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import android.content.Context;
import android.content.Intent;

public class PlayerSvc {

	public static final String CLIENT_ID = "mobile";

	private static PlayerSvcApi playerSvc_;

	public static synchronized PlayerSvcApi getOrShowLogin(Context ctx) {
		if (playerSvc_ != null) {
			return playerSvc_;
		} else {
			Intent i = new Intent(ctx, LoginScreenActivity.class);
			ctx.startActivity(i);
			return null;
		}
	}

	public static synchronized PlayerSvcApi init(String server, String user,
			String pass) {

		playerSvc_ = new SecuredRestBuilder()
				.setLoginEndpoint(server + PlayerSvcApi.TOKEN_PATH)
				.setUsername(user)
				.setPassword(pass)
				.setClientId(CLIENT_ID)
				.setClient(
						new ApacheClient(new EasyHttpClient()))
				.setEndpoint(server).setLogLevel(LogLevel.FULL).build()
				.create(PlayerSvcApi.class);

		return playerSvc_;
	}
}
