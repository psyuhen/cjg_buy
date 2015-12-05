package com.cjhbuy.activity;

import java.io.File;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cjhbuy.auth.SessionManager;
import com.cjhbuy.bean.ResponseInfo;
import com.cjhbuy.bean.User;
import com.cjhbuy.common.Constants;
import com.cjhbuy.utils.CommonsUtil;
import com.cjhbuy.utils.FileUtil;
import com.cjhbuy.utils.HttpUtil;
import com.cjhbuy.utils.ImageUtil;
import com.cjhbuy.utils.JsonUtil;
import com.cjhbuy.utils.QiNiuUtil;
import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.qiniu.android.storage.UpCompletionHandler;
/**
 * 个人信息
 * @author pansen
 *
 */
public class SettingActivity extends BaseActivity implements OnClickListener {
	private Logger LOGGER = LoggerFactory.getLogger(SettingActivity.class);

	private RelativeLayout setting_item_about_rl;
	private EditText setting_name_text_details;//名称
	private EditText setting_qq_text_details;//QQ
	private EditText setting_wechat_text_details;//微信
	private ImageView setting_me_image_head;//头像
	// 添加图片对话框
	private AlertDialog imageChooseDialog = null;
	// 获取的图片和路径
	private String picturePath;
	private String ImageName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initData();
	}

	@Override
	public void initView() {
		super.initView();
		right_text.setVisibility(View.VISIBLE);
		right_text.setText("完成");
		right_imgbtn.setVisibility(View.GONE);
		title.setText("个人信息");
		
		setting_item_about_rl=(RelativeLayout) findViewById(R.id.setting_item_about_rl);
		setting_item_about_rl.setOnClickListener(this);
		
		setting_name_text_details = (EditText)findViewById(R.id.setting_name_text_details);
		setting_name_text_details.setText(sessionManager.getUserName());
		setting_qq_text_details = (EditText)findViewById(R.id.setting_qq_text_details);
		setting_wechat_text_details = (EditText)findViewById(R.id.setting_wechat_text_details);
		setting_me_image_head = (ImageView)findViewById(R.id.setting_me_image_head);
		setting_me_image_head.setOnClickListener(this);
		
		Button setting_logout_btn = (Button) findViewById(R.id.setting_logout_btn);
		setting_logout_btn.setOnClickListener(this);
		Button setting_exit_btn = (Button) findViewById(R.id.setting_exit_btn);
		setting_exit_btn.setOnClickListener(this);
	}

	private void initData() {
		queryById();
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.setting_item_about_rl:
			startActivity(new Intent(SettingActivity.this, AboutUsActivity.class));
			break;

		case R.id.setting_exit_btn:
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// 设置对话框标题
			isExit.setTitle("系统提示");
			// 设置对话框消息
			isExit.setMessage("确定要退出吗");
			// 添加选择按钮并注册监听
			isExit.setButton(DialogInterface.BUTTON_POSITIVE,"确定", listener);
			isExit.setButton(DialogInterface.BUTTON_NEGATIVE,"取消", listener);
			// 显示对话框
			isExit.show();
			break;
		case R.id.setting_logout_btn:
			logout();
			break;
		case R.id.right_text:
			finishEdit();
			break;
		case R.id.setting_me_image_head:
			//showImageChoose();
			break;
		/*case R.id.dialog_album:
			imageChooseDialog.dismiss();
			CommonsUtil.opengallry(SettingActivity.this);
			break;
		case R.id.dialog_camera:
			CommonsUtil.openCamera(SettingActivity.this);
			break;
		case R.id.dialog_cancel:
			imageChooseDialog.dismiss();
			break;*/
		default:
			break;
		}
	}
	
	/*private void showImageChoose() {
		if(imageChooseDialog == null){
			imageChooseDialog = ImageUtil.showImageChoose(SettingActivity.this);
		}else{
			imageChooseDialog.show();
		}
	}*/
	
	/**
	 * 获取照片
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.IMAGE_REQUEST_CODE://打开相册后返回的数据
			if(data == null){
				break;
			}
			Uri selectImage = data.getData();
			if (selectImage != null) {
				String uriStr = selectImage.toString();
				String path = uriStr.substring(10, uriStr.length());
				if (path.startsWith("com.sec.android.gallery3d")) {
					Toast.makeText(
							this,
							"It's auto backup pic path:"
									+ selectImage.toString(),
							Toast.LENGTH_SHORT).show();
				}
			}
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = SettingActivity.this.getContentResolver().query(
					selectImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			ImageName = picturePath.substring(picturePath.lastIndexOf("."),
					picturePath.length());
			ImageName = "test123" + ImageName;
			cursor.close();
			//ImageUtil.startPhotoZoom(data.getData(), SettingActivity.this);
			break;
		case Constants.CAMERA_REQUEST_CODE://打开相机后返回来数据 
			if (CommonsUtil.hasSdcard()) {
				File tempFile = FileUtil.getAppFolderFile(Constants.IMAGE_FILE_NAME);
				//ImageUtil.startPhotoZoom(Uri.fromFile(tempFile), SettingActivity.this);
			} else {
				Toast.makeText(SettingActivity.this, "未找到存储卡，无法存储照片！",
						Toast.LENGTH_LONG).show();
			}

			break;
		case Constants.RESULT_REQUEST_CODE:
			if (data != null) {
				getImageToView(data, SettingActivity.this);
				imageChooseDialog.hide();
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data, Context context) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			if(photo == null){
				setting_me_image_head.setImageResource(R.drawable.login_head_icon);
				return;
			}
			
			setting_me_image_head.setImageBitmap(photo);
			//上传图片到7牛
			File image = ImageUtil.bitmap2file(photo);
			if(image == null){
				CommonsUtil.showShortToast(getApplicationContext(), "生成图片文件失败");
				return;
			}
			int user_id = sessionManager.getUserId();
			QiNiuUtil.resumeUploadFile(image.getName(), image, String.valueOf(user_id), new UpCompletionHandler() {
				@Override
				public void complete(String key, com.qiniu.android.http.ResponseInfo info, JSONObject jsonObj) {
					if(info.statusCode == HttpStatus.OK.value()){
//						CommonsUtil.showShortToast(getApplicationContext(), "更新图片成功");
						LOGGER.info("上传图片成功！");
						//更新数据库的文件名
						User user = new User();
						user.setUser_id(sessionManager.getUserId());
						user.setPhoto(key);
						saveUser(user);
					}else{
//						CommonsUtil.showShortToast(getApplicationContext(), "保存图片到服务器失败");
						LOGGER.error("保存图片到服务器失败" + info.error);
					}
				}
			});
		}
	}
	//注销
	private void logout(){
		sessionManager.clearData();
		startActivity(new Intent(SettingActivity.this, HomeActivity.class));
		finish();
	}
	
	private void finishEdit(){
		String name = setting_name_text_details.getText().toString();
		String qq = setting_qq_text_details.getText().toString();
		String wechat = setting_wechat_text_details.getText().toString();
		
		User user = new User();
		user.setUser_id(sessionManager.getUserId());
		user.setName(name);
		user.setQq(qq);
		user.setWe_chat(wechat);
		
		saveUser(user);
	}
	
	//更新用户信息
	private void saveUser(User user){
		startProgressDialog();
		new saveUserTask(user).execute();
	}
	
	private class saveUserTask extends AsyncTask<Void, Void, ResponseInfo>{
		private User user;
		public saveUserTask(User user) {
			this.user = user;
		}
		@Override
		protected ResponseInfo doInBackground(Void... params) {
			String url = HttpUtil.BASE_URL + "/user/updateUser.do";
			try{
				String json = HttpUtil.postRequest(url, user);
				if(json == null){
					return null;
				}
				
				ResponseInfo responseInfo = JsonUtil.parse2Object(json, ResponseInfo.class);
				
				return responseInfo;
			}catch (Exception e) {
				LOGGER.error(">>> 修改失败", e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ResponseInfo responseInfo) {
			super.onPostExecute(responseInfo);
			stopProgressDialog();
			
			if(responseInfo == null){
				CommonsUtil.showLongToast(getApplicationContext(), "修改失败");
				return;
			}
			
			CommonsUtil.showLongToast(getApplicationContext(), responseInfo.getDesc());
			
			if(ResponseInfo.SUCCESS.equals(responseInfo.getStatus())){
				if(user.getName()  != null){
					sessionManager.put(SessionManager.KEY_NAME, user.getName());
				}
				if(user.getQq()  != null){
					sessionManager.put(SessionManager.KEY_QQ, user.getQq());
				}
				if(user.getWe_chat()  != null){
					sessionManager.put(SessionManager.KEY_WE_CHAT, user.getWe_chat());
				}
				if(user.getPhoto()  != null){
					sessionManager.put(SessionManager.KEY_PHOTO, user.getPhoto());
				}
			}
		}
	}
	
	
	private void queryById(){
		startProgressDialog();
		new queryByIdTask(sessionManager.getUserId()).execute();
	}
	
	private class queryByIdTask extends AsyncTask<Void, Void, User>{
		private int user_id;
		public queryByIdTask(int user_id) {
			this.user_id = user_id;
		}
		@Override
		protected User doInBackground(Void... params) {
			String url = HttpUtil.BASE_URL + "/user/query.do?id="+user_id;
			try{
				String json = HttpUtil.getRequest(url);
				if(json == null){
					return null;
				}
				
				User user = JsonUtil.parse2Object(json, User.class);
				
				return user;
			}catch (Exception e) {
				LOGGER.error(">>> 查询用户信息失败", e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(User user) {
			super.onPostExecute(user);
			stopProgressDialog();
			
			if(user == null){
				CommonsUtil.showLongToast(getApplicationContext(), "查询用户信息失败");
				return;
			}
			
			setting_name_text_details.setText(user.getName());
			setting_qq_text_details.setText(user.getQq());
			setting_wechat_text_details.setText(user.getWe_chat());
			
			String photo = user.getPhoto();
			getImageToView(photo);
		}
	}
	
	
	/**
	 * 先从本地中获取，如果获取不到再获取网络图片
	 * @param fileName
	 */
	private void getImageToView(String fileName){
		Bitmap bitmap = null;
		bitmap = FileUtil.getCacheFile(fileName);
		
		if(bitmap != null){//防止没有图片的时候，报错
			setting_me_image_head.setImageBitmap(bitmap);
		}else{
			setting_me_image_head.setImageResource(R.drawable.login_head_icon);
		}
	}
	
	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:// "确认"按钮退出程序
				sessionManager.clearData();
				/**
				 * 完全退出程序
				 */
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			case DialogInterface.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};
}
