package com.example.yujhaochen.ims;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class WorkNoteAdapter extends BaseAdapter {

    private final Context mcontext;

    private LayoutInflater mLayInf;

    private List<WorkNote_Item> WorkNote_List;

    public WorkNoteAdapter(Context context, List<WorkNote_Item> WorkNote_List)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mcontext = context;

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
        final ImageView Img_WorkNote = (ImageView) v.findViewById(R.id.Img_WorkNote);

        TextView txt_WorkNote_Author = (TextView) v.findViewById(R.id.txt_WorkNote_Author);
        TextView txt_WorkNote_Date = (TextView) v.findViewById(R.id.txt_WorkNote_Date);
        TextView txt_WorkNote_Content = (TextView) v.findViewById(R.id.txt_WorkNote_Content);

        final String ImagePath = GetServiceData.ServicePath + "/Get_File?FileName=" + WorkNote_List.get(position).GetFile();

        //GetServiceData.GetUserPhoto(WorkNote_List.get(position).GetAuthor_WorkID(),Img_WorkNote_Author);

        if(WorkNote_List.get(position).GetFile().equals(""))
        {
            Log.v("NoneText",WorkNote_List.get(position).GetFile());

            Img_WorkNote.setVisibility(View.GONE);
            txt_WorkNote_Content.setVisibility(View.VISIBLE);
            txt_WorkNote_Content.setText(WorkNote_List.get(position).GetContent());
        }
        else
        {
            final String FilePath = GetServiceData.ServicePath + "/Get_File?FileName=" + WorkNote_List.get(position).GetFile();

            Glide.with(mcontext)
                    .load(GetServiceData.ServicePath + "/Get_File?FileName=" + WorkNote_List.get(position).GetFile())
                    .centerCrop()
                    .placeholder(R.mipmap.progress_image)
                    .crossFade()
                    .into(Img_WorkNote);

            Glide.with(mcontext)
                    .load(GetServiceData.ServicePath + "/Get_File?FileName=" + "//172.16.111.114/File/SDQA/Code/Admin/" + WorkNote_List.get(position).GetAuthor_WorkID() + ".jpg")
                    .centerCrop()
                    .placeholder(R.mipmap.progress_image)
                    .crossFade()
                    .into(Img_WorkNote_Author);

            Img_WorkNote.setVisibility(View.VISIBLE);
            txt_WorkNote_Content.setVisibility(View.GONE);

            Img_WorkNote.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    imageZoom.ImagePath = FilePath;

                    Intent intent = new Intent(mcontext, imageZoom.class);

                    mcontext.startActivity(intent);

                }
            });
        }


        txt_WorkNote_Content.setText(WorkNote_List.get(position).GetContent());
        txt_WorkNote_Author.setText(WorkNote_List.get(position).GetAuthor());
        txt_WorkNote_Date.setText(WorkNote_List.get(position).GetDate());

        return v;
    }



}
