package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import omkar.tenkale.gcoeapapers.R;

public class SearchListAdapter extends BaseAdapter {

    private Context mContext ;
    View list_item ;
    private ArrayList search_list ;
    DownloadItem item ;
    private  String CompoundSubject ;
    private TextView availabletv;
    private  ArrayList<DownloadItem> CompoundObjectList;

    SearchListAdapter(Context context , ArrayList<SearchItem> search_list, Papers fragment){
        Papers parentFragment = fragment;
        CompoundObjectList= new ArrayList<>();
        this.search_list = search_list;
        mContext = context;
    }

    @Override
    public int getCount() {
        Log.e("SEARCHLIST ADAPTER ","SEARCHLIST SIZE : " + search_list.size());

        return search_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = LayoutInflater.from(mContext);
        list_item =  inflater.inflate(R.layout.item_listview_result,null,false);
        TextView subject = list_item.findViewById(R.id.result_list_subject);
        TextView course_code = list_item.findViewById(R.id.result_list_course);
       // availabletv = list_item.findViewById(R.id.result_list_available);
        SearchItem sitem = (SearchItem) search_list.get(position);
        if(new File(Environment.getExternalStorageDirectory() + "/GCOEA App/Papers/"+ sitem.Get_SUBJECT_NAME()+" "+ sitem.Get_EXAM_TYPE()+" "+ sitem.Get_COURSE_CODE()+".pdf").exists()){
            list_item.findViewById(R.id.result_list_available).setVisibility(View.VISIBLE);
        }
        TextView exam_type = list_item.findViewById(R.id.result_list_exam_type);
        TextView result_list_item_position = list_item.findViewById(R.id.result_list_item_position);

        item =new DownloadItem()
                .setEXTRA_EXAMTYPE(sitem.Get_EXAM_TYPE())
                .setEXTRA_COURSE_CODE(sitem.Get_COURSE_CODE())
                .setTYPE(DownloadItem.TYPE.NATIVE_PDF); //native download
        subject.setText(sitem.Get_SUBJECT_NAME());
        course_code.setText(sitem.Get_COURSE_CODE());
        exam_type.setText(sitem.Get_EXAM_TYPE());


        CompoundObjectList.add(position,item);
        result_list_item_position.setText(String.valueOf(position));


        list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                ViewCompat.animate(v)
                        .setDuration(200)
                        .scaleX(0.97f)
                        .scaleY(0.97f)
                        .setInterpolator(new CycleInterpolator(0.5f))
                        .setListener(new ViewPropertyAnimatorListener() {
                            @Override
                            public void onAnimationStart(final View view) { }
                            @Override
                            public void onAnimationEnd(final View view) {

                                TextView pos = v.findViewById(R.id.result_list_item_position);
                String mposition =  pos.getText().toString();
                int mpositionInt = Integer.parseInt(mposition);
                //mposition = CompoundSubjectList.get(mpositionInt);
              // CompoundSubject= course_code.getText().toString()+exam_type.getText().toString() ;
                //CompoundSubject = sitem.Get_COURSE_CODE()+sitem.Get_EXAM_TYPE()  ;
             //   ((MainActivity) getActivity()).startChronometer();
               // MainActivity main = new MainActivity();
                                ((MainActivity) mContext).addDownload(CompoundObjectList.get(mpositionInt));

                            }
                      @Override
                            public void onAnimationCancel(final View view) { }})
                        .withLayer()
                        .start();}
        });
        return list_item;
    }
}
