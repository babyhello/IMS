package com.apps.ims;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Setting.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Setting extends Fragment implements SeekBar.OnSeekBarChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String EXTRA_TEXT = " more text to add when seekbar is seeked, when the text doesn't fit it resize it";
    private SeekBar mSeekBar;
    private TextView DemoText;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context GetBaseContext;

    private OnFragmentInteractionListener mListener;

    public Setting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Setting.
     */
    // TODO: Rename and change types and number of parameters
    public static Setting newInstance(String param1, String param2) {
        Setting fragment = new Setting();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
    }


    class CustomAdapter<T> extends ArrayAdapter<T> {
        public CustomAdapter(Context context, int textViewResourceId,
                             T[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (view instanceof TextView) {
                ((TextView) view).setTextSize(5);
            }
            return view;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(com.apps.ims.R.layout.fragment_setting, container, false);
        //宣告 ListView 元件'
        DemoText = (TextView) v.findViewById(com.apps.ims.R.id.txt_Setting_DemoText);
        mSeekBar = (SeekBar) v.findViewById(com.apps.ims.R.id.SKB_Font_Setting);
        mSeekBar.setMax(EXTRA_TEXT.length());
        mSeekBar.setOnSeekBarChangeListener(this);

        //DemoText.setVisibility(View.GONE);
        mSeekBar.setVisibility(View.GONE);

        GetBaseContext = getContext();


        String VersionExplain =

                "(Ver 43.0) 20170510 Update \n" + " 1.Update Issue Info Close、Priority、Ｏwner function" +
                        "\n" +
                        "\n" +

                        "(Ver 42.0) 20170504 Update \n" + " 1.Fix Project List Wrong Text" +
                        "\n" +
                        "\n" +

                        "(Ver 41.0) 20170425 Update \n" + " 1.Issue Info Add Close Type & Reason" +
                        "\n" +
                        "\n" +

                        "(Ver 40.0) 20170424 Update \n" + " 1.Issue Info Status Function" +
                        "\n 2.New Issue VideoViewer Change" +
                        "\n 3.Issue Info WorkNote Browser Photo Function" +
                        "\n" +
                        "\n" +

                        "(Ver 39.0) 20170419 Update \n" + " 1.Fix Verify Issue Error" +
                        "\n" +
                        "\n" +

                        "(Ver 38.0) 20170418 Update \n" + " 1.Fix Issue Verify,Fail,Close Not Send Notification Issue" +
                        "\n" +
                        "\n" +

                        "(Ver 37.0) 20170417 Update \n" + " 1.Close Issue Process finish" +
                        "\n" +
                        "\n" +

                        "(Ver 36.0) 20170415 Update \n" + " 1.Close Issue Info Alert Dialog" +
                        "\n 2.New Issue VideoViewer Change" +
                        "\n 3.New Issue Add Multi Select Photo Function" +
                        "\n" +
                        "\n" +

                        "(Ver 35.0) 20170413 Update \n" + " 1.Fix IssueInfo Send Message and Mail Loop Issue" +
                        "\n 2.Issue List Word Break Issue" +
                        "\n" +
                        "\n" +

                        "(Ver 34.0) 20170411 Update \n" + " 1.Fix IssueInfo Send Message and Mail Loop Issue" +
                        "\n" +
                        "\n" +

                        "(Ver 33.0) 20170411 Update \n" + " 1.Fix New Issue Microphone Not Show Progress" +
                        "\n 2.Issue Info Picture Used Thumbnail Picture" +
                        "\n 3.Issue Info VideoFile Set AutoPlay" +
                        "\n 4.Issue List IssueNo Remove 00" +
                        "\n" +
                        "\n" +

                        "(Ver 32.0) 20170410 Update \n" + " 1.Issue info photo set zoom in and zoom out function" +
                        "\n " +
                        "\n" +
                        "\n" +

                        "(Ver 31.0) 20170410 Update \n" + " 1.Fix Issue Info Send WorkNote Error" +
                        "\n 2.Fix Project Spec and Member NoData Issue" +
                        "\n" +
                        "\n" +

                        "(Ver 30.0) 20170410 Update \n" + " 1.Fix Issue Info Scroll Crash Issue" +
                        "\n 2. Change New Issue Subject bottom border" +
                        "\n" +
                        "\n" +

                        "(Ver 29.0) 20170407 Update \n" + " 1.Fix Title Font Size Issue " +
                        "\n 2. Change Priority Add Reason Function" +
                        "\n 3. Change Owner Add Reason Function" +
                        "\n 4. Notification Beta Online" +
                        "\n" +
                        "\n" +

                        "(Ver 28.0) 20170406 Update \n" + " 1.Fix Issue Info WorkNote Avatar Not Visible Issue " +
                        "\n 2. Fix Issue Info Video Play Issue" +
                        "\n 3. Fix Issue Info Crash Issue" +
                        "\n" +
                        "\n" +


                        "(Ver 27.0) 2017405 Update \n" + " 1.Fix Outof Memory Crash Issue " +
                        "\n" +
                        "\n" +

                        "(Ver 26.0) 20170327 Update \n" + " 1.Fix Issue Info Crash Issue " +
                        "\n" +
                        "\n" +

                        "(Ver 25.0) 20170327 Update \n" + " 1. Issue Mail List copy to Project Member List" +
                        "\n 2. Fix Issue List WorkNote Number Count Issue" +
                        "\n 3. Update WorkNote Font Size" +
                        "\n 4. Fix Project Spec Data Issue" +
                        "\n" +
                        "\n" +

                        "(Ver 24.0) 20170324 Update \n" + " 1. Edit Close Issue Style" +
                        "\n 2. Issue List Add Issue Author" +
                        "\n 3. Issue Info Add Issue Author & Owner" +
                        "\n 4. Issue List & My Issue (Read Count) Center" +
                        "\n 5. Fix Share Photo Subject MaxLength to 80" +
                        "\n 6. Issue Status Close Change Background Color" +
                        "\n 7. Fix Project Load More Hide" +
                        "\n" +
                        "\n" +


                        "(Ver 23.0) 20170323 Update \n" + " 1. Fix New Issue Subject Max Length" +
                        "\n 2. Fix Font Size" +
                        "\n 3. Fix Setting Scroll Error" +
                        "\n 4. Add Issue Close Function" +
                        "\n" +
                        "\n" +

                        "(Ver 22.0) 20170321 Update \n" + " 1. Fix Microphone Authority" +
                        "\n 2. Fix Project Font Size" +
                        "\n 3. Fix Project Collapse Error" +
                        "\n 4. Fix Project Sort Message" +
                        "\n 5. Fix Project Project Member Lost Issue" +
                        "\n 6. Fix Project Spec and Member No Data Issue" +
                        "\n 7. Fix NewIssue Remove Item Alert Number Message Issue" +
                        "\n 8. Fix Project Member ext. Overlap Issue" +
                        "\n 9. Fix Issue List Back to Project List ReLoad Issue" +
                        "\n 10. Fix New Issue Touch Back Button Alert Message" +
                        "\n" +
                        "\n" +

                        "(Ver 21.0) 20170315 Update \n" + "Project List Show All and Add filter function" +
                        "\n" +
                        "\n" +
                        "(Ver 20.0) 20170308 Update \n" + "fixed QRCode Vertical Issue" +
                        "\n" +
                        "\n" +
                        "(Ver 19.0) 20170306 Update \n" + "fixed project spec and member empty data" +
                        "\n" +
                        "\n" +
                        "(Ver 18.0) 20170303 Update \n update notification function";

        DemoText.setText(VersionExplain);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (!fromUser)
            return;

        DemoText.setText(EXTRA_TEXT.substring(0, progress));

        Settings.System.putFloat(GetBaseContext.getContentResolver(),
                Settings.System.FONT_SCALE, (float) 1.0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
