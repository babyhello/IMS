package com.apps.ims;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IssueGallery extends Activity {

    //final int[] imagesId = new int[]{R.drawable.chrysanthemum, R.drawable.desert, R.drawable.penguins, R.drawable.tulips};


    List<IssueFile_Item> IssueFile_List = new ArrayList<IssueFile_Item>();
    public static List<ImageView> ImageViewList = new ArrayList<ImageView>();
    ImageSwitcher imageSwitcher;
    Gallery gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.apps.ims.R.layout.activity_issue_gallery);

        imageSwitcher = (ImageSwitcher) findViewById(com.apps.ims.R.id.imageSwitcher);

        gallery = (Gallery) findViewById(com.apps.ims.R.id.gallery);

        //為ImageSwitcher設置ViewFactory，用來處理圖片切換的顯示
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());

                return imageView;
            }
        });
        //為ImageSwitcher設置淡入淡出動畫
        imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));

        imageSwitcher.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));

        imageSwitcher.setImageResource(com.apps.ims.R.mipmap.project_default);
        //為Gallery設置Adapter以讓Gallery顯示圖片
        gallery.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return ImageViewList.size();
            }

            @Override
            public Object getItem(int position) {
                return ImageViewList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView = new ImageView(getApplicationContext());

                imageView = ImageViewList.get(position);

                //設定圖片尺寸等比例縮放
                imageView.setAdjustViewBounds(true);
                imageView.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT));
                return imageView;
            }
        });
        //為Gallery設置一個OnItemSelectedListener，置於中間的縮圖為被Selected
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //將對應選到的縮圖的大圖放置於ImageSwitcher中
//                    ImageView Img = new ImageView(getBaseContext());
//                    GetServiceData.GetImageByImageLoad(IssueFile_List.get(position).GetImage(),Img);
//
//

                BitmapDrawable BitmapDrawable = ((BitmapDrawable) ImageViewList.get(position).getDrawable());
                System.out.println(BitmapDrawable);
                //imageSwitcher.setImageResource(bitmap);
                imageSwitcher.setImageDrawable(BitmapDrawable);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    private void Issue_File_List(String Issue_ID) {

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Issue_File_List?F_SeqNo=" + Issue_ID;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                IssueInfoFile_ListMapping(result);
            }
        });


    }

    private void IssueInfoFile_ListMapping(JSONObject result) {
        try {
            IssueFile_List.clear();

            JSONArray UserArray = new JSONArray(result.getString("Key"));


            //取得ImageSwitcher和Gallery


            for (int i = 0; i < UserArray.length(); i++) {
                JSONObject IssueData = UserArray.getJSONObject(i);

                String F_DownloadFilePath = IssueData.getString("F_DownloadFilePath");

                IssueFile_List.add(i, new IssueFile_Item(F_DownloadFilePath, "", "", ""));

                ImageView IV = new ImageView(this);

                GetServiceData.GetImageByImageLoad(F_DownloadFilePath, IV);

                ImageViewList.add(i, IV);

            }


        } catch (JSONException ex) {

        }


    }

}
