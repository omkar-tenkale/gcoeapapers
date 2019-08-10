package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;
import java.io.File;
import java.util.ArrayList;

import omkar.tenkale.gcoeapapers.R;

import static omkar.tenkale.gcoeapapers.LinksManager.InitLinkUpdater;
import static omkar.tenkale.gcoeapapers.UtilityMethods.getExtrasPath;
import static omkar.tenkale.gcoeapapers.UtilityMethods.getPath;

public class DownloadListAdapter extends BaseAdapter {
    Context mContext;
    Download Downloadfragment;
    private ArrayList<View> AvailableViews;
    private ArrayList<DownloadItem> AvailableItems;
    // ArrayList<String> mDownloadInitializerStrings;
    ArrayList<Integer> mdownload_ids;
    ProgressBar progress_bar;

    public DownloadListAdapter(Context context, Download fragment, ArrayList<DownloadItem> DownloadInitializerObjects) {
        Log.e("ADAPTERBUGHUNT", " ENTERED  DOWNLOADLISTADAPTER");
       // AvailableItems = new ArrayList<>();
        AvailableViews = new ArrayList();
        mdownload_ids = new ArrayList();
        mContext = context;
        Downloadfragment = fragment;
        //mDownloadInitializerStrings = DownloadInitializerStrings;
        AvailableItems = DownloadInitializerObjects;
    }

    @Override
    public int getCount() {
        if (AvailableItems.size() != 0) {
            ((MainActivity) mContext).setDownloadingEmptyState(false);
        }
        return AvailableItems.size();
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

        if (position < AvailableViews.size()) {
            View v =AvailableViews.get(position);
            boolean b = (v==null);
            Log.e("cleanup1", "RETURNING AVAILABLE VIEW for @"+position+"IS NULL ? "+b +"visible ? "+v.getVisibility());
            return v;
        } else {
            Log.e("cleanup1", "RETURNING NEW VIEW for @"+position);
            return HandleNewDownloadRequest(AvailableItems.get(position),position);
        }
    }

    private View HandleNewDownloadRequest(DownloadItem mitem, int position){
        DownloadItem.TYPE type = mitem.getTYPE();
        switch (type) {
            case NATIVE_PDF:
            return initpdfDownload(mitem,position,true);
        case PDF:
            return initpdfDownload(mitem,position,false);
        case UPDATE_APK:
            return initUpdateDownload(mitem,position);
        }
        return null;
    }
    private View initpdfDownload(DownloadItem item , int position, boolean isNative){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mITEM_VIEW = inflater.inflate(R.layout.item_downloading_list, null, false);
        mITEM_VIEW.setTag(item);
        item.setITEM_VIEW(mITEM_VIEW);
        AvailableViews.add(position,mITEM_VIEW);
        if(isNative) {
            item.setDNAME(UtilityMethods.getDNameByCodenType(mContext, item.getEXTRA_COURSE_CODE(), item.getEXTRA_EXAMTYPE()));
            item.setFNAME(UtilityMethods.getFNameByCodenType(mContext, item.getEXTRA_COURSE_CODE(), item.getEXTRA_EXAMTYPE()));
            item.setLINK(UtilityMethods.resolve2((MainActivity) mContext, LinksManager.getCORE_DOMAIN(), UtilityMethods.LINK.CORE_DOMAIN) + "/papers/" + item.getEXTRA_COURSE_CODE() + item.getEXTRA_EXAMTYPE() + ".pdf");
            item.setPATH(getPath(mContext));
            item.getTVPATH().setText(getPath(mContext) + "/" + UtilityMethods.getFNameByCodenType(mContext, item.getEXTRA_COURSE_CODE(), item.getEXTRA_EXAMTYPE()));
            AnalyticsManager.OnNativePaperDownload(mContext, item.getEXTRA_COURSE_CODE() + item.getEXTRA_EXAMTYPE());
         }else {item.getTVPATH().setText(getExtrasPath(mContext)+"/"+item.getFNAME());}
        DownloadEngine(item,position);
        return mITEM_VIEW ;
    }

