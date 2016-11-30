package com.example.yujhaochen.ims;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class IssueAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;

    private List<Issue_Item> Issue_List;

    public IssueAdapter(Context context, List<Issue_Item> Issue_List)
    {
        if (context != null)
        {
            mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //mLayInf = ;

        this.Issue_List = Issue_List;
    }
    @Override
    public int getCount() {
        return Issue_List.size();
    }

    @Override
    public Object getItem(int position) {
        return Issue_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mLayInf.inflate(R.layout.issue_item, parent, false);

        ImageView Img_Priority = (ImageView) v.findViewById(R.id.Img_Priority);

        TextView txt_Issue_Project_Name = (TextView) v.findViewById(R.id.txt_Issue_Project_Name);

        TextView txt_Issue_Date = (TextView) v.findViewById(R.id.txt_Issue_Date);

        TextView txt_Issue_Subject = (TextView) v.findViewById(R.id.txt_Issue_Subject);

        TextView txt_Issue_WorkNoteCount = (TextView) v.findViewById(R.id.txt_Issue_WorkNoteCount);

//        GetServiceData.GetImageByImageLoad("http://172.16.111.114/File/SDQA/Code/Admin/10010670.jpg",Img_Priority);
        //Img_Priority.setImageResource(Integer.valueOf(Project_List.get(position).GetImage().toString()));

        txt_Issue_Project_Name.setText(Issue_List.get(position).GetProjectName());

        txt_Issue_Date.setText(Issue_List.get(position).GetDate());

        txt_Issue_Subject.setText(Issue_List.get(position).GetSubject());

        if(Issue_List.get(position).GetWorkNoteCount().equals("0"))
        {
            txt_Issue_WorkNoteCount.setVisibility(View.GONE);
        }
        else
        {
            txt_Issue_WorkNoteCount.setText(Issue_List.get(position).GetWorkNoteCount());
        }

        Img_Priority.setImageResource(AppClass.PriorityImage(Issue_List.get(position).GetPriority()));

        return v;
    }


}
