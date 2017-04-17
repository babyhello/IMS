package com.example.yujhaochen.ims;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private Context WorkNotecontext;

    private LayoutInflater mLayInf;

    private List<WorkNote_Item> WorkNote_List;

    public WorkNoteAdapter(Context context, List<WorkNote_Item> WorkNote_List)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WorkNotecontext = context;

        this.WorkNote_List = WorkNote_List;
    }


    @Override
    public int getCount() {
        return WorkNote_List.size();
    }

    @Override
    public Object getItem(int position) {

        if (WorkNote_List.size() == 0) {
            return null;
        }

        return WorkNote_List.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = mLayInf.inflate(R.layout.worknote_item, parent, false);

        final ImageView Img_WorkNote_Author = (ImageView) v.findViewById(R.id.Img_WorkNote_Author);
        final ImageView Img_WorkNote = (ImageView) v.findViewById(R.id.Img_WorkNote);

        TextView txt_WorkNote_Author = (TextView) v.findViewById(R.id.txt_WorkNote_Author);
        TextView txt_WorkNote_Date = (TextView) v.findViewById(R.id.txt_WorkNote_Date);
        TextView txt_WorkNote_Content = (TextView) v.findViewById(R.id.txt_WorkNote_Content);

        final String ImagePath = GetServiceData.ServicePath + "/Get_File?FileName=" + WorkNote_List.get(position).GetFile();


        if(WorkNote_List.get(position).GetFile().equals(""))
        {


            Img_WorkNote.setVisibility(View.GONE);
            txt_WorkNote_Content.setVisibility(View.VISIBLE);


            txt_WorkNote_Content.setText(Html.fromHtml(WorkNote_List.get(position).GetContent()));
        }
        else
        {
            String _ImagePath = WorkNote_List.get(position).GetFile();


            if (_ImagePath.toLowerCase().contains("img")) {
                _ImagePath = GetServiceData.getUrlFromImgTag(_ImagePath);
            }


            Glide
                    .with(WorkNotecontext)
                    .load(ImagePath)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.progress_image)
                    .into(new SimpleTarget<Bitmap>(100, 100) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                            Img_WorkNote.setImageBitmap(resource);

                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        }
                    });





            Img_WorkNote.setVisibility(View.VISIBLE);
            txt_WorkNote_Content.setVisibility(View.GONE);

            Img_WorkNote.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    imageZoom.ImagePath = ImagePath;

                    Intent intent = new Intent(WorkNotecontext, imageZoom.class);

                    WorkNotecontext.startActivity(intent);

                }
            });
        }

        Glide
                .with(WorkNotecontext)
                .load(GetServiceData.ServicePath + "/Get_File?FileName=" + "//172.16.111.114/File/SDQA/Code/Admin/" + WorkNote_List.get(position).GetAuthor_WorkID() + ".jpg")
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.progress_image)
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                        Img_WorkNote_Author.setImageBitmap(resource);

                    }
                });
        txt_WorkNote_Content.setText(WorkNote_List.get(position).GetContent());
        txt_WorkNote_Author.setText(WorkNote_List.get(position).GetAuthor());
        txt_WorkNote_Date.setText(WorkNote_List.get(position).GetDate());

        return v;
    }



}
