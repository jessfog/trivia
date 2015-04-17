package org.magnum.videoup.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import client.MovieSet;
import client.MovieSetSvc;
import client.MovieSetSvcApi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class VideoListActivity extends Activity {

	@InjectView(R.id.videoList)
	protected ListView videoList_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_list);

		ButterKnife.inject(this);
		

		videoList_.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) {
				Log.i("VideoListActivity", "Clicked on item: " + id);
			    Toast.makeText(getApplicationContext(), "selected Item Name is " + id, Toast.LENGTH_LONG).show();
				
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		fetchMovieSets();
	}

	private void refreshVideos() {
		final VideoSvcApi svc = VideoSvc.getOrShowLogin(this);

		if (svc != null) {
			CallableTask.invoke(new Callable<Collection<Video>>() {

				@Override
				public Collection<Video> call() throws Exception {
					return svc.getVideoList();
				}
			}, new TaskCallback<Collection<Video>>() {

				@Override
				public void success(Collection<Video> result) {
					List<String> names = new ArrayList<String>();
					int i = 0;
					for (Video v : result) {
						Log.d("VideoListActivity", "video id: " + v.getId() + " name: " + v.getName());
						names.add(v.getName()+ i++);
					}
					videoList_.setAdapter(new ArrayAdapter<String>(
							VideoListActivity.this,
							android.R.layout.simple_list_item_1, names));
					
				}

				@Override
				public void error(Exception e) {
					Toast.makeText(
							VideoListActivity.this,
							"Unable to fetch the video list, please login again.",
							Toast.LENGTH_SHORT).show();

					startActivity(new Intent(VideoListActivity.this,
							LoginScreenActivity.class));
				}
			});
		}
	}
	

	private void fetchMovieSets() {
		final MovieSetSvcApi svc = MovieSetSvc.getOrShowLogin(this);

		if (svc != null) {
			CallableTask.invoke(new Callable<Collection<MovieSet>>() {

				@Override
				public Collection<MovieSet> call() throws Exception {
					return svc.getMovieSetList();
				}
			}, new TaskCallback<Collection<MovieSet>>() {

				@Override
				public void success(Collection<MovieSet> result) {
					List<String> names = new ArrayList<String>();
					for (MovieSet v : result) {
						names.add(v.getQuestion());
					}
					videoList_.setAdapter(new ArrayAdapter<String>(
							VideoListActivity.this,
							android.R.layout.simple_list_item_1, names));
				}

				@Override
				public void error(Exception e) {
					Toast.makeText(
							VideoListActivity.this,
							"Unable to fetch the video list, please login again.",
							Toast.LENGTH_SHORT).show();

					startActivity(new Intent(VideoListActivity.this,
							LoginScreenActivity.class));
				}
			});
		}
		else {
			Toast.makeText(
					VideoListActivity.this,
					"Unable to fetch the video list, please login again.",
					Toast.LENGTH_SHORT).show();
		}
	}

}
