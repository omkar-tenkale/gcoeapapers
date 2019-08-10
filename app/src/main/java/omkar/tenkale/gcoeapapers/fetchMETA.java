package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class fetchMETA extends AsyncTask <Void, Void, Void>{
 String data ;
 String link;
    Context c;
    public fetchMETA(Context c,String link) {
        this.c=c;
        this.link=link;
    }

@Override
    protected void onPreExecute() {}
    @Override
    protected Void doInBackground(Void... voids) {
        try{
            URL url = new URL(link);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String jsondata = "";
            while (line != null) {
                line = bufferedReader.readLine();
                jsondata = jsondata + line;
            }
           data = jsondata;
            Log.e("27-6","IN ASYNCTASK N DATA = "+jsondata);
        }catch (Exception ignored){}
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        LinksManager.onReply(c,data);
    }
}
