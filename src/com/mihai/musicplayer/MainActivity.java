package com.mihai.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mihai.adapter.MyAdapter;
import com.mihai.constant.Constants;
import com.mihai.service.MusicService;
import com.mihai.utils.MediaUtils;
import com.mihai.view.ScrollableViewGroup;
import com.mihai.view.ScrollableViewGroup.OnCurrentViewChangedListener;



public class MainActivity extends Activity implements OnClickListener {

	private TextView mTv_curduration;
	private TextView mTv_minilrc;
	private TextView mTv_totalduration;
	private SeekBar mSk_duration;
	private ImageView mIv_bottom_model;
	private ImageView mIv_bottom_play;
	private ListView mLv_list;
	private ScrollableViewGroup mSvg_main;
	private Handler handler = new Handler() {//接收结果,刷新ui
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.MSG_ONPREPARED:
				int currentPosition = msg.arg1;
				int totalDuration = msg.arg2;
				mTv_curduration.setText(MediaUtils.duration2Str(currentPosition));
				mTv_totalduration.setText(MediaUtils.duration2Str(totalDuration));
				mSk_duration.setMax(totalDuration);
				mSk_duration.setProgress(currentPosition);
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
		initListener();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mTv_curduration = (TextView) findViewById(R.id.tv_curduration);
		mTv_minilrc = (TextView) findViewById(R.id.tv_minilrc);
		mTv_totalduration = (TextView) findViewById(R.id.tv_totalduration);
		mSk_duration = (SeekBar) findViewById(R.id.sk_duration);
		mIv_bottom_model = (ImageView) findViewById(R.id.iv_bottom_model);
		mIv_bottom_play = (ImageView) findViewById(R.id.iv_bottom_play);
		mLv_list = (ListView) findViewById(R.id.lv_list);
		mSvg_main = (ScrollableViewGroup) findViewById(R.id.svg_main);
		//默认选中第一个
		findViewById(R.id.ib_top_play).setSelected(true);
	}

	/**
	 * 数据的加载
	 */
	private void initData() {
		MediaUtils.initSongList(this);
		mLv_list.setAdapter(new MyAdapter(this));
	}

