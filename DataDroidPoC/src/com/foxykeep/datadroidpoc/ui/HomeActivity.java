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
import com.foxykeep.datadroidpoc.ui.crud.CrudSyncPhoneListActivity;
import com.foxykeep.datadroidpoc.ui.rss.RssFeedListActivity;
import com.foxykeep.datadroidpoc.ui.ws.CityListActivity;
import com.foxykeep.datadroidpoc.ui.ws.PersonListActivity;

public final class HomeActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home);
        bindViews();
    }

    private void bindViews() {
        ((Button) findViewById(R.id.b_person_list)).setOnClickListener(this);
        ((Button) findViewById(R.id.b_city_list)).setOnClickListener(this);

        ((Button) findViewById(R.id.b_phones_crud_sync)).setOnClickListener(this);

        ((Button) findViewById(R.id.b_rss_feed)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.b_person_list:
                intent = new Intent(this, PersonListActivity.class);
                break;
            case R.id.b_city_list:
                intent = new Intent(this, CityListActivity.class);
                break;
            case R.id.b_phones_crud_sync:
                intent = new Intent(this, CrudSyncPhoneListActivity.class);
                break;
            case R.id.b_rss_feed:
                intent = new Intent(this, RssFeedListActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
