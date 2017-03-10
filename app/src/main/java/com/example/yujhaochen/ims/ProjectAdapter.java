package com.example.yujhaochen.ims;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.yujhaochen.ims.AppController.TAG;
import static com.example.yujhaochen.ims.R.id.imageView;
import static java.lang.System.load;

/**
 * Created by yujhaochen on 2016/10/21.
 */
public class ProjectAdapter extends BaseAdapter {

    private LayoutInflater mLayInf;

    private List<Project_Item> Project_List;

    private Context ProjectContext;

    public ProjectAdapter(Context context, List<Project_Item> Project_List)
    {
        mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ProjectContext = context;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = new View(ProjectContext);
        //如果專案是Focus的
        if (Project_List.get(position).GetFocusType())
        {

            ///AppClass.makeTextAndShow(ProjectContext,"Focus??",2);

            v = mLayInf.inflate(R.layout.project_item_focus, parent, false);
        }
        else {
            v = mLayInf.inflate(R.layout.project_item, parent, false);
        }

        final ImageView Img_ProjectImage = (ImageView) v.findViewById(R.id.Img_ProjectImage);

//        Img_ProjectImage.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View arg0) {
//
//                ItemClick(3);
//            }
//        });

//        Img_ProjectImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                if (!UserData.WorkID.matches(""))
//                {
//
//                    Insert_Forcus_Data(UserData.WorkID,"",Project_List.get(position).ModelID);
//
//                }
//
//                ProjectInfo.Project_Item = Project_List.get(position);
//
//                Intent intent = new Intent(ProjectContext, ProjectInfo.class);
//
//                ProjectContext.startActivity(intent);
//            }
//        });



        TextView txt_Project_Name = (TextView) v.findViewById(R.id.txt_Project_Name);

//        txt_Project_Name.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//
//                if (!UserData.WorkID.matches(""))
//                {
//
//                    Insert_Forcus_Data(UserData.WorkID,"",Project_List.get(position).ModelID);
//
//                }
//
//                Bundle bundle = new Bundle();
//
//                bundle.putString("ModelID", Project_List.get(position).GetModelID());
//
//                bundle.putString("ModelName", Project_List.get(position).GetName());
//
//                ProjectInfo.Project_Item = Project_List.get(position);
//
//                Intent intent = new Intent(ProjectContext, IssueList.class);
//
//                intent.putExtras(bundle);
//
//                ProjectContext.startActivity(intent);
//            }
//        });

        txt_Project_Name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {

                NewIssue(Project_List.get(position));

                return false;
            }
        });

        TextView txt_Issue_WorkNoteCount = (TextView) v.findViewById(R.id.txt_Issue_WorkNoteCount);

        if(!Project_List.get(position).GetRead().equals("0"))
        {
            txt_Issue_WorkNoteCount.setVisibility(View.VISIBLE);
        }
        else
        {
            txt_Issue_WorkNoteCount.setVisibility(View.GONE);
        }

        Glide.with(ProjectContext)
                .load(GetServiceData.ServicePath + "/Get_File?FileName=" + Project_List.get(position).GetImage())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(300, 300) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        Img_ProjectImage.setImageBitmap(AppClass.roundCornerImage(bitmap,30));
                    }
                });



        txt_Project_Name.setText(Project_List.get(position).GetName());



        return v;
    }

    private void NewIssue(Project_Item Project_Item) {
        Bundle bundle = new Bundle();

        bundle.putString("ModelID", Project_Item.GetModelID());

        bundle.putString("ModelName", Project_Item.GetName());

        Intent intent = new Intent();

        intent = new Intent(ProjectContext, NewIssue.class);

        intent.putExtras(bundle);

        ProjectContext.startActivity(intent);


    }

    private void Insert_Forcus_Data(String F_Keyin,String F_Owner,String F_PM_ID) {

        RequestQueue mQueue = Volley.newRequestQueue(ProjectContext);

        String Path = GetServiceData.ServicePath + "/Insert_Focus_Model" + "?F_Keyin=" + F_Keyin + "&F_Owner=" + F_Owner + "&F_PM_ID=" + F_PM_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                //PMData_Mapping(result);
            }
        });

    }
}
