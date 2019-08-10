package omkar.tenkale.gcoeapapers;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import omkar.tenkale.gcoeapapers.R;

public class DownloadItem {

    private String FNAME="";
    private int ID;
    private String DNAME;
    private String LINK;
    private String PATH;
    private String EXTENSION;
    private String EXTRA;
    private ImageView PAUSERESUMEBUTTON;
    private ImageView CANCELBUTTON;
    private TextView TITLE;
    private TextView TVPATH;
    private ProgressBar PROGRESS_BAR;
    private TextView PROGRESS_PERCENT;
    private LinearLayout CLICK_ROOT;
    private View ITEM_VIEW;
    private String EXTRA_COURSE_CODE;
    private String EXTRA_EXAMTYPE;
    public enum TYPE{
        NATIVE_PDF,
        PDF,
        UPDATE_APK,
    }
    private TYPE ITEM_TYPE;

    public DownloadItem setITEM_VIEW(View ITEM_VIEW) {
        this.ITEM_VIEW = ITEM_VIEW;
        PAUSERESUMEBUTTON = ITEM_VIEW.findViewById(R.id.download_control_button);
        CANCELBUTTON = ITEM_VIEW.findViewById(R.id.download_cancel_button);
        TITLE = ITEM_VIEW.findViewById(R.id.download_list_item_subject);
        PROGRESS_BAR = ITEM_VIEW.findViewById(R.id.download_progress_bar);
        TVPATH = ITEM_VIEW.findViewById(R.id.download_list_item_filepath);
        PROGRESS_PERCENT = ITEM_VIEW.findViewById(R.id.download_list_item_progress);
        CLICK_ROOT = ITEM_VIEW.findViewById(R.id.download_fragment_subroot);
        return  this;
    }
/*
    public DownloadItem(  ) {
       // LayoutInflater inflater = LayoutInflater.from(mContext);
       // ITEM_VIEW = inflater.inflate(R.layout.item_downloading_list, null, false);

    }

*/

    public View getITEM_VIEW() {
        return ITEM_VIEW;
    }

    public ImageView getPAUSERESUMEBUTTON() {
        return PAUSERESUMEBUTTON;
    }

    public ImageView getCANCELBUTTON() {
        return CANCELBUTTON;
    }

    public TextView getTITLE() {
        return TITLE;
    }
    public int getID() {
        return ID;
    }

    public DownloadItem setID(int ID) {
        this.ID = ID;
        return this;
    }

    public TextView getTVPATH() {
        return TVPATH;
    }

    public DownloadItem setTVPATH(TextView TVPATH) {
        this.TVPATH = TVPATH;
        return this;
    }


    public ProgressBar getPROGRESS_BAR() {
        return PROGRESS_BAR;
    }

    public TextView getPROGRESS_PERCENT() {
        return PROGRESS_PERCENT;
    }

    public LinearLayout getCLICK_ROOT() {
        return CLICK_ROOT;
    }

    public String getLINK() {
        return LINK;
    }

    public DownloadItem setLINK(String LINK) {
        this.LINK = LINK;
        return this;
    }

    public String getEXTRA() {
        return EXTRA;
    }

    public DownloadItem setEXTRA(String EXTRA) {
        this.EXTRA = EXTRA;
        return this;
    }

    public String getEXTRA_COURSE_CODE() {
        return EXTRA_COURSE_CODE;
    }

    public DownloadItem setEXTRA_COURSE_CODE(String EXTRA_COURSE_CODE) {
        this.EXTRA_COURSE_CODE = EXTRA_COURSE_CODE;
        return this;
    }

    public String getEXTRA_EXAMTYPE() {
        return EXTRA_EXAMTYPE;
    }

    public DownloadItem setEXTRA_EXAMTYPE(String EXTRA_EXAMTYPE) {
        this.EXTRA_EXAMTYPE = EXTRA_EXAMTYPE;
        return this;
    }

    public String getFNAME() {
        return FNAME;

    }

    public DownloadItem setFNAME(String FNAME) {
        this.FNAME = FNAME;
        return this;
    }

    public TYPE getTYPE() {
        return ITEM_TYPE;
    }

    public DownloadItem setTYPE(TYPE t) {
        this.ITEM_TYPE = t;
        return this;
    }

    public String getDNAME() {
        return DNAME;
    }

    public DownloadItem setDNAME(String DNAME) {
        this.DNAME = DNAME;
        return this;
    }

    public String getPATH() {
        return PATH;
    }

    public DownloadItem setPATH(String PATH) {
        this.PATH = PATH;
        return this;
    }

    public String getEXTENSION() {
        return EXTENSION;
    }

    public DownloadItem setEXTENSION(String EXTENSION) {
        this.EXTENSION = EXTENSION;
        return this;
    }


}
