package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class myFragmentAdapter extends FragmentPagerAdapter {
     private Download d ;
    private Available a ;
     ArrayList<Fragment> list ;
     Context mcontext ;
    public myFragmentAdapter(FragmentManager fm, Context c) {
        super(fm);
        mcontext = c ;
       // list = new ArrayList() ;
        list = ((MainActivity)c).fragments;
       d = new Download() ;
       a = new Available();
    }
 //list.get(0)==null
    @Override
    public Fragment getItem(int position) {
       switch (position)
        {   case  0 :
            if (list.size()==0){ Feed f =new Feed(); list.add(0 ,f) ; return f;}
             else{return list.get(0);}
           // return new Feed();

            case  1 :
               if (list.size()==1){ Papers f =new Papers(); list.add(1 ,f) ; return f;}
               else{return list.get(1);}
               // return new Papers();

            case  2 :
               //  Download d = new Download();
               if (list.size()==2){ list.add(2 ,d) ; return d;}
              else{return list.get(2);}

              // return d;

            case  3 :
                if (list.size()==3){  list.add(3 ,a) ; return a;}
              else{return list.get(3);}
                //return new Available();

            case  4 :
               if (list.size()==4){ About f =new About(); list.add(4 ,f) ; return f;}
               else{return list.get(4);}
               // return new About();

        }return null ;
    }

    @Override
    public int getCount() {
        return 5;
    }

    public Download getD()
    {

        return  d;
    }

    public Available getA()
    {
        return  a;
    }

}
