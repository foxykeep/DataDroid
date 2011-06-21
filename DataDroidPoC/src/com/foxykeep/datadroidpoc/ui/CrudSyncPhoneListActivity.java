/*
 * 2011 Foxykeep (http://www.foxykeep.com)
 *
 * Licensed under the Beerware License :
 * 
 *   As long as you retain this notice you can do whatever you want with this stuff. If we meet some day, and you think
 *   this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.PhoneDao;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager.OnRequestFinishedListener;
import com.foxykeep.datadroidpoc.data.service.PoCService;
import com.foxykeep.datadroidpoc.util.NotifyingAsyncQueryHandler;
import com.foxykeep.datadroidpoc.util.NotifyingAsyncQueryHandler.AsyncQueryListener;
import com.foxykeep.datadroidpoc.util.UserManager;

public class CrudSyncPhoneListActivity extends ListActivity implements AsyncQueryListener, OnRequestFinishedListener {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";
    private static final String SAVED_STATE_IS_RESULT_LOADED = "savedStateIsResultLoaded";

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;

    private boolean mIsResultLoaded = false;

    private NotifyingAsyncQueryHandler mQueryHandler;

    private LayoutInflater mInflater;

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
            mIsResultLoaded = savedInstanceState.getBoolean(SAVED_STATE_IS_RESULT_LOADED, false);
        }

        mRequestManager = PoCRequestManager.from(this);
        mQueryHandler = new NotifyingAsyncQueryHandler(getContentResolver(), this);
        mInflater = getLayoutInflater();

        mQueryHandler.startQuery(PhoneDao.CONTENT_URI, PhoneDao.CONTENT_LIST_PROJECTION, PhoneDao.NAME_ORDER_BY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestId != -1) {
            if (mRequestManager.isRequestInProgress(mRequestId)) {
                mRequestManager.addOnRequestFinishedListener(this);
                setProgressBarIndeterminateVisibility(true);
            } else {
                mRequestId = -1;

                // Get the number of persons in the database
                int number = 1;
                // TODO gestion des infos en base pour voir si requete OK

                if (number < 1) {
                    // We don't have a way to know if the request was correctly
                    // executed with 0 result or if an error occurred.
                    // Here I choose to display an error but it's up to you
                    showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                } else {
                    mIsResultLoaded = true;
                }
            }
        } else if (!mIsResultLoaded) {
            callPhoneListWS();
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
        outState.putBoolean(SAVED_STATE_IS_RESULT_LOADED, mIsResultLoaded);
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
                        callPhoneListWS();
                    }
                });
                b.setTitle(R.string.dialog_error_connexion_error_title);
                b.setMessage(R.string.dialog_error_connexion_error_message);
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

    private void callPhoneListWS() {
        setProgressBarIndeterminateVisibility(true);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestId = mRequestManager.getPhoneList(UserManager.getUserId(this));
    }

    @Override
    public void onRequestFinished(final int requestId, final int resultCode, final Bundle payload) {
        if (requestId == mRequestId) {
            setProgressBarIndeterminateVisibility(false);
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
                mIsResultLoaded = true;
            }
        }
    }

    @Override
    public void onQueryComplete(final int token, final Object cookie, final Cursor cursor) {
        PhoneListAdapter adapter = (PhoneListAdapter) getListAdapter();
        if (adapter == null) {
            adapter = new PhoneListAdapter(this, cursor);
            setListAdapter(adapter);
        } else {
            adapter.changeCursor(cursor);
        }
    }

    class ViewHolder {
        private long id;
        private TextView mTextViewName;
        private CharArrayBuffer mCharArrayBufferName;
        private TextView mTextViewManufacturer;
        private CharArrayBuffer mCharArrayBufferManufacturer;

        public ViewHolder(final View view) {
            mTextViewName = (TextView) view.findViewById(R.id.tv_name);
            mTextViewManufacturer = (TextView) view.findViewById(R.id.tv_manufacturer);

            mCharArrayBufferName = new CharArrayBuffer(20);
            mCharArrayBufferManufacturer = new CharArrayBuffer(20);
        }

        public void populateView(final Cursor c) {
            id = c.getLong(PhoneDao.CONTENT_LIST_ID_COLUMN);

            c.copyStringToBuffer(PhoneDao.CONTENT_LIST_NAME_COLUMN, mCharArrayBufferName);
            mTextViewName.setText(mCharArrayBufferName.data, 0, mCharArrayBufferName.sizeCopied);

            c.copyStringToBuffer(PhoneDao.CONTENT_LIST_MANUFACTURER_COLUMN, mCharArrayBufferManufacturer);
            mTextViewManufacturer
                    .setText(mCharArrayBufferManufacturer.data, 0, mCharArrayBufferManufacturer.sizeCopied);
        }
    }

    class PhoneListAdapter extends CursorAdapter {

        public PhoneListAdapter(final Context context, final Cursor c) {
            super(context, c);
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            ((ViewHolder) view.getTag()).populateView(cursor);
        }

        @Override
        public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
            View view = mInflater.inflate(R.layout.crud_phone_list_item, null);
            view.setTag(new ViewHolder(view));
            return view;
        }
    }
}
