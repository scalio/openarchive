package net.opendasharchive.openarchive;

import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by josh on 3/6/15.
 */
public class OpenArchiveApp extends com.orm.SugarApp {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
