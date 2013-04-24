/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.foxykeep.datadroidpoc.ui;

import com.foxykeep.datadroidpoc.R;
import com.foxykeep.datadroidpoc.dialogs.SampleDescriptionDialogFragment;
import com.foxykeep.datadroidpoc.ui.crud.CrudSyncPhoneListActivity;
import com.foxykeep.datadroidpoc.ui.feature.AuthenticationActivity;
import com.foxykeep.datadroidpoc.ui.feature.CustomRequestExceptionActivity;
import com.foxykeep.datadroidpoc.ui.feature.RefreshActivity;
import com.foxykeep.datadroidpoc.ui.requesttype.RequestTypesActivity;
import com.foxykeep.datadroidpoc.ui.rss.RssFeedListActivity;
import com.foxykeep.datadroidpoc.ui.ws.CityListActivity;
import com.foxykeep.datadroidpoc.ui.ws.DoubleListActivity;
import com.foxykeep.datadroidpoc.ui.ws.PersonListActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public final class HomeActivity extends FragmentActivity implements OnItemClickListener,
        OnItemLongClickListener {

    private LayoutInflater mInflater;

    private SampleListAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);

        mInflater = getLayoutInflater();

        populateViews();
    }

    private void populateViews() {
        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        mListAdapter = new SampleListAdapter(this);
        listView.setAdapter(mListAdapter);

        populateAdapter();
    }

    private void populateAdapter() {
        mListAdapter.setNotifyOnChange(false);

        mListAdapter.add(new Sample(R.string.home_person_list_title,
                R.string.home_person_list_description, PersonListActivity.class));
        mListAdapter.add(new Sample(R.string.home_city_list_title,
                R.string.home_city_list_description, CityListActivity.class));
        mListAdapter.add(new Sample(R.string.home_crud_phone_list_sync_title,
                R.string.home_crud_phone_list_sync_description, CrudSyncPhoneListActivity.class));

        mListAdapter.add(new Sample(R.string.home_double_list_title,
                R.string.home_double_list_description, DoubleListActivity.class));
        mListAdapter.add(new Sample(R.string.home_request_types_title,
                R.string.home_request_types_description, RequestTypesActivity.class));
        mListAdapter.add(new Sample(R.string.home_authentication_title,
                R.string.home_authentication_description, AuthenticationActivity.class));
        mListAdapter.add(new Sample(R.string.home_refresh_title, R.string.home_refresh_description,
                RefreshActivity.class));
        mListAdapter.add(new Sample(R.string.home_custom_request_exception_title,
                R.string.home_custom_request_exception_description,
                CustomRequestExceptionActivity.class));

        mListAdapter.add(new Sample(R.string.home_rss_feed_title,
                R.string.home_rss_feed_description, RssFeedListActivity.class));

        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sample sample = mListAdapter.getItem(position);

        Intent intent = new Intent(this, sample.activityKlass);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Sample sample = mListAdapter.getItem(position);

        SampleDescriptionDialogFragment.show(this, sample.titleResId, sample.descriptionResId);
        return true;
    }

    private final class SampleListAdapter extends ArrayAdapter<Sample> {

        public SampleListAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView == null) {
                textView = (TextView) mInflater.inflate(android.R.layout.simple_list_item_1, null);
            } else {
                textView = (TextView) convertView;
            }

            Sample sample = getItem(position);
            textView.setText(sample.titleResId);

            return textView;
        }
    }

    private final class Sample {
        public int titleResId;
        public int descriptionResId;
        public Class<? extends Activity> activityKlass;

        public Sample(int titleResId, int descriptionResId,
                Class<? extends Activity> activityKlass) {
            this.titleResId = titleResId;
            this.descriptionResId = descriptionResId;
            this.activityKlass = activityKlass;
        }
    }
}
