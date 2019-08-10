package omkar.tenkale.gcoeapapers;



public class DownloadingItem {

private  String DOWNLOAD_TITLE ;
private  int DOWNLOAD_ID ;
private  String EXAM_TYPE ;
private  boolean IS_DOWNLOADED ;

public String Get_SUBJECT_NAME ( )
    {
        return DOWNLOAD_TITLE;
    }
    public int Get_COURSE_CODE ( )
    {
        return  DOWNLOAD_ID;
    }
    public String Get_EXAM_TYPE ( )
    {
        return  EXAM_TYPE;
    }
    public boolean Get_IS_DOWNLOADED()
    {
        return  IS_DOWNLOADED;
    }

    public void Set_SUBJECT_NAME (String S )
    {
        DOWNLOAD_TITLE = S ;
    }
    public void Set_COURSE_CODE (int S)
    {
        DOWNLOAD_ID = S;
    }
    public void Set_EXAM_TYPE (String S )
    {
       EXAM_TYPE = S ;
    }
    public void Set_IS_DOWNLOADED(boolean b)
    {
       IS_DOWNLOADED = b;
    }

//************************************************           don't think i'll need this
}
