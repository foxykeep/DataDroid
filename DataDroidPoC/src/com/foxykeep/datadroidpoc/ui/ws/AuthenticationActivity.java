/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.ui.ws;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment.ConnectionErrorDialogListener;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;

public final class AuthenticationActivity extends DataDroidActivity implements RequestListener,
        OnClickListener, ConnectionErrorDialogListener {

    private TextView mTVResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.authentication);

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
                mRequestList.remove(request);
                i--;
                mRequestManager.callListenerWithCachedData(this, request);
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
        ((Button) findViewById(R.id.b_load)).setOnClickListener(this);
        ((Button) findViewById(R.id.b_load_with_authentication)).setOnClickListener(this);

        mTVResult = (TextView) findViewById(R.id.tv_result);
    }

    private void callAuthenticationWSWithout() {
        mTVResult.setText("");
        setProgressBarIndeterminateVisibility(true);
        Request request = PoCRequestFactory.authenticationRequest(false);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    private void callAuthenticationWSWith() {
        mTVResult.setText("");
        setProgressBarIndeterminateVisibility(true);
        Request request = PoCRequestFactory.authenticationRequest(true);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_load:
                callAuthenticationWSWithout();
                break;
            case R.id.b_load_with_authentication:
                callAuthenticationWSWith();
                break;
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            mTVResult.setText(resultData
                    .getString(PoCRequestFactory.BUNDLE_EXTRA_AUTHENTICATION_RESULT));
        }
    }

    @Override
    public void onRequestConnectionError(Request request) {
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
    public void connectionErrorDialogCancel(Request request) {}

    @Override
    public void connectionErrorDialogRetry(Request request) {
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }
}
