package com.cjhbuy.common;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class TopTagTextView extends TextView {

	public TopTagTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TopTagTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//
		// canvas.rotate(45);
		// canvas.translate(getHeight()/3, getWidth()/3);
		Log.i("zkq", getHeight() + "----height");
		Log.i("zkq", getWidth() + "----width");
		
		canvas.rotate(45,getMeasuredWidth()/3,getMeasuredHeight()/3);
//		Matrix matrix=new Matrix()
//		canvas.setMatrix(new Matrix(getMatrix()));
		//canvas.rotate(45, getMeasuredWidth() / 3, getMeasuredHeight() / 3);
		Log.i("zkq", getMeasuredWidth() + "----measure-width");
		Log.i("zkq", getMeasuredHeight() + "----measure-height");
		super.onDraw(canvas);
	}

}
