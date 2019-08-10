package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import omkar.tenkale.gcoeapapers.R;

import static android.content.Context.MODE_PRIVATE;

public class Download extends Fragment {
    View thisFragment;
    ListView DownloadingListView;
    DownloadListAdapter downloadingadapter;
    ArrayList<String> DownloadInitializerStrings;
    ArrayList<DownloadItem> DownloadInitializerObjects;
    Context mainActivityContext;
    ProgressBar progress_bar;

    public Download() {
        // Required empty public constructor
    }

    public static Download newInstance(String param1, String param2) {
        Download fragment = new Download();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  DownloadInitializerStrings = new ArrayList<>();
        DownloadInitializerObjects = new ArrayList<>();

        // DownloadingList = new ArrayList();
        thisFragment = inflater.inflate(R.layout.fragment_download, container, false);
        //set root bg color
        // List<String> course_list = Arrays.asList(getResources().getStringArray(R.array.course));
        //String[] bankNames= (String[]) course_list.toArray();

        LinearLayout rootLayout = thisFragment.findViewById(R.id.download_fragment_root);
        rootLayout.setBackgroundColor(getResources().getColor(R.color.colorDarkest));

        // Spinner course_spinner = (Spinner) thisFragment.findViewById(R.id.course_spinner);

        // Spinner semester_spinner = (Spinner) thisFragment.findViewById(R.id.semester_spinner);
        // ArrayAdapter aa = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,bankNames);
        // aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //course_spinner.setAdapter(aa);
        //***************************************************************TO DO ADD LOCAL FILES DISPLAY HERE
        DownloadingListView = thisFragment.findViewById(R.id.downloading_list_view);
        downloadingadapter = new DownloadListAdapter(getActivity(), Download.this, DownloadInitializerObjects);
        DownloadingListView.setAdapter(downloadingadapter);
        DownloadingListView.setDividerHeight(0);
        //  LocalPapersListView = thisFragment.findViewById(R.id.downloaded_list_view);
        //setadapter


        return thisFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityContext = context;
        boolean b = context == null;
        Log.e("ONATTACH", "ONATTACH INVOKED , CONTEXT NULL ? " + b);
        b = mainActivityContext == null;
        Log.e("ONATTACH", "ONATTACH INVOKED , mainActivityContext NULL ? " + b);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

/*
    public void passItOn(String COmpoundSubject) {

        //  boolean b =ifExists(getDownloadFileName(COmpoundSubject.substring(0,6),COmpoundSubject.substring(6)),new File(Environment.getExternalStorageDirectory()+"/GCOEA App/Papers"));
        if (ifExists(getDownloadFileName(COmpoundSubject.substring(0, 6), COmpoundSubject.substring(6)), new File(Environment.getExternalStorageDirectory() + "/GCOEA App/Papers"))) {

            Toast.makeText(getContext(), "Alraedy Exists", Toast.LENGTH_LONG).show();

        } else {
            DownloadItem item = new DownloadItem(getContext());
            item.setEXTRA(COmpoundSubject);
            ((MainActivity) mainActivityContext).forwardNewDownload(item);
            if(1==1){return;}
            //temp return
            DownloadInitializerStrings.add(COmpoundSubject);
            DownloadingListView.invalidate();
            downloadingadapter.notifyDataSetChanged();
            DownloadingListView.setSelection(DownloadingListView.getCount() - 1);
            ((MainActivity) mainActivityContext).mainViewPager.setCurrentItem(2);
        }
        //  new_download_course_code = CompoundItem.substring(0,6);
        //  new_download_course_code = COmpoundSubject.substring(0,6);
        //  int item_length=CompoundItem.length();

        //Log.e("MYMESSAGE","ITEM DOWNLOADCOMPOUND ITEM LENGTH IS "+ item_length);
        //  new_download_exam_type = COmpoundSubject.substring(6);
        //TextView tv = thisFragment.findViewById(R.id.download_fragment_textview);
        // tv.setText(COmpoundSubject);
        //  Log.e("MYMESSAGEEE"," WRITE PERMISSION GRANTED ?  "+ checkWritePermission());
        //int download_id =  initiateDownload(new_download_course_code,new_download_exam_type);
        // DownloadingList.add(download_id,0);
        // DownloadInitializerStrings.add(COmpoundSubject);
       }

*/

    private String getDownloadFileName(String course_code, String exam_type) {
        String downloadFileName = course_code + " " + exam_type + ".pdf";
        String query = "SELECT * FROM " + "PAPERS " + "WHERE " + "course_code = '" + course_code + "' AND "
                + " exam_type =  '" + exam_type + "'";
        // SQLiteDatabase db = getWritableDatabase();
        Log.e("mymessage", "inside getdownloadfilename ");
        boolean b = getActivity() == null;
        Log.e("XXXXX", "IS CONTEXT NULL DOWNLOAD JAVA : " + b);
        b = (mainActivityContext == null);
        Log.e("XXXXX", "IS mainActivityContext  NULL  ? DOWNLOAD JAVA : " + b);
        DBAdapter DBAdapter_instance = new DBAdapter(mainActivityContext);
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

    public boolean ifExists(String name, File file) {
        boolean found = false;
        File[] list = file.listFiles();
        if (list != null) {
            for (File fil : list) {

                if (name.equalsIgnoreCase(fil.getName())) {
                    found = true;
                }
            }
        }
        return found;
    }

    public void OnDownloadAdded(DownloadItem item) {
        if (!UtilityMethods.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        if( pref.getBoolean("mandetory",false)){
            Toast.makeText(getContext(), "Error Occured.Please Update the App", Toast.LENGTH_SHORT).show();
            return;
        }
//        String s =item.getEXTRA_COURSE_CODE()+item.getEXTRA_EXAMTYPE();
//        File f = new File(Environment.getExternalStorageDirectory() + "/GCOEA App/Papers");
//        String name =UtilityMethods.getFNameByCodenType(getContext(),s.substring(0, 6), s.substring(6));  //getDownloadFileName(s.substring(0, 6), s.substring(6));

        if ((new File(UtilityMethods.getPath(getContext()) + "/" + item.getFNAME() + ".temp")).exists() || (new File(UtilityMethods.getExtrasPath(getContext()) + "/" + item.getFNAME() + ".temp")).exists()) {
            Toast.makeText(getContext(), "Already Downloading", Toast.LENGTH_SHORT).show();
            downloadingadapter.notifyDataSetChanged();
        } else {
//            if ((new File(UtilityMethods.getPath(getContext()) + "/" + item.getFNAME())).exists() || (new File(UtilityMethods.getExtrasPath(getContext()) + "/" + item.getFNAME())).exists()) {
//                Toast.makeText(getContext(), "Downloading Again", Toast.LENGTH_SHORT).show();
//                downloadingadapter.notifyDataSetChanged();
//            }
            DownloadInitializerObjects.add(item);
            // DownloadInitializerStrings.add(s);
            //DownloadingListView.invalidate();
            downloadingadapter.notifyDataSetChanged();
            DownloadingListView.setSelection(DownloadingListView.getCount() - 1);
            ((MainActivity) mainActivityContext).mainViewPager.setCurrentItem(2);
        }
    }

    interface DownloadFragmentInterface {
        void newDownloadFromFragment(DownloadItem item);
    }
}
