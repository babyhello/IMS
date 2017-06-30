package com.apps.ims;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by androids on 2017/2/25.
 */

public class MyInstanceIDService extends FirebaseInstanceIdService {


    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

        super.onTokenRefresh();
    }
}
