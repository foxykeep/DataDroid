/*
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.memprovider.MemoryProvider;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager.OnRequestFinishedListener;
import com.foxykeep.datadroidpoc.data.service.PoCService;
import com.foxykeep.datadroidpoc.util.UserManager;

public class CrudSyncPhoneViewActivity extends Activity implements OnRequestFinishedListener {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";
    private static final String SAVED_STATE_PHONE = "savedStatePhone";
    private static final String SAVED_STATE_IS_PHONE_EDITED = "savedStateIsPhoneEdited";

    public static final String INTENT_EXTRA_PHONE = "com.foxykeep.datadroidpoc.ui.extras.phone";

    private static final int ACTIVITY_FOR_RESULT_EDIT = 1;

    public static final String RESULT_EXTRA_EDITED_PHONE = "resultExtraEditedPhone";

    private Phone mPhone;
    private boolean mIsPhoneEdited;

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;
    private String mUserId;

    private MemoryProvider mMemoryProvider = MemoryProvider.getInstance();

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_phone_view);

        Intent intent = getIntent();

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
            mPhone = savedInstanceState.getParcelable(SAVED_STATE_PHONE);
            mIsPhoneEdited = savedInstanceState.getBoolean(SAVED_STATE_IS_PHONE_EDITED);
        } else if (intent != null) {
            mPhone = intent.getParcelableExtra(INTENT_EXTRA_PHONE);
            mIsPhoneEdited = false;
        }

        populateViews();

        mRequestManager = PoCRequestManager.from(this);
        mUserId = UserManager.getUserId(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestId != -1) {
            if (mRequestManager.isRequestInProgress(mRequestId)) {
                mRequestManager.addOnRequestFinishedListener(this);
                showDialog(DialogConfig.DIALOG_PROGRESS);
            } else {
                mRequestId = -1;

                if (mMemoryProvider.syncPhoneDeleteData == null) {
                    showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                } else {
                    final long[] syncDeletedPhoneIdArray = mMemoryProvider.syncPhoneDeleteData;

                    Intent data = new Intent();
                    data.putExtra(CrudSyncPhoneListActivity.RESULT_EXTRA_DELETED_PHONE_ID, syncDeletedPhoneIdArray[0]);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRequestId != -1) {
            mRequestManager.removeOnRequestFinishedListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_STATE_REQUEST_ID, mRequestId);
        outState.putString(SAVED_STATE_ERROR_TITLE, mErrorDialogTitle);
        outState.putString(SAVED_STATE_ERROR_MESSAGE, mErrorDialogMessage);
        outState.putParcelable(SAVED_STATE_PHONE, mPhone);
        outState.putBoolean(SAVED_STATE_IS_PHONE_EDITED, mIsPhoneEdited);
    }

    private void populateViews() {
        ((TextView) findViewById(R.id.tv_name)).setText(mPhone.name);
        ((TextView) findViewById(R.id.tv_manufacturer)).setText(mPhone.manufacturer);
        ((TextView) findViewById(R.id.tv_android_version)).setText(mPhone.androidVersion);
        ((TextView) findViewById(R.id.tv_screen_size)).setText(getString(
                R.string.crud_phone_view_tv_screen_size_format, mPhone.screenSize));
        ((TextView) findViewById(R.id.tv_price)).setText(getString(R.string.crud_phone_view_tv_price_format,
                mPhone.price));
    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        Builder b;
        switch (id) {
            case DialogConfig.DIALOG_ERROR:
                b = new Builder(this);
                b.setTitle(mErrorDialogTitle);
                b.setMessage(mErrorDialogMessage);
                b.setCancelable(true);
                b.setNeutralButton(android.R.string.ok, null);
                return b.create();
            case DialogConfig.DIALOG_CONNEXION_ERROR:
                b = new Builder(this);
                b.setCancelable(true);
                b.setNeutralButton(getString(android.R.string.ok), null);
                b.setPositiveButton(getString(R.string.dialog_button_retry), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                        callSyncPhoneDeleteWS();
                    }
                });
                b.setTitle(R.string.dialog_error_connexion_error_title);
                b.setMessage(R.string.dialog_error_connexion_error_message);
                return b.create();
            case DialogConfig.DIALOG_PROGRESS:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle(R.string.progress_dialog_title);
                dialog.setMessage(getString(R.string.progress_dialog_message));
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                return dialog;
            case DialogConfig.DIALOG_DELETE_CONFIRM:
                b = new Builder(this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setTitle(R.string.crud_phone_view_dialog_delete_confirm_title);
                b.setMessage(getString(R.string.crud_phone_view_dialog_delete_confirm_message, mPhone.name));
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

    @Override
    protected void onPrepareDialog(final int id, final Dialog dialog) {
        switch (id) {
            case DialogConfig.DIALOG_ERROR:
                dialog.setTitle(mErrorDialogTitle);
                ((AlertDialog) dialog).setMessage(mErrorDialogMessage);
                break;
            default:
                super.onPrepareDialog(id, dialog);
                break;
        }
    }

    private void callSyncPhoneDeleteWS() {
        showDialog(DialogConfig.DIALOG_PROGRESS);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestId = mRequestManager.deleteSyncPhones(mUserId, String.valueOf(mPhone.serverId));
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
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsPhoneEdited) {
                Intent resultData = new Intent();
                resultData.putExtra(CrudSyncPhoneListActivity.RESULT_EXTRA_EDITED_PHONE, mPhone);
                setResult(RESULT_OK, resultData);
                finish();
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
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
    public void onRequestFinished(final int requestId, final int resultCode, final Bundle payload) {
        if (requestId == mRequestId) {
            dismissDialog(DialogConfig.DIALOG_PROGRESS);
            mRequestId = -1;
            mRequestManager.removeOnRequestFinishedListener(this);
            if (resultCode == PoCService.ERROR_CODE) {
                if (payload != null) {
                    final int errorType = payload.getInt(PoCRequestManager.RECEIVER_EXTRA_ERROR_TYPE, -1);
                    if (errorType == PoCRequestManager.RECEIVER_EXTRA_VALUE_ERROR_TYPE_DATA) {
                        mErrorDialogTitle = getString(R.string.dialog_error_data_error_title);
                        mErrorDialogMessage = getString(R.string.dialog_error_data_error_message);
                        showDialog(DialogConfig.DIALOG_ERROR);
                    } else {
                        showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                    }
                } else {
                    showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                }
            } else {
                final long[] syncDeletedPhoneIdArray = payload
                        .getLongArray(PoCRequestManager.RECEIVER_EXTRA_PHONE_DELETE_DATA);

                Intent data = new Intent();
                data.putExtra(CrudSyncPhoneListActivity.RESULT_EXTRA_DELETED_PHONE_ID, syncDeletedPhoneIdArray[0]);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
