package com.cjhbuy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cjhbuy.activity.AddressActivity;
import com.cjhbuy.activity.ChatActivity;
import com.cjhbuy.activity.HomeActivity;
import com.cjhbuy.activity.LoginActivity;
import com.cjhbuy.activity.R;

public class MeFragment extends Fragment implements OnClickListener {
	private Button me_login_btn;
	private RelativeLayout me_order_rl;
	private RelativeLayout me_address_rl;
	private RelativeLayout me_coupon_rl;
	private RelativeLayout me_share_rl;
	private RelativeLayout me_remind_ll;
	private RelativeLayout me_customerservice_rl;
	private RelativeLayout me_help_rl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View contentView = inflater.inflate(R.layout.fragmeng_me, container,
				false);
		me_login_btn = (Button) contentView.findViewById(R.id.me_login_btn);
		me_login_btn.setOnClickListener(this);
		me_order_rl = (RelativeLayout) contentView
				.findViewById(R.id.me_order_rl);
		me_order_rl.setOnClickListener(this);
		me_address_rl = (RelativeLayout) contentView
				.findViewById(R.id.me_address_rl);
		me_address_rl.setOnClickListener(this);
		me_coupon_rl = (RelativeLayout) contentView
				.findViewById(R.id.me_coupon_rl);
		me_coupon_rl.setOnClickListener(this);
		me_share_rl = (RelativeLayout) contentView
				.findViewById(R.id.me_share_rl);
		me_share_rl.setOnClickListener(this);
		me_remind_ll = (RelativeLayout) contentView
				.findViewById(R.id.me_remind_ll);
		me_remind_ll.setOnClickListener(this);
		me_customerservice_rl = (RelativeLayout) contentView
				.findViewById(R.id.me_customerservice_rl);
		me_customerservice_rl.setOnClickListener(this);
		me_help_rl = (RelativeLayout) contentView.findViewById(R.id.me_aboutus_rl);
		me_help_rl.setOnClickListener(this);

		return contentView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.me_login_btn:
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.me_order_rl:
			Intent intent = new Intent();
			intent.putExtra("order", "order");
			intent.setClass(getActivity(), HomeActivity.class);
			startActivity(intent);
			break;
		case R.id.me_address_rl:
			startActivity(new Intent(getActivity(), AddressActivity.class));
			break;
		case R.id.me_coupon_rl:
			break;
		case R.id.me_share_rl:
			Toast.makeText(getActivity(), "分享超家伙(友盟或者别的分享)", Toast.LENGTH_LONG)
					.show();
			break;
		case R.id.me_remind_ll:

			break;
		case R.id.me_customerservice_rl:
			startActivity(new Intent(getActivity(), ChatActivity.class));
			break;
		case R.id.me_aboutus_rl:
			break;
		default:
			break;
		}
	}

}
