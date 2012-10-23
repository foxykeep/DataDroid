/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.foxykeep.datadroid.requestmanager.RequestManager.OnRequestFinishedListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.PersonDao;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.service.PoCService;
import com.foxykeep.datadroidpoc.util.NotifyingAsyncQueryHandler;
import com.foxykeep.datadroidpoc.util.NotifyingAsyncQueryHandler.AsyncQueryListener;

public final class PersonListActivity extends ListActivity implements OnRequestFinishedListener,
        AsyncQueryListener, OnClickListener {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";

    private Spinner mSpinnerReturnFormat;
    private Button mButtonLoad;
    private Button mButtonClearDb;

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;

    private NotifyingAsyncQueryHandler mQueryHandler;

    private LayoutInflater mInflater;

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.person_list);
        bindViews();

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
        }

        mRequestManager = PoCRequestManager.from(this);
        mQueryHandler = new NotifyingAsyncQueryHandler(getContentResolver(), this);
        mInflater = getLayoutInflater();

        mQueryHandler.startQuery(PersonDao.CONTENT_URI, PersonDao.CONTENT_PROJECTION,
                PersonDao.LAST_NAME_ORDER_BY);
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
                int number = ((PersonListAdapter) getListAdapter()).getCursor().getCount();

                if (number < 1) {
                    // In this case, we don't have a way to know if the request
                    // was correctly executed with 0 result or if an error
                    // occurred. Here I choose to display an error but it's up
                    // to you
                    showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                }
                // Nothing to do if it works as the cursor is automatically
                // updated
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
        mSpinnerReturnFormat = (Spinner) findViewById(R.id.sp_return_format);

        mButtonLoad = (Button) findViewById(R.id.b_load);
        mButtonLoad.setOnClickListener(this);

        mButtonClearDb = (Button) findViewById(R.id.b_clear_db);
        mButtonClearDb.setOnClickListener(this);
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
                                callPersonListWS();
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

    private void callPersonListWS() {
        setProgressBarIndeterminateVisibility(true);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestId = mRequestManager.getPersonList(mSpinnerReturnFormat.getSelectedItemPosition());
    }

    @Override
    public void onClick(final View view) {
        if (view == mButtonLoad) {
            callPersonListWS();
        } else if (view == mButtonClearDb) {
            mQueryHandler.startDelete(PersonDao.CONTENT_URI);
        }
    }

    @Override
    public void onRequestFinished(final int requestId, final int resultCode, final Bundle payload) {
        if (requestId == mRequestId) {
            setProgressBarIndeterminateVisibility(false);
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
            }
            // Nothing to do if it works as the cursor is automatically updated
        }
    }

    @Override
    public void onQueryComplete(final int token, final Object cookie, final Cursor cursor) {
        PersonListAdapter adapter = (PersonListAdapter) getListAdapter();
        if (adapter == null) {
            adapter = new PersonListAdapter(this, cursor);
            setListAdapter(adapter);
        } else {
            adapter.changeCursor(cursor);
        }
    }

    class ViewHolder {
        private TextView mTextViewFirstName;
        private CharArrayBuffer mCharArrayBufferFirstName;
        private TextView mTextViewLastName;
        private CharArrayBuffer mCharArrayBufferLastName;
        private TextView mTextViewAge;
        private TextView mTextViewEmail;
        private CharArrayBuffer mCharArrayBufferEmail;
        private TextView mTextViewPostalCode;
        private TextView mTextViewCity;
        private CharArrayBuffer mCharArrayBufferCity;

        public ViewHolder(final View view) {
            mTextViewFirstName = (TextView) view.findViewById(R.id.tv_first_name);
            mTextViewLastName = (TextView) view.findViewById(R.id.tv_last_name);
            mTextViewAge = (TextView) view.findViewById(R.id.tv_age);
            mTextViewEmail = (TextView) view.findViewById(R.id.tv_email);
            mTextViewPostalCode = (TextView) view.findViewById(R.id.tv_postal_code);
            mTextViewCity = (TextView) view.findViewById(R.id.tv_city);

            mCharArrayBufferFirstName = new CharArrayBuffer(20);
            mCharArrayBufferLastName = new CharArrayBuffer(20);
            mCharArrayBufferEmail = new CharArrayBuffer(20);
            mCharArrayBufferCity = new CharArrayBuffer(20);
        }

        public void populateView(final Cursor c) {
            c.copyStringToBuffer(PersonDao.CONTENT_FIRST_NAME_COLUMN, mCharArrayBufferFirstName);
            mTextViewFirstName.setText(mCharArrayBufferFirstName.data, 0,
                    mCharArrayBufferFirstName.sizeCopied);

            c.copyStringToBuffer(PersonDao.CONTENT_LAST_NAME_COLUMN, mCharArrayBufferLastName);
            mTextViewLastName.setText(mCharArrayBufferLastName.data, 0,
                    mCharArrayBufferLastName.sizeCopied);

            mTextViewAge.setText(getString(R.string.person_list_item_tv_age_format,
                    c.getInt(PersonDao.CONTENT_AGE_COLUMN)));

            c.copyStringToBuffer(PersonDao.CONTENT_EMAIL_COLUMN, mCharArrayBufferEmail);
            mTextViewEmail.setText(mCharArrayBufferEmail.data, 0, mCharArrayBufferEmail.sizeCopied);

            mTextViewPostalCode.setText(String.valueOf(c
                    .getInt(PersonDao.CONTENT_POSTAL_CODE_COLUMN)));

            c.copyStringToBuffer(PersonDao.CONTENT_CITY_COLUMN, mCharArrayBufferCity);
            mTextViewCity.setText(mCharArrayBufferCity.data, 0, mCharArrayBufferCity.sizeCopied);
        }
    }

    class PersonListAdapter extends CursorAdapter {

        public PersonListAdapter(final Context context, final Cursor c) {
            super(context, c);
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            ((ViewHolder) view.getTag()).populateView(cursor);
        }

        @Override
        public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
            View view = mInflater.inflate(R.layout.person_list_item, null);
            view.setTag(new ViewHolder(view));
            return view;
        }
    }
}
