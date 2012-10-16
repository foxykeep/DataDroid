/**
 * 2011 Foxykeep (http://datadroid.foxykeep.com)
 * <p>
 * Licensed under the Beerware License : <br />
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */

package com.foxykeep.datadroidpoc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.foxykeep.datadroidpoc.R;

public class HomeActivity extends Activity implements OnClickListener {

    private Button mButtonPersonList;
    private Button mButtonCityList;
    private Button mButtonPhonesCrudSync;

    private Button mButtonRssFeed;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);
        bindViews();
    }

    private void bindViews() {
        mButtonPersonList = (Button) findViewById(R.id.b_person_list);
        mButtonPersonList.setOnClickListener(this);

        mButtonCityList = (Button) findViewById(R.id.b_city_list);
        mButtonCityList.setOnClickListener(this);

        mButtonPhonesCrudSync = (Button) findViewById(R.id.b_phones_crud_sync);
        mButtonPhonesCrudSync.setOnClickListener(this);

        mButtonRssFeed = (Button) findViewById(R.id.b_rss_feed);
        mButtonRssFeed.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        Intent intent = null;
        if (view == mButtonPersonList) {
            intent = new Intent(this, PersonListActivity.class);
        } else if (view == mButtonCityList) {
            intent = new Intent(this, CityListActivity.class);
        } else if (view == mButtonPhonesCrudSync) {
            intent = new Intent(this, CrudSyncPhoneListActivity.class);
        } else if (view == mButtonRssFeed) {
            intent = new Intent(this, RssFeedListActivity.class);
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
