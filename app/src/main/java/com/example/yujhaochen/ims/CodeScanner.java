package com.example.yujhaochen.ims;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

/**
 * 條碼掃描
 *
 * @author magiclen
 *
 */
public class CodeScanner {

    // -----類別常數-----
    private static final String SCAN_MODES = "SCAN_MODE";
    private static final String USE_CROP = "USE_CROP";
    private static final String SCAN_PREFIX = "SCAN_PREFIX";

    private static final String[] STRING_INSTALL_TITLE = new String[] { "Install Barcode Scanner", "安裝條碼掃描器" };
    private static final String[] STRING_INSTALL_CLOSE = new String[] { "Close", "關閉" };
    private static final String[] STRING_INSTALL_INSTALL = new String[] { "Install", "安裝" };
    private static final String[] STRING_INSTALL_MESSAGE = new String[] { "In order to scan barcode, you neet to install Easy Barcode Scanner first.", "為了能夠掃描條碼，您需要一個簡易條碼掃描器。" };
    private static final String[] STRING_URL_EASY_BARCODE_SCANNER = new String[] { "https://play.google.com/store/apps/details?id=org.magiclen.barcodescanner" };

    // -----類別列舉-----
    public static enum Mode {
        PRODUCT_MODE, SCAN_MODE, ONE_D_MODE, TWO_D_MODE, QR_CODE_MODE, DATA_MATRIX_MODE;
    }

    // -----類別介面-----
    public static interface CodeReaderListener {
        public void codeReadResult(final String type, final String data);
    }

    // -----類別變數-----
    private static boolean chinese = isChinese();

    // -----類別方法-----
    private static boolean isChinese() {
        final Locale locale = Locale.getDefault();
        return locale.equals(Locale.CHINESE) || locale.equals(Locale.SIMPLIFIED_CHINESE) || locale.equals(Locale.TRADITIONAL_CHINESE);
    }

    private static String getString(final String[] string) {
        int index = chinese ? 1 : 0;
        if (index >= string.length) {
            index = 0;
        }
        return string[index];
    }

    private static int ACTIVITY_SCANNER = 9974;

    // -----物件變數-----
    private Activity activity;
    private Fragment fragment;
    private CodeReaderListener codeReaderListener;
    private Mode mode = Mode.SCAN_MODE;
    private boolean crop = false;
    private String scanPrefix = null;

    // -----建構子-----
    public CodeScanner(final Activity activity, final CodeReaderListener codeReaderListener) {
        this.activity = activity;
        this.codeReaderListener = codeReaderListener;
    }

    public CodeScanner(final Fragment fragment, final CodeReaderListener codeReaderListener) {
        this(fragment.getActivity(), codeReaderListener);
        this.fragment = fragment;
    }

    // -----物件方法-----
    public Activity getActivity() {
        if (activity != null) {
            return activity;
        } else {
            return null;
        }
    }

    public CodeReaderListener getCodeReaderListener() {
        return codeReaderListener;
    }

    public void setCrop(final boolean crop) {
        this.crop = crop;
    }

    public boolean getCrop() {
        return crop;
    }

    public void setMode(final Mode mode) {
        if (mode != null) {
            this.mode = mode;
        } else {
            this.mode = Mode.SCAN_MODE;
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setScanPrefix(final String scanPrefix) {
        this.scanPrefix = scanPrefix;
    }

    public String getScanPrefix() {
        return scanPrefix;
    }

    public boolean scan() {
        final PackageManager packageManager = activity.getPackageManager();
        final Intent easyBarcodeScanner = new Intent("org.magiclen.barcodescanner.SCAN");
        easyBarcodeScanner.setPackage("org.magiclen.barcodescanner");
        final List<ResolveInfo> list = packageManager.queryIntentActivities(easyBarcodeScanner, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            easyBarcodeScanner.putExtra(SCAN_MODES, mode.toString());
            easyBarcodeScanner.putExtra(USE_CROP, crop);
            easyBarcodeScanner.putExtra(SCAN_PREFIX, scanPrefix);
            if (fragment != null) {
                fragment.startActivityForResult(easyBarcodeScanner, ACTIVITY_SCANNER);
            } else {
                activity.startActivityForResult(easyBarcodeScanner, ACTIVITY_SCANNER);
            }
            return true;
        } else {
            new AlertDialog.Builder(activity).setTitle(getString(STRING_INSTALL_TITLE)).setMessage(getString(STRING_INSTALL_MESSAGE)).setPositiveButton(getString(STRING_INSTALL_INSTALL), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    final String url = getString(STRING_URL_EASY_BARCODE_SCANNER);
                    final Intent openURLIntent = new Intent(Intent.ACTION_VIEW);
                    openURLIntent.setData(Uri.parse(url));
                    final List<ResolveInfo> list = packageManager.queryIntentActivities(openURLIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() > 0) {
                        final Intent destIntent = Intent.createChooser(openURLIntent, null);
                        activity.startActivity(destIntent);
                    }
                }

            }).setNegativeButton(getString(STRING_INSTALL_CLOSE), null).show();
            return false;
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == ACTIVITY_SCANNER && codeReaderListener != null) {
            if (resultCode == Activity.RESULT_OK) {
                final String resultType = data.getStringExtra("code_type");
                final String resultData = data.getStringExtra("code_data");
                codeReaderListener.codeReadResult(resultType, resultData);
            } else {
                codeReaderListener.codeReadResult(null, null);
            }
        }
    }
}