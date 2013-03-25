/**
 * 2012 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui.ws;

import com.foxykeep.datadroid.requestmanager.Request;
import com.foxykeep.datadroid.requestmanager.RequestManager.RequestListener;
import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.data.model.City;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestFactory;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ConnectionErrorDialogFragment.ConnectionErrorDialogListener;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment;
import com.foxykeep.datadroidpoc.dialogs.ProgressDialogFragment.ProgressDialogFragmentBuilder;
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

public final class DoubleListActivity extends DataDroidActivity implements RequestListener,
        OnClickListener, ConnectionErrorDialogListener {

    private static final String SAVED_STATE_CITY_LIST_TOP = "savedStateCityListTop";
    private static final String SAVED_STATE_CITY_LIST_BOTTOM = "savedStateCityListBottom";

    private ListView mListViewTop;
    private CityListAdapter mListAdapterTop;
    private ListView mListViewBottom;
    private CityListAdapter mListAdapterBottom;

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.double_list);

        bindViews();

        mInflater = getLayoutInflater();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (int i = 0; i < mRequestList.size(); i++) {
            Request request = mRequestList.get(i);
            int requestType = request.getRequestType();

            if (mRequestManager.isRequestInProgress(request)) {
                if (requestType == PoCRequestFactory.REQUEST_TYPE_CITY_LIST) {
                    setProgressBarIndeterminateVisibility(true);
                }
                mRequestManager.addRequestListener(this, request);
            } else {
                if (requestType == PoCRequestFactory.REQUEST_TYPE_CITY_LIST_2) {
                    ProgressDialogFragment.dismiss(this);
                }
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

        ArrayList<City> cityListTop = new ArrayList<City>();
        for (int i = 0, n = mListAdapterTop.getCount(); i < n; i++) {
            cityListTop.add(mListAdapterTop.getItem(i));
        }

        outState.putParcelableArrayList(SAVED_STATE_CITY_LIST_TOP, cityListTop);

        ArrayList<City> cityListBottom = new ArrayList<City>();
        for (int i = 0, n = mListAdapterBottom.getCount(); i < n; i++) {
            cityListBottom.add(mListAdapterBottom.getItem(i));
        }

        outState.putParcelableArrayList(SAVED_STATE_CITY_LIST_BOTTOM, cityListBottom);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mListAdapterTop.setNotifyOnChange(false);
        mListAdapterBottom.setNotifyOnChange(false);

        ArrayList<City> cityItemListTop = savedInstanceState
                .getParcelableArrayList(SAVED_STATE_CITY_LIST_TOP);
        ArrayList<City> cityItemListBottom = savedInstanceState
                .getParcelableArrayList(SAVED_STATE_CITY_LIST_BOTTOM);

        for (int i = 0, length = cityItemListTop.size(); i < length; i++) {
            mListAdapterTop.add(cityItemListTop.get(i));
        }
        for (int i = 0, length = cityItemListBottom.size(); i < length; i++) {
            mListAdapterBottom.add(cityItemListBottom.get(i));
        }

        mListAdapterTop.notifyDataSetChanged();
        mListAdapterBottom.notifyDataSetChanged();
    }

    private void bindViews() {
        ((Button) findViewById(R.id.b_load_top)).setOnClickListener(this);
        ((Button) findViewById(R.id.b_load_bottom)).setOnClickListener(this);
        ((Button) findViewById(R.id.b_load_both)).setOnClickListener(this);

        mListViewTop = (ListView) findViewById(R.id.lv_top);
        mListAdapterTop = new CityListAdapter(this);
        mListViewTop.setAdapter(mListAdapterTop);
        mListViewTop.setEmptyView(findViewById(R.id.tv_empty_top));

        mListViewBottom = (ListView) findViewById(R.id.lv_bottom);
        mListAdapterBottom = new CityListAdapter(this);
        mListViewBottom.setAdapter(mListAdapterBottom);
        mListViewBottom.setEmptyView(findViewById(R.id.tv_empty_bottom));
    }

    private void callCityListWS() {
        mListAdapterTop.clear();
        setProgressBarIndeterminateVisibility(true);
        Request request = PoCRequestFactory.getCityListRequest();
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    private void callCityList2WS() {
        mListAdapterBottom.clear();
        new ProgressDialogFragmentBuilder(this)
                .setMessage(R.string.progress_dialog_message)
                .setCancelable(true)
                .show();
        Request request = PoCRequestFactory.getCityList2Request();
        mRequestManager.execute(request, this);
        mRequestList.add(request);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_load_top:
                callCityListWS();
                break;
            case R.id.b_load_bottom:
                callCityList2WS();
                break;
            case R.id.b_load_both:
                callCityListWS();
                callCityList2WS();
                break;
        }
    }

    @Override
    public void onRequestFinished(Request request, Bundle resultData) {
        if (mRequestList.contains(request)) {
            int requestType = request.getRequestType();

            switch (requestType) {
                case PoCRequestFactory.REQUEST_TYPE_CITY_LIST:
                    setProgressBarIndeterminateVisibility(false);
                    break;
                case PoCRequestFactory.REQUEST_TYPE_CITY_LIST_2:
                    ProgressDialogFragment.dismiss(this);
                    break;
            }
            mRequestList.remove(request);

            ArrayList<City> cityList = resultData
                    .getParcelableArrayList(PoCRequestFactory.BUNDLE_EXTRA_CITY_LIST);

            switch (requestType) {
                case PoCRequestFactory.REQUEST_TYPE_CITY_LIST:
                    mListAdapterTop.setNotifyOnChange(false);
                    for (int i = 0, length = cityList.size(); i < length; i++) {
                        mListAdapterTop.add(cityList.get(i));
                    }
                    mListAdapterTop.notifyDataSetChanged();
                    break;
                case PoCRequestFactory.REQUEST_TYPE_CITY_LIST_2:
                    mListAdapterBottom.setNotifyOnChange(false);
                    for (int i = 0, length = cityList.size(); i < length; i++) {
                        mListAdapterBottom.add(cityList.get(i));
                    }
                    mListAdapterBottom.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestConnectionError(Request request, int statusCode) {
        if (mRequestList.contains(request)) {
            switch (request.getRequestType()) {
                case PoCRequestFactory.REQUEST_TYPE_CITY_LIST:
                    setProgressBarIndeterminateVisibility(false);
                    break;
                case PoCRequestFactory.REQUEST_TYPE_CITY_LIST_2:
                    ProgressDialogFragment.dismiss(this);
                    break;
            }
            mRequestList.remove(request);

            ConnectionErrorDialogFragment.show(this, request, this);
        }
    }

    @Override
    public void onRequestDataError(Request request) {
        if (mRequestList.contains(request)) {
            switch (request.getRequestType()) {
                case PoCRequestFactory.REQUEST_TYPE_CITY_LIST:
                    setProgressBarIndeterminateVisibility(false);
                    break;
                case PoCRequestFactory.REQUEST_TYPE_CITY_LIST_2:
                    ProgressDialogFragment.dismiss(this);
                    break;
            }
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
        switch (request.getRequestType()) {
            case PoCRequestFactory.REQUEST_TYPE_CITY_LIST:
                callCityListWS();
                break;
            case PoCRequestFactory.REQUEST_TYPE_CITY_LIST_2:
                callCityList2WS();
                break;
        }
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
