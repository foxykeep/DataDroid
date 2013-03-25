/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.ui.feature;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.model.City;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment.ConnectionErrorDialogListener;
import com.foxykeep.datadroidpoc.dialogs.ErrorDialogFragment.ErrorDialogFragmentBuilder;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomRequestExceptionActivity extends DataDroidActivity implements RequestListener,
        OnClickListener, ConnectionErrorDialogListener {

    private static final String SAVED_STATE_CITY_LIST = "savedStateCityList";

    private ListView mListView;
    private CityListAdapter mListAdapter;

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.custom_request_exception);

        bindViews();

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

        ArrayList<City> cityList = new ArrayList<City>();
        for (int i = 0, n = mListAdapter.getCount(); i < n; i++) {
            cityList.add(mListAdapter.getItem(i));
        }

        outState.putParcelableArrayList(SAVED_STATE_CITY_LIST, cityList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList<City> cityItemList = savedInstanceState
                .getParcelableArrayList(SAVED_STATE_CITY_LIST);
        mListAdapter.setNotifyOnChange(false);
        for (int i = 0, length = cityItemList.size(); i < length; i++) {
            mListAdapter.add(cityItemList.get(i));
        }
        mListAdapter.notifyDataSetChanged();
    }

    private void bindViews() {
        ((Button) findViewById(R.id.b_without_exception)).setOnClickListener(this);
        ((Button) findViewById(R.id.b_with_exception)).setOnClickListener(this);

        mListView = (ListView) findViewById(android.R.id.list);
        mListAdapter = new CityListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setEmptyView(findViewById(android.R.id.empty));
    }

    private void callWithoutExceptionWS() {
        mListAdapter.clear();
        setProgressBarIndeterminateVisibility(true);
        Request request = PoCRequestFactory.getCityListExceptionRequest(false);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    private void callWithExceptionWS() {
        mListAdapter.clear();
        setProgressBarIndeterminateVisibility(true);
        Request request = PoCRequestFactory.getCityListExceptionRequest(true);
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_without_exception:
                callWithoutExceptionWS();
                break;
            case R.id.b_with_exception:
                callWithExceptionWS();
                break;
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            ArrayList<City> cityList = resultData
                    .getParcelableArrayList(PoCRequestFactory.BUNDLE_EXTRA_CITY_LIST);

            mListAdapter.setNotifyOnChange(false);
            for (City city : cityList) {
                mListAdapter.add(city);
            }
            mListAdapter.notifyDataSetChanged();
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
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            String errorMessage = resultData
                    .getString(PoCRequestFactory.BUNDLE_EXTRA_ERROR_MESSAGE);
            ErrorDialogFragmentBuilder builder = new ErrorDialogFragmentBuilder(this);
            builder.setTitle(R.string.dialog_error_title);
            builder.setMessage(errorMessage);
            builder.show();
        }
    }

    @Override
    public void connectionErrorDialogCancel(Request request) {}

    @Override
    public void connectionErrorDialogRetry(Request request) {
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    class ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewPostalCode;
        private TextView mTextViewState;
        private TextView mTextViewCountry;

        public ViewHolder(View view) {
            mTextViewName = (TextView) view.findViewById(R.id.tv_name);
            mTextViewPostalCode = (TextView) view.findViewById(R.id.tv_postal_code);
            mTextViewState = (TextView) view.findViewById(R.id.tv_state);
            mTextViewCountry = (TextView) view.findViewById(R.id.tv_country);
        }

        public void populateViews(City city) {
            mTextViewName.setText(city.name);
            mTextViewPostalCode.setText(city.postalCode);
            mTextViewState.setText(city.state);
            mTextViewCountry.setText(city.country);
        }
    }

    class CityListAdapter extends ArrayAdapter<City> {

        public CityListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.city_list_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.populateViews(getItem(position));

            return convertView;
        }
    }
}
