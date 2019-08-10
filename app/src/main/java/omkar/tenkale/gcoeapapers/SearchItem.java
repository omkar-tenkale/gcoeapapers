package omkar.tenkale.gcoeapapers;

public class SearchItem {


    private  String SUBJECT_NAME ;
    private  String COURSE_CODE ;
    private  String EXAM_TYPE ;
    private  boolean IS_DOWNLOADED ;

    public String Get_SUBJECT_NAME ( )
    {
        return SUBJECT_NAME;
    }
    public String Get_COURSE_CODE ( )
    {
        return  COURSE_CODE;
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
        SUBJECT_NAME =S;
    }
    public void Set_COURSE_CODE (String S)
    {
        COURSE_CODE = S;
    }
    public void Set_EXAM_TYPE (String S )
    {
        EXAM_TYPE = S ;
    }
    public void Set_IS_DOWNLOADED(boolean b)
    {
        IS_DOWNLOADED = b;
    }

}
