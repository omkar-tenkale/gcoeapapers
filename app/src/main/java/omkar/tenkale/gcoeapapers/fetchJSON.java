package omkar.tenkale.gcoeapapers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;
import static omkar.tenkale.gcoeapapers.LinksManager.InitLinkUpdater;
import static omkar.tenkale.gcoeapapers.UtilityMethods.resolve2;

public class fetchJSON extends AsyncTask <Void, Void, Void>{
 String jsondata="";
 boolean mandetory = false;
 Context mcontext ;
 String apklink;

    public fetchJSON(Context context) {
        mcontext = context;
    }

@Override
    protected void onPreExecute() {

    new Handler().postDelayed(new Runnable() {


        @Override

        public void run() {
            Log.e("2SPLASH","JSON HANDLER EXECUTION STARTED ");
            SharedPreferences pref = mcontext.getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            if(pref.getBoolean("agreedTOS", false)){
                    try {
                        Log.e("2SPLASH","JSON TAKING TOO LONG , LAUNCHING MAINACTIVITY");
                        ((SplashScreen) mcontext).LaunchMainActivity(false);
                    }catch (Exception e){            Log.e("2SPLASH","2ND INSTANCE ERROR");
                    }}
        }

    },  3000);
    Log.e("2SPLASH","JSON HANDLER SET ");


}
    @Override
    protected Void doInBackground(Void... voids) {

        try{
            Log.e("SPLASH","ENTERED JSON TRY");
            URL url = new URL(resolve2((SplashScreen)mcontext,LinksManager.getCORE_FEED(), UtilityMethods.LINK.CORE_FEED));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            //try
          //   URL murl = new URL("https://filedldrhost.cu.ma/counter.php");
          //  URLConnection c = url.openConnection();
//c.connect();
            while(line != null){
                line = bufferedReader.readLine();
                jsondata = jsondata + line;

            }
            Log.e("SPLASH","ENTERED JSON TRY , DATA = "+jsondata);
            jsondata= UtilityMethods.getJSONstr(mcontext,resolve2((Activity) mcontext,LinksManager.getCORE_FEED(), UtilityMethods.LINK.CORE_FEED));
            JSONArray JA = new JSONArray(jsondata);
            Log.e("SPLASH","json 2");
            for(int i =0 ;i <JA.length(); i++) {
                JSONObject obj = (JSONObject) JA.get(i);
                Log.e("SPLASH","ENTERING IF BOOOLEAN CHECK  TYPE SSTRING ,  "+obj.getString("type"));
                if ((obj.get("type")).equals("update_check")) {

                    mandetory =Boolean.parseBoolean( obj.getString("mandatory"));
                    apklink = obj.getString("apklink");
                    Log.e("SPLASH","RESULT OF PARSEBOOLEON = "+Boolean.parseBoolean( obj.getString("mandatory")));
                }
            }
            InitLinkUpdater(mcontext);
        } catch(java.io.IOException | JSONException e){e.printStackTrace();}
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.e("SPLASH","IN POST EXECUTE , MANDETORY = "+mandetory);

        if(mandetory){
            try{ Log.e("2SPLASH", "JSON - MANDATORY \n");
                ((SplashScreen) mcontext).blockLaunch=true;
                ((SplashScreen)  mcontext).onMandetory(apklink);}catch(Exception ignored){}

                }else{
            Log.e("2SPLASH", "JSON - NOT MANDATORY \n");
            ((SplashScreen)  mcontext).checkTOS();
        }

    }


}
