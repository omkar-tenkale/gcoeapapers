package omkar.tenkale.gcoeapapers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import omkar.tenkale.gcoeapapers.R;

import static android.content.Context.MODE_PRIVATE;
import static omkar.tenkale.gcoeapapers.UtilityMethods.isAppInstalled;
import static omkar.tenkale.gcoeapapers.UtilityMethods.openInBrowser;
import static omkar.tenkale.gcoeapapers.UtilityMethods.openInWhatsApp;
import static omkar.tenkale.gcoeapapers.UtilityMethods.resolve2;

public class About extends Fragment {

    int tapcount = 0;
    View thisFragment;
    TextView me, madewithlove,versiontv;
    LinearLayout pdf, feedback, bug, permission, link, visit_root, licenses;
    ImageView shareApk, shareit, whatsapp, bt;
    Switch auto_update_btn;
    Handler h;
    Runnable rnbl;

    public About() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisFragment = inflater.inflate(R.layout.fragment_about, container, false);
        LinearLayout rootLayout = thisFragment.findViewById(R.id.about_fragment_root);
        rootLayout.setBackgroundColor(getResources().getColor(R.color.colorDarkest));
        h = new Handler();
        bug = thisFragment.findViewById(R.id.report_problem_root);
        feedback = thisFragment.findViewById(R.id.feedback_root);
        link = thisFragment.findViewById(R.id.share_link_root);
        pdf = thisFragment.findViewById(R.id.about_submit_pdf_root);
        permission = thisFragment.findViewById(R.id.allow_permission_root);
        licenses = thisFragment.findViewById(R.id.licenses_root);
        shareApk = thisFragment.findViewById(R.id.share_apk);
        shareit = thisFragment.findViewById(R.id.shareit_share);
        whatsapp = thisFragment.findViewById(R.id.whatsapp_share);
        bt = thisFragment.findViewById(R.id.bt_share);
        visit_root = thisFragment.findViewById(R.id.visit_root);
        auto_update_btn = thisFragment.findViewById(R.id.auto_update);
        madewithlove = thisFragment.findViewById(R.id.made_with_love);
        me = thisFragment.findViewById(R.id.me);
        versiontv=thisFragment.findViewById(R.id.about_version_tv);

        DBAdapter DBAdapter_instance = new DBAdapter(getContext());
        DBAdapter_instance.createDatabase();
        DBAdapter_instance.open();
        versiontv.setText("Version 1.0.2 ["+DBAdapter_instance.getVersion()+"]");
        DBAdapter_instance.close();
        rnbl = new Runnable() {
            @Override
            public void run() {
                tapcount = 0;
                me.setText("Designed,Developed and Maintained by OPUS");
            }
        };

        me.setOnClickListener(new View.OnClickListener() {
//            Vibrator vbr;

            @Override
            public void onClick(final View v) {
                    String url = "https://www.facebook.com/omkar.tenkale.5";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
            }
        });
        madewithlove.setText("Made with " + Html.fromHtml("<font color='red'>" + new String(Character.toChars(0x2764)) + "</font>") + " in GCOEA, For GCOEA", TextView.BufferType.SPANNABLE);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        auto_update_btn.setChecked(pref.getBoolean("auto_update", true));
        bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInWhatsApp(getContext(), resolve2(getActivity(), LinksManager.get_BUG_REPORT(), UtilityMethods.LINK.BUG_REPORT));
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openInWhatsApp(getContext(), resolve2(getActivity(), LinksManager.getFEEDBACK(), UtilityMethods.LINK.FEEDBACK));
            }
        });
        licenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = "Download GCOEA Papers App \n http://bit.ly/DownloadGCOEAPapersApp";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Download APp");
                i.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(i);
            }
        });
        auto_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Switch b = v.findViewById(R.id.auto_update);
                if (!b.isChecked()) {
                    b.setChecked(false);
                    editor.putBoolean("auto_update", false);
                    editor.apply();
                } else {
                    b.setChecked(true);
                    editor.putBoolean("auto_update", true);
                    editor.commit();
                }
            }
        });
        visit_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInBrowser(getContext(), "https://omkar-tenkale.github.io/papers/index.html");
            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInWhatsApp(getContext(), resolve2(getActivity(), LinksManager.getSUBMIT_PDF(), UtilityMethods.LINK.SUBMIT_PDF));

            }
        });
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        shareApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAPK(null, null);
            }
        });
        shareit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAPK("com.lenovo.anyshare.gps", "ShareIt");
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAPK("com.whatsapp", "WhatsApp");
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAPK("com.android.bluetooth", "Bluetooth");
            }
        });
        return thisFragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void shareAPK(String pkgname, String appname) {
        UtilityMethods.checkWritePermission(getContext(), true);
        ApplicationInfo app = getActivity().getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;
        String tempFile = String.valueOf(getActivity().getExternalCacheDir()) + "/GCOEA Papers 1.0.2.apk";
        if (!(new File(tempFile).exists())) {
            try {
                try (InputStream in = new FileInputStream(filePath)) {
                    try (OutputStream out = new FileOutputStream(tempFile)) {

                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);

                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
       // Toast.makeText(getContext(),tempFile,Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("*/*");
      //  intent.setData(Uri.fromFile(new File(tempFile)));
        if (pkgname != null) {
            if (isAppInstalled(getContext(), pkgname)) {
                intent.setPackage(pkgname);

            } else {
                Toast.makeText(getContext(), appname + " Not Installed ", Toast.LENGTH_SHORT).show();
                return;
            }
        }
//        intent.setType("application/*").setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_RECEIVER_REGISTERED_ONLY | Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
//        Uri uri;
//        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//            uri = Uri.fromFile(new File(tempFile));
//        } else {
//            uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, new File(tempFile));
//        }
       intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(tempFile)));
        //getActivity().grantUriPermission(getActivity().getPackageManager().toString(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_licences, null);
        view.loadUrl("file:///android_asset/open_source_licenses.html");
        AlertDialog mAlertDialog = new AlertDialog.Builder(getContext(), R.style.ThemeOverlay_AppCompat_Dark)
                .setTitle("Open Sources Licenses")
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout view = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.dialog_licences, null);
        WebView wview = view.findViewById(R.id.licences_webview);
        wview.setBackgroundColor(getResources().getColor(R.color.colorDarkest));
        wview.loadUrl("file:///android_asset/open_source_licences.html");
        TextView confirmbtn = view.findViewById(R.id.licences_ok);
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);

        /*
        TextView canclebtn =  dialog.findViewById(R.id.simple_dialog_cancel);
        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView confirmbtn =  dialog.findViewById(R.id.simple_dialog_delete);
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = view.findViewById(R.id.item_downloaded_list_path);
                File file = new File(tv.getText().toString());
                if(file.delete())
                {
                    Toast.makeText(mContext,"File Deleted",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    refreshMe();

                }
                else
                {
                    dialog.dismiss();
                    Toast.makeText(mContext,"Failed to Delete the File. Check App Permissions.",Toast.LENGTH_LONG).show();
                }

            }
        });

        */
        dialog.show();
    }

    interface AboutFragmentInterface {
        void newDownloadFromFragment(DownloadItem item);
    }
}
