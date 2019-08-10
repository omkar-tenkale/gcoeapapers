package omkar.tenkale.gcoeapapers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import omkar.tenkale.gcoeapapers.R;

public class Available extends Fragment  {

    View thisFragment;
    ListView downloaded_papers_listview;
    ListView extras_listview;
    public ArrayList<String> downloaded_files;
    DownloadedPapersAdapter papers_adapter;
    DynamicDownloadedAdapter extras_adapter;
    RelativeLayout availabletvroot;


    public Available() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisFragment = inflater.inflate(R.layout.fragment_available, container, false);
        LinearLayout rootLayout =  thisFragment.findViewById(R.id.settings_fragment_root);
        rootLayout.setBackgroundColor(getResources().getColor(R.color.colorDarkest));

        downloaded_papers_listview = thisFragment.findViewById(R.id.offline_papers_listview);
        extras_listview = thisFragment.findViewById(R.id.offline_dynamic_downloads_listview);

        papers_adapter = new DownloadedPapersAdapter(getContext(),this);
        extras_adapter = new DynamicDownloadedAdapter(getContext(),this);
        downloaded_papers_listview.setAdapter(papers_adapter);
        downloaded_papers_listview.setDividerHeight(0);
        extras_listview.setAdapter(extras_adapter);
        extras_listview.setDividerHeight(0);

        refreshEmptyState();

        availabletvroot = thisFragment.findViewById(R.id.availabletvroot);
        availabletvroot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                papers_adapter.refreshMe();
                extras_adapter.refreshMe();
                setDynamicHeight(downloaded_papers_listview);
                setDynamicHeight(extras_listview);
                (v.findViewById(R.id.available_refresh_icon)).startAnimation(UtilityMethods.getRotateAnimation(getContext()));
            }
        });
//        setDynamicHeight(downloaded_papers_listview);
//        setDynamicHeight(extras_listview);
        return thisFragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void refreshAvailableFragment() {
        Log.e("22-6","INSIDE REFRESH AVAILABLE FRAG");

        extras_adapter.refreshMe();
        papers_adapter.refreshMe();
//        setDynamicHeight(downloaded_papers_listview);
//        setDynamicHeight(extras_listview);
        refreshEmptyState();
        }

    public void refreshEmptyState(){

        LinearLayout lr =  thisFragment.findViewById(R.id.downloaded_fragment_empty_state);
        Log.e("22-6","EXTRA COUNT = "+extras_adapter.getCount()+"PAPER COUNT = "+ papers_adapter.getCount());
        if(extras_adapter.getCount()==0 && papers_adapter.getCount()==0){ Log.e("22-6","REFRESH EMPTY STATE SETT VISIBLE");lr.setVisibility(View.VISIBLE); }
        else {
            Log.e("22-6","REFRESH EMPTY STATE SETT GONE");lr.setVisibility(View.GONE); }
    }
    public void setEmptyStateGONE(Boolean b){
        if(b){thisFragment.findViewById(R.id.downloaded_fragment_empty_state).setVisibility(View.GONE);}
        else { thisFragment.findViewById(R.id.downloaded_fragment_empty_state).setVisibility(View.VISIBLE); }
    }

//    public void refreshlist() {
//        downloaded_files = getFiles();
//        papers_adapter.getPapers();
//        downloaded_papers_listview.invalidate();
//        papers_adapter.notifyDataSetChanged();
//        papers_adapter.refreshArrayList(downloaded_files);
//    }






/*
            boolean nevershowagainticked  = (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE ));
            Toast.makeText(getContext(),"nevershowagain ? "+nevershowagainticked,Toast.LENGTH_SHORT).show();
            if(nevershowagainticked){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);}
*/
public static void setDynamicHeight(ListView mListView) {
    ListAdapter mListAdapter = mListView.getAdapter();
    if (mListAdapter == null) {
        // when adapter is null
        return;
    }
    int height = 0;
    int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
    for (int i = 0; i < mListAdapter.getCount(); i++) {
        View listItem = mListAdapter.getView(i, null, mListView);
        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
        height += listItem.getMeasuredHeight();
    }
    ViewGroup.LayoutParams params = mListView.getLayoutParams();
    params.height = height + 20 +(mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
    mListView.setLayoutParams(params);
    mListView.requestLayout();
}

    interface AvailableFragmentInterface{
        void newDownloadFromFragment(DownloadItem item);
    }
}
