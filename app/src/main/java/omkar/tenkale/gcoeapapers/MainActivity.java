package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.downloader.PRDownloader;
import java.util.ArrayList;

import omkar.tenkale.gcoeapapers.R;
import devlight.io.library.ntb.NavigationTabBar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import static omkar.tenkale.gcoeapapers.UtilityMethods.clearTempFiles;

public class MainActivity extends AppCompatActivity implements Feed.FeedFragmentInterface, Papers.PapersFragmentInterface, Download.DownloadFragmentInterface, Available.AvailableFragmentInterface, About.AboutFragmentInterface {
    public ArrayList<Fragment> fragments;
    myFragmentAdapter mainPageAdapter;
    ViewPager mainViewPager;
    Download download_fragment;
    Available available_fragment;
    PassCallToB passOnToB;
    PassCallToB PassOnToP;
    NavigationTabBar navigationTabBar;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/Rubik-Medium.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());
        setContentView(R.layout.activity_main);
        UtilityMethods.disableURIcheck();
        //mainPageAdapter  = new myFragmentAdapter(getSupportFragmentManager());


/*
        PRDownloader.download("https://filedldrhost.cu.ma/counter.php",this.getApplicationInfo().dataDir + "/databases/","temp")
                .build().start(
                new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                    }
                    @Override
                    public void onError(Error error) {


                    }
                }
        );
*/
        fragments = new ArrayList();
        mainPageAdapter = new myFragmentAdapter(getSupportFragmentManager(), this);
        mainViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mainViewPager.setAdapter(mainPageAdapter);
        mainViewPager.setOffscreenPageLimit(5);
        initNTB();
        download_fragment = mainPageAdapter.getD();
        available_fragment = mainPageAdapter.getA();
//        passOnToB = (PassCallToB) download_fragment;
        //   PassOnToP = (PassCallToB) available_fragment;
        clearTempFiles(this);
    }
  /*  public void setStatusBarColor(){

        if (Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.KITKAT)
        {

        }
        else
        {
            Window window = MainActivity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(activity,R.color.my_statusbar_color));

        }

    }
*/

    public void initNTB() {
        final String[] colors = getResources().getStringArray(R.array.default_preview);

        navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        navigationTabBar.setTypeface("fonts/Rubik-Medium.ttf");
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icon_feed),
                        Color.parseColor(colors[0]))
                        //  .selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("Home")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icon_papers),
                        Color.parseColor(colors[1]))
//                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("Papers")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icon_download),
                        Color.parseColor(colors[2]))
                        // .selectedIcon(getResources().getDrawable(R.drawable.ic_seventh))
                        .title("Download")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icon_available),
                        Color.parseColor(colors[3]))
                        //  .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .title("Available")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.icon_info),
                        Color.parseColor(colors[4]))
                        .selectedIcon(getResources().getDrawable(R.drawable.icon_code))
                        .title("About")
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(mainViewPager, 0);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(final int position) {
                // mainViewPager.setCurrentItem(position);
                //  Toast.makeText(getApplicationContext(),"onPageSelected",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.TapToExitToast, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void refresh() {

        ((Available) fragments.get(3)).refreshAvailableFragment();
        ((Papers) fragments.get(1)).result_adapter.notifyDataSetChanged();

        //  PassOnToP.refreshlist();
    }

    @Override
    public void newDownloadFromFragment(DownloadItem item) {
        addDownload(item);
        Log.e("FEEDAPK", "MAINACTIVITY: DOWNLOAD FORWARDED");
    }

    public void addDownload(DownloadItem item) {
        // Fragment download_fragment = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_viewpager + ":" + 3); //  3  is  fragment index
        //download_fragment.addDownload();
        try{((Download) fragments.get(2)).OnDownloadAdded(item);}catch(Exception e){
            Toast.makeText(this,"Some Error Occcured. Restart App if Problem Persists",Toast.LENGTH_LONG).show();
        }

        //passOnToB.passItOn(COmpoundSubject);
    }

    public void setDownloadingEmptyState(Boolean b) {
        Log.e("EMPRTSTATEBUG", "IN MAINACTIVITY SHOWEMPTYSTATE BOOLEAN B = " + b);
        LinearLayout lr = (LinearLayout) this.findViewById(R.id.downloading_fragment_empty_state);
        if (b) {
            Log.e("EMPRTSTATEBUG", "IN MAINACTIVITY SHOWEMPTYSTATESETTING  VIEW VISIBLE  =");

            lr.setVisibility(View.VISIBLE);
        } else {

            Log.e("EMPRTSTATEBUG", "IN MAINACTIVITY SHOWEMPTYSTATESETTING  VIEW GONE   =");
            lr.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PRDownloader.shutDown();
    }

    public interface PassCallToB {


        void passItOn(String COmpoundSubject);

        void refreshlist();
    }
    public void submitPDF(View b){
        ((About)this.fragments.get(4)).pdf.performClick();
    }
}
