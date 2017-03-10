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
public class NotificationAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;

    private List<Notification_Item> Notification_List;

    public NotificationAdapter(Context context, List<Notification_Item> Notification_List)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.Notification_List = Notification_List;
    }
    @Override
    public int getCount() {
        return Notification_List.size();
    }

    @Override
    public Object getItem(int position) {
        return Notification_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mLayInf.inflate(R.layout.notification_item, parent, false);

        ImageView Img_Notification_Author = (ImageView) v.findViewById(R.id.Img_Notification_Author);


        TextView txt_Notification_Title = (TextView) v.findViewById(R.id.txt_Notification_Title);
        TextView txt_Notification_Content = (TextView) v.findViewById(R.id.txt_Notification_Content);
        TextView txt_Notification_Date = (TextView) v.findViewById(R.id.txt_Notification_Date);
        TextView txt_Notification_AuthorName = (TextView) v.findViewById(R.id.txt_Notification_AuthorName);

//        GetServiceData.GetUserPhoto(Notification_List.get(position).GetAuthor_WorkID(),Img_Notification_Author);

        txt_Notification_Title.setText(Notification_List.get(position).GetTitle());
        txt_Notification_Content.setText(Notification_List.get(position).GetContent());
        txt_Notification_Date.setText(Notification_List.get(position).GetDate());
        txt_Notification_AuthorName.setText(Notification_List.get(position).GetAuthor_Name());

        return v;
    }

}
