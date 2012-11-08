/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui.crud;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnexionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment.ProgressDialogFragmentBuilder;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;
import com.foxykeep.datadroidpoc.util.UserManager;

public final class CrudSyncPhoneViewActivity extends DataDroidActivity implements RequestListener {

    private static final String SAVED_STATE_PHONE = "savedStatePhone";
    private static final String SAVED_STATE_IS_PHONE_EDITED = "savedStateIsPhoneEdited";

    public static final String INTENT_EXTRA_PHONE = "com.foxykeep.datadroidpoc.ui.extras.phone";

    private static final int ACTIVITY_FOR_RESULT_EDIT = 1;

    public static final String RESULT_EXTRA_EDITED_PHONE = "resultExtraEditedPhone";

    private Phone mPhone;
    private boolean mIsPhoneEdited;

    private String mUserId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
        for (int i = 0, length = mRequestList.size(); i < length; i++) {
            Request request = mRequestList.get(i);

            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
            } else {
                ProgressDialogFragment.dismiss(this);
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

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
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

    @Override
    protected Dialog onCreateDialog(final int id) {
        Builder b;
        switch (id) {
            case DialogConfig.DIALOG_DELETE_CONFIRM:
                b = new Builder(this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setTitle(R.string.crud_phone_view_dialog_delete_confirm_title);
                b.setMessage(getString(R.string.crud_phone_view_dialog_delete_confirm_message,
                        mPhone.name));
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        callSyncPhoneDeleteWS();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, null);
                b.setCancelable(true);
                return b.create();
            default:
                return super.onCreateDialog(id);
        }
    }

    private void callSyncPhoneDeleteWS() {
        new ProgressDialogFragmentBuilder(this)
                .setMessage(R.string.progress_dialog_message)
                .setCancelable(true)
                .show();
        Request request = PoCRequestFactory.createDeleteSyncPhonesRequest(mUserId,
                String.valueOf(mPhone.serverId));
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.crud_phone_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_edit:
                final Intent intent = new Intent(this, CrudSyncPhoneAddEditActivity.class);
                intent.putExtra(CrudSyncPhoneAddEditActivity.INTENT_EXTRA_PHONE, mPhone);
                startActivityForResult(intent, ACTIVITY_FOR_RESULT_EDIT);
                return true;
            case R.id.menu_delete:
                showDialog(DialogConfig.DIALOG_DELETE_CONFIRM);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            ProgressDialogFragment.dismiss(this);
            mRequestList.remove(request);

            final long[] syncDeletedPhoneIdArray = resultData
                    .getLongArray(PoCRequestFactory.BUNDLE_EXTRA_PHONE_DELETE_DATA);

            Intent data = new Intent();
            data.putExtra(CrudSyncPhoneListActivity.RESULT_EXTRA_DELETED_PHONE_ID,
                    syncDeletedPhoneIdArray[0]);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onRequestConnectionError(Request request) {
        if (mRequestList.contains(request)) {
            ProgressDialogFragment.dismiss(this);
            mRequestList.remove(request);

            ConnexionErrorDialogFragment.show(this, request, this);
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
}