    private View initUpdateDownload(DownloadItem item, int position){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mITEM_VIEW = inflater.inflate(R.layout.item_downloading_list, null, false);
        mITEM_VIEW.setTag(item);
        item.setITEM_VIEW(mITEM_VIEW);
        AvailableViews.add(position,mITEM_VIEW);
        item.setDNAME("App Update");
        item.setFNAME("update.apk");
        item.setPATH(Environment.getExternalStorageDirectory() + "/GCOEA App");
        DownloadEngine(item,position);
        return mITEM_VIEW;
    }
    private  void DownloadEngine(final DownloadItem moditem, final int mposition){

        moditem.getCLICK_ROOT().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (PRDownloader.getStatus(mdownload_ids.get(mposition)) == Status.UNKNOWN) {
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

                                    DownloadItem i =(DownloadItem)((LinearLayout)v.getParent()).getTag();
                                    if( i.getTYPE()== DownloadItem.TYPE.UPDATE_APK){
                                        UtilityMethods.tryAPKInstall((MainActivity)mContext);
                                        return;
                                    }
                                    if( i.getTYPE()== DownloadItem.TYPE.PDF){
                                        File f = new File (i.getTVPATH().getText().toString());
                                        if(f.exists()){UtilityMethods.openPDF(mContext,f);}
                                        return;
                                    }

                                    // TextView pathttv = v.findViewById(R.id.download_list_item_filepath);
                                  //  String filepath = pathttv.getText().toString();
                                    String path=Environment.getExternalStorageDirectory() + "/GCOEA App/Papers/"+i.getFNAME();
                                 //   Toast.makeText(mContext, path, Toast.LENGTH_LONG).show();
                                    if (!(new File(path)).exists()) {
                                        return;
                                    }
                                    UtilityMethods.openPDF(mContext,new File(path));
                                }

                                @Override
                                public void onAnimationCancel(final View view) {
                                }
                            })
                            .withLayer()
                            .start();
                }// else Toast.makeText(mContext, "Please Wait :)", Toast.LENGTH_LONG).show();
            }});
        moditem.getPAUSERESUMEBUTTON().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PRDownloader.getStatus(mdownload_ids.get(mposition)) == Status.UNKNOWN) {
                    //Toast.makeText(mContext,"unknown...",Toast.LENGTH_SHORT).show();

                    return;}
                //   Drawable current_img =pauseresumebtn.getDrawable();
                // Drawable pause_icon  = view.getResources().getDrawable(R.drawable.icon_pause);
                // int current_img_int =   view.getContext().getResources().getIdentifier("icon_pause", "drawable",mContext.getPackageName());
                if (PRDownloader.getStatus(mdownload_ids.get(mposition)) == Status.RUNNING) {
                    // if (areDrawablesIdentical(current_img,pause_icon)){
                    //  if(current_img.){
                    //do work here
                    // pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                    //  pauseresumebtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_resume));
                    Log.e("BUTTONBUG", "++++++++++PAUSED ");
                    Toast.makeText(mContext,"Pausing...",Toast.LENGTH_SHORT).show();
                    PRDownloader.pause(mdownload_ids.get(mposition));
                    // Log.e("MYMESSAGE15-5-18","AFTER MANUAL PRDOWNLOAD.PAUSE ID WAS (FROM DownloadingList.get(0))  :" + DownloadingList.get(0) );
                } else {
                    // pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                    // pauseresumebtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_pause));
                    Toast.makeText(mContext,"Resuming...",Toast.LENGTH_SHORT).show();

                    PRDownloader.resume(mdownload_ids.get(mposition));

                    Log.e("BUTTONBUG", "++++++++++RESUMED");

                }
            }
        });

        moditem.getCANCELBUTTON().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PRDownloader.cancel(mdownload_ids.get(position));

                int id = mdownload_ids.get(mposition);
                PRDownloader.cancel(id);

            }
        });
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //  int id = PRDownloader.download(UtilityMethods.resolve(((Activity) mContext), 1) + "/GCOEA App/Papers/" + course_code + exam_type + ".pdf", path, name)
        Log.e("ANALYTIC","MOD LINK +++++ ="+moditem.getLINK());
        Log.e("cleanup1", "+++++++++PRDOWNLOADER START DOWNLOAD WITH LINK =  : "+moditem.getLINK()+"  path :  "+moditem.getPATH()+" dname:   "+moditem.getDNAME() +" <");
        int id = PRDownloader.download(moditem.getLINK(), moditem.getPATH(), moditem.getFNAME())
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {


                        ////   pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                        //  tvsubject = AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                        // tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);;
                        //  progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);
                        // tvprogress =  AvailableViews.get(position).findViewById(R.id.download_list_item_progress);;
                        //  TextView pathttv = AvailableViews.get(position).findViewById(R.id.download_list_item_filepath);


                        //    Log.e("mymessage", "inside download onstartorresume lisnr");
                        // progress_bar.setIndeterminate(false);
                        // ((DownloadItem)  (AvailableViews.get(position)).get  .setIndeterminate(false);
                        AvailableItems.get(mposition).getPROGRESS_BAR().setIndeterminate(false);
                        //  tvsubject.setText(getDownloadFileName(course_code,exam_type));
                        AvailableItems.get(mposition).getTITLE().setText(moditem.getDNAME());
                        // tvsubject.setText(getDownloadFileNameNoExtension(course_code, exam_type));
                        AvailableItems.get(mposition).getPAUSERESUMEBUTTON().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_pause));
                        // AvailableItems.get(position).setPATH(Environment.getExternalStorageDirectory() + "/GCOEA App/Papers/" + getDownloadFileName(AvailableItems.get(position).getEXTRA_COURSE_CODE(), AvailableItems.get(position).getEXTRA_EXAMTYPE()));
                        //  pauseresumebtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_pause));
                        //buttonOne.setText(R.string.pause);
                        // buttonCancelOne.setEnabled(true);
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {


                        //pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                        //tvsubject =  AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                        // tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);;
                        // progress_bar =  AvailableViews.get(position).findViewById(R.id.progress_bar);;
                        // tvprogress =  AvailableViews.get(position).findViewById(R.id.download_list_item_progress);;


                        AvailableItems.get(mposition).getPAUSERESUMEBUTTON().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_resume));

                        // buttonOne.setText(R.string.resume);
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        // buttonOne.setText(R.string.start);
                        // buttonCancelOne.setEnabled(false);

