package com.example.yujhaochen.ims;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by yujhaochen on 2017/2/25.
 */

public class MyInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM", "Token:"+token);

        super.onTokenRefresh();
    }
}
