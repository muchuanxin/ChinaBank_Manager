package com.xd.aselab.chinabankmanager.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2017/10/29.
 */

public class RequestDialog {

    private ProgressDialog progDialog = null;// 进度条

    /**
     * 显示进度框
     */
    public void showProgressDialog(Context context, String msg) {
        if (progDialog == null)
            progDialog = new ProgressDialog(context);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage(msg);
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    public void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

}
