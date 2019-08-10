package omkar.tenkale.gcoeapapers;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends ArrayAdapter {
    String[] array ;
    Context mContext;

    public SpinnerAdapter(@NonNull Context context, int resource, String[] array) {
        super(context, resource);
        this.array = array;
        this.mContext = context;
    }



    @Override
    public int getCount() {
        return array.length;
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
        View v = (LayoutInflater.from(mContext)).inflate(R.layout.spinner_item,null,false);
        ((TextView)v.findViewById(R.id.spinner_text)).setText(array[position]);
        return v;
    }
}
