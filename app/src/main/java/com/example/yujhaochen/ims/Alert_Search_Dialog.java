package com.example.yujhaochen.ims;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Alert_Search_Dialog extends Dialog implements View.OnClickListener {

    public interface Dialog_Finish_Listener {

        void Finished();

    }

    private Dialog_Finish_Listener mDialog_Finish_Listener;
    public String Reason;
    private ListView list;
    private EditText filterText = null;
    ArrayAdapter<String> adapter = null;
    //private static final String TAG = "CityList";
    List<List_Item> _DataList = new ArrayList<List_Item>();
    String[] DataStringArray;
    public String SelectValue;
    public String SelectText;
    private Context mContext;
    public Alert_Search_Dialog(Context context, String Title, List<List_Item> DataList) {

        super(context);
        mContext = context;
        this._DataList = DataList;

        setContentView(R.layout.alert_search_dialog);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int screenWidth = (int) (metrics.widthPixels * 0.9);

        int screenHeight = (int) (metrics.heightPixels * 0.78);

        getWindow().setLayout(screenWidth, screenHeight);

        this.setTitle(Title);


        filterText = (EditText) findViewById(R.id.EditBox);
        filterText.addTextChangedListener(filterTextWatcher);
        list = (ListView) findViewById(R.id.List);
        DataStringArray = new String[DataList.size()];

        for (int i = 0; i < DataList.size(); i++) {
            DataStringArray[i] = DataList.get(i).GetText();
        }

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, DataStringArray);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                SelectValue = _DataList.get(position).GetValue();

                SelectText = _DataList.get(position).GetText();

                //onClick(v);
            }
        });

        final EditText txt_Reason = (EditText) findViewById(R.id.txt_Reason);

        txt_Reason.setBackgroundResource(R.drawable.edittextnormalcolor);

        Button Btn_Finish = (Button) findViewById(R.id.Btn_Finish);

        Btn_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(SelectValue)) {
                    if (!TextUtils.isEmpty(txt_Reason.getText().toString())) {
                        if (mDialog_Finish_Listener != null) {
                            mDialog_Finish_Listener.Finished();

                            dismiss();
                        }
                    } else {
                        txt_Reason.setBackgroundResource(R.drawable.edittextwarningcolor);
                    }

                }


            }
        });
    }

    public void SetOnDialog_Finish_Listener(Dialog_Finish_Listener eventListener) {
        mDialog_Finish_Listener = eventListener;
    }

    public String GetReason() {
        EditText txt_Reason = (EditText) findViewById(R.id.txt_Reason);

        return txt_Reason.getText().toString();
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter.getFilter().filter(s);
        }
    };

    @Override
    public void onStop() {
        filterText.removeTextChangedListener(filterTextWatcher);
    }
}