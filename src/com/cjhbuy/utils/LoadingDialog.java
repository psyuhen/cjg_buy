package com.cjhbuy.utils;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.cjhbuy.activity.R;

/**
 * 加载框
 * @author pansen
 *
 */
public class LoadingDialog extends ProgressDialog {
    private Context context = null;

    public LoadingDialog(Context context) {
       this(context, 0);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
    }
}
