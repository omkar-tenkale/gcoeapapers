package omkar.tenkale.gcoeapapers;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import omkar.tenkale.gcoeapapers.R;

public class DynamicDownloadedAdapter extends BaseAdapter {
    Context mContext;
    View list_item;
    ArrayList<String> extras_files;
    TextView item_downloaded_list_path;
    TextView result_list_item_position;
    LinearLayout item_downloaded_list_delete_button_cover;
    Available AvailableFrag;

    DynamicDownloadedAdapter(Context context,Available parentfrag) {
        mContext = context;
        AvailableFrag=parentfrag;
        extras_files = getExtras();
        //this.downloaded_papers = getPapers();        runtime error
    }

    @Override
    public int getCount() {
        int count =extras_files.size();
        if(count==0) {if(AvailableFrag.papers_adapter.downloaded_papers.size()==0){AvailableFrag.setEmptyStateGONE(false);}}else{AvailableFrag.setEmptyStateGONE(true);}
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
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        list_item = inflater.inflate(R.layout.item_extras_list, null, false);
        LinearLayout root_layout_item = list_item.findViewById(R.id.item_downloaded_list_root);
        item_downloaded_list_path = list_item.findViewById(R.id.item_downloaded_list_path);
        TextView item_downloaded_list_title = list_item.findViewById(R.id.item_downloaded_list_subject);
        item_downloaded_list_delete_button_cover = list_item.findViewById(R.id.item_downloaded_list_delete_button_cover);
        item_downloaded_list_delete_button_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(v);
            }
        });
        item_downloaded_list_path.setText(UtilityMethods.getExtrasPath(mContext)+"/"+extras_files.get(position));
        item_downloaded_list_title.setText(extras_files.get(position).substring(0,extras_files.get(position).length()-4));
        root_layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.animate(v).setDuration(200).scaleX(0.97f).scaleY(0.97f).setInterpolator(new CycleInterpolator(0.5f)).setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        TextView tv = view.findViewById(R.id.item_downloaded_list_path);
                        String tvtext = tv.getText().toString();
                        //Toast.makeText(mContext,tvtext,Toast.LENGTH_LONG).show();
                        if (new File(tvtext).exists()) {
                            UtilityMethods.openPDF(mContext, new File(tvtext));
                        } else {
                            AvailableFrag.refreshAvailableFragment();
                        }
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                    }
                })
                        .withLayer()
                        .start();
            }
        });
        return list_item;
    }


    public void refreshMe() {
        extras_files = getExtras();
        this.notifyDataSetChanged();
    }

    public ArrayList<String> getExtras() {
        String directory = UtilityMethods.getExtrasPath(mContext);
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

        } catch (Exception ignored) {
        }
       // refreshDownloadedEmptyState(downloaded_files.size());
        return downloaded_files;
    }


    private void showDialog(final View view) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.item_simple_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView canclebtn = dialog.findViewById(R.id.simple_dialog_cancel);
        canclebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView confirmbtn = dialog.findViewById(R.id.simple_dialog_delete);
        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = view.findViewById(R.id.item_downloaded_list_path);
                File file = new File(tv.getText().toString());
                if (file.delete()) {
                    Toast.makeText(mContext, "File Deleted", Toast.LENGTH_SHORT).show();
                    ((Papers) ((MainActivity) mContext).fragments.get(1)).result_adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    AvailableFrag.refreshAvailableFragment();

                } else {
                    dialog.dismiss();
                    AvailableFrag.refreshAvailableFragment();
                    Toast.makeText(mContext, "Failed to Delete the File. Check App Permissions.", Toast.LENGTH_LONG).show();
                }

            }
        });

        dialog.show();
    }
}

