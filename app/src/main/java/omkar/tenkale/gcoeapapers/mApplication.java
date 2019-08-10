package omkar.tenkale.gcoeapapers;

import android.app.Application;

import omkar.tenkale.gcoeapapers.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class mApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Rubik-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
