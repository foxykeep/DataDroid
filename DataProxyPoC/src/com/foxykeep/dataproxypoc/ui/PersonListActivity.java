package com.foxykeep.dataproxypoc.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;

import com.foxykeep.dataproxypoc.R;
import com.foxykeep.dataproxypoc.data.provider.PoCContent.PersonDao;
import com.foxykeep.dataproxypoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.dataproxypoc.data.requestmanager.PoCRequestManager.OnRequestFinishedListener;
import com.foxykeep.dataproxypoc.data.service.PoCService;
import com.foxykeep.dataproxypoc.data.worker.PersonListWorker;
import com.foxykeep.dataproxypoc.util.NotifyingAsyncQueryHandler;
import com.foxykeep.dataproxypoc.util.NotifyingAsyncQueryHandler.AsyncQueryListener;

public class PersonListActivity extends ListActivity implements OnRequestFinishedListener, AsyncQueryListener {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_ARE_PERSONS_LOADED = "savedStateArePersonsLoaded";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";

    private static final int DIALOG_CONNEXION_ERROR = 1;
    private static final int DIALOG_ERROR = 2;

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;

    private boolean mArePersonsLoaded = false;

    private NotifyingAsyncQueryHandler mQueryHandler;

    private LayoutInflater mInflater;

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        bindViews();

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mArePersonsLoaded = savedInstanceState.getBoolean(SAVED_STATE_ARE_PERSONS_LOADED, false);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
        }

        mRequestManager = PoCRequestManager.getInstance(this);
        mQueryHandler = new NotifyingAsyncQueryHandler(getContentResolver(), this);
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

                if (number > 1) {
                    showDialog(DIALOG_CONNEXION_ERROR);
                } else {
                    mArePersonsLoaded = true;

                    mQueryHandler.startQuery(PersonDao.CONTENT_URI, PersonDao.CONTENT_NAME_PROJECTION,
                            PersonDao.LAST_NAME_ORDER_BY);
                }
            }
        } else if (!mArePersonsLoaded) {
            callPersonListWS();
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
        outState.putBoolean(SAVED_STATE_ARE_PERSONS_LOADED, mArePersonsLoaded);
        outState.putString(SAVED_STATE_ERROR_TITLE, mErrorDialogTitle);
        outState.putString(SAVED_STATE_ERROR_MESSAGE, mErrorDialogMessage);
    }

    private void bindViews() {
        // TODO Auto-generated method stub

    }

    @Override
    protected Dialog onCreateDialog(final int id) {
        Builder b;
        switch (id) {
            case DIALOG_ERROR:
                b = new Builder(this);
                b.setTitle(mErrorDialogTitle);
                b.setMessage(mErrorDialogMessage);
                b.setCancelable(false);
                b.setNeutralButton(android.R.string.ok, null);
                return b.create();
            case DIALOG_CONNEXION_ERROR:
                b = new Builder(this);
                b.setCancelable(false);
                b.setNeutralButton(getString(android.R.string.ok), null);
                b.setPositiveButton(getString(R.string.dialog_button_retry), new DialogInterface.OnClickListener() {
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
            case DIALOG_ERROR:
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
        mRequestId = mRequestManager.getPersonList(PersonListWorker.RETURN_FORMAT_XML);
    }

    @Override
    public void onRequestFinished(final int requestId, final int resultCode, final Bundle payload) {
        if (requestId == mRequestId) {
            setProgressBarIndeterminateVisibility(false);
            mRequestId = -1;
            if (resultCode == PoCService.ERROR_CODE) {
                if (payload != null) {
                    final int errorType = payload.getInt(PoCRequestManager.RECEIVER_EXTRA_ERROR_TYPE, -1);
                    if (errorType == PoCRequestManager.RECEIVER_EXTRA_VALUE_ERROR_TYPE_DATA) {
                        mErrorDialogTitle = getString(R.string.dialog_error_data_error_title);
                        mErrorDialogMessage = getString(R.string.dialog_error_data_error_message);
                        showDialog(DIALOG_ERROR);
                    } else {
                        showDialog(DIALOG_CONNEXION_ERROR);
                    }
                } else {
                    showDialog(DIALOG_CONNEXION_ERROR);
                }
            } else {
                mArePersonsLoaded = true;

                mQueryHandler.startQuery(PersonDao.CONTENT_URI, PersonDao.CONTENT_NAME_PROJECTION,
                        PersonDao.LAST_NAME_ORDER_BY);
            }
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

    class PersonListAdapter extends CursorAdapter {

        public PersonListAdapter(final Context context, final Cursor c) {
            super(context, c);
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            // TODO Auto-generated method stub

        }

        @Override
        public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
