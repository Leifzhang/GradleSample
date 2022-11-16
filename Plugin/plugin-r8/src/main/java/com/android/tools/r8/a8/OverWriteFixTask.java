package com.android.tools.r8.a8;

import com.android.tools.r8.graph.AppInfo;
import com.android.tools.r8.utils.InternalOptions;

public class OverWriteFixTask {

    public static void execute(AppInfo appInfo, InternalOptions options) {
        String version = System.getProperty("fawkes.maven.base.version");
        if (version != null && version.length() > 0 &&
                options.debug) {
            new KotlinOverWriteFixTask(appInfo).execute();
        }
    }
}
