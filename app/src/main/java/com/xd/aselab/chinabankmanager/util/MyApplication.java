package com.xd.aselab.chinabankmanager.util;

import android.app.Application;

/**
 * Created by wenqr on 2017/12/3.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFont(this, "DEFAULT", "font/SourceHanSansCN-Regular_0.otf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "font/SourceHanSansCN-Regular_0.otf");
        FontsOverride.setDefaultFont(this, "SERIF", "font/SourceHanSansCN-Regular_0.otf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "font/SourceHanSansCN-Regular_0.otf");
        /*Typeface mTypeface = Typeface.createFromAsset(getAssets(), "font/SourceHanSansCN-Regular_0.otf");

        try {
            Field field = Typeface.class.getDeclaredField("SANS_SERIF");
            field.setAccessible(true);
            field.set(null, mTypeface);
        } catch (NoSuchFieldException e) {
            Log.e("MyApp","加载第三方字体失败。") ;
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }*/


    }
}
