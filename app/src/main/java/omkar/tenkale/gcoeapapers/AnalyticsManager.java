package omkar.tenkale.gcoeapapers;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class AnalyticsManager {
    public static void OnInstall(Context mContext,int currentcode) {
        Log.e("#","on install code > "+ currentcode);
        PackageManager myapp= mContext.getPackageManager();
        String installer = myapp.getInstallerPackageName(mContext.getPackageName());
       proceedviaDownload(mContext,UtilityMethods.resolve2((Activity)mContext,LinksManager.getANALYTICS_PAPERS(), UtilityMethods.LINK.ANALYTICS_PAPERS)+"/analytics.php?type=oninstall&version="+currentcode+"&installer="+installer);
      //  proceedviaDownload(mContext,"https://filedldrhost.cu.ma/analytics.php?type=oninstall&version="+currentcode+"&installer="+installer);
    }

    public static void OnUpdate(Context mContext,int oldVersion,int newVersion) {
        PackageManager myapp= mContext.getPackageManager();
        String installer = myapp.getInstallerPackageName(mContext.getPackageName());
        proceedviaDownload( mContext,UtilityMethods.resolve2((Activity)mContext,LinksManager.getANALYTICS_PAPERS(), UtilityMethods.LINK.ANALYTICS_PAPERS)+"/analytics.php?type=onupdate&oldversion="+oldVersion+"&newversion="+newVersion+"&installer="+installer);
    }

    public static void OnNativePaperDownload(Context mContext, String CompoundSubject) {
//        proceedviaDownload(mContext,"https://filedldrhost.cu.ma/counter.php");
//        try{  URL url = new URL("https://filedldrhost.cu.ma/counter.php");
//            URLConnection cn = url.openConnection();
//            cn.setDoOutput(true);
//            HttpURLConnection http = (HttpURLConnection)cn;
//            http.setRequestMethod("GET");// PUT is another valid option
//            Toast.makeText(mContext,"make conn", Toast.LENGTH_SHORT).show();
//
//
//        }catch (Exception e){  Log.e("#","errorgrsg in analytics "+Log.getStackTraceString(e));}

     //  proceed("https://filedldrhost.cu.ma/counter.php");
        proceedviaDownload(mContext,UtilityMethods.resolve2((Activity)mContext,LinksManager.getANALYTICS_PAPERS(), UtilityMethods.LINK.ANALYTICS_PAPERS)+"/analytics.php?type=paper_download&item="+CompoundSubject);
        //  proceed("https://filedldrhost.cu.ma/analytics.php?type=oninstall&version=2");
        //  proceed(mContext,UtilityMethods.resolve2((MainActivity)mContext,LinksManager.getANALYTICS_PAPERS(), UtilityMethods.LINK.ANALYTICS_PAPERS) + "?item=" + CompoundSubject);
      //  proceed(mContext,UtilityMethods.resolve2((MainActivity)mContext,LinksManager.getANALYTICS_PAPERS_TOTAL_HITS(), UtilityMethods.LINK.ANALYTICS_PAPERS_TOTAL_HITS));
    }
private static void proceed(final String link){
    Log.e("#","ANALYTICS LINK> "+ link);
    AsyncTask.execute(new Runnable() {
        @Override
        public void run() {
            try{

                //                String rawData = "id=10";
//                String type = "application/x-www-form-urlencoded";
//                String encodedData = URLEncoder.encode( rawData, "UTF-8" );
//                URL u = new URL("http://www.example.com/page.php");
//                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//            //    conn.setRequestMethod("POST");
//                conn.setRequestProperty( "Content-Type", type );
//                conn.setRequestProperty( "Content-Length", String.valueOf(encodedData.length()));
//                OutputStream os = conn.getOutputStream()
//                os.write(encodedData.getBytes());
//


               // String  link2 = URLEncoder.encode( link, "UTF-8" );
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                HttpURLConnection http = (HttpURLConnection)conn;
                http.setRequestMethod("POST");// PUT is another valid option
                http.setDoOutput(true);
                http.connect();
                Log.e("#","executed");

            }catch (Exception e){        Log.e("#","error in analytics "+Log.getStackTraceString(e));
            }

        }
    });

}
    private static void proceedviaDownload(final Context mContext, String link) {

        PRDownloader.download(link, mContext.getApplicationInfo().dataDir + "/", ".al").build().start(
                new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        new File(String.valueOf(mContext.getApplicationInfo().dataDir + "/.al")).delete();
                    }

                    @Override
                    public void onError(Error error) {
                        new File(String.valueOf(mContext.getApplicationInfo().dataDir + "/.al")).delete();
                    }
                });
    }

}