/*
                    pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                    cancelbtn = AvailableViews.get(position).findViewById(R.id.download_cancel_button);
                    tvsubject = AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                    tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);
                    ;
                    progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);
                    ;
                    tvprogress = AvailableViews.get(position).findViewById(R.id.download_list_item_progress);
*/
                        Log.e("cleanup1","cancelled download at position "+mposition);

                        new File( AvailableItems.get(mposition).getTVPATH().getText().toString()+".temp").delete();
                        Log.e("cleanup1","cancelled visibility gone @@"+mposition);
                        AvailableItems.get(mposition).getPAUSERESUMEBUTTON().setVisibility(View.GONE);
                        // tvsubject.setText("Download Cancelled");
                        AvailableItems.get(mposition).getPROGRESS_BAR().setVisibility(View.GONE);
                        AvailableItems.get(mposition).getPROGRESS_PERCENT().setText("Download Cancelled");

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {


                        // pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                        // tvsubject =  AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                        // tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);;
                        //  progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);
                        //  tvprogress = AvailableViews.get(position).findViewById(R.id.download_list_item_progress);

                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        if (progressPercent < 1010 && progressPercent >= 0) {
                            Log.e("PROGRESSBUG", "SETTING PROGRESS : " + progressPercent);
                            AvailableItems.get(mposition).getPROGRESS_BAR().setProgress((int) progressPercent);

                            // tvprogress.setText(Utils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                            AvailableItems.get(mposition).getPROGRESS_PERCENT().setText(Long.toString(progressPercent) + "%");
                        } else {
                            AvailableItems.get(mposition).getPROGRESS_BAR().setProgress(100);
                            AvailableItems.get(mposition).getPROGRESS_PERCENT().setText("100%");


                        }
                    }
                })
                .start(
                        new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {

/*
                            pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                            cancelbtn = AvailableViews.get(position).findViewById(R.id.download_cancel_button);
                            tvsubject = AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                            tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);
                            ;
                            progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);
                            ;
                            tvprogress = AvailableViews.get(position).findViewById(R.id.download_list_item_progress);
                            ;

*/
                                Log.e("mymessage", "inside ONdownloadCOMPLETE   lisnr");

                                //  buttonOne.setEnabled(false);
                                // buttonCancelOne.setEnabled(false);
                                // buttonOne.setText(R.string.completed);
                                AvailableItems.get(mposition).getPAUSERESUMEBUTTON().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_downloaded));
                                Log.e("cleanup1","completed visibility gone @@"+mposition);
                                AvailableItems.get(mposition).getCANCELBUTTON().setVisibility(View.GONE);
                                ((MainActivity) mContext).refresh();

                                //  AvailableViews.get(position).setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Error error) {


                                Log.e("mymessage", "POSITION IS ++++++++++++++++++++ " + mposition);
                                Log.e("mymessage", "SERVER ERROR ?  " + error.isServerError());
                                Log.e("mymessage", "CONNECTION ERROR ?  " + error.isConnectionError());

