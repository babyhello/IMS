package com.apps.ims;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainTab extends AppCompatActivity {


    static final int REQUEST_VIDEO_CAPTURE = 1;

    static final int REQUEST_IMAGE_CAPTURE = 2;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private ProgressDialog pDialog;
    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private Context mcontext;

    static final int PICK_FROM_GALLERY = 5;
    private File ImageFile;

    private File VideoFile;

    static boolean QR_Code_Checked = false;
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
            com.apps.ims.R.mipmap.btn_tab_projec_sel,
            com.apps.ims.R.mipmap.btn_tab_issue_sel,
            com.apps.ims.R.mipmap.tab_btn_notification_sel,
            com.apps.ims.R.mipmap.btn_tab_set_sel
    };

    private int[] UnSelectIcon = {
            com.apps.ims.R.mipmap.btn_tab_projec_nor,
            com.apps.ims.R.mipmap.btn_tab_issue_nor,
            com.apps.ims.R.mipmap.tab_btn_notification_nor,
            com.apps.ims.R.mipmap.btn_tab_set_nor
    };

    private RequestQueue mQueue;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 什麼都不用寫
        } else {
            // 什麼都不用寫
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.apps.ims.R.layout.activity_main_tab);

        Toolbar toolbar = (Toolbar) findViewById(com.apps.ims.R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(com.apps.ims.R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mcontext = MainTab.this;

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
                        setTitle("msi News+");
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


        final TabLayout tabLayout = (TabLayout) findViewById(com.apps.ims.R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

//                tabLayout.getTabAt(tab.getPosition()).setCustomView(prepareTabView("1",SelectIcon[tab.getPosition()]));
                View TabView = tab.getCustomView();

                prepareTabView(TabView, "0", SelectIcon[tab.getPosition()]);

                UserDB UserDB = new UserDB(MainTab.this);

                //如果進來程式有資料的話就不用再登入
                if (UserDB.getCount() > 0) {
                    UserData UserData = new UserData();

                    UserData = UserDB.getAll().get(0);

                    UserDB.UpdateLastTab(String.valueOf(tab.getPosition()), UserData.WorkID);
                }

                invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View TabView = tab.getCustomView();
                //tabLayout.getTabAt(tab.getPosition()).setCustomView(prepareTabView("1", UnSelectIcon[tab.getPosition()]));
                prepareTabView(TabView, "0", UnSelectIcon[tab.getPosition()]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            if (i == 2) {
                tabLayout.getTabAt(i).setCustomView(prepareTabView("0", UnSelectIcon[i]));
            } else {
                tabLayout.getTabAt(i).setCustomView(prepareTabView("0", UnSelectIcon[i]));
            }

        }

        UserDB UserDB = new UserDB(MainTab.this);

        int LastTab = 0;
        //如果進來程式有資料的話就不用再登入
        if (UserDB.getCount() > 0) {
            UserData UserData = new UserData();

            UserData = UserDB.getAll().get(0);

            if (!TextUtils.isEmpty(UserData.LastTab)) {
                LastTab = Integer.parseInt(UserData.LastTab);
            }

        }


        View TabView = tabLayout.getTabAt(LastTab).getCustomView();

        prepareTabView(TabView, "0", SelectIcon[LastTab]);

        tabLayout.getTabAt(LastTab).select();

        switch (LastTab) {
            case 0:
                setTitle("Project");
                break;
            case 1:
                setTitle("Issue");
                break;
            case 2:
                setTitle("msi News+");
                break;
            case 3:
                setTitle("Setting");
                break;

            default:
                //預設選project這頁
                setTitle("Project");

                View DefaultTabView = tabLayout.getTabAt(0).getCustomView();

                prepareTabView(DefaultTabView, "0", SelectIcon[0]);
        }

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );


        } else {
            GetServiceData.isUpdate(MainTab.this);
        }


        ImageView Img_Project_QRCode = (ImageView) findViewById(com.apps.ims.R.id.Img_Project_QRCode);

        Img_Project_QRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                QR_Code_Scan_Start();
            }
        });

        final String token = FirebaseInstanceId.getInstance().getToken();

        if (token != null && !TextUtils.isEmpty(UserData.WorkID)) {
            Insert_Device_Token(UserData.WorkID, token);
        }

    }

    private void Insert_Device_Token(String WorkID, String Token) {

        if (!TextUtils.isEmpty(WorkID) && !TextUtils.isEmpty(Token)) {

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(this);
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("WorkID", WorkID);
            map.put("Token", Token);

            String Path = GetServiceData.ServicePath + "/InsertUserToken";

            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                }

                @Override
                public void onSendRequestError(String result) {

                }

            }, map);
        }
    }

    private View prepareTabView(String text, int resId) {
        View view = LayoutInflater.from(this).inflate(com.apps.ims.R.layout.tabslayout, null);
        ImageView iv = (ImageView) view.findViewById(com.apps.ims.R.id.TabImageView);
        TextView tv = (TextView) view.findViewById(com.apps.ims.R.id.txt_Issue_WorkNoteCount);
        iv.setImageResource(resId);
        tv.setText(text);

        if (text.equals("0")) {
            tv.setVisibility(View.GONE);
        }
        return view;
    }

    private View prepareTabView(View TabView, String text, int resID) {
        ImageView iv = (ImageView) TabView.findViewById(com.apps.ims.R.id.TabImageView);
        TextView tv = (TextView) TabView.findViewById(com.apps.ims.R.id.txt_Issue_WorkNoteCount);
        iv.setImageResource(resID);
        tv.setText(text);

        if (text.equals("0")) {
            tv.setVisibility(View.GONE);
        }

        return TabView;
    }

    private void Go_To_New_Issue(String JsonString) {
        try {
            JSONObject QR_Object = new JSONObject(JsonString);

            JSONArray ProjectArray = new JSONArray(QR_Object.getString("Key"));

            if (ProjectArray.length() > 0) {


                JSONObject ProjectData = ProjectArray.getJSONObject(0);

                String Model_ID = String.valueOf(ProjectData.getInt("ModelID"));

                String ModelName = ProjectData.getString("ModelName");

                String Subject = ProjectData.getString("Subject");

                Bundle bundle = new Bundle();

                bundle.putString("ModelID", Model_ID);

                bundle.putString("ModelName", ModelName);

                bundle.putString("Subject", Subject);

                Intent intent = new Intent(MainTab.this, NewIssue.class);

                intent.putExtras(bundle);

                startActivity(intent);
            }

        } catch (JSONException ex) {

        }


    }

    private void Search_Project(String QR_Content) {

        if (QR_Content.contains("Subject")) {
            Go_To_New_Issue(QR_Content);
        } else {


            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(this);
            }

            String Path = GetServiceData.ServicePath + "/Find_Project_List_Search?ModelName=" + QR_Content;

            GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                    Go_To_Project(result);
                }
            });
        }
    }

    private void Go_To_Project(JSONObject ModelID) {
        try {
            JSONArray ProjectArray = new JSONArray(ModelID.getString("Key"));

            if (ProjectArray.length() > 0) {

                JSONObject ProjectData = ProjectArray.getJSONObject(0);

                String Model_ID = String.valueOf(ProjectData.getInt("ModelID"));

                String ModelName = ProjectData.getString("ModelName");

                Bundle bundle = new Bundle();

                bundle.putString("ModelID", Model_ID);

                bundle.putString("ModelName", ModelName);

                Intent intent = new Intent(MainTab.this, IssueList.class);

                intent.putExtras(bundle);

                startActivity(intent);
            } else {
                AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(this);

                MyAlertDialog.setMessage("Project Was Not Found!!!");

                MyAlertDialog.show();
            }

        } catch (JSONException ex) {

        }

    }

    private void QR_Code_Scan_Start() {

        try {

            QR_Code_Checked = true;

            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setCaptureActivity(CaptureActivityPortrait.class);
            //integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("Scan QR Code");
            integrator.setOrientationLocked(false);
            integrator.setBeepEnabled(false);
            integrator.initiateScan();

            //new IntentIntegrator(this).setOrientationLocked(false).setCaptureActivity(CustomScannerActivity.class).initiateScan();


        } catch (ActivityNotFoundException anfe) {

        }
    }

    private File configFileName(String prefix, String extension) {

        String fileName = FileUtil.getUniqueFileName();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        return new File(dir, prefix + fileName + extension);

    }


    private void GoCamera() {

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 無權限，向使用者請求
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE
            );


        } else {

            permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE
                );

            } else {
                Intent intentCamera =
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 照片檔案名稱
                File pictureFile = configFileName("P", ".jpg");

                ImageFile = pictureFile;

                Uri uri = Uri.fromFile(pictureFile);
                // 設定檔案名稱
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 啟動相機元件
                startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
            }
        }


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK && QR_Code_Checked == true) {

            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);


            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();

                Search_Project(scanContent);


                QR_Code_Checked = false;
            }
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {


            if (VideoFile.exists()) {

                //AddVideoItem(VideoFile.getAbsolutePath());

//                VideoView Vdo_Issue_File = (VideoView)findViewById(R.id.videoView);
//
//                Vdo_Issue_File.setVideoURI(Uri.parse(VideoFile.getAbsolutePath()));

                // System.out.println("Video Path" + NewIssueFile_List.get(position).GetVideoPath());
                VideoFile = null;
            }

        }
        // 如果照片檔案存在
        else if (resultCode == REQUEST_IMAGE_CAPTURE) {

            Bundle bundleAccount = data.getExtras();


            if (!TextUtils.isEmpty(bundleAccount.getString("FilePath"))) {


                Bitmap photo = FileUtil.FilePathGetBitMap(bundleAccount.getString("FilePath"));

                Bitmap minibm = ThumbnailUtils.extractThumbnail(photo, 1024, 800);

                SpeedNewIssue.ImgBmp = photo;

                SpeedNewIssue.ImagePath = bundleAccount.getString("FilePath");

                Intent intent = new Intent(MainTab.this, SpeedNewIssue.class);

                startActivity(intent);
            }

        } else {
            if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
//取得圖檔的路徑位置
                Uri uri = data.getData();
                //寫log

                //抽象資料的接口
                ContentResolver cr = this.getContentResolver();
                try {
                    //由抽象資料接口轉換圖檔路徑為Bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));


                    //File myFile = new File(uri.toString());

                    File MyNewFile = configFileName("P", ".jpg");


                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(MyNewFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                        SpeedNewIssue.ImgBmp = bitmap;

                        SpeedNewIssue.ImagePath = MyNewFile.getAbsolutePath();


                        Intent intent = new Intent(MainTab.this, SpeedNewIssue.class);

                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

//                    try {
//
//                        //copy(myFile,MyNewFile);
//
//
//
//                    } catch (IOException e) {
//                        Log.e("Exception", "File write failed: " + e.toString());
//                    }


                } catch (FileNotFoundException e) {
                    Log.e("Exception", e.getMessage(), e);
                }

            } else if (resultCode == RESULT_OK) {

                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();
                    String scanFormat = scanningResult.getFormatName();

                } else {


                }


            }
        }

    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
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
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        TabLayout tabLayout = (TabLayout) findViewById(com.apps.ims.R.id.tabs);

        MenuInflater inflater = getMenuInflater();

        int tab = tabLayout.getSelectedTabPosition();

        if (tab == 0)
            getMenuInflater().inflate(com.apps.ims.R.menu.menu_project_tab, menu);
