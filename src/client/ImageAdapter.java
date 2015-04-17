package client;

import java.util.List;

//import com.facebook.widget.ProfilePictureView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private static final int PADDING = 8;
	private static final int WIDTH = 250;
	private static final int HEIGHT = 250;
	private static final String TAG = "ImageAdapter";
	private Context mContext;
	private List<Player> mFriendIds;

	public ImageAdapter(Context c, List<Player> ids) {
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
//		ProfilePictureView imageView = (ProfilePictureView) convertView;
		TextView imageView = (TextView)convertView;
		// if convertView's not recycled, initialize some attributes
		if (imageView == null) {
			imageView = new TextView(mContext);
			//imageView.setBackgroundColor(Color.WHITE);
//			imageView.setLayoutParams(new GridView.LayoutParams(WIDTH, HEIGHT));
//			imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
//			imageView.setPresetSize(ProfilePictureView.NORMAL);
		}
		imageView.setLines(2);
		Player p = mFriendIds.get(position);
		if(p == null)
			return null;
		imageView.setText(p.getFirst() + "\n " + p.getLast());
//		imageView.setProfileId(mFriendIds.get(position).toString());
		return imageView;
	}
}
