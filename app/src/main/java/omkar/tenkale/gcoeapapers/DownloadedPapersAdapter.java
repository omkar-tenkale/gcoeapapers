package omkar.tenkale.gcoeapapers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;

import omkar.tenkale.gcoeapapers.R;

public class DownloadedPapersAdapter extends BaseAdapter {
    private Rect rect;
    Context mContext ;
    View list_item ;
    ArrayList<String> downloaded_papers;
    TextView item_downloaded_list_path;
    TextView result_list_item_position ;
    LinearLayout root_layout_item;
    TextView item_downloaded_list_title;
    TextView item_downloaded_list_course ;
    TextView item_downloaded_list_exam_type;
    ImageView delete_button;
    Available AvailableFrag;

    LinearLayout item_downloaded_list_delete_button_cover ;
    DownloadedPapersAdapter(Context context,Available parentfrag){
       mContext = context;
        AvailableFrag=parentfrag;
        downloaded_papers=getPapers();
        //this.downloaded_papers = getPapers();        runtime error
    }

    @Override
    public int getCount() {
        int count=downloaded_papers.size();
        if(count==0) {if(AvailableFrag.extras_adapter.extras_files.size()==0){AvailableFrag.setEmptyStateGONE(false);}}else{AvailableFrag.setEmptyStateGONE(true);}
        return count;
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
    public View getView( int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        list_item =  inflater.inflate(R.layout.item_downloaded_list,null,false);
          root_layout_item =  list_item.findViewById(R.id.item_downloaded_list_root);
        item_downloaded_list_path = list_item.findViewById(R.id.item_downloaded_list_path);
        item_downloaded_list_path.setText(downloaded_papers.get(position));
        item_downloaded_list_title = list_item.findViewById(R.id.item_downloaded_list_subject);
        item_downloaded_list_course = list_item.findViewById(R.id.item_downloaded_list_course);
        item_downloaded_list_exam_type= list_item.findViewById(R.id.item_downloaded_list_exam_type);
        delete_button = list_item.findViewById(R.id.item_downloaded_list_delete_button);
        item_downloaded_list_delete_button_cover = list_item.findViewById(R.id.item_downloaded_list_delete_button_cover);
        item_downloaded_list_delete_button_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v) {
                showDialog(v);



/*


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                               //  View rootview = tempview.getRootView()  ;
                               // TextView tv =rootview.findViewById(R.id.item_downloaded_list_path);



                              //  String path = tv.getText().toString();
                             //   Log.e("POSITIONBUG","PATH :   :  "+ tv.getText().toString() );

                                Log.e("POSITIONBUG","POSITION  :  "+temp_position );
                                File file = new File(temp_text);
                                if(file.delete())
                                {
                                    Toast.makeText(mContext,"File Deleted " ,Toast.LENGTH_SHORT).show();
                                    refreshMe();

                                }
                                else
                                {
                                    Toast.makeText(mContext,"Failed to Delete the File. Check App Permissions.",Toast.LENGTH_LONG).show();

                                }

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Delete this file ?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                */

            }
        });

        try {
            String tempstring = downloaded_papers.get(position);
            item_downloaded_list_path.setText(Environment.getExternalStorageDirectory() + "/GCOEA App/Papers/" + tempstring);
           // tempstring.substring(0, (tempstring.length() - 4));
            item_downloaded_list_title.setText(tempstring.substring(0, (tempstring.length() - 14)));
            item_downloaded_list_course.setText(tempstring.substring(tempstring.lastIndexOf(32), tempstring.length() - 4));
       item_downloaded_list_exam_type.setText(tempstring.substring(tempstring.length()-14,tempstring.lastIndexOf(32)));
        }catch (Exception e){return null;}
       // item_downloaded_list_exam_type.setText(arr[1]);
        root_layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  ViewCompat.animate(v)
                    .setDuration(200)
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setInterpolator(new CycleInterpolator(0.5f))
                    .setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart( View view) { }

                        @Override
                        public void onAnimationEnd( View view) {


                            TextView tv = view.findViewById(R.id.item_downloaded_list_path);
                            String tvtext = tv.getText().toString();
                            //Toast.makeText(mContext,tvtext,Toast.LENGTH_LONG).show();;
                            if (new File(tvtext).exists()){
                            UtilityMethods.openPDF(mContext,new File (tvtext));}else{AvailableFrag.refreshAvailableFragment();}
                        }

                        @Override
                        public void onAnimationCancel( View view) { }
                    })
                    .withLayer()
                    .start();}
        });
     /*   root_layout_item.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                    v.animate().scaleXBy(0.03f).setDuration(100).start();
                    v.animate().scaleYBy(0.03f).setDuration(100).start();

                    return true;
                } else if (action == MotionEvent.ACTION_UP ) {
                    v.animate().cancel();
                    v.animate().scaleX(1f).setDuration(100).start();
                    v.animate().scaleY(1f).setDuration(100).start();
                    v.performClick();
                    return true;}
             //   }else  if(event.getAction() == MotionEvent.ACTION_MOVE) {
                   if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                        // User moved outside bounds
                     //   v.animate().cancel();
                        v.animate().scaleX(1f).setDuration(100).start();
                        v.animate().scaleY(1f).setDuration(100).start();
                       Log.e("xxxxyy","moved outside");
                       v.performClick();

                   }
               // }
                return true;
            }

        });

*/

        return list_item;
    }

    public void refreshMe() {
        downloaded_papers = getPapers();
        this.notifyDataSetChanged();

    }

      public void  refreshArrayList(ArrayList<String> list)
      {
          downloaded_papers =list;
          this.notifyDataSetChanged();
      }
    public void  refreshmArrayList()
    {
        Log.e("POSITIONBUG","REFRESHARRAYLIST INVOKED");
        downloaded_papers = getPapers();
        this.notifyDataSetChanged();
    }

    public ArrayList<String> getPapers() {
        String directory = UtilityMethods.getPath(mContext);
        ArrayList<String> downloaded_files = new ArrayList<>();
        File dir = new File(directory);
        try {
            int i = dir.listFiles().length;
            if ((i > 0)) {
                for (File file : dir.listFiles()) {
                    if (file.getName().endsWith((".pdf"))) {
                        downloaded_files.add(file.getName());
                    }
                }
            }

        }catch (Exception ignored){}
        Log.e("EMPRT", "RETURNING ARRAY LENGTH  1 " + downloaded_files.size()+" <");


        Log.e("EMPRT", "RETURNING ARRAY LENGTH " + downloaded_files.size());

        return downloaded_files;
    }


    private void showDialog(final View view){
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.item_simple_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
                    ((Papers)((MainActivity)mContext).fragments.get(1)).result_adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    AvailableFrag.refreshAvailableFragment();

                }
                else
                {
                    dialog.dismiss();
                    AvailableFrag.refreshAvailableFragment();
                    Toast.makeText(mContext,"Failed to Delete the File. Check App Permissions.",Toast.LENGTH_LONG).show();
                }

            }
        });
/*
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
*/
        dialog.show();
    }
}

