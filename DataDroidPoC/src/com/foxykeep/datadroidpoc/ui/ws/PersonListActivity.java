/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui.ws;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.provider.PoCContent.DbPerson;
import com.foxykeep.datadroidpoc.data.provider.util.ProviderCriteria;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment.ConnectionErrorDialogListener;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public final class PersonListActivity extends DataDroidActivity implements RequestListener,
        OnClickListener, ConnectionErrorDialogListener, LoaderCallbacks<Cursor> {

    private Spinner mSpinnerReturnFormat;
    private ListView mListView;
    private PersonListAdapter mListAdapter;

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.person_list);
        bindViews();

        getSupportLoaderManager().initLoader(0, null, this);
        mInflater = getLayoutInflater();
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

                // Get the number of persons in the database
                int number = mListAdapter.getCursor().getCount();

                if (number < 1) {
                    // In this case, we don't have a way to know if the request was correctly
                    // executed with 0 result or if an error occurred. Here I choose to display an
                    // error but it's up to you
                    ConnectionErrorDialogFragment.show(this, request, this);
                }

                // Nothing to do if it works as the cursor is automatically updated
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
        mSpinnerReturnFormat = (Spinner) findViewById(R.id.sp_return_format);

        ((Button) findViewById(R.id.b_load)).setOnClickListener(this);
        ((Button) findViewById(R.id.b_clear_db)).setOnClickListener(this);

        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setEmptyView(findViewById(android.R.id.empty));
        mListAdapter = new PersonListAdapter(this);
        mListView.setAdapter(mListAdapter);
    }

    private void callPersonListWS() {
        setProgressBarIndeterminateVisibility(true);
        Request request = PoCRequestFactory.getPersonListRequest(mSpinnerReturnFormat
                .getSelectedItemPosition());
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_load:
                callPersonListWS();
                break;
            case R.id.b_clear_db:
                getContentResolver().delete(DbPerson.CONTENT_URI, null, null);
                break;
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            // Nothing to do if it works as the cursor is automatically updated
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
        callPersonListWS();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        ProviderCriteria criteria = new ProviderCriteria();
        criteria.addSortOrder(DbPerson.Columns.LAST_NAME, true);
        return new CursorLoader(this, DbPerson.CONTENT_URI, DbPerson.PROJECTION, null, null,
                criteria.getOrderClause());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.changeCursor(null);
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

        public ViewHolder(View view) {
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

        public void populateView(Cursor c) {
            c.copyStringToBuffer(DbPerson.Columns.FIRST_NAME.getIndex(),
                    mCharArrayBufferFirstName);
            mTextViewFirstName.setText(mCharArrayBufferFirstName.data, 0,
                    mCharArrayBufferFirstName.sizeCopied);

            c.copyStringToBuffer(DbPerson.Columns.LAST_NAME.getIndex(), mCharArrayBufferLastName);
            mTextViewLastName.setText(mCharArrayBufferLastName.data, 0,
                    mCharArrayBufferLastName.sizeCopied);

            mTextViewAge.setText(getString(R.string.person_list_item_tv_age_format,
                    c.getInt(DbPerson.Columns.AGE.getIndex())));

            c.copyStringToBuffer(DbPerson.Columns.EMAIL.getIndex(), mCharArrayBufferEmail);
            mTextViewEmail.setText(mCharArrayBufferEmail.data, 0, mCharArrayBufferEmail.sizeCopied);

            mTextViewPostalCode.setText(String.valueOf(c.getInt(DbPerson.Columns.POSTAL_CODE
                    .getIndex())));

            c.copyStringToBuffer(DbPerson.Columns.CITY.getIndex(), mCharArrayBufferCity);
            mTextViewCity.setText(mCharArrayBufferCity.data, 0, mCharArrayBufferCity.sizeCopied);
        }
    }

    class PersonListAdapter extends CursorAdapter {

        public PersonListAdapter(Context context) {
            super(context, null, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ((ViewHolder) view.getTag()).populateView(cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = mInflater.inflate(R.layout.person_list_item, null);
            view.setTag(new ViewHolder(view));
            return view;
        }
    }
}
