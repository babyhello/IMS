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
public class ProjectAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;

    private List<Project_Item> Project_List;

    public ProjectAdapter(Context context, List<Project_Item> Project_List)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.Project_List = Project_List;
    }
    @Override
    public int getCount() {
        return Project_List.size();
    }

    @Override
    public Object getItem(int position) {
        return Project_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mLayInf.inflate(R.layout.project_item, parent, false);

        ImageView Img_ProjectImage = (ImageView) v.findViewById(R.id.Img_ProjectImage);

        TextView txt_Project_Name = (TextView) v.findViewById(R.id.txt_Project_Name);

        //Img_ProjectImage.setImageResource(Integer.valueOf(Project_List.get(position).GetImage().toString()));

        GetServiceData.GetImageByImageLoad(Project_List.get(position).GetImage(),Img_ProjectImage,R.mipmap.project_default,R.mipmap.project_default);

        txt_Project_Name.setText(Project_List.get(position).GetName());

        return v;
    }

}
