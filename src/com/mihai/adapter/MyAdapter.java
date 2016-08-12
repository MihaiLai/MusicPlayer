package com.mihai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mihai.bean.Music;
import com.mihai.musicplayer.R;
import com.mihai.utils.MediaUtils;

public class MyAdapter extends BaseAdapter {
	
	private Context context;

	public MyAdapter(Context context) {
		super();
		this.context = context;
	}
	//数据：被封装在MediaUtils中
	@Override
	public int getCount() {
		if (MediaUtils.songList != null) {
			return MediaUtils.songList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (MediaUtils.songList != null) {
			return MediaUtils.songList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/**---------------视图的初始化---------------**/
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_music, null);
			holder = new ViewHolder();
			convertView.setTag(holder);//设置tag的作用
			holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_artist = (TextView) convertView.findViewById(R.id.tv_artist);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		/**---------------拿到数据---------------**/
		Music music = MediaUtils.songList.get(position);
		/**---------------设置数据---------------**/
		holder.tv_title.setText(music.title);
		holder.tv_artist.setText(music.artist);

		if (MediaUtils.CURPOSITION == position) {
			//			holder.tv_title.setTextColor(这个是接收一个十六进制的颜色值);//00FF00
			//			holder.tv_title.setTextColor(R.color._greeen);//错误做法
			//			holder.tv_title.setTextColor(context.getResources().getColor(R.color._greeen));//正确做法
			holder.tv_title.setTextColor(Color.GREEN);
		} else {
			holder.tv_title.setTextColor(Color.WHITE);
		}
		holder.tv_title.setTag(position);
		return convertView;
	}

	class ViewHolder {
		TextView tv_title;
		TextView tv_artist;
	}
	
}
