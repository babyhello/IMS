package com.example.yujhaochen.ims;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SendNotification extends Activity {


    private EditText txt_Title;

    private EditText txt_Message;

    private TextView btn_Message_Send;

    private ProgressDialog pDialog;

    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        pDialog = new ProgressDialog(this);

        pDialog.setTitle("Loading...");

         txt_Title = (EditText)findViewById(R.id.txt_Title);

         txt_Message = (EditText)findViewById(R.id.txt_Message);

         btn_Message_Send = (Button)findViewById(R.id.btn_Message_Send);

        btn_Message_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Title = txt_Title.getText().toString();

                String Message = txt_Message.getText().toString();

                C_Comment_Insert(Title,Message);
            }
        });
    }

    private void C_Comment_Insert(String Title,String Message) {

        pDialog.show();


        if (!TextUtils.isEmpty(Title) && !TextUtils.isEmpty(Message)) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("Title", Title);
            map.put("Message", "Message");


            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(this);
            }
            String Path = GetServiceData.ServicePath + "/SendPushNotificationWTSC";


            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    AppClass.AlertMessage("Send Message Success!!",SendNotification.this);


                    pDialog.hide();
                }

                @Override
                public void onSendRequestError(String result) {
                    //Log.w("NotificationSuccess",result);
                }

            }, map);

        }


    }


}
