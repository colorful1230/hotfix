package com.test.hotfix;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by zhaolin on 2017/12/25.
 */

public class PatchApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        String patchPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "patch.apk";
        HotfixUtils.checkPatch(base, patchPath);
    }
}
