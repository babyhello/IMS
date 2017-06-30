package com.apps.ims;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by androids on 2016/10/21.
 */
public class MemberAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;

    private List<Member_Item> Member_List;

    private Context mcontext;

    public MemberAdapter(Context context, List<Member_Item> Member_List) {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.Member_List = Member_List;

        mcontext = context;
    }

    @Override
    public int getCount() {
        return Member_List.size();
    }

    @Override
    public Object getItem(int position) {
        return Member_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mLayInf.inflate(com.apps.ims.R.layout.member_item, parent, false);

        if (Member_List.get(position).GetSection()) {
            v = mLayInf.inflate(com.apps.ims.R.layout.member_section, parent, false);

            TextView txt_Member_Title = (TextView) v.findViewById(com.apps.ims.R.id.txt_Member_Title);

            txt_Member_Title.setText(Member_List.get(position).GetTitle());

        } else

        {

            ImageView Img_Member = (ImageView) v.findViewById(com.apps.ims.R.id.Img_Member);

            ImageView Img_Member_Phone = (ImageView) v.findViewById(com.apps.ims.R.id.Img_Member_Phone);

            TextView txt_Member_Name = (TextView) v.findViewById(com.apps.ims.R.id.txt_Member_Name);

            TextView txt_Member_Phone = (TextView) v.findViewById(com.apps.ims.R.id.txt_Member_Phone);
            //Img_ProjectImage.setImageResource(Integer.valueOf(Project_List.get(position).GetImage().toString()));

            GetServiceData.GetUserPhoto(mcontext, Member_List.get(position).GetWorkID(), Img_Member);

            txt_Member_Name.setText(Member_List.get(position).GetName());

            txt_Member_Phone.setText(Member_List.get(position).GetPhone());

            if (Member_List.get(position).GetPhone().matches("") || Member_List.get(position).GetPhone().matches("null")) {
                txt_Member_Phone.setVisibility(View.GONE);

                Img_Member_Phone.setVisibility(View.GONE);
            }
        }


        return v;
    }

}
