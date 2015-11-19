package com.cjhbuy.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.cjhbuy.activity.R;
import com.cjhbuy.utils.AVImClientManager;
import com.cjhbuy.utils.DateUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.StringUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
/**
 * LeanCloud消息
 * @author pansen
 *
 */
public class MultipleItemAdapter extends BaseAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultipleItemAdapter.class);


	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}
	// 时间间隔最小为十分钟
	private final long TIME_INTERVAL = 10 * 60 * 1000;

	private List<AVIMTypedMessage> messageList;
	
	private Context ctx;

	private LayoutInflater mInflater;
	private MediaPlayer mMediaPlayer = new MediaPlayer();

	public MultipleItemAdapter(Context context, List<AVIMTypedMessage> messageList) {
		ctx = context;
		this.messageList = messageList;
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return messageList.size();
	}

	public Object getItem(int position) {
		return messageList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		AVIMTypedMessage msg = messageList.get(position);
		
		if(msg.getFrom().equals(AVImClientManager.getInstance().getClientId())){
			return IMsgViewType.IMVT_TO_MSG;
		}

		return IMsgViewType.IMVT_COM_MSG;
	}

	public int getViewTypeCount() {
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final AVIMTypedMessage msg = messageList.get(position);
		int itemViewType = getItemViewType(position);
		boolean isComMsg = itemViewType == IMsgViewType.IMVT_COM_MSG ? true : false;

		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
			}

			viewHolder = new ViewHolder();
			viewHolder.iv_userhead = (ImageView) convertView.findViewById(R.id.iv_userhead);
			viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			viewHolder.isComMsg = isComMsg;

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		//发送时间
		String time = "";
		if(shouldShowTime(position)){
			time = DateUtil.today("yyyy-MM-dd HH:mm");
			long timestamp = msg.getTimestamp();
			if(timestamp != 0){
				time = DateUtil.getDateStrByLong(timestamp);
			}
			viewHolder.tvSendTime.setText(time);
			viewHolder.tvSendTime.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvSendTime.setVisibility(View.GONE);
		}
		
		boolean isAudio = false;
		String fileName = "";
		if(msg instanceof AVIMAudioMessage){//语音的处理
			isAudio = true;
			AVIMAudioMessage audio = (AVIMAudioMessage)msg;
			Map<String, Object> attrs = audio.getAttrs();
			
			viewHolder.tvContent.setText("");
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chatto_voice_playing, 0);
			viewHolder.tvTime.setText(StringUtil.trimToEmpty(attrs.get("audio_time")));
			
			//头像
			Object p = attrs.get("photo");
			String photo = StringUtil.trimToEmpty(p);
			if(p == null || "".equals(photo)){
				viewHolder.iv_userhead.setImageResource(R.drawable.ic_launcher);
			}else{
				viewHolder.iv_userhead.setImageBitmap(FileUtil.getCacheFile(photo));
			}
			
			//语音文件
			AVFile avFile = audio.getAVFile();
			String url = avFile.getUrl();
			fileName = StringUtil.trimToEmpty(attrs.get("file_name"));
			FileUtil.getRemoteFile(url, fileName);
		}
		
		if(!isAudio){//文本的处理
			AVIMTextMessage textMsg = (AVIMTextMessage)msg;
			Map<String, Object> attrs = textMsg.getAttrs();
			viewHolder.tvContent.setText(textMsg.getText());			
			viewHolder.tvContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			viewHolder.tvTime.setText("");
			
			//头像
			Object p = attrs.get("photo");
			String photo = StringUtil.trimToEmpty(p);
			if(p == null || "".equals(photo)){
				viewHolder.iv_userhead.setImageResource(R.drawable.ic_launcher);
			}else{
				viewHolder.iv_userhead.setImageBitmap(FileUtil.getCacheFile(photo));
			}
		}
		
		viewHolder.tvContent.setOnClickListener(new PlayFileClick(isAudio, fileName));
		viewHolder.tvUserName.setText("");
		
		return convertView;
	}

	static class ViewHolder {
		public ImageView iv_userhead;
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public TextView tvTime;
		public boolean isComMsg = true;
	}

	private class PlayFileClick implements OnClickListener{
		private boolean isAudio;
		private String fileName;
		
		public PlayFileClick (boolean isAudio, String fileName){
			this.isAudio = isAudio;
			this.fileName = fileName;
		}
		@Override
		public void onClick(View v) {
			if(isAudio){
				playMusic(FileUtil.getAudioFolder() + "/" + fileName);
			}
		}
	}
	
	/**
	 * @Description
	 * @param name
	 */
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			LOGGER.error("播放出错", e);
		}
	}
	
	/**
	 * 十分钟之内不显示时间
	 * @param position
	 * @return
	 */
	private boolean shouldShowTime(int position) {
		if (position == 0) {
			return true;
		}
		long lastTime = messageList.get(position - 1).getTimestamp();
		long curTime = messageList.get(position).getTimestamp();
		return curTime - lastTime > TIME_INTERVAL;
	}
}
