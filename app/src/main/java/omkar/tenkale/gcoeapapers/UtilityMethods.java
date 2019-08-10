package omkar.tenkale.gcoeapapers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.getExternalStorageDirectory;

public class UtilityMethods {

    static boolean DBUPDATEINPROGRESS=false;

    static String resolve2(Activity a, String def, LINK l) {
        try {
            String resolved = null;
            SharedPreferences pref = a.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            switch (l) {
                case CORE_UPDATOR:
                    resolved = pref.getString("1", def);
                    break;
                case CORE_DOMAIN:
                    resolved = pref.getString("2", def);
                    break;
                case CORE_FEED:
                    resolved = pref.getString("3", def);
                    break;
                case SUBMIT_PDF:
                    resolved = pref.getString("11", def);
                    break;
                case FEEDBACK:
                    resolved = pref.getString("12", def);
                    break;
                case BUG_REPORT:
                    resolved = pref.getString("13", def);
                    break;
                case ANALYTICS_PAPERS:
                    resolved = pref.getString("21", def);
                    break;
            }
            return new String(Base64.decode(new String(Base64.decode(resolved.substring(4), Base64.DEFAULT), "UTF-8"), Base64.DEFAULT), "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static boolean checkWritePermission(Context context, boolean showToast) {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        int result = context.checkCallingOrSelfPermission(WRITE_EXTERNAL_STORAGE);
        boolean b = result == PackageManager.PERMISSION_GRANTED;
        if (!b && showToast) {
            Toast.makeText(context, "Please Allow Storage Permission", Toast.LENGTH_SHORT).show();
        }
        return b;
    }


    static void openPDF(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Log.e("22-6","file in open pdf = "+file.toString());
        intent.setDataAndType(Uri.fromFile(file),"application/pdf");
      //  intent.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file), "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PackageManager packageManager = context.getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Please Install an app to open PDF Files", Toast.LENGTH_LONG).show();
        }
    }

    static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    static String getFNameByCodenType(Context mContext, String course_code, String exam_type) {
        String downloadFileName = course_code + " " + exam_type + ".pdf";
        String query = "SELECT * FROM " + "PAPERS " + "WHERE " + "course_code = '" + course_code + "' AND " + " exam_type =  '" + exam_type + "'";
        Log.e("mymessage", "inside getdownloadfilename ");
        DBAdapter DBAdapter_instance = new DBAdapter(mContext);
        DBAdapter_instance.createDatabase();
        DBAdapter_instance.open();
        Cursor returned_cursor = DBAdapter_instance.getTestData(query);
        Log.w("DBBUGHUNT", "INSIDE DOWNLOAD.JAVA AFTER CURSOR CREATED AND CURSOR LENGTH : " + returned_cursor.getCount() + "where query was : " + query);
        if (returned_cursor.moveToFirst()) {
            while (!returned_cursor.isAfterLast()) {
                Log.w("DBBUGHUNT", "INSIDE DOWNLOAD.JAVA AFTER CURSOR CREATED INSIDE   if (returned_cursor.moveToFirst()) ");
                downloadFileName = returned_cursor.getString(returned_cursor.getColumnIndex("SUBJECT__NAME")) + " " + exam_type + " " + course_code + ".pdf";
                returned_cursor.moveToNext();
            }
        }
        DBAdapter_instance.close();
        Log.e("ADAPTERBUGHUNT", "EXITING FROM GETDOWNLOADFILENAME WHERE NAME IS  : " + downloadFileName);

        return downloadFileName;

    }

    static String getDNameByCodenType(Context mContext, String course_code, String exam_type) {
        String downloadFileName = course_code + " " + exam_type + ".pdf";
        String query = "SELECT * FROM " + "PAPERS " + "WHERE " + "course_code = '" + course_code + "' AND " + " exam_type =  '" + exam_type + "'";
        Log.e("mymessage", "inside getdownloadfilename ");
        DBAdapter DBAdapter_instance = new DBAdapter(mContext);
        DBAdapter_instance.createDatabase();
        DBAdapter_instance.open();
        Cursor returned_cursor = DBAdapter_instance.getTestData(query);
        Log.w("DBBUGHUNT", "INSIDE DOWNLOAD.JAVA AFTER CURSOR CREATED AND CURSOR LENGTH : " + returned_cursor.getCount() + "where query was : " + query);
        if (returned_cursor.moveToFirst()) {
            while (!returned_cursor.isAfterLast()) {
                Log.w("DBBUGHUNT", "INSIDE DOWNLOAD.JAVA AFTER CURSOR CREATED INSIDE   if (returned_cursor.moveToFirst()) ");
                downloadFileName = returned_cursor.getString(returned_cursor.getColumnIndex("SUBJECT__NAME")) + " " + exam_type;
                returned_cursor.moveToNext();
            }
        }
        DBAdapter_instance.close();

        return downloadFileName;

    }

    static String getPath(Context context) {
        String path = getExternalStorageDirectory() + "/GCOEA App/Papers";
        File f = new File(path);
        checkWritePermission(context, true);
        if (!f.exists()) {
            f.mkdir();
        }
        return path;
    }
    static String getExtrasPath(Context context) {
        String path = getExternalStorageDirectory() + "/GCOEA App/Extras";
        File f = new File(path);
        checkWritePermission(context, true);
        if (!f.exists()) {
            f.mkdir();
        }
        return path;
    }

    static void initiateDBUpdate(final Context context, String link) {

        DBUPDATEINPROGRESS = true ;
        PRDownloader.download(link, context.getApplicationInfo().dataDir + "/databases/", "core.db")
                .build().start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                try {
                    Toast.makeText(context, " Database Updated !", Toast.LENGTH_LONG).show();
                    DBUPDATEINPROGRESS = false ;
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(context, " Database Update Error ", Toast.LENGTH_LONG).show();
                DBUPDATEINPROGRESS = false ;
            }
        });
    }

    static Animation getRotateAnimation(Context context) {
        Animation animation = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(0);
        animation.setInterpolator(context, android.R.anim.decelerate_interpolator);
        animation.setDuration(1300);
        return animation;
    }

    static void clearTempFiles(Context context) {
        new File(String.valueOf(context.getExternalCacheDir())).delete();
       // new File(String.valueOf(context.getExternalCacheDir()) + "/GCOEA Papers 1.0.0.apk").delete();
        String directory = UtilityMethods.getPath(context);
        File dir = new File(directory);
        try {
            int i = dir.listFiles().length;
            if ((i > 0)) {
                for (File file : dir.listFiles()) {
                    if (file.getName().endsWith((".temp"))) {
                        file.delete();
                    }
                }
            }

        } catch (Exception ignored) {
        }

    }

    static void openInBrowser(Context context, String link) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        context.startActivity(i);

    }

    static boolean tryAPKInstall(Activity a) {
        String apk_path = Environment.getExternalStorageDirectory() + "/GCOEA App/update.apk";
        File f = new File(apk_path);
        if (f.exists()) {
            int currentcode = 0, apkcode = 0;

            try {
                PackageInfo pInfo = a.getApplication().getPackageManager().getPackageInfo(a.getPackageName(), 0);
                currentcode = pInfo.versionCode;
                pInfo = a.getApplication().getPackageManager().getPackageArchiveInfo(apk_path, 0);
                apkcode = pInfo.versionCode;
            } catch (Exception e) {

                e.printStackTrace();
            }
            if (apkcode > currentcode) {
                Intent intent = new Intent(Intent.ACTION_VIEW);


                Uri uri = Uri.fromFile(new File(apk_path));
                // if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                //     uri = Uri.fromFile(new File(apk_path));
                // } else {
                //     uri = FileProvider.getUriForFile(a, BuildConfig.APPLICATION_ID, new File(apk_path));
                // }


                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                a.startActivity(intent);
                return true;
            } else {
                Log.e("MANDATORY2", "ROGUE APK DELETED");
                f.delete();
                return false;
            }
        }
        Log.e("MANDATORY2", "FILE DOESNT EXIST");

        return false;
    }

    static void disableURIcheck() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void openInWhatsApp(Context c, String link) {
        if (isAppInstalled(c, "com.whatsapp")) {
            Uri uri = Uri.parse(link);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.setPackage("com.whatsapp");
            c.startActivity(i);  //Intent.createChooser(i, "")
        } else {
            //Toast.makeText(c, "Whatsapp Not Installed ", Toast.LENGTH_SHORT).show();
            openInBrowser(c, link);
        }
    }

    static String getJSONstr(Context mcontext,String link){
        try{

            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String jsondata = "";
            while(line != null){
                line = bufferedReader.readLine();
                jsondata = jsondata + line;
                }
                return jsondata;
        }catch(Exception e){return null;}
    }

    enum LINK {
        CORE_DOMAIN,
        CORE_FEED,
        CORE_UPDATOR,
        ANALYTICS_PAPERS,
        SUBMIT_PDF,
        FEEDBACK,
        BUG_REPORT
    }


}
