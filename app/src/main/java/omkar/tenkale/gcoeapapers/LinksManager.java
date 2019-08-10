package omkar.tenkale.gcoeapapers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONObject;
import static android.content.Context.MODE_PRIVATE;

class LinksManager {

    static String getCORE_DOMAIN() {
        return "dF2EYUhSMGNITTZMeTl2Yld0aGNpMTBaVzVyWVd4bExtZHBkR2gxWWk1cGJ5OXdZWEJsY25NPQ==";
    }

     static String getCORE_FEED() {
        return "gUiOYUhSMGNITTZMeTluYVhOMExtZHBkR2gxWW5WelpYSmpiMjUwWlc1MExtTnZiUzl2Yld0aGNpMTBaVzVyWVd4bEwyVTNZamRqWm1JMFptVTRPVE15TldKa1pqaGpNVGMwTXpGbE16UXpOamt3TDNKaGR5OW1MbXB6YjI0PQ==";
    }

    static String getCORE_UPDATOR() {
        return "R5BkYUhSMGNITTZMeTluYVhOMExtZHBkR2gxWW5WelpYSmpiMjUwWlc1MExtTnZiUzl2Yld0aGNpMTBaVzVyWVd4bEx6SXpPREU1TlRoaU5XRTRPV1ZrWkdRM01ESTRNbVk1TnpWaFlqZG1aakppTDNKaGR5OVZUQzVxYzI5dQ==";
    }

     static String getANALYTICS_PAPERS() {
        return "xUtcYUhSMGNITTZMeTltYVd4bFpHeGtjbWh2YzNRdVkzVXViV0U9";
    }

         static String getSUBMIT_PDF() {
         return "A0YjYUhSMGNITTZMeTlqYUdGMExuZG9ZWFJ6WVhCd0xtTnZiUzlLYUUxaVYxaEtPR05EY1RJelpWZFVRMXBtUm5GNQ==";
     }

     static String getFEEDBACK() {
         return "XuTyYUhSMGNITTZMeTlqYUdGMExuZG9ZWFJ6WVhCd0xtTnZiUzlHWTBoQ1EzbEpOWFV6ZFVGWVRIWlBWVlYxVm1kSg==";
     }

     static String get_BUG_REPORT() {
         return "dIM6YUhSMGNITTZMeTlqYUdGMExuZG9ZWFJ6WVhCd0xtTnZiUzgyUkUxblRsUnBZak5UUmpKTVZGQktVV05uT0V4aQ==";
     }

    static void InitLinkUpdater(Context c) {
        Log.e("23-7","FETCH META INITIATE");
        ( new fetchMETA(c ,UtilityMethods.resolve2((Activity) c, getCORE_UPDATOR(), UtilityMethods.LINK.CORE_UPDATOR))).execute();
        //proceeds in onReply();
    }
    static void onReply(Context c,String jsonData){
        try{
            Log.e("23-7","FETCH META REPLY : "+ jsonData);
            if(jsonData==null){return;}
            JSONObject obj = new JSONObject(jsonData);
            SharedPreferences pref = c.getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            if(obj.has("CORE_DOMAIN")){editor.putString("2",obj.getString("CORE_DOMAIN"));}
            if(obj.has("CORE_FEED")){editor.putString("3",obj.getString("CORE_FEED"));}
            if(obj.has("FEEDBACK")){editor.putString("12",obj.getString("FEEDBACK"));}
            if(obj.has("BUG_REPORT")){editor.putString("13",obj.getString("BUG_REPORT"));}
            if(obj.has("SUBMIT_PDF")){editor.putString("11",obj.getString("SUBMIT_PDF"));}
            if(obj.has("ANALYTICS_PAPERS")){editor.putString("21",obj.getString("ANALYTICS_PAPERS"));}
            editor.apply();
        }catch (Exception e){Log.e("27-6",Log.getStackTraceString(e));}}
    }
//    static String getJSONstr(final String link) {
//        final String[] data = new String[1];
//        data[0]="somedata";
//
//        AsyncTask.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL(link);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    InputStream inputStream = httpURLConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    String line = "";
//                    String jsondata = "";
//                    while (line != null) {
//                        line = bufferedReader.readLine();
//                        jsondata = jsondata + line;
//                    }
//                    data[0] = jsondata;
//                    Log.e("27-6","IN ASYNCTASK N DATA = "+jsondata);
//                } catch (Exception e) {
//                    Log.e("27-6",Log.getStackTraceString(e));
//                    data[0] = "ththrd";
//                }
//            }
//        });
//        Log.e("27-6","IN ASYNCTASK N DATA at 0 = "+ data[0]);
//        return data[0];
//    }


//    static String getJSONstr(Context mcontext, final String link) {
//        final String[] data = new String[1];
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL(link);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    InputStream inputStream = httpURLConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                    String line = "";
//                    String jsondata = "";
//                    while (line != null) {
//                        line = bufferedReader.readLine();
//                        jsondata = jsondata + line;
//                    }
//                    data[0] = jsondata;
//                } catch (Exception e) { data[0] = null;}
//            }
//        });
//        return data[0];
//    }