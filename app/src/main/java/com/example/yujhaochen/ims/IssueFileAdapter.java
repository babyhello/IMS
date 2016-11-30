package com.example.yujhaochen.ims;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
public class IssueFileAdapter extends RecyclerView.Adapter<IssueFileAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<IssueFile_Item> mDatas;
    public IssueFileAdapter(Context context, List<IssueFile_Item> datats)
    {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View arg0)
        {
            super(arg0);
        }
        ImageView mImg;
    }
    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }
    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.issuefile_item,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mImg = (ImageView) view
                .findViewById(R.id.Img_IssueFile_Image);



        return viewHolder;
    }
    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        GetServiceData.GetImageByImageLoad(mDatas.get(i).GetImage(),viewHolder.mImg);
    }

}

