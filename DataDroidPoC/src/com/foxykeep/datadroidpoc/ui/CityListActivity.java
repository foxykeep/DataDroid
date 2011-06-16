package com.foxykeep.datadroidpoc.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.config.DialogConfig;
import com.foxykeep.datadroidpoc.data.memprovider.MemoryProvider;
import com.foxykeep.datadroidpoc.data.model.City;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager;
import com.foxykeep.datadroidpoc.data.requestmanager.PoCRequestManager.OnRequestFinishedListener;
import com.foxykeep.datadroidpoc.data.service.PoCService;

public class CityListActivity extends ListActivity implements OnRequestFinishedListener, OnClickListener {

    private static final String SAVED_STATE_REQUEST_ID = "savedStateRequestId";
    private static final String SAVED_STATE_ERROR_TITLE = "savedStateErrorTitle";
    private static final String SAVED_STATE_ERROR_MESSAGE = "savedStateErrorMessage";

    private Button mButtonLoad;
    private Button mButtonClearMemory;

    private PoCRequestManager mRequestManager;
    private int mRequestId = -1;

    private MemoryProvider mMemoryProvider = MemoryProvider.getInstance();

    private LayoutInflater mInflater;

    private String mErrorDialogTitle;
    private String mErrorDialogMessage;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.city_list);
        bindViews();
        setListAdapter(new CityListAdapter(this));

        if (savedInstanceState != null) {
            mRequestId = savedInstanceState.getInt(SAVED_STATE_REQUEST_ID, -1);
            mErrorDialogTitle = savedInstanceState.getString(SAVED_STATE_ERROR_TITLE);
            mErrorDialogMessage = savedInstanceState.getString(SAVED_STATE_ERROR_MESSAGE);
        }

        mRequestManager = PoCRequestManager.getInstance(this);
        mInflater = getLayoutInflater();
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

                if (mMemoryProvider.cityList == null) {
                    showDialog(DialogConfig.DIALOG_CONNEXION_ERROR);
                } else {
                    final ArrayList<City> cityList = mMemoryProvider.cityList;

                    final CityListAdapter adapter = (CityListAdapter) getListAdapter();
                    adapter.setNotifyOnChange(false);
                    for (City city : cityList) {
                        adapter.add(city);
                    }
                    adapter.notifyDataSetChanged();
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
    }

    private void bindViews() {
        mButtonLoad = (Button) findViewById(R.id.b_load);
        mButtonLoad.setOnClickListener(this);

        mButtonClearMemory = (Button) findViewById(R.id.b_clear_memory);
        mButtonClearMemory.setOnClickListener(this);
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
                        callCityListWS();
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

    private void callCityListWS() {
        ((CityListAdapter) getListAdapter()).clear();

        setProgressBarIndeterminateVisibility(true);
        mRequestManager.addOnRequestFinishedListener(this);
        mRequestId = mRequestManager.getCityList();
    }

    @Override
    public void onClick(final View view) {
        if (view == mButtonLoad) {
            callCityListWS();
        } else if (view == mButtonClearMemory) {
            mMemoryProvider.cityList = null;
            ((CityListAdapter) getListAdapter()).clear();
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
                final ArrayList<City> cityList = payload
                        .getParcelableArrayList(PoCRequestManager.RECEIVER_EXTRA_CITY_LIST);

                final CityListAdapter adapter = (CityListAdapter) getListAdapter();
                adapter.setNotifyOnChange(false);
                for (City city : cityList) {
                    adapter.add(city);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    class ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewPostalCode;
        private TextView mTextViewCountyNumber;
        private TextView mTextViewCountyName;

        public ViewHolder(final View view) {
            mTextViewName = (TextView) view.findViewById(R.id.tv_name);
            mTextViewPostalCode = (TextView) view.findViewById(R.id.tv_postal_code);
            mTextViewCountyNumber = (TextView) view.findViewById(R.id.tv_county_number);
            mTextViewCountyName = (TextView) view.findViewById(R.id.tv_county_name);
        }

        public void populateViews(final City city) {
            mTextViewName.setText(city.name);
            mTextViewPostalCode.setText(String.valueOf(city.postalCode));
            mTextViewCountyNumber.setText(String.valueOf(city.countyNumber));
            mTextViewCountyName.setText(city.countyName);
        }
    }

    class CityListAdapter extends ArrayAdapter<City> {

        public CityListAdapter(final Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
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
