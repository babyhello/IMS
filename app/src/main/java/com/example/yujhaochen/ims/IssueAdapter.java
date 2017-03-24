package com.example.yujhaochen.ims;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private String AdapterType;

    private Context mContext;

    public IssueAdapter(Context context, List<Issue_Item> Issue_List, String AdapterType) {
        this.AdapterType = AdapterType;

        if (context != null) {
            mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        mContext = context;

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

        TextView IssueList_Author = (TextView) v.findViewById(R.id.IssueList_Author);

//        GetServiceData.GetImageByImageLoad("http://172.16.111.114/File/SDQA/Code/Admin/10010670.jpg",Img_Priority);
        //Img_Priority.setImageResource(Integer.valueOf(Project_List.get(position).GetImage().toString()));

        if (AdapterType.equals("MyIssue")) {

            if (Issue_List.get(position).GetProjectName().toLowerCase().contains("ms-"))
            {
                txt_Issue_Project_Name.setText(Issue_List.get(position).GetProjectName());
            }
            else
            {
                txt_Issue_Project_Name.setText("MS-" + Issue_List.get(position).GetProjectName());
            }

        } else {
            txt_Issue_Project_Name.setText("#" + Issue_List.get(position).GetID());
        }

        txt_Issue_Date.setText(Issue_List.get(position).GetDate());

        txt_Issue_Subject.setText(Issue_List.get(position).GetSubject());

        IssueList_Author.setText(Issue_List.get(position).GetAuthor());

        if (Issue_List.get(position).GetRead().equals("0")) {
            txt_Issue_WorkNoteCount.setText("N");

            txt_Issue_WorkNoteCount.setTextColor(Color.parseColor("#ffffff"));
        } else {
            if (Issue_List.get(position).GetWorkNoteCount().equals("0")) {
                txt_Issue_WorkNoteCount.setVisibility(View.GONE);
            } else {
                txt_Issue_WorkNoteCount.setText(Issue_List.get(position).GetWorkNoteCount());
            }
        }


        Img_Priority.setImageResource(AppClass.PriorityImage(Issue_List.get(position).GetPriority()));

        LinearLayout IssueList_Background = (LinearLayout) v.findViewById(R.id.IssueList_Background);

        if (Issue_List.get(position).GetIssueStatus().equals("3")) {
            IssueList_Background.setBackgroundColor(mContext.getResources().getColor(R.color.Issue_Status));
        }

        return v;
    }


}
