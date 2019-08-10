package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import omkar.tenkale.gcoeapapers.R;

import static android.content.Context.MODE_PRIVATE;
import static omkar.tenkale.gcoeapapers.UtilityMethods.DBUPDATEINPROGRESS;

public class Papers extends Fragment {

    View thisFragment;
    String[] exam_type, branch, semester, course;
    Spinner course_spinner, branch_spinner, exam_type_spinner, semester_spinner;
    ArrayAdapter exam_type_adapter, branch_adapter, semester_adapter, course_adapter;
    String selected_course, selected_semester, selected_branch, selected_exam_type, query;
    Cursor returned_cursor;
    //List<String> result_array = new ArrayList<>();
    ListView result_listView;
    LinearLayout no_papers_found_view,papers_submit_root;
    TextView searching_view;
    SearchItem item;
    ArrayList<SearchItem> search_list;
    SearchListAdapter result_adapter;

    public Papers() {
        // Required empty public constructor
    }
  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        thisFragment = inflater.inflate(R.layout.fragment_papers, container, false);
        LinearLayout rootLayout =  thisFragment.findViewById(R.id.download_fragment_root);
        rootLayout.setBackgroundColor(getResources().getColor(R.color.colorDarkest));
        initSpinner();
        result_listView = thisFragment.findViewById(R.id.search_result_listview);
        search_list=new ArrayList<>();
        result_adapter = new SearchListAdapter(getContext(), search_list, Papers.this);
        result_listView.setAdapter(result_adapter);
        View footer =LayoutInflater.from(getContext()).inflate(R.layout.item_submit_pdf_footer,null,false);
       footer.findViewById(R.id.submit_pdf_btn).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ((About)   ((MainActivity)getActivity()).fragments.get(4)).pdf.performClick();
           }
       });
        result_listView.addFooterView(footer);


       /* result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),position,Toast.LENGTH_SHORT).show();
                Log.e("MY MESSAGE","ITEM CLICKED ------------------------");
            }});

*/

        Button searchbtn = thisFragment.findViewById(R.id.search_button);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                makeQuery();
                                // ondownloadclicked.addDownload( COmpoundSubject); //calling B by search button TEMPERORY
                            }

                            @Override
                            public void onAnimationCancel(final View view) {

                            }

                        })
                        .withLayer()
                        .start();
            }
        });
        no_papers_found_view.setVisibility(View.GONE);
        searching_view.setVisibility(View.GONE);
        return thisFragment;

    }

    private void initSpinnerArray() {
        List<String> course_list = Arrays.asList(getResources().getStringArray(R.array.course));
        course = (String[]) course_list.toArray();

        List<String> semester_list = Arrays.asList(getResources().getStringArray(R.array.semester));
        semester = (String[]) semester_list.toArray();

        List<String> branch_list = Arrays.asList(getResources().getStringArray(R.array.branch));
        branch = (String[]) branch_list.toArray();

        List<String> exam_type_list = Arrays.asList(getResources().getStringArray(R.array.exam_type));
        exam_type = (String[]) exam_type_list.toArray();
    }

    private void initSpinner() {

        course_spinner =  thisFragment.findViewById(R.id.course_spinner);
        semester_spinner =  thisFragment.findViewById(R.id.semester_spinner);
        branch_spinner =  thisFragment.findViewById(R.id.branch_spinner);
        exam_type_spinner =  thisFragment.findViewById(R.id.exam_type_spinner);
        no_papers_found_view =  thisFragment.findViewById(R.id.no_papers_found_view);
        papers_submit_root= thisFragment.findViewById(R.id.papers_submit_root);
        no_papers_found_view.findViewById(R.id.submit_pdf_searchlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((About)   ((MainActivity)getActivity()).fragments.get(4)).pdf.performClick();
            }
        });
        searching_view =  thisFragment.findViewById(R.id.searching_view);

        initSpinnerArray();
        initSpinnerAdapter();

    }

    private void initSpinnerAdapter() {

        branch_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_simple_item, branch);
        semester_adapter = new ArrayAdapter<>(getContext(),  R.layout.spinner_simple_item, semester);
        exam_type_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_simple_item, exam_type);
        course_adapter = new ArrayAdapter<>(getContext(),  R.layout.spinner_simple_item, course);


//        branch_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item,R.id.spinner_text, branch);
//        semester_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item,R.id.spinner_text, semester);
//        exam_type_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item,R.id.spinner_text, exam_type);
//        course_adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item,R.id.spinner_text, course);