//        else
//            getMenuInflater().inflate(R.menu.menu_main_tab, menu);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();

        TabLayout tabLayout = (TabLayout) findViewById(com.apps.ims.R.id.tabs);

        MenuInflater inflater = getMenuInflater();

        int tab = tabLayout.getSelectedTabPosition();

        if (tab == 0)
            getMenuInflater().inflate(com.apps.ims.R.menu.menu_project_tab, menu);
        else
            getMenuInflater().inflate(com.apps.ims.R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int item_id = item.getItemId();

        switch (item_id) {
//            case R.id.New_Issue:
//
//                Intent CameraIntent = new Intent(MainTab.this, VideoRecord.class);
//
//                Bundle bundle = new Bundle();
//
//                bundle.putString("FilePath", configFileName("P", ".jpg").getAbsolutePath());
//
//                CameraIntent.putExtras(bundle);
//
//                startActivityForResult(CameraIntent, REQUEST_IMAGE_CAPTURE);
//
//                break;
//            case R.id.GallyToNewIssue:
//                Intent i = new Intent(Intent.ACTION_PICK, null);
//                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(i, PICK_FROM_GALLERY);
//                break;
            case com.apps.ims.R.id.QRCode:
                QR_Code_Scan_Start();
                break;
            case com.apps.ims.R.id.ProjectSort:

                TabLayout tabLayout = (TabLayout) findViewById(com.apps.ims.R.id.tabs);

                MenuInflater inflater = getMenuInflater();

                int tab = tabLayout.getSelectedTabPosition();

                project_expandtable a = (project_expandtable) mSectionsPagerAdapter.getFragment(tab);

                if (a != null)
                {
                    a.Project_Sort();
                }
            case com.apps.ims.R.id.Contact:

                Intent intent = new Intent(this, ShareToNewIssue.class);

                // 呼叫「startActivity」，參數為一個建立好的Intent物件
                // 這行敘述執行以後，如果沒有任何錯誤，就會啟動指定的元件
                startActivity(intent);



            default:
                return false;
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

            View rootView = inflater.inflate(com.apps.ims.R.layout.fragment_project, container, false);

            switch (Position) {
                case 0:
                    rootView = inflater.inflate(com.apps.ims.R.layout.fragment_project, container, false);
                    return rootView;
                case 1:
                    rootView = inflater.inflate(com.apps.ims.R.layout.fragment_my_issue, container, false);
                    return rootView;
                case 2:
                    rootView = inflater.inflate(com.apps.ims.R.layout.fragment_notification, container, false);
                    return rootView;
                case 3:
                    rootView = inflater.inflate(com.apps.ims.R.layout.fragment_setting, container, false);
                    return rootView;
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private FragmentManager mFragmentManager;
        private Map<Integer, String> mFragmentTags;
        private int[] imageResId = {
                com.apps.ims.R.mipmap.btn_tab_projec_nor,
                com.apps.ims.R.mipmap.btn_tab_issue_nor,
                com.apps.ims.R.mipmap.tab_btn_notification_nor,
                com.apps.ims.R.mipmap.btn_tab_projec_nor
        };

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                // record the fragment tag here.
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentTags = new HashMap<Integer, String>();
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment TabFragment = new Fragment();

            switch (position) {
                case 0:
                    project_expandtable Project = new project_expandtable();

                    return Project.newInstance("0", "0");
                case 1:
                    MyIssue MyIssue = new MyIssue();
                    return MyIssue.newInstance("0", "0");
                case 2:
                    NotificationContent NotificationContent = new NotificationContent();
                    return NotificationContent.newInstance("0", "0");
                case 3:
                    Setting Setting = new Setting();
                    return Setting.newInstance("0", "0");

            }


            return TabFragment;
        }

        @Override
        public int getCount() {
            return 4;
        }


        public Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return mFragmentManager.findFragmentByTag(tag);
        }

    }


}
