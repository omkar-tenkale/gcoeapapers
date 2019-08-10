package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import omkar.tenkale.gcoeapapers.R;

import static android.content.Context.MODE_PRIVATE;
import static omkar.tenkale.gcoeapapers.LinksManager.InitLinkUpdater;
import static omkar.tenkale.gcoeapapers.UtilityMethods.getExtrasPath;
import static omkar.tenkale.gcoeapapers.UtilityMethods.openInBrowser;
import static omkar.tenkale.gcoeapapers.UtilityMethods.openPDF;

public class Feed extends Fragment {
    View thisFragment;
    String jsonString;
    LayoutInflater inflater;
    LinearLayout root;
    private FeedFragmentInterface thisInterface;

    public Feed() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisInterface = (FeedFragmentInterface) getActivity();
        thisFragment = inflater.inflate(R.layout.fragment_feed, container, false);
        root = thisFragment.findViewById(R.id.feed_root_layout);
        Button b = thisFragment.findViewById(R.id.feed_refresh_button);
        LinearLayout l = thisFragment.findViewById(R.id.notifiationtvroot);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    UtilityMethods.checkWritePermission(getContext(), true);
                    if (UtilityMethods.isNetworkAvailable(getContext())) {
                        // Toast.makeText(getContext(),"Refreshing",Toast.LENGTH_SHORT).show();
                        v.findViewById(R.id.feed_refresh_icon).startAnimation(UtilityMethods.getRotateAnimation(getContext()));
                        downloadFeed();
                    } else {
                        Toast.makeText(getContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ViewCompat.animate(v)
                        .setDuration(200)
                        .scaleX(0.97f)
                        .scaleY(0.97f)
                        .setInterpolator(new CycleInterpolator(0.5f))
                        .setListener(new ViewPropertyAnimatorListener() {
                            @Override
                            public void onAnimationStart(final View view) {
                            }

                            @Override
                            public void onAnimationEnd(final View view) {

                                UtilityMethods.checkWritePermission(getContext(), false);
                                if(UtilityMethods.isNetworkAvailable(getContext())){
                                    Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_SHORT).show();
                                }
                                refreshFeed();

                            }

                            @Override
                            public void onAnimationCancel(final View view) {
                            }
                        })
                        .withLayer()
                        .start();
            }
        });
        //  rootLayout.setBackgroundColor(getResources().getColor(R.color.colorDark));
        //setfont  Fonty.setFonts((ViewGroup) view);
        //  ListView listView = (ListView) view.findViewById(R.id.mobile_list);
        // ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_list_item, mobileArray);
        //  listView.setAdapter(adapter);
        //  root = (ScrollView)thisFragment.findViewById(R.id.feed_root_layout) ;
        refreshFeed();
        return thisFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void refreshFeed() {

        if (!(UtilityMethods.isNetworkAvailable(getActivity()))) {
            Toast.makeText(getContext(), "Couldn't refresh  Feed!\nCheck Internet Connection", Toast.LENGTH_SHORT).show();
            updateFeed();
        } else {
           // Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_SHORT).show();
            if (!updateFeed()) {
                InitLinkUpdater(getContext());
                downloadFeed();
            }
        }

    }

    public String readExistingFeedFile() {
        String json;
        try {
            File feedfile = new File(getContext().getApplicationInfo().dataDir + "/.feed");
            InputStream is = new FileInputStream(feedfile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return json;

    }

    public void downloadFeed() {
//"https://drive.google.com/uc?export=download&id=1u4cLOB_q1tuUVfwFn8w3FoBX6AMOuuit"
       // String link = "https://gist.githubusercontent.com/omkar-tenkale/e7b7cfb4fe89325bdf8c17431e343690/raw/f.json";
        //UtilityMethods.resolve(getActivity(),3)
        int id = PRDownloader.download(UtilityMethods.resolve2(getActivity(),LinksManager.getCORE_FEED(), UtilityMethods.LINK.CORE_FEED), getContext().getApplicationInfo().dataDir + "/", ".feed")
                .build().start(
                        new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                try {
                                    root.removeAllViews();
                                    if (!updateFeed()) {
                                        Toast.makeText(getContext(), "Error Occured while Updating Feed", Toast.LENGTH_SHORT).show();
                                        InitLinkUpdater(getContext());
                                    }
                                    //  {Toast.makeText(getContext(),"Feed Updated",Toast.LENGTH_SHORT).show();}
                                } catch (Exception ignored) {}
                            }

                            @Override
                            public void onError(Error error) {
                            }
                        }
                );
    }

    public boolean updateFeed() {
        Log.e("FEED2", "INSIDE UPDATEFEED() ");

        jsonString = readExistingFeedFile();
        if (jsonString == null) {
            return false;
        }
        root.removeAllViews();
        inflater = LayoutInflater.from(getActivity());
        root = thisFragment.findViewById(R.id.feed_root_layout);
        try {
            JSONArray JA = new JSONArray(jsonString);
            for (int i = 0; i < JA.length(); i++) {
                final JSONObject obj = (JSONObject) JA.get(i);
                if ((obj.get("type")).equals("update_check")) {
                    setupdateitem(obj);
                } else if ((obj.get("type")).equals("download_item")) {
                    setupDownloadItem(obj);
                } else if ((obj.get("type")).equals("feed_item")) {
                    View view = inflater.inflate(R.layout.item_feed_simple, root, false);
                    if(obj.has("link")){
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ViewCompat.animate(v).setDuration(200).scaleX(0.97f).scaleY(0.97f).setInterpolator(new CycleInterpolator(0.5f)).setListener(new ViewPropertyAnimatorListener() {
                                    @Override
                                    public void onAnimationStart(final View view) {
                                    }

                                    @Override
                                    public void onAnimationEnd(final View view) {
                                        try{ openInBrowser(getContext(),obj.getString("link"));}catch (Exception e){
                                            Toast.makeText(getContext(),"Some Error Occured !",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onAnimationCancel(final View view) {
                                    }
                                })
                                        .withLayer()
                                        .start();
                            }
                        });
                    }
                    TextView titletv = view.findViewById(R.id.item_feed_simple_head);
                    titletv.setText(obj.getString("title"));
                    Log.e("FEED", "FEED ITEM TITLE = " + obj.getString("title"));
                    TextView descriptiontv = view.findViewById(R.id.item_feed_simple_detail);
                    descriptiontv.setText(obj.getString("description"));
                    Log.e("FEED", "FEED ITEM DESCRIPTION = " + obj.getString("description"));
                    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_fade);
                    view.startAnimation(animation);
                    root.addView(view);
                }
            }
            // addRefreshBtn(root);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    public void setupMETA(JSONObject obj) {
//        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        for (int i = 1; i < 6; i++) {
//            String s;
//            if ((s = getString4meta(obj, i)) != null) {
//                editor.putString(Integer.toString(i), s);
//            }
//        }
//        editor.apply();
//
//        // editor.putString("1",getString4meta(obj,1));
//        //  editor.putString("2",getString4meta(obj,2));
//        //  editor.putString("3",getString4meta(obj,3));
//        //  editor.putString("4",getString4meta(obj,4));
//        //  editor.putString("5",getString4meta(obj,5));
//    }

    public String getString4meta(JSONObject obj, int i) {
        try {
            return obj.getString(Integer.toString(i));
        } catch (Exception e) {
            return null;
        }
    }

    public void setupDownloadItem(final JSONObject obj) {
        try {
            View view = inflater.inflate(R.layout.item_feed_download_item, root, false);
            TextView titletv = view.findViewById(R.id.item_feed_download_head);
            titletv.setText(obj.getString("title"));
            TextView descriptiontv = view.findViewById(R.id.item_feed_download_detail);
            descriptiontv.setText(obj.getString("description"));
            final TextView linktv = view.findViewById(R.id.item_feed_download_link);
            linktv.setText(obj.getString("link"));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewCompat.animate(v).setDuration(200).scaleX(0.97f).scaleY(0.97f).setInterpolator(new CycleInterpolator(0.5f)).setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(final View view) {
                        }

                        @Override
                        public void onAnimationEnd(final View view) {
                            try {
                                File f =new File(UtilityMethods.getExtrasPath(getContext())+"/"+obj.getString("FNAME"));
                                if(f.exists()){openPDF(getContext(),f);return;}
                                ((MainActivity) getActivity()).addDownload(new DownloadItem().setDNAME(obj.getString("DNAME")).setTYPE(DownloadItem.TYPE.PDF).setFNAME(obj.getString("FNAME")).setTYPE(DownloadItem.TYPE.PDF).setLINK(linktv.getText().toString()).setPATH(getExtrasPath(getContext())));
                            } catch (Exception e) {
                                Toast.makeText(getContext(), "Some Error Occured !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onAnimationCancel(final View view) {
                        }
                    })
                            .withLayer()
                            .start();
                }
            });
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_fade);
            view.startAnimation(animation);
            root.addView(view);
        } catch (Exception ignored) {
        }

    }

    public void setupdateitem(JSONObject obj) {
        int vcode;
        //View view = inflater.inflate(R.layout.item_downloading_list, null, false);
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            vcode = pInfo.versionCode;
            Log.e("FEED", "VCODE = " + vcode + " jsoncode = " + Integer.parseInt((String) obj.get("code")));
            DBAdapter DBAdapter_instance = new DBAdapter(getContext());
            DBAdapter_instance.createDatabase();
            DBAdapter_instance.open();
            if (DBAdapter_instance.getVersion() < Double.parseDouble((String) obj.get("dbcode"))) {
                Toast.makeText(getContext(), "Updating DataBase v" + DBAdapter_instance.getVersion() + " > v" + Double.parseDouble((String) obj.get("dbcode")), Toast.LENGTH_LONG).show();
                UtilityMethods.initiateDBUpdate(getContext(), obj.getString("dblink"));

            }
            DBAdapter_instance.close();
            if (Integer.parseInt((String) obj.get("code")) > vcode) {

                Log.e("FEED", "ENTERED IF BLOCK");
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("code", vcode);
                editor.apply();

                if (Boolean.parseBoolean((String) obj.get("mandatory"))) {
                    editor.putBoolean("mandetory", true);
                    editor.putString("apklink", obj.getString("apklink"));
                    editor.commit();
                }

                View v = inflater.inflate(R.layout.item_feed_update, root, false);
                TextView versionlinetv = v.findViewById(R.id.item_update_available_version_line);
                versionlinetv.setText("v" + pInfo.versionName + " > " + "v" + obj.get("name"));
                TextView detailstv = v.findViewById(R.id.item_update_available_description);
                detailstv.setText((String) obj.get("description"));
                TextView linktv = v.findViewById(R.id.item_update_available_link);
                linktv.setText((String) obj.get("apklink"));
                LinearLayout updateitemroot = v.findViewById(R.id.item_update_available_root);
                updateitemroot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!UtilityMethods.tryAPKInstall(getActivity())) {
                            TextView linktv = v.findViewById(R.id.item_update_available_link);
                            thisInterface.newDownloadFromFragment(new DownloadItem().setTYPE(DownloadItem.TYPE.UPDATE_APK).setLINK((String) linktv.getText()));


/*
                                        Log.e("3FEED","CREATING NEW DOWNLOAD");

                                        Toast.makeText(getContext(),"Downloading Latest APK. Please try again after some time",Toast.LENGTH_SHORT).show();

                                        PRDownloader.download((String) linktv.getText(),Environment.getExternalStorageDirectory() + "/GCOEA App/Papers/","update.apk").build()
                                            .start(
                                                    new OnDownloadListener() {
                                                        @Override
                                                        public void onDownloadComplete() {

                                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                AlertDialog alertDialog = builder.create();
                                                                alertDialog.setCanceledOnTouchOutside(true);
                                                                alertDialog.setTitle("Update Downloaded");
                                                                alertDialog.setMessage("Successfully Downloaded Latest Version.Tap to Install");
                                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "INSTALL",

                                                                        new DialogInterface.OnClickListener() {
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                                                intent.setDataAndType(Uri.fromFile(new File(apk_path)), "application/vnd.android.package-archive");
                                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                startActivity(intent);
                                                                            } });
                                                                alertDialog.show();

                                                        }
                                                        @Override
                                                        public void onError(Error error) {
                                                            try{
                                                             Toast.makeText(getContext(),"Error Occured while Downloading Update",Toast.LENGTH_SHORT).show();

                                                        }catch (Exception e){}}
                                                    }
                                            );


                               */
                        }
                    }
                });
                root.addView(v);
                if (pref.getBoolean("auto_update", true)) {

                    PRDownloader.download((String) linktv.getText(), Environment.getExternalStorageDirectory() + "/GCOEA App/", "update.apk")
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
                }
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animation_fade);
                v.startAnimation(animation);
                Log.e("FEED", "UPDATE AVAILABLE VIEW ADDED");

            }
            Log.e("FEED", "EXIT TRY CATCH");
        } catch (Exception e) {
            Log.e("FEED", "EXCEPTION", e);
        }
    }

    private void addRefreshBtn(LinearLayout root) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_refresh_button, root, false);
        TextView btn = view.findViewById(R.id.refresh_button_main);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.animate(v).setDuration(200).scaleX(0.97f).scaleY(0.97f).setInterpolator(new CycleInterpolator(0.5f)).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(final View view) {
                    }

                    @Override
                    public void onAnimationEnd(final View view) {

                        UtilityMethods.checkWritePermission(getContext(), false);
                        if (UtilityMethods.isNetworkAvailable(getContext())) {
                            Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_SHORT).show();
                            downloadFeed();
                        } else {
                            Toast.makeText(getContext(), "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onAnimationCancel(final View view) {
                    }
                })
                        .withLayer()
                        .start();
            }
        });
        root.addView(view);

    }

    interface FeedFragmentInterface {
        void newDownloadFromFragment(DownloadItem item);
    }
}