	/**
	 * 初始化监听
	 */
	private void initListener() {
		findViewById(R.id.ib_top_play).setOnClickListener(this);
		findViewById(R.id.ib_top_list).setOnClickListener(this);
		findViewById(R.id.ib_top_lrc).setOnClickListener(this);
		findViewById(R.id.ib_top_volumn).setOnClickListener(this);
		findViewById(R.id.ib_bottom_model).setOnClickListener(this);
		findViewById(R.id.ib_bottom_last).setOnClickListener(this);
		findViewById(R.id.ib_bottom_play).setOnClickListener(this);
		findViewById(R.id.ib_bottom_next).setOnClickListener(this);
		findViewById(R.id.ib_bottom_update).setOnClickListener(this);

		mSvg_main.setOnCurrentViewChangedListener(new OnCurrentViewChangedListener() {

			@Override
			public void onCurrentViewChanged(View view, int currentview) {
				System.out.println("-------------" + currentview + "---------------");
				setTopSelected(topArr[currentview]);
			}
		});
		mLv_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO
				//1.修改curposition
				changeColorWhite();
				MediaUtils.CURPOSITION = position;
				changeColorGreen();
				//2.播放
				startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
				//3.修改图标
				mIv_bottom_play.setImageResource(R.drawable.appwidget_pause);
			}
		});
		mSk_duration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {//停止拖拽
				mSk_duration.setProgress(seekBar.getProgress());
				startMediaService("进度", seekBar.getProgress());
				//音乐播放器,跳转到指定的位置播放
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {//触摸到拖拽按钮
				// TODO

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {//进度改变
				// TODO

			}
		});
	}

	private int[] topArr = { R.id.ib_top_play, R.id.ib_top_list, R.id.ib_top_lrc, R.id.ib_top_volumn };

	/**
	 * 顶部按钮的选中效果
	 * @param selectedId
	 */
	private void setTopSelected(int selectedId) {
		//1.还原所有控件的效果,让top上面的4个按钮显示效果都是未选中
		findViewById(R.id.ib_top_play).setSelected(false);
		findViewById(R.id.ib_top_list).setSelected(false);
		findViewById(R.id.ib_top_lrc).setSelected(false);
		findViewById(R.id.ib_top_volumn).setSelected(false);

		//2.让传递进来的控件有选中效果
		findViewById(selectedId).setSelected(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_top_play:
			mSvg_main.setCurrentView(0);//mSvg_main显示第一个孩子
			setTopSelected(R.id.ib_top_play);
			break;
		case R.id.ib_top_list:
			mSvg_main.setCurrentView(1);//mSvg_main显示第二个孩子
			setTopSelected(R.id.ib_top_list);
			break;
		case R.id.ib_top_lrc:
			mSvg_main.setCurrentView(2);//mSvg_main显示第三个孩子
			setTopSelected(R.id.ib_top_lrc);

			break;
		case R.id.ib_bottom_play://播放按钮,点击同一个按钮.有两个操作.需要定义一个变量进行控制
			//启动服务.而且让服务播放音乐
			if (MediaUtils.CURSTATE == Constants.STATE_STOP) {//默认是停止,点击就变播放
				startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
				//修改图标
				mIv_bottom_play.setImageResource(R.drawable.appwidget_pause);
			} else if (MediaUtils.CURSTATE == Constants.STATE_PLAY) {//第二次点击的时候.当前的状态是播放
				startMediaService("暂停");
				//修改图标
				mIv_bottom_play.setImageResource(R.drawable.img_playback_bt_play);
			} else if (MediaUtils.CURSTATE == Constants.STATE_PAUSE) {//第三次点击的时候.当前的状态是暂停
				startMediaService("继续");
				//修改图标
				mIv_bottom_play.setImageResource(R.drawable.appwidget_pause);
			}

			break;
		case R.id.ib_bottom_last:
			if (MediaUtils.CURPOSITION > 0) {
				changeColorWhite();
				MediaUtils.CURPOSITION--;
				changeColorGreen();
				//2.播放
				startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
				//3.修改图标
				mIv_bottom_play.setImageResource(R.drawable.appwidget_pause);
			}
			break;
		case R.id.ib_bottom_next:

			if (MediaUtils.CURPOSITION < MediaUtils.songList.size() - 1) {//MediaUtils.songList.size() - 1
				changeColorWhite();
				MediaUtils.CURPOSITION++;
				changeColorGreen();
				//2.播放
				startMediaService("播放", MediaUtils.songList.get(MediaUtils.CURPOSITION).path);
				//3.修改图标
				mIv_bottom_play.setImageResource(R.drawable.appwidget_pause);

			}

			break;
		case R.id.ib_bottom_model:

			break;
		case R.id.ib_top_volumn:

			break;

		case R.id.ib_bottom_update:

			break;

		default:
			break;
		}
	}

	public void startMediaService(String option) {
		Intent service = new Intent(MainActivity.this, MusicService.class);
		service.putExtra("messenger", new Messenger(handler));
		service.putExtra("option", option);
		startService(service);
	}

	public void startMediaService(String option, String path) {
		Intent service = new Intent(MainActivity.this, MusicService.class);
		service.putExtra("option", option);
		service.putExtra("messenger", new Messenger(handler));
		service.putExtra("path", path);
		startService(service);
	}

	public void startMediaService(String option, int progress) {
		Intent service = new Intent(MainActivity.this, MusicService.class);
		service.putExtra("option", option);
		service.putExtra("messenger", new Messenger(handler));
		service.putExtra("progress", progress);
		startService(service);
	}

	/**
	 * 修改颜色.只要我们的curPostion修改了.那么颜色值就需要修改
	 * 
	 * @param color
	 */
	//一开始还以为为什么不直接用adapter直接通知更新就可以了，因为getView中已经设置了颜色的改变
	//因为我忘记了getView的调用时间，直接在item点击的时候并没有调用getView，在item加载的时候才是
	
	public void changeColorWhite() {
		//在getView中给holder.tv_title.setTag(position);现在就可以在MainAcitivity取得这个对象进行属性的修改了。
		TextView tv = (TextView) mLv_list.findViewWithTag(MediaUtils.CURPOSITION);
		if (tv != null) {
			tv.setTextColor(Color.WHITE);
		}
	}

	public void changeColorGreen() {
		TextView tv = (TextView) mLv_list.findViewWithTag(MediaUtils.CURPOSITION);
		if (tv != null) {
			tv.setTextColor(Color.GREEN);
		}
	}
}
