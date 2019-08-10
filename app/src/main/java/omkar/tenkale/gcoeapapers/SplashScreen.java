package omkar.tenkale.gcoeapapers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import omkar.tenkale.gcoeapapers.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


import static omkar.tenkale.gcoeapapers.UtilityMethods.disableURIcheck;
import static omkar.tenkale.gcoeapapers.UtilityMethods.openInBrowser;
import static omkar.tenkale.gcoeapapers.UtilityMethods.tryAPKInstall;

public class SplashScreen extends Activity {


    boolean agreedTOS= false;
    boolean blockLaunch=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableURIcheck();
        setContentView(R.layout.splash_screen);
        UtilityMethods.checkWritePermission(this,false);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Rubik-Medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        if(UtilityMethods.isNetworkAvailable(this)){
            Log.e("2SPLASH", "NETWORK AVAILABLE \n");

//            fetchJSON f = new fetchJSON(this);
//            f.execute();
            checkNewInstall();
            (new fetchJSON(this)).execute();
        }
        else{

            Boolean mandetory = pref.getBoolean("mandetory", false);
            if(mandetory){
                Log.e("2SPLASH", "MANDATORY \n");
                onMandetory(pref.getString("apklink",null));
            }
            else
            {

                Log.e("2SPLASH", " NOT MANDATORY \n");
                checkTOS();
            }
        }
        Log.e("SPLASH", "JSON end AFTER CATCH , IN SHAREDPREF");
    }
    private void checkNewInstall(){try{
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int version = pref.getInt("version",0);
        PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        int currentcode = pInfo.versionCode;
        if(version==0){
            Log.e("#", "in oninstall if \n");
            AnalyticsManager.OnInstall(this, currentcode);
           editor.putInt("version", currentcode);}
        else if (currentcode>version ) {
            editor.putInt("version", currentcode);
            AnalyticsManager.OnUpdate(this,version,currentcode);
            editor.apply();
        }
    }catch (Exception ignored){}

    }

    public void LaunchMainActivity(boolean fromclick) {
        if (!blockLaunch)  {
                        startActivity(new Intent(getApplication(), MainActivity.class));
                        finish();
                    }
/*

        new Handler().postDelayed(new Runnable() {


            @Override

            public void run() {
                Log.e("SPLASH","LAUNCHING ACTIVITY FROM HANDLER RUN");
                startActivity(new Intent(getApplication(), MainActivity.class));
                finish();

            }

        }, 2 * 1000);
        finish();
*/
    }

    public void onMandetory(final String apklink) {

        //hideButton();
   /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        builder.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setTitle("App Update Available");
        alertDialog.setMessage("The version you are using is no longer supported. Please Update app to continue .");
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "UPDATE",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {tryAPKInstall();}
                });
        alertDialog.show();
        */


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.item_mandatory_update_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView btn =  dialog.findViewById(R.id.mandetory_update_dialog_update_btn);
        final TextView subtitle =  dialog.findViewById(R.id.mandetory_update_dialog_subtitle);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !tryAPKInstall(SplashScreen.this)){
                    if(!UtilityMethods.isNetworkAvailable(SplashScreen.this)){
                        subtitle.setText("Please Check Your Internet Connection.");
                        btn.setText("TRY AGAIN");
                        return;}
                    btn.setText("DOWNLOADING");
                   btn.setEnabled(false);
                  final LinearLayout progressroot = dialog.findViewById(R.id.mandetory_update_dialog_progressroot);
                   final ProgressBar progressbar =  dialog.findViewById(R.id.mandetory_update_dialog_progressbar);
                   final TextView progresstv =  dialog.findViewById(R.id.mandetory_update_dialog_progresstv);
                   progressroot.setVisibility(View.VISIBLE);
                   PRDownloader.download(apklink,Environment.getExternalStorageDirectory() + "/GCOEA App/","update.apk")
                           .build()
                           .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                               @Override
                               public void onStartOrResume() {
                                   progressbar.setIndeterminate(false);
                               }
                           })
                           .setOnProgressListener(new OnProgressListener() {
                               @Override
                               public void onProgress(Progress progress) {
                                   long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                   if (progressPercent < 1010 && progressPercent >= 0) {
                                       int percent = (int)progressPercent;
                                       progressbar.setProgress(percent);
                                       progresstv.setText(percent+"%");
                                   }
                               }
                           })
                           .start( new OnDownloadListener() {
                       @Override public void onDownloadComplete() {try{
                           subtitle.setText("Download Successful ! Tap to Install.");
                           btn.setText("INSTALL");
                           btn.setEnabled(true);
                           btn.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                  if (!tryAPKInstall(SplashScreen.this)){
                                      btn.setText("VISIT WEBSITE");
                                      progressroot.setVisibility(View.INVISIBLE);
                                      subtitle.setText("Some Error Occured! Please Visit Website and Download the Latest Version.");
                                      btn.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Context con=v.getContext();
                                              openInBrowser(con,"https://omkar-tenkale.github.io/papers");
                                          }
                                      });
                                  }
                               }
                           });
                       }catch(Exception ignored){} }
                       @Override public void onError(Error error) {
                           btn.setText("VISIT WEBSITE");
                           btn.setEnabled(true);
                           progressroot.setVisibility(View.INVISIBLE);
                           subtitle.setText("Some Error Occured! Please Visit Website and Download Latest Version.");
                           btn.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   openInBrowser(getBaseContext(),"https://omkar-tenkale.github.io/papers/");
                               }});
                       }});


               }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
     dialog.show();
    }

    public void showButton() {
        LinearLayout tos_root = findViewById(R.id.tos_root);

        tos_root.setVisibility(View.VISIBLE);

    }

    public void checkTOS() {


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        agreedTOS = pref.getBoolean("agreedTOS", false);

        if(agreedTOS){

            Log.e("2SPLASH", "AGREED TOS TRUE \n");
            LaunchMainActivity(false);
        }else {

            Log.e("2SPLASH", "AGREED TOS FALSE \n");
            // String path = getFilesDir().getAbsolutePath(); datapath
            TextView t = findViewById(R.id.tos);
            TextView ct = findViewById(R.id.splash_continue_button);
            t.setText(Html.fromHtml("By continuing you agree to the " +
                    "<a href=\"https://omkar-tenkale.github.io/papers/Terms-of-Service.html\">Terms of Service</a>" + " and " + "<a href=\"https://omkar-tenkale.github.io/papers/Privacy-Policy.html\">Privacy Policy</a>"));
            t.setMovementMethod(LinkMovementMethod.getInstance());
            LinearLayout tos_root = findViewById(R.id.tos_root);
            ct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("SPLASHBUGHUNT", "CONTINUE ONCLICK INVOKED");
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("agreedTOS", true);
                    editor.apply();
                    Log.e("SPLASH", "LAUNCHING ACTIVITY FROM CLICK");

                    LaunchMainActivity(true);
                }
            });
            tos_root.setVisibility(View.VISIBLE);

        }

    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {

        this.finish();
    }
}