/*
                            pauseresumebtn = AvailableViews.get(mposition).findViewById(R.id.download_control_button);
                            tvsubject = AvailableViews.get(mposition).findViewById(R.id.download_list_item_subject);
                            tvexam_type = AvailableViews.get(mposition).findViewById(R.id.download_list_item_exam_type);

                            progress_bar = AvailableViews.get(mposition).findViewById(R.id.download_progress_bar);

                            tvprogress = AvailableViews.get(mposition).findViewById(R.id.download_list_item_progress);
*/
                                String errorstring = "Unknown Error Occured";
                                if (error.isConnectionError()) {
                                    if (!UtilityMethods.checkWritePermission(mContext,false)) {
                                        errorstring = "Can't Download Without Storage Permission";
                                    } else {
                                        errorstring = "Connection Error Occured";
                                    }
                                } else if (error.isServerError()) {
                                    errorstring = "Server Error Occured";
                                }


                                //  tvsubject.setText("Download Failed !");
                                // buttonOne.setText(R.string.start);
                                Toast.makeText(mContext, errorstring, Toast.LENGTH_LONG).show();
                                AvailableItems.get(mposition).getPROGRESS_PERCENT().setText("Download Failed !");
                                Log.e("22-6","corelink = "+AvailableItems.get(mposition).getLINK());
                                new File( AvailableItems.get(mposition).getTVPATH().getText().toString()+".temp").delete();
                                //progress_bar.setProgress(0);
                                // downloadIdOne = 0;
                                //buttonCancelOne.setEnabled(false);
                                //progress_bar.setIndeterminate(false);
                                Log.e("cleanup1","error visibility gone @@"+mposition);
                                AvailableItems.get(mposition).getPROGRESS_BAR().setVisibility(View.GONE);
                                AvailableItems.get(mposition).getPAUSERESUMEBUTTON().setVisibility(View.GONE);
                                // pauseresumebtn.setColorFilter(ContextCompat.getColor(mContext, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
                                //buttonOne.setEnabled(true);
                                InitLinkUpdater(mContext);
                            }});

        mdownload_ids.add(id);
        moditem.setID(id);
     // AvailableItems.add(mposition,moditem);
       // AvailableViews.add(moditem.getITEM_VIEW());
       // return moditem.getITEM_VIEW();
    }
