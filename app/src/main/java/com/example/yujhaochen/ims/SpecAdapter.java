package com.example.yujhaochen.ims;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class SpecAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;

    private List<Spec_Item> Spec_List;

    public SpecAdapter(Context context, List<Spec_Item> Spec_List)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.Spec_List = Spec_List;
    }




    @Override
    public int getCount() {
        return Spec_List.size();
    }

    @Override
    public Object getItem(int position) {
        return Spec_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mLayInf.inflate(R.layout.spec_item, parent, false);



        TextView txt_Spec_ComponentName = (TextView) v.findViewById(R.id.txt_Spec_ComponentName);
        TextView txt_Spec_ComponentContent = (TextView) v.findViewById(R.id.txt_Spec_ComponentContent);
        //Img_ProjectImage.setImageResource(Integer.valueOf(Project_List.get(position).GetImage().toString()));
        txt_Spec_ComponentName.setText(Spec_List.get(position).GetComponentName());
        txt_Spec_ComponentContent.setText(Spec_List.get(position).GetComponentContent());

        return v;
    }

}
