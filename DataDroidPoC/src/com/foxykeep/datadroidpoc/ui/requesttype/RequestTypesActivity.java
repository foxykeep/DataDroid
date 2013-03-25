/**
 * 2013 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.ui.requesttype;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public final class RequestTypesActivity extends DataDroidActivity
        implements RequestManager.RequestListener, View.OnClickListener,
        ConnectionErrorDialogFragment.ConnectionErrorDialogListener {

    private Spinner mSpinnerRequestTypes;
    private EditText mETNumber;
    private TextView mTVResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.request_types);

        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mRequestList.size(); i++) {
            Request request = mRequestList.get(i);

            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
                setProgressBarIndeterminateVisibility(true);
            } else {
                mRequestManager.callListenerWithCachedData(this, request);
                i--;
                mRequestList.remove(request);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mRequestList.isEmpty()) {
            mRequestManager.removeRequestListener(this);
        }
    }

    private void bindViews() {
        mSpinnerRequestTypes = (Spinner) findViewById(R.id.sp_request_types);

        mETNumber = (EditText) findViewById(R.id.et_number);

        findViewById(R.id.b_load).setOnClickListener(this);

        mTVResult = (TextView) findViewById(R.id.tv_result);
    }

    private void callComputeSquareWS() {
        setProgressBarIndeterminateVisibility(true);
        mTVResult.setText("");
        int method = mSpinnerRequestTypes.getSelectedItemPosition();
        int number = Integer.parseInt(mETNumber.getText().toString());
        Request request = PoCRequestFactory.getComputeSquareRequest(method, number);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_load:
                callComputeSquareWS();
                break;
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            String result = getString(R.string.request_types_tv_result_format,
                    resultData.getInt(PoCRequestFactory.BUNDLE_EXTRA_SQUARE));
            mTVResult.setText(result);
        }
    }

    @Override
    public void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            ConnectionErrorDialogFragment.show(this, request, this);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            showBadDataErrorDialog();
        }
    }

    @Override
    public void onRequestCustomError(Request request, Bundle resultData) {
        // Never called.
    }

    @Override
    public void connectionErrorDialogCancel(Request request) {
    }

    @Override
    public void connectionErrorDialogRetry(Request request) {
        callComputeSquareWS();
    }
}
