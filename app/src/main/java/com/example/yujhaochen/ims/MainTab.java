package com.example.yujhaochen.ims;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainTab extends AppCompatActivity {

    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private int[] SelectIcon = {
            R.mipmap.tab_btn_issue_sel,
            R.mipmap.btn_tab_issue_sel,
            R.mipmap.tab_btn_notification_sel,
            R.mipmap.tab_btn_set_sel
    };

    private int[] UnSelectIcon = {
            R.mipmap.tab_btn_issue_nor,
            R.mipmap.btn_tab_issue_nor,
            R.mipmap.tab_btn_notification_nor,
            R.mipmap.tab_btn_set_nor
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setTitle("Project");
                        break;
                    case 1:
                        setTitle("Issue");
                        break;
                    case 2:
                        setTitle("Notification");
                        break;
                    case 3:
                        setTitle("Setting");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


       TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                tab.setIcon(SelectIcon[tab.getPosition()]);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(UnSelectIcon[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        for(int i=0; i < tabLayout.getTabCount() ; i++){

            tabLayout.getTabAt(i).setIcon(UnSelectIcon[i]);
        }

        //預設選project這頁
        setTitle("Project");

        tabLayout.getTabAt(0).setIcon(SelectIcon[0]);

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this ,
                    new String[] { WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );


        }else{
            GetServiceData.isUpdate(this);
        }


        ImageView Img_Project_QRCode = (ImageView) findViewById(R.id.Img_Project_QRCode);

        Img_Project_QRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                QR_Code_Scan_Start();
            }
        });
    }


    private void QR_Code_Scan_Start() {

        try {

            IntentIntegrator scanIntegrator = new IntentIntegrator(this);

            scanIntegrator.setOrientationLocked(false);



            scanIntegrator.initiateScan();

        } catch (ActivityNotFoundException anfe) {
            System.out.println(anfe);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();

                //System.out.println(scanContent);
                Search_Project(scanContent);

            }
        }

    }

    private void Search_Project(String QR_Content) {

        if(QR_Content.contains("Subject"))
        {
            Go_To_New_Issue(QR_Content);
        }
        else
        {
            RequestQueue mQueue = Volley.newRequestQueue(this);

            String Path = GetServiceData.ServicePath + "/Find_Project_List_Search?ModelName=" + QR_Content;

            GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                    Go_To_Project(result);
                }
            });
        }
    }

    private void Go_To_New_Issue(String JsonString)
    {
        try
        {
            JSONObject QR_Object = new JSONObject(JsonString);

            JSONArray ProjectArray = new JSONArray(QR_Object.getString("Key"));

            if (ProjectArray.length() > 0 )
            {
                System.out.println("Length" + ProjectArray.length());

                JSONObject ProjectData = ProjectArray.getJSONObject(0);

                String Model_ID = String.valueOf(ProjectData.getInt("ModelID"));

                String ModelName = ProjectData.getString("ModelName");

                String Subject = ProjectData.getString("Subject");

                Bundle bundle = new Bundle();

                bundle.putString("ModelID", Model_ID);

                bundle.putString("ModelName", ModelName);

                bundle.putString("Subject", Subject);

                Intent intent = new Intent(this,NewIssue.class);

                intent.putExtras(bundle);

                startActivity(intent);
            }

        }
        catch (JSONException ex)
        {

        }


    }

    private void Go_To_Project(JSONObject ModelID)
    {
        try
        {
            JSONArray ProjectArray = new JSONArray(ModelID.getString("Key"));

            if (ProjectArray.length() > 0 )
            {
                System.out.println("Length" + ProjectArray.length());

                JSONObject ProjectData = ProjectArray.getJSONObject(0);

                String Model_ID = String.valueOf(ProjectData.getInt("ModelID"));

                String ModelName = ProjectData.getString("ModelName");

                Bundle bundle = new Bundle();

                bundle.putString("ModelID", Model_ID);

                bundle.putString("ModelName", ModelName);

                Intent intent = new Intent(this,IssueList.class);

                intent.putExtras(bundle);

                startActivity(intent);
            }
            else
            {
                AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(this);

                MyAlertDialog.setMessage("Project Was Not Found!!!");

                MyAlertDialog.show();
            }

        }
        catch (JSONException ex)
        {

        }


//        Bundle bundle = new Bundle();
//
//        bundle.putString("ModelID", scanContent);
//
//        bundle.putString("ModelName", Project_Item.GetName());
//
//        Intent intent = new Intent();
//
//        intent.putExtras(bundle);
//
//        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GetServiceData.isUpdate(this);

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int Position = getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView =  inflater.inflate(R.layout.fragment_project, container, false);

                        switch (Position) {
                case 0:
                    rootView =  inflater.inflate(R.layout.fragment_project, container, false);
                    return rootView;
                case 1:
                    rootView =  inflater.inflate(R.layout.fragment_my_issue, container, false);
                    return rootView;
                case 2:
                    rootView =  inflater.inflate(R.layout.fragment_notification, container, false);
                    return rootView;
                case 3:
                    rootView =  inflater.inflate(R.layout.fragment_setting, container, false);
                    return rootView;
            }


//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int[] imageResId = {
                R.mipmap.tab_btn_issue_nor,
                R.mipmap.btn_tab_issue_nor,
                R.mipmap.tab_btn_notification_nor,
                R.mipmap.tab_btn_set_nor
        };

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment TabFragment = new Fragment() ;

            switch (position) {
                case 0:
                    Project Project = new Project();
                    return  Project.newInstance("0","0");
                case 1:
                    MyIssue MyIssue = new MyIssue();
                    return MyIssue.newInstance("0","0");
                case 2:
                    Notification Notification = new Notification();
                    return Notification.newInstance("0","0");
                case 3:
                    Setting Setting = new Setting();
                    return Setting.newInstance("0","0");
            }


            return TabFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//
//            Drawable image = ContextCompat.getDrawable(getApplicationContext(), imageResId[position]);
//            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//            SpannableString sb = new SpannableString(" ");
//            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//            return sb;
//        }
    }
}
