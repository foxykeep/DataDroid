/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.memprovider.MemoryProvider;
import com.foxykeep.datadroidpoc.data.model.Phone;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager.OnRequestFinishedListener;
import com.foxykeep.datadroidpoc.data.service.PoCService;
import com.foxykeep.datadroidpoc.util.UserManager;

public class CrudSyncPhoneAddEditActivity extends Activity implements OnRequestFinishedListener,
        OnClickListener,
        TextWatcher {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_REQUEST_TYPE = "savedStateRequestType";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";

    private static final int REQUEST_TYPE_ADD = 1;
    private static final int REQUEST_TYPE_EDIT = 2;

    public static final String INTENT_EXTRA_PHONE = "com.foxykeep.datadroidpoc.ui.extras.phone";

    private EditText mEditTextName;
    private EditText mEditTextManufacturer;
    private EditText mEditTextAndroidVersion;
    private EditText mEditTextScreenSize;
    private EditText mEditTextPrice;
    private Button mButtonSubmit;

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;
    private int mRequestType = -1;
    private String mUserId;

    private MemoryProvider mMemoryProvider = MemoryProvider.getInstance();

    private Phone mPhone;

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.crud_phone_add_edit);
        bindViews();

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mRequestType = savedInstanceState.getInt(SAVED_STATE_REQUEST_TYPE, -1);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
        }

        mRequestManager = PoCRequestManager.from(this);
        mUserId = UserManager.getUserId(this);

        final Intent intent = getIntent();
        if (intent != null) {
            mPhone = intent.getParcelableExtra(INTENT_EXTRA_PHONE);
        }

        setTitle(mPhone == null ? R.string.crud_sync_phone_add_title
                : R.string.crud_sync_phone_edit_title);

        populateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestId != -1) {
            if (mRequestManager.isRequestInProgress(mRequestId)) {
                mRequestManager.addOnRequestFinishedListener(this);
                showDialog(DialogConfig.DIALOG_PROGRESS);
            } else {
                if (mRequestType == REQUEST_TYPE_ADD) {
                    if (mMemoryProvider.syncPhoneAddedPhone == null) {
                        showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                    } else {
                        mRequestType = -1;

                        Intent resultData = new Intent();
                        resultData.putExtra(CrudSyncPhoneListActivity.RESULT_EXTRA_ADDED_PHONE,
                                mMemoryProvider.syncPhoneAddedPhone);
                        setResult(RESULT_OK, resultData);
                        finish();
                    }
                } else if (mRequestType == REQUEST_TYPE_EDIT) {
                    if (mMemoryProvider.syncPhoneEditedPhone == null) {
                        showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                    } else {
                        mRequestType = -1;

                        Intent resultData = new Intent();
                        resultData.putExtra(CrudSyncPhoneListActivity.RESULT_EXTRA_EDITED_PHONE,
                                mMemoryProvider.syncPhoneEditedPhone);
                        setResult(RESULT_OK, resultData);
                        finish();
                    }
                }

                mRequestId = -1;
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
    }

    private void bindViews() {
        mEditTextName = (EditText) findViewById(R.id.et_name);
        mEditTextName.addTextChangedListener(this);
        mEditTextManufacturer = (EditText) findViewById(R.id.et_manufacturer);
        mEditTextManufacturer.addTextChangedListener(this);
        mEditTextAndroidVersion = (EditText) findViewById(R.id.et_android_version);
        mEditTextAndroidVersion.addTextChangedListener(this);
        mEditTextScreenSize = (EditText) findViewById(R.id.et_screen_size);
        mEditTextScreenSize.addTextChangedListener(this);
        mEditTextPrice = (EditText) findViewById(R.id.et_price);
        mEditTextPrice.addTextChangedListener(this);

        mButtonSubmit = (Button) findViewById(R.id.b_submit);
        mButtonSubmit.setOnClickListener(this);
    }

    private void populateViews() {
        if (mPhone != null) {
            mEditTextName.setText(mPhone.name);
            mEditTextManufacturer.setText(mPhone.manufacturer);
            mEditTextAndroidVersion.setText(mPhone.androidVersion);
            mEditTextScreenSize.setText(String.valueOf(mPhone.screenSize));
            mEditTextPrice.setText(String.valueOf(mPhone.price));
        }
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
                b.setPositiveButton(getString(R.string.dialog_button_retry),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int which) {
                                if (mRequestType == REQUEST_TYPE_ADD) {
                                    callSyncPhoneAddWS();
                                } else if (mRequestType == REQUEST_TYPE_EDIT) {
                                    callSyncPhoneEditWS();
                                }
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

    private void callSyncPhoneAddWS() {
        showDialog(DialogConfig.DIALOG_PROGRESS);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestType = REQUEST_TYPE_ADD;
        mRequestId = mRequestManager.addSyncPhone(mUserId, mEditTextName.getText().toString(),
                mEditTextManufacturer
                        .getText().toString(), mEditTextAndroidVersion.getText().toString(), Double
                        .parseDouble(mEditTextScreenSize.getText().toString()),
                Integer.parseInt(mEditTextPrice.getText()
                        .toString()));
    }

    private void callSyncPhoneEditWS() {
        showDialog(DialogConfig.DIALOG_PROGRESS);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestType = REQUEST_TYPE_EDIT;
        mRequestId = mRequestManager.editSyncPhone(mUserId, mPhone.serverId, mEditTextName
                .getText().toString(),
                mEditTextManufacturer.getText().toString(), mEditTextAndroidVersion.getText()
                        .toString(),
                Double.parseDouble(mEditTextScreenSize.getText().toString()),
                Integer.parseInt(mEditTextPrice.getText().toString()));
    }

    @Override
    public void onClick(final View view) {
        if (view == mButtonSubmit) {
            if (mPhone == null) {
                callSyncPhoneAddWS();
            } else {
                callSyncPhoneEditWS();
            }
        }
    }

    @Override
    public void afterTextChanged(final Editable s) {
        mButtonSubmit.setEnabled(!TextUtils.isEmpty(mEditTextName.getText().toString())
                && !TextUtils.isEmpty(mEditTextManufacturer.getText().toString())
                && !TextUtils.isEmpty(mEditTextAndroidVersion.getText().toString())
                && !TextUtils.isEmpty(mEditTextScreenSize.getText().toString())
                && !TextUtils.isEmpty(mEditTextPrice.getText().toString()));
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count,
            final int after) {
    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before,
            final int count) {
    }

    @Override
    public void onRequestFinished(final int requestId, final int resultCode, final Bundle payload) {
        if (requestId == mRequestId) {
            dismissDialog(DialogConfig.DIALOG_PROGRESS);
            mRequestId = -1;
            mRequestManager.removeOnRequestFinishedListener(this);
            if (resultCode == PoCService.ERROR_CODE) {
                if (payload != null) {
                    final int errorType = payload.getInt(
                            PoCRequestManager.RECEIVER_EXTRA_ERROR_TYPE, -1);
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
                if (mRequestType == REQUEST_TYPE_ADD) {
                    Intent resultData = new Intent();
                    resultData
                            .putExtra(
                                    CrudSyncPhoneListActivity.RESULT_EXTRA_ADDED_PHONE,
                                    payload.getParcelable(PoCRequestManager.RECEIVER_EXTRA_PHONE_ADD_EDIT_DATA));
                    setResult(RESULT_OK, resultData);
                    finish();
                } else if (mRequestType == REQUEST_TYPE_EDIT) {
                    Intent resultData = new Intent();
                    resultData
                            .putExtra(
                                    CrudSyncPhoneListActivity.RESULT_EXTRA_EDITED_PHONE,
                                    payload.getParcelable(PoCRequestManager.RECEIVER_EXTRA_PHONE_ADD_EDIT_DATA));
                    setResult(RESULT_OK, resultData);
                    finish();
                }

                mRequestType = -1;
            }
        }
    }
}
