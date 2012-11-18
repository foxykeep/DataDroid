/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui.ws;

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

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.model.City;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnexionErrorDialogFragment;
import com.foxykeep.datadroidpoc.ui.DataDroidActivity;

import java.util.ArrayList;

public final class CityListActivity extends DataDroidActivity implements RequestListener,
        OnClickListener {

    private static final String SAVED_STATE_CITY_LIST = "savedStateCityList";

    private Button mButtonLoad;
    private Button mButtonClearMemory;
    private ListView mListView;
    private CityListAdapter mListAdapter;

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.city_list);

        bindViews();

        mInflater = getLayoutInflater();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0, length = mRequestList.size(); i < length; i++) {
            Request request = mRequestList.get(i);

            if (mRequestManager.isRequestInProgress(request)) {
                mRequestManager.addRequestListener(this, request);
                setProgressBarIndeterminateVisibility(true);
            } else {
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

        ArrayList<City> rssItemList = savedInstanceState
                .getParcelableArrayList(SAVED_STATE_CITY_LIST);
        mListAdapter.setNotifyOnChange(false);
        for (int i = 0, length = rssItemList.size(); i < length; i++) {
            mListAdapter.add(rssItemList.get(i));
        }
        mListAdapter.notifyDataSetChanged();
    }

    private void bindViews() {
        mButtonLoad = (Button) findViewById(R.id.b_load);
        mButtonLoad.setOnClickListener(this);

        mButtonClearMemory = (Button) findViewById(R.id.b_clear_memory);
        mButtonClearMemory.setOnClickListener(this);

        mListView = (ListView) findViewById(android.R.id.list);
        mListAdapter = new CityListAdapter(this);
        mListView.setAdapter(mListAdapter);
    }

    private void callCityListWS() {
        mListAdapter.clear();
        setProgressBarIndeterminateVisibility(true);
        Request request = PoCRequestFactory.createGetCityListRequest();
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onClick(View view) {
        if (view == mButtonLoad) {
            callCityListWS();
        } else if (view == mButtonClearMemory) {
            (mListAdapter).clear();
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
    public void onRequestConnectionError(Request request) {
        if (mRequestList.contains(request)) {
            setProgressBarIndeterminateVisibility(false);
            mRequestList.remove(request);

            ConnexionErrorDialogFragment.show(this, request, this);
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

    class ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewPostalCode;
        private TextView mTextViewCountyNumber;
        private TextView mTextViewCountyName;

        public ViewHolder(View view) {
            mTextViewName = (TextView) view.findViewById(R.id.tv_name);
            mTextViewPostalCode = (TextView) view.findViewById(R.id.tv_postal_code);
            mTextViewCountyNumber = (TextView) view.findViewById(R.id.tv_county_number);
            mTextViewCountyName = (TextView) view.findViewById(R.id.tv_county_name);
        }

        public void populateViews(City city) {
            mTextViewName.setText(city.name);
            mTextViewPostalCode.setText(String.valueOf(city.postalCode));
            mTextViewCountyNumber.setText(String.valueOf(city.countyNumber));
            mTextViewCountyName.setText(city.countyName);
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
