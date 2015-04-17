package client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.magnum.videoup.client.CallableTask;
import org.magnum.videoup.client.R;
import org.magnum.videoup.client.TaskCallback;

import db.SpringDataREST;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsActivity extends Activity {

	public static final String EXTRA_RES_ID = "POS";
	private final String TAG = "FriendsActivity";
	private GridView mFriendGrid;
	private Collection<Player> mPlayers;
	private ArrayList<Player> mFriendsList;

	private SharedPreferences mSharedPrefs;
	private SharedPreferences.Editor mSharedPrefsEditor;
	private String user;
	private String pass;
	private String server;
	private long player_id;
	private String first;
	private String last;

	public FriendsActivity() {
		// TODO Auto-generated constructor stub
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);

		mSharedPrefs = getApplicationContext().getSharedPreferences("mutibo", MODE_PRIVATE);
		mSharedPrefsEditor = mSharedPrefs.edit();

		user = mSharedPrefs.getString("user", null);
		pass = mSharedPrefs.getString("pass", null);
		server = mSharedPrefs.getString("server", null);
		player_id = mSharedPrefs.getLong("player_id", -1);
		first = mSharedPrefs.getString("first", null);
		last = mSharedPrefs.getString("last", null);
		final PlayerSvcApi playerSvc = PlayerSvc.init(server, user, pass);
		mFriendGrid = (GridView) findViewById(R.id.friendsGrid);
		
		//TODO replace below with 
		//mPlayers = SpringDataREST.get(getApplicationContext()).retrieveFriendsList();
		
		CallableTask.invoke(new Callable<Collection<Player>>() {

			@Override
			public Collection<Player> call() throws Exception {
				return playerSvc.getPlayerList();
			}
		}, new TaskCallback<Collection<Player>>() {

			@Override
			public void success(Collection<Player> result) {
				mPlayers = result;
				int added = 0;
				if(result.size() > 0) {
					//Log.d("MovieSetInventory", "retrieved set: " + ((MovieSet)result.toArray()[1]).getId());
					for(Player p:result) {
						Log.d("Player", "retrieved player First: " + p.getFirst() + ", Last: " + p.getLast());
					}
				}
				Log.d("CallableTask", result.size() + " players were found.");
				if(mPlayers != null && mPlayers.size() > 0) {
					Log.d(TAG, "Friends found in inventory");
					mFriendsList = new ArrayList<Player>();
					mFriendsList.addAll(mPlayers);
					updateFriendsView(mFriendsList);
				}
				else {
					Log.d(TAG, "Friends NOT found in inventory");
					//FBQuery.get(this).getMyFriends(FriendsActivity.this);
				}
			}

			@Override
			public void error(Exception e) {
				Log.e("CallableTask", "Error logging in via OAuth.", e);
				
				Toast.makeText(
						FriendsActivity.this,
						"Login failed, check your Internet connection and credentials.",
						Toast.LENGTH_SHORT).show();
			}
		});

		mFriendGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Log.d(TAG, "Friend id tapped: " + id + ", name: " + mFriendsList.get(position).getFirst());
				SpringDataREST.get(getApplicationContext()).sendChallenge(id);
			}
		});

	}

	
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	public void updateFriendsView(ArrayList<Player> friends) {
		Log.d(TAG, "FriendsActivity::updateFriendsView() called; " + friends.size() + " friends were found");
		mFriendGrid.setAdapter(new FriendAdapter(FriendsActivity.this, friends));
	}
	
	public class FriendAdapter extends BaseAdapter {
		private static final int PADDING = 8;
		private static final int WIDTH = 250;
		private static final int HEIGHT = 250;
		private static final String TAG = "FriendAdapter";
		private Context mContext;
		private List<Player> mFriendIds;

		public FriendAdapter(Context c, List<Player> ids) {
			mContext = c;
			this.mFriendIds = ids;
		}

		@Override
		public int getCount() {
			return mFriendIds.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		// Will get called to provide the ID that
		// is passed to OnItemClickListener.onItemClick()
		@Override
		public long getItemId(int position) {
			Log.d(TAG, (mFriendIds.get(position).getFirst()) + " returning: " + (mFriendIds.get(position).getId()));
			
			return (mFriendIds.get(position).getId());
		}

		// create a new ImageView for each item referenced by the Adapter
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
////			ProfilePictureView imageView = (ProfilePictureView) convertView;
//			TextView imageView = (TextView)convertView;
//			// if convertView's not recycled, initialize some attributes
//			if (imageView == null) {
//				imageView = new TextView(mContext);
//				//imageView.setBackgroundColor(Color.WHITE);
////				imageView.setLayoutParams(new GridView.LayoutParams(WIDTH, HEIGHT));
////				imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
////				imageView.setPresetSize(ProfilePictureView.NORMAL);
//			}
			Player p = mFriendIds.get(position);
			convertView = getLayoutInflater().inflate(R.layout.friend, null);
            TextView firstNameTV = (TextView)convertView.findViewById(R.id.firstNameTextView);
            firstNameTV.setText(p.getFirst());
            TextView lastNameTV = (TextView)convertView.findViewById(R.id.lastNameTextView);
            lastNameTV.setText(p.getLast());
			//imageView.setLines(2);
			if(p == null)
				return null;
			//imageView.setText(p.getFirst() + "\n " + p.getLast());
//			imageView.setProfileId(mFriendIds.get(position).toString());
			return convertView;
		}
	}

}
