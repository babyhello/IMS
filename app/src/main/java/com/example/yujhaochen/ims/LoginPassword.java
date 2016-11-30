package com.example.yujhaochen.ims;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginPassword extends Activity {

    private ProgressDialog pDialog;
    UserData UserDataClass = new UserData();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_password);
        Button button = (Button) findViewById(R.id.Btn_Login_Password);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ValidationOutlook();
            }
        });

        EditText txt_Login_Account = (EditText) findViewById(R.id.txt_Login_Password);
        pDialog = new ProgressDialog(this);
        txt_Login_Account.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            ValidationOutlook();
                            return true;
                        case KeyEvent.KEYCODE_BREAK:
                            ValidationOutlook();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void GetAccountData(String Account, String Password) {

        pDialog.setMessage("Login...");

        pDialog.show();

        RequestQueue mQueue = Volley.newRequestQueue(this);

        String Path = GetServiceData.ServicePath + "/Authenticate?OutlookID=" + Account + "&OutlookPassword=" + Password;

        GetServiceData.getString(Path, mQueue, new GetServiceData.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {

                if (SetUserData(result)) {

                    UserDB UserDB = new UserDB(getApplicationContext());

                    UserDB.insert(UserDataClass);

                    Intent intent = new Intent(getBaseContext(), MainTab.class);

                    startActivity(intent);

                }
                else
                {
                    AppClass.AlertMessage("Validation Account",getBaseContext());

                }

                pDialog.hide();
            }
        });

    }

    private Boolean SetUserData(JSONObject UserResult) {

        Boolean Validate = false;

        try {
            if (UserResult != null) {

                JSONArray UserArray = new JSONArray(UserResult.getString("Key"));

                if (UserArray.length() > 0) {

                    String Account = UserArray.getJSONObject(0).getString("EnglishName");

                    String Password = "";

                    String WorkID = UserArray.getJSONObject(0).getString("WorkID");

                    String Name = UserArray.getJSONObject(0).getString("ChineseName");

                    String Phone = UserArray.getJSONObject(0).getString("Tel");

                    String Dept = UserArray.getJSONObject(0).getString("DeptName");

                    UserDataClass = new UserData(Account, Password, WorkID, Name, Phone, Dept,Account);




                }

                if (UserDataClass.Account != "") {
                    Validate = true;

                }
            }


        } catch (JSONException ex) {
            AppClass.AlertMessage(ex.getMessage(),getBaseContext());
        }


        return Validate;

    }


    private void ValidationOutlook() {

        EditText txt_Login_Account = (EditText) findViewById(R.id.txt_Login_Password);

        Bundle bundleAccount = this.getIntent().getExtras();

        String Account = bundleAccount.getString("Account");

        String Password = txt_Login_Account.getText().toString();

        GetAccountData(Account, Password);
    }
}