/*
    public View createNewDownload(int position) {
        Log.e("ADAPTERBUGHUNT", " ENTERED  DOWNLOADLISTADAPTER getnewitem");
       String COmpoundSubject =      "";              //mDownloadInitializerStrings.get(position);
        new_download_course_code = COmpoundSubject.substring(0, 6);
        new_download_exam_type = COmpoundSubject.substring(6);
        View view = initiateDownload(new_download_course_code, new_download_exam_type, position);
        Log.e("ADAPTERBUGHUNT", " SUCCESSFULLY EXITED  FROM  DOWNLOADLISTADAPTER , NOW BACK IN GETNEWITEM");
        AvailableViews.add(view);
        return view;
    }

    private void initiateDownload2(DownloadItem item) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_downloading_list, null, false);
        ImageView pauseresumebtn = view.findViewById(R.id.download_control_button);
        ImageView cancelbtn = view.findViewById(R.id.download_cancel_button);
        TextView title = view.findViewById(R.id.download_list_item_subject);
        TextView tvexam_type = view.findViewById(R.id.download_list_item_exam_type);
        ProgressBar progress_bar = view.findViewById(R.id.download_progress_bar);
        ;
        TextView tvprogress = view.findViewById(R.id.download_list_item_progress);
        LinearLayout sub_root = view.findViewById(R.id.download_list_item_subroot);



    }

    private View initiateDownload(final String course_code, final String exam_type, final int position) {
        Log.e("ADAPTERBUGHUNT", " ENTERED  DOWNLOADLISTADAPTER INITIALIZEDOWNLOAD");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_downloading_list, null, false);
        String path = getPath();
        String name = getDownloadFileName(course_code, exam_type);

      pauseresumebtn = view.findViewById(R.id.download_control_button);
        cancelbtn = view.findViewById(R.id.download_cancel_button);
        tvsubject = view.findViewById(R.id.download_list_item_subject);
        tvexam_type = view.findViewById(R.id.download_list_item_exam_type);
        progress_bar = view.findViewById(R.id.download_progress_bar);
        tvprogress = view.findViewById(R.id.download_list_item_progress);
        sub_root = view.findViewById(R.id.download_list_item_subroot);

            sub_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.e("aaaa", "inside" + PRDownloader.getStatus(mdownload_ids.get(position)).toString());

                if (PRDownloader.getStatus(mdownload_ids.get(position)) == Status.UNKNOWN) {
                    Log.e("aaaa", "inside if unknown" + PRDownloader.getStatus(mdownload_ids.get(position)).toString());

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
                                    Log.e("aaaa", "inside naimation end" + PRDownloader.getStatus(mdownload_ids.get(position)).toString());

                                    // getDownloadFileName() Environment.getExternalStorageDirectory()+"/GCOEA App/Papers";
                                    //((MainActivity) mContext).mainViewPager.setCurrentItem(3); ;
                                    TextView pathttv = v.findViewById(R.id.download_list_item_filepath);
                                    String filepath = pathttv.getText().toString();
                                    if (!(new File(filepath)).exists()) {
                                        return;
                                    }

                                    UtilityMethods.openPDF(new File(filepath));
                                }

                                @Override
                                public void onAnimationCancel(final View view) {
                                }
                            })
                            .withLayer()
                            .start();
                } else Toast.makeText(mContext, "sometfnvs", Toast.LENGTH_LONG).show();
            }
        });
        pauseresumebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (PRDownloader.getStatus(mdownload_ids.get(position)) == Status.UNKNOWN) {
                    return;
                }
                //   Drawable current_img =pauseresumebtn.getDrawable();
                // Drawable pause_icon  = view.getResources().getDrawable(R.drawable.icon_pause);
                // int current_img_int =   view.getContext().getResources().getIdentifier("icon_pause", "drawable",mContext.getPackageName());
                if (PRDownloader.getStatus(mdownload_ids.get(position)) == Status.RUNNING) {
                    // if (areDrawablesIdentical(current_img,pause_icon)){
                    //  if(current_img.){
                    //do work here
                    // pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                    //  pauseresumebtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_resume));
                    Log.e("BUTTONBUG", "++++++++++PAUSED ");

                    PRDownloader.pause(mdownload_ids.get(position));
                    // Log.e("MYMESSAGE15-5-18","AFTER MANUAL PRDOWNLOAD.PAUSE ID WAS (FROM DownloadingList.get(0))  :" + DownloadingList.get(0) );
                } else {
                    // pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                    // pauseresumebtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_pause));
                    PRDownloader.resume(mdownload_ids.get(position));

                    Log.e("BUTTONBUG", "++++++++++RESUMED");

                }
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PRDownloader.cancel(mdownload_ids.get(position));
                int id = mdownload_ids.get(position);
                PRDownloader.cancel(id);
            }
        });

      Log.e("mymessage", "+++++++++++++++++++filename SET :  " + name);  //http://www.africau.edu/images/default/sample.pdf , "http://www.pdf995.com/samples/pdf.pdf"
        Log.e("RESOLVE", "GENERATED LINK  : " + UtilityMethods.resolve(((Activity) mContext), 1) + "papers/" + course_code + exam_type + ".pdf");
        int id = PRDownloader.download(UtilityMethods.resolve(((Activity) mContext), 1) + "/GCOEA App/Papers/" + course_code + exam_type + ".pdf", path, name)


                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {


                        pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                        tvsubject = AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                        // tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);;
                        progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);
                        ;
                        // tvprogress =  AvailableViews.get(position).findViewById(R.id.download_list_item_progress);;
                        TextView pathttv = AvailableViews.get(position).findViewById(R.id.download_list_item_filepath);


                        Log.e("mymessage", "inside download onstartorresume lisnr");
                        progress_bar.setIndeterminate(false);
                        //  tvsubject.setText(getDownloadFileName(course_code,exam_type));
                        tvsubject.setText(getDownloadFileNameNoExtension(course_code, exam_type));

                        pathttv.setText(Environment.getExternalStorageDirectory() + "/GCOEA App/Papers/" + getDownloadFileName(course_code, exam_type));
                        pauseresumebtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_pause));
                        //buttonOne.setText(R.string.pause);
                        // buttonCancelOne.setEnabled(true);
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {


                        pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                        //tvsubject =  AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                        // tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);;
                        // progress_bar =  AvailableViews.get(position).findViewById(R.id.progress_bar);;
                        // tvprogress =  AvailableViews.get(position).findViewById(R.id.download_list_item_progress);;


                        pauseresumebtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_resume));

                        // buttonOne.setText(R.string.resume);
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        // buttonOne.setText(R.string.start);
                        // buttonCancelOne.setEnabled(false);


                        pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                        cancelbtn = AvailableViews.get(position).findViewById(R.id.download_cancel_button);
                        tvsubject = AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                        tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);
                        ;
                        progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);
                        ;
                        tvprogress = AvailableViews.get(position).findViewById(R.id.download_list_item_progress);

                        pauseresumebtn.setVisibility(View.GONE);
                        // tvsubject.setText("Download Cancelled");
                        progress_bar.setVisibility(View.GONE);
                        tvprogress.setText("Download Cancelled");

                        //TODO MORE WORK HEREEE8888888888888888888888888888888888888888888888888888888888888888888888888888888

                        //  progress_bar.setIndeterminate(false);
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {


                        // pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                        // tvsubject =  AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                        // tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);;
                        progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);
                        ;
                        tvprogress = AvailableViews.get(position).findViewById(R.id.download_list_item_progress);
                        ;


                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        if (progressPercent < 101 && progressPercent > 0) {
                            Log.e("PROGRESSBUG", "SETTING PROGRESS : " + progressPercent);
                            progress_bar.setProgress((int) progressPercent);

                            // tvprogress.setText(Utils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                            tvprogress.setText(Long.toString(progressPercent) + "%");
                        } else {
                            progress_bar.setProgress(100);
                            tvprogress.setText("100%");


                        }
                    }
                })
                .start(
                        new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {


                                pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                                cancelbtn = AvailableViews.get(position).findViewById(R.id.download_cancel_button);
                                tvsubject = AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                                tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);
                                ;
                                progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);
                                ;
                                tvprogress = AvailableViews.get(position).findViewById(R.id.download_list_item_progress);
                                ;


                                Log.e("mymessage", "inside ONdownloadCOMPLETE   lisnr");

                                //  buttonOne.setEnabled(false);
                                // buttonCancelOne.setEnabled(false);
                                // buttonOne.setText(R.string.completed);
                                pauseresumebtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.icon_downloaded));
                                cancelbtn.setVisibility(View.GONE);
                                ((MainActivity) mContext).refresh();
                                //  AvailableViews.get(position).setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Error error) {


                                Log.e("mymessage", "POSITION IS ++++++++++++++++++++ " + position);
                                Log.e("mymessage", "SERVER ERROR ?  " + error.isServerError());
                                Log.e("mymessage", "CONNECTION ERROR ?  " + error.isConnectionError());


                                pauseresumebtn = AvailableViews.get(position).findViewById(R.id.download_control_button);
                                tvsubject = AvailableViews.get(position).findViewById(R.id.download_list_item_subject);
                                tvexam_type = AvailableViews.get(position).findViewById(R.id.download_list_item_exam_type);

                                progress_bar = AvailableViews.get(position).findViewById(R.id.download_progress_bar);

                                tvprogress = AvailableViews.get(position).findViewById(R.id.download_list_item_progress);

                                String errorstring = "Unknown Error Occured";
                                if (error.isConnectionError()) {
                                    if (!UtilityMethods.checkWritePermission()) {
                                        errorstring = "Can't Download without Storage Permission";
                                    } else {
                                        errorstring = "Connection Error Occured";
                                    }
                                } else if (error.isServerError()) {
                                    errorstring = "Server Error Occured";
                                }


                                //  tvsubject.setText("Download Failed !");
                                // buttonOne.setText(R.string.start);
                                Toast.makeText(mContext, errorstring, Toast.LENGTH_LONG).show();
                                tvprogress.setText("Download Failed !");
                                //progress_bar.setProgress(0);
                                // downloadIdOne = 0;
                                //buttonCancelOne.setEnabled(false);
                                //progress_bar.setIndeterminate(false);
                                progress_bar.setVisibility(View.GONE);
                                pauseresumebtn.setVisibility(View.GONE);
                                // pauseresumebtn.setColorFilter(ContextCompat.getColor(mContext, R.color.colorRed), android.graphics.PorterDuff.Mode.MULTIPLY);
                                //buttonOne.setEnabled(true);
                            }


                        });


        Log.e("ADAPTERBUGHUNT", " INSIDE  DOWNLOADLISTADAPTER INITIALIZEDOWNLOAD , ALL LISTENERS SET and ID IS  :  " + id);
        mdownload_ids.add(id);
        return view;
    }

    private String getDownloadFileName(String course_code, String exam_type) {
        String downloadFileName = course_code + " " + exam_type + ".pdf";
        String query = "SELECT * FROM " + "PAPERS " + "WHERE " + "course_code = '" + course_code + "' AND "
                + " exam_type =  '" + exam_type + "'";
        // SQLiteDatabase db = getWritableDatabase();
        Log.e("mymessage", "inside getdownloadfilename ");
        DBAdapter DBAdapter_instance = new DBAdapter(mContext);
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

    private String getDownloadFileNameNoExtension(String course_code, String exam_type) {
        String downloadFileName = course_code + " " + exam_type + ".pdf";
        String query = "SELECT * FROM " + "PAPERS " + "WHERE " + "course_code = '" + course_code + "' AND "
                + " exam_type =  '" + exam_type + "'";
        // SQLiteDatabase db = getWritableDatabase();
        Log.e("mymessage", "inside getdownloadfilename ");
        DBAdapter DBAdapter_instance = new DBAdapter(mContext);
        DBAdapter_instance.createDatabase();
        DBAdapter_instance.open();
        Cursor returned_cursor = DBAdapter_instance.getTestData(query);
        Log.w("DBBUGHUNT", "INSIDE DOWNLOAD.JAVA AFTER CURSOR CREATED AND CURSOR LENGTH : " + returned_cursor.getCount() + "where query was : " + query);
        if (returned_cursor.moveToFirst()) {
            while (!returned_cursor.isAfterLast()) {
                Log.w("DBBUGHUNT", "INSIDE DOWNLOAD.JAVA AFTER CURSOR CREATED INSIDE   if (returned_cursor.moveToFirst()) ");
                downloadFileName = returned_cursor.getString(returned_cursor.getColumnIndex("SUBJECT__NAME")) + " " + exam_type;
                returned_cursor.moveToNext();
            }
        }
        DBAdapter_instance.close();

        return downloadFileName;

    }



    public static boolean areDrawablesIdentical(Drawable drawableA, Drawable drawableB) {
        Drawable.ConstantState stateA = drawableA.getConstantState();
        Drawable.ConstantState stateB = drawableB.getConstantState();
        // If the constant state is identical, they are using the same drawable resource.
        // However, the opposite is not necessarily true.
        return (stateA != null && stateB != null && stateA.equals(stateB))
                || getBitmap(drawableA).sameAs(getBitmap(drawableB));
    }

    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap result;
        if (drawable instanceof BitmapDrawable) {
            result = ((BitmapDrawable) drawable).getBitmap();
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            // Some drawables have no intrinsic width - e.g. solid colours.
            if (width <= 0) {
                width = 1;
            }
            if (height <= 0) {
                height = 1;
            }

            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }
        return result;
    }

*/
}
