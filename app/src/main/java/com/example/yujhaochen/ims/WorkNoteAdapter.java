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
public class WorkNoteAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;

    private List<WorkNote_Item> WorkNote_List;

    public WorkNoteAdapter(Context context, List<WorkNote_Item> WorkNote_List)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.WorkNote_List = WorkNote_List;
    }
    @Override
    public int getCount() {
        return WorkNote_List.size();
    }

    @Override
    public Object getItem(int position) {
        return WorkNote_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mLayInf.inflate(R.layout.worknote_item, parent, false);

        ImageView Img_WorkNote_Author = (ImageView) v.findViewById(R.id.Img_WorkNote_Author);

        TextView txt_WorkNote_Author = (TextView) v.findViewById(R.id.txt_WorkNote_Author);
        TextView txt_WorkNote_Date = (TextView) v.findViewById(R.id.txt_WorkNote_Date);
        TextView txt_WorkNote_Content = (TextView) v.findViewById(R.id.txt_WorkNote_Content);
        //Img_ProjectImage.setImageResource(Integer.valueOf(Project_List.get(position).GetImage().toString()));
        GetServiceData.GetUserPhoto(WorkNote_List.get(position).GetAuthor_WorkID(),Img_WorkNote_Author);
        txt_WorkNote_Author.setText(WorkNote_List.get(position).GetAuthor());
        txt_WorkNote_Date.setText(WorkNote_List.get(position).GetDate());
        txt_WorkNote_Content.setText(WorkNote_List.get(position).GetContent());

        return v;
    }

}
