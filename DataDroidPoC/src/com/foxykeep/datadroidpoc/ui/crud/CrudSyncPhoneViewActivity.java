/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui.crud;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment.ConnectionErrorDialogListener;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment.ProgressDialogFragmentBuilder;
import com.foxykeep.datadroidpoc.dialogs.QuestionDialogFragment.QuestionDialogFragmentBuilder;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;
import com.foxykeep.datadroidpoc.util.UserManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public final class CrudSyncPhoneViewActivity extends DataDroidActivity implements RequestListener,
        ConnectionErrorDialogListener {

    private static final String SAVED_STATE_PHONE = "savedStatePhone";
    private static final String SAVED_STATE_IS_PHONE_EDITED = "savedStateIsPhoneEdited";

    public static final String INTENT_EXTRA_PHONE = "com.foxykeep.datadroidpoc.ui.extra.phone";

    private static final int ACTIVITY_FOR_RESULT_EDIT = 1;

    public static final String RESULT_EXTRA_EDITED_PHONE = "resultExtraEditedPhone";

    private Phone mPhone;
    private boolean mIsPhoneEdited;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_phone_view);

        Intent intent = getIntent();

        if (savedInstanceState != null) {
            mPhone = savedInstanceState.getParcelable(SAVED_STATE_PHONE);
            mIsPhoneEdited = savedInstanceState.getBoolean(SAVED_STATE_IS_PHONE_EDITED);
        } else if (intent != null) {
            mPhone = intent.getParcelableExtra(INTENT_EXTRA_PHONE);
            mIsPhoneEdited = false;
        }

        populateViews();

        mUserId = UserManager.getUserId(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mRequestList.size(); i++) {
            Request request = mRequestList.get(i);

            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
            } else {
                ProgressDialogFragment.dismiss(this);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVED_STATE_PHONE, mPhone);
        outState.putBoolean(SAVED_STATE_IS_PHONE_EDITED, mIsPhoneEdited);
    }

    private void populateViews() {
        ((TextView) findViewById(R.id.tv_name)).setText(mPhone.name);
        ((TextView) findViewById(R.id.tv_manufacturer)).setText(mPhone.manufacturer);
        ((TextView) findViewById(R.id.tv_android_version)).setText(mPhone.androidVersion);
        ((TextView) findViewById(R.id.tv_screen_size)).setText(getString(
                R.string.crud_phone_view_tv_screen_size_format, mPhone.screenSize));
        ((TextView) findViewById(R.id.tv_price)).setText(getString(
                R.string.crud_phone_view_tv_price_format,
                mPhone.price));
    }

    private void callSyncPhoneDeleteWS() {
        new ProgressDialogFragmentBuilder(this)
                .setMessage(R.string.progress_dialog_message)
                .setCancelable(true)
                .show();
        Request request = PoCRequestFactory.deleteSyncPhonesRequest(mUserId,
                String.valueOf(mPhone.serverId));
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_FOR_RESULT_EDIT:
                if (resultCode == RESULT_OK) {
                    mPhone = data.getParcelableExtra(RESULT_EXTRA_EDITED_PHONE);
                    mIsPhoneEdited = true;
                    populateViews();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsPhoneEdited) {
            Intent resultData = new Intent();
            resultData.putExtra(CrudSyncPhoneListActivity.RESULT_EXTRA_EDITED_PHONE, mPhone);
            setResult(RESULT_OK, resultData);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_phone_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_edit: {
                Intent intent = new Intent(this, CrudSyncPhoneAddEditActivity.class);
                intent.putExtra(CrudSyncPhoneAddEditActivity.INTENT_EXTRA_PHONE, mPhone);
                startActivityForResult(intent, ACTIVITY_FOR_RESULT_EDIT);
                return true;
            }
            case R.id.menu_delete: {
                QuestionDialogFragmentBuilder b = new QuestionDialogFragmentBuilder(this);
                b.setTitle(R.string.crud_phone_view_dialog_delete_confirm_title);
                b.setMessage(getString(R.string.crud_phone_view_dialog_delete_confirm_message,
                        mPhone.name));
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callSyncPhoneDeleteWS();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, null);
                b.show();
                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            ProgressDialogFragment.dismiss(this);
            mRequestList.remove(request);

            long[] syncDeletedPhoneIdArray = resultData
                    .getLongArray(PoCRequestFactory.BUNDLE_EXTRA_PHONE_DELETE_DATA);

            Intent data = new Intent();
            data.putExtra(CrudSyncPhoneListActivity.RESULT_EXTRA_DELETED_PHONE_ID,
                    syncDeletedPhoneIdArray[0]);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            ProgressDialogFragment.dismiss(this);
            mRequestList.remove(request);

            ConnectionErrorDialogFragment.show(this, request, this);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            ProgressDialogFragment.dismiss(this);
            mRequestList.remove(request);

            showBadDataErrorDialog();
        }
    }

    @Override
    public void onRequestCustomError(Request request, Bundle resultData) {
        // Never called.
    }

    @Override
    public void connectionErrorDialogCancel(Request request) {}

    @Override
    public void connectionErrorDialogRetry(Request request) {
        callSyncPhoneDeleteWS();
    }
}