//        branch_adapter=new SpinnerAdapter(getContext(),0,branch);
//        exam_type_adapter=new SpinnerAdapter(getContext(),0,exam_type);
//        semester_adapter=new SpinnerAdapter(getContext(),0,semester);
//        course_adapter=new SpinnerAdapter(getContext(),0,course);


        branch_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exam_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semester_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        course_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    Toast.makeText(getContext(),"Coming Soon!",Toast.LENGTH_SHORT).show();
                    course_spinner.setSelection(0);}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        course_spinner.setAdapter(course_adapter);
        semester_spinner.setAdapter(semester_adapter);
        branch_spinner.setAdapter(branch_adapter);
        exam_type_spinner.setAdapter(exam_type_adapter);

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        course_spinner.setSelection(pref.getInt("selected_course",0));
        semester_spinner.setSelection(pref.getInt("selected_semester",0));
        branch_spinner.setSelection(pref.getInt("selected_branch",0));
        exam_type_spinner.setSelection(pref.getInt("selected_exam_type",0));


    }

    private void makeQuery() {
        searching_view.setVisibility(View.VISIBLE);
        selected_course = course_spinner.getSelectedItem().toString();
        selected_branch = branch_spinner.getSelectedItem().toString();
        selected_exam_type = exam_type_spinner.getSelectedItem().toString();
        selected_semester = semester_spinner.getSelectedItem().toString();

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("selected_course",course_spinner.getSelectedItemPosition());
        editor.putInt("selected_branch",branch_spinner.getSelectedItemPosition());
        editor.putInt("selected_exam_type",exam_type_spinner.getSelectedItemPosition());
        editor.putInt("selected_semester",semester_spinner.getSelectedItemPosition());
        editor.apply();

        query = "SELECT * FROM " + "PAPERS " + "WHERE " + "course = '" + selected_course + "' AND "
                + " exam_type =  '" + selected_exam_type + "' AND " + "semester = '" + selected_semester.substring(0, 1) + "' AND " + "branch = '" + selected_branch + "'";
        try {
            // returned_cursor = dbadapter_instance.getData(query);
            DBAdapter DBAdapter_instance = new DBAdapter(getContext());
            DBAdapter_instance.createDatabase();
            DBAdapter_instance.open();
            returned_cursor = DBAdapter_instance.getTestData(query);
            /*   if (returned_cursor.getCount() == 0)
             {
                 Toast.makeText(getContext(),"QUERY SUCCESS , NO ITEMS RETURNED",Toast.LENGTH_SHORT).show();
             }
           StringBuilder buffer = new StringBuilder();
             while(returned_cursor.moveToNext())
             {
                 buffer.append(returned_cursor.getString(1)+ "\n");
             }
             */
            Log.i("PAPERS.JAVA", "BEFORE IF METHOD");
            //result_array.clear();

            search_list.clear();
            if (returned_cursor.moveToFirst()) {
                //   str = returned_cursor.getString(1);
                Log.i("PAPERS.JAVA", "INSIDE IF METHOD BEFORE WHILE");
                while (!returned_cursor.isAfterLast()) {
                    //result_array.add(returned_cursor.getString(1)); //cursor.getColumnIndex("course")
                    // result_array.add(returned_cursor.getString(returned_cursor.getColumnIndex("SUBJECT"))); //cursor.getColumnIndex("course")
                    item = new SearchItem();
                    item.Set_SUBJECT_NAME(returned_cursor.getString(returned_cursor.getColumnIndex("SUBJECT__NAME")));
                    item.Set_COURSE_CODE(returned_cursor.getString(returned_cursor.getColumnIndex("COURSE_CODE")));
                    item.Set_EXAM_TYPE(returned_cursor.getString(returned_cursor.getColumnIndex("EXAM_TYPE")));
                    //  str = str + returned_cursor.getString(1) + "\n";
                    search_list.add(item);
                    returned_cursor.moveToNext();
                }
                /*
                 result_listView = (ListView) thisFragment.findViewById(R.id.search_result_listview);
                result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getContext(),position,Toast.LENGTH_SHORT).show();
                        Log.e("MY MESSAGE","ITEM CLICKED ------------------------");
                    }});
                   */

                //ArrayAdapter papers_adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_listview_result,R.id.result_list_subject, result_array);
             //   SearchListAdapter papers_adapter = new SearchListAdapter(getContext(), search_list,Papers.this);
               // result_listView.setAdapter(papers_adapter);
                result_adapter.notifyDataSetChanged();
                no_papers_found_view.setVisibility(View.GONE);
                searching_view.setVisibility(View.GONE);
                result_listView.setVisibility(View.VISIBLE);

                //Toast.makeText(getContext(),"RESULT \n"+str,Toast.LENGTH_SHORT).show();

            } else { //Toast.makeText(getContext(),"QUERY SUCCESS , NO ITEMS RETURNED",Toast.LENGTH_SHORT).show();
                //no papers found ++
                if (DBUPDATEINPROGRESS){
                    Toast.makeText(getContext(),"Database Update is in Progress.Please Try Again After Some Time",Toast.LENGTH_LONG).show();
                }
                result_listView.setVisibility(View.GONE);
                searching_view.setVisibility(View.GONE);
                no_papers_found_view.setVisibility(View.VISIBLE);
            } // 0,4,8
            Log.w("DBBUGHUNT", "AFTER IF METHOD");

            //    Toast.makeText(getContext(),"RESULT \n"+str,Toast.LENGTH_SHORT).show();

            DBAdapter_instance.close();

        } catch (Exception e) {
            Toast.makeText(getContext(), "ERROR MAKING QUERY", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    interface PapersFragmentInterface{
        void newDownloadFromFragment(DownloadItem item);
    }

}
